package allmind.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import allmind.model.Deadline;
import allmind.model.Event;
import allmind.model.Task;
import allmind.model.ToDo;

/** Tests the conversion of task commands into task objects. */
public class ParserTest {
    private final Parser parser = new Parser();

    /** A todo command should create an unfinished todo with the supplied description. */
    @Test
    public void parseTask_todoCommand_createsTodoTask() throws Exception {
        Task task = parser.parseTask("todo read lecture notes");

        ToDo todo = assertInstanceOf(ToDo.class, task);
        assertEquals("read lecture notes", todo.getDescription());
        assertFalse(todo.isDone());
    }

    /** A deadline command should parse and retain both its description and date. */
    @Test
    public void parseTask_deadlineCommand_createsDeadlineTask() throws Exception {
        Task task = parser.parseTask("deadline submit report /by 2026-10-20");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("submit report", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 10, 20), deadline.getBy());
    }

    /** An event command should parse and retain its description and date range. */
    @Test
    public void parseTask_eventCommand_createsEventTask() throws Exception {
        Task task = parser.parseTask("event team meeting /from 2026-10-21 /to 2026-10-22");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("team meeting", event.getDescription());
        assertEquals(LocalDate.of(2026, 10, 21), event.getFrom());
        assertEquals(LocalDate.of(2026, 10, 22), event.getTo());
    }

    /** Surrounding whitespace should not become part of a task's fields. */
    @Test
    public void parseTask_commandWithExtraWhitespace_trimsFields() throws Exception {
        Task task = parser.parseTask("todo   buy groceries   ");

        assertEquals("buy groceries", task.getDescription());
    }

    /** A todo without a description should be rejected with a missing-field error. */
    @Test
    public void parseTask_todoWithoutDescription_missingFieldExceptionThrown() {
        assertThrows(MissingFieldException.class, () -> parser.parseTask("todo   "));
    }

    /** A deadline without a date should be rejected with a missing-field error. */
    @Test
    public void parseTask_deadlineWithoutDate_missingFieldExceptionThrown() {
        assertThrows(MissingFieldException.class,
                () -> parser.parseTask("deadline submit report"));
    }

    /** An event with an invalid date should be rejected with an invalid-command error. */
    @Test
    public void parseTask_eventWithInvalidDate_invalidCommandExceptionThrown() {
        assertThrows(InvalidCommandException.class,
                () -> parser.parseTask("event team meeting /from tomorrow /to 2026-10-22"));
    }

    /** A command with no recognized task type should be rejected. */
    @Test
    public void parseTask_unknownCommand_invalidCommandExceptionThrown() {
        assertThrows(InvalidCommandException.class, () -> parser.parseTask("set meeting"));
    }

    @Test
    public void fileFormat_todoRoundTrip_preservesDescriptionAndStatus() throws Exception {
        ToDo todo = new ToDo("read\\notes\tsoon\n");
        todo.markAsDone();

        Task restored = parser.fromFileFormat(parser.toFileFormat(todo));

        assertEquals(todo.getDescription(), restored.getDescription());
        assertTrue(restored.isDone());
    }

    @Test
    public void fileFormat_deadlineAndEventRoundTrip_preservesDates() throws Exception {
        Deadline deadline = new Deadline("submit", LocalDate.of(2026, 10, 20));
        Event event = new Event("meeting", LocalDate.of(2026, 10, 21), LocalDate.of(2026, 10, 22));

        Deadline restoredDeadline = (Deadline) parser.fromFileFormat(parser.toFileFormat(deadline));
        Event restoredEvent = (Event) parser.fromFileFormat(parser.toFileFormat(event));

        assertEquals(deadline.getBy(), restoredDeadline.getBy());
        assertEquals(event.getFrom(), restoredEvent.getFrom());
        assertEquals(event.getTo(), restoredEvent.getTo());
    }

    @Test
    public void fromFileFormat_malformedRecord_illegalArgumentExceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> parser.fromFileFormat("TODO\t0"));
        assertThrows(IllegalArgumentException.class, () -> parser.fromFileFormat("UNKNOWN\t0\ttask"));
        assertThrows(IllegalArgumentException.class, () -> parser.fromFileFormat("DEADLINE\t0\ttask"));
        assertThrows(IllegalArgumentException.class, () -> parser.fromFileFormat("EVENT\t0\ttask\t2026-10-20"));
    }

    @Test
    public void fromFileFormat_invalidDate_invalidCommandExceptionThrown() {
        assertThrows(InvalidCommandException.class,
                () -> parser.fromFileFormat("DEADLINE\t0\ttask\tnot-a-date"));
    }

    @Test
    public void parseTask_missingDeadlineOrEventField_missingFieldExceptionThrown() {
        assertThrows(MissingFieldException.class, () -> parser.parseTask("deadline task /by"));
        assertThrows(MissingFieldException.class, () -> parser.parseTask("event meeting /from 2026-10-20"));
        assertThrows(MissingFieldException.class,
                () -> parser.parseTask("event meeting /from 2026-10-20 /to"));
    }
}
