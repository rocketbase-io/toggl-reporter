package io.rocketbase.toggl.backend.model;

import ch.simas.jtoggl.domain.Workspace;
import de.jollyday.HolidayCalendar;
import io.rocketbase.toggl.backend.util.ColorPalette;
import lombok.*;
import org.joda.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

import static io.rocketbase.toggl.backend.model.ApplicationSetting.COLLECTION_NAME;

/**
 * Created by marten on 08.03.17.
 */
@Document(collection = COLLECTION_NAME)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationSetting {

    public static final String COLLECTION_NAME = "applicationSettings";

    @Id
    private String id;

    private long currentWorkspaceId;

    private String apiToken;

    private Map<Long, Workspace> workspaceMap;

    private Map<Long, UserDetails> userMap;

    private HolidayCalendar holidayCalendar;

    private List<DayOfWeek> regularWorkinsDays;

    private SchedulingConfig schedulingConfig = SchedulingConfig.EMPTY;

    @Data
    @RequiredArgsConstructor
    public static class UserDetails {

        private final long uid;

        private final String name;

        private final String email;

        private ColorPalette graphColor;

        private String avatar;

    }


    @Data
    @RequiredArgsConstructor
    public static class SchedulingConfig {

        public static final SchedulingConfig EMPTY = new SchedulingConfig(false, null);

        private final boolean enableScheduling;

        private final LocalDate startSchedulingFrom;

        @Setter
        private LocalDate lastFinishedDate;
    }


}
