package io.rocketbase.toggl.ui.view.setting.window;


import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Window;
import io.rocketbase.toggl.backend.model.Worker;
import io.rocketbase.toggl.backend.security.MongoUserDetails;
import io.rocketbase.toggl.backend.security.MongoUserService;
import io.rocketbase.toggl.backend.service.WorkerService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.vaadin.viritin.button.PrimaryButton;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.Resource;

@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LinkWorkerWindow extends Window {

    @Resource
    private WorkerService workerService;

    @Resource
    private MongoUserService mongoUserService;

    public LinkWorkerWindow linkUser(MongoUserDetails userDetails) {
        setCaption(String.format("link user: %s", userDetails.getUsername()));
        setWidth("400px");
        setModal(true);
        setResizable(false);
        setDraggable(false);
        center();

        ComboBox<Worker> workerSelect = new ComboBox<>("Worker", workerService.findAll());
        workerSelect.setItemCaptionGenerator(e -> e.getFullName());
        workerSelect.setWidth("100%");
        if (userDetails.getWorker() != null) {
            workerSelect.setValue(userDetails.getWorker());
        }

        setContent(new MVerticalLayout()
                .add(workerSelect)
                .add(new PrimaryButton("Save", e -> {
                    mongoUserService.updateWorker(userDetails, workerSelect.getValue());
                    close();
                }))
                .withFullWidth());

        return this;
    }


}
