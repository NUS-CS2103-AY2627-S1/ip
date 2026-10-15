package bern.datetime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

/**
 * Verifies date-time formatting and chronological ordering with optional times.
 */
public class DateTimeTest {
    @Test
    public void dateTimeString_nullTime_displays() {
        LocalDate date = LocalDate.of(2026, 8, 27);

        DateTime dateTime = new DateTime(date, null);

        assertEquals(
                date.format(DateTime.DATE_FORMATTER),
                dateTime.toString()
        );
    }

    @Test
    public void dateTimeString_nullTime_comparisons() {
        LocalDate date = LocalDate.of(2026, 8, 27);
        LocalTime time = LocalTime.of(6, 7);

        DateTime nullDateTime1 = new DateTime(date, null);
        DateTime nullDateTime2 = new DateTime(date, null);
        DateTime dateTime = new DateTime(date, time);

        assertTrue(dateTime.compareTo(nullDateTime1) > 0);
        assertTrue(nullDateTime1.compareTo(dateTime) < 0);
        assertEquals(0, nullDateTime1.compareTo(nullDateTime2));
    }

    @Test
    public void compareDate_distantDates_preservesChronologicalOrder() {
        DateTime earliest = new DateTime(LocalDate.EPOCH, null);
        DateTime latest = new DateTime(LocalDate.EPOCH.plusDays((long) Integer.MAX_VALUE + 1), null);

        assertTrue(earliest.compareDate(latest) < 0);
        assertTrue(latest.compareDate(earliest) > 0);
        assertTrue(earliest.compareTo(latest) < 0);
        assertTrue(latest.compareTo(earliest) > 0);
    }

    @Test
    public void compareTo_timesWithinSameMinute_preservesChronologicalOrder() {
        LocalDate date = LocalDate.of(2026, 9, 15);
        DateTime earlier = new DateTime(date, LocalTime.of(9, 0, 1));
        DateTime later = new DateTime(date, LocalTime.of(9, 0, 2));

        assertTrue(earlier.compareTo(later) < 0);
        assertTrue(later.compareTo(earlier) > 0);
        assertEquals(0, earlier.compareDate(later));
    }
}
