package io.rocketbase.toggl.ui.view.worker.form;

import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import io.rocketbase.toggl.backend.model.worker.ContractTerms;
import io.rocketbase.toggl.backend.util.LocalDateConverter;
import org.vaadin.viritin.fields.IntegerField;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.time.DayOfWeek;

public class ContractTermsForm extends AbstractForm<ContractTerms> {

    private MTextField name = new MTextField("name")
            .withFullWidth();

    private CheckBoxGroup<DayOfWeek> weeklyWorkingDays = new CheckBoxGroup<>("Working Days");

    private MTextField weeklyWorkingHours = new MTextField("weekly working hours")
            .withFullWidth();

    private IntegerField daysOfVacationPerYear = new IntegerField("days of vacation per year")
            .withFullWidth();

    private MTextField grossMonthlySalary = new MTextField("gross monthly salary")
            .withFullWidth();

    private MTextField netMonthlySalary = new MTextField("net monthly salary")
            .withFullWidth();

    private DateField validFromYoda = new DateField("valid from");

    private DateField validToYoda = new DateField("valid to");

    public ContractTermsForm() {
        super(ContractTerms.class);

        weeklyWorkingDays.setDescription("used to leave calculations");
        weeklyWorkingDays.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        weeklyWorkingDays.setItems(DayOfWeek.values());
        weeklyWorkingDays.setWidth("100%");

        getBinder().bind(name, "name");
        getBinder().bind(weeklyWorkingDays, "weeklyWorkingDays");
        getBinder().bind(daysOfVacationPerYear, "daysOfVacationPerYear");

        getBinder().forField(weeklyWorkingHours)
                .withNullRepresentation("")
                .withConverter(new StringToBigDecimalConverter("invalid input"))
                .bind("weeklyWorkingHours");

        getBinder().forField(grossMonthlySalary)
                .withNullRepresentation("")
                .withConverter(new StringToBigDecimalConverter("invalid input"))
                .bind("grossMonthlySalary");

        getBinder().forField(netMonthlySalary)
                .withNullRepresentation("")
                .withConverter(new StringToBigDecimalConverter("invalid input"))
                .bind("netMonthlySalary");
    }

    @Override
    public Window openInModalPopup() {
        Window window = super.openInModalPopup();
        window.setWidth("600px");
        return window;
    }

    @Override
    public ContractTerms getEntity() {
        ContractTerms entity = super.getEntity();
        entity.setValidFrom(validFromYoda.getValue() != null ? LocalDateConverter.convert(validFromYoda.getValue()) : null);
        entity.setValidTo(validToYoda.getValue() != null ? LocalDateConverter.convert(validToYoda.getValue()) : null);
        return entity;
    }

    @Override
    protected Component createContent() {
        validFromYoda.setWidth("100%");
        validToYoda.setWidth("100%");

        return new MVerticalLayout()
                .add(name)
                .add(weeklyWorkingDays)
                .add(new MHorizontalLayout()
                        .add(weeklyWorkingHours, 1)
                        .add(daysOfVacationPerYear, 1)
                        .withMargin(false)
                        .withFullWidth())
                .add(new MHorizontalLayout()
                        .add(grossMonthlySalary, 1)
                        .add(netMonthlySalary, 1)
                        .withMargin(false)
                        .withFullWidth())
                .add(new MHorizontalLayout()
                        .add(validFromYoda, 1)
                        .add(validToYoda, 1)
                        .withMargin(false)
                        .withFullWidth())
                .add(getToolbar())
                .withFullWidth();
    }
}
