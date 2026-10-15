package yapbot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class DeadlineTest {

    @Test
    public void toFileFormat_returnsIsoFormattedDateAppended() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));

        assertEquals("D | 0 | return book | 2019-10-15", deadline.toFileFormat());
    }

    @Test
    public void getFormattedBy_returnsHumanReadableDate() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));

        assertEquals("Oct 15 2019", deadline.getFormattedBy());
    }

    @Test
    public void toFileFormat_doneTask_statusFieldReflectsDone() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        deadline.markAsDone();

        assertEquals("D | 1 | return book | 2019-10-15", deadline.toFileFormat());
    }

    @Test
    public void toString_notDone_showsHumanReadableDate() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));

        assertEquals("[D][ ]return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void toString_done_showsXMarkAndHumanReadableDate() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        deadline.markAsDone();

        assertEquals("[D][X]return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void toFileFormat_prioritySet_appendsPriorityAfterDate() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        deadline.setPriority(Priority.LOW);

        assertEquals("D | 0 | return book | 2019-10-15 | LOW", deadline.toFileFormat());
    }

    @Test
    public void toString_prioritySet_showsPriorityTagBeforeDescription() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        deadline.setPriority(Priority.LOW);

        assertEquals("[D][ ][LOW]return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void isDuplicateOf_sameDescriptionAndDate_returnsTrue() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        Deadline other = new Deadline("return book", LocalDate.of(2019, 10, 15));

        assertTrue(deadline.isDuplicateOf(other));
    }

    @Test
    public void isDuplicateOf_sameDescriptionDifferentDate_returnsFalse() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        Deadline other = new Deadline("return book", LocalDate.of(2019, 10, 16));

        assertFalse(deadline.isDuplicateOf(other));
    }

    @Test
    public void isDuplicateOf_sameDetailsButTodo_returnsFalse() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        Todo other = new Todo("return book");

        assertFalse(deadline.isDuplicateOf(other));
    }

    @Test
    public void isOverdue_dateInPast_returnsTrue() {
        Deadline deadline = new Deadline("return book", LocalDate.now().minusDays(1));

        assertTrue(deadline.isOverdue());
    }

    @Test
    public void isOverdue_dateIsToday_returnsFalse() {
        Deadline deadline = new Deadline("return book", LocalDate.now());

        assertFalse(deadline.isOverdue());
    }

    @Test
    public void isOverdue_dateInFuture_returnsFalse() {
        Deadline deadline = new Deadline("return book", LocalDate.now().plusDays(1));

        assertFalse(deadline.isOverdue());
    }
}
