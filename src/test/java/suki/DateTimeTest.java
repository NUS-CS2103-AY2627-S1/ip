package suki;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link DateTime}, which is the class most worth testing because it
 * accepts several input formats and has to remember whether a time was given.
 */
public class DateTimeTest {

    @Test
    public void parse_isoDateWithoutTime_printsDateOnly() throws SukiException {
        assertEquals("Oct 15 2019", DateTime.parse("2019-10-15").toString());
    }

    @Test
    public void parse_isoDateWithTime_printsDateAndTime() throws SukiException {
        assertEquals("Oct 15 2019, 6:00PM", DateTime.parse("2019-10-15 1800").toString());
    }

    @Test
    public void parse_slashDateWithoutTime_printsDateOnly() throws SukiException {
        assertEquals("Oct 15 2019", DateTime.parse("15/10/2019").toString());
    }

    @Test
    public void parse_slashDateWithTime_printsDateAndTime() throws SukiException {
        assertEquals("Oct 15 2019, 6:00PM", DateTime.parse("15/10/2019 1800").toString());
    }

    @Test
    public void parse_surroundingWhitespace_ignored() throws SukiException {
        assertEquals("Oct 15 2019", DateTime.parse("  2019-10-15  ").toString());
    }

    @Test
    public void parse_unrecognisedText_throws() {
        SukiException e = assertThrows(SukiException.class, () -> DateTime.parse("next tuesday"));
        assertTrue(e.getMessage().contains("next tuesday"),
                "the message should quote the input that failed");
    }

    @Test
    public void parse_impossibleDate_throws() {
        // 31 February is well-formed but does not exist.
        assertThrows(SukiException.class, () -> DateTime.parse("2019-02-31"));
    }

    @Test
    public void toStorageString_roundTripsThroughParse() throws SukiException {
        // Saving and reloading must not lose the time of day.
        DateTime withTime = DateTime.parse("2019-10-15 1800");
        assertEquals(withTime.toString(), DateTime.parse(withTime.toStorageString()).toString());

        DateTime dateOnly = DateTime.parse("2019-10-15");
        assertEquals(dateOnly.toString(), DateTime.parse(dateOnly.toStorageString()).toString());
    }

    @Test
    public void compareTo_ordersChronologically() throws SukiException {
        DateTime earlier = DateTime.parse("2019-10-15 0900");
        DateTime later = DateTime.parse("2019-10-15 1800");
        assertTrue(earlier.compareTo(later) < 0);
        assertTrue(later.compareTo(earlier) > 0);
        assertEquals(0, earlier.compareTo(DateTime.parse("2019-10-15 0900")));
    }
}
