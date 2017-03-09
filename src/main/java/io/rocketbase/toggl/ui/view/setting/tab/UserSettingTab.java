package io.rocketbase.toggl.ui.view.setting.tab;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Component;
import io.rocketbase.toggl.backend.config.TogglService;
import io.rocketbase.toggl.backend.model.ApplicationSettingModel.UserDetails;
import io.rocketbase.toggl.ui.component.tab.AbstractTab;
import io.rocketbase.toggl.ui.view.setting.form.UserDetailForm;
import org.vaadin.viritin.MSize;
import org.vaadin.viritin.fields.TypedSelect;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.Resource;

/**
 * Created by marten on 09.03.17.
 */
@UIScope
@SpringComponent
public class UserSettingTab extends AbstractTab {

    @Resource
    private TogglService togglService;

    private TypedSelect<UserDetails> userTypedSelect;

    private UserDetailForm userDetailForm;

    @Override
    public Component initLayout() {
        userDetailForm = new UserDetailForm();
        userDetailForm.setSavedHandler((e) -> {
            togglService.updateUser(e);
            refreshTab();
        });
        userDetailForm.setVisible(false);

        userTypedSelect = initUserTypeSelect();

        return new MVerticalLayout()
                .add(userTypedSelect)
                .add(userDetailForm, 1)
                .withSize(MSize.FULL_SIZE);
    }

    private TypedSelect<UserDetails> initUserTypeSelect() {
        return new TypedSelect<>(UserDetails.class)
                .asComboBoxType()
                .setCaptionGenerator(u -> u.getName())
                .addMValueChangeListener(e -> {
                    userDetailForm.setVisible(e.getValue() != null);
                    if (e.getValue() != null) {
                        userDetailForm.setEntity(e.getValue());
                    }
                })
                .withFullWidth();
    }

    @Override
    public void onTabEnter() {
        userTypedSelect.setBeans(togglService.getAllUsers());
    }
}
