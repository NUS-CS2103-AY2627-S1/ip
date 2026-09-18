package jason;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import jason.command.AddCommand;
import jason.command.DeleteCommand;
import jason.command.ExitCommand;
import jason.command.FindCommand;
import jason.command.ListCommand;
import jason.command.MarkCommand;
import jason.command.UnmarkCommand;
import jason.exception.InvalidCommandException;
import jason.exception.InvalidDeadlineException;
import jason.exception.InvalidEventException;
import jason.exception.InvalidToDoException;
import jason.task.Deadline;
import jason.task.Event;
import jason.task.Task;
import jason.task.ToDo;

/** Tests command interpretation and task construction in {@link Parser}. */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parse_supportedCommands_returnsMatchingCommandTypes() throws Exception {
        assertInstanceOf(AddCommand.class, parser.parse("todo read book"));
        assertInstanceOf(ListCommand.class, parser.parse("list"));
        assertInstanceOf(MarkCommand.class, parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, parser.parse("delete 1"));
        assertInstanceOf(ExitCommand.class, parser.parse("bye"));
        assertInstanceOf(FindCommand.class, parser.parse("find book"));
    }

    @Test
    void parse_unknownOrBlankCommand_throwsInvalidCommandException() {
        assertThrows(InvalidCommandException.class, () -> parser.parse("unknown"));
        assertThrows(InvalidCommandException.class, () -> parser.parse(""));
        assertThrows(InvalidCommandException.class, () -> parser.parse(null));
    }

    @Test
    void parse_indexCommandWithoutValidIndex_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse("mark"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("delete abc"));
    }

    @Test
    void parseIndex_multipleSpaces_returnsIndex() {
        assertEquals(2, parser.parseIndex("mark    2"));
    }

    @Test
    void parse_whitespaceAroundCommand_returnsCommand() throws Exception {
        assertInstanceOf(ListCommand.class, parser.parse("  list  "));
        assertInstanceOf(AddCommand.class, parser.parse("  todo   read book  "));
    }

    @Test
    void parse_findCommandWithoutKeyword_throwsInvalidCommandException() {
        assertThrows(InvalidCommandException.class, () -> parser.parse("find"));
        assertThrows(InvalidCommandException.class, () -> parser.parse("find   "));
    }

    @Test
    void parseTask_todoCommand_createsTodo() throws Exception {
        Task task = parser.parseTask("todo    read book");

        assertInstanceOf(ToDo.class, task);
        assertEquals("read book", task.getDescription());
    }

    @Test
    void parseTask_durationTodo_acceptsSupportedFormats() throws Exception {
        assertEquals(Duration.ofHours(2), ((ToDo) parser.parseTask(
                "todo read report /for 2h")).getDuration());
        assertEquals(Duration.ofMinutes(90), ((ToDo) parser.parseTask(
                "todo read report /for 90m")).getDuration());
        assertEquals(Duration.ofMinutes(90), ((ToDo) parser.parseTask(
                "todo read report /for 1h 30m")).getDuration());
    }

    @Test
    void parseTask_durationTodo_rejectsInvalidFormats() {
        assertThrows(InvalidToDoException.class, () -> parser.parseTask("todo read report /for"));
        assertThrows(InvalidToDoException.class, () -> parser.parseTask("todo read report /for 0m"));
        assertThrows(InvalidToDoException.class, () -> parser.parseTask("todo read report /for 25h"));
        assertThrows(InvalidToDoException.class, () -> parser.parseTask("todo read report /for 1h 60m"));
        assertThrows(InvalidToDoException.class, () -> parser.parseTask(
                "todo read report /for 999999999999999999999999h"));
        assertThrows(InvalidToDoException.class, () -> parser.parseTask("todo /for 2h"));
    }

    @Test
    void parseTask_deadlineCommand_createsDeadlineWithDateTime()
            throws InvalidToDoException, InvalidDeadlineException, InvalidEventException {
        Task task = parser.parseTask("deadline submit report /by 2019-10-15 18:00");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0), deadline.getDeadline());
    }

    @Test
    void parseTask_eventCommand_createsEventWithDateTimes()
            throws InvalidToDoException, InvalidDeadlineException, InvalidEventException {
        Task task = parser.parseTask(
                "event planning /from 2020-01-02 14:00 /to 2020-01-02 16:30");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals(LocalDateTime.of(2020, 1, 2, 14, 0), event.getStartDate());
        assertEquals(LocalDateTime.of(2020, 1, 2, 16, 30), event.getEndDate());
    }

    @Test
    void parseTask_malformedTaskCommand_throwsSpecificException() {
        assertThrows(InvalidToDoException.class, () -> parser.parseTask("todo"));
        assertThrows(InvalidToDoException.class, () -> parser.parseTask("todo   "));
        assertThrows(InvalidDeadlineException.class, () ->
                parser.parseTask("deadline submit report /by not-a-date"));
        assertThrows(InvalidDeadlineException.class, () ->
                parser.parseTask("deadline submit report /by"));
        assertThrows(InvalidEventException.class, () ->
                parser.parseTask("event planning /from 2020-01-02 14:00"));
        assertThrows(InvalidEventException.class, () ->
                parser.parseTask("event do hw /from 1 /to"));
    }
}
