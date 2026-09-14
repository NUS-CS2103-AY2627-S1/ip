package penguin.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import penguin.exception.PenguinException;

/**
 * Tests strict date/time parsing and display formatting.
 */
class DateTimeUtilTest {
    @Test
    void parseDate_validDate_returnsExpectedLocalDate()
            throws PenguinException {
        LocalDate actual = DateTimeUtil.parseDate("2027-12-31");

        assertEquals(LocalDate.of(2027, 12, 31), actual);
    }

    @Test
    void parseDate_leapDay_returnsExpectedLocalDate()
            throws PenguinException {
        LocalDate actual = DateTimeUtil.parseDate("2028-02-29");

        assertEquals(LocalDate.of(2028, 2, 29), actual);
    }

    @Test
    void parseDate_surroundingWhitespace_returnsExpectedLocalDate()
            throws PenguinException {
        LocalDate actual = DateTimeUtil.parseDate(" 2027-12-31 ");

        assertEquals(LocalDate.of(2027, 12, 31), actual);
    }

    @Test
    void parseDate_invalidFormat_throwsException() {
        assertThrows(PenguinException.class,
                () -> DateTimeUtil.parseDate("31-12-2027"));
    }

    @Test
    void parseDate_nullInput_throwsPenguinException() {
        assertThrows(PenguinException.class,
                () -> DateTimeUtil.parseDate(null));
    }

    @Test
    void parseDate_outOfRangeMonth_throwsException() {
        assertThrows(PenguinException.class,
                () -> DateTimeUtil.parseDate("2027-13-31"));
    }

    @Test
    void parseDate_outOfRangeDay_throwsException() {
        assertThrows(PenguinException.class,
                () -> DateTimeUtil.parseDate("2027-04-31"));
    }

    @Test
    void parseDate_nonLeapYearFebruary29_throwsException() {
        assertThrows(PenguinException.class,
                () -> DateTimeUtil.parseDate("2027-02-29"));
    }

    @Test
    void parseDateTime_validDateTime_returnsExpectedLocalDateTime()
            throws PenguinException {
        assertEquals(LocalDateTime.of(2099, 12, 31, 18, 0),
                DateTimeUtil.parseDateTime("2099-12-31 1800"));
    }

    @Test
    void parseDateTime_invalidDate_throwsException() {
        assertThrows(PenguinException.class,
                () -> DateTimeUtil.parseDateTime("2018-13-05 1800"));
    }

    @Test
    void parseDateTime_invalidTime_throwsException() {
        assertThrows(PenguinException.class,
                () -> DateTimeUtil.parseDateTime("2099-12-31 2500"));
    }

    @Test
    void parseDateTime_nullInput_throwsPenguinException() {
        assertThrows(PenguinException.class,
                () -> DateTimeUtil.parseDateTime(null));
    }

    @Test
    void formatForStorage_validDateTime_roundTrips()
            throws PenguinException {
        LocalDateTime original = LocalDateTime.of(2099, 12, 31, 18, 0);

        assertEquals(original,
                DateTimeUtil.parseDateTime(DateTimeUtil.formatForStorage(original)));
    }

    @Test
    void formatForDisplay_validDateTime_returnsUserFriendlyDateTime() {
        assertEquals("31 Dec 2099, 6:00PM",
                DateTimeUtil.formatForDisplay(LocalDateTime.of(2099, 12, 31, 18, 0)));
    }
}
