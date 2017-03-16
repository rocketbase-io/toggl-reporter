package io.rocketbase.toggl.ui.view.error;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.viritin.MSize;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

public class ErrorView extends CustomComponent implements View {

    public ErrorView() {
        setCompositionRoot(new MVerticalLayout()
                .add(new MLabel("error - wrong url")
                        .withFullWidth()
                        .withStyleName(ValoTheme.NOTIFICATION_ERROR), Alignment.TOP_CENTER)
                .withSize(MSize.FULL_SIZE));
    }

    @Override
    public void enter(ViewChangeEvent event) {

    }
}
