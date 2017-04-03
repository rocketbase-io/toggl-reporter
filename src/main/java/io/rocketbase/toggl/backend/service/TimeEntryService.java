package io.rocketbase.toggl.backend.service;

import io.rocketbase.toggl.backend.config.TogglService;
import io.rocketbase.toggl.backend.model.DateTimeEntryGroupModel;
import io.rocketbase.toggl.backend.model.report.UserTimeline;
import io.rocketbase.toggl.backend.model.report.WeekTimeline;
import io.rocketbase.toggl.backend.repository.DateTimeEntryGroupRepository;
import io.rocketbase.toggl.backend.util.LocalDateConverter;
import io.rocketbase.toggl.backend.util.YearWeekUtil;
import org.joda.time.LocalDate;
import org.joda.time.YearMonth;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.threeten.extra.YearWeek;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
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

    @Resource
    private HolidayManagerService holidayManagerService;

    public int countAll() {
        return (int) dateTimeEntryGroupRepository.count();
    }

    public List<DateTimeEntryGroupModel> findPaged(int page, int perPage) {
        return dateTimeEntryGroupRepository.findAll(new PageRequest(page, perPage, Direction.DESC, "date"))
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

    public List<YearMonth> fetchAllYearMonths() {
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

    public List<YearWeek> fetchAllYearWeeks() {
        Query query = Query.query(Criteria.where("workspaceId")
                .is(togglService.getWorkspaceId()));
        query.fields()
                .include("date");

        List<DateTimeEntryGroupModel> queryResult = mongoTemplate.find(query, DateTimeEntryGroupModel.class, DateTimeEntryGroupModel.COLLECTION_NAME);

        Set<YearWeek> result = new TreeSet<>();
        queryResult.forEach(e -> result.add(YearWeek.of(e.getDate()
                        .getYear(),
                e.getDate()
                        .getWeekOfWeekyear())));

        return result.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }


    public List<WeekTimeline> getWeekTimelines(@NotNull YearWeek from, YearWeek to) {
        List<DateTimeEntryGroupModel> queryResult = dateTimeEntryGroupRepository.findByWorkspaceIdAndDateBetween(togglService.getWorkspaceId(),
                YearWeekUtil.getFirstDay(from)
                        .minusDays(1)
                        .toDate(),
                to != null ? YearWeekUtil.getLastDay(to)
                        .plusDays(1)
                        .toDate() : LocalDate.now()
                        .plusDays(1)
                        .toDate());

        Map<YearWeek, Map<Long, UserTimeline>> result = new HashMap<>();
        queryResult.forEach(e -> {
            YearMonth yearMonth = YearMonth.fromDateFields(e.getDate()
                    .toDate());
            e.getUserTimeEntriesMap()
                    .forEach((userId, timeEntries) -> {
                        YearWeek yearWeek = YearWeek.of(e.getDate()
                                        .getYear(),
                                e.getDate()
                                        .getWeekOfWeekyear());
                        result.putIfAbsent(yearWeek, new HashMap<>());
                        Map<Long, UserTimeline> yearWeekMap = result.get(yearWeek);
                        yearWeekMap.putIfAbsent(userId, new UserTimeline(togglService.getUserById(userId), yearMonth));
                        yearWeekMap.get(userId)
                                .addTimeEntries(e.getDate(), timeEntries);
                    });
        });
        return result.entrySet()
                .stream()
                .map(e -> {
                    WeekTimeline r = new WeekTimeline(e.getKey());
                    r.getUidTimelines()
                            .putAll(e.getValue());
                    r.getHolidays()
                            .addAll(holidayManagerService.getHolidays(LocalDateConverter.convert(YearWeekUtil.getFirstDay(e.getKey())),
                                    LocalDateConverter.convert(YearWeekUtil.getLastDay(e.getKey()))));
                    return r;
                })
                .sorted(Comparator.comparing(WeekTimeline::getYearWeek)
                        .reversed())
                .collect(Collectors.toList());
    }
}
