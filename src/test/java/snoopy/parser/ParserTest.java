package snoopy.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import snoopy.command.CommandType;
import snoopy.exception.SnoopyException;
import snoopy.task.Deadline;
import snoopy.task.Event;
import snoopy.task.Task;
import snoopy.task.Todo;

/**
 * Tests command recognition and parsing of task details and indexes.
 */
public class ParserTest {
    @Test
    public void normalizeInput_nullAndWhitespace_returnsNormalizedCommands() {
        assertEquals("", Parser.normalizeInput(null));
        assertEquals("todo read book", Parser.normalizeInput("  todo read book  "));
    }

    @Test
    public void parseCommandType_supportedCommands_returnsMatchingTypes() {
        assertEquals(CommandType.BYE, Parser.parseCommandType("bye"));
        assertEquals(CommandType.LIST, Parser.parseCommandType("list"));
        assertEquals(CommandType.MARK, Parser.parseCommandType("mark 2"));
        assertEquals(CommandType.UNMARK, Parser.parseCommandType("unmark 2"));
        assertEquals(CommandType.DELETE, Parser.parseCommandType("delete 2"));
        assertEquals(CommandType.FIND, Parser.parseCommandType("find book"));
        assertEquals(CommandType.UPDATE, Parser.parseCommandType("update 2 revised description"));
        assertEquals(CommandType.TODO, Parser.parseCommandType("todo   read book"));
        assertEquals(CommandType.DEADLINE,
                Parser.parseCommandType("deadline return book /by 2026-08-30"));
        assertEquals(CommandType.EVENT,
                Parser.parseCommandType("event meeting /from 2026-09-01 /to 2026-09-02"));
    }

    @Test
    public void parseCommandType_invalidAndExtraArguments_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType(""));
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("TODO read book"));
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("marking 2"));
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("bye now"));
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("list all"));
    }

    @Test
    public void parseTask_validCommands_returnsTypedTasks() throws SnoopyException {
        Task todo = Parser.parseTask("todo read book", CommandType.TODO);
        Task deadline = Parser.parseTask(
                "deadline return book /by 2026-08-30", CommandType.DEADLINE);
        Task event = Parser.parseTask(
                "event meeting /from 2026-09-01 /to 2026-09-02", CommandType.EVENT);

        assertInstanceOf(Todo.class, todo);
        assertInstanceOf(Deadline.class, deadline);
        assertInstanceOf(Event.class, event);
        assertEquals("T | 0 | read book", todo.toDataString());
        assertEquals("D | 0 | return book | 2026-08-30", deadline.toDataString());
        assertEquals("E | 0 | meeting | 2026-09-01 | 2026-09-02", event.toDataString());
    }

    @Test
    public void parseTask_unsupportedCommand_failsClearly() {
        assertThrows(IllegalArgumentException.class, () ->
                Parser.parseTask("list", CommandType.LIST));
    }

    @Test
    public void parseTaskIndex_validAndInvalidValues_returnsOrRejectsIndex()
            throws SnoopyException {
        assertEquals(1, Parser.parseTaskIndex("2", 3, CommandType.MARK));
        assertParsingError("Please provide a task number, for example 'mark 2'.", () ->
                Parser.parseTaskIndex("", 2, CommandType.MARK));
        assertParsingError("'abc' is not a valid task number.", () ->
                Parser.parseTaskIndex("abc", 2, CommandType.MARK));
        assertParsingError("Your task list is empty, so there is no task to mark.", () ->
                Parser.parseTaskIndex("1", 0, CommandType.MARK));
        assertParsingError("Task 3 does not exist. Choose a number from 1 to 2.", () ->
                Parser.parseTaskIndex("3", 2, CommandType.MARK));
    }

    @Test
    public void parseUpdate_validAndInvalidDetails_returnsOrRejectsDetails()
            throws SnoopyException {
        Parser.UpdateDetails details = Parser.parseUpdate(
                "update 2 revised description", 3, CommandType.UPDATE);

        assertEquals(1, details.getTaskIndex());
        assertEquals("revised description", details.getDescription());
        assertParsingError("Please use: update <task number> <new description>.", () ->
                Parser.parseUpdate("update 2", 3, CommandType.UPDATE));
    }

    private void assertParsingError(String expectedMessage, ThrowingParserAction action) {
        SnoopyException exception = assertThrows(SnoopyException.class, action::run);
        assertEquals(expectedMessage, exception.getMessage());
    }

    /**
     * Represents a parser call that can reject user input.
     */
    @FunctionalInterface
    private interface ThrowingParserAction {
        void run() throws SnoopyException;
    }
}
