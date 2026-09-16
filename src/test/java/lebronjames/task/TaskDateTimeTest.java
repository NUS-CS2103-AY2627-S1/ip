package lebronjames.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import lebronjames.LebronJamesException;

/**
 * Tests {@link TaskDateTime}, which decides which of several date formats the
 * user typed and how a date is written back out.
 *
 * <p>Method names follow featureUnderTest_testScenario_expectedBehavior.
 */
public class TaskDateTimeTest {
    @Test
    public void parse_isoDateWithoutTime_dateStoredTimeOmitted() throws LebronJamesException {
        TaskDateTime parsed = TaskDateTime.parse("2019-12-02");

        assertEquals(LocalDate.of(2019, 12, 2), parsed.getDate());
        // No time was given, so none should be invented or printed.
        assertEquals("2019-12-02", parsed.toFileFormat());
        assertEquals("Dec 02 2019", parsed.toString());
    }

    @Test
    public void parse_slashDateWithoutTime_dateStoredTimeOmitted() throws LebronJamesException {
        TaskDateTime parsed = TaskDateTime.parse("2/12/2019");

        assertEquals(LocalDate.of(2019, 12, 2), parsed.getDate());
        assertEquals("2019-12-02", parsed.toFileFormat());
    }

    @Test
    public void parse_isoDateWithTime_dateAndTimeStored() throws LebronJamesException {
        TaskDateTime parsed = TaskDateTime.parse("2019-12-02 1800");

        assertEquals(LocalDate.of(2019, 12, 2), parsed.getDate());
        assertEquals("2019-12-02 1800", parsed.toFileFormat());
        assertEquals("Dec 02 2019, 6:00pm", parsed.toString());
    }

    @Test
    public void parse_slashDateWithTime_dateAndTimeStored() throws LebronJamesException {
        TaskDateTime parsed = TaskDateTime.parse("2/12/2019 1800");

        assertEquals(LocalDate.of(2019, 12, 2), parsed.getDate());
        assertEquals("2019-12-02 1800", parsed.toFileFormat());
    }

    @Test
    public void parse_singleDigitDayAndMonth_accepted() throws LebronJamesException {
        // The d/M/yyyy pattern must accept "2/1/2019" as well as "02/01/2019".
        assertEquals(LocalDate.of(2019, 1, 2), TaskDateTime.parse("2/1/2019").getDate());
        assertEquals(LocalDate.of(2019, 1, 2), TaskDateTime.parse("02/01/2019").getDate());
    }

    @Test
    public void parse_surroundingWhitespace_ignored() throws LebronJamesException {
        assertEquals(LocalDate.of(2019, 12, 2), TaskDateTime.parse("   2019-12-02   ").getDate());
    }

    @Test
    public void parse_midnight_timeKeptNotTreatedAsAbsent() throws LebronJamesException {
        // 0000 is a real time the user typed, so it must not be mistaken for
        // "no time given" - that would silently drop it when saving.
        TaskDateTime parsed = TaskDateTime.parse("2019-12-02 0000");

        assertEquals("2019-12-02 0000", parsed.toFileFormat());
        assertEquals("Dec 02 2019, 12:00am", parsed.toString());
    }

    @Test
    public void parse_noon_displayedAsPm() throws LebronJamesException {
        assertEquals("Dec 02 2019, 12:00pm", TaskDateTime.parse("2019-12-02 1200").toString());
    }

    @Test
    public void parse_unrecognisedFormat_exceptionThrown() {
        assertThrows(LebronJamesException.class, () -> TaskDateTime.parse("tomorrow"));
        assertThrows(LebronJamesException.class, () -> TaskDateTime.parse("2019/12/02"));
        assertThrows(LebronJamesException.class, () -> TaskDateTime.parse("02-12-2019"));
        assertThrows(LebronJamesException.class, () -> TaskDateTime.parse(""));
    }

    @Test
    public void parse_impossibleCalendarDate_exceptionThrown() {
        // A well-formed but non-existent date must be rejected rather than
        // silently rolled over to the 1st of the next month.
        assertThrows(LebronJamesException.class, () -> TaskDateTime.parse("2019-02-30"));
        assertThrows(LebronJamesException.class, () -> TaskDateTime.parse("2019-13-01"));
        assertThrows(LebronJamesException.class, () -> TaskDateTime.parse("2019-12-02 2500"));
    }

    @Test
    public void parse_leapDay_acceptedOnlyInLeapYear() throws LebronJamesException {
        assertEquals(LocalDate.of(2020, 2, 29), TaskDateTime.parse("2020-02-29").getDate());
        assertThrows(LebronJamesException.class, () -> TaskDateTime.parse("2019-02-29"));
    }

    @Test
    public void parse_errorMessage_namesTheOffendingTextAndAcceptedFormats() {
        LebronJamesException exception =
                assertThrows(LebronJamesException.class, () -> TaskDateTime.parse("tomorrow"));

        assertEquals("AIRBALL. I could not read the date 'tomorrow'.\n"
                + "Please use " + TaskDateTime.ACCEPTED_FORMATS + ".", exception.getMessage());
    }

    @Test
    public void toFileFormat_reparsed_producesSameValue() throws LebronJamesException {
        // The save file is only useful if what is written can be read back
        // unchanged, whichever format the user originally typed.
        for (String typed : new String[] {"2019-12-02", "2/12/2019", "2019-12-02 1800", "2/12/2019 0930"}) {
            String saved = TaskDateTime.parse(typed).toFileFormat();

            assertEquals(saved, TaskDateTime.parse(saved).toFileFormat(), "round trip failed for: " + typed);
        }
    }

    @Test
    public void equals_sameDateWrittenTwoWays_equal() throws LebronJamesException {
        // The two accepted formats mean the same day, so they must compare equal
        // or duplicate detection would depend on how the user typed the date.
        assertEquals(TaskDateTime.parse("2019-12-02"), TaskDateTime.parse("2/12/2019"));
    }

    @Test
    public void equals_sameDateAndTime_equalWithMatchingHashCode() throws LebronJamesException {
        TaskDateTime first = TaskDateTime.parse("2019-12-02 1800");
        TaskDateTime second = TaskDateTime.parse("2019-12-02 1800");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void equals_dateOnlyVersusDateWithTime_notEqual() throws LebronJamesException {
        // "by Monday" and "by Monday 6pm" are different instructions.
        assertNotEquals(TaskDateTime.parse("2019-12-02"), TaskDateTime.parse("2019-12-02 1800"));
    }

    @Test
    public void equals_differentDates_notEqual() throws LebronJamesException {
        assertNotEquals(TaskDateTime.parse("2019-12-02"), TaskDateTime.parse("2019-12-03"));
    }
}
