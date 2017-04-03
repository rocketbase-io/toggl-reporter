package io.rocketbase.toggl.backend.service;

import de.jollyday.Holiday;
import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;
import de.jollyday.ManagerParameters;
import io.rocketbase.toggl.backend.config.TogglService;
import org.joda.time.YearMonth;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by marten on 10.03.17.
 */
@Service
public class HolidayManagerService {

    @Resource
    private TogglService togglService;

    public static LocalDate convert(org.joda.time.LocalDate localDate) {
        return LocalDate.of(localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth());
    }

    public static org.joda.time.LocalDate convert(LocalDate localDate) {
        return org.joda.time.LocalDate.parse(localDate.format(DateTimeFormatter.BASIC_ISO_DATE), ISODateTimeFormat.basicDate());
    }

    public Set<Holiday> getHolidays(YearMonth yearMonth) {
        HolidayCalendar holidayCalendar = togglService.getHolidayCalender();
        Set<Holiday> result = new TreeSet<>();
        if (holidayCalendar != null) {
            HolidayManager m = HolidayManager.getInstance(ManagerParameters.create(holidayCalendar, null));
            result.addAll(m.getHolidays(convert(yearMonth.toLocalDate(1)),
                    convert(yearMonth.toLocalDate(1)
                            .plusMonths(1)
                            .minusDays(1))));
        }
        return result;
    }

}
