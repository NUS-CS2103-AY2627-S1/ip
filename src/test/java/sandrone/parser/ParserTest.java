package sandrone.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import sandrone.SandroneException;
import sandrone.command.CommandType;
import sandrone.command.FindCommand;
import sandrone.command.StatsCommand;
import sandrone.task.Deadline;
import sandrone.task.Event;
import sandrone.task.Task;
import sandrone.task.Todo;

/** Tests command and saved-task parsing. */
public class ParserTest {
    @Test
    public void getCommandType_supportedAndUnknownCommands_returnsExpectedTypes() {
        Parser parser = new Parser();

        assertEquals(CommandType.BYE, parser.getCommandType("bye"));
        assertEquals(CommandType.LIST, parser.getCommandType("list 30/8/2026"));
        assertEquals(CommandType.MARK, parser.getCommandType("mark 1"));
        assertEquals(CommandType.UNMARK, parser.getCommandType("unmark 1"));
        assertEquals(CommandType.ADD, parser.getCommandType("todo read book"));
        assertEquals(CommandType.REMOVE, parser.getCommandType("remove 1"));
        assertEquals(CommandType.FIND, parser.getCommandType("find book"));
        assertEquals(CommandType.STATS, parser.getCommandType("stats"));
        assertEquals(CommandType.UNKNOWN, parser.getCommandType("remind me"));
        assertEquals(CommandType.UNKNOWN, parser.getCommandType("todoLater"));
        assertEquals(CommandType.UNKNOWN, parser.getCommandType("bye later"));
        assertEquals(CommandType.UNKNOWN, parser.getCommandType("stats today"));
    }

    @Test
    public void parseTask_todoCommand_returnsTodo() throws SandroneException {
        Parser parser = new Parser();

        Task task = parser.parseTask("todo read book");

        assertInstanceOf(Todo.class, task);
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void parse_findCommand_returnsFindCommand() throws SandroneException {
        Parser parser = new Parser();

        assertInstanceOf(FindCommand.class, parser.parse("find book"));
    }

    @Test
    public void parse_statsCommand_returnsStatsCommand() throws SandroneException {
        Parser parser = new Parser();

        assertInstanceOf(StatsCommand.class, parser.parse("stats"));
    }

    @Test
    public void parse_findCommandWithoutKeyword_throwsException() {
        Parser parser = new Parser();

        SandroneException exception = assertThrows(SandroneException.class,
            () -> parser.parse("find"));

        assertEquals("Search keyword cannot be empty", exception.getMessage());
    }

    @Test
    public void parseTask_deadlineCommand_returnsDeadlineWithParsedDate() throws SandroneException {
        Parser parser = new Parser();

        Task task = parser.parseTask("deadline return book /by 29/8/2026 1430");

        assertInstanceOf(Deadline.class, task);
        assertEquals("D | 0 | return book | 29/8/2026 2:30PM", task.toFileFormat());
    }

    @Test
    public void parseTask_deadlineWithExtraSpacesAroundByMarker_returnsDeadline()
            throws SandroneException {
        Parser parser = new Parser();

        Task task = parser.parseTask("deadline return book    /by   29/8/2026 1430");

        assertInstanceOf(Deadline.class, task);
    }

    @Test
    public void parseTask_eventCommand_returnsEventWithParsedDates() throws SandroneException {
        Parser parser = new Parser();

        Task task = parser.parseTask("event project meeting /from 30/8/2026 1400 /to 30/8/2026 1600");

        assertInstanceOf(Event.class, task);
        assertEquals("E | 0 | project meeting | 30/8/2026 2:00PM | 30/8/2026 4:00PM", task.toFileFormat());
    }

    @Test
    public void parseTask_eventWithExtraSpacesAroundMarkers_returnsEvent() throws SandroneException {
        Parser parser = new Parser();

        Task task = parser.parseTask(
                "event project meeting   /from 30/8/2026 1400    /to   30/8/2026 1600");

        assertInstanceOf(Event.class, task);
    }

    @Test
    public void parseTask_eventEndingAtStartTime_throwsException() {
        Parser parser = new Parser();

        SandroneException exception = assertThrows(SandroneException.class,
            () -> parser.parseTask("event meeting /from 30/8/2026 1400 /to 30/8/2026 1400"));

        assertEquals("Event end time must be after its start time", exception.getMessage());
    }

    @Test
    public void parseTask_missingDeadlineTime_throwsException() {
        Parser parser = new Parser();

        SandroneException exception = assertThrows(SandroneException.class,
            () -> parser.parseTask("deadline submit report"));

        assertEquals("Deadline must include /by followed by a time", exception.getMessage());
    }

    @Test
    public void parseTask_deadlineWithRepeatedByMarker_throwsException() {
        Parser parser = new Parser();

        SandroneException exception = assertThrows(SandroneException.class,
            () -> parser.parseTask("deadline submit report /by 29/8/2026 1400 /by 30/8/2026 1400"));

        assertEquals("Deadline must contain exactly one /by marker", exception.getMessage());
    }

    @Test
    public void parseTask_eventWithRepeatedToMarker_throwsException() {
        Parser parser = new Parser();

        SandroneException exception = assertThrows(SandroneException.class,
            () -> parser.parseTask(
                "event meeting /from 29/8/2026 1400 /to 29/8/2026 1500 /to 29/8/2026 1600"));

        assertEquals("Event must contain exactly one /from and one /to marker", exception.getMessage());
    }

    @Test
    public void parseTask_descriptionContainingPipe_throwsException() {
        Parser parser = new Parser();

        SandroneException exception = assertThrows(SandroneException.class,
            () -> parser.parseTask("todo invalid | description"));

        assertEquals("Description cannot contain |", exception.getMessage());
    }

    @Test
    public void parseTask_unknownTaskCommand_throwsException() {
        Parser parser = new Parser();

        SandroneException exception = assertThrows(SandroneException.class,
            () -> parser.parseTask("remind me"));

        assertEquals("Invalid command", exception.getMessage());
    }

    @Test
    public void parseTaskFromFile_eachTaskType_restoresTypeAndCompletionStatus()
            throws SandroneException {
        Parser parser = new Parser();

        Task todo = parser.parseTaskFromFile("T | 0 | read book");
        Task deadline = parser.parseTaskFromFile("D | 1 | return book | 29/8/2026 2:30PM");
        Task event = parser.parseTaskFromFile(
            "E | 0 | project meeting | 30/8/2026 2:00PM | 30/8/2026 4:00PM");

        assertInstanceOf(Todo.class, todo);
        assertEquals("[T][ ] read book", todo.toString());
        assertInstanceOf(Deadline.class, deadline);
        assertEquals("[D][X] return book (by: 29/8/2026 2:30PM)", deadline.toString());
        assertInstanceOf(Event.class, event);
        assertEquals("[E][ ] project meeting (from: 30/8/2026 2:00PM to: 30/8/2026 4:00PM)",
            event.toString());
    }

    @Test
    public void parseTaskFromFile_invalidStatus_throwsException() {
        Parser parser = new Parser();

        SandroneException exception = assertThrows(SandroneException.class,
            () -> parser.parseTaskFromFile("T | done | read book"));

        assertEquals("invalid task status", exception.getMessage());
    }

    @Test
    public void parseTaskFromFile_eventEndingBeforeStartTime_throwsException() {
        Parser parser = new Parser();

        SandroneException exception = assertThrows(SandroneException.class,
            () -> parser.parseTaskFromFile("E | 0 | meeting | 30/8/2026 4:00PM | 30/8/2026 2:00PM"));

        assertEquals("Event end time must be after its start time", exception.getMessage());
    }

    @Test
    public void parseListDate_validDate_returnsDate() throws SandroneException {
        Parser parser = new Parser();

        assertEquals(LocalDate.of(2026, 8, 29), parser.parseListDate("29/8/2026"));
    }

    @Test
    public void parseListDate_invalidDate_throwsException() {
        Parser parser = new Parser();

        SandroneException exception = assertThrows(SandroneException.class,
            () -> parser.parseListDate("31/2/2026"));

        assertEquals("Invalid list date. Use d/M/yyyy, e.g. 29/8/2026", exception.getMessage());
    }
}
