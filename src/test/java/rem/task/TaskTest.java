package rem.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests mutable state and validation shared by all task types.
 */
public class TaskTest {
    @Test
    public void completionStatus_markAndUnmark_statusAndDisplayUpdated() {
        Task task = new Task("read book");

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] read book", task.toString());

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("[X] read book", task.toString());

        task.markAsNotDone();
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void note_setReplaceAndDelete_noteStateAndDisplayUpdated() {
        Task task = new Task("read book");

        assertFalse(task.hasNote());
        assertNull(task.getNote());

        task.setNote("Borrow it from Alice");
        assertTrue(task.hasNote());
        assertEquals("Borrow it from Alice", task.getNote());
        assertEquals("[ ] read book\n  Note: Borrow it from Alice", task.toString());

        task.setNote("Return it tomorrow");
        assertEquals("Return it tomorrow", task.getNote());

        task.deleteNote();
        assertFalse(task.hasNote());
        assertNull(task.getNote());
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    public void setNote_invalidValues_assertionErrorThrown() {
        Task task = new Task("read book");

        assertThrows(AssertionError.class, () -> task.setNote(null));
        assertThrows(AssertionError.class, () -> task.setNote(""));
        assertThrows(AssertionError.class, () -> task.setNote("   "));
        assertThrows(AssertionError.class, () -> task.setNote(" untrimmed"));
        assertThrows(AssertionError.class, () -> task.setNote("untrimmed "));
        assertThrows(AssertionError.class, () -> task.setNote("two\nlines"));
        assertThrows(AssertionError.class, () -> task.setNote("two\rlines"));
        assertThrows(AssertionError.class, () -> task.setNote("a".repeat(Task.MAX_NOTE_LENGTH + 1)));
    }

    @Test
    public void setNote_maximumUnicodeCodePoints_noteAccepted() {
        Task task = new Task("celebrate");
        String note = "😀".repeat(Task.MAX_NOTE_LENGTH);

        task.setNote(note);

        assertEquals(note, task.getNote());
    }
}
