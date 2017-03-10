package io.rocketbase.toggl.ui.view.setting.form;

import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Image;
import io.rocketbase.toggl.backend.model.ApplicationSettingModel.UserDetails;
import io.rocketbase.toggl.backend.util.ColorPalette;
import org.vaadin.viritin.MBeanFieldGroup;
import org.vaadin.viritin.fields.LabelField;
import org.vaadin.viritin.fields.TypedSelect;
import org.vaadin.viritin.fields.config.ComboBoxConfig;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.util.Arrays;


/**
 * Created by marten on 09.03.17.
 */
public class UserDetailForm extends AbstractForm<UserDetails> {

    private LabelField name = new LabelField(String.class, "name");
    private LabelField email = new LabelField(String.class, "email");

    private MLabel colorBox = new MLabel("").withContentMode(ContentMode.HTML)
            .withWidth("37px")
            .withHeight("37px");

    private TypedSelect<ColorPalette> graphColor = new TypedSelect<>(ColorPalette.class).asComboBoxType(ComboBoxConfig.build()
            .withItemStyleGenerator((ComboBox.ItemStyleGenerator) (source, itemId) -> {
                if (itemId instanceof ColorPalette) {
                    return "color-palette " + ((ColorPalette) itemId).getStyleName();
                }
                return null;
            }))
            .setNullSelectionAllowed(false)
            .setBeans(Arrays.asList(ColorPalette.values()))
            .setCaptionGenerator(e -> e.name()
                    .toLowerCase()
                    .replace("_", ""))
            .addMValueChangeListener(e -> {
                colorBox.setValue(String.format("<div class=\"color-box\" style=\"background-color: #%s\"></div>",
                        e.getValue()
                                .getHexCode()));
            })
            .withFullWidth();

    private Image avatar;

    public UserDetailForm() {
        super();
        avatar = new Image(null, null);
        avatar.setWidth("64px");
        avatar.setHeight("64px");

    }

    @Override
    public MBeanFieldGroup<UserDetails> setEntity(UserDetails entity) {
        MBeanFieldGroup<UserDetails> result = super.setEntity(entity);
        if (entity != null) {
            avatar.setSource(new ExternalResource(entity.getAvatar()));
        } else {
            avatar.setSource(null);
        }
        return result;
    }

    @Override
    protected Component createContent() {
        return new MVerticalLayout()
                .add(new MHorizontalLayout()
                        .add(new MVerticalLayout()
                                .add(name)
                                .add(email)
                                .add(new MHorizontalLayout()
                                        .add(colorBox, Alignment.MIDDLE_CENTER)
                                        .add(graphColor, Alignment.MIDDLE_CENTER, 1)
                                        .withFullWidth())
                                .withMargin(false)
                                .withFullWidth(), 2)
                        .add(avatar, Alignment.MIDDLE_CENTER, 1)
                        .withFullWidth())
                .withMargin(false)
                .add(getToolbar())
                .withFullWidth();
    }
}
