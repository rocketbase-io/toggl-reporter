package io.rocketbase.toggl.backend.util;

import org.joda.time.LocalDate;
import org.joda.time.YearMonth;

import java.util.ArrayList;
import java.util.List;

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
}
