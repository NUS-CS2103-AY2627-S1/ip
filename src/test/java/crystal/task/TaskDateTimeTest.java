package crystal.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests Crystal's accepted date/time forms and normalization rules.
 */
public class TaskDateTimeTest {
    /** Verifies normalization of supported numeric date formats and two-digit years. */
    @Test
    public void parse_supportedNumericDates_normalizesDates() {
        assertEquals("15 Oct 2019", TaskDateTime.parse("2019-10-15").toString());
        assertEquals("02 Dec 2019", TaskDateTime.parse("2/12/2019").toString());
        assertEquals("02 Dec 2026", TaskDateTime.parse("2/12/26").toString());
    }

    /** Verifies flexible month-name dates, spacing, case, suffixes, and short years. */
    @Test
    public void parse_supportedMonthNameDates_normalizesSpacingCaseAndYears() {
        assertEquals("02 Oct 2026", TaskDateTime.parse("2Oct2026").toString());
        assertEquals("02 Dec 2026", TaskDateTime.parse("2 Dec2026").toString());
        assertEquals("02 Nov 2026", TaskDateTime.parse("2Nov 26").toString());
        assertEquals("02 Oct 2026", TaskDateTime.parse("2nd-oct-26").toString());
        assertEquals("03 Oct 2026", TaskDateTime.parse("3 October 2026").toString());
    }

    /** Verifies conversion of supported 12-hour times into a 24-hour clock. */
    @Test
    public void parse_supportedTwelveHourTimes_normalizesToTwentyFourHourClock() {
        assertEquals("0600", TaskDateTime.parse("6am").toString());
        assertEquals("0000", TaskDateTime.parse("12am").toString());
        assertEquals("1200", TaskDateTime.parse("12pm").toString());
        assertEquals("1830", TaskDateTime.parse("630pm").toString());
        assertEquals("1830", TaskDateTime.parse("6.30PM").toString());
        assertEquals("1830", TaskDateTime.parse("6:30pm").toString());
    }

    /** Verifies normalization of separated and compact 24-hour times. */
    @Test
    public void parse_supportedTwentyFourHourTimes_normalizesSeparators() {
        assertEquals("1845", TaskDateTime.parse("18:45").toString());
        assertEquals("1845", TaskDateTime.parse("18.45").toString());
        assertEquals("0630", TaskDateTime.parse("630").toString());
        assertEquals("1830", TaskDateTime.parse("1830").toString());
    }

    /** Verifies normalization when a value contains both a date and a time. */
    @Test
    public void parse_dateAndTime_normalizesBothComponents() {
        assertEquals("02 Dec 2026 1830",
                TaskDateTime.parse("2 Dec 2026 630pm").toString());
    }

    /** Verifies that unrestricted text is preserved while a final time is normalized. */
    @Test
    public void parse_textWithFinalTime_preservesTextAndNormalizesTime() {
        assertEquals("Monday 0600", TaskDateTime.parse("Monday 6am").toString());
        assertEquals("team meeting 1830", TaskDateTime.parse("team meeting 18:30").toString());
    }

    /** Verifies that invalid or unrecognized scheduling input remains trimmed text. */
    @Test
    public void parse_unrecognizedOrInvalidInput_preservesTrimmedText() {
        assertEquals("Monday", TaskDateTime.parse("  Monday  ").toString());
        assertEquals("13pm", TaskDateTime.parse("13pm").toString());
        assertEquals("2400", TaskDateTime.parse("2400").toString());
        assertEquals("1260", TaskDateTime.parse("1260").toString());
        assertEquals("31/02/26", TaskDateTime.parse("31/02/26").toString());
    }

    /** Verifies that direct date parsing returns dates for valid input and null otherwise. */
    @Test
    public void parseDate_supportedAndInvalidInputs_returnsDateOrNull() {
        assertEquals(LocalDate.of(2026, 12, 2), TaskDateTime.parseDate(" 2Dec26 "));
        assertEquals(LocalDate.of(2019, 10, 15), TaskDateTime.parseDate("2019-10-15"));
        assertNull(TaskDateTime.parseDate("31/02/26"));
        assertNull(TaskDateTime.parseDate("Monday"));
    }

    /** Verifies date access, occurrence matching, and Crystal's date formatting. */
    @Test
    public void dateAccessors_datedAndTextValues_returnExpectedResults() {
        LocalDate date = LocalDate.of(2026, 12, 2);
        TaskDateTime datedValue = TaskDateTime.parse("2Dec26 0900");
        TaskDateTime textValue = TaskDateTime.parse("Monday");

        assertEquals(date, datedValue.getDate());
        assertTrue(datedValue.occursOn(date));
        assertFalse(datedValue.occursOn(date.plusDays(1)));
        assertNull(textValue.getDate());
        assertFalse(textValue.occursOn(date));
        assertEquals("02 Dec 2026", TaskDateTime.formatDate(date));
    }
}
