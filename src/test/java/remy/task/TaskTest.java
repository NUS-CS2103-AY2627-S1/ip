package remy.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests invariants shared by tasks and their endpoint-based subtypes.
 */
class TaskTest {

    @Test
    void constructor_nullDescription_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new Todo(null));
    }

    @Test
    void constructor_blankDescription_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new Todo("   "));
    }

    @Test
    void deadlineConstructor_noEndpoint_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new Deadline("Submit report", null, null));
    }

    @Test
    void deadlineConstructor_twoEndpointRepresentations_assertionErrorThrown() {
        LocalDate date = LocalDate.of(2026, 9, 8);
        LocalDateTime dateTime = LocalDateTime.of(2026, 9, 8, 18, 0);

        assertThrows(AssertionError.class, () -> new Deadline("Submit report", date, dateTime));
    }

    @Test
    void eventConstructor_mixedEndpointRepresentations_assertionErrorThrown() {
        LocalDate startDate = LocalDate.of(2026, 9, 8);
        LocalDateTime endDateTime = LocalDateTime.of(2026, 9, 8, 18, 0);

        assertThrows(AssertionError.class, () ->
                new Event("Project meeting", startDate, null, null, endDateTime));
    }

    @Test
    void eventConstructor_startIsNotBeforeEnd_illegalArgumentExceptionThrown() {
        LocalDate date = LocalDate.of(2026, 9, 8);
        LocalDateTime laterDateTime = LocalDateTime.of(2026, 9, 8, 18, 0);
        LocalDateTime earlierDateTime = LocalDateTime.of(2026, 9, 8, 17, 0);

        assertThrows(IllegalArgumentException.class, () -> new Event("Project meeting", date, date));
        assertThrows(IllegalArgumentException.class, () -> new Event(
                "Project meeting", laterDateTime, earlierDateTime));
    }

    @Test
    void hasSameDetailsAs_completionStatusAndTaskDetails_expectedResultsReturned() {
        Task firstTodo = new Todo("Read book");
        Task completedTodo = new Todo("Read book");
        completedTodo.markAsDone();
        Task differentTodo = new Todo("Return book");
        Task deadline = new Deadline("Read book", LocalDate.of(2026, 9, 8));

        assertTrue(firstTodo.hasSameDetailsAs(completedTodo));
        assertFalse(firstTodo.hasSameDetailsAs(differentTodo));
        assertFalse(firstTodo.hasSameDetailsAs(deadline));
        assertFalse(firstTodo.hasSameDetailsAs(null));
    }

    @Test
    void hasSameDetailsAs_deadlineAndEventEndpoints_expectedResultsReturned() {
        Task firstDeadline = new Deadline("Submit report", LocalDate.of(2026, 9, 8));
        Task sameDeadline = new Deadline("Submit report", LocalDate.of(2026, 9, 8));
        Task differentDeadline = new Deadline("Submit report", LocalDate.of(2026, 9, 9));
        Task firstEvent = new Event("Conference", LocalDate.of(2026, 9, 8), LocalDate.of(2026, 9, 9));
        Task sameEvent = new Event("Conference", LocalDate.of(2026, 9, 8), LocalDate.of(2026, 9, 9));
        Task differentEvent = new Event("Conference", LocalDate.of(2026, 9, 8), LocalDate.of(2026, 9, 10));

        assertTrue(firstDeadline.hasSameDetailsAs(sameDeadline));
        assertFalse(firstDeadline.hasSameDetailsAs(differentDeadline));
        assertTrue(firstEvent.hasSameDetailsAs(sameEvent));
        assertFalse(firstEvent.hasSameDetailsAs(differentEvent));
    }

    @Test
    void hasSameDetailsAs_dateTimeEndpointsAndDifferentTypes_expectedResultsReturned() {
        LocalDateTime firstDateTime = LocalDateTime.of(2026, 9, 8, 18, 0);
        LocalDateTime secondDateTime = LocalDateTime.of(2026, 9, 8, 19, 0);
        Task firstDeadline = new Deadline("Submit report", firstDateTime);
        Task sameDeadline = new Deadline("Submit report", firstDateTime);
        Task differentDeadline = new Deadline("Submit report", secondDateTime);
        Task firstEvent = new Event("Conference", firstDateTime, secondDateTime);
        Task sameEvent = new Event("Conference", firstDateTime, secondDateTime);
        Task differentStartEvent = new Event("Conference", firstDateTime.minusHours(1), secondDateTime);
        Task differentEndEvent = new Event("Conference", firstDateTime, secondDateTime.plusHours(1));
        Task todo = new Todo("Conference");

        assertTrue(firstDeadline.hasSameDetailsAs(sameDeadline));
        assertFalse(firstDeadline.hasSameDetailsAs(differentDeadline));
        assertFalse(firstDeadline.hasSameDetailsAs(todo));
        assertTrue(firstEvent.hasSameDetailsAs(sameEvent));
        assertFalse(firstEvent.hasSameDetailsAs(differentStartEvent));
        assertFalse(firstEvent.hasSameDetailsAs(differentEndEvent));
        assertFalse(firstEvent.hasSameDetailsAs(todo));
    }
}
