package xerxes.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests the display representation of {@link ToDo} tasks.
 */
public class ToDoTest {
    /** Verifies that a newly created to-do task is displayed as incomplete. */
    @Test
    void toString_uncompletedTask_returnsUncheckedTodoFormat() {
        ToDo task = new ToDo("Read notes");

        assertEquals("[T][ ] Read notes", task.toString());
    }

    /** Verifies that a completed to-do task is displayed as complete. */
    @Test
    void toString_completedTask_returnsCheckedTodoFormat() {
        ToDo task = new ToDo("Read notes");
        task.markCompleted();

        assertEquals("[T][X] Read notes", task.toString());
    }

    /** Verifies that unmarking a completed task restores its incomplete display state. */
    @Test
    void toString_completedThenUncompletedTask_returnsUncheckedTodoFormat() {
        ToDo task = new ToDo("Read notes");
        task.markCompleted();
        task.markUncompleted();

        assertEquals("[T][ ] Read notes", task.toString());
    }
}
