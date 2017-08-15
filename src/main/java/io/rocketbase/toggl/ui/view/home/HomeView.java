package io.rocketbase.toggl.ui.view.home;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import io.rocketbase.toggl.ui.component.tab.ExtendedTabSheet;
import io.rocketbase.toggl.ui.view.AbstractView;
import io.rocketbase.toggl.ui.view.home.tab.ChartTab;
import io.rocketbase.toggl.ui.view.home.tab.MonthStatisticsTab;
import io.rocketbase.toggl.ui.view.home.tab.WeekStatisticsTab;
import org.vaadin.viritin.MSize;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.Resource;

/**
 * Created by marten on 08.03.17.
 */
@UIScope
@SpringView(name = HomeView.VIEW_NAME)
public class HomeView extends AbstractView {

    public static final String VIEW_NAME = "";

    @Resource
    private ChartTab chartTab;

    @Resource
    private MonthStatisticsTab monthStatisticsTab;

    @Resource
    private WeekStatisticsTab weekStatisticsTab;

    public HomeView() {
        super(VIEW_NAME, "Chart", VaadinIcons.LINE_CHART, 0);
    }

    public static Component getPlaceHolder() {
        return new MVerticalLayout()
                .add(new MLabel("select year month").withStyleName("text-center", "placeholder"), Alignment.MIDDLE_CENTER)
                .withSize(MSize.FULL_SIZE);
    }

    @Override
    public Component initialzeUi() {
        ExtendedTabSheet tabSheet = new ExtendedTabSheet();
        tabSheet.addTab(VaadinIcons.LINE_CHART, "chart", chartTab);
        tabSheet.addTab(VaadinIcons.STAR_O, "month-statistics", monthStatisticsTab);
        tabSheet.addTab(VaadinIcons.CALENDAR, "week-statistics", weekStatisticsTab);


        return new MVerticalLayout()
                .add(tabSheet, 1)
                .withSize(MSize.FULL_SIZE);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
    }
}
