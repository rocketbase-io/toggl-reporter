package io.rocketbase.toggl.ui.component;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.FontIcon;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;
import io.rocketbase.toggl.backend.security.MongoUserDetails;
import io.rocketbase.toggl.backend.security.MongoUserService;
import io.rocketbase.toggl.backend.security.UserRole;
import io.rocketbase.toggl.ui.view.AbstractView;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.viritin.button.PrimaryButton;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;
import org.vaadin.viritin.v7.fields.MPasswordField;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@UIScope
@SpringComponent
public class Menu extends CssLayout {

    private static final String VALO_MENUITEMS = "valo-menuitems";
    private static final String VALO_MENU_TOGGLE = "valo-menu-toggle";
    private static final String VALO_MENU_VISIBLE = "valo-menu-visible";

    @Value("${application.title}")
    private String applicationTitle;

    private Map<String, MenuEntry> viewMenus = new HashMap<>();

    private CssLayout menuItemsLayout;
    private CssLayout menuPart;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private MongoUserService mongoUserService;

    @PostConstruct
    public void postConstruct() {
        setPrimaryStyleName(ValoTheme.MENU_ROOT);
        menuPart = new CssLayout();
        menuPart.addStyleName(ValoTheme.MENU_PART);

        // header of the menu
        final HorizontalLayout top = new HorizontalLayout();
        top.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        top.addStyleName(ValoTheme.MENU_TITLE);
        top.setSpacing(true);
        Label title = new Label(applicationTitle);
        title.addStyleName(ValoTheme.LABEL_H3);
        title.setSizeUndefined();
        Image image = new Image(null, new ThemeResource("img/toggl-logo.png"));
        image.setWidth(16, Unit.PIXELS);
        image.setHeight(16, Unit.PIXELS);
        image.setStyleName("logo");
        top.addComponent(image);
        top.addComponent(title);
        menuPart.addComponent(top);

        // logout menu item
        menuPart.addComponent(initLogoutMenu());

        // button for toggling the visibility of the menu when on a small screen
        final Button showMenu = new Button("Menu", (ClickListener) event -> {
            if (menuPart.getStyleName()
                    .contains(VALO_MENU_VISIBLE)) {
                menuPart.removeStyleName(VALO_MENU_VISIBLE);
            } else {
                menuPart.addStyleName(VALO_MENU_VISIBLE);
            }
        });
        showMenu.addStyleName(ValoTheme.BUTTON_PRIMARY);
        showMenu.addStyleName(ValoTheme.BUTTON_SMALL);
        showMenu.addStyleName(VALO_MENU_TOGGLE);
        showMenu.setIcon(FontAwesome.NAVICON);
        menuPart.addComponent(showMenu);

        // container for the navigation buttons, which are added by addView()
        menuItemsLayout = new CssLayout();
        menuItemsLayout.setPrimaryStyleName(VALO_MENUITEMS);

        scanViews();

        menuPart.addComponent(menuItemsLayout);

        addComponent(menuPart);
    }

    private Component initLogoutMenu() {
        MenuBar logoutMenu = new MenuBar();
        MenuBar.MenuItem logout = logoutMenu.addItem("", FontAwesome.SIGN_OUT, (MenuBar.Command) selectedItem -> {
            UI.getCurrent()
                    .getPage()
                    .setLocation("logout");
        });
        logout.setDescription("logout");
        MenuBar.MenuItem changePassword = logoutMenu.addItem("", FontAwesome.KEY, (MenuBar.Command) selectedItem -> {
            MPasswordField password = new MPasswordField("password")
                    .withRequired(true)
                    .withFullWidth();

            MWindow window = new MWindow("change password")
                    .withModal(true)
                    .withDraggable(false)
                    .withResizable(false)
                    .withCenter();
            window.setContent(new MVerticalLayout().add(password)
                    .add(new PrimaryButton("change", changeEvent -> {
                        mongoUserService.updatePassword(getLoggedInUser(), password.getValue());
                        Notification.show("successfully changed password");
                        window.close();
                    }))
                    .withWidth("300px"));
            UI.getCurrent()
                    .addWindow(window);
        });
        changePassword.setDescription("change password");
        logoutMenu.addStyleName("user-menu");
        return logoutMenu;
    }

    private MongoUserDetails getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        if (principal instanceof MongoUserDetails) {
            return (MongoUserDetails) principal;
        }
        return null;
    }

    private void scanViews() {
        List<MenuEntry> menuEntries = new ArrayList<>();
        String[] viewBeanNames = applicationContext
                .getBeanNamesForAnnotation(SpringView.class);
        for (String beanName : viewBeanNames) {
            final Class<?> type = applicationContext.getType(beanName);
            if (AbstractView.class.isAssignableFrom(type)) {
                AbstractView view = (AbstractView) applicationContext.getBean(type);
                menuEntries.add(new MenuEntry(view.getViewName(), view.getCaption(), view.getIcon(), view.getOrder(), view.getUserRole()));
            }
        }
        AtomicInteger roleOrdinal = new AtomicInteger(getLoggedInUser().getRole()
                .ordinal());
        menuEntries.stream()
                .sorted(Comparator.comparing(MenuEntry::getOrder))
                .filter(m -> m.getRole()
                        .ordinal() <= roleOrdinal.get())
                .forEach(m -> initMenuEntry(m));
    }

    private void initMenuEntry(MenuEntry menu) {
        Button button = new Button(menu.getCaption(),
                (ClickListener) event -> UI.getCurrent()
                        .getNavigator()
                        .navigateTo(menu.getName()));
        button.setPrimaryStyleName(ValoTheme.MENU_ITEM);
        button.setIcon(menu.getIcon());
        menuItemsLayout.addComponent(button);
        menu.setButton(button);
        viewMenus.put(menu.getName(), menu);
    }

    /**
     * Highlights a view navigation button as the currently active view in the
     * menu. This method does not perform the actual navigation.
     *
     * @param viewName the name of the view to show as active
     */
    public void setActiveView(String viewName) {
        for (MenuEntry menu : viewMenus.values()) {
            menu.getButton()
                    .removeStyleName("selected");
        }
        MenuEntry selected = viewMenus.get(viewName);
        if (selected != null) {
            selected.getButton()
                    .addStyleName("selected");
        }
        menuPart.removeStyleName(VALO_MENU_VISIBLE);
    }

    @Getter
    @RequiredArgsConstructor
    private static class MenuEntry {
        private final String name, caption;
        private final FontIcon icon;
        private final int order;
        private final UserRole role;
        @Setter
        private Button button;
    }
}
