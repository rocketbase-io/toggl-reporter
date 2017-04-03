package io.rocketbase.toggl.backend.util;

import org.joda.time.LocalDate;
import org.threeten.extra.YearWeek;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public final class YearWeekUtil {


    public static LocalDate getFirstDay(YearWeek yearWeek) {
        return LocalDateConverter.convert(yearWeek.atDay(DayOfWeek.MONDAY));
    }

    public static LocalDate getLastDay(YearWeek yearWeek) {
        return LocalDateConverter.convert(yearWeek.atDay(DayOfWeek.MONDAY))
                .plusDays(6);
    }

    public static List<LocalDate> getAllDatesOfYearWeek(YearWeek yearWeek) {
        List<LocalDate> result = new ArrayList<>();
        LocalDate start = getFirstDay(yearWeek);
        do {
            result.add(start);
            start = start.plusDays(1);
        } while (start.isBefore(getLastDay(yearWeek)));
        return result;
    }


}
