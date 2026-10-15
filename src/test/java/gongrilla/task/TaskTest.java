package gongrilla.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests task construction, completion state, display, and storage formatting.
 */
class TaskTest {
    @Test
    void constructor_validDescription_storesDescriptionAndStartsIncomplete() {
        Task task = new Task("read book");

        assertEquals("read book", task.getName());
        assertFalse(task.isDone());
        assertEquals("[ ]", task.getIsDoneStatus());
    }

    @Test
    void constructor_nullDescription_throwsNullPointerException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                new Task(null));

        assertEquals("Task description cannot be null.", exception.getMessage());
    }

    @Test
    void constructor_blankDescriptions_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Task(""));
        assertThrows(IllegalArgumentException.class, () -> new Task("   "));
        assertThrows(IllegalArgumentException.class, () -> new Task("\t\n"));
    }

    @Test
    void markDone_incompleteTask_marksTaskComplete() {
        Task task = new Task("read book");

        task.markDone();

        assertTrue(task.isDone());
        assertEquals("[X]", task.getIsDoneStatus());
    }

    @Test
    void markDone_alreadyCompleteTask_remainsComplete() {
        Task task = new Task("read book");
        task.markDone();

        task.markDone();

        assertTrue(task.isDone());
        assertEquals("[X]", task.getIsDoneStatus());
    }

    @Test
    void unmarkDone_completeTask_marksTaskIncomplete() {
        Task task = new Task("read book");
        task.markDone();

        task.unmarkDone();

        assertFalse(task.isDone());
        assertEquals("[ ]", task.getIsDoneStatus());
    }

    @Test
    void unmarkDone_alreadyIncompleteTask_remainsIncomplete() {
        Task task = new Task("read book");

        task.unmarkDone();

        assertFalse(task.isDone());
        assertEquals("[ ]", task.getIsDoneStatus());
    }

    @Test
    void toString_incompleteTask_returnsIncompleteDisplay() {
        Task task = new Task("read book");

        assertEquals("[ ] read book", task.toString());
    }

    @Test
    void toString_completeTask_returnsCompleteDisplay() {
        Task task = new Task("read book");
        task.markDone();

        assertEquals("[X] read book", task.toString());
    }

    @Test
    void toDataString_newTask_returnsIncompleteRecord() {
        Task task = new Task("read book");

        assertEquals("T2 | 0 | read+book", task.toDataString());
    }

    @Test
    void toDataString_markedTask_returnsCompleteRecord() {
        Task task = new Task("read book");
        task.markDone();

        assertEquals("T2 | 1 | read+book", task.toDataString());
    }

    @Test
    void toDataString_unmarkedTask_returnsIncompleteRecord() {
        Task task = new Task("read book");
        task.markDone();
        task.unmarkDone();

        assertEquals("T2 | 0 | read+book", task.toDataString());
    }

    @Test
    void toDataString_descriptionWithFileDelimiters_encodesDescription() {
        Task task = new Task("read | notes%\nnext line");

        assertEquals("T2 | 0 | read+%7C+notes%25%0Anext+line", task.toDataString());
    }
}
