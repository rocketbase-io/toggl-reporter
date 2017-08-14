package io.rocketbase.toggl.ui.view.setting.tab;

import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import de.jollyday.HolidayCalendar;
import io.rocketbase.toggl.backend.config.TogglService;
import io.rocketbase.toggl.backend.model.ApplicationSettingModel.UserDetails;
import io.rocketbase.toggl.ui.component.tab.AbstractTab;
import io.rocketbase.toggl.ui.view.setting.form.UserDetailForm;
import org.vaadin.viritin.MSize;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.v7.fields.TypedSelect;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * Created by marten on 09.03.17.
 */
@UIScope
@SpringComponent
public class SettingTab extends AbstractTab {

    @Resource
    private TogglService togglService;

    private TypedSelect<HolidayCalendar> holidayCalendarTypedSelect;

    private TypedSelect<UserDetails> userTypedSelect;

    private UserDetailForm userDetailForm;

    private MLabel placeHolder = new MLabel("");

    @Override
    public Component initLayout() {
        userDetailForm = new UserDetailForm();
        userDetailForm.setSavedHandler((e) -> {
            togglService.updateUser(e);
            refreshTab();
        });
        userDetailForm.setVisible(false);

        holidayCalendarTypedSelect = initHolidayCalenderTypeSelect();
        userTypedSelect = initUserTypeSelect();

        MButton refreshUsers = new MButton(FontAwesome.REFRESH, "Refresh Users", event -> {
            togglService.updateCurrentWorkspaceUsers();
            onTabEnter();
        });
        return new MVerticalLayout()
                .add(holidayCalendarTypedSelect)
                .add(new MHorizontalLayout().add(userTypedSelect, 1)
                        .add(refreshUsers, Alignment.BOTTOM_CENTER)
                        .withFullWidth())
                .add(userDetailForm, 1)
                .add(placeHolder, 1)
                .withSize(MSize.FULL_SIZE);
    }

    private TypedSelect<HolidayCalendar> initHolidayCalenderTypeSelect() {
        return new TypedSelect<>(HolidayCalendar.class).asComboBoxType()
                .withCaption("HolidayCalendar")
                .withDescription("get displayed in below detailed statistics")
                .addMValueChangeListener(e -> {
                    togglService.updateHolidayCalendar(e.getValue());
                })
                .withFullWidth()
                .setBeans(Arrays.asList(HolidayCalendar.values()));
    }

    private TypedSelect<UserDetails> initUserTypeSelect() {
        return new TypedSelect<>(UserDetails.class)
                .asComboBoxType()
                .withCaption("User-Settings")
                .setCaptionGenerator(u -> u.getName())
                .addMValueChangeListener(e -> {
                    userDetailForm.setVisible(e.getValue() != null);
                    placeHolder.setVisible(e.getValue() == null);
                    if (e.getValue() != null) {
                        userDetailForm.setEntity(e.getValue());
                    }
                })
                .withFullWidth();
    }

    @Override
    public void onTabEnter() {
        userTypedSelect.setBeans(togglService.getAllUsers());
        holidayCalendarTypedSelect.setValue(togglService.getHolidayCalender());
    }
}
