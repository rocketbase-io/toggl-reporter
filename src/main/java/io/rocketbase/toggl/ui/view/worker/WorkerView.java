package io.rocketbase.toggl.ui.view.worker;

import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import io.rocketbase.toggl.backend.model.Worker;
import io.rocketbase.toggl.backend.model.worker.ContractTerms;
import io.rocketbase.toggl.backend.security.UserRole;
import io.rocketbase.toggl.backend.service.WorkerService;
import io.rocketbase.toggl.ui.view.AbstractView;
import io.rocketbase.toggl.ui.view.worker.form.ContractTermsForm;
import io.rocketbase.toggl.ui.view.worker.form.WorkerForm;
import org.springframework.context.ApplicationContext;
import org.vaadin.viritin.MSize;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.Resource;
import java.util.ArrayList;

@UIScope
@SpringView(name = WorkerView.VIEW_NAME)
public class WorkerView extends AbstractView {

    public static final String VIEW_NAME = "worker";

    @Resource
    private WorkerService workerService;

    @Resource
    private ApplicationContext applicationContext;

    private Grid<Worker> workerGrid;

    private Window contractTermsFormWindow = null;

    public WorkerView() {
        super(VIEW_NAME, "Worker", VaadinIcons.USERS, 10);
        setUserRole(UserRole.ROLE_ADMIN);
    }

    @Override
    public Component initialzeUi() {
        workerGrid = initWorkerGrid();
        return new MVerticalLayout()
                .add(new MButton(VaadinIcons.PLUS, "add worker", e -> {
                    openEditForm(new Worker());
                }), Alignment.TOP_RIGHT)
                .add(workerGrid, 1)
                .withSize(MSize.FULL_SIZE);
    }

    private void openEditForm(Worker worker) {
        WorkerForm form = applicationContext.getBean(WorkerForm.class);
        form.setEntity(worker);
        form.setSavedHandler(bean -> {
            workerService.updateWorker(bean);
            form.closePopup();
            reloadGrid();
        });
        form.openInModalPopup();
    }

    private Grid<Worker> initWorkerGrid() {
        Grid<Worker> grid = new Grid<>(Worker.class);
        grid.setColumns();
        grid.addColumn("fullName")
                .setCaption("Name");
        grid.addColumn("dateOfJoining")
                .setCaption("Date joined");
        grid.addComponentColumn((ValueProvider<Worker, Component>) bean -> new MButton(VaadinIcons.PENCIL, e -> openEditForm(bean))
                .withStyleName(ValoTheme.BUTTON_BORDERLESS))
                .setCaption("edit")
                .setWidth(100);
        grid.addComponentColumn((ValueProvider<Worker, Component>) bean -> new MButton(VaadinIcons.WRENCH, e -> {
            ContractTermsForm form = new ContractTermsForm();
            form.setEntity(bean.getContractTerms() != null && !bean.getContractTerms()
                    .isEmpty() ? bean.getContractTerms()
                    .get(0) : new ContractTerms());

            form.setSavedHandler((AbstractForm.SavedHandler<ContractTerms>) contractTerms -> {
                if (bean.getContractTerms() == null) {
                    bean.setContractTerms(new ArrayList<>());
                    bean.getContractTerms()
                            .add(contractTerms);
                    workerService.updateWorker(bean);
                    contractTermsFormWindow.close();
                    reloadGrid();
                }
            });
            contractTermsFormWindow = form.openInModalPopup();
        })
                .withStyleName(ValoTheme.BUTTON_BORDERLESS))
                .setCaption("contact")
                .setWidth(100);
        grid.setSizeFull();
        return grid;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
        reloadGrid();
    }

    private void reloadGrid() {
        workerGrid.setItems(workerService.findAll());
    }
}
