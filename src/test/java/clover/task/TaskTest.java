package clover.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests the completion state of a task. */
class TaskTest {

    @Test
    void markAsDone_newTodo_taskMarkedAndDisplayedAsDone() {
        Task task = new ToDo("read book");

        task.markAsDone();

        assertTrue(task.isDone());
        assertEquals("[T] [X] read book", task.toString());
    }

    @Test
    void markAsUndone_completedTask_taskUnmarkedAndDisplayedAsIncomplete() {
        Task task = new ToDo("read book");
        task.markAsDone();

        task.markAsUndone();

        assertFalse(task.isDone());
        assertEquals("[T] [ ] read book", task.toString());
    }

    @Test
    void constructor_nullDescription_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new ToDo(null));
    }

    @Test
    void toString_datedTasksWithTutoree_displaysAllDetails() {
        assertEquals("[D] [ ] submit report (by: Sep 1 2026) (for: Alice Tan)",
                new Deadline("submit report", LocalDate.of(2026, 9, 1), "Alice Tan").toString());
        assertEquals("[E] [ ] meeting (from: Sep 1 2026 to: Sep 2 2026) (for: Bob Lim)",
                new Event("meeting", LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 2), "Bob Lim").toString());
    }

    @Test
    void hasSameDetails_nullAndDifferentDatesOrTutorees_falseReturned() {
        Deadline deadline = new Deadline("submit", LocalDate.of(2026, 9, 1), "Alice");

        assertFalse(deadline.hasSameDetails(null));
        assertFalse(deadline.hasSameDetails(new Deadline("submit", LocalDate.of(2026, 9, 2), "Alice")));
        assertFalse(deadline.hasSameDetails(new Deadline("submit", LocalDate.of(2026, 9, 1), "Bob")));
        assertFalse(new Event("meeting", LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 2))
                .hasSameDetails(new Event("meeting", LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 3))));
    }
}
