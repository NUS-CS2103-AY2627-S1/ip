package rudra.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import rudra.exception.RudraException;

/**
 * Tests for {@link Task}.
 */
public class TaskTest {
    @Test
    public void constructor_missingTaskType_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Task(null, "read book"));
    }

    @Test
    public void constructor_blankDescription_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new ToDo(" "));
    }

    @Test
    public void deadlineConstructor_missingDueDate_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Deadline("return book", null));
    }

    @Test
    public void eventConstructor_missingStartDate_throwsAssertionError() throws RudraException {
        TaskDateTime endDate = TaskDateTime.parse("2026-08-28 1600");

        assertThrows(AssertionError.class, () -> new Event("project sync", null, endDate));
    }

    @Test
    public void matchesKeyword_descriptionContainsKeyword_returnsTrue() {
        Task task = new ToDo("return book to library");

        assertTrue(task.matchesKeyword("book"));
    }

    @Test
    public void matchesKeyword_differentLetterCase_returnsTrue() {
        Task task = new ToDo("Read Book");

        assertTrue(task.matchesKeyword("book"));
    }

    @Test
    public void matchesKeyword_descriptionDoesNotContainKeyword_returnsFalse() {
        Task task = new ToDo("submit assignment");

        assertFalse(task.matchesKeyword("book"));
    }

    @Test
    public void toStorageString_taskTypes_returnsFieldsInStorageOrder() throws RudraException {
        Task todo = new ToDo("borrow book");
        Task deadline = new Deadline("return book", TaskDateTime.parse("2026-08-28"));
        Task event = new Event("project sync", TaskDateTime.parse("2026-08-28 1400"),
                TaskDateTime.parse("2026-08-28 1600"));

        assertEquals("T | 0 | borrow book", todo.toStorageString());
        assertEquals("D | 0 | return book | 2026-08-28", deadline.toStorageString());
        assertEquals("E | 0 | project sync | 2026-08-28 1400 | 2026-08-28 1600", event.toStorageString());
    }
}
