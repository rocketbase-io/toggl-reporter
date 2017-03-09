package io.rocketbase.toggl.ui.view;

import ch.simas.jtoggl.domain.Workspace;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import io.rocketbase.toggl.backend.config.TogglService;
import io.rocketbase.toggl.backend.model.DateTimeEntryGroupModel;
import io.rocketbase.toggl.backend.service.FetchAndStoreService;
import io.rocketbase.toggl.backend.service.TimeEntryService;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.vaadin.viritin.MSize;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MDateField;
import org.vaadin.viritin.fields.TypedSelect;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.grid.StringPropertyValueGenerator;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by marten on 08.03.17.
 */
@UIScope
@SpringView(name = SettingView.VIEW_NAME)
public class SettingView extends AbstractView {

    public static final String VIEW_NAME = "setting";

    PeriodFormatter fmt = new PeriodFormatterBuilder()
            .printZeroNever()
            .appendDays()
            .appendSuffix(" day ", " days ")
            .appendHours()
            .appendSuffix(" hours ")
            .printZeroAlways()
            .appendMinutes()
            .appendSuffix(" mins ")
            .toFormatter();

    @Resource
    private TogglService togglService;

    @Resource
    private FetchAndStoreService fetchAndStoreService;

    @Resource
    private TimeEntryService timeEntryService;
    private MGrid<DateTimeEntryGroupModel> dateTimeGrid;

    public SettingView() {
        super(VIEW_NAME, "Setting", FontAwesome.GEARS, 100);
    }

    @Override
    public Component initialzeUi() {
        TypedSelect<Workspace> workspaceTypedSelect = initWorkspaceSelect();

        dateTimeGrid = initFetchedDataTable();
        reloadData();

        return new MVerticalLayout()
                .add(initFetchSelection())
                .add(dateTimeGrid, 1)
                .withSize(MSize.FULL_SIZE);
    }

    private void reloadData() {
        dateTimeGrid.setRows(timeEntryService.findAllWorkspaceIndipendant());
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
                UI.getCurrent()
                        .setPollInterval(100);

                MWindow waitWindow = initWaitWindow();
                UI.getCurrent()
                        .addWindow(waitWindow);
                new Thread(() -> {

                    fetchAndStoreService.fetchBetween(from.getValue(), to.getValue());
                    UI.getCurrent()
                            .access(() -> {
                                waitWindow.close();
                                reloadData();
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
                .withGeneratedColumn("workspace",
                        (StringPropertyValueGenerator.ValueGenerator<DateTimeEntryGroupModel>) bean -> togglService.getWorkspaceById(bean.getWorkspaceId())
                                .getName())
                .withGeneratedColumn("userCount",
                        (StringPropertyValueGenerator.ValueGenerator<DateTimeEntryGroupModel>) bean -> String.valueOf(bean.getUserTimeEntriesMap()
                                .size()))
                .withGeneratedColumn("totalTime", (StringPropertyValueGenerator.ValueGenerator<DateTimeEntryGroupModel>) bean -> {
                    AtomicLong totalMilliseconds = new AtomicLong(0);
                    bean.getUserTimeEntriesMap()
                            .forEach((user, timeEntries) -> {
                                totalMilliseconds.addAndGet(timeEntries.stream()
                                        .mapToLong(e -> e.getDuration())
                                        .sum());
                            });
                    return fmt.print(new Period(totalMilliseconds.get()));
                })
                .withProperties("workspace", "date", "fetched", "userCount", "totalTime")
                .withColumnHeaders("workspace", "date", "fetched", "count of users", "total time")
                .withSize(MSize.FULL_SIZE);

        return grid;
    }
}
