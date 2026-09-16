package serangooner.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import serangooner.SerangoonerException;

public class TaskDateTimeTest {
    @Test
    public void parse_bareDate_keepsTheDateAndShowsNoTime() {
        assertEquals("01 Sep 2026", TaskDateTime.parse("2026-09-01").toString());
    }

    @Test
    public void parse_dateWithTime_showsTheTimeOfDay() {
        assertEquals("01 Sep 2026, 6:00PM", TaskDateTime.parse("2026-09-01 1800").toString());
    }

    @Test
    public void parse_midnightGivenExplicitly_stillShowsTheTime() {
        assertEquals("01 Sep 2026, 12:00AM", TaskDateTime.parse("2026-09-01 0000").toString());
    }

    @Test
    public void parse_surroundingWhitespace_ignoresIt() {
        assertEquals("01 Sep 2026", TaskDateTime.parse("  2026-09-01  ").toString());
    }

    @Test
    public void parse_unrecognizedFormat_exceptionThrown() {
        assertThrows(SerangoonerException.class, () -> TaskDateTime.parse("next tuesday"));
        assertThrows(SerangoonerException.class, () -> TaskDateTime.parse("01-09-2026"));
        assertThrows(SerangoonerException.class, () -> TaskDateTime.parse("2026-09-01 6pm"));
        assertThrows(SerangoonerException.class, () -> TaskDateTime.parse(""));
    }

    @Test
    public void parse_monthOutOfRange_exceptionThrown() {
        assertThrows(SerangoonerException.class, () -> TaskDateTime.parse("2026-13-01"));
        assertThrows(SerangoonerException.class, () -> TaskDateTime.parse("2026-00-01"));
    }

    @Test
    public void parse_dayPastEndOfMonth_exceptionThrown() {
        assertThrows(SerangoonerException.class, () -> TaskDateTime.parse("2026-02-30"));
        assertThrows(SerangoonerException.class, () -> TaskDateTime.parse("2026-04-31"));
        assertThrows(SerangoonerException.class, () -> TaskDateTime.parse("2026-02-29 1200"));
    }

    @Test
    public void parse_leapDay_returnsIt() {
        assertEquals("29 Feb 2028", TaskDateTime.parse("2028-02-29").toString());
    }

    @Test
    public void parse_outOfRangeTime_exceptionThrown() {
        assertThrows(SerangoonerException.class, () -> TaskDateTime.parse("2026-09-01 2500"));
        assertThrows(SerangoonerException.class, () -> TaskDateTime.parse("2026-09-01 2400"));
        assertThrows(SerangoonerException.class, () -> TaskDateTime.parse("2026-09-01 1260"));
    }

    @Test
    public void parse_rightShapeButNoSuchDate_messageSaysTheDateDoesNotExist() {
        SerangoonerException exception = assertThrows(SerangoonerException.class, () ->
                TaskDateTime.parse("2026-02-30"));
        assertTrue(exception.getMessage().contains("isn't on any calendar"));
    }

    @Test
    public void isSameMomentAs_bareDateAndThatDateAtMidnight_returnsTrue() {
        // The same moment, although not equal values, since only one of them shows a time.
        TaskDateTime bareDate = TaskDateTime.parse("2026-09-01");
        assertTrue(bareDate.isSameMomentAs(TaskDateTime.parse("2026-09-01 0000")));
    }

    @Test
    public void isSameMomentAs_bareDateAndLaterThatDay_returnsFalse() {
        TaskDateTime bareDate = TaskDateTime.parse("2026-09-01");
        assertFalse(bareDate.isSameMomentAs(TaskDateTime.parse("2026-09-01 0001")));
    }

    @Test
    public void parse_unrecognizedFormat_messageQuotesInputAndGivesTheHint() {
        SerangoonerException exception = assertThrows(SerangoonerException.class, () ->
                TaskDateTime.parse("next tuesday"));
        assertTrue(exception.getMessage().contains("next tuesday"));
        assertTrue(exception.getMessage().contains(TaskDateTime.FORMAT_HINT));
    }

    @Test
    public void toSaveFormat_bareDate_readsBackAsTheSameValue() {
        TaskDateTime original = TaskDateTime.parse("2026-09-01");
        assertEquals("2026-09-01", original.toSaveFormat());
        assertEquals(original.toString(), TaskDateTime.parse(original.toSaveFormat()).toString());
    }

    @Test
    public void toSaveFormat_dateWithTime_readsBackAsTheSameValue() {
        TaskDateTime original = TaskDateTime.parse("2026-09-01 1800");
        assertEquals("2026-09-01 1800", original.toSaveFormat());
        assertEquals(original.toString(), TaskDateTime.parse(original.toSaveFormat()).toString());
    }

    @Test
    public void isBefore_earlierValue_returnsTrue() {
        assertTrue(TaskDateTime.parse("2026-09-01").isBefore(TaskDateTime.parse("2026-09-02")));
    }

    @Test
    public void isBefore_laterValue_returnsFalse() {
        assertFalse(TaskDateTime.parse("2026-09-02").isBefore(TaskDateTime.parse("2026-09-01")));
    }

    @Test
    public void isBefore_sameValue_returnsFalse() {
        assertFalse(TaskDateTime.parse("2026-09-01").isBefore(TaskDateTime.parse("2026-09-01")));
    }

    @Test
    public void isBefore_sameDayDifferentTimes_comparesTheTime() {
        assertTrue(TaskDateTime.parse("2026-09-01 0900")
                .isBefore(TaskDateTime.parse("2026-09-01 1700")));
    }

    @Test
    public void isBefore_bareDateAgainstSameDayWithTime_countsAsStartOfDay() {
        assertTrue(TaskDateTime.parse("2026-09-01")
                .isBefore(TaskDateTime.parse("2026-09-01 0900")));
    }

    @Test
    public void parseDate_dateWithTime_dropsTheTimeOfDay() {
        assertEquals(LocalDate.of(2026, 9, 1), TaskDateTime.parseDate("2026-09-01 1800"));
    }

    @Test
    public void format_date_writesItTheWayTheUserSeesIt() {
        assertEquals("01 Sep 2026", TaskDateTime.format(LocalDate.of(2026, 9, 1)));
    }

    @Test
    public void toLocalDate_bareDate_returnsThatDay() {
        assertEquals(LocalDate.of(2026, 9, 1), TaskDateTime.parse("2026-09-01").toLocalDate());
    }

    @Test
    public void toLocalDate_dateWithTime_dropsTheTimeOfDay() {
        assertEquals(LocalDate.of(2026, 9, 1),
                TaskDateTime.parse("2026-09-01 1800").toLocalDate());
    }

    @Test
    public void parseDate_bareDate_returnsThatDay() {
        assertEquals(LocalDate.of(2026, 9, 1), TaskDateTime.parseDate("2026-09-01"));
    }

    @Test
    public void parseDate_unreadableText_exceptionThrown() {
        assertThrows(SerangoonerException.class, () -> TaskDateTime.parseDate("whenever"));
    }

    @Test
    public void equals_sameTextParsedTwice_returnsTrueWithMatchingHashCodes() {
        TaskDateTime first = TaskDateTime.parse("2026-09-01 1800");
        TaskDateTime second = TaskDateTime.parse(" 2026-09-01 1800 ");
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void equals_bareDateAgainstThatDateAtMidnight_returnsFalse() {
        assertNotEquals(TaskDateTime.parse("2026-09-01"), TaskDateTime.parse("2026-09-01 0000"));
    }

    @Test
    public void equals_differentTimesOnTheSameDay_returnsFalse() {
        assertNotEquals(TaskDateTime.parse("2026-09-01 1800"), TaskDateTime.parse("2026-09-01 1900"));
    }

    @Test
    public void parseAndToString_defaultLocaleChinese_stillReadAndWriteEnglish() {
        // A machine set to Chinese must not change what the user types or sees.
        Locale originalLocale = Locale.getDefault();
        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
        try {
            TaskDateTime dateTime = TaskDateTime.parse("2026-09-01 1800");
            assertEquals("01 Sep 2026, 6:00PM", dateTime.toString());
            assertEquals("2026-09-01 1800", dateTime.toSaveFormat());
        } finally {
            Locale.setDefault(originalLocale);
        }
    }
}
