package probe.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import probe.ProbeException;
import probe.task.Deadline;
import probe.task.Event;
import probe.task.Task;
import probe.task.Todo;

class ParserTest {

    private final Parser parser = new Parser();

    @Test
    void parsesTodoCommand() throws ProbeException {
        Task task = parser.parseTask("todo read a book");

        assertInstanceOf(Todo.class, task);
        assertEquals("read a book", task.getDescription());
    }

    @Test
    void parsesDeadlineCommandWithDateAndTime() throws ProbeException {
        Task task = parser.parseTask("deadline submit report /by 28/8/2026 1430");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("submit report", deadline.getDescription());
        assertEquals("D | 0 | submit report | 2026-08-28T14:30", deadline.toFileFormat());
    }

    @Test
    void parsesEventCommand() throws ProbeException {
        Task task = parser.parseTask(
                "event team meeting /from 28/8/2026 1000 /to 28/8/2026 1100");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("team meeting", event.getDescription());
        assertEquals("E | 0 | team meeting | 2026-08-28T10:00 | 2026-08-28T11:00",
                event.toFileFormat());
    }

    @Test
    void rejectsTodoWithoutDescription() {
        ProbeException exception = assertThrows(
                ProbeException.class, () -> parser.parseTask("todo"));

        assertEquals("A todo description cannot be empty.", exception.getMessage());
    }

    @Test
    void rejectsEventEndingBeforeItStarts() {
        assertThrows(ProbeException.class, () -> parser.parseTask(
                "event meeting /from 28/8/2026 1100 /to 28/8/2026 1000"));
    }

    @Test
    void rejectsEventWithEqualStartAndEndTimes() {
        assertThrows(ProbeException.class, () -> parser.parseTask(
                "event meeting /from 28/8/2026 1100 /to 28/8/2026 1100"));
    }

    @Test
    void acceptsExtraWhitespaceBetweenArguments() throws ProbeException {
        Task task = parser.parseTask("  todo   read a book  ");

        assertEquals("read a book", task.getDescription());
    }

    @Test
    void rejectsUnknownTaskType() {
        assertThrows(ProbeException.class, () -> parser.parseTask("reminder read a book"));
    }

    @Test
    void parsesTaskNumber() throws ProbeException {
        assertEquals(3, parser.parseNumber("delete 3", "Invalid command"));
    }

    @Test
    void rejectsNonNumericTaskNumber() {
        ProbeException exception = assertThrows(
                ProbeException.class,
                () -> parser.parseNumber("delete abc", "Invalid command"));

        assertEquals("The task number must be a number.", exception.getMessage());
    }
}
