package io.rocketbase.toggl.backend.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

import static io.rocketbase.toggl.backend.model.DailyWorkingLog.COLLECTION_NAME;

@Document(collection = COLLECTION_NAME)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyWorkingLog {

    public static final String COLLECTION_NAME = "dailyWorkingLogs";

    @Id
    private String id;

    @DBRef(lazy = true)
    private Worker worker;

    @DBRef(lazy = true)
    private LeaveEntry leaveEntry;

    private LocalDate date;

    /**
     * logs the time someone should work
     */
    private Integer minutesToWork;

    /**
     * actual worked minutes by someone
     */
    private Integer minutesWorked;

    /**
     * time to used in daily calculation<br>
     * for example someone worked 3hrs and then stopped earlier because of sickness -> daily working hours get filled, but minutesWorked remains by 3hrs
     */
    private Integer minutesLogged;

    /**
     * actual earned money by worker
     */
    private BigDecimal moneyEarned;

    /**
     * automatically update will get stopped when locked=true
     */
    private boolean locked;
}
