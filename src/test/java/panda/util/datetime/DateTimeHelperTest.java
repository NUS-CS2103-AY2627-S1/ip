package panda.util.datetime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;

import org.junit.jupiter.api.Test;

import panda.exception.parser.IllegalDateTimeException;

/**
 * Contains unit tests for {@link DateTimeHelper}.
 */
public class DateTimeHelperTest {

    @Test
    public void parseInput_validDateTime_returnsLocalDateTime() throws IllegalDateTimeException {
        Temporal temporal = DateTimeHelper.parseInput("15/10/2026 18:00");
        assertEquals(LocalDateTime.of(2026, 10, 15, 18, 0), temporal);
    }

    @Test
    public void parseInput_validDateOnly_returnsLocalDate() throws IllegalDateTimeException {
        Temporal temporal = DateTimeHelper.parseInput("15/10/2026");
        assertEquals(LocalDate.of(2026, 10, 15), temporal);
    }

    @Test
    public void parseInput_invalidInput_throwsIllegalDateTimeException() {
        assertThrows(IllegalDateTimeException.class, () -> DateTimeHelper.parseInput(null));
        assertThrows(IllegalDateTimeException.class, () -> DateTimeHelper.parseInput("   "));
        assertThrows(IllegalDateTimeException.class, () -> DateTimeHelper.parseInput("32/1/2026"));
        assertThrows(IllegalDateTimeException.class, () -> DateTimeHelper.parseInput("invalid-date"));
    }

    @Test
    public void formatForDisplay_localDateTime_returnsFormattedString() {
        LocalDateTime dateTime = LocalDateTime.of(2019, 12, 2, 18, 0);
        assertEquals("2 Dec 2019 18:00", DateTimeHelper.formatForDisplay(dateTime));
    }

    @Test
    public void formatForDisplay_localDate_returnsFormattedString() {
        LocalDate date = LocalDate.of(2019, 12, 2);
        assertEquals("2 Dec 2019", DateTimeHelper.formatForDisplay(date));
    }

    @Test
    public void formatForDisplay_nullInput_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DateTimeHelper.formatForDisplay(null));
    }

    @Test
    public void formatForStorage_localDateTime_returnsStorageFormat() {
        LocalDateTime dateTime = LocalDateTime.of(2019, 12, 2, 18, 0);
        assertEquals("2019-12-02 18:00", DateTimeHelper.formatForStorage(dateTime));
    }

    @Test
    public void formatForStorage_localDate_returnsStorageFormat() {
        LocalDate date = LocalDate.of(2019, 12, 2);
        assertEquals("2019-12-02", DateTimeHelper.formatForStorage(date));
    }

    @Test
    public void parseStorage_validStorageFormats_returnsTemporal() throws IllegalDateTimeException {
        Temporal dateTime = DateTimeHelper.parseStorage("2019-12-02 18:00");
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), dateTime);

        Temporal dateOnly = DateTimeHelper.parseStorage("2019-12-02");
        assertEquals(LocalDate.of(2019, 12, 2), dateOnly);
    }

    @Test
    public void parseStorage_legacyInputFormat_returnsTemporal() throws IllegalDateTimeException {
        Temporal fallback = DateTimeHelper.parseStorage("2/12/2019");
        assertEquals(LocalDate.of(2019, 12, 2), fallback);
    }

    @Test
    public void parseStorage_invalidValue_throwsIllegalDateTimeException() {
        assertThrows(IllegalDateTimeException.class, () -> DateTimeHelper.parseStorage(null));
        assertThrows(IllegalDateTimeException.class, () -> DateTimeHelper.parseStorage("   "));
        assertThrows(IllegalDateTimeException.class, () -> DateTimeHelper.parseStorage("invalid-date"));
    }
}
