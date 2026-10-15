package neil.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import neil.exception.NeilException;

/**
 * Tests for task state changes and reconstruction from saved representations.
 */
class TaskTest {

    @Test
    void toString_newTask_taskShownAsNotDone() {
        Task task = new ToDoTask("read book");

        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    void markAsDone_taskNotDone_taskShownAsDone() {
        Task task = new ToDoTask("read book");

        task.markAsDone();

        assertEquals("[T][X] read book", task.toString());
    }

    @Test
    void unmark_taskDone_taskShownAsNotDone() {
        Task task = new ToDoTask("read book");
        task.markAsDone();

        task.unmark();

        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    void hasKeyword_keywordInDescription_returnsTrue() {
        Task task = new ToDoTask("Read library book");

        assertTrue(task.hasKeyword("read"));
        assertTrue(task.hasKeyword("BOOK"));
        assertTrue(task.hasKeyword("library book"));
    }

    @Test
    void hasKeyword_keywordNotInDescription_returnsFalse() {
        Task task = new ToDoTask("read book");

        assertFalse(task.hasKeyword("report"));
    }

    @Test
    void decode_todoTaskNotDone_todoTaskReconstructed() throws NeilException {
        Task task = Task.decode("T | 0 | read book");

        assertInstanceOf(ToDoTask.class, task);
        assertEquals("[T][ ] read book", task.toString());
        assertEquals("T | 0 | read book", task.encode());
    }

    @Test
    void decode_todoTaskDone_todoTaskMarkedDone() throws NeilException {
        Task task = Task.decode("T | 1 | read book");

        assertInstanceOf(ToDoTask.class, task);
        assertEquals("[T][X] read book", task.toString());
        assertEquals("T | 1 | read book", task.encode());
    }

    @Test
    void decode_deadlineTask_deadlineTaskReconstructed() throws NeilException {
        Task task = Task.decode("D | 1 | return book | 2026-08-25");

        assertInstanceOf(DeadlineTask.class, task);
        assertEquals("[D][X] return book (by: Aug 25 2026)", task.toString());
        assertEquals("D | 1 | return book | 2026-08-25", task.encode());
    }

    @Test
    void decode_eventTask_eventTaskReconstructed() throws NeilException {
        Task task = Task.decode("E | 0 | team meeting | 2026-08-25 14:00 | 2026-08-25 16:00");

        assertInstanceOf(EventTask.class, task);
        assertEquals(
                "[E][ ] team meeting (from: 2026-08-25 14:00 to: 2026-08-25 16:00)",
                task.toString());
        assertEquals(
                "E | 0 | team meeting | 2026-08-25 14:00 | 2026-08-25 16:00",
                task.encode());
    }

    @Test
    void decode_fieldsWithWhitespace_whitespaceAroundSeparatorsIgnored() throws NeilException {
        Task task = Task.decode("T    |    0    |    read book");

        assertEquals("T | 0 | read book", task.encode());
    }

    @Test
    void decode_fewerThanThreeFields_exceptionThrown() {
        assertInvalidSavedTask("T | 0");
    }

    @Test
    void decode_blankDescription_exceptionThrown() {
        assertInvalidSavedTask("T | 0 |   ");
    }

    @Test
    void decode_invalidStatus_exceptionThrown() {
        assertInvalidSavedTask("T | completed | read book");
    }

    @Test
    void decode_todoWithExtraField_exceptionThrown() {
        assertInvalidSavedTask("T | 0 | read book | extra");
    }

    @Test
    void decode_deadlineWithMissingOrBlankDate_exceptionThrown() {
        assertInvalidSavedTask("D | 0 | return book");
        assertInvalidSavedTask("D | 0 | return book | ");
    }

    @Test
    void decode_deadlineWithInvalidDate_exceptionThrown() {
        assertInvalidSavedTask("D | 0 | return book | tomorrow");
    }

    @Test
    void decode_deadlineOrEventWithExtraField_exceptionThrown() {
        assertInvalidSavedTask("D | 0 | return book | 2026-08-25 | extra");
        assertInvalidSavedTask(
                "E | 0 | team meeting | 2026-08-25 14:00 | 2026-08-25 16:00 | extra");
    }

    @Test
    void decode_eventWithMissingOrBlankTime_exceptionThrown() {
        assertInvalidSavedTask("E | 0 | team meeting | 2026-08-25 14:00");
        assertInvalidSavedTask("E | 0 | team meeting |  | 2026-08-25 16:00");
        assertInvalidSavedTask("E | 0 | team meeting | 2026-08-25 14:00 | ");
    }

    @Test
    void decode_eventWithInvalidDateOrTime_exceptionThrown() {
        assertInvalidSavedTask("E | 0 | meeting | 2026-02-30 14:00 | 2026-03-01 16:00");
        assertInvalidSavedTask("E | 0 | meeting | 2026-08-25 25:00 | 2026-08-26 16:00");
    }

    @Test
    void decode_eventStartNotEarlierThanEnd_exceptionThrown() {
        assertInvalidSavedTask("E | 0 | meeting | 2026-08-25 14:00 | 2026-08-25 14:00");
        assertInvalidSavedTask("E | 0 | meeting | 2026-08-25 16:00 | 2026-08-25 14:00");
    }

    @Test
    void decode_unknownTaskType_exceptionThrown() {
        assertInvalidSavedTask("X | 0 | read book");
    }

    /**
     * Verifies that decoding invalid saved data reports a domain-specific exception.
     *
     * @param encodedTask invalid saved task data.
     */
    private void assertInvalidSavedTask(String encodedTask) {
        assertThrows(NeilException.class, () -> Task.decode(encodedTask));
    }
}
