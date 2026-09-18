package alfred;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Dates}. The refusals are checked by their message, since both
 * throw an {@link AlfredException} and telling them apart is the point.
 */
public class DatesTest {
    @Test
    public void parse_validDate_dateReturned() throws AlfredException {
        assertEquals(LocalDate.of(2019, 10, 15), Dates.parse("2019-10-15"));
    }

    @Test
    public void parse_leapDayInLeapYear_dateReturned() throws AlfredException {
        assertEquals(LocalDate.of(2020, 2, 29), Dates.parse("2020-02-29"));
    }

    @Test
    public void parse_yearBoundaries_dateReturned() throws AlfredException {
        assertEquals(LocalDate.of(2019, 1, 1), Dates.parse("2019-01-01"));
        assertEquals(LocalDate.of(2019, 12, 31), Dates.parse("2019-12-31"));
    }

    @Test
    public void parse_emptyText_formatExceptionThrown() {
        assertFormatRefused("");
    }

    @Test
    public void parse_notADateAtAll_formatExceptionThrown() {
        assertFormatRefused("Sunday");
    }

    @Test
    public void parse_singleDigitMonth_formatExceptionThrown() {
        assertFormatRefused("2019-1-15");
    }

    @Test
    public void parse_singleDigitDay_formatExceptionThrown() {
        assertFormatRefused("2019-10-5");
    }

    @Test
    public void parse_dayFirstOrder_formatExceptionThrown() {
        assertFormatRefused("15-10-2019");
    }

    @Test
    public void parse_slashSeparators_formatExceptionThrown() {
        assertFormatRefused("2019/10/15");
    }

    @Test
    public void parse_noSeparators_formatExceptionThrown() {
        assertFormatRefused("20191015");
    }

    @Test
    public void parse_trailingText_formatExceptionThrown() {
        assertFormatRefused("2019-10-15 please");
    }

    @Test
    public void parse_leapDayInCommonYear_nonexistentDateExceptionThrown() {
        assertNonexistentDateRefused("2019-02-29");
    }

    @Test
    public void parse_dayAfterEndOfMonth_nonexistentDateExceptionThrown() {
        assertNonexistentDateRefused("2019-04-31");
    }

    @Test
    public void parse_monthAboveTwelve_nonexistentDateExceptionThrown() {
        assertNonexistentDateRefused("2019-13-01");
    }

    @Test
    public void parse_zeroMonth_nonexistentDateExceptionThrown() {
        assertNonexistentDateRefused("2019-00-15");
    }

    @Test
    public void parse_zeroDay_nonexistentDateExceptionThrown() {
        assertNonexistentDateRefused("2019-10-00");
    }

    @Test
    public void format_ordinaryDate_monthNamedAndDayPadded() {
        assertEquals("Oct 15 2019", Dates.format(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void format_singleDigitDay_dayPaddedToTwoDigits() {
        assertEquals("Jan 01 2019", Dates.format(LocalDate.of(2019, 1, 1)));
    }

    @Test
    public void format_lastDayOfYear_displayFormReturned() {
        assertEquals("Dec 31 2019", Dates.format(LocalDate.of(2019, 12, 31)));
    }

    @Test
    public void format_leapDay_displayFormReturned() {
        assertEquals("Feb 29 2020", Dates.format(LocalDate.of(2020, 2, 29)));
    }

    @Test
    public void format_nonEnglishDefaultLocale_monthNamedInEnglish() {
        Locale original = Locale.getDefault();
        try {
            // German would name this month Okt, so the assertion below fails
            // if the display format ever stops pinning its locale.
            Locale.setDefault(Locale.GERMANY);
            assertEquals("Oct 15 2019", Dates.format(LocalDate.of(2019, 10, 15)));
        } finally {
            // Restored so that a failure cannot leak a locale into later tests.
            Locale.setDefault(original);
        }
    }

    /**
     * Checks that the text is refused for not being in the accepted format.
     *
     * @param text the date as it was written.
     */
    private void assertFormatRefused(String text) {
        AlfredException e = assertThrows(AlfredException.class, () -> Dates.parse(text));
        assertEquals("I don't know '" + text + "' as a date, sir. Do use yyyy-mm-dd, as in 2019-10-15.",
                e.getMessage());
    }

    /**
     * Checks that the text is refused for naming a day that does not exist,
     * rather than for being in the wrong format.
     *
     * @param text the date as it was written.
     */
    private void assertNonexistentDateRefused(String text) {
        AlfredException e = assertThrows(AlfredException.class, () -> Dates.parse(text));
        assertEquals("There is no such date as '" + text + "', sir. Do check the day and the month.",
                e.getMessage());
    }
}
