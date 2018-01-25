package io.rocketbase.toggl.ui.view.setting.tab;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import io.rocketbase.toggl.backend.config.TogglService;
import io.rocketbase.toggl.backend.model.ApplicationSetting.SchedulingConfig;
import io.rocketbase.toggl.backend.util.LocalDateConverter;
import io.rocketbase.toggl.ui.component.tab.AbstractTab;
import org.vaadin.viritin.button.PrimaryButton;
import org.vaadin.viritin.label.RichText;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.Resource;

/**
 * Created by marten on 09.03.17.
 */
@UIScope
@SpringComponent
public class SchedulingTab extends AbstractTab {

    @Resource
    private TogglService togglService;

    private CheckBox enableScheduling;

    private DateField startSchedulingFrom;

    @Override
    public Component initLayout() {
        enableScheduling = new CheckBox("enable scheduling", false);
        enableScheduling.addValueChangeListener(e -> checkStatus());

        startSchedulingFrom = new DateField("scheduling start from");
        checkStatus();

        return new MVerticalLayout()
                .add(new RichText().withMarkDown("### explanation\n" +
                        "will schedule every hour and refresh's not finished days")
                        .withFullWidth())
                .add(enableScheduling)
                .add(startSchedulingFrom)
                .add(new PrimaryButton("Save", event -> {
                    togglService.updateSchedulingConfig(new SchedulingConfig(enableScheduling.getValue(),
                            LocalDateConverter.convert(startSchedulingFrom.getValue())));
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
                    .getStartSchedulingFrom() != null ?
                    LocalDateConverter.convert(
                            schedulingConfig
                                    .getStartSchedulingFrom()) : null);
        }
        checkStatus();
    }
}
