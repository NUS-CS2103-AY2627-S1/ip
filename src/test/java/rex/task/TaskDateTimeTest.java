package rex.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests reading a date from text and writing it back out again.
 *
 * The two formats a date is written in are checked alongside the reading of
 * it, because they are what the reading has to agree with: the display format
 * is what the user sees, and the save format is what has to be readable again
 * on the next run.
 *
 * Test methods are deliberately not public. JUnit 5 does not require it, and
 * keeping them package-private means the coding standard's rule about
 * documenting public methods does not apply to a name that already says what
 * the test does.
 */
public class TaskDateTimeTest {
    @Test
    void parse_dateOnly_noTimeShown() {
        TaskDateTime date = TaskDateTime.parse("2019-10-15");

        assertEquals("Oct 15 2019", date.toString());
        assertEquals("2019-10-15", date.toSaveFormat());
    }

    @Test
    void parse_dateWithTime_timeShown() {
        TaskDateTime dateTime = TaskDateTime.parse("2019-10-15 1800");

        assertEquals("Oct 15 2019, 6:00PM", dateTime.toString());
        assertEquals("2019-10-15 1800", dateTime.toSaveFormat());
    }

    @Test
    void parse_midnight_countsAsHavingATime() {
        // Midnight is the value stored for a date given without a time, so this
        // is what shows that a time is remembered by its own flag rather than
        // guessed from the value.
        TaskDateTime midnight = TaskDateTime.parse("2019-10-15 0000");

        assertEquals("Oct 15 2019, 12:00AM", midnight.toString());
        assertEquals("2019-10-15 0000", midnight.toSaveFormat());
    }

    @Test
    void parse_surroundingWhitespace_ignored() {
        assertEquals("2019-10-15", TaskDateTime.parse("   2019-10-15   ").toSaveFormat());
        assertEquals("2019-10-15 1800", TaskDateTime.parse("  2019-10-15 1800  ").toSaveFormat());
    }

    @Test
    void toSaveFormat_readBackAgain_unchanged() {
        // Saving and loading must not drift, or a task would come back from the
        // file describing a different moment than the one that was written.
        String[] inputs = {"2019-10-15", "2019-10-15 1800", "2019-10-15 0000"};

        for (String input : inputs) {
            TaskDateTime original = TaskDateTime.parse(input);
            TaskDateTime reloaded = TaskDateTime.parse(original.toSaveFormat());

            assertEquals(original.toSaveFormat(), reloaded.toSaveFormat());
            assertEquals(original.toString(), reloaded.toString());
        }
    }

    @Test
    void parse_slashSeparatedDate_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> TaskDateTime.parse("15/10/2019"));
    }

    @Test
    void parse_dayOutsideMonth_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> TaskDateTime.parse("2019-02-30"));
    }

    @Test
    void parse_monthOutOfRange_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> TaskDateTime.parse("2019-13-01"));
    }

    @Test
    void parse_timeOutOfRange_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> TaskDateTime.parse("2019-10-15 2500"));
    }

    @Test
    void parse_timeMissingLeadingZero_exceptionThrown() {
        // A time is four digits, so 8am is 0800 and never 800.
        assertThrows(IllegalArgumentException.class, () -> TaskDateTime.parse("2019-10-15 800"));
    }

    @Test
    void parse_wordInsteadOfDate_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> TaskDateTime.parse("tomorrow"));
    }

    @Test
    void parse_emptyText_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> TaskDateTime.parse(""));
    }

    @Test
    void isOn_sameDayCarryingATime_returnsTrue() {
        TaskDateTime evening = TaskDateTime.parse("2019-10-15 1800");

        assertTrue(evening.isOn(LocalDate.of(2019, 10, 15)));
    }

    @Test
    void isOn_differentDay_returnsFalse() {
        TaskDateTime date = TaskDateTime.parse("2019-10-15");

        assertFalse(date.isOn(LocalDate.of(2019, 10, 16)));
    }

    @Test
    void isBefore_bothCarryingTimes_timesCompared() {
        TaskDateTime twoPm = TaskDateTime.parse("2019-10-15 1400");
        TaskDateTime fourPm = TaskDateTime.parse("2019-10-15 1600");

        assertTrue(twoPm.isBefore(fourPm));
        assertFalse(fourPm.isBefore(twoPm));
    }

    @Test
    void isBefore_sameMoment_returnsFalse() {
        TaskDateTime twoPm = TaskDateTime.parse("2019-10-15 1400");

        assertFalse(twoPm.isBefore(TaskDateTime.parse("2019-10-15 1400")));
    }

    @Test
    void isBefore_datesWithoutTimes_daysCompared() {
        assertTrue(TaskDateTime.parse("2019-10-15").isBefore(TaskDateTime.parse("2019-10-16")));
        assertFalse(TaskDateTime.parse("2019-10-16").isBefore(TaskDateTime.parse("2019-10-15")));
    }

    @Test
    void isBefore_onlyOneCarryingATimeOnTheSameDay_neitherBeforeTheOther() {
        // A plain date is the whole day, not its midnight, so a time on that
        // same day comes neither before nor after it.
        TaskDateTime twoPm = TaskDateTime.parse("2019-10-15 1400");
        TaskDateTime wholeDay = TaskDateTime.parse("2019-10-15");

        assertFalse(twoPm.isBefore(wholeDay));
        assertFalse(wholeDay.isBefore(twoPm));
    }

    @Test
    void isBefore_onlyOneCarryingATimeOnDifferentDays_daysCompared() {
        assertTrue(TaskDateTime.parse("2019-10-14").isBefore(TaskDateTime.parse("2019-10-15 0900")));
        assertFalse(TaskDateTime.parse("2019-10-15 0900").isBefore(TaskDateTime.parse("2019-10-14")));
    }

    @Test
    void formatDate_plainDay_usesDisplayFormat() {
        assertEquals("Oct 15 2019", TaskDateTime.formatDate(LocalDate.of(2019, 10, 15)));
    }
}
