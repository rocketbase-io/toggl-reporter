package io.rocketbase.toggl.ui.component;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import io.rocketbase.toggl.backend.model.global.Note;
import org.joda.time.format.DateTimeFormat;
import org.vaadin.viritin.MSize;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.label.RichText;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.util.function.Consumer;

public class NoteComponent extends CustomComponent {


    private Note note;

    public NoteComponent(Note note) {
        this.note = note;
        setSizeFull();
        setCompositionRoot(initReadOnly());
    }

    public NoteComponent(Note note, Consumer<Note> noteConsumer) {
        this.note = note;
        setSizeFull();
        setCompositionRoot(initEdit(noteConsumer));
    }

    private Component initEdit(Consumer<Note> noteConsumer) {
        MTextField title = new MTextField("Title", note != null && note.getTitle() != null ? note.getTitle() : "");
        TextArea body = new TextArea("Body");
        body.setValue(note != null && note.getBody() != null ? note.getBody() : "");
        body.setSizeFull();

        return new MVerticalLayout()
                .add(title)
                .add(body, 1)
                .add(new MButton(FontAwesome.SAVE, "save", e -> {
                    if (title.getValue() != null && title.getValue()
                            .trim()
                            .length() > 0 && body.getValue() != null && body.getValue()
                            .trim()
                            .length() > 0) {
                        noteConsumer.accept(Note.builder()
                                .title(title.getValue())
                                .body(body.getValue())
                                .build());
                    } else {
                        Notification.show("please fill title and body!");
                    }
                }))
                .withSize(MSize.FULL_SIZE);
    }

    private Component initReadOnly() {
        return new MVerticalLayout()
                .add(new MLabel(note.getTitle()).withStyleName(ValoTheme.LABEL_BOLD))
                .add(new RichText()
                        .withMarkDown(note.getBody() != null ? note.getBody() : "")
                        .withSize(MSize.FULL_SIZE), 1)
                .add(new MHorizontalLayout()
                        .add(new MLabel(note.getCreated()
                                .toString(DateTimeFormat.longDateTime()))
                                .withStyleName(ValoTheme.LABEL_SMALL), Alignment.MIDDLE_RIGHT)
                        .add(new MLabel(note.getUsername())
                                .withStyleName(ValoTheme.LABEL_SMALL, ValoTheme.LABEL_COLORED), Alignment.MIDDLE_RIGHT)
                        .withFullWidth())
                .withSize(MSize.FULL_SIZE);
    }
}
