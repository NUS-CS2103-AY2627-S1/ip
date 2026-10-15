package charliek.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.junit.jupiter.api.Test;

import charliek.parser.DateTimeParser;

/**
 * Tests the common task behavior and the three concrete task types.
 */
class TaskTest {
    /**
     * Verifies that task descriptions must contain non-whitespace text.
     */
    @Test
    void task_blankDescription_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new ToDo("  "));
        assertThrows(IllegalArgumentException.class, () -> new ToDo(null));
        assertThrows(IllegalArgumentException.class, () -> new ToDo("line\nbreak"));
        assertThrows(IllegalArgumentException.class, () -> new ToDo("tab\tbreak"));
    }

    /**
     * Verifies the state and icon changes made by the completion methods.
     */
    @Test
    void task_markingDoneAndNotDone_updatesStateAndIcon() {
        Task task = new ToDo("read book");

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());

        task.markAsNotDone();
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    /**
     * Verifies the common persistence and display values of a to-do task.
     */
    @Test
    void todo_commonMethods_returnTodoValues() {
        Task task = new ToDo("read book");

        assertEquals("T", task.getStorageType());
        assertEquals(List.of("read book"), task.getStorageFields());
        assertTrue(task.getSortDateTime().isEmpty());
        assertEquals("[T][ ] read book", task.toString());
    }

    /**
     * Verifies date-only deadline persistence, display, and sorting values.
     */
    @Test
    void deadline_dateOnly_returnsExpectedValues() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2));

        assertEquals("D", deadline.getStorageType());
        assertEquals(List.of("return book", "2019-12-02"), deadline.getStorageFields());
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), deadline.getSortDateTime().orElseThrow());
        assertEquals("[D][ ] return book (by: 2 Dec 2019)", deadline.toString());
    }

    /**
     * Verifies date-time deadline persistence, display, and sorting values.
     */
    @Test
    void deadline_dateTime_returnsExpectedValues() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 5));

        assertEquals(List.of("return book", "2019-12-02T18:05:00"), deadline.getStorageFields());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 5), deadline.getSortDateTime().orElseThrow());
        assertEquals("[D][ ] return book (by: 2 Dec 2019, 18:05)", deadline.toString());
    }

    /**
     * Verifies date-only and date-time event values.
     */
    @Test
    void event_dateOnlyAndDateTime_returnsExpectedValues() {
        Event dateEvent = new Event("project meeting", "2019-12-02", "2019-12-03");
        Event dateTimeEvent = new Event("project meeting",
                "2019-12-02 14:00", "2019-12-02 15:00");

        assertEquals("E", dateEvent.getStorageType());
        assertEquals(List.of("project meeting", "2019-12-02", "2019-12-03"),
                dateEvent.getStorageFields());
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), dateEvent.getSortDateTime().orElseThrow());
        assertEquals("[E][ ] project meeting (from: 2 Dec 2019 to: 3 Dec 2019)",
                dateEvent.toString());

        assertEquals(List.of("project meeting", "2019-12-02T14:00:00", "2019-12-02T15:00:00"),
                dateTimeEvent.getStorageFields());
        assertEquals(LocalDateTime.of(2019, 12, 2, 14, 0),
                dateTimeEvent.getSortDateTime().orElseThrow());
        assertEquals("[E][ ] project meeting (from: 2 Dec 2019, 14:00 to: 2 Dec 2019, 15:00)",
                dateTimeEvent.toString());
    }

    /**
     * Verifies that dated task constructors reject missing parsed values.
     */
    @Test
    void datedTask_missingDateTime_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Deadline(
                "return book", (DateTimeParser.ParsedDateTime) null));
        assertThrows(IllegalArgumentException.class, () -> new Event(
                "meeting", null, DateTimeParser.ParsedDateTime.ofDate(LocalDate.of(2019, 12, 2))));
        assertThrows(IllegalArgumentException.class, () -> new Event(
                "meeting", DateTimeParser.ParsedDateTime.ofDate(LocalDate.of(2019, 12, 2)), null));
    }

    /**
     * Verifies that an event must have a strictly increasing time range.
     */
    @Test
    void event_nonIncreasingRange_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Event(
                "meeting", "2019-12-03", "2019-12-02"));
        assertThrows(IllegalArgumentException.class, () -> new Event(
                "meeting", "2019-12-02 14:00", "2019-12-02 14:00"));
    }

    /**
     * Verifies that task details determine duplicate identity independently of status.
     */
    @Test
    void task_sameDetails_returnsTrueRegardlessOfStatus() {
        Task first = new ToDo("read book");
        Task second = new ToDo("read book");
        second.markAsDone();

        assertTrue(first.hasSameDetailsAs(second));
        assertFalse(first.hasSameDetailsAs(new ToDo("write book")));
        assertFalse(first.hasSameDetailsAs(null));
        assertFalse(first.hasSameDetailsAs(new Deadline("read book", LocalDate.of(2019, 12, 2))));
    }

    /**
     * Verifies that repeated completion changes remain idempotent.
     */
    @Test
    void task_repeatedCompletionChanges_remainConsistent() {
        Task task = new ToDo("read book");

        task.markAsDone();
        task.markAsDone();
        assertTrue(task.isDone());

        task.markAsNotDone();
        task.markAsNotDone();
        assertFalse(task.isDone());
    }

    /**
     * Verifies that an event can mix date-only and date-time endpoints.
     */
    @Test
    void event_mixedDateEndpointTypes_formatsAndSortsCorrectly() {
        Event event = new Event("project meeting",
                DateTimeParser.ParsedDateTime.ofDate(LocalDate.of(2019, 12, 2)),
                DateTimeParser.ParsedDateTime.ofDateTime(LocalDateTime.of(2019, 12, 2, 18, 0)));

        assertEquals(List.of("project meeting", "2019-12-02", "2019-12-02T18:00:00"),
                event.getStorageFields());
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), event.getSortDateTime().orElseThrow());
        assertEquals("[E][ ] project meeting (from: 2 Dec 2019 to: 2 Dec 2019, 18:00)", event.toString());
    }

    /**
     * Verifies that string date constructors reject malformed values.
     */
    @Test
    void datedTask_invalidStringDate_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> new Deadline("submit report", "2019-02-30"));
        assertThrows(DateTimeParseException.class, () -> new Event("meeting", "2019-02-30", "2019-03-01"));
    }

}
