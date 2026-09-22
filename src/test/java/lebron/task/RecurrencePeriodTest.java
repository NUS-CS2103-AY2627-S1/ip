package lebron.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link RecurrencePeriod}: parsing command/file text, advancing a
 * {@link DateTime} by one period, and formatting back to text.
 */
public class RecurrencePeriodTest {

    // ---- parse -----------------------------------------------------------

    @Test
    public void parse_day_returnsDay() {
        assertEquals(RecurrencePeriod.DAY, RecurrencePeriod.parse("day"));
    }

    @Test
    public void parse_week_returnsWeek() {
        assertEquals(RecurrencePeriod.WEEK, RecurrencePeriod.parse("week"));
    }

    @Test
    public void parse_month_returnsMonth() {
        assertEquals(RecurrencePeriod.MONTH, RecurrencePeriod.parse("month"));
    }

    @Test
    public void parse_unrecognisedText_throws() {
        assertThrows(IllegalArgumentException.class, () -> RecurrencePeriod.parse("fortnight"));
    }

    @Test
    public void parse_wrongCase_throws() {
        // Exact lowercase match only, consistent with the rest of the parser.
        assertThrows(IllegalArgumentException.class, () -> RecurrencePeriod.parse("Week"));
    }

    // ---- advance -----------------------------------------------------

    @Test
    public void advance_day_addsOneDay() {
        assertEquals("Jan 02 2025", RecurrencePeriod.DAY.advance(DateTime.parse("2025-01-01")).toString());
    }

    @Test
    public void advance_week_addsSevenDays() {
        assertEquals("Jan 08 2025", RecurrencePeriod.WEEK.advance(DateTime.parse("2025-01-01")).toString());
    }

    @Test
    public void advance_month_addsOneMonth() {
        assertEquals("Feb 01 2025", RecurrencePeriod.MONTH.advance(DateTime.parse("2025-01-01")).toString());
    }

    @Test
    public void advance_keepsTimeOfDay() {
        assertEquals("Jan 08 2025 9:00am",
                RecurrencePeriod.WEEK.advance(DateTime.parse("2025-01-01 0900")).toString());
    }

    // ---- toFileFormat --------------------------------------------------

    @Test
    public void toFileFormat_roundTripsThroughParse() {
        for (RecurrencePeriod period : RecurrencePeriod.values()) {
            assertEquals(period, RecurrencePeriod.parse(period.toFileFormat()));
        }
    }
}
