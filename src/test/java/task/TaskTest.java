package task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for the base Task class: construction, completion state, signatures and equality.
 */
public class TaskTest {
    @Test
    public void constructor_singleArg_isNotDone() {
        Task task = new Task("read book");
        assertFalse(task.isDone());
        assertEquals("read book", task.getDescription());
    }

    @Test
    public void constructor_withDoneFlag_setsCompletionState() {
        Task task = new Task("read book", true);
        assertTrue(task.isDone());
        assertTrue(task.getCompletionStatus());
    }

    @Test
    public void setDone_updatesCompletionState() {
        Task task = new Task("read book");
        task.setDone(true);
        assertTrue(task.isDone());

        task.setDone(false);
        assertFalse(task.isDone());
    }

    @Test
    public void toSignature_reflectsCompletionState() {
        assertEquals("T|0|read book", new Task("read book").toSignature());
        assertEquals("T|1|read book", new Task("read book", true).toSignature());
    }

    @Test
    public void toString_reflectsCompletionState() {
        assertEquals("[T][ ] read book", new Task("read book").toString());
        assertEquals("[T][X] read book", new Task("read book", true).toString());
    }

    @Test
    public void equals_sameDescriptionIgnoresCompletionState() {
        assertEquals(new Task("read book", false), new Task("read book", true));
    }

    @Test
    public void equals_differentDescriptionIsNotEqual() {
        assertFalse(new Task("read book").equals(new Task("write book")));
    }

    @Test
    public void equals_differentTypeIsNotEqual() {
        assertFalse(new Task("read book").equals("read book"));
    }

    @Test
    public void equals_nullIsNotEqual() {
        assertFalse(new Task("read book").equals(null));
    }

    @Test
    public void equals_sameInstanceIsEqual() {
        Task task = new Task("read book");
        assertTrue(task.equals(task));
    }
}
