package jason.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;

/** Tests task state, description, and display behavior. */
class TaskTest {
    @Test
    void task_newTask_isIncompleteAndDisplaysDescription() {
        Task task = new Task("read book") { };

        assertEquals("read book", task.getDescription());
        assertFalse(task.isCompleted());
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    void markComplete_thenMarkIncomplete_updatesCompletionState() {
        Task task = new Task("read book") { };

        task.markComplete();
        assertTrue(task.isCompleted());
        assertEquals("[X] read book", task.toString());

        task.markIncomplete();
        assertFalse(task.isCompleted());
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    void todo_toString_includesTodoType() {
        assertEquals("[T] [ ] read book", new ToDo("read book").toString());
    }

    @Test
    void durationTodo_toStringUsesCanonicalDuration() {
        ToDo todo = new ToDo("read report", Duration.ofMinutes(90));

        assertEquals("[T] [ ] read report (for: 1h 30m)", todo.toString());
        assertEquals(Duration.ofMinutes(90), todo.getDuration());
    }

    @Test
    void durationTodo_invalidDuration_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new ToDo("read report", Duration.ZERO));
        assertThrows(IllegalArgumentException.class, () ->
                new ToDo("read report", Duration.ofHours(25)));
    }
}
