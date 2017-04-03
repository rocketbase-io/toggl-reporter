package io.rocketbase.toggl.ui.view.home.tab;

import com.google.common.base.Joiner;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.themes.ValoTheme;
import io.rocketbase.toggl.backend.config.TogglService;
import io.rocketbase.toggl.backend.model.ApplicationSettingModel.UserDetails;
import io.rocketbase.toggl.backend.model.report.UserTimeline.WeekStatistics;
import io.rocketbase.toggl.backend.model.report.WeekTimeline;
import io.rocketbase.toggl.backend.service.TimeEntryService;
import io.rocketbase.toggl.ui.component.tab.AbstractTab;
import io.rocketbase.toggl.ui.view.home.HomeView;
import org.joda.time.YearMonth;
import org.threeten.extra.YearWeek;
import org.vaadin.viritin.MSize;
import org.vaadin.viritin.fields.MTable;
import org.vaadin.viritin.fields.TypedSelect;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.Resource;
import java.util.List;


@UIScope
@SpringComponent
public class WeekStatisticsTab extends AbstractTab<YearMonth> {

    @Resource
    private TimeEntryService timeEntryService;

    @Resource
    private TogglService togglService;

    private MVerticalLayout layout;
    private TypedSelect<YearWeek> weekFrom, weekTo;


    @Override
    public Component initLayout() {
        layout = new MVerticalLayout()
                .withSize(MSize.FULL_SIZE)
                .withMargin(false)
                .add(HomeView.getPlaceHolder(), 1);


        weekFrom = new TypedSelect<>(YearWeek.class).asComboBoxType()
                .setNullSelectionAllowed(false)
                .addMValueChangeListener(e -> {
                    if (e.getValue() != null && weekTo.getValue() != null) {
                        if (weekTo.getValue()
                                .isBefore(e.getValue())) {
                            weekTo.setValue(null);
                        }
                    }
                    filter();
                })
                .withWidth("200px");
        weekTo = new TypedSelect<>(YearWeek.class).asComboBoxType()
                .setNullSelectionAllowed(false)
                .addMValueChangeListener(e -> {
                    if (e.getValue() != null) {
                        if (weekFrom.getValue() != null && e.getValue()
                                .isAfter(weekFrom.getValue())) {
                            filter();
                            return;
                        } else {
                            weekTo.setValue(null);
                        }
                    }
                    filter();
                })
                .withWidth("200px");

        return new MVerticalLayout()
                .add(new MHorizontalLayout()
                        .add(weekFrom, Alignment.MIDDLE_RIGHT, 1)
                        .add(weekTo, Alignment.MIDDLE_RIGHT)
                        .withFullWidth())
                .add(layout, 1)
                .withSize(MSize.FULL_SIZE);
    }

    @Override
    public void onTabEnter() {
        layout.removeAllComponents();
        List<YearWeek> yearWeekList = timeEntryService.fetchAllYearWeeks();
        weekFrom.setBeans(yearWeekList);
        weekTo.setBeans(yearWeekList);
        filter();
    }

    private void filter() {
        layout.removeAllComponents();
        if (weekFrom.getValue() != null) {
            layout.add(genTable(timeEntryService.getWeekTimelines(weekFrom.getValue(), weekTo.getValue())), 1);
        } else {
            layout.add(HomeView.getPlaceHolder(), 1);
        }
    }


    protected Component genTable(List<WeekTimeline> data) {

        MTable<WeekTimeline> table = new MTable<>(WeekTimeline.class)
                .withProperties()
                .withStyleName("week-statistics")
                .withGeneratedColumn("week", e -> new MVerticalLayout()
                        .withFullWidth()
                        .withMargin(false)
                        .add(new MLabel(e.getYearWeek()
                                .toString()).withStyleName(ValoTheme.LABEL_BOLD, ValoTheme.LABEL_LARGE))
                        .add(genLabelInfo("Total hours", e.getTotalHours()))
                        .add(genLabelInfo("Billable hours", e.getBillableHours()))
                        .add(genLabelInfo("Earned", e.getBillableAmount()))
                        .add(genLabelInfo("Holidays",
                                e.getHolidays()
                                        .size() > 0 ? "<button title=\"" + Joiner.on(", ")
                                        .join(e.getHolidays()) + "\">" + e.getHolidays()
                                        .size() + "</button>" : "-")
                                .withStyleName("left-right"))
                        .withStyleName("cell-content-wrapper"))
                .withColumnWidth("week", 240)
                .withSize(MSize.FULL_SIZE);
        table.setColumnHeader("week", "Week");

        List<UserDetails> userList = togglService.getAllUsers();
        userList.forEach(user -> {
            String columnId = String.valueOf(user.getUid());
            table.withGeneratedColumn(columnId, e -> {
                if (e.getUidTimelines()
                        .containsKey(user.getUid())) {
                    WeekStatistics stat = e.getUidTimelines()
                            .get(user.getUid())
                            .getWeekStatisticsOfWeek(e.getYearWeek());
                    return new MVerticalLayout()
                            .withFullWidth()
                            .withMargin(false)
                            .add(genLabelInfo("Total hours", stat.getTotalHours()))
                            .add(genLabelInfo("Billable hours", stat.getBillableHours()))
                            .add(genLabelInfo("Earned", stat.getBillableAmount()))
                            .add(genLabelInfo("Days worked", stat.getWorkedDays()))
                            .add(genLabelInfo("Ø per day", stat.getAverageHoursPerDay()))
                            .withStyleName("cell-content-wrapper");
                } else {
                    return null;
                }
            });
            table.setColumnHeader(columnId, user.getName());
            table.setColumnWidth(columnId, 190);
        });
        table.setBeans(data);
        table.setFooterVisible(true);
        String format = "<div class=\"footer-key-value\"><span>Hours:</span> %s</div><div class=\"footer-key-value\"><span>Earned:</span> %s</div>";
        table.setColumnFooter("week",
                String.format(format,
                        Math.round(data.stream()
                                .mapToDouble(e -> e.getTotalHours())
                                .sum() * 10.0) / 10.0,
                        data.stream()
                                .mapToLong(e -> e.getBillableAmount())
                                .sum()));
        userList.forEach(user -> {
            String columnId = String.valueOf(user.getUid());
            table.setColumnFooter(columnId,
                    String.format(format,
                            Math.round(data.stream()
                                    .filter(e -> e.getUidTimelines()
                                            .containsKey(user.getUid()))
                                    .mapToDouble(e -> e.getUidTimelines()
                                            .get(user.getUid())
                                            .getWeekStatisticsOfWeek(e.getYearWeek())
                                            .getTotalHours())
                                    .sum() * 10.0) / 10.0,
                            data.stream()
                                    .filter(e -> e.getUidTimelines()
                                            .containsKey(user.getUid()))
                                    .mapToLong(e -> e.getUidTimelines()
                                            .get(user.getUid())
                                            .getWeekStatisticsOfWeek(e.getYearWeek())
                                            .getBillableAmount())
                                    .sum()));
        });
        return table;
    }

    private MLabel genLabelInfo(String caption, Number value) {
        return genLabelInfo(caption, String.valueOf(value)).withStyleName("left-right");
    }

    private MLabel genLabelInfo(String caption, String value) {
        return new MLabel(String.format("<span>%s:</span> %s", caption, value)).withFullWidth()
                .withContentMode(ContentMode.HTML)
                .withStyleName("info");
    }
}
