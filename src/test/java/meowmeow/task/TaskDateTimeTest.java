package meowmeow.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import meowmeow.MeowMeowException;

public class TaskDateTimeTest {

    @Test
    public void parse_isoDateWithTime_timeShown() throws MeowMeowException {
        assertEquals("Dec 02 2099, 6:00PM", TaskDateTime.parse("2099-12-02 1800").toString());
    }

    @Test
    public void parse_colonTimeFormat_timeShown() throws MeowMeowException {
        assertEquals("Dec 02 2099, 6:30PM", TaskDateTime.parse("2099-12-02 18:30").toString());
    }

    @Test
    public void parse_slashDateNoTime_dateOnlyShown() throws MeowMeowException {
        assertEquals("Dec 02 2099", TaskDateTime.parse("2/12/2099").toString());
    }

    @Test
    public void parse_unrecognisedText_exceptionThrown() {
        assertThrows(MeowMeowException.class, () -> TaskDateTime.parse("sometime soon"));
    }

    @Test
    public void parse_dayOutsideMonth_exceptionThrown() {
        assertThrows(MeowMeowException.class, () -> TaskDateTime.parse("2099-02-30"));
    }

    @Test
    public void parse_februaryTwentyNineInNonLeapYear_exceptionThrown() {
        assertThrows(MeowMeowException.class, () -> TaskDateTime.parse("2099-02-29"));
    }

    @Test
    public void parse_februaryTwentyNineInLeapYear_accepted() throws MeowMeowException {
        assertEquals("Feb 29 2104", TaskDateTime.parse("2104-02-29").toString());
    }

    @Test
    public void parse_dayName_resolvesToFutureDayOfThatName() throws MeowMeowException {
        TaskDateTime friday = TaskDateTime.parse("fri");
        assertEquals(DayOfWeek.FRIDAY, friday.toLocalDate().getDayOfWeek());
        assertTrue(friday.toLocalDate().isAfter(LocalDate.now()));
    }

    @Test
    public void parse_fullDayNameMixedCase_accepted() throws MeowMeowException {
        assertEquals(DayOfWeek.MONDAY, TaskDateTime.parse("Monday").toLocalDate().getDayOfWeek());
    }

    @Test
    public void isInThePast_longPastDate_returnsTrue() throws MeowMeowException {
        assertTrue(TaskDateTime.parse("2000-01-01").isInThePast());
    }

    @Test
    public void isInThePast_todayWithoutTime_returnsFalse() throws MeowMeowException {
        assertFalse(TaskDateTime.parse(LocalDate.now().toString()).isInThePast());
    }

    @Test
    public void isBefore_earlierDate_returnsTrue() throws MeowMeowException {
        assertTrue(TaskDateTime.parse("2099-01-01").isBefore(TaskDateTime.parse("2099-01-02")));
    }

    @Test
    public void equals_sameDateDifferentInputFormat_returnsTrue() throws MeowMeowException {
        assertEquals(TaskDateTime.parse("2099-12-02"), TaskDateTime.parse("2/12/2099"));
    }

    @Test
    public void equals_sameInstantDifferentPrecision_returnsFalse() throws MeowMeowException {
        assertNotEquals(TaskDateTime.parse("2099-12-02"), TaskDateTime.parse("2099-12-02 0000"));
    }

    @Test
    public void fromStorage_roundTrip_valuePreserved() throws MeowMeowException {
        TaskDateTime original = TaskDateTime.parse("2099-12-02 1800");
        assertEquals(original, TaskDateTime.fromStorage(original.toStorageFormat()));
    }
}