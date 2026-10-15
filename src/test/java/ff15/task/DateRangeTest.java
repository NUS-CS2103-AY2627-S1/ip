package ff15.task;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import ff15.FF15Exception;

/**
 * Tests how {@link DateRange} decides which span the user asked about, and which
 * dates then fall inside it.
 *
 * <p>The interesting cases are the edges: the first and last day of a span
 * belong to it, and a span that ends where another begins still overlaps it.
 */
public class DateRangeTest {

    @Test
    public void parse_fullDate_spansThatDayOnly() throws FF15Exception {
        DateRange range = DateRange.parse("2019-12-02");
        assertTrue(range.includes(LocalDate.of(2019, 12, 2)));
        assertFalse(range.includes(LocalDate.of(2019, 12, 1)));
        assertFalse(range.includes(LocalDate.of(2019, 12, 3)));
        assertEquals("Dec 02 2019", range.getLabel());
    }

    @Test
    public void parse_yearAndMonth_spansWholeMonth() throws FF15Exception {
        DateRange range = DateRange.parse("2019-12");
        assertTrue(range.includes(LocalDate.of(2019, 12, 1)));
        assertTrue(range.includes(LocalDate.of(2019, 12, 17)));
        assertTrue(range.includes(LocalDate.of(2019, 12, 31)));
        assertFalse(range.includes(LocalDate.of(2019, 11, 30)));
        assertFalse(range.includes(LocalDate.of(2020, 1, 1)));
        assertEquals("Dec 2019", range.getLabel());
    }

    @Test
    public void parse_februaryInLeapYear_endsOnTheTwentyNinth() throws FF15Exception {
        DateRange range = DateRange.parse("2020-02");
        assertTrue(range.includes(LocalDate.of(2020, 2, 29)));
        assertFalse(range.includes(LocalDate.of(2020, 3, 1)));
    }

    @Test
    public void parse_februaryInCommonYear_endsOnTheTwentyEighth() throws FF15Exception {
        DateRange range = DateRange.parse("2019-02");
        assertTrue(range.includes(LocalDate.of(2019, 2, 28)));
        assertFalse(range.includes(LocalDate.of(2019, 3, 1)));
    }

    @Test
    public void parse_yearOnly_spansWholeYear() throws FF15Exception {
        DateRange range = DateRange.parse("2019");
        assertTrue(range.includes(LocalDate.of(2019, 1, 1)));
        assertTrue(range.includes(LocalDate.of(2019, 6, 15)));
        assertTrue(range.includes(LocalDate.of(2019, 12, 31)));
        assertFalse(range.includes(LocalDate.of(2018, 12, 31)));
        assertFalse(range.includes(LocalDate.of(2020, 1, 1)));
        assertEquals("2019", range.getLabel());
    }

    @Test
    public void parse_tooManyParts_throwsException() {
        assertThrows(FF15Exception.class, () -> DateRange.parse("2019-12-02-03"));
    }

    @Test
    public void parse_malformedText_throwsException() {
        assertThrows(FF15Exception.class, () -> DateRange.parse(""));
        assertThrows(FF15Exception.class, () -> DateRange.parse("December"));
        assertThrows(FF15Exception.class, () -> DateRange.parse("2019-13"));
        assertThrows(FF15Exception.class, () -> DateRange.parse("2019-12-32"));
        assertThrows(FF15Exception.class, () -> DateRange.parse("2019-02-30"));
        assertThrows(FF15Exception.class, () -> DateRange.parse("not-a-date"));
    }

    @Test
    public void parse_shortYear_isReadAsThatLiteralYear() {
        // Year.parse accepts fewer than four digits, so "on 19" asks about the year
        // 19 rather than being rejected. Recorded here so a future change to reject
        // short years is a deliberate one.
        DateRange range = assertDoesNotThrow(() -> DateRange.parse("19"));
        assertEquals("19", range.getLabel());
        assertTrue(range.includes(LocalDate.of(19, 6, 1)));
        assertFalse(range.includes(LocalDate.of(2019, 6, 1)));
    }

    @Test
    public void parse_rejectedText_messageQuotesWhatWasTyped() {
        FF15Exception thrown = assertThrows(FF15Exception.class, () -> DateRange.parse("Decemberish"));
        assertTrue(thrown.getMessage().contains("Decemberish"),
                "the message should show the user what was rejected: " + thrown.getMessage());
    }

    @Test
    public void overlaps_spanEndingBeforeTheRange_returnsFalse() throws FF15Exception {
        DateRange december = DateRange.parse("2019-12");
        assertFalse(december.overlaps(LocalDate.of(2019, 11, 1), LocalDate.of(2019, 11, 30)));
    }

    @Test
    public void overlaps_spanStartingAfterTheRange_returnsFalse() throws FF15Exception {
        DateRange december = DateRange.parse("2019-12");
        assertFalse(december.overlaps(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 31)));
    }

    @Test
    public void overlaps_spanEndingOnTheFirstDayOfTheRange_returnsTrue() throws FF15Exception {
        DateRange december = DateRange.parse("2019-12");
        assertTrue(december.overlaps(LocalDate.of(2019, 11, 25), LocalDate.of(2019, 12, 1)));
    }

    @Test
    public void overlaps_spanStartingOnTheLastDayOfTheRange_returnsTrue() throws FF15Exception {
        DateRange december = DateRange.parse("2019-12");
        assertTrue(december.overlaps(LocalDate.of(2019, 12, 31), LocalDate.of(2020, 1, 5)));
    }

    @Test
    public void overlaps_spanSwallowingTheWholeRange_returnsTrue() throws FF15Exception {
        DateRange singleDay = DateRange.parse("2019-12-02");
        assertTrue(singleDay.overlaps(LocalDate.of(2019, 1, 1), LocalDate.of(2020, 12, 31)));
    }

    @Test
    public void overlaps_spanContainedInTheRange_returnsTrue() throws FF15Exception {
        DateRange year = DateRange.parse("2019");
        assertTrue(year.overlaps(LocalDate.of(2019, 6, 1), LocalDate.of(2019, 6, 2)));
    }

    @Test
    public void overlaps_singleDaySpanOnTheRangeDay_returnsTrue() throws FF15Exception {
        DateRange singleDay = DateRange.parse("2019-12-02");
        LocalDate sameDay = LocalDate.of(2019, 12, 2);
        assertTrue(singleDay.overlaps(sameDay, sameDay));
    }

    @Test
    public void overlaps_singleDaySpanOneDayOff_returnsFalse() throws FF15Exception {
        DateRange singleDay = DateRange.parse("2019-12-02");
        LocalDate dayBefore = LocalDate.of(2019, 12, 1);
        LocalDate dayAfter = LocalDate.of(2019, 12, 3);
        assertFalse(singleDay.overlaps(dayBefore, dayBefore));
        assertFalse(singleDay.overlaps(dayAfter, dayAfter));
    }
}
