package pixel.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests reconstruction and validation of tasks from saved records.
 */
class TaskTest {

    @Test
    void hasSameDetails_matchingTasks_ignoresStatusButIncludesTypedDetails() {
        Todo first = new Todo("read book");
        Todo completed = new Todo("read book");
        completed.markAsDone();

        assertTrue(first.hasSameDetails(completed));
        assertFalse(first.hasSameDetails(new Todo("Read book")));
        assertFalse(first.hasSameDetails(new Deadline("read book", LocalDate.of(2026, 9, 12))));
        assertTrue(new Event("meeting", "2pm", "4pm")
                .hasSameDetails(new Event("meeting", "2pm", "4pm")));
        assertFalse(new Event("meeting", "2pm", "4pm")
                .hasSameDetails(new Event("meeting", "3pm", "4pm")));
    }

    @Test
    void eventConstructor_structuredInvalidRange_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Event("meeting", "2026-09-12", "2026-09-12"));
        assertThrows(IllegalArgumentException.class, () ->
                new Event("meeting", "2026-09-13 14:00", "2026-09-12 15:00"));
        assertThrows(IllegalArgumentException.class, () ->
                new Event("meeting", "2026-09-12T14:00", "2026-09-12T14:00"));
        assertThrows(IllegalArgumentException.class, () ->
                new Event("meeting", "4pm", "2pm"));
        assertThrows(IllegalArgumentException.class, () ->
                new Event("meeting", "14:00", "14:00"));
    }

    @Test
    void getStatusIcon_newTask_returnsBlankIcon() {
        Task task = new Task("read book");

        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void markAsDone_notDoneTask_returnsCompletedIcon() {
        Task task = new Task("read book");

        task.markAsDone();

        assertEquals("X", task.getStatusIcon());
    }

    @Test
    void markAsNotDone_completedTask_returnsBlankIcon() {
        Task task = new Task("read book");
        task.markAsDone();

        task.markAsNotDone();

        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void occursOn_plainTask_returnsFalseForAnyDate() {
        Task task = new Task("read book");

        assertFalse(task.occursOn(LocalDate.of(2019, 10, 15)));
    }

    @Test
    void containsKeyword_presentAbsentAndCaseDifference_returnsExpectedResults() {
        Task task = new Todo("Read library book");

        assertTrue(task.containsKeyword("library"));
        assertFalse(task.containsKeyword("Library"));
        assertFalse(task.containsKeyword("missing"));
    }

    @Test
    void toString_plainTask_includesStatusAndDescription() {
        Task task = new Task("read book");

        assertEquals("[ ] read book", task.toString());
    }

    @Test
    void toDataString_plainTask_serializesTypeStatusAndDescription() {
        Task task = new Task("read book");
        task.markAsDone();

        assertEquals("T | 1 | read book", task.toDataString());
    }

    @Test
    void escapeDataField_specialCharacters_escapesBackslashesAndPipes() {
        assertEquals("plan \\| review \\\\ docs",
                Task.escapeDataField("plan | review \\ docs"));
    }

    @Test
    void deadlineOccursOn_exactDateMatchesOnlyThatDate() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2019, 10, 15));

        assertTrue(deadline.occursOn(LocalDate.of(2019, 10, 15)));
        assertFalse(deadline.occursOn(LocalDate.of(2019, 10, 14)));
        assertFalse(deadline.occursOn(LocalDate.of(2019, 10, 16)));
    }

    @Test
    void eventOccursOn_dateRangeIncludesBothEndpoints() {
        Event event = new Event("project meeting", "2019-10-15 14:00", "2019-10-17 16:00");

        assertTrue(event.occursOn(LocalDate.of(2019, 10, 15)));
        assertTrue(event.occursOn(LocalDate.of(2019, 10, 16)));
        assertTrue(event.occursOn(LocalDate.of(2019, 10, 17)));
        assertFalse(event.occursOn(LocalDate.of(2019, 10, 14)));
        assertFalse(event.occursOn(LocalDate.of(2019, 10, 18)));
    }

    @Test
    void eventOccursOn_invalidDateText_returnsFalse() {
        Event event = new Event("project meeting", "not-a-date", "2019-10-17");

        assertFalse(event.occursOn(LocalDate.of(2019, 10, 15)));
    }

    @Test
    void fromDataString_todoRecord_reconstructsNotDoneTask() {
        Task task = Task.fromDataString("T | 0 | read book");

        assertInstanceOf(Todo.class, task);
        assertEquals("[T][ ] read book", task.toString());
        assertEquals("T | 0 | read book", task.toDataString());
    }

    @Test
    void fromDataString_doneTodoRecord_reconstructsCompletedTask() {
        Task task = Task.fromDataString("T | 1 | read book");

        assertEquals("[T][X] read book", task.toString());
        assertEquals("T | 1 | read book", task.toDataString());
    }

    @Test
    void fromDataString_deadlineRecord_parsesAndFormatsDate() {
        Task task = Task.fromDataString("D | 1 | submit report | 2019-10-15");

        assertInstanceOf(Deadline.class, task);
        assertEquals("[D][X] submit report (by: Oct 15 2019)", task.toString());
        assertEquals("D | 1 | submit report | 2019-10-15", task.toDataString());
    }

    @Test
    void fromDataString_eventRecord_preservesEventTimes() {
        Task task = Task.fromDataString("E | 0 | project meeting | 2pm | 4pm");

        assertInstanceOf(Event.class, task);
        assertEquals("[E][ ] project meeting (from: 2pm to: 4pm)", task.toString());
        assertEquals("E | 0 | project meeting | 2pm | 4pm", task.toDataString());
    }

    @Test
    void fromDataString_escapedFields_restoresLiteralSeparators() {
        Task task = Task.fromDataString("T | 0 | plan \\| review \\\\ docs");

        assertEquals("[T][ ] plan | review \\ docs", task.toString());
        assertEquals("T | 0 | plan \\| review \\\\ docs", task.toDataString());
    }

    @Test
    void fromDataString_invalidStatus_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Task.fromDataString("T | 2 | read book"));
    }

    @Test
    void fromDataString_unknownType_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Task.fromDataString("X | 0 | unknown task"));
    }

    @Test
    void fromDataString_malformedRecords_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Task.fromDataString("T | 0"));
        assertThrows(IllegalArgumentException.class, () -> Task.fromDataString("T | 0 |   "));
        assertThrows(IllegalArgumentException.class, () -> Task.fromDataString("D | 0 | report | not-a-date"));
    }

    @Test
    void withDescription_completedTypedTasks_preservesTypeDetailsAndStatus() {
        Todo todo = new Todo("old todo");
        Deadline deadline = new Deadline("old deadline", LocalDate.of(2026, 9, 5));
        Event event = new Event("old event", "Monday", "Tuesday");
        todo.markAsDone();
        deadline.markAsDone();
        event.markAsDone();

        assertEquals("[T][X] new todo", todo.withDescription("new todo").toString());
        assertEquals("[D][X] new deadline (by: Sep 05 2026)",
                deadline.withDescription("new deadline").toString());
        assertEquals("[E][X] new event (from: Monday to: Tuesday)",
                event.withDescription("new event").toString());
    }

    @Test
    void withDeadline_completedDeadline_preservesDescriptionAndStatus() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 5));
        deadline.markAsDone();

        Deadline updated = deadline.withDeadline(LocalDate.of(2026, 9, 10));

        assertEquals("[D][X] submit report (by: Sep 10 2026)", updated.toString());
        assertEquals("D | 1 | submit report | 2026-09-10", updated.toDataString());
    }

    @Test
    void withEventTimes_completedEvent_preservesOtherDetailsAndStatus() {
        Event event = new Event("meeting", "2pm", "4pm");
        event.markAsDone();

        Event newStart = event.withStart("3pm");
        Event newEnd = event.withEnd("5pm");

        assertEquals("[E][X] meeting (from: 3pm to: 4pm)", newStart.toString());
        assertEquals("[E][X] meeting (from: 2pm to: 5pm)", newEnd.toString());
    }
}
