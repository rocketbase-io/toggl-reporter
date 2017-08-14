package io.rocketbase.toggl.ui.view.setting.tab;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Component;
import io.rocketbase.toggl.backend.config.TogglService;
import io.rocketbase.toggl.backend.model.ApplicationSettingModel.SchedulingConfig;
import io.rocketbase.toggl.ui.component.tab.AbstractTab;
import org.joda.time.LocalDate;
import org.vaadin.viritin.button.PrimaryButton;
import org.vaadin.viritin.label.RichText;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.v7.fields.MCheckBox;
import org.vaadin.viritin.v7.fields.MDateField;

import javax.annotation.Resource;

/**
 * Created by marten on 09.03.17.
 */
@UIScope
@SpringComponent
public class SchedulingTab extends AbstractTab {

    @Resource
    private TogglService togglService;

    private MCheckBox enableScheduling;

    private MDateField startSchedulingFrom;

    @Override
    public Component initLayout() {
        enableScheduling = new MCheckBox("enable scheduling", false)
                .withValueChangeListener(e -> checkStatus());

        startSchedulingFrom = new MDateField("scheduling start from");
        checkStatus();

        return new MVerticalLayout()
                .add(new RichText().withMarkDown("### explanation\n" +
                        "will schedule every hour and refresh's not finished days")
                        .withFullWidth())
                .add(enableScheduling)
                .add(startSchedulingFrom)
                .add(new PrimaryButton("Save", event -> {
                    togglService.updateSchedulingConfig(new SchedulingConfig(enableScheduling.getValue(),
                            LocalDate.fromDateFields(startSchedulingFrom.getValue())));
                }))
                .withFullWidth();
    }

    private void checkStatus() {
        startSchedulingFrom.setVisible(enableScheduling.getValue());
    }

    @Override
    public void onTabEnter() {
        SchedulingConfig schedulingConfig = togglService.getSchedulingConfig();
        if (schedulingConfig != null) {
            enableScheduling.setValue(schedulingConfig
                    .isEnableScheduling());

            startSchedulingFrom.setValue(schedulingConfig
                    .getStartSchedulingFrom() != null ? schedulingConfig
                    .getStartSchedulingFrom()
                    .toDate() : null);
        }
        checkStatus();
    }
}
