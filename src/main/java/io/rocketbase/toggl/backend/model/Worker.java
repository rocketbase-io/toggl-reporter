package io.rocketbase.toggl.backend.model;

import io.rocketbase.toggl.backend.model.global.Note;
import io.rocketbase.toggl.backend.model.worker.Contact;
import io.rocketbase.toggl.backend.model.worker.ContactType;
import io.rocketbase.toggl.backend.model.worker.ContractTerms;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

import static io.rocketbase.toggl.backend.model.Worker.COLLECTION_NAME;

@Document(collection = COLLECTION_NAME)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Worker {

    public static final String COLLECTION_NAME = "workers";

    private String firstName;

    private String lastName;

    private Map<ContactType, Contact> contacts;

    private LocalDate dateOfJoining;

    private List<ContractTerms> contractTerms;

    private List<Note> notes;

    @Transient
    public String getFullName() {
        return String.format("%s %s", firstName != null ? firstName : "", lastName != null ? lastName : "")
                .trim();
    }

}
