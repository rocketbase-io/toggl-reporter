package io.rocketbase.toggl.backend.model;

import io.rocketbase.toggl.backend.model.global.Note;
import io.rocketbase.toggl.backend.model.leave.LeaveStatus;
import io.rocketbase.toggl.backend.model.leave.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

import static io.rocketbase.toggl.backend.model.LeaveEntry.COLLECTION_NAME;

@Document(collection = COLLECTION_NAME)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveEntry {

    public static final String COLLECTION_NAME = "leaveEntries";

    @Id
    private String id;

    @DBRef
    private Worker worker;

    private LeaveStatus leaveStatus;

    private LeaveType leaveType;

    private LocalDate start;

    private LocalDate end;

    /**
     * dates that will get counted/reduce vaction (this skipps weekend and holidays)
     */
    private List<LocalDate> clearedDays;

    /**
     * logs status-changes
     */
    private List<LeaveProtocol> protocols;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    private static class LeaveProtocol {

        private LeaveStatus status;

        private Note note;
    }

}
