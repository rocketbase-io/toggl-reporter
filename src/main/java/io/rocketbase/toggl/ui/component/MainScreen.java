package io.rocketbase.toggl.ui.component;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import io.rocketbase.toggl.ui.view.error.ErrorView;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;

/**
 * Created by marten on 08.03.17.
 */
@UIScope
@SpringComponent
public class MainScreen extends HorizontalLayout {

    @Value("${application.title}")
    private String applicationTitle;

    @Resource
    private SpringViewProvider viewProvider;

    @Resource
    private Menu menu;

    private CssLayout viewContainer;

    public MainScreen initWithUi(UI ui) {
        setStyleName("main-screen");

        viewContainer = new CssLayout();
        viewContainer.addStyleName("valo-content");
        viewContainer.setSizeFull();


        Navigator navigator = new Navigator(ui, viewContainer);
        navigator.addProvider(viewProvider);
        navigator.setErrorView(ErrorView.class);
        navigator.addViewChangeListener(new ViewChangeListener() {

            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {
                return true;
            }

            @Override
            public void afterViewChange(ViewChangeEvent event) {
                menu.setActiveView(event.getViewName());
            }

        });

        ui.getPage()
                .setTitle(applicationTitle);

        addComponent(menu);
        addComponent(viewContainer);
        setExpandRatio(viewContainer, 1);
        setSizeFull();

        return this;
    }
}
