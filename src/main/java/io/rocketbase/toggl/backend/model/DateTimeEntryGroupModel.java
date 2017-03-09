package io.rocketbase.toggl.backend.model;

import io.rocketbase.toggl.api.model.TimeEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

/**
 * Created by marten on 08.03.17.
 */
@Document(collection = DateTimeEntryGroupModel.COLLECTION_NAME)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DateTimeEntryGroupModel {

    public static final String COLLECTION_NAME = "dateTimeEntryGroups";

    @Id
    private String id;

    private long workspaceId;

    private LocalDate date;

    private DateTime fetched;

    private Map<Long, List<TimeEntry>> userTimeEntriesMap;

}
