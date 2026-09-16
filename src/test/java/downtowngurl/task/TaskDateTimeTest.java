package downtowngurl.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import downtowngurl.exception.DowntownGurlException;

/**
 * Tests date-time parsing and formatting used by dated tasks.
 */
public class TaskDateTimeTest {
    /**
     * Checks that a valid date and time input is parsed correctly.
     *
     * @throws DowntownGurlException If parsing unexpectedly fails.
     */
    @Test
    public void parse_validDateAndTime_returnsExpectedDateTime() throws DowntownGurlException {
        LocalDateTime parsedDateTime = TaskDateTime.parse("2/12/2019 1800");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), parsedDateTime);
    }

    /**
     * Checks that a valid date-only input uses midnight as the default time.
     *
     * @throws DowntownGurlException If parsing unexpectedly fails.
     */
    @Test
    public void parse_validDateOnly_returnsDateAtMidnight() throws DowntownGurlException {
        LocalDateTime parsedDateTime = TaskDateTime.parse("2/12/2019");

        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), parsedDateTime);
    }

    /**
     * Checks that surrounding spaces are ignored when parsing date-time input.
     *
     * @throws DowntownGurlException If parsing unexpectedly fails.
     */
    @Test
    public void parse_validInputWithLeadingAndTrailingSpaces_returnsExpectedDateTime() throws DowntownGurlException {
        LocalDateTime parsedDateTime = TaskDateTime.parse("  2/12/2019 1800  ");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), parsedDateTime);
    }

    /**
     * Checks that unsupported date-time input is rejected.
     */
    @Test
    public void parse_invalidInput_throwsDowntownGurlException() {
        assertThrows(DowntownGurlException.class, () -> TaskDateTime.parse("2 Dec 2019 6pm"));
    }

    /**
     * Checks that dates which do not exist in the calendar are rejected.
     */
    @Test
    public void parse_nonExistentCalendarDate_throwsDowntownGurlException() {
        assertThrows(DowntownGurlException.class, () -> TaskDateTime.parse("31/2/2026 1800"));
        assertThrows(DowntownGurlException.class, () -> TaskDateTime.parse("29/2/2025 1800"));
    }

    /**
     * Checks that an ISO storage date-time is parsed correctly.
     *
     * @throws DowntownGurlException If parsing unexpectedly fails.
     */
    @Test
    public void parseFromStorage_isoDateTime_returnsExpectedDateTime() throws DowntownGurlException {
        LocalDateTime parsedDateTime = TaskDateTime.parseFromStorage("2019-12-02T18:00:00");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), parsedDateTime);
    }

    /**
     * Checks that legacy storage date-time text remains readable.
     *
     * @throws DowntownGurlException If parsing unexpectedly fails.
     */
    @Test
    public void parseFromStorage_legacyDateAndTime_returnsExpectedDateTime() throws DowntownGurlException {
        LocalDateTime parsedDateTime = TaskDateTime.parseFromStorage("2/12/2019 1800");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), parsedDateTime);
    }

    /**
     * Checks that legacy date-only storage text uses midnight as the default time.
     *
     * @throws DowntownGurlException If parsing unexpectedly fails.
     */
    @Test
    public void parseFromStorage_legacyDateOnly_returnsDateAtMidnight() throws DowntownGurlException {
        LocalDateTime parsedDateTime = TaskDateTime.parseFromStorage("2/12/2019");

        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), parsedDateTime);
    }

    /**
     * Checks that surrounding spaces are ignored when parsing storage date-time text.
     *
     * @throws DowntownGurlException If parsing unexpectedly fails.
     */
    @Test
    public void parseFromStorage_inputWithLeadingAndTrailingSpaces_returnsExpectedDateTime()
            throws DowntownGurlException {
        LocalDateTime parsedDateTime = TaskDateTime.parseFromStorage("  2019-12-02T18:00:00  ");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), parsedDateTime);
    }

    /**
     * Checks that invalid storage date-time text is rejected.
     */
    @Test
    public void parseFromStorage_invalidInput_throwsDowntownGurlException() {
        assertThrows(DowntownGurlException.class, () -> TaskDateTime.parseFromStorage("2 Dec 2019 6pm"));
    }

    /**
     * Checks that legacy storage dates which do not exist in the calendar are rejected.
     */
    @Test
    public void parseFromStorage_nonExistentCalendarDate_throwsDowntownGurlException() {
        assertThrows(DowntownGurlException.class, () -> TaskDateTime.parseFromStorage("31/2/2026 1800"));
        assertThrows(DowntownGurlException.class, () -> TaskDateTime.parseFromStorage("29/2/2025 1800"));
    }

    /**
     * Checks that date-times are formatted as ISO text for storage.
     */
    @Test
    public void formatForStorage_dateTime_returnsIsoDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2019, 12, 2, 18, 0);

        assertEquals("2019-12-02T18:00:00", TaskDateTime.formatForStorage(dateTime));
    }

    /**
     * Checks that date-times are formatted in the user-facing display format.
     */
    @Test
    public void formatForDisplay_dateTime_returnsUserFriendlyDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2019, 12, 2, 18, 0);

        assertEquals("02 Dec 2019, Monday 18:00", TaskDateTime.formatForDisplay(dateTime));
    }
}
