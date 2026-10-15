package serina.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests todo state, display, serialization, and copying. */
public class TodoTest {
    @Test
    public void todo_defaultAndMarkedStates_formatCorrectly() {
        Todo todo = new Todo("review | path \\ notes");

        assertEquals(TaskType.TODO, todo.getTaskType());
        assertFalse(todo.isDone());
        assertEquals("[T][ ] review | path \\ notes", todo.toString());
        assertEquals("T | 0 | review \\| path \\\\ notes", todo.toFileString());
        assertFalse(todo.isOccurringOn(LocalDate.of(2026, 1, 1)));

        todo.markAsDone();
        assertTrue(todo.isDone());
        assertEquals("X", todo.getStatusIcon());
    }

    @Test
    public void copy_markingCopy_doesNotChangeOriginal() {
        Todo original = new Todo("inspect engines", TaskStatus.DONE);
        Task copy = original.copy();

        copy.markAsNotDone();

        assertTrue(original.isDone());
        assertFalse(copy.isDone());
        assertEquals(original.getDescription(), copy.getDescription());
    }
}
