package alexa;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests state, searching, and identity behavior shared by task types. */
class TaskTest {
    @Test
    void markAndUnmark_taskDisplayReflectsCurrentStatus() {
        Todo task = new Todo("read book");

        task.markAsDone();
        assertTrue(task.toString().contains("[X]"));
        assertTrue(task.toStorageString().contains(" | 1 | "));

        task.unmark();
        assertTrue(task.toString().contains("[ ]"));
        assertTrue(task.toStorageString().contains(" | 0 | "));
    }

    @Test
    void hasDescriptionContaining_caseInsensitiveKeyword_returnsExpectedResult() {
        Task task = new Todo("Read Book");

        assertTrue(task.hasDescriptionContaining("book"));
        assertTrue(task.hasDescriptionContaining("READ"));
        assertFalse(task.hasDescriptionContaining("meeting"));
    }

    @Test
    void hasSameIdentity_todosWithMatchingDescriptions_returnsTrue() {
        Task firstTodo = new Todo("read book");
        Task matchingTodo = new Todo("read book");

        assertTrue(firstTodo.hasSameIdentity(matchingTodo));
        assertFalse(firstTodo.hasSameIdentity(new Todo("return book")));
        assertFalse(firstTodo.hasSameIdentity(null));
        assertFalse(firstTodo.hasSameIdentity(new Deadline("read book", LocalDate.of(2026, 10, 15))));
    }

    @Test
    void hasSameIdentity_deadlinesAndEventsCompareApplicableDates() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 10, 15));
        Event event = new Event("meeting", LocalDate.of(2026, 10, 15), LocalDate.of(2026, 10, 16));

        assertTrue(deadline.hasSameIdentity(new Deadline("return book", LocalDate.of(2026, 10, 15))));
        assertFalse(deadline.hasSameIdentity(new Deadline("return book", LocalDate.of(2026, 10, 16))));
        assertTrue(event.hasSameIdentity(new Event("meeting", LocalDate.of(2026, 10, 15),
                LocalDate.of(2026, 10, 16))));
        assertFalse(event.hasSameIdentity(new Event("meeting", LocalDate.of(2026, 10, 15),
                LocalDate.of(2026, 10, 17))));
    }
}