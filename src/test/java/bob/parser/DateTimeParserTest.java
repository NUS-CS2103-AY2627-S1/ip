package bob.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import bob.task.TaskDateTime;

/** Tests parsing and formatting of task dates and times. */
class DateTimeParserTest {

    @Test
    void parseUserInput_dateOnly_returnsDateWithoutTime() {
        TaskDateTime result = DateTimeParser.parseUserInput("2/12/2019");

        assertEquals(LocalDate.of(2019, 12, 2), result.getDate());
        assertFalse(result.getTime().isPresent());
    }

    @Test
    void parseUserInput_dateAndTime_returnsDateAndTime() {
        TaskDateTime result = DateTimeParser.parseUserInput("2/12/2019 1800");

        assertEquals(LocalDate.of(2019, 12, 2), result.getDate());
        assertEquals(LocalTime.of(18, 0), result.getTime().orElseThrow());
    }

    @Test
    void parseUserInput_invalidCalendarDate_illegalArgumentExceptionThrown() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> DateTimeParser.parseUserInput("32/2/2019"));

        assertEquals("Use d/M/yyyy or d/M/yyyy HHmm, such as 2/12/2019 or 2/12/2019 1800.",
                exception.getMessage());
    }

    @Test
    void parseUserInput_invalidFormat_illegalArgumentExceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> DateTimeParser.parseUserInput("2 December 2019"));
        assertThrows(IllegalArgumentException.class, () -> DateTimeParser.parseUserInput("2/12/2019 18:00"));
        assertThrows(IllegalArgumentException.class, () -> DateTimeParser.parseUserInput("2/12/19"));
    }

    @Test
    void parseUserInput_invalidTime_illegalArgumentExceptionIncludesFormatGuidance() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> DateTimeParser.parseUserInput("2/12/2019 2460"));

        assertEquals("Use d/M/yyyy or d/M/yyyy HHmm, such as 2/12/2019 or 2/12/2019 1800.",
                exception.getMessage());
    }

    @Test
    void formatForDisplay_dateOnly_returnsReadableDate() {
        TaskDateTime value = new TaskDateTime(LocalDate.of(2019, 12, 2), null);

        assertEquals("Dec 02 2019", DateTimeParser.formatForDisplay(value));
    }

    @Test
    void formatForDisplay_dateAndTime_returnsReadableDateAndTime() {
        TaskDateTime value = new TaskDateTime(LocalDate.of(2019, 12, 2), LocalTime.of(18, 5));

        assertEquals("Dec 02 2019 18:05", DateTimeParser.formatForDisplay(value));
    }

    @Test
    void formatForDisplay_inconsistentTimeValue_assertionErrorThrown() {
        TaskDateTime value = new InconsistentTaskDateTime();

        AssertionError exception = assertThrows(AssertionError.class,
                () -> DateTimeParser.formatForDisplay(value));

        assertEquals("A formatted time must be present after the empty check", exception.getMessage());
    }

    @Test
    void formatForStorage_dateOnly_returnsIsoDate() {
        TaskDateTime value = new TaskDateTime(LocalDate.of(2019, 12, 2), null);

        assertEquals("2019-12-02", DateTimeParser.formatForStorage(value));
    }

    @Test
    void formatForStorage_dateAndTime_returnsIsoDateAndTime() {
        TaskDateTime value = new TaskDateTime(LocalDate.of(2019, 12, 2), LocalTime.of(18, 5));

        assertEquals("2019-12-02T18:05", DateTimeParser.formatForStorage(value));
    }

    @Test
    void formatForStorage_inconsistentTimeValue_assertionErrorThrown() {
        TaskDateTime value = new InconsistentTaskDateTime();

        AssertionError exception = assertThrows(AssertionError.class,
                () -> DateTimeParser.formatForStorage(value));

        assertEquals("A stored time must be present after the empty check", exception.getMessage());
    }

    @Test
    void parseStorage_dateOnly_returnsDateWithoutTime() {
        TaskDateTime result = DateTimeParser.parseStorage("2019-12-02");

        assertEquals(LocalDate.of(2019, 12, 2), result.getDate());
        assertFalse(result.getTime().isPresent());
    }

    @Test
    void parseStorage_dateAndTime_returnsDateAndTime() {
        TaskDateTime result = DateTimeParser.parseStorage("2019-12-02T18:05");

        assertEquals(LocalDate.of(2019, 12, 2), result.getDate());
        assertEquals(LocalTime.of(18, 5), result.getTime().orElseThrow());
    }

    @Test
    void parseStorage_invalidFormat_illegalArgumentExceptionThrown() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> DateTimeParser.parseStorage("2/12/2019"));

        assertEquals("Invalid stored date-time: 2/12/2019", exception.getMessage());
    }

    @Test
    void parseStorage_invalidDateTime_illegalArgumentExceptionThrown() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> DateTimeParser.parseStorage("2019-02-30T18:05"));

        assertEquals("Invalid stored date-time: 2019-02-30T18:05", exception.getMessage());
    }

    @Test
    void parseStorage_invalidDate_illegalArgumentExceptionIncludesInput() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> DateTimeParser.parseStorage("2019-02-30"));

        assertEquals("Invalid stored date-time: 2019-02-30", exception.getMessage());
    }

    private static class InconsistentTaskDateTime extends TaskDateTime {
        private int timeAccessCount;

        InconsistentTaskDateTime() {
            super(LocalDate.of(2019, 12, 2), LocalTime.NOON);
        }

        @Override
        public Optional<LocalTime> getTime() {
            timeAccessCount++;
            return timeAccessCount == 1 ? Optional.of(LocalTime.NOON) : Optional.empty();
        }
    }
}
