package amadeus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import amadeus.AmadeusException;

/**
 * Unit tests for {@link TaskDateTime#parse(String)}.
 * <p>
 * parse() tries several formats in turn and relies on STRICT resolving to reject
 * impossible dates, so it is the most bug-prone method in the app.
 */
public class TaskDateTimeTest {

    /**
     * The date-with-time formats are tried before the date-only ones, so a
     * trailing time must be read as a time rather than making the parse fail.
     */
    @Test
    void parse_dateWithTime_keepsTime() throws AmadeusException {
        assertEquals("2019-12-02 1800", TaskDateTime.parse("2/12/2019 1800").toStorageString());
    }

    /**
     * STRICT resolving is what makes this fail. With Java's default resolver a
     * 31st of February is quietly moved to the 28th, which would store the wrong
     * date instead of telling the user they mistyped.
     */
    @Test
    void parse_impossibleDayOfMonth_throwsAmadeusException() {
        assertThrows(AmadeusException.class, () -> TaskDateTime.parse("2019-02-31"));
    }
}
