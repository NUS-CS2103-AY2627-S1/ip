package peter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Task}.
 */
public class TaskTest {
    @Test
    public void getStatusIcon_newTask_isNotDone() {
        Task task = new Task("read book");
        assertEquals("[ ]", task.getStatusIcon());
    }

    @Test
    public void markAsDone_changesStatusIconToX() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("[X]", task.getStatusIcon());
    }

    @Test
    public void markAsNotDone_afterMarkingDone_revertsStatusIcon() {
        Task task = new Task("read book");
        task.markAsDone();
        task.markAsNotDone();
        assertEquals("[ ]", task.getStatusIcon());
    }

    @Test
    public void getDescription_returnsConstructorValue() {
        Task task = new Task("read book");
        assertEquals("read book", task.getDescription());
    }

    @Test
    public void toFileFormat_notDone_usesZeroMarker() {
        Task task = new Task("read book");
        assertEquals("T | 0 | read book", task.toFileFormat());
    }

    @Test
    public void toFileFormat_done_usesOneMarker() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("T | 1 | read book", task.toFileFormat());
    }

    @Test
    public void toString_notDone_showsEmptyStatusIcon() {
        Task task = new Task("read book");
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    public void toString_done_showsXStatusIcon() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("[X] read book", task.toString());
    }

    @Test
    public void equals_sameDescription_returnsTrue() {
        assertEquals(new Task("read book"), new Task("read book"));
    }

    @Test
    public void equals_differentDescription_returnsFalse() {
        assertFalse(new Task("read book").equals(new Task("return book")));
    }

    @Test
    public void equals_completionStateIgnored_stillEqual() {
        Task done = new Task("read book");
        done.markAsDone();
        Task notDone = new Task("read book");

        assertEquals(done, notDone);
    }

    @Test
    public void equals_differentConcreteType_returnsFalse() {
        // A plain Task and a Todo with the same description are not the same
        // kind of task, even though Todo is-a Task.
        assertFalse(new Task("read book").equals(new Todo("read book")));
    }

    @Test
    public void equals_null_returnsFalse() {
        assertFalse(new Task("read book").equals(null));
    }

    @Test
    public void equals_sameInstance_returnsTrue() {
        Task task = new Task("read book");
        assertTrue(task.equals(task));
    }

    @Test
    public void hashCode_equalTasks_haveSameHashCode() {
        assertEquals(new Task("read book").hashCode(), new Task("read book").hashCode());
    }
}
