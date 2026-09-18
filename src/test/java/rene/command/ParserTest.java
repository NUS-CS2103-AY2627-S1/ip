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

class ParserTest {
    private Parser parser;

    @BeforeEach
    void setUp() {
        parser = new Parser();
    }

    @Test
    void parse_knownCommand_separatesKeywordAndArgument() throws ReneException {
        ParsedCommand command = parser.parse("deadline return book /by 2026-08-31");

        assertEquals(CommandType.DEADLINE, command.type());
        assertEquals("return book /by 2026-08-31", command.argument());
    }

    @Test
    void parse_unknownCommand_throwsException() {
        ReneException exception = assertThrows(ReneException.class, () -> parser.parse("remind me"));

        assertEquals(
                "That falls outside my scope of operations. Standard procedures are: todo, deadline, "
                        + "event, list, mark, unmark, delete, find, help, or bye.",
                exception.getMessage());
    }

    @Test
    void parse_helpCommand_returnsHelpType() throws ReneException {
        ParsedCommand command = parser.parse("help");

        assertEquals(CommandType.HELP, command.type());
        assertEquals("", command.argument());
    }

    @Test
    void parse_findCommand_separatesKeywordFromSearchTerm() throws ReneException {
        ParsedCommand command = parser.parse("find return book");

        assertEquals(CommandType.FIND, command.type());
        assertEquals("return book", command.argument());
    }

    @Test
    void parseTask_todoCommand_createsTodo() throws ReneException {
        Task task = parser.parseTask(parser.parse("todo read book"));

        Todo todo = assertInstanceOf(Todo.class, task);
        assertEquals("read book", todo.getDescription());
    }

    @Test
    void parseTask_deadlineCommand_createsDeadlineWithDate() throws ReneException {
        Task task = parser.parseTask(parser.parse("deadline return book /by 2026-08-31"));

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 8, 31), deadline.getDueDate());
    }

    @Test
    void parseTask_eventCommand_createsEventWithTimeRange() throws ReneException {
        Task task = parser.parseTask(parser.parse("event lecture /from 2pm /to 4pm"));

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("lecture", event.getDescription());
        assertEquals("2pm", event.getFrom());
        assertEquals("4pm", event.getTo());
    }

    @Test
    void parseTask_invalidDeadlineDate_throwsException() throws ReneException {
        ParsedCommand command = parser.parse("deadline invalid /by 2026-02-29");

        ReneException exception = assertThrows(ReneException.class, () -> parser.parseTask(command));
        assertEquals(
                "The due date must be a valid date in yyyy-MM-dd format. "
                        + "Try: deadline submit report /by 2026-08-31",
                exception.getMessage());
    }

    @Test
    void parseTask_eventMissingEnd_throwsException() throws ReneException {
        ParsedCommand command = parser.parse("event lecture /from 2pm");

        ReneException exception = assertThrows(ReneException.class, () -> parser.parseTask(command));
        assertEquals(
                "An event requires /from and /to. Try: event study group /from 2pm /to 4pm",
                exception.getMessage());
    }

    @Test
    void parseTaskNumber_wholeNumber_returnsNumber() throws ReneException {
        assertEquals(12, parser.parseTaskNumber(parser.parse("mark 12")));
    }

    @Test
    void parseTaskNumber_nonNumber_throwsCommandSpecificException() throws ReneException {
        ParsedCommand command = parser.parse("delete first");

        ReneException exception = assertThrows(ReneException.class, () -> parser.parseTaskNumber(command));
        assertEquals("A task position must be a whole number, e.g.: delete 1", exception.getMessage());
    }
}
