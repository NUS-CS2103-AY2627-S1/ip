package seedu.goat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests the conversion between the stored ISO date form and the displayed form.
 */
public class DateFormatsTest {

    @Test
    public void parse_isoDate_dateReturned() throws GoatException {
        assertEquals(LocalDate.of(2019, 10, 15), DateFormats.parse("2019-10-15"));
    }

    @Test
    public void parse_notADate_exceptionThrown() {
        GoatException e = assertThrows(GoatException.class, () -> DateFormats.parse("Sunday"));
        assertTrue(e.getMessage().contains("yyyy-mm-dd"),
                "the message should tell the user which format to use");
    }

    @Test
    public void parse_impossibleDate_exceptionThrown() {
        // Well-formed but not a real date: month 13 and day 45 do not exist.
        assertThrows(GoatException.class, () -> DateFormats.parse("2019-13-45"));
    }

    @Test
    public void parse_wrongOrderOfFields_exceptionThrown() {
        assertThrows(GoatException.class, () -> DateFormats.parse("15-10-2019"));
    }

    @Test
    public void parse_emptyText_exceptionThrown() {
        assertThrows(GoatException.class, () -> DateFormats.parse(""));
    }

    @Test
    public void format_date_displayForm() {
        assertEquals("Oct 15 2019", DateFormats.format(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void format_singleDigitDay_paddedToTwoDigits() {
        assertEquals("Jan 05 2020", DateFormats.format(LocalDate.of(2020, 1, 5)));
    }

    @Test
    public void parseThenFormat_roundTrip_preservesTheDate() throws GoatException {
        LocalDate parsed = DateFormats.parse("2019-12-02");
        assertEquals("Dec 02 2019", DateFormats.format(parsed));
        // The stored form is what parse() reads, so it must survive a round trip.
        assertEquals(parsed, DateFormats.parse(parsed.toString()));
    }
    @Test
    public void parse_leapYearBoundaries_strictValidation() throws GoatException {
        assertEquals(LocalDate.of(2024, 2, 29), DateFormats.parse("2024-02-29"));
        for (String date : new String[]{"2026-02-29", "2026-04-31", "2026-13-01", "2026-00-01",
                "2026-01-00", "2026-9-18", "18/09/2026", "2026-09-18T12:00"}) {
            assertThrows(GoatException.class, () -> DateFormats.parse(date), date);
        }
    }
}
