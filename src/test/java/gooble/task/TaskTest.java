package gooble.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import gooble.GoobleException;

/** Tests task state, validation, and display behavior. */
class TaskTest {
    @Test
    void constructor_validDescription_createsIncompleteTask() {
        Task task = new Task("read book");

        assertEquals("read book", task.getDescription());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    void constructor_nullOrBlankDescription_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new Task(null));
        assertThrows(IllegalArgumentException.class, () -> new Task("   "));
    }

    @Test
    void markAsDone_taskBecomesComplete() {
        Task task = new Task("read book");

        task.markAsDone();

        assertEquals("X", task.getStatusIcon());
        assertEquals("[X] read book", task.toString());
    }

    @Test
    void markAsNotDone_completedTaskBecomesIncomplete() {
        Task task = new Task("read book");
        task.markAsDone();

        task.markAsNotDone();

        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    void addTag_validTagsAreNormalizedAndDisplayed() {
        Task task = new Task("read book");

        task.addTag("#Fun");

        assertEquals(java.util.List.of("#fun"), task.getTags());
        assertEquals("[ ] read book [#fun]", task.toString());
    }

    @Test
    void taskSubclasses_renderTheirDetails() throws GoobleException {
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("submit report", DeadlineDateParser.parse("2026-02-01 0900"));
        Event event = new Event("meeting", "2026-02-01 1000", "2026-02-01 1100");

        assertEquals("[T][ ] read book", todo.toString());
        assertEquals("[D][ ] submit report (by: Feb 01 2026, 9:00 AM)", deadline.toString());
        assertEquals("[E][ ] meeting (from: 2026-02-01 1000 to: 2026-02-01 1100)", event.toString());
    }

    @Test
    void deadlineAndEvent_rejectNullDetails() {
        assertThrows(IllegalArgumentException.class, () -> new Deadline("task", null));
        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", "", "10am"));
        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", "9am", null));
    }

    @Test
    void addTag_fourthTagRemovesOldestTag() {
        Task task = new Task("read book");
        task.addTag("#one");
        task.addTag("#two");
        task.addTag("#three");

        task.addTag("#four");

        assertEquals(java.util.List.of("#two", "#three", "#four"), task.getTags());
    }

    @Test
    void addTag_duplicateOrInvalidTag_throwsException() {
        Task task = new Task("read book");
        task.addTag("#fun");

        assertThrows(IllegalArgumentException.class, () -> task.addTag("#FUN"));
        assertThrows(IllegalArgumentException.class, () -> task.addTag("fun!"));
    }
}
