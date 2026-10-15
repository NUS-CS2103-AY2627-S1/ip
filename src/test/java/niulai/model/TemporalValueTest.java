package niulai.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests strict recognition and preservation of temporal values. */
class TemporalValueTest {
    /** Verifies that absent temporal values are rejected before parsing. */
    @Test
    void parse_nullOrBlankValue_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> TemporalValue.parse(null));
        assertThrows(IllegalArgumentException.class, () -> TemporalValue.parse(" \t "));
    }

    @Test
    void parse_supportedFullDate_returnsDate() {
        TemporalValue value = TemporalValue.parse("2026-09-10");

        assertEquals(LocalDate.of(2026, 9, 10), value.getDate());
        assertEquals("2026-09-10", value.toString());
    }

    /** Verifies that every supported numeric date-time shape exposes its calendar date. */
    @Test
    void parse_supportedNumericDateTimes_returnsDate() {
        TemporalValue slashDateTime = TemporalValue.parse("2/12/2019 1800");
        TemporalValue isoDateTime = TemporalValue.parse("2019-12-02 18:30");

        assertEquals(LocalDate.of(2019, 12, 2), slashDateTime.getDate());
        assertEquals(LocalDate.of(2019, 12, 2), isoDateTime.getDate());
    }

    /** Verifies that 12-hour and 24-hour clock values are interpreted consistently. */
    @Test
    void parse_supportedClockValues_comparesChronologically() {
        assertTrue(isAfter("12am", "12pm"));
        assertTrue(isAfter("1:05pm", "13:06"));
        assertTrue(isAfter("09:30", "2359"));
        assertFalse(isAfter("11am", "10:59am"));
    }

    /** Verifies month-name parsing across every supported month abbreviation. */
    @Test
    void parse_supportedMonthNames_comparesChronologically() {
        assertTrue(isAfter("Jan 1st", "Feb 1st"));
        assertTrue(isAfter("Feb 1st", "Mar 1st"));
        assertTrue(isAfter("Mar 1st", "Apr 1st"));
        assertTrue(isAfter("Apr 1st", "May 1st"));
        assertTrue(isAfter("May 1st", "Jun 1st"));
        assertTrue(isAfter("Jun 1st", "Jul 1st"));
        assertTrue(isAfter("Jul 1st", "Aug 1st"));
        assertTrue(isAfter("Aug 1st", "Sep 1st"));
        assertTrue(isAfter("Sep 1st", "Oct 1st"));
        assertTrue(isAfter("Oct 1st", "Nov 1st"));
        assertTrue(isAfter("Nov 1st", "Dec 1st"));
    }

    /** Verifies that month-name values support optional years and clock times. */
    @Test
    void parse_monthDayVariants_classifiesComparableValues() {
        TemporalValue fullDate = TemporalValue.parse("February 2nd 2026");
        TemporalValue fullDateTime = TemporalValue.parse("February 2nd 2026 9am");

        assertEquals(LocalDate.of(2026, 2, 2), fullDate.getDate());
        assertEquals(LocalDate.of(2026, 2, 2), fullDateTime.getDate());
        assertTrue(isAfter("February 2nd 9am", "February 2nd 10am"));
        assertTrue(isAfter("February 2nd 10am", "February 3rd 9am"));
        assertFalse(isAfter("February 2nd 10am", "February 2nd 10am"));
        assertFalse(isAfter("February 3rd 9am", "February 2nd 10am"));
    }

    @Test
    void parse_impossibleRecognizableDate_exceptionThrown() {
        assertThrows(IllegalArgumentException.class,
                () -> TemporalValue.parse("2026-02-30"));
        assertThrows(IllegalArgumentException.class,
                () -> TemporalValue.parse("30/2/2026"));
        assertThrows(IllegalArgumentException.class,
                () -> TemporalValue.parse("Feb 30"));
        assertThrows(IllegalArgumentException.class,
                () -> TemporalValue.parse("Feb 29 2025"));
    }

    @Test
    void parse_impossibleRecognizableTime_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> TemporalValue.parse("24:30"));
        assertThrows(IllegalArgumentException.class, () -> TemporalValue.parse("13pm"));
        assertThrows(IllegalArgumentException.class, () -> TemporalValue.parse("12:60am"));
    }

    /** Verifies that ordinal suffixes agree with the numeric day. */
    @Test
    void parse_incorrectOrdinalSuffix_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> TemporalValue.parse("Jan 1nd"));
        assertThrows(IllegalArgumentException.class, () -> TemporalValue.parse("Jan 2rd"));
        assertThrows(IllegalArgumentException.class, () -> TemporalValue.parse("Jan 3th"));
        assertThrows(IllegalArgumentException.class, () -> TemporalValue.parse("Jan 4st"));
        assertThrows(IllegalArgumentException.class, () -> TemporalValue.parse("Jan 11st"));
    }

    /** Verifies all supported comparison categories and mixed-category fallback behavior. */
    @Test
    void isEndStrictlyAfter_supportedCategories_returnsExpectedOrdering() {
        assertTrue(isAfter("2026-01-01", "2026-01-02"));
        assertFalse(isAfter("2026-01-02", "2026-01-02"));
        assertTrue(isAfter("2026-01-01 1000", "2026-01-01 1100"));
        assertFalse(isAfter("2026-01-01 1100", "2026-01-01 1000"));
        assertTrue(isAfter("2026-01-01 1000", "11am"));
        assertFalse(isAfter("2026-01-01 1000", "9am"));
        assertTrue(isAfter("Jan 1 10am", "11am"));
        assertFalse(isAfter("Jan 1 10am", "9am"));
        assertTrue(isAfter("2026-01-01", "11am"));
    }

    /** Verifies that comparison rejects missing operands. */
    @Test
    void isEndStrictlyAfter_nullOperand_exceptionThrown() {
        TemporalValue value = TemporalValue.parse("10am");

        assertThrows(NullPointerException.class,
                () -> TemporalValue.isEndStrictlyAfter(null, value));
        assertThrows(NullPointerException.class,
                () -> TemporalValue.isEndStrictlyAfter(value, null));
    }

    @Test
    void parse_freeFormValue_preservesTextWithoutComparableDate() {
        TemporalValue value = TemporalValue.parse("tomorrow morning");

        assertEquals("tomorrow morning", value.toString());
        assertNull(value.getDate());
        assertTrue(isAfter("Jan 1 breakfast", "unrelated free text"));
    }

    /** Returns whether the second parsed value is strictly after the first. */
    private static boolean isAfter(String start, String end) {
        return TemporalValue.isEndStrictlyAfter(
                TemporalValue.parse(start), TemporalValue.parse(end));
    }
}
