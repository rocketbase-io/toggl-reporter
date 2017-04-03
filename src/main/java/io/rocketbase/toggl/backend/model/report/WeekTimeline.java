package io.rocketbase.toggl.backend.model.report;

import de.jollyday.Holiday;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.threeten.extra.YearWeek;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Getter
public class WeekTimeline {

    private final YearWeek yearWeek;
    private Set<Holiday> holidays = new HashSet<>();
    private Map<Long, UserTimeline> uidTimelines = new HashMap<>();

    private static double round(double value) {
        return Math.round((value) * 10.0) / 10.0;
    }

    public double getTotalHours() {
        return round(uidTimelines.values()
                .stream()
                .mapToDouble(e -> e.getWeekStatisticsOfWeek(yearWeek)
                        .getTotalHours())
                .sum());
    }

    public double getBillableHours() {
        return round(uidTimelines.values()
                .stream()
                .mapToDouble(e -> e.getWeekStatisticsOfWeek(yearWeek)
                        .getBillableHours())
                .sum());
    }

    public long getBillableAmount() {
        return uidTimelines.values()
                .stream()
                .mapToLong(e -> e.getWeekStatisticsOfWeek(yearWeek)
                        .getBillableAmount())
                .sum();
    }

}