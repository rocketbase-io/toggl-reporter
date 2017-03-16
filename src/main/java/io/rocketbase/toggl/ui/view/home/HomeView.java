package io.rocketbase.toggl.ui.view.home;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import io.rocketbase.toggl.backend.service.TimeEntryService;
import io.rocketbase.toggl.ui.component.tab.ExtendedTabSheet;
import io.rocketbase.toggl.ui.view.AbstractView;
import io.rocketbase.toggl.ui.view.home.tab.ChartTab;
import io.rocketbase.toggl.ui.view.home.tab.StatisticsTab;
import org.joda.time.YearMonth;
import org.vaadin.viritin.MSize;
import org.vaadin.viritin.fields.TypedSelect;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
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
    private TimeEntryService timeEntryService;

    @Resource
    private ChartTab chartTab;

    @Resource
    private StatisticsTab statisticsTab;
    private TypedSelect<YearMonth> typedSelect;

    public HomeView() {
        super(VIEW_NAME, "Chart", FontAwesome.LINE_CHART, 0);
    }

    public static Component getPlaceHolder() {
        return new MVerticalLayout()
                .add(new MLabel("select year month").withStyleName("text-center", "placeholder"), Alignment.MIDDLE_CENTER)
                .withSize(MSize.FULL_SIZE);
    }

    @Override
    public Component initialzeUi() {
        ExtendedTabSheet<YearMonth> tabSheet = new ExtendedTabSheet<>();
        tabSheet.addTab(FontAwesome.LINE_CHART, "chart", chartTab);
        tabSheet.addTab(FontAwesome.STAR_O, "statistics", statisticsTab);

        typedSelect = new TypedSelect<>(YearMonth.class).asComboBoxType()
                .setNullSelectionAllowed(false)
                .addMValueChangeListener(e -> {
                    tabSheet.setFilterAndRefresh(e.getValue());
                })
                .withWidth("200px");


        return new MVerticalLayout()
                .add(new MHorizontalLayout()
                        .add(typedSelect, Alignment.MIDDLE_RIGHT)
                        .withFullWidth())
                .add(tabSheet, 1)
                .withSize(MSize.FULL_SIZE);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
        typedSelect.setBeans(timeEntryService.fetchAllMonth());
    }
}
