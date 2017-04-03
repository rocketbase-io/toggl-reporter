package io.rocketbase.toggl.backend.util;

import io.rocketbase.toggl.backend.service.HolidayManagerService;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joda.time.LocalDate;
import org.joda.time.YearMonth;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by marten on 09.03.17.
 */
public final class YearMonthUtil {

    public static List<LocalDate> getAllDatesOfMonth(YearMonth yearMonth) {
        List<LocalDate> result = new ArrayList<>();
        org.joda.time.LocalDate start = yearMonth.toLocalDate(1);
        do {
            result.add(start);
            start = start.plusDays(1);
        } while (start.getMonthOfYear() == yearMonth.getMonthOfYear());
        return result;
    }

    public static List<LocalDate> getAllDatesOfYearWeek(org.threeten.extra.YearWeek yearWeek) {
        List<LocalDate> result = new ArrayList<>();
        org.joda.time.LocalDate start = HolidayManagerService.convert(yearWeek.atDay(DayOfWeek.MONDAY));
        org.joda.time.LocalDate end = HolidayManagerService.convert(yearWeek.atDay(DayOfWeek.SUNDAY));
        do {
            result.add(start);
            start = start.plusDays(1);
        } while (start.isBefore(end));
        return result;
    }

    public static List<Integer> getAllWeeksOfWeekyear(YearMonth yearMonth) {
        List<LocalDate> result = getAllDatesOfMonth(yearMonth);
        return result.stream()
                .map(d -> new YearWeek(d.getDayOfYear(), d.getWeekOfWeekyear()))
                .collect(Collectors.toSet())
                .stream()
                .sorted(Comparator.comparing(YearWeek::getSorting))
                .map(v -> v.getWeek())
                .collect(Collectors.toList());
    }

    @Getter
    @EqualsAndHashCode(of = {"week"})
    @RequiredArgsConstructor
    private static class YearWeek {

        private final int sorting;
        private final int week;

    }
}
