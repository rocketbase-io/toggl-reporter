package io.rocketbase.toggl.ui;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.annotations.Theme;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import io.rocketbase.toggl.backend.config.TogglService;
import io.rocketbase.toggl.ui.component.MainScreen;
import io.rocketbase.toggl.ui.view.setting.SettingView;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

import javax.annotation.Resource;

/**
 * Created by marten on 20.02.17.
 */
@SpringUI(path = "/app")
@Theme("valo")
@StyleSheet("design.css")
public class MainUI extends UI {

    @Resource
    private MainScreen mainScreen;

    @Resource
    private TogglService togglService;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Responsive.makeResponsive(this);
        setLocale(vaadinRequest.getLocale());

        addStyleName(ValoTheme.UI_WITH_MENU);

        if (!togglService.isApiTokenAvailable()) {
            initTokenWizard();

        } else {
            setContent(mainScreen.initWithUi(this));
        }

    }

    private void initTokenWizard() {
        MTextField apiToken = new MTextField("Api-Token").withFullWidth();

        MWindow configWindow = new MWindow("Configure API-Token")
                .withWidth("50%")
                .withModal(true)
                .withResizable(false)
                .withDraggable(false)
                .withClosable(false)
                .withCenter();

        configWindow.setContent(new MVerticalLayout()
                .add(apiToken, Alignment.MIDDLE_CENTER, 1)
                .add(new MButton(FontAwesome.SAVE, "Save", e -> {
                    try {
                        togglService.updateToken(apiToken.getValue());

                        configWindow.close();
                        setContent(mainScreen.initWithUi(this));

                        UI.getCurrent()
                                .getNavigator()
                                .navigateTo(SettingView.VIEW_NAME);
                    } catch (Exception exp) {
                        Notification.show("invalid api-token", Notification.Type.ERROR_MESSAGE);
                    }
                }), Alignment.MIDDLE_CENTER));

        UI.getCurrent()
                .addWindow(configWindow);

        setContent(new MVerticalLayout());
    }

}
