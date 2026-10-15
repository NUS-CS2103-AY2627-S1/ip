package magnus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests to-do formatting and detail comparison.
 */
public class ToDoTaskTest {
    @Test
    public void toString_incompleteAndCompletedTask_usesTodoMarker() {
        ToDoTask task = new ToDoTask("read book");

        assertEquals("[T][ ] read book", task.toString());

        task.markAsDone();
        assertEquals("[T][X] read book", task.toString());
    }

    @Test
    public void hasSameDetails_matchingDescription_returnsTrue() {
        assertTrue(new ToDoTask("read book").hasSameDetails(new ToDoTask("read book")));
    }

    @Test
    public void hasSameDetails_nullDifferentDescriptionOrType_returnsFalse() {
        ToDoTask task = new ToDoTask("read book");

        assertFalse(task.hasSameDetails(null));
        assertFalse(task.hasSameDetails(new ToDoTask("write notes")));
        assertFalse(task.hasSameDetails(new DeadlineTask("read book", "20/09/2026 1730")));
    }
}
