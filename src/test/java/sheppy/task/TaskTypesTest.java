package sheppy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import sheppy.SheppyException;

/** Covers model validation, serialization and collection boundaries. */
class TaskTypesTest {
    /** Verifies required fields for each task type. */
    @Test
    void constructors_rejectMissingFields() {
        assertThrows(SheppyException.class, () -> new Todo(null));
        assertThrows(SheppyException.class, () -> new Todo(" "));
        assertThrows(SheppyException.class, () -> new Deadline("report", null));
        assertThrows(SheppyException.class, () -> new Event("meeting", null, "end"));
        assertThrows(SheppyException.class, () -> new Event("meeting", "start", " "));
    }

    /** Checks display and storage independently for each type and status. */
    @Test
    void taskTypes_formatAndSerialize() throws SheppyException {
        Task deadline = new Deadline("report", LocalDate.of(2026, 9, 18));
        assertEquals("[D][ ] report (by: Sep 18 2026)", deadline.toString());
        deadline.markAsDone();
        assertEquals("D | 1 | report | 2026-09-18", deadline.toStorageString());
        deadline.markAsUndone();
        assertEquals(" ", deadline.getStatusIcon());
        Task event = new Event("meeting", "Mon 2pm", "4pm");
        assertEquals("E | 0 | meeting | Mon 2pm | 4pm", event.toStorageString());
        assertEquals("[E][ ] meeting (from: Mon 2pm to: 4pm)", event.toString());
    }

    /** Checks sorting ties, duplicate retention and task-number boundaries. */
    @Test
    void taskList_preservesObjectsAndProtectsCollection() throws SheppyException {
        Todo first = new Todo("apple");
        Todo second = new Todo("Apple");
        TaskList tasks = new TaskList(first, second, first);
        tasks.sortByDescription();
        assertEquals(List.of(second, first, first), tasks.asList());
        assertThrows(UnsupportedOperationException.class, () -> tasks.asList().clear());
        assertThrows(SheppyException.class, () -> tasks.remove(-1));
        assertThrows(SheppyException.class, () -> tasks.updateStatus(4, true));
        assertEquals(List.of(), tasks.find("missing"));
        TaskList empty = new TaskList();
        empty.sortByDescription();
        assertEquals(0, empty.size());
        assertThrows(SheppyException.class, () -> empty.get(1));
    }
}
