package io.rocketbase.toggl.backend.model;

import ch.simas.jtoggl.domain.Workspace;
import de.jollyday.HolidayCalendar;
import io.rocketbase.toggl.backend.util.ColorPalette;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

/**
 * Created by marten on 08.03.17.
 */
@Document(collection = "applicationSettings")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationSettingModel {

    @Id
    private String id;

    private long currentWorkspaceId;

    private String apiToken;

    private Map<Long, Workspace> workspaceMap;

    private Map<Long, UserDetails> userMap;

    private HolidayCalendar holidayCalendar;

    @Data
    @RequiredArgsConstructor
    public static class UserDetails {

        private final long uid;
        private final String name;
        private final String email;

        private ColorPalette graphColor;
        private String avatar;


    }

}
