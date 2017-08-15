package io.rocketbase.toggl.backend.service;

import io.rocketbase.toggl.api.model.TimeEntry;
import io.rocketbase.toggl.api.util.FetchAllDetailed;
import io.rocketbase.toggl.backend.config.TogglService;
import io.rocketbase.toggl.backend.model.DateTimeEntryGroup;
import io.rocketbase.toggl.backend.repository.DateTimeEntryGroupRepository;
import lombok.SneakyThrows;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by marten on 08.03.17.
 */
@Service
public class FetchAndStoreService {

    @Resource
    private DateTimeEntryGroupRepository dateTimeEntryGroupRepository;

    @Resource
    private TogglService togglService;

    @SneakyThrows
    public void fetchBetween(Date from, Date to) {


        DateTime fetchedDate = DateTime.now();
        List<TimeEntry> timeEntryList = new ArrayList<>();

        LocalDate fromAsLocalDate = LocalDate.fromDateFields(from);
        LocalDate toAsLocalDate = LocalDate.fromDateFields(to);

        if (Days.daysBetween(fromAsLocalDate, toAsLocalDate)
                .getDays() > 100) {
            LocalDate startFrom = LocalDate.fromDateFields(from);
            do {
                LocalDate toSub = startFrom.plusDays(99);
                if (toAsLocalDate.isBefore(toSub)) {
                    toSub = toAsLocalDate;
                }
                timeEntryList.addAll(fetch(startFrom, toSub));
                startFrom = startFrom.plusDays(100);
            } while (startFrom.isBefore(toAsLocalDate));
        } else {
            timeEntryList.addAll(fetch(fromAsLocalDate, toAsLocalDate));
        }

        dateTimeEntryGroupRepository.deleteByWorkspaceIdAndDateBetween(togglService.getWorkspaceId(),
                LocalDate.fromDateFields(from)
                        .minusDays(1)
                        .toDate(),
                toAsLocalDate
                        .plusDays(1)
                        .toDate());


        List<DateTimeEntryGroup> newEntities = new ArrayList<>();

        Map<LocalDate, List<TimeEntry>> dateListMap = timeEntryList.stream()
                .collect(Collectors.groupingBy(e -> e.getStart()
                        .toLocalDate()));

        dateListMap.forEach((date, timeEntries) -> {
            newEntities.add(DateTimeEntryGroup.builder()
                    .date(date)
                    .workspaceId(togglService.getWorkspaceId())
                    .fetched(fetchedDate)
                    .userTimeEntriesMap(timeEntries.stream()
                            .collect(Collectors.groupingBy(TimeEntry::getUserId)))
                    .build());
        });
        dateTimeEntryGroupRepository.save(newEntities);

    }

    private List<TimeEntry> fetch(LocalDate from, LocalDate to) {
        return FetchAllDetailed.getAll(togglService.getTogglReportApi()
                .detailed()
                .since(from.toDateTime(LocalTime.MIDNIGHT)
                        .toDate())
                .until(to.toDateTime(new LocalTime(23, 59, 59, 999))
                        .toDate()));
    }
}
