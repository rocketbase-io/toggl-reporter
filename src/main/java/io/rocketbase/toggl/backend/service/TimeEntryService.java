package io.rocketbase.toggl.backend.service;

import io.rocketbase.toggl.backend.config.TogglService;
import io.rocketbase.toggl.backend.model.DateTimeEntryGroupModel;
import io.rocketbase.toggl.backend.model.report.UserTimeline;
import io.rocketbase.toggl.backend.repository.DateTimeEntryGroupRepository;
import org.joda.time.LocalDate;
import org.joda.time.YearMonth;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by marten on 09.03.17.
 */
@Service
public class TimeEntryService {

    @Resource
    private DateTimeEntryGroupRepository dateTimeEntryGroupRepository;

    @Resource
    private TogglService togglService;

    @Resource
    private MongoTemplate mongoTemplate;

    public int countAll() {
        return (int) dateTimeEntryGroupRepository.count();
    }

    public List<DateTimeEntryGroupModel> findPaged(int page, int perPage) {
        return dateTimeEntryGroupRepository.findAll(new PageRequest(page, perPage, Direction.ASC, "date"))
                .getContent();
    }

    public List<DateTimeEntryGroupModel> getCurrentMonth() {
        return dateTimeEntryGroupRepository.findByWorkspaceIdAndDateBetween(togglService.getWorkspaceId(),
                LocalDate.now()
                        .withDayOfMonth(1)
                        .minusDays(1)
                        .toDate(),
                LocalDate.now()
                        .plusMonths(1)
                        .withDayOfMonth(1)
                        .toDate());
    }

    public List<YearMonth> fetchAllMonth() {
        Query query = Query.query(Criteria.where("workspaceId")
                .is(togglService.getWorkspaceId()));
        query.fields()
                .include("date");

        List<DateTimeEntryGroupModel> queryResult = mongoTemplate.find(query, DateTimeEntryGroupModel.class, DateTimeEntryGroupModel.COLLECTION_NAME);

        Set<YearMonth> result = new TreeSet<>();
        queryResult.forEach(e -> result.add(new YearMonth(e.getDate())));

        return result.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public List<UserTimeline> getUserTimeLines(YearMonth yearMonth) {
        List<DateTimeEntryGroupModel> queryResult = dateTimeEntryGroupRepository.findByWorkspaceIdAndDateBetween(togglService.getWorkspaceId(),
                yearMonth.toLocalDate(1)
                        .minusDays(1)
                        .toDate(),
                yearMonth.toLocalDate(1)
                        .plusMonths(1)
                        .toDate());

        Map<Long, UserTimeline> result = new HashMap<>();
        queryResult.forEach(e -> {
            e.getUserTimeEntriesMap()
                    .forEach((userId, timeEntries) -> {
                        result.putIfAbsent(userId, new UserTimeline(togglService.getUserById(userId), yearMonth));
                        result.get(userId)
                                .addTimeEntries(e.getDate(), timeEntries);
                    });
        });
        return new ArrayList<>(result.values());
    }
}
