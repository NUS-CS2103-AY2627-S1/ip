package piplupbot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import piplupbot.PiplupBotException;

/**
 * Tests {@link DateTimes}, which is the one place that knows how a date is
 * written in each of the three places the program writes one.
 *
 * <p>This class is tested rather than the rest of the bot because its three
 * public methods are pure functions: a value goes in, and a value -- or a
 * {@link PiplupBotException} -- comes out. Nothing is printed, read from disk
 * or remembered between calls, so a test needs no task list, no save file and
 * no stand-in for the user, which is what makes the failures reported here easy
 * to place.</p>
 *
 * <p>The three methods are tested together because they are three halves of one
 * promise, and it is the relationships between them that matter most: what
 * {@link DateTimes#toFileString} writes, {@link DateTimes#parse} must read back
 * as the same moment, while {@link DateTimes#format} is deliberately allowed to
 * differ from both because it is written for a person rather than for the
 * program.</p>
 *
 * <p>Cases are grouped by what they check rather than by layout. The refusals
 * matter as much as the successes: a lenient formatter would turn
 * {@code 31/2/2019} into 28 February and store a date the user never typed.</p>
 */
public class DateTimesTest {

    // ================= parse: reading a date the user or the save file gives =============

    // ---------- The layouts a user may type, and the one the save file holds ----------

    /**
     * The form {@link DateTimes#toFileString} writes, which {@code parse} must
     * accept so that a saved file can be read back.
     */
    @Test
    public void parse_isoDateTimeFromSaveFile_returnsSameDateTime() throws PiplupBotException {
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0), DateTimes.parse("2019-10-15T18:00"));
    }

    @Test
    public void parse_dashDateWithCompactTime_returnsDateTime() throws PiplupBotException {
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0), DateTimes.parse("2019-10-15 1800"));
    }

    @Test
    public void parse_dashDateWithColonTime_returnsDateTime() throws PiplupBotException {
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0), DateTimes.parse("2019-10-15 18:00"));
    }

    /** The day comes first in the slash layout, so this is 2 December, not 12 February. */
    @Test
    public void parse_slashDateWithCompactTime_returnsDateTime() throws PiplupBotException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), DateTimes.parse("2/12/2019 1800"));
    }

    @Test
    public void parse_slashDateWithColonTime_returnsDateTime() throws PiplupBotException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), DateTimes.parse("2/12/2019 18:00"));
    }

    /** A padded day or month is still accepted, since people write dates both ways. */
    @Test
    public void parse_slashDateWithLeadingZeros_returnsDateTime() throws PiplupBotException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), DateTimes.parse("02/12/2019 1800"));
    }

    /** Midnight and noon are where a 12-hour reading of the clock would go wrong. */
    @Test
    public void parse_midnightAndNoon_returnsDateTime() throws PiplupBotException {
        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0), DateTimes.parse("2019-10-15 0000"));
        assertEquals(LocalDateTime.of(2019, 10, 15, 12, 0), DateTimes.parse("2019-10-15 1200"));
    }

    // ---------- A date with no time of day ----------

    /**
     * A deadline is often a whole day, so a date on its own is taken to mean the
     * start of that day. The user is never shown that choice being made for
     * them, which is why it is pinned down here.
     */
    @Test
    public void parse_isoDateOnly_returnsMidnight() throws PiplupBotException {
        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0), DateTimes.parse("2019-10-15"));
    }

    @Test
    public void parse_slashDateOnly_returnsMidnight() throws PiplupBotException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), DateTimes.parse("2/12/2019"));
    }

    // ---------- Spaces around and inside the date ----------

    /**
     * The text arrives from a split on {@code /by} or {@code /to}, so it may
     * still carry spaces the user typed around it.
     */
    @Test
    public void parse_surroundingWhitespace_returnsDateTime() throws PiplupBotException {
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0),
                DateTimes.parse("   2019-10-15 1800   "));
    }

    /**
     * Every layout has one space between the day and the time, so without the
     * step that squeezes spaces together, a second one typed by accident would
     * make a good date unreadable. Both layouts are tried, as each has its own
     * pattern.
     */
    @Test
    public void parse_severalSpacesBeforeTime_returnsDateTime() throws PiplupBotException {
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0), DateTimes.parse("2019-10-15   1800"));
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), DateTimes.parse("2/12/2019  18:00"));
    }

    /**
     * The spaces are squeezed only to read the date; a date that still cannot
     * be read is quoted back as it was typed, so the user recognizes it.
     */
    @Test
    public void parse_severalSpacesInUnreadableDate_messageQuotesTheTypedText() {
        PiplupBotException exception =
                assertThrows(PiplupBotException.class, () -> DateTimes.parse("2019-10-15  2500"));
        assertEquals("Pip... I don't understand the date \"2019-10-15  2500\".",
                exception.getMessageLines()[0]);
    }

    // ---------- Dates that do not exist on the calendar ----------

    @Test
    public void parse_leapDayInLeapYear_returnsDateTime() throws PiplupBotException {
        assertEquals(LocalDateTime.of(2020, 2, 29, 0, 0), DateTimes.parse("29/2/2020"));
    }

    /** 2019 has no 29 February, so this must be refused rather than shifted to the 28th. */
    @Test
    public void parse_leapDayInNonLeapYear_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> DateTimes.parse("29/2/2019"));
    }

    /** February never has 31 days, in any year. */
    @Test
    public void parse_dayOutsideMonth_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> DateTimes.parse("31/2/2019 1800"));
    }

    @Test
    public void parse_monthOutOfRange_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> DateTimes.parse("2019-13-01 1800"));
    }

    // ---------- Times that do not exist on the clock ----------

    @Test
    public void parse_hourOutOfRange_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> DateTimes.parse("2019-10-15 2500"));
    }

    @Test
    public void parse_minuteOutOfRange_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> DateTimes.parse("2019-10-15 1860"));
    }

    // ---------- Text that names no layout the bot accepts ----------

    /**
     * The dashed layout wants a two-digit month and day, so a date written
     * without the padding is refused. Recording that here means a later decision
     * to accept {@code 2019-1-5} shows up as this test failing, rather than as a
     * silent change in what the bot takes.
     */
    @Test
    public void parse_dashDateWithoutLeadingZeros_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> DateTimes.parse("2019-1-5 1800"));
    }

    /** Month-first with dashes is a layout the bot does not offer. */
    @Test
    public void parse_monthFirstDashDate_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> DateTimes.parse("10-15-2019 1800"));
    }

    /** The bot does not read dates written in words, however common they are. */
    @Test
    public void parse_wordDate_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> DateTimes.parse("tomorrow"));
    }

    /**
     * An empty {@code /by} part is caught by {@link Parser} first, but a date is
     * still refused here rather than becoming some default moment in time.
     */
    @Test
    public void parse_emptyText_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> DateTimes.parse(""));
    }

    @Test
    public void parse_blankText_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> DateTimes.parse("     "));
    }

    // ---------- What the user is told when the date cannot be read ----------

    /**
     * The refusal has to be usable: it quotes the text that was rejected, so the
     * user can see what the bot actually received, and follows it with the hint
     * listing the layouts that would have worked.
     */
    @Test
    public void parse_unreadableDate_messageQuotesTextAndGivesHint() {
        PiplupBotException exception =
                assertThrows(PiplupBotException.class, () -> DateTimes.parse("  next Friday  "));

        String[] lines = exception.getMessageLines();
        assertEquals(2, lines.length);
        // The quoted text is trimmed, so the user is shown the date without the
        // spaces that surrounded it in the command they typed.
        assertTrue(lines[0].contains("\"next Friday\""),
                "The first line should quote the rejected date, but was: " + lines[0]);
        assertEquals(DateTimes.FORMAT_HINT, lines[1]);
    }

    // ---------- Saving and loading are the exact reverse of each other ----------

    /**
     * Checks the promise the two methods make together: whatever
     * {@link DateTimes#toFileString} writes into the save file, {@code parse}
     * reads back as the same moment. If that did not hold, every task with a
     * date would come back wrong -- or not at all -- the next time the bot
     * started.
     */
    @Test
    public void parse_textFromToFileString_returnsSameDateTime() throws PiplupBotException {
        LocalDateTime original = LocalDateTime.of(2019, 10, 15, 18, 0);
        assertEquals(original, DateTimes.parse(DateTimes.toFileString(original)));
    }

    // ================= format: writing a date for the user to read ======================

    /**
     * The everyday case, as the task list shows it. The hour is padded to two
     * digits and given on a 12-hour clock, so 18:00 reads as {@code 06:00 PM}.
     */
    @Test
    public void format_eveningTime_returnsPaddedTwelveHourClock() {
        assertEquals("Oct 15 2019 06:00 PM", DateTimes.format(LocalDateTime.of(2019, 10, 15, 18, 0)));
    }

    /**
     * The day is <em>not</em> padded, unlike the hour, so the 2nd reads as
     * {@code Oct 2} rather than {@code Oct 02}. The two are formatted
     * differently on purpose, which is easy to change by accident.
     */
    @Test
    public void format_singleDigitDay_returnsDayWithoutPadding() {
        assertEquals("Oct 2 2019 02:00 PM", DateTimes.format(LocalDateTime.of(2019, 10, 2, 14, 0)));
    }

    /**
     * Midnight is the boundary a 12-hour clock is most likely to get wrong: the
     * hour is written 12, not 00, and the marker is AM. This is also the time
     * every date-only deadline is stored at, so it is shown often.
     */
    @Test
    public void format_midnight_returnsTwelveAm() {
        assertEquals("Dec 1 2019 12:00 AM", DateTimes.format(LocalDateTime.of(2019, 12, 1, 0, 0)));
    }

    /** Noon is the other boundary: also written 12, but PM. */
    @Test
    public void format_noon_returnsTwelvePm() {
        assertEquals("Oct 15 2019 12:00 PM", DateTimes.format(LocalDateTime.of(2019, 10, 15, 12, 0)));
    }

    /** The last minute of the morning and the last minute of the day, either side of 12. */
    @Test
    public void format_minutesEitherSideOfTwelve_returnsCorrectMarker() {
        assertEquals("Oct 15 2019 11:59 AM", DateTimes.format(LocalDateTime.of(2019, 10, 15, 11, 59)));
        assertEquals("Oct 15 2019 11:59 PM", DateTimes.format(LocalDateTime.of(2019, 10, 15, 23, 59)));
    }

    /**
     * The month name and the AM/PM marker must be English wherever the bot is
     * run, because the recorded test cases in {@code test/ui-test-plan.md} say
     * so -- a machine set to another language would otherwise fail tests that
     * are about something else entirely.
     *
     * <p>The default locale is a setting shared by the whole program, so it is
     * put back in a {@code finally} block: a test that changed it and left it
     * changed would quietly alter every test that ran after it.</p>
     */
    @Test
    public void format_nonEnglishDefaultLocale_returnsEnglishMonthAndMarker() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.GERMANY);
            assertEquals("Oct 15 2019 06:00 PM",
                    DateTimes.format(LocalDateTime.of(2019, 10, 15, 18, 0)));
        } finally {
            Locale.setDefault(original);
        }
    }

    // ================= toFileString: writing a date for the program to read =============

    /**
     * The form the save file holds. Seconds are left out because every date the
     * bot stores has none, which is what keeps the saved line as short as the
     * format in the requirements.
     */
    @Test
    public void toFileString_dateTimeWithMinutes_returnsIsoText() {
        assertEquals("2019-10-15T18:00", DateTimes.toFileString(LocalDateTime.of(2019, 10, 15, 18, 0)));
    }

    /**
     * Midnight still writes its time part rather than being shortened to the
     * date alone, so a date-only deadline is stored the same way as any other.
     */
    @Test
    public void toFileString_midnight_returnsIsoTextWithZeroTime() {
        assertEquals("2019-12-01T00:00", DateTimes.toFileString(LocalDateTime.of(2019, 12, 1, 0, 0)));
    }

    /**
     * Unlike the displayed form, the stored form pads the month, the day and
     * the hour, since a fixed-width layout is what makes the file unambiguous
     * to read back.
     */
    @Test
    public void toFileString_singleDigitParts_returnsPaddedIsoText() {
        assertEquals("2019-01-05T09:05", DateTimes.toFileString(LocalDateTime.of(2019, 1, 5, 9, 5)));
    }

    /**
     * The path an actual save takes: the user types one of the accepted
     * layouts, and the file records it in the single stored layout. Whichever
     * way the date was typed, the file looks the same.
     */
    @Test
    public void toFileString_dateTimeFromTypedLayout_returnsIsoText() throws PiplupBotException {
        assertEquals("2019-12-02T18:00", DateTimes.toFileString(DateTimes.parse("2/12/2019 1800")));
        assertEquals("2019-12-02T18:00", DateTimes.toFileString(DateTimes.parse("2019-12-02 18:00")));
    }

    /**
     * A date typed without a time is stored at midnight, so the choice
     * {@code parse} made for the user is what the file keeps -- and is what the
     * bot shows on the next run.
     */
    @Test
    public void toFileString_dateTimeFromDateOnlyInput_returnsMidnightIsoText() throws PiplupBotException {
        assertEquals("2019-12-02T00:00", DateTimes.toFileString(DateTimes.parse("2/12/2019")));
    }
}
