package peter.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import peter.exception.PeterException;

public class TaskTest {
    @Test
    public void todo_statusTransitions_updateDisplayAndStorageFormats() {
        Todo todo = new Todo("read book");

        assertEquals("[T][ ] read book", todo.toString());
        assertEquals("T | 0 | read book", todo.toFileFormat());

        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
        assertEquals("T | 1 | read book", todo.toFileFormat());

        todo.markAsUndone();
        assertEquals(" ", todo.getStatusIcon());
    }

    @Test
    public void deadline_dateOnly_usesEndOfDayAndRoundTripsToStorage() throws PeterException {
        Deadline deadline = new Deadline("submit report", "2026-09-10");

        assertEquals(LocalDateTime.of(2026, 9, 10, 23, 59), deadline.getBy());
        assertEquals("D | 0 | submit report | 2026-09-10 2359", deadline.toFileFormat());
    }

    @Test
    public void deadline_invalidLeapDayAndTime_exceptionThrown() {
        assertThrows(PeterException.class, () -> new Deadline("bad date", "2025-02-29 1200"));
        assertThrows(PeterException.class, () -> new Deadline("bad time", "2026-01-01 2460"));
        assertThrows(PeterException.class, () -> new Deadline("bad format", "tomorrow"));
    }

    @Test
    public void event_validRange_formatsForDisplayAndStorage() throws PeterException {
        Event event = new Event("meeting", "0900", "1030");

        assertEquals("[E][ ] meeting (from: 0900 to: 1030)", event.toString());
        assertEquals("E | 0 | meeting | 0900 | 1030", event.toFileFormat());
    }

    @Test
    public void event_nonNumericOrIncompleteTime_exceptionThrown() {
        assertThrows(PeterException.class, () -> new Event("meeting", "9am", "1030"));
        assertThrows(PeterException.class, () -> new Event("meeting", "090", "1030"));
        assertThrows(PeterException.class, () -> new Event("meeting", null, "1030"));
    }

    @Test
    public void hasSameDetails_comparesTypeDescriptionAndSubtypeFields() throws PeterException {
        assertTrue(new Todo("Read Book").hasSameDetails(new Todo("read book")));
        assertFalse(new Todo("read book").hasSameDetails(new Task("read book")));
        assertFalse(new Todo("read book").hasSameDetails(null));
        assertTrue(new Deadline("submit", "2026-09-10 1200")
                .hasSameDetails(new Deadline("SUBMIT", "2026-09-10 1200")));
        assertFalse(new Deadline("submit", "2026-09-10 1200")
                .hasSameDetails(new Deadline("submit", "2026-09-10 1300")));
        assertTrue(new Event("meeting", "0900", "1000")
                .hasSameDetails(new Event("MEETING", "0900", "1000")));
        assertFalse(new Event("meeting", "0900", "1000")
                .hasSameDetails(new Event("meeting", "0900", "1100")));
    }
}
