package io.rocketbase.toggl.ui.view.chart;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.LineChartConfig;
import com.byteowls.vaadin.chartjs.data.LineDataset;
import com.byteowls.vaadin.chartjs.options.InteractionMode;
import com.byteowls.vaadin.chartjs.options.Position;
import com.byteowls.vaadin.chartjs.options.Tooltips;
import com.byteowls.vaadin.chartjs.options.scale.Axis;
import com.byteowls.vaadin.chartjs.options.scale.CategoryScale;
import com.byteowls.vaadin.chartjs.options.scale.LinearScale;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import io.rocketbase.toggl.backend.model.report.UserTimeline;
import io.rocketbase.toggl.backend.service.TimeEntryService;
import io.rocketbase.toggl.backend.util.YearMonthUtil;
import io.rocketbase.toggl.ui.view.AbstractView;
import org.joda.time.YearMonth;
import org.vaadin.viritin.MSize;
import org.vaadin.viritin.fields.TypedSelect;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MPanel;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by marten on 08.03.17.
 */
@UIScope
@SpringView(name = ChartView.VIEW_NAME)
public class ChartView extends AbstractView {

    public static final String VIEW_NAME = "";

    private static final MVerticalLayout PLACEHOLDER = new MVerticalLayout()
            .add(new MLabel("select year month").withStyleName("text-center", "placeholder"), Alignment.MIDDLE_CENTER)
            .withSize(MSize.FULL_SIZE);

    @Resource
    private TimeEntryService timeEntryService;

    private MPanel chartPanel = new MPanel(PLACEHOLDER).withSize(MSize.FULL_SIZE);


    public ChartView() {
        super(VIEW_NAME, "Chart", FontAwesome.LINE_CHART, 0);
    }

    @Override
    public Component initialzeUi() {
        return new MVerticalLayout()
                .add(new MHorizontalLayout()
                        .add(new TypedSelect<>(YearMonth.class).asComboBoxType()
                                .setBeans(timeEntryService.fetchAllMonth())
                                .setNullSelectionAllowed(false)
                                .addMValueChangeListener(e -> {
                                    if (e != null) {
                                        chartPanel.setContent(genChart(e.getValue()));
                                    } else {
                                        chartPanel.setContent(PLACEHOLDER);
                                    }
                                })
                                .withWidth("200px"), Alignment.MIDDLE_RIGHT)
                        .withFullWidth())
                .add(chartPanel, 1)
                .withSize(MSize.FULL_SIZE);
    }


    protected Component genChart(YearMonth yearMonth) {
        LineChartConfig config = new LineChartConfig();

        config.data()
                .labelsAsList(YearMonthUtil.getAllDatesOfMonth(yearMonth)
                        .stream()
                        .map(d -> String.valueOf(d.getDayOfMonth()))
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
                            .dataAsList(userTimeline.getHoursWorked(yearMonth))
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
        chart.addStyleName("chart-container");
        chart.setWidth("100%");

        return new MVerticalLayout()
                .add(chart, Alignment.MIDDLE_CENTER, 1)
                .withSize(MSize.FULL_SIZE);
    }
}
