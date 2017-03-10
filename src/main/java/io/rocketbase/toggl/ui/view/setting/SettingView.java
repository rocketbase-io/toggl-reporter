package io.rocketbase.toggl.ui.view.setting;

import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Component;
import io.rocketbase.toggl.ui.component.tab.ExtendedTabSheet;
import io.rocketbase.toggl.ui.view.AbstractView;
import io.rocketbase.toggl.ui.view.setting.tab.PullDataTab;
import io.rocketbase.toggl.ui.view.setting.tab.SettingTab;
import org.vaadin.viritin.MSize;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.Resource;

/**
 * Created by marten on 08.03.17.
 */
@UIScope
@SpringView(name = SettingView.VIEW_NAME)
public class SettingView extends AbstractView {

    public static final String VIEW_NAME = "setting";

    @Resource
    private PullDataTab pullDataTab;

    @Resource
    private SettingTab settingTab;


    public SettingView() {
        super(VIEW_NAME, "Setting", FontAwesome.GEARS, 100);
    }

    @Override
    public Component initialzeUi() {
        ExtendedTabSheet tabSheet = new ExtendedTabSheet();
        tabSheet.addTab("pull-data", pullDataTab);
        tabSheet.addTab("settings", settingTab);


        return new MVerticalLayout()
                .add(tabSheet, 1)
                .withSize(MSize.FULL_SIZE);
    }

}
