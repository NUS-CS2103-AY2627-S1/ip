package thomas.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests DeadlineTask.
 * Two of its three methods are worth testing on their own. The occursOn method
 * drops the time of day before comparing, so a deadline at any hour counts as
 * being on that day. The toSaveFormat method writes the due date in the format
 * it is typed in, which keeps the save file readable by the same parser the
 * user's typing goes through.
 */
public class DeadlineTaskTest {

    private static final LocalDate DEC_02 = LocalDate.of(2019, 12, 2);
    private static final LocalDateTime DEC_02_6PM = LocalDateTime.of(2019, 12, 2, 18, 0);

    // ---- display ----

    @Test
    public void toString_notDoneDeadline_showsTagAndDueDate() {
        assertEquals("[D][ ] return book (by: Dec 02 2019, 6:00 PM)",
                new DeadlineTask("return book", DEC_02_6PM).toString());
    }

    @Test
    public void toString_doneDeadline_showsTickedBox() {
        DeadlineTask task = new DeadlineTask("return book", DEC_02_6PM);
        task.markAsDone();

        assertEquals("[D][X] return book (by: Dec 02 2019, 6:00 PM)", task.toString());
    }

    @Test
    public void toString_morningDeadline_showsAmMarker() {
        // The due date is shown in the display format, not the one it was typed in.
        assertEquals("[D][ ] return book (by: Mar 04 2019, 7:45 AM)",
                new DeadlineTask("return book", LocalDateTime.of(2019, 3, 4, 7, 45)).toString());
    }

    // ---- occursOn ----

    @Test
    public void occursOn_dayItIsDue_isTrue() {
        assertTrue(new DeadlineTask("return book", DEC_02_6PM).occursOn(DEC_02));
    }

    @Test
    public void occursOn_dueAtMidnight_isOnThatDay() {
        // The time is dropped, so the first minute of the day still counts as that day.
        DeadlineTask task = new DeadlineTask("return book", DEC_02.atTime(0, 0));

        assertTrue(task.occursOn(DEC_02));
    }

    @Test
    public void occursOn_dueAtLastMinute_isOnThatDay() {
        // And so does the last, which comparing moments rather than days would miss.
        DeadlineTask task = new DeadlineTask("return book", DEC_02.atTime(23, 59));

        assertTrue(task.occursOn(DEC_02));
    }

    @Test
    public void occursOn_dayBefore_isFalse() {
        assertFalse(new DeadlineTask("return book", DEC_02_6PM).occursOn(DEC_02.minusDays(1)));
    }

    @Test
    public void occursOn_dayAfter_isFalse() {
        assertFalse(new DeadlineTask("return book", DEC_02_6PM).occursOn(DEC_02.plusDays(1)));
    }

    @Test
    public void occursOn_sameDayDifferentYear_isFalse() {
        // The same day a year earlier is a different day, not a match on month and day.
        assertFalse(new DeadlineTask("return book", DEC_02_6PM).occursOn(DEC_02.minusYears(1)));
    }

    // ---- save format ----

    @Test
    public void toSaveFormat_notDoneDeadline_writesTagFlagDescriptionAndDate() {
        assertEquals("D | 0 | return book | 2019-12-02 1800",
                new DeadlineTask("return book", DEC_02_6PM).toSaveFormat());
    }

    @Test
    public void toSaveFormat_doneDeadline_writesTheDoneFlag() {
        DeadlineTask task = new DeadlineTask("return book", DEC_02_6PM);
        task.markAsDone();

        assertEquals("D | 1 | return book | 2019-12-02 1800", task.toSaveFormat());
    }

    @Test
    public void toSaveFormat_morningDeadline_writesTwentyFourHourTime() {
        // The saved date is written in the format the parser reads back.
        assertEquals("D | 0 | return book | 2019-03-04 0745",
                new DeadlineTask("return book", LocalDateTime.of(2019, 3, 4, 7, 45)).toSaveFormat());
    }
}
