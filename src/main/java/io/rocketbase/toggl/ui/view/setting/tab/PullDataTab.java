package io.rocketbase.toggl.ui.view.setting.tab;

import ch.simas.jtoggl.domain.Workspace;
import com.vaadin.data.ValueProvider;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import io.rocketbase.toggl.backend.config.TogglService;
import io.rocketbase.toggl.backend.model.DateTimeEntryGroupModel;
import io.rocketbase.toggl.backend.model.report.UserTimeline;
import io.rocketbase.toggl.backend.service.FetchAndStoreService;
import io.rocketbase.toggl.backend.service.TimeEntryService;
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
import org.vaadin.viritin.v7.fields.MDateField;
import org.vaadin.viritin.v7.fields.TypedSelect;

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

    private MGrid<DateTimeEntryGroupModel> dateTimeGrid;

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

    private TypedSelect<Workspace> initWorkspaceSelect() {
        List<Workspace> workspaces = togglService.getJToggl()
                .getWorkspaces();

        TypedSelect<Workspace> workspaceTypedSelect = new TypedSelect<>(Workspace.class).asComboBoxType()
                .withCaption("workspace")
                .setBeans(workspaces)
                .setCaptionGenerator(w -> w.getName())
                .setNullSelectionAllowed(false)
                .addMValueChangeListener(e -> togglService.setWorkspace(e.getValue()))
                .withFullWidth();

        workspaces.forEach(w -> {
            if (w.getId()
                    .equals(togglService.getWorkspaceId())) {
                workspaceTypedSelect.setValue(w);
            }
        });

        return workspaceTypedSelect;
    }


    private MHorizontalLayout initFetchSelection() {
        TypedSelect<Workspace> workspaceTypedSelect = initWorkspaceSelect();
        boolean workspaceSelected = workspaceTypedSelect.getValue() != null;

        MDateField from = new MDateField("from").withFullWidth();
        from.setEnabled(workspaceSelected);
        MDateField to = new MDateField("to").withFullWidth();
        to.setEnabled(workspaceSelected);

        MButton fetch = new MButton(FontAwesome.DOWNLOAD, "fetch", e -> {
            if (from.getValue() != null && to.getValue() != null) {
                final UI ui = UI.getCurrent();
                ui.setPollInterval(100);

                MWindow waitWindow = initWaitWindow();
                ui.addWindow(waitWindow);

                new Thread(() -> {
                    UI.setCurrent(ui);
                    fetchAndStoreService.fetchBetween(from.getValue(), to.getValue());

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

        workspaceTypedSelect.addMValueChangeListener(e -> {
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

    private MGrid<DateTimeEntryGroupModel> initFetchedDataTable() {
        MGrid<DateTimeEntryGroupModel> grid = new MGrid<>(DateTimeEntryGroupModel.class)
                .withProperties()
                .withSize(MSize.FULL_SIZE);
        grid.addComponentColumn((ValueProvider<DateTimeEntryGroupModel, Component>) bean -> new MLabel(togglService.getWorkspaceById(bean.getWorkspaceId())
                .getName()))
                .setCaption("workspace");
        grid.addColumn("date")
                .setCaption("date");
        grid.addComponentColumn((ValueProvider<DateTimeEntryGroupModel, Component>) bean ->
                new MLabel(bean.getFetched() != null ? bean.getFetched()
                        .toString(DATE_TIME_FORMAT) : "-"))
                .setCaption("fetched");
        grid.addComponentColumn((ValueProvider<DateTimeEntryGroupModel, Component>) bean -> new MLabel(String.valueOf(bean.getUserTimeEntriesMap()
                .size())))
                .setCaption("count of users");
        grid.addComponentColumn((ValueProvider<DateTimeEntryGroupModel, Component>) bean -> {
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
