package io.rocketbase.toggl.ui.view.setting;

import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Component;
import io.rocketbase.toggl.backend.security.UserRole;
import io.rocketbase.toggl.ui.component.tab.ExtendedTabSheet;
import io.rocketbase.toggl.ui.view.AbstractView;
import io.rocketbase.toggl.ui.view.setting.tab.LoginUserTab;
import io.rocketbase.toggl.ui.view.setting.tab.PullDataTab;
import io.rocketbase.toggl.ui.view.setting.tab.SchedulingTab;
import io.rocketbase.toggl.ui.view.setting.tab.SettingTab;
import org.vaadin.viritin.MSize;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.Resource;

/**
 * Created by marten on 08.03.17.
 */
@UIScope
@SpringView(name = SettingView.VIEW_NAME)
public class SettingView extends AbstractView {

    public static final String VIEW_NAME = "setting";


    @Resource
    private SettingTab settingTab;

    @Resource
    private SchedulingTab schedulingTab;

    @Resource
    private LoginUserTab loginUserTab;

    @Resource
    private PullDataTab pullDataTab;

    public SettingView() {
        super(VIEW_NAME, "Setting", FontAwesome.GEARS, 100);
        setUserRole(UserRole.ROLE_ADMIN);
    }

    @Override
    public Component initialzeUi() {
        ExtendedTabSheet tabSheet = new ExtendedTabSheet();
        tabSheet.addTab(FontAwesome.GEARS, "settings", settingTab);
        tabSheet.addTab(FontAwesome.CALENDAR_TIMES_O, "scheduling", schedulingTab);
        tabSheet.addTab(FontAwesome.USERS, "login-user", loginUserTab);
        tabSheet.addTab(FontAwesome.DOWNLOAD, "pull-data", pullDataTab);


        return new MVerticalLayout()
                .add(tabSheet, 1)
                .withSize(MSize.FULL_SIZE);
    }

}
