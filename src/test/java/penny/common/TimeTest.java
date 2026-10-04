package penny.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

/**
 * Unit test suite for {@link Time}.
 * Verifies parsing, formatting, date matching, and exception handling across date and time formats.
 */
public class TimeTest {

    @Test
    public void constructor_isoDateTimeFormat_parsesSuccessfully() throws PennyException {
        Time time = new Time("2019-10-15 1800");
        assertEquals(LocalDate.of(2019, 10, 15), time.getDate());
        assertTrue(time.hasTime());
        assertEquals(LocalTime.of(18, 0), time.getTime());

        Time timeWithColon = new Time("2019-10-15 18:00");
        assertEquals(LocalTime.of(18, 0), timeWithColon.getTime());
    }

    @Test
    public void constructor_slashDateTimeFormat_parsesSuccessfully() throws PennyException {
        Time time = new Time("15/10/2019 1800");
        assertEquals(LocalDate.of(2019, 10, 15), time.getDate());
        assertEquals(LocalTime.of(18, 0), time.getTime());

        Time singleDigitDate = new Time("2/12/2019 18:30");
        assertEquals(LocalDate.of(2019, 12, 2), singleDigitDate.getDate());
        assertEquals(LocalTime.of(18, 30), singleDigitDate.getTime());
    }

    @Test
    public void constructor_isoDateFormat_parsesSuccessfully() throws PennyException {
        Time time = new Time("2019-10-15");
        assertEquals(LocalDate.of(2019, 10, 15), time.getDate());
        assertFalse(time.hasTime());
        assertNull(time.getTime());
    }

    @Test
    public void constructor_slashDateFormat_parsesSuccessfully() throws PennyException {
        Time time = new Time("15/10/2019");
        assertEquals(LocalDate.of(2019, 10, 15), time.getDate());
        assertFalse(time.hasTime());
    }

    @Test
    public void constructor_invalidFormat_throwsPennyException() {
        assertThrows(PennyException.class, () -> {
            new Time("yesterday");
        });

        assertThrows(PennyException.class, () -> {
            new Time("2019/10/15");
        });
    }

    @Test
    public void constructor_impossibleCalendarDate_throwsPennyException() {
        assertThrows(PennyException.class, () -> {
            new Time("2023-99-99");
        });

        assertThrows(PennyException.class, () -> {
            new Time("2023-13-01");
        });
    }

    @Test
    public void parseDate_validDateOnly_returnsLocalDate() throws PennyException {
        LocalDate date = Time.parseDate("2019-10-15");
        assertEquals(LocalDate.of(2019, 10, 15), date);

        LocalDate slashDate = Time.parseDate("15/10/2019");
        assertEquals(LocalDate.of(2019, 10, 15), slashDate);
    }

    @Test
    public void parseDate_dateTimeString_returnsLocalDatePart() throws PennyException {
        LocalDate date = Time.parseDate("2019-10-15 1800");
        assertEquals(LocalDate.of(2019, 10, 15), date);
    }

    @Test
    public void parseDate_invalidDate_throwsPennyException() {
        assertThrows(PennyException.class, () -> {
            Time.parseDate("invalid-date");
        });
    }

    @Test
    public void formatDateForDisplay_validDate_returnsDisplayString() {
        String formatted = Time.formatDateForDisplay(LocalDate.of(2019, 10, 15));
        assertEquals("Oct 15 2019", formatted);
    }

    @Test
    public void isOnDate_matchingAndNonMatchingDate_returnsExpectedBoolean() throws PennyException {
        Time time = new Time("2019-10-15 1800");
        assertTrue(time.isOnDate(LocalDate.of(2019, 10, 15)));
        assertFalse(time.isOnDate(LocalDate.of(2019, 10, 16)));
    }

    @Test
    public void toFileFormat_withAndWithoutTime_returnsExpectedStorageString() throws PennyException {
        Time timeWithTime = new Time("2019-10-15 1800");
        assertEquals("2019-10-15 1800", timeWithTime.toFileFormat());

        Time timeDateOnly = new Time("2019-10-15");
        assertEquals("2019-10-15", timeDateOnly.toFileFormat());
    }

    @Test
    public void toString_withAndWithoutTime_returnsExpectedDisplayString() throws PennyException {
        Time timeWithTime = new Time("2019-10-15 1800");
        assertEquals("Oct 15 2019, 6:00pm", timeWithTime.toString());

        Time timeDateOnly = new Time("2019-10-15");
        assertEquals("Oct 15 2019", timeDateOnly.toString());
    }
}
