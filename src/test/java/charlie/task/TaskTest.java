package charlie.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import charlie.command.UpdateField;
import charlie.exception.CharlieException;

public class TaskTest {
    @Test
    public void todo_statusAndSerialization_returnsExpectedRepresentations() {
        Todo todo = new Todo("borrow book", false);

        assertEquals("[T][ ] borrow book", todo.toString());
        assertEquals("T | Not done | borrow book", todo.saveFileFormat());

        todo.markDone();
        assertEquals("[T][X] borrow book", todo.toString());
        assertEquals("T | Done | borrow book", todo.saveFileFormat());

        todo.markUndone();
        assertEquals("[T][ ] borrow book", todo.toString());
    }

    @Test
    public void todo_copyAndUpdate_returnsIndependentTodos() {
        Todo original = new Todo("borrow book", true);

        Task statusCopy = original.copyWithStatus(false);
        Task updatedCopy = original.createUpdatedTask(UpdateField.DESCRIPTION, "return book");

        assertNotSame(original, statusCopy);
        assertEquals("[T][ ] borrow book", statusCopy.toString());
        assertNotSame(original, updatedCopy);
        assertEquals("[T][X] return book", updatedCopy.toString());
        assertEquals("[T][X] borrow book", original.toString());
    }

    @Test
    public void todo_updateDateField_exceptionThrown() {
        Todo todo = new Todo("borrow book", false);

        CharlieException exception = assertThrows(
                CharlieException.class, () -> todo.createUpdatedTask(
                        UpdateField.DEADLINE, "2026-09-20"));

        assertEquals("A todo only has a description field.", exception.getMessage());
    }

    @Test
    public void deadline_displaySerializationAndCopies_preserveFields() {
        Deadline deadline = new Deadline("submit report", true, LocalDate.of(2026, 9, 20));

        assertEquals("[D][X] submit report (by: Sep 20 2026)", deadline.toString());
        assertEquals("D | Done | submit report | 2026-09-20", deadline.saveFileFormat());
        assertEquals(
                "[D][ ] submit report (by: Sep 20 2026)",
                deadline.copyWithStatus(false).toString());
        assertEquals(
                "[D][X] revised report (by: Sep 20 2026)",
                deadline.createUpdatedTask(UpdateField.DESCRIPTION, "revised report").toString());
        assertEquals(
                "[D][X] submit report (by: Sep 21 2026)",
                deadline.createUpdatedTask(UpdateField.DEADLINE, "2026-09-21").toString());
    }

    @Test
    public void deadline_updateInvalidOrUnsupportedField_exceptionThrown() {
        Deadline deadline = new Deadline("submit report", false, LocalDate.of(2026, 9, 20));

        CharlieException invalidDate = assertThrows(
                CharlieException.class, () -> deadline.createUpdatedTask(
                        UpdateField.DEADLINE, "2026-02-30"));
        CharlieException unsupportedField = assertThrows(
                CharlieException.class, () -> deadline.createUpdatedTask(
                        UpdateField.FROM, "2026-09-20 0900"));

        assertEquals("Deadline date must use the yyyy-MM-dd format.", invalidDate.getMessage());
        assertEquals("A deadline does not have from or to fields.", unsupportedField.getMessage());
    }

    @Test
    public void event_displaySerializationAndCopies_preserveFields() {
        Event event = createEvent(true);

        assertEquals(
                "[E][X] meeting (from: Sep 21 2026, 2:00 PM to: Sep 21 2026, 4:00 PM)",
                event.toString());
        assertEquals(
                "E | Done | meeting | 2026-09-21T14:00 | 2026-09-21T16:00",
                event.saveFileFormat());
        assertEquals(
                "[E][ ] meeting (from: Sep 21 2026, 2:00 PM to: Sep 21 2026, 4:00 PM)",
                event.copyWithStatus(false).toString());
        assertEquals(
                "[E][X] review (from: Sep 21 2026, 2:00 PM to: Sep 21 2026, 4:00 PM)",
                event.createUpdatedTask(UpdateField.DESCRIPTION, "review").toString());
    }

    @Test
    public void event_updateStartAndEnd_returnsChronologicalCopies() {
        Event event = createEvent(false);

        Task earlierStart = event.createUpdatedTask(UpdateField.FROM, "2026-09-21 1300");
        Task laterEnd = event.createUpdatedTask(UpdateField.TO, "2026-09-21 1700");

        assertTrue(earlierStart.toString().contains("1:00 PM to: Sep 21 2026, 4:00 PM"));
        assertTrue(laterEnd.toString().contains("2:00 PM to: Sep 21 2026, 5:00 PM"));
        assertEquals(
                "[E][ ] meeting (from: Sep 21 2026, 2:00 PM to: Sep 21 2026, 4:00 PM)",
                event.toString());
    }

    @Test
    public void event_updateInvalidRangesOrFormats_exceptionThrown() {
        Event event = createEvent(false);

        CharlieException lateStart = assertThrows(
                CharlieException.class, () -> event.createUpdatedTask(
                        UpdateField.FROM, "2026-09-21 1600"));
        CharlieException earlyEnd = assertThrows(
                CharlieException.class, () -> event.createUpdatedTask(
                        UpdateField.TO, "2026-09-21 1400"));
        CharlieException invalidFormat = assertThrows(
                CharlieException.class, () -> event.createUpdatedTask(
                        UpdateField.FROM, "tomorrow"));
        CharlieException unsupportedField = assertThrows(
                CharlieException.class, () -> event.createUpdatedTask(
                        UpdateField.DEADLINE, "2026-09-21"));

        assertEquals("Event end must be after its start.", lateStart.getMessage());
        assertEquals("Event end must be after its start.", earlyEnd.getMessage());
        assertEquals("Event dates must use the yyyy-MM-dd HHmm format.", invalidFormat.getMessage());
        assertEquals("An event does not have a deadline field.", unsupportedField.getMessage());
    }

    private Event createEvent(boolean isDone) {
        return new Event(
                "meeting",
                isDone,
                LocalDateTime.of(2026, 9, 21, 14, 0),
                LocalDateTime.of(2026, 9, 21, 16, 0));
    }
}
