package io.rocketbase.toggl.backend.model.global;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Note {

    private String title;

    private String body;

    private DateTime created;

    private String username;
}
