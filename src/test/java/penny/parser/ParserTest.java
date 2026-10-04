package penny.parser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import penny.command.AddCommand;
import penny.command.Command;
import penny.command.DeleteCommand;
import penny.command.ExitCommand;
import penny.command.FindCommand;
import penny.command.ListCommand;
import penny.command.MarkCommand;
import penny.command.OnCommand;
import penny.command.UnmarkCommand;
import penny.common.PennyException;

/**
 * Unit test suite for {@link Parser}.
 * Tests the {@link Parser#parse(String)} method across all supported command types,
 * boundary conditions, syntax variations, and edge cases.
 */
public class ParserTest {

    @Test
    public void parse_todoValidDescription_addCommandCreated() throws PennyException {
        Command command = Parser.parse("todo read textbook");
        assertInstanceOf(AddCommand.class, command);
    }

    @Test
    public void parse_todoEmptyDescription_exceptionThrown() {
        PennyException exception = assertThrows(PennyException.class, () -> {
            Parser.parse("todo   ");
        });
        assertTrue(exception.getMessage().contains("A todo needs a description"));
    }

    @Test
    public void parse_todoReservedDelimiterPipe_exceptionThrown() {
        PennyException exception = assertThrows(PennyException.class, () -> {
            Parser.parse("todo read book | chapter 1");
        });
        assertTrue(exception.getMessage().contains("reserved for data storage"));
    }

    @Test
    public void parse_deadlineValidDateTime_addCommandCreated() throws PennyException {
        Command command = Parser.parse("deadline return book /by 2019-10-15");
        assertInstanceOf(AddCommand.class, command);
    }

    @Test
    public void parse_deadlineMissingByKeyword_exceptionThrown() {
        PennyException exception = assertThrows(PennyException.class, () -> {
            Parser.parse("deadline return book Sunday");
        });
        assertTrue(exception.getMessage().contains("a deadline needs a time limit"));
    }

    @Test
    public void parse_deadlineMissingDescription_exceptionThrown() {
        PennyException exception = assertThrows(PennyException.class, () -> {
            Parser.parse("deadline /by 2019-10-15");
        });
        assertTrue(exception.getMessage().contains("needs a description before /by"));
    }

    @Test
    public void parse_deadlineMissingTimeLimit_exceptionThrown() {
        PennyException exception = assertThrows(PennyException.class, () -> {
            Parser.parse("deadline return book /by   ");
        });
        assertTrue(exception.getMessage().contains("needs a time limit after /by"));
    }

    @Test
    public void parse_eventValidTimes_addCommandCreated() throws PennyException {
        Command command = Parser.parse("event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600");
        assertInstanceOf(AddCommand.class, command);
    }

    @Test
    public void parse_eventMissingFromKeyword_exceptionThrown() {
        PennyException exception = assertThrows(PennyException.class, () -> {
            Parser.parse("event meeting /to 2019-10-15 1600");
        });
        assertTrue(exception.getMessage().contains("needs a start and end time"));
    }

    @Test
    public void parse_eventMissingToKeyword_exceptionThrown() {
        PennyException exception = assertThrows(PennyException.class, () -> {
            Parser.parse("event meeting /from 2019-10-15 1400");
        });
        assertTrue(exception.getMessage().contains("needs a start and end time"));
    }

    @Test
    public void parse_markValidIndex_markCommandCreated() throws PennyException {
        Command command = Parser.parse("mark 2");
        assertInstanceOf(MarkCommand.class, command);
    }

    @Test
    public void parse_markNonNumericIndex_exceptionThrown() {
        PennyException exception = assertThrows(PennyException.class, () -> {
            Parser.parse("mark abc");
        });
        assertTrue(exception.getMessage().contains("Invalid task number: 'abc'"));
    }

    @Test
    public void parse_markMissingArgument_exceptionThrown() {
        PennyException exception = assertThrows(PennyException.class, () -> {
            Parser.parse("mark  ");
        });
        assertTrue(exception.getMessage().contains("Please specify a task number"));
    }

    @Test
    public void parse_unmarkValidIndex_unmarkCommandCreated() throws PennyException {
        Command command = Parser.parse("unmark 1");
        assertInstanceOf(UnmarkCommand.class, command);
    }

    @Test
    public void parse_deleteValidIndex_deleteCommandCreated() throws PennyException {
        Command command = Parser.parse("delete 3");
        assertInstanceOf(DeleteCommand.class, command);
    }

    @Test
    public void parse_listCommand_listCommandCreated() throws PennyException {
        Command command = Parser.parse("list");
        assertInstanceOf(ListCommand.class, command);
    }

    @Test
    public void parse_onValidDate_onCommandCreated() throws PennyException {
        Command command = Parser.parse("on 2019-10-15");
        assertInstanceOf(OnCommand.class, command);
    }

    @Test
    public void parse_findValidKeyword_findCommandCreated() throws PennyException {
        Command command = Parser.parse("find book");
        assertInstanceOf(FindCommand.class, command);
    }

    @Test
    public void parse_findEmptyKeyword_exceptionThrown() {
        PennyException exception = assertThrows(PennyException.class, () -> {
            Parser.parse("find   ");
        });
        assertTrue(exception.getMessage().contains("Please specify a keyword to search for"));
    }

    @Test
    public void parse_findReservedDelimiterPipe_exceptionThrown() {
        PennyException exception = assertThrows(PennyException.class, () -> {
            Parser.parse("find book | novel");
        });
        assertTrue(exception.getMessage().contains("reserved for data storage"));
    }

    @Test
    public void parse_exitCommand_exitCommandCreated() throws PennyException {
        Command command = Parser.parse("bye");
        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.isExit());
    }

    @Test
    public void parse_unknownCommand_exceptionThrown() {
        PennyException exception = assertThrows(PennyException.class, () -> {
            Parser.parse("flyToTheMoon");
        });
        assertTrue(exception.getMessage().contains("Hmm, I don't quite understand that command"));
    }

    @Test
    public void parse_nullCommand_assertionError() {
        assertThrows(AssertionError.class, () -> {
            Parser.parse(null);
        });
    }
}
