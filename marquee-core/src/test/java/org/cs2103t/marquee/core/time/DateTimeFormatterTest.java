package org.cs2103t.marquee.core.time;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.junit.jupiter.api.Test;

class DateTimeFormatterTest {
    @Test
    void parseDateTimeAbsolute() {
        LocalDate today = LocalDate.now();
        assertEquals(
                today.atTime(17, 5, 55),
                DateTimeFormatter.parseDateTime("17:05:55")
        );
        assertEquals(
                LocalDateTime.of(today.getYear(), 12, 1, 0, 0, 0),
                DateTimeFormatter.parseDateTime("1/12")
        );
        assertEquals(
                LocalDateTime.of(1999, 12, 1, 12, 55, 0),
                DateTimeFormatter.parseDateTime("1-12-1999 1255")
        );
        assertEquals(
                LocalDateTime.of(1999, 12, 1, 12, 55, 0),
                DateTimeFormatter.parseDateTime("1 Dec  1999   12:55")
        );
        assertEquals(
                LocalDateTime.of(1999, 12, 1, 12, 55, 7),
                DateTimeFormatter.parseDateTime("12:55:07   dec-1-1999")
        );
        assertEquals(
                today.atTime(12, 55),
                DateTimeFormatter.parseDateTime("1255 today")
        );
        assertEquals(
                today.plusDays(2)
                        .atTime(1, 0, 5),
                DateTimeFormatter.parseDateTime("tomorrow tomorrow 01:00:05")
        );
        assertEquals(
                today.minusDays(3)
                        .atTime(1, 0, 5),
                DateTimeFormatter.parseDateTime("01:00:05 yesterday yesterday yesterday")
        );
        assertEquals(
                today.minusDays(3)
                        .atTime(1, 0, 5),
                DateTimeFormatter.parseDateTime("01:00:05 yesterday yesterday yesterday")
        );
        assertEquals(
                today.with(TemporalAdjusters.previous(DayOfWeek.MONDAY))
                        .atTime(23, 59, 59),
                DateTimeFormatter.parseDateTime("last monday 23:59:59")
        );
        assertEquals(
                today.with(TemporalAdjusters.next(DayOfWeek.FRIDAY))
                        .plusWeeks(1)
                        .atTime(23, 59, 59),
                DateTimeFormatter.parseDateTime("next next Fri 23:59:59")
        );
    }

    @Test
    void parseDateTimeRelative() {
        LocalDateTime now = LocalDateTime.now();
        assertEquals(
                now.truncatedTo(ChronoUnit.HOURS)
                        .minusHours(1),
                DateTimeFormatter.parseDateTime("1hrs ago")
        );
        assertEquals(
                now.truncatedTo(ChronoUnit.MINUTES)
                        .minusHours(3)
                        .minusMinutes(5),
                DateTimeFormatter.parseDateTime("3 hr 5  min ago")
        );
        assertEquals(
                now.truncatedTo(ChronoUnit.SECONDS)
                        .plusHours(2)
                        .plusMinutes(12)
                        .plusSeconds(50),
                DateTimeFormatter.parseDateTime("2 hours 12min 50s later")
        );
        assertEquals(
                now.truncatedTo(ChronoUnit.DAYS)
                        .plusDays(4),
                DateTimeFormatter.parseDateTime("2 days 2d later")
        );
        assertEquals(
                now.truncatedTo(ChronoUnit.SECONDS)
                        .plusDays(1),
                DateTimeFormatter.parseDateTime("1 day 0 seconds later")
        );
    }

    @Test
    void formatDateTime() {
        List<String> monthNames = DateTimeFormatter.MONTHS;
        List<String> dowNames = DateTimeFormatter.DAYS_OF_WEEK;
        LocalDate today = LocalDate.now();
        assertEquals(
                "today 17:05:55",
                DateTimeFormatter.formatDateTime(
                        today.atTime(17, 5, 55)
                )
        );
        LocalDate tomorrow = today.plusDays(1);
        assertEquals(
                "tomorrow",
                DateTimeFormatter.formatDateTime(
                        tomorrow.atTime(0, 0)
                )
        );
        LocalDate fiveDaysAgo = today.minusDays(5);
        assertEquals(
                "last "
                        + dowNames.get(fiveDaysAgo.getDayOfWeek().getValue() - 1)
                        + " 04:00",
                DateTimeFormatter.formatDateTime(
                        fiveDaysAgo.atTime(4, 0)
                )
        );
        LocalDate oneMonthAgo = today.minusMonths(1);
        assertEquals(
                oneMonthAgo.getDayOfMonth() + " "
                        + monthNames.get(oneMonthAgo.getMonthValue() - 1)
                        + " 12:05:45",
                DateTimeFormatter.formatDateTime(
                        oneMonthAgo.atTime(12, 5, 45)
                )
        );
        LocalDate oneYearLater = today.plusYears(1);
        assertEquals(
                oneYearLater.getDayOfMonth() + " "
                        + monthNames.get(oneYearLater.getMonthValue() - 1) + " "
                        + oneYearLater.getYear(),
                DateTimeFormatter.formatDateTime(
                        oneYearLater.atTime(0, 0)
                )
        );
    }
}