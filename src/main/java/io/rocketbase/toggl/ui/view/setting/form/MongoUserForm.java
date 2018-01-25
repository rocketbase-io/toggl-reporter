package io.rocketbase.toggl.ui.view.setting.form;

import com.vaadin.ui.*;
import io.rocketbase.toggl.backend.security.MongoUserDetails;
import io.rocketbase.toggl.backend.security.UserRole;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.util.Arrays;

public class MongoUserForm extends AbstractForm<MongoUserDetails> {

    private TextField username = new TextField("username");

    private PasswordField newPassword = new PasswordField("password");

    private CheckBox enabled = new CheckBox("enabled");

    private boolean createNew = false;

    private ComboBox<UserRole> role = new ComboBox("role", Arrays.asList(UserRole.values()));

    private Image avatar;

    public MongoUserForm(MongoUserDetails bean) {
        super(MongoUserDetails.class);

        username.setWidth("100%");
        newPassword.setWidth("100%");

        role.setWidth("100%");
        role.setItemCaptionGenerator(e -> e.name()
                .toLowerCase()
                .replace("_", " "));


        setModalWindowTitle("edit login-user");
        setEntity(bean);
        newPassword.setVisible(false);


        getBinder().bind(username, "username");
        getBinder().bind(enabled, "enabled");
        getBinder().bind(role, "role");
    }

    public Window initCreateWindow(SavedHandler<MongoUserDetails> handler) {
        createNew = true;
        setSaveCaption("create");
        newPassword.setVisible(true);

        Window window = openInModalPopup();
        window.setWidth("500px");
        setSavedHandler((SavedHandler<MongoUserDetails>) entity -> {
            handler.onSave(entity);
            window.close();
        });
        return window;
    }

    public Window initEditWindow(SavedHandler<MongoUserDetails> savedHandler, DeleteHandler<MongoUserDetails> deleteHandler) {
        Window window = openInModalPopup();
        window.setWidth("500px");

        setSavedHandler((SavedHandler<MongoUserDetails>) entity -> {
            savedHandler.onSave(entity);
            window.close();
        });
        setDeleteHandler((DeleteHandler<MongoUserDetails>) entity -> {
            deleteHandler.onDelete(entity);
            window.close();
        });

        return window;
    }

    @Override
    public MongoUserDetails getEntity() {
        MongoUserDetails enitity = super.getEntity();
        if (createNew) {
            enitity.setPassword(newPassword.getValue());
        }
        return enitity;
    }

    @Override
    protected Component createContent() {
        return new MVerticalLayout()
                .add(new MVerticalLayout()
                        .add(username)
                        .add(newPassword)
                        .add(role)
                        .add(enabled)
                        .withMargin(false)
                        .withFullWidth())
                .add(getToolbar())
                .withFullWidth();
    }
}
