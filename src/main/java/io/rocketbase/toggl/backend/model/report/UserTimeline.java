package io.rocketbase.toggl.backend.model.report;

import io.rocketbase.toggl.api.model.TimeEntry;
import io.rocketbase.toggl.backend.model.ApplicationSettingModel.UserDetails;
import io.rocketbase.toggl.backend.util.YearMonthUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joda.time.LocalDate;
import org.joda.time.YearMonth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class UserTimeline {

    @Getter
    private final UserDetails user;

    private Map<LocalDate, List<TimeEntry>> dateTimeEntriesMap = new HashMap<>();

    public UserTimeline addTimeEntries(LocalDate localDate, List<TimeEntry> entries) {
        dateTimeEntriesMap.put(localDate, entries);
        return this;
    }

    public List<Double> getHoursWorked(YearMonth yearMonth) {
        List<Double> result = new ArrayList<>();

        YearMonthUtil.getAllDatesOfMonth(yearMonth)
                .forEach(day -> {
                    if (dateTimeEntriesMap.containsKey(day)) {
                        // duration is in milliseconds
                        result.add((double) Math.round((((double) dateTimeEntriesMap.get(day)
                                .stream()
                                .mapToLong(e -> e.getDuration())
                                .sum()) / 1000 / 60 / 60) * 10) / 10);
                    } else {
                        result.add(null);
                    }
                });
        return result;
    }


}
