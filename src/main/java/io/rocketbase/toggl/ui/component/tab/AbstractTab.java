package io.rocketbase.toggl.ui.component.tab;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by marten on 30.01.17.
 */
public abstract class AbstractTab<T> extends CustomComponent {

    @Setter(AccessLevel.PACKAGE)
    @Getter(AccessLevel.PROTECTED)
    private ExtendedTabSheet<T> tabSheet;

    private boolean initialized = false;

    public AbstractTab() {
        setSizeFull();
    }

    public abstract Component initLayout();

    public abstract void onTabEnter();

    protected void onEnter() {
        if (!initialized) {
            setCompositionRoot(initLayout());
            initialized = true;
        }
        onTabEnter();
    }

    protected void refreshTab() {
        tabSheet.triggerTabEnter();
    }

}
