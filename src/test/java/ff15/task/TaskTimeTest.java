package ff15.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import ff15.FF15Exception;

/**
 * Tests the parsing, comparison, and two output forms of {@link TaskTime}.
 *
 * <p>The two output forms are worth keeping apart: {@link TaskTime#toFileFormat()}
 * has to be re-readable by {@link TaskTime#parse(String)} on the next run, while
 * {@link TaskTime#toString()} only has to be readable by a person.
 */
public class TaskTimeTest {

    @Test
    public void parse_dateOnly_keepsDateAndOmitsTime() throws FF15Exception {
        TaskTime time = TaskTime.parse("2019-12-02");
        assertEquals(LocalDate.of(2019, 12, 2), time.getDate());
        assertEquals("Dec 02 2019", time.toString());
        assertEquals("2019-12-02", time.toFileFormat());
    }

    @Test
    public void parse_dateAndTime_keepsBoth() throws FF15Exception {
        TaskTime time = TaskTime.parse("2019-12-02 1800");
        assertEquals(LocalDate.of(2019, 12, 2), time.getDate());
        assertEquals("Dec 02 2019, 6:00pm", time.toString());
        assertEquals("2019-12-02 1800", time.toFileFormat());
    }

    @Test
    public void parse_midnight_shownAsTwelveAm() throws FF15Exception {
        assertEquals("Dec 02 2019, 12:00am", TaskTime.parse("2019-12-02 0000").toString());
    }

    @Test
    public void parse_noon_shownAsTwelvePm() throws FF15Exception {
        assertEquals("Dec 02 2019, 12:00pm", TaskTime.parse("2019-12-02 1200").toString());
    }

    @Test
    public void parse_morningTime_dropsLeadingZeroFromHourOnly() throws FF15Exception {
        assertEquals("Dec 02 2019, 9:05am", TaskTime.parse("2019-12-02 0905").toString());
    }

    @Test
    public void parse_leapDay_accepted() throws FF15Exception {
        assertEquals(LocalDate.of(2020, 2, 29), TaskTime.parse("2020-02-29").getDate());
    }

    @Test
    public void parse_dayNotInThatMonth_throwsException() {
        assertThrows(FF15Exception.class, () -> TaskTime.parse("2019-02-30"));
        assertThrows(FF15Exception.class, () -> TaskTime.parse("2019-02-29"));
        assertThrows(FF15Exception.class, () -> TaskTime.parse("2019-04-31"));
    }

    @Test
    public void parse_malformedDate_throwsException() {
        assertThrows(FF15Exception.class, () -> TaskTime.parse(""));
        assertThrows(FF15Exception.class, () -> TaskTime.parse("tomorrow"));
        assertThrows(FF15Exception.class, () -> TaskTime.parse("02/12/2019"));
        assertThrows(FF15Exception.class, () -> TaskTime.parse("2019-13-02"));
        assertThrows(FF15Exception.class, () -> TaskTime.parse("2019-2-2"));
        assertThrows(FF15Exception.class, () -> TaskTime.parse("2019-12-02extra"));
    }

    @Test
    public void parse_malformedTime_throwsException() {
        assertThrows(FF15Exception.class, () -> TaskTime.parse("2019-12-02 2500"));
        assertThrows(FF15Exception.class, () -> TaskTime.parse("2019-12-02 1870"));
        assertThrows(FF15Exception.class, () -> TaskTime.parse("2019-12-02 18:00"));
        assertThrows(FF15Exception.class, () -> TaskTime.parse("2019-12-02 6pm"));
        assertThrows(FF15Exception.class, () -> TaskTime.parse("2019-12-02 "));
    }

    @Test
    public void parse_rejectedText_messageQuotesWhatWasTyped() {
        FF15Exception thrown = assertThrows(FF15Exception.class, () -> TaskTime.parse("next friday"));
        assertTrue(thrown.getMessage().contains("next friday"),
                "the message should show the user what was rejected: " + thrown.getMessage());
    }

    @Test
    public void isBefore_earlierMoment_returnsTrue() throws FF15Exception {
        assertTrue(TaskTime.parse("2019-12-02").isBefore(TaskTime.parse("2019-12-03")));
    }

    @Test
    public void isBefore_laterMoment_returnsFalse() throws FF15Exception {
        assertFalse(TaskTime.parse("2019-12-03").isBefore(TaskTime.parse("2019-12-02")));
    }

    @Test
    public void isBefore_sameMoment_returnsFalse() throws FF15Exception {
        assertFalse(TaskTime.parse("2019-12-02 1400").isBefore(TaskTime.parse("2019-12-02 1400")));
    }

    @Test
    public void isBefore_sameDayDifferentTimes_comparesTheTime() throws FF15Exception {
        assertTrue(TaskTime.parse("2019-12-02 0900").isBefore(TaskTime.parse("2019-12-02 1700")));
        assertFalse(TaskTime.parse("2019-12-02 1700").isBefore(TaskTime.parse("2019-12-02 0900")));
    }

    @Test
    public void isBefore_dateOnlyAgainstSameDayWithTime_treatsDateAsStartOfDay() throws FF15Exception {
        assertTrue(TaskTime.parse("2019-12-02").isBefore(TaskTime.parse("2019-12-02 0001")));
        assertFalse(TaskTime.parse("2019-12-02 0001").isBefore(TaskTime.parse("2019-12-02")));
    }

    @Test
    public void formatDate_anyDate_returnsDisplayForm() {
        assertEquals("Jan 01 2020", TaskTime.formatDate(LocalDate.of(2020, 1, 1)));
        assertEquals("Dec 31 2019", TaskTime.formatDate(LocalDate.of(2019, 12, 31)));
    }

    @Test
    public void parse_impossibleDateWithTime_throwsRatherThanRollingBack() {
        // Java's default "smart" resolution turns Feb 30 into Feb 28 without a word.
        assertThrows(FF15Exception.class, () -> TaskTime.parse("2019-02-30 1800"));
        assertThrows(FF15Exception.class, () -> TaskTime.parse("2019-04-31 0900"));
    }

    @Test
    public void parse_impossibleTime_throwsException() {
        assertThrows(FF15Exception.class, () -> TaskTime.parse("2019-12-02 2400"));
        assertThrows(FF15Exception.class, () -> TaskTime.parse("2019-12-02 1260"));
    }

    @Test
    public void parse_leapDayWithTime_isAccepted() throws FF15Exception {
        assertEquals("Feb 29 2020, 9:00am", TaskTime.parse("2020-02-29 0900").toString());
    }

    @Test
    public void isSameMomentAs_dateOnlyAndMidnight_isTrue() throws FF15Exception {
        assertTrue(TaskTime.parse("2019-12-02").isSameMomentAs(TaskTime.parse("2019-12-02 0000")));
    }

    @Test
    public void equals_dateOnlyAndMidnight_isFalse() throws FF15Exception {
        // Same instant, but one shows a time and the other does not.
        assertFalse(TaskTime.parse("2019-12-02").equals(TaskTime.parse("2019-12-02 0000")));
    }

    @Test
    public void equals_sameTextTwice_isTrueWithMatchingHashCode() throws FF15Exception {
        TaskTime a = TaskTime.parse("2019-12-02 1800");
        TaskTime b = TaskTime.parse("2019-12-02 1800");
        assertTrue(a.equals(b));
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void equals_sameInstance_isTrue() throws FF15Exception {
        TaskTime time = TaskTime.parse("2019-12-02");
        assertTrue(time.equals(time));
    }

    @Test
    public void equals_somethingThatIsNotATaskTime_isFalse() throws FF15Exception {
        assertFalse(TaskTime.parse("2019-12-02").equals("2019-12-02"));
    }
}
