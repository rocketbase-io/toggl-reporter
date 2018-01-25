package io.rocketbase.toggl.ui.view.setting.tab;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;
import io.rocketbase.toggl.backend.security.MongoUserDetails;
import io.rocketbase.toggl.backend.security.MongoUserService;
import io.rocketbase.toggl.backend.security.UserRole;
import io.rocketbase.toggl.ui.component.tab.AbstractTab;
import io.rocketbase.toggl.ui.view.setting.form.MongoUserForm;
import io.rocketbase.toggl.ui.view.setting.window.LinkWorkerWindow;
import org.springframework.context.ApplicationContext;
import org.vaadin.viritin.MSize;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.button.PrimaryButton;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

import javax.annotation.Resource;

/**
 * Created by marten on 09.03.17.
 */
@UIScope
@SpringComponent
public class LoginUserTab extends AbstractTab {

    @Resource
    private MongoUserService mongoUserService;

    @Resource
    private ApplicationContext applicationContext;

    private Grid<MongoUserDetails> userTable;

    @Override
    public Component initLayout() {
        userTable = initUserTable();

        return new MVerticalLayout()
                .add(new MButton(VaadinIcons.PLUS, "add", e -> {
                    new MongoUserForm(MongoUserDetails.builder()
                            .role(UserRole.ROLE_USER)
                            .build())
                            .initCreateWindow((AbstractForm.SavedHandler<MongoUserDetails>) entity -> {
                                mongoUserService.register(entity);
                            })
                            .addCloseListener(closeEvent -> {
                                refreshTab();
                            });
                }), Alignment.MIDDLE_RIGHT)
                .add(userTable, 1)
                .withSize(MSize.FULL_SIZE);
    }

    private Grid<MongoUserDetails> initUserTable() {
        Grid<MongoUserDetails> table = new Grid<>();
        table.addColumn(MongoUserDetails::getUsername)
                .setExpandRatio(1);
        table.addColumn(MongoUserDetails::getRole);
        table.addColumn(MongoUserDetails::isEnabled);
        table.addComponentColumn(user -> new MButton(VaadinIcons.USER,
                user.getWorker() != null ? user.getWorker()
                        .getFullName() : "not linked",
                e -> {
                    LinkWorkerWindow window = applicationContext.getBean(LinkWorkerWindow.class);
                    window.linkUser(user);
                    window.addCloseListener(closeEvent -> onTabEnter());
                    UI.getCurrent()
                            .addWindow(window);
                }).withStyleName(ValoTheme.BUTTON_BORDERLESS))
                .setCaption("linked worker")
                .setWidth(200);

        table.addComponentColumn(user -> new MButton(VaadinIcons.KEY, e -> {
            PasswordField password = new PasswordField("password");
            password.setRequiredIndicatorVisible(true);
            password.setWidth("100%");

            MWindow window = new MWindow("change password")
                    .withModal(true)
                    .withDraggable(false)
                    .withResizable(false)
                    .withCenter();
            window.setContent(new MVerticalLayout().add(password)
                    .add(new PrimaryButton("change", changeEvent -> {
                        mongoUserService.updatePassword(user, password.getValue());
                        Notification.show("successfully changed password");
                        window.close();
                    }))
                    .withWidth("300px"));
            UI.getCurrent()
                    .addWindow(window);
        }).withStyleName(ValoTheme.BUTTON_BORDERLESS, ValoTheme.BUTTON_ICON_ONLY))
                .setCaption("password")
                .setWidth(80);

        table.addComponentColumn(user -> new MButton(VaadinIcons.PENCIL, e -> {
            new MongoUserForm(user)
                    .initEditWindow((AbstractForm.SavedHandler<MongoUserDetails>) entity -> {
                        mongoUserService.updateDetailsExceptPassword(entity);
                    }, (AbstractForm.DeleteHandler<MongoUserDetails>) entity -> {
                        try {
                            mongoUserService.delete(entity);
                        } catch (Exception exception) {
                            Notification.show("you cannot delete yourself!", Type.ERROR_MESSAGE);
                        }
                    })
                    .addCloseListener(closeEvent -> {
                        refreshTab();
                    });
        }).withStyleName(ValoTheme.BUTTON_BORDERLESS, ValoTheme.BUTTON_ICON_ONLY))
                .setCaption("edit")
                .setWidth(80);

        table.setSizeFull();
        return table;
    }


    @Override
    public void onTabEnter() {
        userTable.setDataProvider(DataProvider.ofCollection(mongoUserService.findAll()));
    }
}
