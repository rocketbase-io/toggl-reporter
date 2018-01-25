package io.rocketbase.toggl.ui.view.worker.form;

import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import io.rocketbase.toggl.backend.model.Worker;
import io.rocketbase.toggl.backend.util.LocalDateConverter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MVerticalLayout;

@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WorkerForm extends AbstractForm<Worker> {

    private TextField firstName = new MTextField("first name").withFullWidth();

    private TextField lastName = new MTextField("last name").withFullWidth();

    private DateField dateOfJoiningJavaTime = new DateField();

    public WorkerForm() {
        super(Worker.class);

        getBinder().bind(firstName, "firstName");
        getBinder().bind(lastName, "lastName");
    }

    @Override
    public Worker getEntity() {
        Worker worker = super.getEntity();
        // manual mapping because of joda java.time conversation problems
        worker.setDateOfJoining(dateOfJoiningJavaTime.getValue() != null ? LocalDateConverter.convert(dateOfJoiningJavaTime.getValue()) : null);
        return worker;
    }

    @Override
    public void setEntity(Worker entity) {
        super.setEntity(entity);
        if (entity != null && entity.getDateOfJoining() != null) {
            dateOfJoiningJavaTime.setValue(LocalDateConverter.convert(entity.getDateOfJoining()));
        }
    }

    @Override
    public Window openInModalPopup() {
        Window window = super.openInModalPopup();
        window.setWidth("500px");
        return window;
    }

    @Override
    protected Component createContent() {
        dateOfJoiningJavaTime.setWidth("100%");
        return new MVerticalLayout()
                .add(firstName)
                .add(lastName)
                .add(dateOfJoiningJavaTime)
                .add(getToolbar())
                .withFullWidth();
    }
}
