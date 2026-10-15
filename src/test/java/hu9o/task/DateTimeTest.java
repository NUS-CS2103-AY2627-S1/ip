package hu9o.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link DateTime}: the two accepted input forms (whole hour, and hour with
 * minutes), strict rejection of impossible dates, and the storage and display
 * output forms.
 */
public class DateTimeTest {

    @Test
    public void parseString_wholeHour_parsesTime() {
        assertEquals(LocalDateTime.of(2026, 9, 20, 18, 0), DateTime.parseString("20/9/26 6 PM"));
    }

    @Test
    public void parseString_hourWithMinutes_parsesTime() {
        assertEquals(LocalDateTime.of(2026, 9, 20, 18, 30), DateTime.parseString("20/9/26 630 PM"));
    }

    @Test
    public void parseString_lowerCaseMeridiem_stillParses() {
        assertEquals(LocalDateTime.of(2026, 8, 12, 15, 30), DateTime.parseString("12/8/26 330 pm"));
    }

    @Test
    public void parseString_noonAndMidnight_mapToTwelveAndZeroHundred() {
        assertEquals(LocalDateTime.of(2026, 9, 20, 12, 0), DateTime.parseString("20/9/26 12 PM"));
        assertEquals(LocalDateTime.of(2026, 9, 20, 0, 0), DateTime.parseString("20/9/26 12 AM"));
        assertEquals(LocalDateTime.of(2026, 9, 20, 0, 30), DateTime.parseString("20/9/26 1230 AM"));
    }

    @Test
    public void parseString_nonexistentDay_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTime.parseString("31/2/26 6 PM"));
        assertThrows(DateTimeParseException.class, () -> DateTime.parseString("31/4/26 6 PM"));
    }

    @Test
    public void parseString_februaryTwentyNinth_acceptedOnlyInLeapYears() {
        assertThrows(DateTimeParseException.class, () -> DateTime.parseString("29/2/26 6 PM"));
        assertEquals(LocalDateTime.of(2028, 2, 29, 18, 0), DateTime.parseString("29/2/28 6 PM"));
    }

    @Test
    public void parseString_hourOutOfRange_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTime.parseString("20/9/26 13 PM"));
        assertThrows(DateTimeParseException.class, () -> DateTime.parseString("20/9/26 660 PM"));
    }

    @Test
    public void formatForStorage_thenParseString_roundTripsBothForms() {
        LocalDateTime onTheHour = LocalDateTime.of(2026, 9, 20, 18, 0);
        LocalDateTime withMinutes = LocalDateTime.of(2026, 9, 20, 18, 30);

        assertEquals("20/09/26 6 PM", DateTime.formatForStorage(onTheHour));
        assertEquals("20/09/26 630 PM", DateTime.formatForStorage(withMinutes));
        assertEquals(onTheHour, DateTime.parseString(DateTime.formatForStorage(onTheHour)));
        assertEquals(withMinutes, DateTime.parseString(DateTime.formatForStorage(withMinutes)));
    }

    @Test
    public void formatForDisplay_showsYearAndReadableMinutes() {
        assertEquals("20 Sep 2026, 6 PM", DateTime.formatForDisplay(LocalDateTime.of(2026, 9, 20, 18, 0)));
        assertEquals("20 Sep 2027, 6:30 PM", DateTime.formatForDisplay(LocalDateTime.of(2027, 9, 20, 18, 30)));
    }
}
