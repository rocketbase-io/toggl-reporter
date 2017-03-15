package io.rocketbase.toggl.ui.view.setting.form;

import com.vaadin.ui.*;
import io.rocketbase.toggl.backend.security.MongoUserDetails;
import io.rocketbase.toggl.backend.security.UserRole;
import org.vaadin.viritin.fields.MCheckBox;
import org.vaadin.viritin.fields.MPasswordField;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.fields.TypedSelect;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.util.Arrays;

public class MongoUserForm extends AbstractForm<MongoUserDetails> {

    private TextField username = new MTextField("username").withFullWidth();
    private PasswordField newPassword = new MPasswordField("password").withFullWidth();

    private MCheckBox enabled = new MCheckBox("enabled");

    private boolean createNew = false;

    private TypedSelect<UserRole> role = new TypedSelect<>(UserRole.class).asComboBoxType()
            .setNullSelectionAllowed(false)
            .setBeans(Arrays.asList(UserRole.values()))
            .setCaptionGenerator(e -> e.name()
                    .toLowerCase()
                    .replace("_", " "))
            .withFullWidth();

    private Image avatar;

    public MongoUserForm(MongoUserDetails bean) {
        super();
        setModalWindowTitle("edit login-user");
        setEntity(bean);
        newPassword.setVisible(false);
    }

    public Window initCreateWindow(SavedHandler<MongoUserDetails> handler) {
        createNew = true;
        setSaveCaption("create");
        newPassword.setVisible(true);
        newPassword.setRequired(true);

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
