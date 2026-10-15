package vector.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import vector.VectorException;
import vector.command.AddCommand;
import vector.command.Command;
import vector.command.DeleteCommand;
import vector.command.ExitCommand;
import vector.command.FindCommand;
import vector.command.ListCommand;
import vector.command.MarkCommand;
import vector.command.ScheduleCommand;
import vector.command.UnmarkCommand;

public class ParserTest {

    @Test
    public void parse_byeCommand_returnsExitCommand() throws VectorException {
        Command command = Parser.parse("bye");
        assertTrue(command instanceof ExitCommand);
    }

    @Test
    public void parse_listCommand_returnsListCommand() throws VectorException {
        Command command = Parser.parse("list");
        assertTrue(command instanceof ListCommand);
    }

    @Test
    public void parse_deleteCommandWithValidIndex_returnsDeleteCommand() throws VectorException {
        Command command = Parser.parse("delete 2");
        assertTrue(command instanceof DeleteCommand);
        // Note: we'd need a getter in DeleteCommand to verify the index,
        // but checking the type is good enough for basic testing.
    }

    @Test
    public void parse_deleteCommandWithoutIndex_throwsVectorException() {
        VectorException thrown = assertThrows(VectorException.class, () -> {
            Parser.parse("delete");
        });
        assertEquals("Please specify which task you want to delete. For example: delete 1", thrown.getMessage());
    }

    @Test
    public void parse_todoCommandWithDescription_returnsAddCommand() throws VectorException {
        Command command = Parser.parse("todo read book");
        assertTrue(command instanceof AddCommand);
    }

    @Test
    public void parse_todoCommandWithoutDescription_throwsVectorException() {
        VectorException thrown = assertThrows(VectorException.class, () -> {
            Parser.parse("todo");
        });
        assertEquals("A todo task must have a description. Please try again.", thrown.getMessage());
    }

    @Test
    public void parse_unrecognizedCommand_throwsVectorException() {
        VectorException thrown = assertThrows(VectorException.class, () -> {
            Parser.parse("hello");
        });
        assertEquals("I don't recognize that command. "
                + "Valid commands are: todo, deadline, event, list, mark, "
                + "unmark, delete, schedule, find, help, bye.", thrown.getMessage());
    }

    @Test
    public void parse_markCommandWithValidIndex_returnsMarkCommand() throws VectorException {
        Command command = Parser.parse("mark 1");
        assertTrue(command instanceof MarkCommand);
    }

    @Test
    public void parse_unmarkCommandWithValidIndex_returnsUnmarkCommand() throws VectorException {
        Command command = Parser.parse("unmark 1");
        assertTrue(command instanceof UnmarkCommand);
    }

    @Test
    public void parse_deadlineCommandValidFormat_returnsAddCommand() throws VectorException {
        Command command = Parser.parse("deadline submit report /by 2023-12-01");
        assertTrue(command instanceof AddCommand);
    }

    @Test
    public void parse_deadlineCommandMissingBy_throwsVectorException() {
        VectorException thrown = assertThrows(VectorException.class, () -> {
            Parser.parse("deadline submit report");
        });
        assertEquals("The deadline format is incorrect. Use: deadline <task> /by <date/time>", thrown.getMessage());
    }

    @Test
    public void parse_eventCommandValidFormat_returnsAddCommand() throws VectorException {
        Command command = Parser.parse("event project meeting /from 2023-12-01 /to 2023-12-02");
        assertTrue(command instanceof AddCommand);
    }

    @Test
    public void parse_eventCommandMissingFromTo_throwsVectorException() {
        VectorException thrown = assertThrows(VectorException.class, () -> {
            Parser.parse("event project meeting /from Monday");
        });
        assertEquals("The event format is incorrect. Use: event <task> /from <start> /to <end>", thrown.getMessage());
    }

    @Test
    public void parse_scheduleCommandValidFormat_returnsScheduleCommand() throws VectorException {
        Command command = Parser.parse("schedule 2023-12-01");
        assertTrue(command instanceof ScheduleCommand);
    }

    @Test
    public void parse_findCommandValidKeyword_returnsFindCommand() throws VectorException {
        Command command = Parser.parse("find book");
        assertTrue(command instanceof FindCommand);
    }

    @Test
    public void parse_findCommandMissingKeyword_throwsVectorException() {
        VectorException thrown = assertThrows(VectorException.class, () -> {
            Parser.parse("find");
        });
        assertEquals("Please specify a keyword to find. For example: find book", thrown.getMessage());
    }

    @Test
    public void parse_inputWithPipeCharacter_throwsVectorException() {
        VectorException thrown = assertThrows(VectorException.class, () -> {
            Parser.parse("todo read | book");
        });
        assertEquals("The character '|' is reserved and cannot be used in tasks.", thrown.getMessage());
    }
}
