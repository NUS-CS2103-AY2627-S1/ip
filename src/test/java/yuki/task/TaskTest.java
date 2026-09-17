package yuki.task;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import yuki.time.TaskDateTime;

/** Tests task validation, identity rules, status changes, and display text. */
class TaskTest {
    @Test
    void constructor_invalidDescription_exceptionThrown() {
        assertAll(() -> assertThrows(NullPointerException.class, () -> new Task(null)), () ->
                assertThrows(IllegalArgumentException.class, () -> new Task("   ")), () ->
                assertThrows(IllegalArgumentException.class, () -> new Task("read\u0000book")));
    }

    @Test
    void markAndUnmark_task_statusIconAndTextUpdated() {
        Task task = new Task("read book");

        assertAll(() -> assertFalse(task.isDone()), () ->
                assertEquals(" ", task.getStatusIcon()), () ->
                assertEquals("[ ] read book", task.toString()));

        task.markAsDone();
        assertAll(() -> assertTrue(task.isDone()), () ->
                assertEquals("X", task.getStatusIcon()), () ->
                assertEquals("[X] read book", task.toString()));

        task.markAsNotDone();
        assertFalse(task.isDone());
    }

    @Test
    void hasSameDetails_statusCaseTypeAndDescription_expectedResultReturned() {
        ToDo task = new ToDo("Read Book");
        ToDo sameDetails = new ToDo("read book");
        sameDetails.markAsDone();

        assertAll(() -> assertTrue(task.hasSameDetails(sameDetails)), () ->
                assertFalse(task.hasSameDetails(new ToDo("read notes"))), () ->
                assertFalse(task.hasSameDetails(new Task("Read Book"))), () ->
                assertFalse(task.hasSameDetails(null)));
    }

    @Test
    void deadline_propertiesAndDetails_expectedValuesReturned() {
        TaskDateTime dueDate = TaskDateTime.of(LocalDate.of(2026, 12, 2));
        Deadline deadline = new Deadline("submit report", dueDate);

        assertAll(() -> assertEquals(dueDate, deadline.getBy()), () ->
                assertEquals("[D][ ] submit report (by: Dec 2 2026)", deadline.toString()), () ->
                assertTrue(deadline.hasSameDetails(new Deadline("SUBMIT REPORT",
                        TaskDateTime.of(LocalDate.of(2026, 12, 2))))), () ->
                assertFalse(deadline.hasSameDetails(new Deadline("submit report",
                        TaskDateTime.of(LocalDate.of(2026, 12, 3))))), () ->
                assertFalse(deadline.hasSameDetails(new ToDo("submit report"))), () ->
                assertThrows(NullPointerException.class, () -> new Deadline("submit report", null)));
    }

    @Test
    void event_propertiesDetailsAndText_expectedValuesReturned() {
        TaskDateTime start = TaskDateTime.of(LocalDateTime.of(2026, 8, 6, 14, 0));
        TaskDateTime end = TaskDateTime.of(LocalDateTime.of(2026, 8, 6, 16, 0));
        Event event = new Event("meeting", start, end);

        assertAll(() -> assertEquals(start, event.getFrom()), () ->
                assertEquals(end, event.getTo()), () ->
                assertEquals("[E][ ] meeting (from: Aug 6 2026 14:00 to: Aug 6 2026 16:00)",
                        event.toString()), () ->
                assertTrue(event.hasSameDetails(new Event("MEETING",
                        TaskDateTime.of(LocalDateTime.of(2026, 8, 6, 14, 0)),
                        TaskDateTime.of(LocalDateTime.of(2026, 8, 6, 16, 0))))), () ->
                assertFalse(event.hasSameDetails(new Event("meeting",
                        TaskDateTime.of(LocalDateTime.of(2026, 8, 6, 15, 0)),
                        TaskDateTime.of(LocalDateTime.of(2026, 8, 6, 16, 0))))), () ->
                assertFalse(event.hasSameDetails(new Event("meeting",
                        TaskDateTime.of(LocalDateTime.of(2026, 8, 6, 14, 0)),
                        TaskDateTime.of(LocalDateTime.of(2026, 8, 6, 17, 0))))));
    }

    @Test
    void event_invalidRangeOrNullValue_exceptionThrown() {
        TaskDateTime date = TaskDateTime.of(LocalDate.of(2026, 8, 6));
        TaskDateTime nextDate = TaskDateTime.of(LocalDate.of(2026, 8, 7));

        assertAll(() -> assertThrows(NullPointerException.class, () ->
                        new Event("meeting", null, nextDate)), () ->
                assertThrows(NullPointerException.class, () ->
                        new Event("meeting", date, null)), () ->
                assertThrows(IllegalArgumentException.class, () ->
                        new Event("meeting", date, date)), () ->
                assertThrows(IllegalArgumentException.class, () ->
                        new Event("meeting", nextDate, date)));
    }

    @Test
    void event_unparsedTextRange_eventCreatedWithoutOrderingAssumption() {
        Event event = new Event("meeting", TaskDateTime.of("later"), TaskDateTime.of("earlier"));

        assertEquals("later", event.getFrom().getText());
    }
}
