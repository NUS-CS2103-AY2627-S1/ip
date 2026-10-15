package doge.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import doge.exception.DogeException;
import doge.model.Deadline;
import doge.model.Event;
import doge.model.Task;
import doge.model.Todo;

/** Tests parsing of all supported task command formats and validation errors. */
class ParserTest {

    /** Verifies that a todo command preserves its complete description. */
    @Test
    void parseTask_todoCommand_returnsTodo() throws DogeException {
        Task task = Parser.parseTask("todo Buy dog treats");

        assertInstanceOf(Todo.class, task);
        assertEquals("Buy dog treats", task.getDescription());
    }

    /** Verifies that a deadline command parses its description and date. */
    @Test
    void parseTask_deadlineCommand_returnsDeadline() throws DogeException {
        Deadline deadline = (Deadline) Parser.parseTask("deadline Submit report /by 5/6/2026 1430");

        assertEquals("Submit report", deadline.getDescription());
        assertEquals(LocalDateTime.of(2026, 6, 5, 14, 30), deadline.getBy());
    }

    /** Verifies that an event command parses both endpoints. */
    @Test
    void parseTask_eventCommand_returnsEvent() throws DogeException {
        Event event = (Event) Parser.parseTask("event Team lunch /from 5/6/2026 1200 /to 5/6/2026 1330");

        assertEquals("Team lunch", event.getDescription());
        assertEquals(LocalDateTime.of(2026, 6, 5, 12, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2026, 6, 5, 13, 30), event.getTo());
    }

    /** Verifies that an empty todo is rejected. */
    @Test
    void parseTask_emptyTodo_throwsDogeException() {
        assertThrows(DogeException.class, () -> Parser.parseTask("todo"));
    }

    /** Verifies that a deadline missing its marker is rejected. */
    @Test
    void parseTask_deadlineMissingByMarker_throwsDogeException() {
        assertThrows(DogeException.class, () -> Parser.parseTask("deadline Submit report"));
    }

    /** Verifies that an invalid deadline date is rejected. */
    @Test
    void parseTask_invalidDeadlineDate_throwsDogeException() {
        assertThrows(DogeException.class, () -> Parser.parseTask("deadline Submit report /by 5/6/2026 2560"));
    }

    /** Verifies that an event missing its end marker is rejected. */
    @Test
    void parseTask_eventMissingToMarker_throwsDogeException() {
        assertThrows(DogeException.class, () -> Parser.parseTask("event Team lunch /from 5/6/2026 1200"));
    }

    /** Verifies that invalid event dates are rejected. */
    @Test
    void parseTask_invalidEventDate_throwsDogeException() {
        assertThrows(DogeException.class, () -> Parser.parseTask("event Team lunch /from tomorrow /to later"));
    }

    /** Verifies that an event's end time must be later than its start time. */
    @Test
    void parseTask_eventEndNotLaterThanStart_throwsDogeException() {
        DogeException exception = assertThrows(DogeException.class, () ->
            Parser.parseTask("event Team lunch /from 5/6/2026 1330 /to 5/6/2026 1200"));

        assertEquals("The event's /to date and time must be later than its /from date and time.",
                exception.getMessage());
    }

    /** Verifies that non-task commands are rejected by the task parser. */
    @Test
    void parseTask_nonTaskCommand_throwsDogeException() {
        assertThrows(DogeException.class, () -> Parser.parseTask("list"));
    }

    /** Verifies that null and blank task input produce a helpful exception. */
    @Test
    void parseTask_blankOrNullInput_throwsDogeException() {
        assertEquals("Please enter a command.",
                assertThrows(DogeException.class, () -> Parser.parseTask(" ")).getMessage());
        assertEquals("Please enter a command.",
                assertThrows(DogeException.class, () -> Parser.parseTask(null)).getMessage());
    }
}
