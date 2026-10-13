package arrodes.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


/** Tests common task description, status, mutation, and display behavior. */
class TaskTest {

    /** Returns the description supplied to a new task. */
    @Test
    void getDescription_newTask_returnsDescription() {
        Task task = new Todo("read a book");
        assertEquals("read a book", task.getDescription());
    }

    /** Uses a blank status icon for a new task. */
    @Test
    void getStatusIcon_newTask_returnsSpace() {
        Task task = new Todo("read a book");
        assertEquals(" ", task.getStatusIcon());
    }

    /** Marks an incomplete task as done. */
    @Test
    void markAsDone_incompleteTask_taskIsMarkedDone() {
        Task task = new Todo("read a book");
        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
    }

    /** Reopens a completed task. */
    @Test
    void markAsNotDone_completedTask_taskIsMarkedNotDone() {
        Task task = new Todo("read a book");
        task.markAsDone();
        task.markAsNotDone();
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    /** Associates and retrieves a tag from a task. */
    @Test
    void tagWith_validTag_taskHasTag() {
        Task task = new Todo("read a book");

        task.tagWith("personal");

        assertEquals("personal", task.getTag());
    }

    /** Includes the incomplete status in task text. */
    @Test
    void toString_incompleteTask_containsBlankStatusIcon() {
        Task task = new Todo("read a book");
        assertTrue(task.toString().contains("[ ]"));
    }

    /** Includes the completed status in task text. */
    @Test
    void toString_completedTask_containsXStatusIcon() {
        Task task = new Todo("read a book");
        task.markAsDone();
        assertTrue(task.toString().contains("[X]"));
    }

    /** Includes a task's tag in its display text. */
    @Test
    void toString_taggedTask_containsTag() {
        Task task = new Todo("read a book");
        task.tagWith("personal");

        assertTrue(task.toString().contains("#personal"));
    }

    /** Treats equal todo descriptions as the same task details. */
    @Test
    void hasSameDetailsAs_equalTodos_returnsTrue() {
        assertTrue(new Todo("read a book").hasSameDetailsAs(new Todo("read a book")));
    }

    /** Distinguishes tasks with different types or descriptions. */
    @Test
    void hasSameDetailsAs_differentTypeOrDescription_returnsFalse() {
        Task todo = new Todo("read a book");

        assertFalse(todo.hasSameDetailsAs(new Todo("write a book")));
        assertFalse(todo.hasSameDetailsAs(new Deadline("read a book", java.time.LocalDateTime.now())));
        assertFalse(todo.hasSameDetailsAs(null));
    }

    /** Compares deadline details, including the due date. */
    @Test
    void hasSameDetailsAs_deadlinesWithDifferentDueDates_returnsFalse() {
        java.time.LocalDateTime firstDate = java.time.LocalDateTime.of(2026, 1, 1, 0, 0);
        java.time.LocalDateTime secondDate = firstDate.plusDays(1);

        assertFalse(new Deadline("submit report", firstDate)
                .hasSameDetailsAs(new Deadline("submit report", secondDate)));
    }

    /** Compares event details, including both endpoints. */
    @Test
    void hasSameDetailsAs_eventsWithDifferentEndpoints_returnsFalse() {
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2026, 1, 1, 9, 0);
        java.time.LocalDateTime end = start.plusHours(1);

        assertFalse(new Event("meeting", start, end)
                .hasSameDetailsAs(new Event("meeting", start, end.plusHours(1))));
    }

    /** Rejects null tags rather than changing the existing tag. */
    @Test
    void tagWith_nullTag_throwsIllegalArgumentException() {
        Task task = new Todo("read a book");

        assertThrows(IllegalArgumentException.class, () -> task.tagWith(null));
    }
}
