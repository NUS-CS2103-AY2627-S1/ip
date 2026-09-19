package yuno.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void constructor_nullOrBlankDescription_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Todo(null, false));
        assertThrows(AssertionError.class, () -> new Todo(" ", false));
    }

    @Test
    void markDone_incompleteTask_setsCompletedStatus() {
        Task task = new Todo("read book", false);

        task.markDone();

        assertEquals('X', task.getStatus());
    }

    @Test
    void unmarkDone_completedTask_setsIncompleteStatus() {
        Task task = new Todo("read book", true);

        task.unmarkDone();

        assertEquals(' ', task.getStatus());
    }

    @Test
    void toString_task_returnsStatusAndDescription() {
        Task task = new Todo("read book", true);

        assertEquals("[T][X] read book", task.toString());
    }

    @Test
    void containsText_descriptionContainsLowercaseSearchText_returnsTrue() {
        Task task = new Todo("Submit PROJECT report", false);

        assertTrue(task.containsText("project"));
    }

    @Test
    void containsText_descriptionDoesNotContainSearchText_returnsFalse() {
        Task task = new Todo("read book", false);

        assertFalse(task.containsText("project"));
    }
}
