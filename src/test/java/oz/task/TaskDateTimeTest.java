package oz.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import oz.exception.OzException;

/**
 * Tests the parsing, formatting, and comparison functionality of {@link TaskDateTime}.
 */
public class TaskDateTimeTest {

    /** Verifies that date-only construction uses midnight for chronological comparisons. */
    @Test
    public void fromDate_validDate_omitsTimeAndComparesAtStartOfDay() {
        LocalDate date = LocalDate.of(2019, 10, 15);
        TaskDateTime dateOnly = TaskDateTime.fromDate(date);
        TaskDateTime midnight = TaskDateTime.fromDateTime(date.atStartOfDay());
        assertEquals(date, dateOnly.toLocalDate());
        assertEquals("Oct 15 2019", dateOnly.toDisplayString());
        assertEquals("2019-10-15", dateOnly.toStorageString());
        assertFalse(dateOnly.isBefore(midnight));
        assertFalse(dateOnly.isAfter(midnight));
    }

    /** Verifies standalone dates use the same display format as task values. */
    @Test
    public void formatDate_validDate_returnsTaskDisplayFormat() {
        assertEquals("Oct 15 2019", TaskDateTime.formatDate(
                LocalDate.of(2019, 10, 15)));
    }

    /** Verifies explicit time construction retains midnight and nonzero minutes. */
    @Test
    public void fromDateTime_midnightAndAfternoon_preservesExplicitTime() {
        TaskDateTime midnight = TaskDateTime.fromDateTime(LocalDateTime.of(2019, 10, 15, 0, 0));
        TaskDateTime afternoon = TaskDateTime.fromDateTime(LocalDateTime.of(2019, 10, 15, 14, 30));
        assertEquals("Oct 15 2019, 12am", midnight.toDisplayString());
        assertEquals("2019-10-15 0000", midnight.toStorageString());
        assertEquals("Oct 15 2019, 2:30pm", afternoon.toDisplayString());
        assertEquals("2019-10-15 1430", afternoon.toStorageString());
        assertTrue(afternoon.isAfter(midnight));
    }

    @Test
    public void parse_validIsoDate_success() throws OzException {
        TaskDateTime dateTime = TaskDateTime.parse("2019-10-15");
        assertEquals("Oct 15 2019", dateTime.toDisplayString());
        assertEquals("2019-10-15", dateTime.toStorageString());
        assertEquals(LocalDate.of(2019, 10, 15), dateTime.toLocalDate());
        assertEquals("Oct 15 2019", dateTime.toString());
    }

    /** Verifies that display formats distinguish midnight, noon, and times with minutes. */
    @Test
    public void toDisplayString_midnightNoonAndMinutes_preservesClockNotation() throws OzException {
        assertEquals("Oct 15 2019, 12am", TaskDateTime.parse("2019-10-15 0000").toDisplayString());
        assertEquals("Oct 15 2019, 12pm", TaskDateTime.parse("2019-10-15 1200").toDisplayString());
        assertEquals("Oct 15 2019, 12:05pm", TaskDateTime.parse("2019-10-15 1205").toDisplayString());
    }

    /** Verifies that omitting a time remains distinct from explicitly specifying midnight. */
    @Test
    public void toDisplayString_dateOnlyAndExplicitMidnight_distinguishesTimePresence() throws OzException {
        TaskDateTime dateOnly = TaskDateTime.parse("2019-10-15");
        TaskDateTime midnight = TaskDateTime.parse("2019-10-15 0000");
        assertEquals(dateOnly.toLocalDate(), midnight.toLocalDate());
        assertEquals("Oct 15 2019", dateOnly.toDisplayString());
        assertEquals("Oct 15 2019, 12am", midnight.toDisplayString());
    }

    @Test
    public void parse_validSlashDate_success() throws OzException {
        TaskDateTime dateTime = TaskDateTime.parse("2/12/2019");
        assertEquals("Dec 02 2019", dateTime.toDisplayString());
        assertEquals("2019-12-02", dateTime.toStorageString());
    }

    @Test
    public void parse_validDashDate_success() throws OzException {
        TaskDateTime dateTime = TaskDateTime.parse("15-10-2019");
        assertEquals("Oct 15 2019", dateTime.toDisplayString());
        assertEquals("2019-10-15", dateTime.toStorageString());
    }

    @Test
    public void parse_validDateTime24h_success() throws OzException {
        TaskDateTime dateTime = TaskDateTime.parse("2/12/2019 1800");
        assertEquals("Dec 02 2019, 6pm", dateTime.toDisplayString());
        assertEquals("2019-12-02 1800", dateTime.toStorageString());
    }

    @Test
    public void parse_validDateTimeWithMinutes_success() throws OzException {
        TaskDateTime dateTime = TaskDateTime.parse("2019-10-15 1830");
        assertEquals("Oct 15 2019, 6:30pm", dateTime.toDisplayString());
        assertEquals("2019-10-15 1830", dateTime.toStorageString());
    }

    @Test
    public void parse_validDateTimeWithColon_success() throws OzException {
        TaskDateTime dateTime = TaskDateTime.parse("2019-10-15 18:45");
        assertEquals("Oct 15 2019, 6:45pm", dateTime.toDisplayString());
        assertEquals("2019-10-15 1845", dateTime.toStorageString());
    }

    @Test
    public void parse_valid12HourAmPm_success() throws OzException {
        TaskDateTime dateTimeLower = TaskDateTime.parse("2019-10-15 6pm");
        assertEquals("Oct 15 2019, 6pm", dateTimeLower.toDisplayString());
        assertEquals("2019-10-15 1800", dateTimeLower.toStorageString());

        TaskDateTime dateTimeUpper = TaskDateTime.parse("2/12/2019 9:15AM");
        assertEquals("Dec 02 2019, 9:15am", dateTimeUpper.toDisplayString());
        assertEquals("2019-12-02 0915", dateTimeUpper.toStorageString());
    }

    @Test
    public void parse_isoLocalDateTime_success() throws OzException {
        TaskDateTime dateTime = TaskDateTime.parse("2019-10-15T14:30:00");
        assertEquals("Oct 15 2019, 2:30pm", dateTime.toDisplayString());
        assertEquals("2019-10-15 1430", dateTime.toStorageString());
    }

    @Test
    public void parse_invalidDateFormat_exceptionThrown() {
        OzException exception = assertThrows(OzException.class, () ->
                TaskDateTime.parse("not-a-valid-date"));
        assertEquals("Please provide a valid date/time (e.g., 2019-10-15 or 2/12/2019 1800).",
                exception.getMessage());
    }

    @Test
    public void parse_emptyOrBlankInput_exceptionThrown() {
        OzException emptyException = assertThrows(OzException.class, () -> TaskDateTime.parse(""));
        assertEquals("Date/time argument cannot be empty.", emptyException.getMessage());

        OzException blankException = assertThrows(OzException.class, () -> TaskDateTime.parse("   "));
        assertEquals("Date/time argument cannot be empty.", blankException.getMessage());

        OzException nullException = assertThrows(OzException.class, () -> TaskDateTime.parse(null));
        assertEquals("Date/time argument cannot be empty.", nullException.getMessage());
    }

    @Test
    public void parseDate_dateOnlyAcceptedAndDateTimeRejected() throws OzException {
        assertEquals(LocalDate.of(2026, 10, 2), TaskDateTime.parseDate("2/10/2026"));
        assertThrows(OzException.class, () -> TaskDateTime.parseDate("2026-10-02 1400"));
    }

    @Test
    public void parseTime_supportedFormatsAcceptedAndDateTimeRejected() throws OzException {
        assertEquals(LocalTime.of(14, 0), TaskDateTime.parseTime("1400"));
        assertEquals(LocalTime.of(14, 30), TaskDateTime.parseTime("14:30"));
        assertEquals(LocalTime.of(14, 0), TaskDateTime.parseTime("2pm"));
        assertEquals(LocalTime.of(9, 15), TaskDateTime.parseTime("9:15AM"));
        assertThrows(OzException.class, () -> TaskDateTime.parseTime("2026-10-02 1400"));
    }

    @Test
    public void plusDays_dateAndDateTime_preservesTimePresence() throws OzException {
        TaskDateTime date = TaskDateTime.parse("2026-10-02").plusDays(7);
        TaskDateTime dateTime = TaskDateTime.parse("2026-10-02 1430").plusDays(7);

        assertEquals("Oct 09 2026", date.toDisplayString());
        assertEquals("Oct 09 2026, 2:30pm", dateTime.toDisplayString());
    }

    @Test
    public void isAfter_earlierAndLaterDateTimes_correctComparison() throws OzException {
        TaskDateTime earlier = TaskDateTime.parse("2026-09-01 0900");
        TaskDateTime later = TaskDateTime.parse("2026-09-01 1800");

        assertTrue(later.isAfter(earlier));
        assertFalse(earlier.isAfter(later));
        assertTrue(earlier.isBefore(later));
        assertFalse(later.isBefore(earlier));
    }

    @Test
    public void parse_nonExistentDates_exceptionThrown() {
        OzException feb30 = assertThrows(OzException.class, () ->
                TaskDateTime.parse("2024-02-30"));
        assertEquals("That date does not exist on the calendar (e.g., February 30).",
                feb30.getMessage());

        OzException feb29NonLeap = assertThrows(OzException.class, () ->
                TaskDateTime.parse("2023-02-29"));
        assertEquals("That date does not exist on the calendar (e.g., February 30).",
                feb29NonLeap.getMessage());

        OzException apr31 = assertThrows(OzException.class, () ->
                TaskDateTime.parse("31/4/2023 1400"));
        assertEquals("That date does not exist on the calendar (e.g., February 30).",
                apr31.getMessage());

        OzException dateFeb30 = assertThrows(OzException.class, () ->
                TaskDateTime.parseDate("2023-02-30"));
        assertEquals("That date does not exist on the calendar (e.g., February 30).",
                dateFeb30.getMessage());
    }

    @Test
    public void parseDate_emptyOrInvalidInput_exceptionThrown() {
        OzException nullEx = assertThrows(OzException.class, () -> TaskDateTime.parseDate(null));
        assertEquals("Date argument cannot be empty.", nullEx.getMessage());

        OzException blankEx = assertThrows(OzException.class, () -> TaskDateTime.parseDate("   "));
        assertEquals("Date argument cannot be empty.", blankEx.getMessage());

        OzException invalidEx = assertThrows(OzException.class, () -> TaskDateTime.parseDate("not-a-date"));
        assertEquals("Please provide a valid date (e.g., 2019-10-15 or 2/12/2019).", invalidEx.getMessage());
    }

    @Test
    public void parseTime_emptyOrInvalidInput_exceptionThrown() {
        OzException nullEx = assertThrows(OzException.class, () -> TaskDateTime.parseTime(null));
        assertEquals("Time argument cannot be empty.", nullEx.getMessage());

        OzException blankEx = assertThrows(OzException.class, () -> TaskDateTime.parseTime("   "));
        assertEquals("Time argument cannot be empty.", blankEx.getMessage());

        OzException invalidEx = assertThrows(OzException.class, () -> TaskDateTime.parseTime("not-a-time"));
        assertEquals("Please provide a valid time (e.g., 1400, 14:00, or 2pm).", invalidEx.getMessage());
    }
}
