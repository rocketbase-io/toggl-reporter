package io.rocketbase.toggl.ui.view.setting.tab;

import ch.simas.jtoggl.domain.Workspace;
import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import io.rocketbase.toggl.backend.config.TogglService;
import io.rocketbase.toggl.backend.model.DateTimeEntryGroup;
import io.rocketbase.toggl.backend.model.report.UserTimeline;
import io.rocketbase.toggl.backend.service.FetchAndStoreService;
import io.rocketbase.toggl.backend.service.TimeEntryService;
import io.rocketbase.toggl.backend.util.LocalDateConverter;
import io.rocketbase.toggl.ui.component.tab.AbstractTab;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.vaadin.viritin.MSize;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by marten on 09.03.17.
 */
@UIScope
@SpringComponent
public class PullDataTab extends AbstractTab {

    private static final int PER_PAGE = 45;

    private static DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    @Resource
    private TogglService togglService;

    @Resource
    private FetchAndStoreService fetchAndStoreService;

    @Resource
    private TimeEntryService timeEntryService;

    private MGrid<DateTimeEntryGroup> dateTimeGrid;

    @Override
    public Component initLayout() {
        dateTimeGrid = initFetchedDataTable();

        return new MVerticalLayout()
                .add(initFetchSelection())
                .add(dateTimeGrid, 1)
                .withSize(MSize.FULL_SIZE);
    }

    @Override
    public void onTabEnter() {
        dateTimeGrid.setDataProvider((sortOrders, offset, limit) ->
                        timeEntryService.findPaged(offset / PER_PAGE, PER_PAGE)
                                .stream(),
                () -> timeEntryService.countAll());
    }

    private ComboBox<Workspace> initWorkspaceSelect() {
        List<Workspace> workspaces = togglService.getJToggl()
                .getWorkspaces();

        ComboBox<Workspace> workspaceTypedSelect = new ComboBox("workspace", workspaces);
        workspaceTypedSelect.setItemCaptionGenerator(w -> w.getName());
        workspaceTypedSelect.setEmptySelectionAllowed(false);
        workspaceTypedSelect.setWidth("100%");
        workspaceTypedSelect.addValueChangeListener(e -> togglService.setWorkspace(e.getValue()));

        workspaces.forEach(w -> {
            if (w.getId()
                    .equals(togglService.getWorkspaceId())) {
                workspaceTypedSelect.setValue(w);
            }
        });

        return workspaceTypedSelect;
    }


    private MHorizontalLayout initFetchSelection() {
        ComboBox<Workspace> workspaceTypedSelect = initWorkspaceSelect();
        boolean workspaceSelected = workspaceTypedSelect.getValue() != null;

        DateField from = new DateField("from");
        from.setWidth("100%");
        from.setEnabled(workspaceSelected);
        DateField to = new DateField("to");
        to.setWidth("100%");
        to.setEnabled(workspaceSelected);

        MButton fetch = new MButton(VaadinIcons.DOWNLOAD, "fetch", e -> {
            if (from.getValue() != null && to.getValue() != null) {
                final UI ui = UI.getCurrent();
                ui.setPollInterval(100);

                MWindow waitWindow = initWaitWindow();
                ui.addWindow(waitWindow);

                new Thread(() -> {
                    UI.setCurrent(ui);
                    fetchAndStoreService.fetchBetween(LocalDateConverter.convert(from.getValue()), LocalDateConverter.convert(to.getValue()));

                    ui.access(() -> {
                        waitWindow.close();
                        refreshTab();
                        UI.getCurrent()
                                .setPollInterval(-1);
                    });
                }).start();
            } else {
                Notification.show("Please select from and to");
            }
        });
        fetch.setEnabled(workspaceSelected);

        workspaceTypedSelect.addValueChangeListener(e -> {
            from.setEnabled(e != null);
            to.setEnabled(e != null);
            fetch.setEnabled(e != null);
        });

        return new MHorizontalLayout()
                .add(workspaceTypedSelect, 1)
                .add(from, 1)
                .add(to, 1)
                .add(fetch, Alignment.BOTTOM_CENTER)
                .withFullWidth();
    }

    private MWindow initWaitWindow() {
        ProgressBar progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        return new MWindow("Fetching Data...").withContent(new MVerticalLayout().withSize(MSize.FULL_SIZE)
                .add(progressBar, Alignment.MIDDLE_CENTER))
                .withModal(true)
                .withDraggable(false)
                .withClosable(false)
                .withResizable(false)
                .withWidth("200px")
                .withHeight("200px")
                .withCenter();
    }

    private MGrid<DateTimeEntryGroup> initFetchedDataTable() {
        MGrid<DateTimeEntryGroup> grid = new MGrid<>(DateTimeEntryGroup.class)
                .withProperties()
                .withSize(MSize.FULL_SIZE);
        grid.addComponentColumn((ValueProvider<DateTimeEntryGroup, Component>) bean -> new MLabel(togglService.getWorkspaceById(bean.getWorkspaceId())
                .getName()))
                .setCaption("workspace");
        grid.addColumn("date")
                .setCaption("date");
        grid.addComponentColumn((ValueProvider<DateTimeEntryGroup, Component>) bean ->
                new MLabel(bean.getFetched() != null ? bean.getFetched()
                        .toString(DATE_TIME_FORMAT) : "-"))
                .setCaption("fetched");
        grid.addComponentColumn((ValueProvider<DateTimeEntryGroup, Component>) bean -> new MLabel(String.valueOf(bean.getUserTimeEntriesMap()
                .size())))
                .setCaption("count of users");
        grid.addComponentColumn((ValueProvider<DateTimeEntryGroup, Component>) bean -> {
            AtomicLong totalMilliseconds = new AtomicLong(0);
            bean.getUserTimeEntriesMap()
                    .forEach((user, timeEntries) -> {
                        totalMilliseconds.addAndGet(timeEntries.stream()
                                .mapToLong(e -> e.getDuration())
                                .sum());
                    });
            return new MLabel(UserTimeline.PERIOD_FORMATTER.print(new Period(totalMilliseconds.get())));
        })
                .setCaption("total time");

        grid.setColumnReorderingAllowed(false);
        grid.getColumns()
                .forEach(c -> c.setSortable(false));
        return grid;
    }
}
