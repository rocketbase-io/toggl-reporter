package io.rocketbase.toggl.ui.component.tab;

import com.vaadin.server.FontIcon;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.themes.ValoTheme;


public class ExtendedTabSheet<T> extends CustomComponent {

    private TabSheet tabSheet;
    private AbstractTab currentTab;
    private T filter;

    public ExtendedTabSheet() {
        setSizeFull();

        initTabSheet();
        setCompositionRoot(tabSheet);
        addAttachListener(event -> {
            triggerTabEnter();
        });
    }

    private void initTabSheet() {
        tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        tabSheet.addStyleName(ValoTheme.TABSHEET_COMPACT_TABBAR);
        tabSheet.addSelectedTabChangeListener(event -> {
            currentTab = (AbstractTab) event.getTabSheet()
                    .getSelectedTab();
            triggerTabEnter();
        });
    }

    void triggerTabEnter() {
        if (currentTab != null) {
            currentTab.onEnter();
        }
    }

    public ExtendedTabSheet addTab(FontIcon icon, String caption, AbstractTab tab) {
        tab.setTabSheet(this);
        Tab t = tabSheet.addTab(tab, caption);
        t.setIcon(icon);
        if (currentTab == null) {
            currentTab = tab;
        }
        return this;
    }

    public T getFilter() {
        return filter;
    }

    public void setFilterAndRefresh(T filter) {
        this.filter = filter;
        triggerTabEnter();
    }

}

