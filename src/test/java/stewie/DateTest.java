package stewie;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import stewie.model.Date;

/**
 * Verifies supported date formats and strict calendar validation.
 */
public class DateTest {
    @Test
    public void parseDate_successful() {
        // d/m/yyyy is an accepted format
        assertEquals("02 Aug 2026", new Date("2/8/2026").toString());
    }

    @Test
    public void parseDate_isoFormat() {
        assertEquals("28 Aug 2026", new Date("2026-08-28").toString());
    }

    @Test
    public void parseDate_alternativeNumericFormats() {
        assertAll(
                () -> assertEquals("28 Aug 2026", new Date("28-8-2026").toString()),
                () -> assertEquals("28 Aug 2026", new Date("28.8.2026").toString())
        );
    }

    @Test
    public void parseDate_textualFormatsAreCaseInsensitive() {
        assertAll(
                () -> assertEquals("28 Aug 2026", new Date("28 aug 2026").toString()),
                () -> assertEquals("28 Aug 2026", new Date("28 AUGUST 2026").toString()),
                () -> assertEquals("28 Aug 2026", new Date("aug 28, 2026").toString()),
                () -> assertEquals("28 Aug 2026", new Date("AUGUST 28, 2026").toString())
        );
    }

    @Test
    public void parseDate_trimsWhitespaceAndHandlesLeapDay() {
        assertAll(
                () -> assertEquals("29 Feb 2024", new Date("  29/2/2024  ").toString()),
                () -> assertEquals("01 Jan 2026", new Date("\t1.1.2026\n").toString())
        );
    }

    @Test
    public void parseDate_rejectsInvalidAndUnsupportedInput() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new Date(null)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Date("   ")),
                () -> assertThrows(IllegalArgumentException.class, () -> new Date("31/2/2026")),
                () -> assertThrows(IllegalArgumentException.class, () -> new Date("10am")),
                () -> assertThrows(IllegalArgumentException.class, () -> new Date("not a date"))
        );
    }
    /**
     * Verifies invalid leap days and unsupported years cannot enter storage.
     */
    @Test
    public void parseDate_rejectsInvalidCalendarValues() {
        for (String date : new String[] {"2026-02-30", "2025-02-29", "0000-01-01", "-0001-01-01"}) {
            assertThrows(IllegalArgumentException.class, () -> new Date(date));
        }
    }
}
