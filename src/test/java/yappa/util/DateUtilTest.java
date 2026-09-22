package yappa.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import yappa.exception.YappaException;

/**
 * Tests conversion between user-facing, display, and storage date-time formats.
 */
public class DateUtilTest {

    private static final LocalDateTime SAMPLE_DATE_TIME = LocalDateTime.of(2026, 12, 2, 18, 5);

    /**
     * Verifies that valid user input is trimmed and parsed.
     *
     * @throws YappaException If parsing unexpectedly fails.
     */
    @Test
    public void parseDateTime_validInputWithWhitespace_returnsDateTime() throws YappaException {
        assertEquals(SAMPLE_DATE_TIME, DateUtil.parseDateTime(" 02/12/2026 1805 "));
    }

    /**
     * Verifies that malformed user date-time values are rejected.
     */
    @Test
    public void parseDateTime_invalidFormatOrValue_throwsYappaException() {
        assertThrows(YappaException.class, () -> DateUtil.parseDateTime("2026-12-02 18:05"));
        assertThrows(YappaException.class, () -> DateUtil.parseDateTime("not a date"));
    }

    /**
     * Verifies that storage formatting and parsing preserve a date-time.
     *
     * @throws YappaException If parsing unexpectedly fails.
     */
    @Test
    public void storageFormat_roundTrip_preservesDateTime() throws YappaException {
        String storedDateTime = DateUtil.toFileString(SAMPLE_DATE_TIME);

        assertEquals("2026-12-02T18:05", storedDateTime);
        assertEquals(SAMPLE_DATE_TIME, DateUtil.parseStorageDateTime(storedDateTime));
    }

    /**
     * Verifies that a user-formatted date-time is rejected as a storage value.
     */
    @Test
    public void parseStorageDateTime_invalidValue_throwsYappaException() {
        assertThrows(YappaException.class,
                () -> DateUtil.parseStorageDateTime("02/12/2026 1805"));
    }

    /**
     * Verifies the user-facing date-time format.
     */
    @Test
    public void toDisplayString_dateTime_returnsReadableFormat() {
        assertEquals("Dec 02 2026, 6:05 PM", DateUtil.toDisplayString(SAMPLE_DATE_TIME));
    }
}
