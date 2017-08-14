package io.rocketbase.toggl.ui.view.home.tab;

import com.google.common.base.Joiner;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Image;
import com.vaadin.ui.UI;
import de.jollyday.Holiday;
import io.rocketbase.toggl.backend.model.report.UserTimeline;
import io.rocketbase.toggl.backend.service.HolidayManagerService;
import io.rocketbase.toggl.backend.service.TimeEntryService;
import io.rocketbase.toggl.backend.util.LocalDateConverter;
import io.rocketbase.toggl.backend.util.YearMonthUtil;
import io.rocketbase.toggl.ui.component.tab.AbstractTab;
import io.rocketbase.toggl.ui.view.home.HomeView;
import org.joda.time.YearMonth;
import org.vaadin.viritin.MSize;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.v7.fields.MTable;
import org.vaadin.viritin.v7.fields.MTextArea;
import org.vaadin.viritin.v7.fields.TypedSelect;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@UIScope
@SpringComponent
public class MonthStatisticsTab extends AbstractTab<YearMonth> {

    @Resource
    private TimeEntryService timeEntryService;

    @Resource
    private HolidayManagerService holidayManagerService;

    private MVerticalLayout layout;

    private TypedSelect<YearMonth> typedSelect;


    @Override
    public Component initLayout() {
        layout = new MVerticalLayout()
                .withSize(MSize.FULL_SIZE)
                .withMargin(false)
                .add(HomeView.getPlaceHolder(), 1);

        typedSelect = new TypedSelect<>(YearMonth.class).asComboBoxType()
                .setNullSelectionAllowed(false)
                .addMValueChangeListener(e -> {
                    filter();
                })
                .withWidth("200px");

        return new MVerticalLayout()
                .add(new MHorizontalLayout()
                        .add(typedSelect, Alignment.MIDDLE_RIGHT)
                        .withFullWidth())
                .add(layout, 1)
                .withSize(MSize.FULL_SIZE);
    }

    @Override
    public void onTabEnter() {
        layout.removeAllComponents();
        typedSelect.setBeans(timeEntryService.fetchAllYearMonths());
        filter();
    }

    private void filter() {
        layout.removeAllComponents();
        if (typedSelect.getValue() != null) {
            layout.add(genTable(typedSelect.getValue()), 1);
            layout.add(genHolidays(typedSelect.getValue()));
        } else {
            layout.add(HomeView.getPlaceHolder(), 1);
        }
    }

    private Component genHolidays(YearMonth filter) {
        Set<Holiday> holidaySet = holidayManagerService.getHolidays(filter);

        MTextArea textArea = new MTextArea("Holidays")
                .withValue(Joiner.on(",\t")
                        .join(holidaySet.stream()
                                .sorted(Comparator.comparing(Holiday::getDate))
                                .map(h -> String.format("%s (Week: %d): %s",
                                        h.getDate()
                                                .format(DateTimeFormatter.ISO_DATE),
                                        LocalDateConverter.convert(h.getDate())
                                                .getWeekOfWeekyear(),
                                        h.getDescription(UI.getCurrent()
                                                .getLocale())))
                                .collect(Collectors.toList())));
        textArea.setVisible(holidaySet.size() > 0);
        textArea.setWidth("100%");
        textArea.setHeight("50px");
        return textArea;
    }

    protected Component genTable(YearMonth yearMonth) {
        List<UserTimeline> userTimelineList = timeEntryService.getUserTimeLines(yearMonth);

        MTable<UserTimeline> table = new MTable<>(UserTimeline.class)
                .withGeneratedColumn("user", e -> {
                    Image avatar = new Image(null,
                            new ExternalResource(e.getUser()
                                    .getAvatar()));
                    avatar.setWidth("64px");
                    avatar.setHeight("64px");

                    return new MHorizontalLayout()
                            .add(avatar)
                            .add(new MVerticalLayout()
                                    .withMargin(false)
                                    .add(genLabelInfo("Name",
                                            e.getUser()
                                                    .getName()))
                                    .add(genLabelInfo("Total", e.getTotalHoursFormatted()).withStyleName("left-right"))
                                    .withFullWidth(), Alignment.MIDDLE_LEFT, 1)
                            .withFullWidth()
                            .withStyleName("cell-content-wrapper");
                })
                .withColumnWidth("user", 280)
                .withSize(MSize.FULL_SIZE);

        List<Integer> weekList = YearMonthUtil.getAllWeeksOfWeekyear(yearMonth);
        weekList.forEach(week -> {
            table.withGeneratedColumn(String.valueOf(week), e -> {
                if (e.getWeekStatisticsOfMonth()
                        .containsKey(week)) {
                    UserTimeline.WeekStatistics stat = e.getWeekStatisticsOfMonth()
                            .get(week);
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
            table.setColumnWidth(String.valueOf(week), 190);
        });

        List<String> properties = new ArrayList<>();
        properties.add("user");
        properties.addAll(weekList.stream()
                .map(v -> String.valueOf(v))
                .collect(Collectors.toList()));

        table.withProperties(properties)
                .setBeans(userTimelineList.stream()
                        .sorted(Comparator.comparing(UserTimeline::totalMillisecondsWorked)
                                .reversed())
                        .collect(Collectors.toList()));

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
