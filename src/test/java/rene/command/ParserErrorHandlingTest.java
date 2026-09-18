package rene.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import rene.exception.ReneException;
import rene.task.Deadline;
import rene.task.Event;
import rene.task.Task;
import rene.task.Todo;

/**
 * Verifies that malformed user input is rejected with a helpful message
 * instead of an exception reaching the user.
 */
class ParserErrorHandlingTest {
    private Parser parser;

    @BeforeEach
    void setUp() {
        parser = new Parser();
    }

    @Test
    void parse_blankInput_throwsException() {
        ReneException exception = assertThrows(ReneException.class, () -> parser.parse("   "));

        assertEquals("No directive detected. Try: help", exception.getMessage());
    }

    @Test
    void parse_leadingAndTrailingSpaces_stillRecognizesCommand() throws ReneException {
        ParsedCommand command = parser.parse("  list  ");

        assertEquals(CommandType.LIST, command.type());
        assertEquals("", command.argument());
    }

    @Test
    void parse_unknownCommandWithTrailingSpace_throwsUnknownCommandException() {
        ReneException exception = assertThrows(ReneException.class, () -> parser.parse("blah   "));

        assertEquals(
                "That falls outside my scope of operations. Standard procedures are: todo, deadline, "
                        + "event, list, mark, unmark, delete, find, help, or bye.",
                exception.getMessage());
    }

    @Test
    void parseTask_numberedCommandWithoutNumber_throwsCommandSpecificException() {
        ReneException exception = assertThrows(ReneException.class, () -> parser.parseTaskNumber(parser.parse("mark")));

        assertEquals("A mark directive requires a task number. Try: mark 1", exception.getMessage());
    }

    @Test
    void parseTaskNumber_zeroOrNegative_isParsedAndRejectedLaterByTaskList() throws ReneException {
        // Zero and negative numbers are valid integers; the task list rejects the position.
        assertEquals(0, parser.parseTaskNumber(parser.parse("mark 0")));
        assertEquals(-3, parser.parseTaskNumber(parser.parse("mark -3")));
    }

    @Test
    void parseTaskNumber_outOfIntegerRange_throwsTooLargeException() throws ReneException {
        ParsedCommand command = parser.parse("mark 99999999999999999999");

        ReneException exception = assertThrows(ReneException.class, () -> parser.parseTaskNumber(command));

        assertEquals("That task number exceeds my processing capacity. Try: mark 1", exception.getMessage());
    }

    @Test
    void parseTask_extraSpacesInsideDescription_areCollapsed() throws ReneException {
        Task task = parser.parseTask(parser.parse("todo   read    chapter 3"));

        Todo todo = assertInstanceOf(Todo.class, task);
        assertEquals("read chapter 3", todo.getDescription());
    }

    @Test
    void parseTask_duplicateByMarker_throwsException() throws ReneException {
        ParsedCommand command = parser.parse("deadline report /by 2026-08-31 /by 2026-09-01");

        ReneException exception = assertThrows(ReneException.class, () -> parser.parseTask(command));

        assertEquals(
                "Only one /by is permitted per deadline. Try: deadline submit report /by 2026-08-31",
                exception.getMessage());
    }

    @Test
    void parseTask_duplicateFromOrToMarker_throwsException() throws ReneException {
        ParsedCommand fromCommand = parser.parse("event study /from 2pm /from 3pm /to 4pm");
        ParsedCommand toCommand = parser.parse("event study /from 2pm /to 4pm /to 5pm");

        ReneException fromException = assertThrows(ReneException.class, () -> parser.parseTask(fromCommand));
        ReneException toException = assertThrows(ReneException.class, () -> parser.parseTask(toCommand));

        assertEquals(
                "An event permits one /from and one /to each. Try: event study group /from 2pm /to 4pm",
                fromException.getMessage());
        assertEquals(
                "An event permits one /from and one /to each. Try: event study group /from 2pm /to 4pm",
                toException.getMessage());
    }

    @Test
    void parseTask_toMarkerBeforeFromMarker_throwsException() throws ReneException {
        ParsedCommand command = parser.parse("event study /to 4pm /from 2pm");

        ReneException exception = assertThrows(ReneException.class, () -> parser.parseTask(command));

        assertEquals(
                "An event requires /from before /to. Try: event study group /from 2pm /to 4pm",
                exception.getMessage());
    }

    @Test
    void parseTask_eventValid_withDetailsStillCreated() throws ReneException {
        Task task = parser.parseTask(parser.parse("event lecture /from 2pm /to 4pm"));

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("lecture", event.getDescription());
    }

    @Test
    void parseTask_deadlineValid_stillParsesDate() throws ReneException {
        Task task = parser.parseTask(parser.parse("deadline report /by 2026-08-31"));

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals(LocalDate.of(2026, 8, 31), deadline.getDueDate());
    }
}
