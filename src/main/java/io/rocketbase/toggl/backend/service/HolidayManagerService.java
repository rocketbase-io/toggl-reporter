package io.rocketbase.toggl.backend.service;

import de.jollyday.Holiday;
import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;
import de.jollyday.ManagerParameters;
import io.rocketbase.toggl.backend.config.TogglService;
import io.rocketbase.toggl.backend.util.LocalDateConverter;
import org.joda.time.YearMonth;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by marten on 10.03.17.
 */
@Service
public class HolidayManagerService {

    @Resource
    private TogglService togglService;


    public Set<Holiday> getHolidays(YearMonth yearMonth) {
        HolidayCalendar holidayCalendar = togglService.getHolidayCalender();
        Set<Holiday> result = new TreeSet<>();
        if (holidayCalendar != null) {
            HolidayManager m = HolidayManager.getInstance(ManagerParameters.create(holidayCalendar, null));
            result.addAll(m.getHolidays(LocalDateConverter.convert(yearMonth.toLocalDate(1)),
                    LocalDateConverter.convert(yearMonth.toLocalDate(1)
                            .plusMonths(1)
                            .minusDays(1))));
        }
        return result;
    }

    public Set<Holiday> getHolidays(LocalDate from, LocalDate to) {
        HolidayCalendar holidayCalendar = togglService.getHolidayCalender();
        Set<Holiday> result = new TreeSet<>();
        if (holidayCalendar != null) {
            HolidayManager m = HolidayManager.getInstance(ManagerParameters.create(holidayCalendar, null));
            result.addAll(m.getHolidays(from, to));
        }
        return result;
    }
}
