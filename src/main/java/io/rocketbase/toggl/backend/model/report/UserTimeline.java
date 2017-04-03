package io.rocketbase.toggl.backend.model.report;

import io.rocketbase.toggl.api.model.TimeEntry;
import io.rocketbase.toggl.backend.model.ApplicationSettingModel.UserDetails;
import io.rocketbase.toggl.backend.util.YearMonthUtil;
import io.rocketbase.toggl.backend.util.YearWeekUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.YearMonth;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.threeten.extra.YearWeek;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UserTimeline {


    public static final PeriodFormatter PERIOD_FORMATTER = new PeriodFormatterBuilder()
            .appendHours()
            .appendSuffix(" h ")
            .printZeroAlways()
            .minimumPrintedDigits(2)
            .appendMinutes()
            .appendSuffix(" mins ")
            .toFormatter();

    @Getter
    private final UserDetails user;

    private final YearMonth yearMonth;

    private Map<LocalDate, List<TimeEntry>> dateTimeEntriesMap = new TreeMap<>();
    private Map<Integer, WeekStatistics> cachedStatistics = null;
    private WeekStatistics cachedWeekStatistics = null;

    public static double roundedHours(long milliseconds) {
        return Math.round(((double) milliseconds / 1000.0 / 60.0 / 60.0) * 10.0) / 10.0;
    }

    public UserTimeline addTimeEntries(LocalDate localDate, List<TimeEntry> entries) {
        dateTimeEntriesMap.put(localDate, entries);
        return this;
    }

    public long totalMillisecondsWorked() {
        AtomicLong totalMilliseconds = new AtomicLong(0);
        dateTimeEntriesMap.values()
                .forEach(v -> {
                    totalMilliseconds.addAndGet(v.stream()
                            .mapToLong(e -> e.getDuration())
                            .sum());
                });
        return totalMilliseconds.get();
    }

    public String getTotalHoursFormatted() {
        return UserTimeline.PERIOD_FORMATTER.print(new Period(totalMillisecondsWorked()));
    }

    public List<Double> getHoursWorked() {
        List<Double> result = new ArrayList<>();

        List<LocalDate> dateSeries = YearMonthUtil.getAllDatesOfMonth(yearMonth);
        dateSeries.forEach(day -> {
            if (dateTimeEntriesMap.containsKey(day)) {
                // duration is in milliseconds
                result.add(roundedHours(dateTimeEntriesMap.get(day)
                        .stream()
                        .mapToLong(e -> e.getDuration())
                        .sum()));
            } else {
                result.add(null);
            }
        });
        return result;
    }

    public Map<Integer, WeekStatistics> getWeekStatisticsOfMonth() {
        if (cachedStatistics == null) {
            Map<Integer, List<TimeEntry>> weekTimeEntryList = new TreeMap<>();

            dateTimeEntriesMap.forEach((date, entries) -> {
                weekTimeEntryList.putIfAbsent(date.getWeekOfWeekyear(), new ArrayList<>());
                weekTimeEntryList.get(date.getWeekOfWeekyear())
                        .addAll(entries);
            });

            Map<Integer, WeekStatistics> result = new TreeMap<>();

            Iterator<Entry<Integer, List<TimeEntry>>> iterator = weekTimeEntryList.entrySet()
                    .iterator();
            while (iterator.hasNext()) {
                Entry<Integer, List<TimeEntry>> entry = iterator.next();
                result.put(entry.getKey(), buildWeekStatistics(entry.getKey(), entry.getValue()));
            }

            cachedStatistics = result;
        }
        return cachedStatistics;
    }

    private WeekStatistics buildWeekStatistics(int weekOfWeekyear, List<TimeEntry> timeEntries) {
        double totalHours = roundedHours(timeEntries
                .stream()
                .mapToLong(v -> v.getDuration())
                .sum());

        int workedDays = timeEntries
                .stream()
                .map(v -> v.getStart()
                        .toLocalDate())
                .collect(Collectors.toSet())
                .size();

        double billableHours = roundedHours(timeEntries
                .stream()
                .filter(v -> v.getBillable() != null && v.getBillable())
                .mapToLong(v -> v.getDuration())
                .sum());

        long billableAmount = timeEntries
                .stream()
                .filter(v -> v.getBillable() != null && v.getBillable())
                .mapToLong(v -> v.getBillableAmount())
                .sum();

        return new WeekStatistics(weekOfWeekyear, totalHours, workedDays, billableHours, billableAmount);
    }

    public WeekStatistics getWeekStatisticsOfWeek(YearWeek yearWeek) {
        if (cachedWeekStatistics == null) {
            List<LocalDate> dateSeries = YearWeekUtil.getAllDatesOfYearWeek(yearWeek);
            List<TimeEntry> timeEntries = new ArrayList<>();
            dateSeries.forEach(day -> {
                if (dateTimeEntriesMap.containsKey(day)) {
                    timeEntries.addAll(dateTimeEntriesMap.get(day));
                }
            });
            cachedWeekStatistics = buildWeekStatistics(yearWeek.getWeek(), timeEntries);
        }
        return cachedWeekStatistics;
    }

    @Getter
    @RequiredArgsConstructor
    public static class WeekStatistics {
        private final int weekOfWeekyear;
        private final double totalHours;
        private final int workedDays;
        private final double billableHours;
        private final long billableAmount;

        public double getAverageHoursPerDay() {
            return Math.round((totalHours / (double) workedDays) * 10.0) / 10.0;
        }

    }


}
