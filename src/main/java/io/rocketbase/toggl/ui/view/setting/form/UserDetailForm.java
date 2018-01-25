package io.rocketbase.toggl.ui.view.setting.form;

import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import io.rocketbase.toggl.backend.model.ApplicationSetting.UserDetails;
import io.rocketbase.toggl.backend.util.ColorPalette;
import org.vaadin.viritin.fields.LabelField;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.util.Arrays;


/**
 * Created by marten on 09.03.17.
 */
public class UserDetailForm extends AbstractForm<UserDetails> {

    private LabelField name = new LabelField("name");

    private LabelField email = new LabelField("email");

    private MLabel colorBox = new MLabel("")
            .withContentMode(ContentMode.HTML)
            .withWidth("37px")
            .withHeight("37px");

    private ComboBox<ColorPalette> graphColor = new ComboBox<>("colors", Arrays.asList(ColorPalette.values()));


    private Image avatar;

    public UserDetailForm() {
        super(UserDetails.class);
        avatar = new Image(null, null);
        avatar.setWidth("64px");
        avatar.setHeight("64px");

        graphColor.setStyleGenerator((StyleGenerator<ColorPalette>) colorPalette -> "color-palette " + colorPalette.getStyleName());
        graphColor.setItemCaptionGenerator(e -> e.name()
                .toLowerCase()
                .replace("_", " "));
        graphColor.setEmptySelectionAllowed(false);
        graphColor.addValueChangeListener(e -> colorBox.setValue(String.format("<div class=\"color-box\" style=\"background-color: #%s\"></div>",
                e.getValue()
                        .getHexCode())));

        getBinder().bind(email, "email");
        getBinder().bind(name, "name");
        getBinder().bind(graphColor, "graphColor");
    }

    @Override
    public void setEntity(UserDetails entity) {
        super.setEntity(entity);
        if (entity != null) {
            avatar.setSource(new ExternalResource(entity.getAvatar()));
        } else {
            avatar.setSource(null);
        }
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
