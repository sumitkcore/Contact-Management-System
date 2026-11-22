package com.cms.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Date and Time utilities for formatting, calculating relative timestamps, and countdowns.
 */
public class DateTimeUtils {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("hh:mm a");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter FULL_FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");

    public static String formatDateTime(LocalDateTime ldt) {
        if (ldt == null) return "Never";
        return ldt.format(FULL_FMT);
    }

    public static String formatDate(LocalDate ld) {
        if (ld == null) return "Not Set";
        return ld.format(DATE_FMT);
    }

    public static String formatTime(LocalDateTime ldt) {
        if (ldt == null) return "";
        return ldt.format(TIME_FMT);
    }

    /**
     * Calculates days until next birthday.
     */
    public static long getDaysUntilNextBirthday(LocalDate birthday) {
        if (birthday == null) return -1;
        LocalDate today = LocalDate.now();
        LocalDate nextBday = birthday.withYear(today.getYear());
        if (nextBday.isBefore(today) || nextBday.isEqual(today)) {
            nextBday = nextBday.plusYears(1);
        }
        return ChronoUnit.DAYS.between(today, nextBday);
    }

    /**
     * Human relative time string (e.g. "Today, 10:30 AM", "Yesterday", "3 days ago").
     */
    public static String getRelativeTime(LocalDateTime ldt) {
        if (ldt == null) return "N/A";
        LocalDate date = ldt.toLocalDate();
        LocalDate today = LocalDate.now();

        if (date.isEqual(today)) {
            return "Today at " + ldt.format(TIME_FMT);
        } else if (date.isEqual(today.minusDays(1))) {
            return "Yesterday at " + ldt.format(TIME_FMT);
        } else if (Period.between(date, today).getDays() < 7 && Period.between(date, today).getMonths() == 0) {
            return Period.between(date, today).getDays() + " days ago";
        } else {
            return ldt.format(DATE_FMT);
        }
    }
}
