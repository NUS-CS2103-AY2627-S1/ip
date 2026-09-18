package meowmeow.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import meowmeow.MeowmeowException;

/**
 * Tests for {@link Deadline} - specifically {@link Deadline#occursOn(LocalDate)}
 * (the date match behind "list &lt;date&gt;") and the two rendering methods
 * that append the due date.
 */
public class DeadlineTest {

    private static Deadline deadline(String description, String by) throws MeowmeowException {
        return new Deadline(description, TaskDateTime.parse(by));
    }

    // ---- occursOn ----

    @Test
    public void occursOn_sameDate_true() throws MeowmeowException {
        assertTrue(deadline("return book", "2/12/2019").occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void occursOn_differentDate_false() throws MeowmeowException {
        assertFalse(deadline("return book", "2/12/2019").occursOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    public void occursOn_dueDateTimeMatchesByDay_ignoresTime() throws MeowmeowException {
        // The deadline carries a time, but the filter matches on the day.
        assertTrue(deadline("submit", "2/12/2019 1800").occursOn(LocalDate.of(2019, 12, 2)));
    }

    // ---- toString ----

    @Test
    public void toString_notDoneWithTime_showsTagStatusDescriptionAndDueDate() throws MeowmeowException {
        assertEquals("[D][ ] return book (by: Dec 2 2019, 6:00 pm)",
                deadline("return book", "2/12/2019 1800").toString());
    }

    @Test
    public void toString_dateOnly_showsDateWithoutTime() throws MeowmeowException {
        assertEquals("[D][ ] return book (by: Dec 2 2019)",
                deadline("return book", "2/12/2019").toString());
    }

    @Test
    public void toString_done_showsDoneIcon() throws MeowmeowException {
        Deadline d = deadline("return book", "2/12/2019");
        d.setStatus(TaskStatus.DONE);

        assertEquals("[D][X] return book (by: Dec 2 2019)", d.toString());
    }

    // ---- toFileString ----

    @Test
    public void toFileString_notDoneWithTime_pipeSeparatedWithCanonicalDate() throws MeowmeowException {
        assertEquals("D | 0 | return book | 2019-12-02 1800",
                deadline("return book", "2/12/2019 1800").toFileString());
    }

    @Test
    public void toFileString_done_usesDoneFlag() throws MeowmeowException {
        Deadline d = deadline("return book", "2/12/2019");
        d.setStatus(TaskStatus.DONE);

        assertEquals("D | 1 | return book | 2019-12-02", d.toFileString());
    }

    // ---- priority ----

    @Test
    public void toString_priorityHigh_suffixComesAfterDueDate() throws MeowmeowException {
        Deadline d = deadline("return book", "2/12/2019");
        d.setPriority(TaskPriority.HIGH);

        assertEquals("[D][ ] return book (by: Dec 2 2019) (priority: HIGH)", d.toString());
    }

    @Test
    public void toFileString_priorityLow_fieldComesAfterDueDate() throws MeowmeowException {
        Deadline d = deadline("return book", "2/12/2019");
        d.setPriority(TaskPriority.LOW);

        assertEquals("D | 0 | return book | 2019-12-02 | LOW", d.toFileString());
    }
}
