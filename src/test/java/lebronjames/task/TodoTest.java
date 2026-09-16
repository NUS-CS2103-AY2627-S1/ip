package lebronjames.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import lebronjames.LebronJamesException;

/**
 * Tests {@link Todo}, and through it the completion handling that {@link Task}
 * provides to every kind of task.
 */
public class TodoTest {
    @Test
    public void toString_newTodo_shownAsNotDone() {
        assertEquals("[T][ ] read book", new Todo("read book").toString());
    }

    @Test
    public void toString_afterMarkAsDone_shownAsDone() {
        Todo todo = new Todo("read book");

        todo.markAsDone();

        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void markAsNotDone_previouslyDone_backToNotDone() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        todo.markAsNotDone();

        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void markAsDone_calledTwice_stillDone() {
        Todo todo = new Todo("read book");

        todo.markAsDone();
        todo.markAsDone();

        assertEquals("X", todo.getStatusIcon());
    }

    @Test
    public void toFileFormat_notDone_threeFieldsWithZeroFlag() {
        assertEquals("T | 0 | read book", new Todo("read book").toFileFormat());
    }

    @Test
    public void toFileFormat_done_flagIsOne() {
        Todo todo = new Todo("read book");

        todo.markAsDone();

        assertEquals("T | 1 | read book", todo.toFileFormat());
    }

    @Test
    public void isOn_anyDate_false() {
        // A to-do carries no date, so it belongs to no particular day.
        assertFalse(new Todo("read book").isOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void equals_sameDescription_equalWithMatchingHashCode() {
        assertEquals(new Todo("read book"), new Todo("read book"));
        assertEquals(new Todo("read book").hashCode(), new Todo("read book").hashCode());
    }

    @Test
    public void equals_descriptionDifferingOnlyInCase_equal() {
        // Someone retyping a task rarely reproduces their own capitalisation.
        assertEquals(new Todo("read book"), new Todo("Read Book"));
        assertEquals(new Todo("read book").hashCode(), new Todo("READ BOOK").hashCode());
    }

    @Test
    public void equals_differentDescription_notEqual() {
        assertNotEquals(new Todo("read book"), new Todo("read newspaper"));
    }

    @Test
    public void equals_completionStatusDiffers_stillEqual() {
        // Ticking a task off does not make room for a second copy of it.
        Todo done = new Todo("read book");
        done.markAsDone();

        assertEquals(new Todo("read book"), done);
    }

    @Test
    public void equals_todoAndDeadlineWithSameDescription_notEqual() throws LebronJamesException {
        // Same words, different kind of task, so not a duplicate.
        Deadline deadline = new Deadline("read book", TaskDateTime.parse("2019-12-02"));

        assertNotEquals(new Todo("read book"), deadline);
        assertNotEquals(deadline, new Todo("read book"));
    }

    @Test
    public void equals_null_notEqual() {
        assertFalse(new Todo("read book").equals(null));
        assertTrue(new Todo("read book").equals(new Todo("read book")));
    }
}
