package io.rocketbase.toggl.ui.view.home.tab;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.LineChartConfig;
import com.byteowls.vaadin.chartjs.data.LineDataset;
import com.byteowls.vaadin.chartjs.options.InteractionMode;
import com.byteowls.vaadin.chartjs.options.Position;
import com.byteowls.vaadin.chartjs.options.Tooltips;
import com.byteowls.vaadin.chartjs.options.scale.Axis;
import com.byteowls.vaadin.chartjs.options.scale.CategoryScale;
import com.byteowls.vaadin.chartjs.options.scale.LinearScale;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import io.rocketbase.toggl.backend.model.report.UserTimeline;
import io.rocketbase.toggl.backend.service.TimeEntryService;
import io.rocketbase.toggl.backend.util.YearMonthUtil;
import io.rocketbase.toggl.ui.component.tab.AbstractTab;
import io.rocketbase.toggl.ui.view.home.HomeView;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTimeConstants;
import org.joda.time.YearMonth;
import org.vaadin.viritin.MSize;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MPanel;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.v7.fields.TypedSelect;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


@UIScope
@SpringComponent
@Slf4j
public class ChartTab extends AbstractTab<YearMonth> {

    @Resource
    private TimeEntryService timeEntryService;

    private MPanel panel;

    private TypedSelect<YearMonth> typedSelect;

    @Override
    public Component initLayout() {
        panel = new MPanel(HomeView.getPlaceHolder())
                .withSize(MSize.FULL_SIZE);

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
                .add(panel, 1)
                .withSize(MSize.FULL_SIZE);
    }

    @Override
    public void onTabEnter() {
        typedSelect.setBeans(timeEntryService.fetchAllYearMonths());
        filter();
    }

    private void filter() {
        if (typedSelect.getValue() != null) {

            MVerticalLayout layout = new MVerticalLayout()
                    .add(genChart(typedSelect.getValue()), 1)
                    .withMargin(false)
                    .withStyleName("chart-container")
                    .withSize(MSize.FULL_SIZE);

            panel.setContent(new MVerticalLayout()
                    .add(layout, Alignment.MIDDLE_CENTER, 1)
                    .withSize(MSize.FULL_SIZE));

        } else {
            panel.setContent(HomeView.getPlaceHolder());
        }
    }

    protected ChartJs genChart(YearMonth yearMonth) {
        LineChartConfig config = new LineChartConfig();

        config.data()
                .labelsAsList(YearMonthUtil.getAllDatesOfMonth(yearMonth)
                        .stream()
                        .map(d -> {
                            if (d.getDayOfWeek() > DateTimeConstants.FRIDAY) {
                                return String.format("(%d)", d.getDayOfMonth());
                            } else {
                                return String.valueOf(d.getDayOfMonth());
                            }
                        })
                        .collect(Collectors.toList()));

        List<UserTimeline> userTimelineList = timeEntryService.getUserTimeLines(yearMonth);

        userTimelineList.forEach(userTimeline -> {
            String color = "#" + userTimeline.getUser()
                    .getGraphColor()
                    .getHexCode();
            config.data()
                    .addDataset(new LineDataset().label(userTimeline.getUser()
                            .getName())
                            .backgroundColor(color)
                            .borderColor(color)
                            .dataAsList(userTimeline.getHoursWorked())
                            .fill(false));
        });

        config.options()
                .responsive(true)
                .title()
                .display(true)
                .text(yearMonth.toString())
                .and()
                .tooltips()
                .position(Tooltips.PositionMode.AVERAGE)
                .mode(InteractionMode.INDEX)
                .intersect(false)
                .and()
                .scales()
                .add(Axis.X, new CategoryScale()
                        .display(true)
                        .scaleLabel()
                        .display(true)
                        .labelString("day of month")
                        .and()
                        .position(Position.BOTTOM))
                .add(Axis.Y, new LinearScale()
                        .display(true)
                        .scaleLabel()
                        .display(true)
                        .labelString("Hours")
                        .and()
                        .ticks()
                        .min(0)
                        .max(14)
                        .and()
                        .position(Position.RIGHT))
                .and()
                .maintainAspectRatio(true)
                .done();

        ChartJs chart = new ChartJs(config);
        chart.setWidth(100, Unit.PERCENTAGE);
        chart.addStyleName("home-container");

        return chart;
    }
}
