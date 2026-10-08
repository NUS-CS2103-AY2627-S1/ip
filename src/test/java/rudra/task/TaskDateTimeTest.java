package rudra.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import rudra.exception.RudraException;

/**
 * Tests for {@link TaskDateTime}.
 */
public class TaskDateTimeTest {
    @Test
    public void parse_dateOnly_returnsDateWithoutTime() throws RudraException {
        TaskDateTime parsedDate = TaskDateTime.parse("2026-08-28");

        assertEquals("2026-08-28", parsedDate.toStorageString());
        assertEquals("Aug 28 2026", parsedDate.toDisplayString());
    }

    @Test
    public void parse_dateTimeWithoutColon_returnsNormalizedDateTime() throws RudraException {
        TaskDateTime parsedDateTime = TaskDateTime.parse("2026-08-28 1745");

        assertEquals("2026-08-28 1745", parsedDateTime.toStorageString());
        assertEquals("Aug 28 2026 5:45pm", parsedDateTime.toDisplayString());
    }

    @Test
    public void parse_dateTimeWithColon_returnsNormalizedDateTime() throws RudraException {
        TaskDateTime parsedDateTime = TaskDateTime.parse("2026-08-28 17:45");

        assertEquals("2026-08-28 1745", parsedDateTime.toStorageString());
        assertEquals("Aug 28 2026 5:45pm", parsedDateTime.toDisplayString());
    }

    @Test
    public void parse_invalidFormat_throwsRudraException() {
        RudraException exception = assertThrows(RudraException.class, () -> TaskDateTime.parse("28-08-2026"));

        assertEquals("Please enter dates as yyyy-mm-dd or yyyy-mm-dd HHmm.", exception.getMessage());
    }

    @Test
    public void parse_invalidMonth_throwsRudraException() {
        RudraException exception = assertThrows(RudraException.class, () -> TaskDateTime.parse("2026-13-01"));

        assertEquals("Please enter dates as yyyy-mm-dd or yyyy-mm-dd HHmm.", exception.getMessage());
    }

    @Test
    public void constructor_missingValue_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new TaskDateTime(null, true));
    }

    @Test
    public void constructor_dateWithoutTimeButNonMidnightValue_throwsAssertionError() {
        LocalDateTime nonMidnightValue = LocalDateTime.of(2026, 8, 28, 17, 45);

        assertThrows(AssertionError.class, () -> new TaskDateTime(nonMidnightValue, false));
    }

    @Test
    public void toStorageString_dateWithoutTime_returnsDateOnlyString() {
        TaskDateTime taskDateTime = new TaskDateTime(LocalDateTime.of(2026, 8, 28, 0, 0), false);

        assertEquals("2026-08-28", taskDateTime.toStorageString());
    }

    @Test
    public void toStorageString_dateTimeWithTime_returnsCompactDateTimeString() {
        TaskDateTime taskDateTime = new TaskDateTime(LocalDateTime.of(2026, 8, 28, 17, 45), true);

        assertEquals("2026-08-28 1745", taskDateTime.toStorageString());
    }

    @Test
    public void toDisplayString_dateWithoutTime_returnsFormattedDate() {
        TaskDateTime taskDateTime = new TaskDateTime(LocalDateTime.of(2026, 8, 28, 0, 0), false);

        assertEquals("Aug 28 2026", taskDateTime.toDisplayString());
    }

    @Test
    public void toDisplayString_dateTimeWithTime_returnsFormattedDateTime() {
        TaskDateTime taskDateTime = new TaskDateTime(LocalDateTime.of(2026, 8, 28, 17, 45), true);

        assertEquals("Aug 28 2026 5:45pm", taskDateTime.toDisplayString());
    }

    @Test
    public void compareTo_differentAndEqualValues_returnsChronologicalOrder() throws RudraException {
        TaskDateTime earlier = TaskDateTime.parse("2026-08-27 2359");
        TaskDateTime dateOnly = TaskDateTime.parse("2026-08-28");
        TaskDateTime explicitMidnight = TaskDateTime.parse("2026-08-28 0000");

        assertTrue(earlier.compareTo(dateOnly) < 0);
        assertTrue(dateOnly.compareTo(earlier) > 0);
        assertEquals(0, dateOnly.compareTo(explicitMidnight));
    }
}
