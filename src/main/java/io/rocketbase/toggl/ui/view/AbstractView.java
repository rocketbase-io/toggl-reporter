package io.rocketbase.toggl.ui.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontIcon;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by marten on 08.03.17.
 */
@Getter
@RequiredArgsConstructor
public abstract class AbstractView extends CustomComponent implements View {

    private final String viewName;
    private final String caption;
    private final FontIcon icon;
    private final int order;

    protected boolean initialized = false;


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        if (!initialized) {
            setSizeFull();
            setCompositionRoot(initialzeUi());
            initialized = true;
        }
    }

    /**
     * initialize the ui - get fired only once after the first enter
     *
     * @return component that will get added to composition root
     */
    public abstract Component initialzeUi();
}
