package dobby.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import dobby.exception.DobbyException;

/** Tests task persistence records and completion status. */
class TaskTest {
    @Test
    void constructor_blankDescription_assertionError() {
        assertThrows(AssertionError.class, () -> new ToDo(" "));
    }

    @Test
    void fromFileString_incompleteTodo_restoresTodo() throws DobbyException {
        Task task = Task.fromFileString("T | 0 | read book");

        assertInstanceOf(ToDo.class, task);
        assertEquals("[T][ ] read book", task.toString());
        assertEquals("T | 0 | read book", task.toFileString());
    }

    @Test
    void fromFileString_completedTodo_restoresCompletionStatus() throws DobbyException {
        Task task = Task.fromFileString("T | 1 | read book");

        assertEquals("[T][X] read book", task.toString());
        assertEquals("T | 1 | read book", task.toFileString());
    }

    @Test
    void fromFileString_deadlineWithTime_restoresDeadline() throws DobbyException {
        Task task = Task.fromFileString("D | 0 | return book | 2019-12-02 1800");

        assertInstanceOf(Deadline.class, task);
        assertEquals("[D][ ] return book (by: Dec 02 2019, 18:00)", task.toString());
        assertEquals("D | 0 | return book | 2019-12-02 1800", task.toFileString());
    }

    @Test
    void fromFileString_eventWithDates_restoresEvent() throws DobbyException {
        Task task = Task.fromFileString("E | 1 | project meeting | 2019-12-03 | 2019-12-04");

        assertInstanceOf(Event.class, task);
        assertEquals("[E][X] project meeting (from: Dec 03 2019 to: Dec 04 2019)", task.toString());
        assertEquals("E | 1 | project meeting | 2019-12-03 | 2019-12-04", task.toFileString());
    }

    @Test
    void fromFileString_eventEndingBeforeStart_exceptionThrown() {
        String record = "E | 0 | reversed | 2026-09-22 1600 | 2026-09-22 1400";

        assertInvalidRecord(record, "Invalid saved event order: " + record);
    }

    @Test
    void markDoneAndMarkNotDone_changeStatusAndStoredStatus() {
        Task task = new ToDo("read book");

        task.markDone();
        assertEquals("X", task.getStatusIcon());
        assertEquals("T | 1 | read book", task.toFileString());

        task.markNotDone();
        assertEquals(" ", task.getStatusIcon());
        assertEquals("T | 0 | read book", task.toFileString());
    }

    @Test
    void fromFileString_invalidStatus_exceptionThrown() {
        assertInvalidRecord("T | 2 | read book", "Invalid saved task: T | 2 | read book");
    }

    @Test
    void fromFileString_nullRecord_assertionError() {
        assertThrows(AssertionError.class, () -> Task.fromFileString(null));
    }

    @Test
    void fromFileString_missingDescription_exceptionThrown() {
        assertInvalidRecord("T | 0 | ", "Invalid saved task: T | 0 | ");
    }

    @Test
    void fromFileString_wrongFieldCount_exceptionThrown() {
        assertInvalidRecord("T | 0 | read book | extra", "Invalid saved todo: T | 0 | read book | extra");
    }

    @Test
    void fromFileString_unknownType_exceptionThrown() {
        assertInvalidRecord("X | 0 | read book", "Unknown saved task type: X");
    }

    @Test
    void fromFileString_invalidSavedDate_exceptionThrown() {
        assertInvalidRecord("D | 0 | return book | 2019-02-29",
                "Invalid saved date: D | 0 | return book | 2019-02-29");
    }

    /** Verifies the message for a malformed task persistence record. */
    private void assertInvalidRecord(String record, String expectedMessage) {
        DobbyException exception = assertThrows(DobbyException.class, () -> Task.fromFileString(record));

        assertEquals("> " + expectedMessage, exception.getMessage());
    }
}
