package io.rocketbase.toggl.backend.util;

import org.joda.time.format.ISODateTimeFormat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class LocalDateConverter {

    public static LocalDate convert(org.joda.time.LocalDate localDate) {
        return LocalDate.of(localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth());
    }

    public static org.joda.time.LocalDate convert(LocalDate localDate) {
        return org.joda.time.LocalDate.parse(localDate.format(DateTimeFormatter.BASIC_ISO_DATE), ISODateTimeFormat.basicDate());
    }
}
