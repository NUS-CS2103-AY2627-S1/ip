package billy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import billy.BillyException;

/**
 * Tests {@link TaskDate}, which reads and writes the dates that tasks carry.
 *
 * <p>This class is worth testing closely because it holds the same moment in
 * three shapes at once — the text the user types, the text shown back, and the
 * text written to the save file — and nothing outside it can tell when those
 * drift apart. A date that is shown correctly but saved wrongly would look fine
 * all session and lose the user's work when Billy restarts.
 *
 * <p>The tests are named {@code feature_scenario_expectedBehavior} so that a
 * failure report says what broke without anyone having to open this file.
 */
public class TaskDateTest {

    // ---------------------------------------------------------------
    // Reading what the user typed
    // ---------------------------------------------------------------

    @Test
    public void parse_isoDateWithoutTime_dayKeptTimeAbsent() throws BillyException {
        TaskDate date = TaskDate.parse("2019-12-02");
        assertEquals(LocalDate.of(2019, 12, 2), date.getDate());
        // No time was given, so none should be invented.
        assertEquals("Dec 2 2019", date.toString());
    }

    @Test
    public void parse_isoDateWithTime_bothKept() throws BillyException {
        TaskDate date = TaskDate.parse("2019-12-02 1800");
        assertEquals(LocalDate.of(2019, 12, 2), date.getDate());
        assertEquals("Dec 2 2019, 6:00pm", date.toString());
    }

    @Test
    public void parse_dayFirstDate_readAsDayThenMonth() throws BillyException {
        // 2/12 is the 2nd of December here, not the 12th of February.
        assertEquals(LocalDate.of(2019, 12, 2), TaskDate.parse("2/12/2019").getDate());
    }

    @Test
    public void parse_dayFirstDateWithLeadingZeros_sameDay() throws BillyException {
        assertEquals(TaskDate.parse("2/12/2019").getDate(),
                TaskDate.parse("02/12/2019").getDate());
    }

    @Test
    public void parse_dayFirstDateWithTime_bothKept() throws BillyException {
        assertEquals("Dec 2 2019, 6:00pm", TaskDate.parse("2/12/2019 1800").toString());
    }

    @Test
    public void parse_surroundingSpaces_ignored() throws BillyException {
        assertEquals(LocalDate.of(2019, 12, 2), TaskDate.parse("   2019-12-02   ").getDate());
    }

    @Test
    public void parse_midnight_shownAsTwelveAm() throws BillyException {
        // Guards the 12-hour conversion at both ends of the day, where a 0/12
        // mix-up is easiest to make and hardest to notice.
        assertEquals("Dec 2 2019, 12:00am", TaskDate.parse("2019-12-02 0000").toString());
    }

    @Test
    public void parse_noon_shownAsTwelvePm() throws BillyException {
        assertEquals("Dec 2 2019, 12:00pm", TaskDate.parse("2019-12-02 1200").toString());
    }

    // ---------------------------------------------------------------
    // Refusing what is not a date
    // ---------------------------------------------------------------

    @Test
    public void parse_dayThatDoesNotExist_exceptionThrown() {
        // February never has 30 days. Accepting it would silently shift the task
        // to the 2nd of March, which is why the parser resolves dates strictly.
        assertThrows(BillyException.class, () -> TaskDate.parse("2019-02-30"));
    }

    @Test
    public void parse_dayFirstDateThatDoesNotExist_exceptionThrown() {
        // The same rule has to hold for the other accepted shape.
        assertThrows(BillyException.class, () -> TaskDate.parse("30/2/2019"));
    }

    @Test
    public void parse_monthOutOfRange_exceptionThrown() {
        assertThrows(BillyException.class, () -> TaskDate.parse("2019-13-01"));
    }

    @Test
    public void parse_timeOutOfRange_exceptionThrown() {
        assertThrows(BillyException.class, () -> TaskDate.parse("2019-12-02 2500"));
    }

    @Test
    public void parse_wordsInsteadOfDate_exceptionThrown() {
        assertThrows(BillyException.class, () -> TaskDate.parse("tomorrow"));
    }

    @Test
    public void parse_emptyText_exceptionThrown() {
        assertThrows(BillyException.class, () -> TaskDate.parse(""));
    }

    @Test
    public void parse_textAfterTheTime_exceptionThrown() {
        // Trailing rubbish must be refused rather than quietly dropped, or the
        // user would believe Billy understood something it ignored.
        assertThrows(BillyException.class, () -> TaskDate.parse("2019-12-02 1800 sharp"));
    }

    @Test
    public void parse_unreadableText_messageQuotesInputAndExplainsFormat() {
        BillyException thrown =
                assertThrows(BillyException.class, () -> TaskDate.parse("someday"));
        // The message has to be useful on its own, since it is all the user sees.
        assertTrue(thrown.getMessage().contains("someday"));
        assertTrue(thrown.getMessage().contains(TaskDate.USAGE));
    }

    // ---------------------------------------------------------------
    // Writing back out again
    // ---------------------------------------------------------------

    @Test
    public void toSaveFormat_dateOnly_readsBackAsTheSameDate() throws BillyException {
        TaskDate original = TaskDate.parse("2019-12-02");
        TaskDate reloaded = TaskDate.parse(original.toSaveFormat());
        // This round trip is the promise the save file relies on: whatever is
        // written today has to mean the same thing when read back tomorrow.
        assertEquals(original.getDate(), reloaded.getDate());
        assertEquals(original.toString(), reloaded.toString());
    }

    @Test
    public void toSaveFormat_dateAndTime_readsBackAsTheSameMoment() throws BillyException {
        TaskDate original = TaskDate.parse("2019-12-02 1800");
        TaskDate reloaded = TaskDate.parse(original.toSaveFormat());
        assertEquals(original.toString(), reloaded.toString());
    }

    @Test
    public void toSaveFormat_dayFirstInput_writtenInDashedForm() throws BillyException {
        // Both accepted shapes are stored one way, so the file has a single
        // format however the user happened to type the date.
        assertEquals("2019-12-02", TaskDate.parse("2/12/2019").toSaveFormat());
    }

    @Test
    public void toSaveFormat_dateAndTime_keepsTheTime() throws BillyException {
        assertEquals("2019-12-02 1800", TaskDate.parse("2019-12-02 1800").toSaveFormat());
    }

    @Test
    public void formatDate_anyDate_writtenAsPeopleReadIt() {
        assertEquals("Dec 2 2019", TaskDate.formatDate(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void formatDate_singleDigitDay_noPaddingZero() {
        // "Dec 02 2019" would be the giveaway that the pattern used dd, not d.
        assertEquals("Jan 5 2020", TaskDate.formatDate(LocalDate.of(2020, 1, 5)));
    }

    @Test
    public void getDate_dateWithTime_timeIgnored() throws BillyException {
        assertEquals(LocalDate.of(2019, 12, 2), TaskDate.parse("2019-12-02 1800").getDate());
    }

    // ---------------------------------------------------------------
    // Ordering two points in time
    // ---------------------------------------------------------------

    @Test
    public void isBefore_earlierDay_true() throws BillyException {
        assertTrue(TaskDate.parse("2019-12-02").isBefore(TaskDate.parse("2019-12-03")));
    }

    @Test
    public void isBefore_laterDay_false() throws BillyException {
        assertFalse(TaskDate.parse("2019-12-03").isBefore(TaskDate.parse("2019-12-02")));
    }

    @Test
    public void isBefore_sameDayEarlierTime_true() throws BillyException {
        assertTrue(TaskDate.parse("2019-12-02 1000").isBefore(TaskDate.parse("2019-12-02 1400")));
    }

    @Test
    public void isBefore_sameDayLaterTime_false() throws BillyException {
        assertFalse(TaskDate.parse("2019-12-02 1400").isBefore(TaskDate.parse("2019-12-02 1000")));
    }

    @Test
    public void isBefore_sameDaySameTime_false() throws BillyException {
        // Equal is not before, or an event could not start and end at one moment.
        assertFalse(TaskDate.parse("2019-12-02 1000").isBefore(TaskDate.parse("2019-12-02 1000")));
    }

    @Test
    public void isBefore_sameDayOnlyOneHasTime_false() throws BillyException {
        // A date with no time says nothing about the hour, so neither side can be
        // shown to come first. Treating the missing time as midnight would invent
        // an ordering the user never gave.
        assertFalse(TaskDate.parse("2019-12-02").isBefore(TaskDate.parse("2019-12-02 1000")));
        assertFalse(TaskDate.parse("2019-12-02 1000").isBefore(TaskDate.parse("2019-12-02")));
    }

    // ---------------------------------------------------------------
    // isSameInstant and hasTime
    // ---------------------------------------------------------------

    @Test
    public void isSameInstant_sameDateAndTime_true() throws BillyException {
        assertTrue(TaskDate.parse("2019-12-02 1800").isSameInstant(TaskDate.parse("2019-12-02 1800")));
    }

    @Test
    public void isSameInstant_sameDateNeitherWithATime_true() throws BillyException {
        assertTrue(TaskDate.parse("2019-12-02").isSameInstant(TaskDate.parse("2019-12-02")));
    }

    @Test
    public void isSameInstant_oneWithATimeAndOneWithout_false() throws BillyException {
        // A bare date is not midnight: Billy was never told an hour, and
        // treating the two as the same would invent one.
        assertFalse(TaskDate.parse("2019-12-02").isSameInstant(TaskDate.parse("2019-12-02 0000")));
    }

    @Test
    public void isSameInstant_differentDates_false() throws BillyException {
        assertFalse(TaskDate.parse("2019-12-02").isSameInstant(TaskDate.parse("2019-12-03")));
    }

    @Test
    public void isSameInstant_sameDayWrittenBothWays_true() throws BillyException {
        // The two accepted spellings of one day must agree, or the same date
        // typed two ways would look like two different ones.
        assertTrue(TaskDate.parse("2019-12-02").isSameInstant(TaskDate.parse("2/12/2019")));
    }

    @Test
    public void hasTime_dateOnly_false() throws BillyException {
        assertFalse(TaskDate.parse("2019-12-02").hasTime());
    }

    @Test
    public void hasTime_dateAndTime_true() throws BillyException {
        assertTrue(TaskDate.parse("2019-12-02 1800").hasTime());
    }
}
