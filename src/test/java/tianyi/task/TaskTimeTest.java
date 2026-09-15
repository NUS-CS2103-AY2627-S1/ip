package tianyi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests date parsing, comparison, persistence, and display behavior of {@link TaskTime}.
 */
public class TaskTimeTest {
    @Test
    public void constructor_dateOnly_storesDateWithoutTime() {
        TaskTime taskTime = new TaskTime("2-12-2019");

        assertFalse(taskTime.hasTime());
        assertEquals("2-12-2019", taskTime.getData());
    }

    @Test
    public void constructor_dateAndTime_storesBothValues() {
        TaskTime taskTime = new TaskTime("2-12-2019 18:05");

        assertTrue(taskTime.hasTime());
        assertEquals("2-12-2019 18:05", taskTime.getData());
    }

    @Test
    public void constructor_surroundingAndRepeatedWhitespace_parsesSuccessfully() {
        TaskTime taskTime = new TaskTime("  2-12-2019    08:05  ");

        assertEquals("2-12-2019 08:05", taskTime.getData());
    }

    @Test
    public void constructor_invalidDateOrTime_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () -> new TaskTime(""));
        assertThrows(DateTimeParseException.class, () -> new TaskTime("2019-12-2"));
        assertThrows(DateTimeParseException.class, () -> new TaskTime("31-2-2019"));
        assertThrows(DateTimeParseException.class, () -> new TaskTime("2-12-2019 24:00"));
        assertThrows(DateTimeParseException.class, () -> new TaskTime("2-12-2019 1800"));
    }

    @Test
    public void isBefore_earlierEqualAndLaterDates_returnsExpectedResults() {
        TaskTime reference = new TaskTime("2-12-2019");

        assertTrue(new TaskTime("1-12-2019").isBefore(reference));
        assertFalse(new TaskTime("2-12-2019").isBefore(reference));
        assertFalse(new TaskTime("3-12-2019").isBefore(reference));
    }

    @Test
    public void isAfter_earlierEqualAndLaterDates_returnsExpectedResults() {
        TaskTime reference = new TaskTime("2-12-2019");

        assertFalse(new TaskTime("1-12-2019").isAfter(reference));
        assertFalse(new TaskTime("2-12-2019").isAfter(reference));
        assertTrue(new TaskTime("3-12-2019").isAfter(reference));
    }

    @Test
    public void compare_sameDateWithDifferentTimes_datesAreEqual() {
        TaskTime morning = new TaskTime("2-12-2019 09:00");
        TaskTime evening = new TaskTime("2-12-2019 18:00");

        assertFalse(morning.isBefore(evening));
        assertFalse(evening.isAfter(morning));
    }

    @Test
    public void isStrictlyBefore_differentDatesAndTimes_returnsExpectedResults() {
        assertTrue(new TaskTime("1-12-2019").isStrictlyBefore(new TaskTime("2-12-2019")));
        assertFalse(new TaskTime("3-12-2019").isStrictlyBefore(new TaskTime("2-12-2019")));
        assertTrue(new TaskTime("2-12-2019 09:00")
                .isStrictlyBefore(new TaskTime("2-12-2019 18:00")));
        assertFalse(new TaskTime("2-12-2019 18:00")
                .isStrictlyBefore(new TaskTime("2-12-2019 09:00")));
        assertFalse(new TaskTime("2-12-2019 09:00")
                .isStrictlyBefore(new TaskTime("2-12-2019 09:00")));
        assertFalse(new TaskTime("2-12-2019")
                .isStrictlyBefore(new TaskTime("2-12-2019 18:00")));
    }

    @Test
    public void toString_dateAndDifferentTimes_formatsForDisplay() {
        assertEquals("Mon, Dec 02 2019", new TaskTime("2-12-2019").toString());
        assertEquals("Mon, Dec 02 2019, 12:00 AM", new TaskTime("2-12-2019 00:00").toString());
        assertEquals("Mon, Dec 02 2019, 12:00 PM", new TaskTime("2-12-2019 12:00").toString());
        assertEquals("Mon, Dec 02 2019, 6:05 PM", new TaskTime("2-12-2019 18:05").toString());
    }
}
