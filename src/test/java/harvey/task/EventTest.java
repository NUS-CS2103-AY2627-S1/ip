package harvey.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import harvey.HarveyException;

/**
 * Tests {@link Event}, in particular the reading of times and the detection of clashes.
 * <p>
 * An event carries two points in time rather than one, so it is the only task that can
 * conflict with another. The overlap rule is checked at its boundaries, since those are
 * where an off-by-one comparison hides: two lessons that merely touch must be allowed,
 * while two that share any real time must not.
 */
public class EventTest {
    /** A time to build events around, so the tests read as one afternoon. */
    private static final LocalDateTime FOUR_PM = LocalDateTime.of(2026, 9, 10, 16, 0);
    private static final LocalDateTime SIX_PM = LocalDateTime.of(2026, 9, 10, 18, 0);
    private static final LocalDateTime EIGHT_PM = LocalDateTime.of(2026, 9, 10, 20, 0);

    @Test
    public void parseDateTime_dateAndTime_returnsThatMoment() throws HarveyException {
        assertEquals(FOUR_PM, Event.parseDateTime("2026-09-10 1600"));
    }

    @Test
    public void parseDateTime_midnight_returnsThatMoment() throws HarveyException {
        assertEquals(LocalDateTime.of(2026, 9, 10, 0, 0), Event.parseDateTime("2026-09-10 0000"));
    }

    @Test
    public void parseDateTime_hourThatDoesNotExist_exceptionThrown() {
        // Well formed but impossible: the clock never reaches 24:00.
        assertThrows(HarveyException.class, () -> Event.parseDateTime("2026-09-10 2400"));
    }

    @Test
    public void parseDateTime_dateWithoutTime_exceptionThrown() {
        assertThrows(HarveyException.class, () -> Event.parseDateTime("2026-09-10"));
    }

    @Test
    public void parseDateTime_words_exceptionThrown() {
        assertThrows(HarveyException.class, () -> Event.parseDateTime("Mon 2pm"));
    }

    @Test
    public void parseDateTime_unparseableText_messageNamesTheExpectedFormat() {
        HarveyException e = assertThrows(
                HarveyException.class, () -> Event.parseDateTime("Mon 2pm"));
        // The message has to tell the user what to type instead, not just that it failed.
        assertEquals("I could not read \"Mon 2pm\" as a date and time. "
                + "Please write it as yyyy-mm-dd HHmm, for example 2019-10-15 1400.", e.getMessage());
    }

    @Test
    public void constructor_endBeforeStart_exceptionThrown() {
        assertThrows(HarveyException.class, () -> new Event("tuition", SIX_PM, FOUR_PM));
    }

    @Test
    public void constructor_endEqualToStart_exceptionThrown() {
        // An event of no length has no place in a schedule and would clash with nothing.
        assertThrows(HarveyException.class, () -> new Event("tuition", FOUR_PM, FOUR_PM));
    }

    @Test
    public void overlaps_sameTimes_returnsTrue() throws HarveyException {
        Event sarah = new Event("tuition Sarah", FOUR_PM, SIX_PM);
        Event ben = new Event("tuition Ben", FOUR_PM, SIX_PM);
        assertTrue(sarah.overlaps(ben));
    }

    @Test
    public void overlaps_partialOverlap_returnsTrue() throws HarveyException {
        Event sarah = new Event("tuition Sarah", FOUR_PM, SIX_PM);
        Event ben = new Event("tuition Ben", LocalDateTime.of(2026, 9, 10, 17, 0), EIGHT_PM);
        assertTrue(sarah.overlaps(ben));
    }

    @Test
    public void overlaps_oneInsideTheOther_returnsTrue() throws HarveyException {
        Event lesson = new Event("tuition Sarah", FOUR_PM, EIGHT_PM);
        Event shortBreak = new Event("coffee", LocalDateTime.of(2026, 9, 10, 17, 0), SIX_PM);
        assertTrue(lesson.overlaps(shortBreak));
    }

    @Test
    public void overlaps_backToBack_returnsFalse() throws HarveyException {
        // A tutor teaching 4-6 and then 6-8 is booked solidly, not double booked.
        Event sarah = new Event("tuition Sarah", FOUR_PM, SIX_PM);
        Event ben = new Event("tuition Ben", SIX_PM, EIGHT_PM);
        assertFalse(sarah.overlaps(ben));
    }

    @Test
    public void overlaps_overlapByOneMinute_returnsTrue() throws HarveyException {
        Event sarah = new Event("tuition Sarah", FOUR_PM, SIX_PM);
        Event ben = new Event("tuition Ben", LocalDateTime.of(2026, 9, 10, 17, 59), EIGHT_PM);
        assertTrue(sarah.overlaps(ben));
    }

    @Test
    public void overlaps_differentDaysSameClockTimes_returnsFalse() throws HarveyException {
        Event thisWeek = new Event("tuition Sarah", FOUR_PM, SIX_PM);
        Event nextWeek = new Event("tuition Sarah", FOUR_PM.plusDays(7), SIX_PM.plusDays(7));
        assertFalse(thisWeek.overlaps(nextWeek));
    }

    @Test
    public void overlaps_isSymmetric_returnsSameBothWays() throws HarveyException {
        Event sarah = new Event("tuition Sarah", FOUR_PM, EIGHT_PM);
        Event ben = new Event("tuition Ben", SIX_PM, EIGHT_PM.plusHours(1));
        assertEquals(sarah.overlaps(ben), ben.overlaps(sarah));
    }

    @Test
    public void toString_notDone_showsDisplayDateTimeFormat() throws HarveyException {
        Event event = new Event("tuition Sarah", FOUR_PM, SIX_PM);
        assertEquals("[E][ ] tuition Sarah (from: Sep 10 2026 4:00PM to: Sep 10 2026 6:00PM)",
                event.toString());
    }

    @Test
    public void toFileFormat_notDone_usesInputFormatAndZeroFlag() throws HarveyException {
        Event event = new Event("tuition Sarah", FOUR_PM, SIX_PM);
        // The file must hold the input format, so parseDateTime can read it back.
        assertEquals("E | 0 | tuition Sarah | 2026-09-10 1600 | 2026-09-10 1800",
                event.toFileFormat());
    }

    @Test
    public void toFileFormat_thenParseDateTime_timesSurviveTheRoundTrip() throws HarveyException {
        String line = new Event("tuition Sarah", FOUR_PM, SIX_PM).toFileFormat();

        // Pull the times back out of the line the same way Storage does. This is the
        // property that keeps saved events loadable.
        String[] fields = line.split(" \\| ");
        assertEquals(FOUR_PM, Event.parseDateTime(fields[3]));
        assertEquals(SIX_PM, Event.parseDateTime(fields[4]));
    }

    @Test
    public void toString_computerSetToChinese_monthAndPmStillInEnglish() throws HarveyException {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.CHINA);
            assertEquals("[E][ ] tuition Sarah (from: Sep 10 2026 4:00PM to: Sep 10 2026 6:00PM)",
                    new Event("tuition Sarah", FOUR_PM, SIX_PM).toString());
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    public void hasSameDetails_sameDescriptionAndTimes_returnsTrue() throws HarveyException {
        Event event = new Event("tuition", FOUR_PM, SIX_PM);
        assertTrue(event.hasSameDetails(new Event("Tuition", FOUR_PM, SIX_PM)));
    }

    @Test
    public void hasSameDetails_sameStartOtherEnd_returnsFalse() throws HarveyException {
        // Both times take part in the comparison, not just the start.
        Event event = new Event("tuition", FOUR_PM, SIX_PM);
        assertFalse(event.hasSameDetails(new Event("tuition", FOUR_PM, EIGHT_PM)));
    }

    @Test
    public void hasSameDetails_otherDescriptionSameTimes_returnsFalse() throws HarveyException {
        Event event = new Event("tuition", FOUR_PM, SIX_PM);
        assertFalse(event.hasSameDetails(new Event("dinner", FOUR_PM, SIX_PM)));
    }
}
