package bibi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bibi.BibiException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/**
 * Tests the parsing, comparison, and formatting of task dates.
 */
public class TaskDateTimeTest {

    @Test
    public void parse_isoDateOnly_noTimeShown() throws BibiException {
        assertEquals("Oct 15 2019", TaskDateTime.parse("2019-10-15").toString());
    }

    @Test
    public void parse_isoDateAndTime_timeShown() throws BibiException {
        assertEquals("Dec 02 2019 6:00PM", TaskDateTime.parse("2019-12-02 1800").toString());
    }

    @Test
    public void parse_slashDateAndTime_timeShown() throws BibiException {
        assertEquals("Dec 02 2019 6:00PM", TaskDateTime.parse("2/12/2019 1800").toString());
    }

    @Test
    public void parse_slashDateOnly_noTimeShown() throws BibiException {
        assertEquals("Dec 02 2019", TaskDateTime.parse("2/12/2019").toString());
    }

    @Test
    public void parse_midnightGivenExplicitly_timeStillShown() throws BibiException {
        // A date with an explicit 0000 must stay distinguishable from a whole-day date.
        assertEquals("Dec 02 2019 12:00AM", TaskDateTime.parse("2019-12-02 0000").toString());
    }

    @Test
    public void parse_surroundingSpaces_ignored() throws BibiException {
        assertEquals("Oct 15 2019", TaskDateTime.parse("  2019-10-15  ").toString());
    }

    @Test
    public void parse_unrecognisedText_exceptionThrown() {
        BibiException thrown = assertThrows(BibiException.class,
                () -> TaskDateTime.parse("next Tuesday"));
        assertTrue(thrown.getMessage().contains("next Tuesday"));
        assertTrue(thrown.getMessage().contains("yyyy-MM-dd"));
    }

    @Test
    public void parse_impossibleCalendarDate_exceptionThrown() {
        assertThrows(BibiException.class, () -> TaskDateTime.parse("2019-13-45"));
    }

    @Test
    public void parse_dayOutOfRangeForMonth_exceptionThrown() {
        assertThrows(BibiException.class, () -> TaskDateTime.parse("2019-02-30"));
    }

    @Test
    public void parse_blankOrNull_exceptionThrown() {
        assertThrows(BibiException.class, () -> TaskDateTime.parse("   "));
        assertThrows(BibiException.class, () -> TaskDateTime.parse(null));
    }

    @Test
    public void toStorageString_dateOnly_roundTripsBackToTheSameValue() throws BibiException {
        TaskDateTime original = TaskDateTime.parse("2019-10-15");
        assertEquals("2019-10-15", original.toStorageString());
        assertEquals(original.toString(),
                TaskDateTime.parse(original.toStorageString()).toString());
    }

    @Test
    public void toStorageString_dateAndTime_roundTripsBackToTheSameValue() throws BibiException {
        TaskDateTime original = TaskDateTime.parse("2/12/2019 1800");
        assertEquals("2019-12-02 1800", original.toStorageString());
        assertEquals(original.toString(),
                TaskDateTime.parse(original.toStorageString()).toString());
    }

    @Test
    public void toStorageString_singleDigitTime_paddedToFourDigits() throws BibiException {
        assertEquals("2019-12-02 0905", TaskDateTime.parse("2019-12-02 0905").toStorageString());
    }

    @Test
    public void isOn_matchingAndDifferentDates_reportedCorrectly() throws BibiException {
        TaskDateTime value = TaskDateTime.parse("2019-10-15 1800");
        assertTrue(value.isOn(LocalDate.of(2019, 10, 15)));
        assertFalse(value.isOn(LocalDate.of(2019, 10, 16)));
    }

    @Test
    public void isBefore_earlierAndLaterValues_reportedCorrectly() throws BibiException {
        TaskDateTime earlier = TaskDateTime.parse("2019-10-15 0900");
        TaskDateTime later = TaskDateTime.parse("2019-10-15 1700");
        assertTrue(earlier.isBefore(later));
        assertFalse(later.isBefore(earlier));
    }

    @Test
    public void isBefore_sameValue_false() throws BibiException {
        TaskDateTime value = TaskDateTime.parse("2019-10-15 0900");
        assertFalse(value.isBefore(TaskDateTime.parse("2019-10-15 0900")));
    }

    @Test
    public void isBefore_wholeDayAgainstTimeOnSameDay_countsAsStartOfDay() throws BibiException {
        // A date with no time is treated as the start of its day.
        assertTrue(TaskDateTime.parse("2019-10-15").isBefore(TaskDateTime.parse("2019-10-15 0900")));
    }

    @Test
    public void formatDate_anyDate_usesTheDisplayFormat() {
        assertEquals("Aug 06 2019", TaskDateTime.formatDate(LocalDate.of(2019, 8, 6)));
    }
}
