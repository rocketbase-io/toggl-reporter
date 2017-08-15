package io.rocketbase.toggl.ui.view.worker;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Component;
import io.rocketbase.toggl.backend.security.UserRole;
import io.rocketbase.toggl.ui.view.AbstractView;
import io.rocketbase.toggl.ui.view.setting.SettingView;
import org.vaadin.viritin.label.MLabel;

@UIScope
@SpringView(name = SettingView.VIEW_NAME)
public class WorkerView extends AbstractView {

    public static final String VIEW_NAME = "worker";

    public WorkerView() {
        super(VIEW_NAME, "Worker", VaadinIcons.USERS, 10);
        setUserRole(UserRole.ROLE_ADMIN);
    }

    @Override
    public Component initialzeUi() {
        return new MLabel("Test");
    }
}
