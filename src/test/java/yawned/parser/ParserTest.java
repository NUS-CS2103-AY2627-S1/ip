package yawned.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import yawned.exception.YawnedException;
import yawned.task.Deadline;
import yawned.task.Event;
import yawned.task.Task;
import yawned.task.ToDo;

/** Tests command parsing and task construction. */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseTask_validTaskCommands_createsTasksWithExpectedDetails() throws YawnedException {
        Task todo = parser.parseTask(CommandType.TODO, "todo read book");
        Deadline deadline = assertInstanceOf(Deadline.class,
                parser.parseTask(CommandType.DEADLINE, "deadline submit report /by 2026-01-01 0900"));
        Event event = assertInstanceOf(Event.class,
                parser.parseTask(CommandType.EVENT, "event project meeting /from 2026-01-02 1500 /to 2026-01-02 1600"));

        assertEquals("read book", todo.getDescription());
        assertInstanceOf(ToDo.class, todo);
        assertEquals("submit report", deadline.getDescription());
        assertEquals(LocalDateTime.of(2026, 1, 1, 9, 0), deadline.getEndDate());
        assertEquals("project meeting", event.getDescription());
        assertEquals(LocalDateTime.of(2026, 1, 2, 15, 0), event.getStartDate());
        assertEquals(LocalDateTime.of(2026, 1, 2, 16, 0), event.getEndDate());
    }

    @Test
    void parseTask_aliasCommands_extractArgumentsAfterTypedAlias() throws YawnedException {
        Task todo = parser.parseTask(CommandType.TODO, "t read book");
        Deadline deadline = assertInstanceOf(Deadline.class,
                parser.parseTask(CommandType.DEADLINE, "D submit report /by 2026-01-01 0900"));
        Event event = assertInstanceOf(Event.class,
                parser.parseTask(CommandType.EVENT, "e meeting /from 2026-01-02 1500 /to 2026-01-02 1600"));

        assertEquals("read book", todo.getDescription());
        assertEquals("submit report", deadline.getDescription());
        assertEquals("meeting", event.getDescription());
        assertEquals(12, parser.parseTaskNumber(CommandType.MARK, "m 12"));
        assertEquals(7, parser.parseTaskNumber(CommandType.DELETE, "del 7"));
        assertEquals("project meeting", parser.parseFindKeyword("F project meeting"));
    }

    @Test
    void parseTask_incompleteOrInvalidTaskCommands_throwsHelpfulException() {
        assertYawnedException(CommandType.TODO, "todo",
                "I need a task description before I can save it. For example: todo buy milk");
        assertYawnedException(CommandType.DEADLINE, "deadline /by 2026-01-01 0900",
                "I need a deadline description before I can save it. "
                        + "Use: deadline <description> /by yyyy-MM-dd HHmm");
        assertYawnedException(CommandType.DEADLINE, "deadline submit report",
                "I need a /by date and time in yyyy-MM-dd HHmm format. "
                        + "Example: deadline submit report /by 2026-01-01 0900");
        assertYawnedException(CommandType.DEADLINE, "deadline submit report /by 2026-02-30 0900",
                "I need a valid date and time in yyyy-MM-dd HHmm format. *yawn*");
        assertYawnedException(CommandType.EVENT, "event /from 2026-01-01 0900 /to 2026-01-01 1000",
                "I need an event description before I can save it. "
                        + "Use: event <description> /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm");
        assertYawnedException(CommandType.EVENT, "event meeting /from 2026-01-01 0900",
                "I need /from and /to times in yyyy-MM-dd HHmm format. "
                        + "Example: event meeting /from 2026-01-01 0900 /to 2026-01-01 1000");
        assertYawnedException(CommandType.EVENT,
                "event meeting /from 2026-01-01 0900 /to 2026-01-01 2400",
                "I need a valid date and time in yyyy-MM-dd HHmm format. *yawn*");
    }

    @Test
    void parseTask_nonTaskCommand_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> parser.parseTask(CommandType.LIST, "list"));
    }

    @Test
    void parseTaskNumber_validNumber_returnsOneBasedNumber() throws YawnedException {
        assertEquals(12, parser.parseTaskNumber(CommandType.MARK, "mark 12"));
        assertEquals(-1, parser.parseTaskNumber(CommandType.DELETE, "delete -1"));
    }

    @Test
    void parseTaskNumber_missingOrMalformedNumber_throwsCommandSpecificException() {
        assertTaskNumberException(CommandType.MARK, "mark",
                "Which task should I mark? For example: mark 2");
        assertTaskNumberException(CommandType.UNMARK, "unmark nope",
                "Which task should I unmark? For example: unmark 2");
        assertTaskNumberException(CommandType.DELETE, "delete 1.5",
                "Which task should I delete? For example: delete 2");
    }

    @Test
    void parseFindKeyword_presentKeyword_returnsKeyword() throws YawnedException {
        assertEquals("book", parser.parseFindKeyword("find book"));
        assertEquals("project meeting", parser.parseFindKeyword("find project meeting"));
    }

    @Test
    void parseFindKeyword_missingKeyword_throwsHelpfulException() {
        YawnedException exception = assertThrows(YawnedException.class, () ->
                parser.parseFindKeyword("find"));

        assertEquals("I need something to search for. For example: find book", exception.getMessage());
    }

    @Test
    void parseAliasCommand_validActions_returnsAliasDetails() throws YawnedException {
        AliasCommand listing = parser.parseAliasCommand("alias list");
        AliasCommand creation = parser.parseAliasCommand("alias hw todo");
        AliasCommand removal = parser.parseAliasCommand("alias remove hw");

        assertEquals(AliasAction.LIST, listing.action());
        assertEquals("hw", creation.aliasName());
        assertEquals("todo", creation.targetCommand());
        assertEquals(AliasAction.DEFINE, creation.action());
        assertEquals("hw", removal.aliasName());
        assertEquals("", removal.targetCommand());
        assertEquals(AliasAction.REMOVE, removal.action());
    }

    @Test
    void parseAliasCommand_missingOrExtraArguments_throwsHelpfulException() {
        assertAliasException("alias");
        assertAliasException("alias hw");
        assertAliasException("alias remove");
        assertAliasException("alias list extra");
        assertAliasException("alias hw todo extra");
    }

    private void assertYawnedException(CommandType commandType, String command, String expectedMessage) {
        YawnedException exception = assertThrows(YawnedException.class, () ->
                parser.parseTask(commandType, command));
        assertEquals(expectedMessage, exception.getMessage());
    }

    private void assertTaskNumberException(CommandType commandType, String command, String expectedMessage) {
        YawnedException exception = assertThrows(YawnedException.class, () ->
                parser.parseTaskNumber(commandType, command));
        assertEquals(expectedMessage, exception.getMessage());
    }

    private void assertAliasException(String command) {
        YawnedException exception = assertThrows(YawnedException.class, () -> parser.parseAliasCommand(command));
        assertEquals("I need an alias command. Use: alias list, alias <name> <command>, or alias remove <name>.",
                exception.getMessage());
    }
}
