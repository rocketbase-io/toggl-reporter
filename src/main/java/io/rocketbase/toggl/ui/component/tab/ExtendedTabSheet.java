package io.rocketbase.toggl.ui.component.tab;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.themes.ValoTheme;


public class ExtendedTabSheet extends CustomComponent {

    private TabSheet tabSheet;
    private AbstractTab currentTab;

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

    public ExtendedTabSheet addTab(String caption, AbstractTab tab) {
        tab.setTabSheet(this);
        tabSheet.addTab(tab, caption);
        if (currentTab == null) {
            currentTab = tab;
        }
        return this;
    }

}

