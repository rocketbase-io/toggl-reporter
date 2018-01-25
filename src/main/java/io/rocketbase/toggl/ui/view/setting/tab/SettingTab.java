package io.rocketbase.toggl.ui.view.setting.tab;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.themes.ValoTheme;
import de.jollyday.HolidayCalendar;
import io.rocketbase.toggl.backend.config.TogglService;
import io.rocketbase.toggl.backend.model.ApplicationSetting.UserDetails;
import io.rocketbase.toggl.ui.component.tab.AbstractTab;
import io.rocketbase.toggl.ui.view.setting.form.UserDetailForm;
import org.vaadin.viritin.MSize;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.Resource;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by marten on 09.03.17.
 */
@UIScope
@SpringComponent
public class SettingTab extends AbstractTab {

    @Resource
    private TogglService togglService;

    private ComboBox<HolidayCalendar> holidayCalendarTypedSelect;

    private CheckBoxGroup<DayOfWeek> workingDaysSelect;

    private ComboBox<UserDetails> userTypedSelect;

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
        workingDaysSelect = initWorkingDaysSelect();
        userTypedSelect = initUserTypeSelect();

        MButton refreshUsers = new MButton(VaadinIcons.REFRESH, "Refresh Users", event -> {
            togglService.updateCurrentWorkspaceUsers();
            onTabEnter();
        });
        return new MVerticalLayout()
                .add(holidayCalendarTypedSelect)
                .add(workingDaysSelect)
                .add(new MHorizontalLayout().add(userTypedSelect, 1)
                        .add(refreshUsers, Alignment.BOTTOM_CENTER)
                        .withFullWidth())
                .add(userDetailForm, 1)
                .add(placeHolder, 1)
                .withSize(MSize.FULL_SIZE);
    }


    private ComboBox<HolidayCalendar> initHolidayCalenderTypeSelect() {
        ComboBox<HolidayCalendar> combo = new ComboBox<>("HolidayCalendar", Arrays.asList(HolidayCalendar.values()));
        combo.setDescription("get displayed in below detailed statistics");
        combo.setWidth("100%");
        combo.addValueChangeListener(e -> togglService.updateHolidayCalendar(e.getValue()));
        return combo;
    }

    private CheckBoxGroup<DayOfWeek> initWorkingDaysSelect() {
        CheckBoxGroup<DayOfWeek> checkBoxGroup = new CheckBoxGroup<>("Working Days");
        checkBoxGroup.setDescription("used to leave calculations");
        checkBoxGroup.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        checkBoxGroup.setItems(DayOfWeek.values());
        checkBoxGroup.addValueChangeListener(e -> {
            togglService.updateRegularWorkinsDays(e.getValue());
        });
        checkBoxGroup.setWidth("100%");
        return checkBoxGroup;
    }

    private ComboBox<UserDetails> initUserTypeSelect() {
        ComboBox<UserDetails> combo = new ComboBox<>("User-Settings");
        combo.setItemCaptionGenerator(u -> u.getName());
        combo.setWidth("100%");
        combo.addValueChangeListener(e -> {
            userDetailForm.setVisible(e.getValue() != null);
            placeHolder.setVisible(e.getValue() == null);
            if (e.getValue() != null) {
                userDetailForm.setEntity(e.getValue());
            }
        });
        return combo;
    }

    @Override
    public void onTabEnter() {
        userTypedSelect.setItems(togglService.getAllUsers());
        holidayCalendarTypedSelect.setValue(togglService.getHolidayCalender());
        workingDaysSelect.setValue(new HashSet<>(togglService.getRegularWorkinsDays()));
    }
}
