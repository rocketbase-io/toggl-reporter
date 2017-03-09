package io.rocketbase.toggl.backend.service;

import io.rocketbase.toggl.api.model.TimeEntry;
import io.rocketbase.toggl.api.util.FetchAllDetailed;
import io.rocketbase.toggl.backend.config.TogglService;
import io.rocketbase.toggl.backend.model.DateTimeEntryGroupModel;
import io.rocketbase.toggl.backend.repository.DateTimeEntryGroupRepository;
import lombok.SneakyThrows;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
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
        List<TimeEntry> timeEntryList = FetchAllDetailed.getAll(togglService.getTogglReportApi()
                .detailed()
                .since(from)
                .until(to));

        dateTimeEntryGroupRepository.deleteByWorkspaceIdAndDateBetween(togglService.getWorkspaceId(),
                LocalDate.fromDateFields(from)
                        .minusDays(1)
                        .toDate(),
                LocalDate.fromDateFields(to)
                        .plusDays(1)
                        .toDate());


        List<DateTimeEntryGroupModel> newEntities = new ArrayList<>();

        Map<LocalDate, List<TimeEntry>> dateListMap = timeEntryList.stream()
                .collect(Collectors.groupingBy(e -> e.getStart()
                        .toLocalDate()));

        dateListMap.forEach((date, timeEntries) -> {
            newEntities.add(DateTimeEntryGroupModel.builder()
                    .date(date)
                    .workspaceId(togglService.getWorkspaceId())
                    .fetched(fetchedDate)
                    .userTimeEntriesMap(timeEntries.stream()
                            .collect(Collectors.groupingBy(TimeEntry::getUserId)))
                    .build());
        });
        dateTimeEntryGroupRepository.save(newEntities);

    }
}
