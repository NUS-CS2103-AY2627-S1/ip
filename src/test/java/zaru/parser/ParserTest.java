package zaru.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import zaru.command.ByeCommand;
import zaru.command.Command;
import zaru.command.DeadlineCommand;
import zaru.command.DeleteCommand;
import zaru.command.EventCommand;
import zaru.command.FindCommand;
import zaru.command.ListCommand;
import zaru.command.MarkCommand;
import zaru.command.SortCommand;
import zaru.command.TodoCommand;
import zaru.command.UnmarkCommand;
import zaru.exception.ZaruException;

/** Tests parsing user input into executable commands. */
public class ParserTest {
    /** Verifies that each supported input maps to its corresponding command class. */
    @Test
    public void parseMessage_supportedCommands_returnsMatchingCommand() throws ZaruException {
        assertInstanceOf(ByeCommand.class, Parser.parseMessage("bye"));
        assertInstanceOf(ListCommand.class, Parser.parseMessage("list"));
        assertInstanceOf(SortCommand.class, Parser.parseMessage("sort"));
        assertInstanceOf(MarkCommand.class, Parser.parseMessage("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parseMessage("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parseMessage("delete 1"));
        assertInstanceOf(TodoCommand.class, Parser.parseMessage("todo read book"));
        assertInstanceOf(DeadlineCommand.class, Parser.parseMessage("deadline submit report /by 2026-12-10"));
        assertInstanceOf(EventCommand.class,
                Parser.parseMessage("event project meeting /from 2026-12-10 1000 /to 2026-12-10 1100"));
        assertInstanceOf(FindCommand.class, Parser.parseMessage("find read book"));
    }

    /** Verifies that command names are trimmed and normalized to lower case. */
    @Test
    public void parseMessage_supportedCommand_returnsMatchingCommandName() throws ZaruException {
        Command command = Parser.parseMessage("  TODO read book  ");

        assertEquals("todo", command.getCommand());
    }

    /** Verifies that blank input produces the expected parser error. */
    @Test
    public void parseMessage_blankInput_exceptionThrown() {
        ZaruException exception = assertThrows(ZaruException.class, () -> Parser.parseMessage("   "));

        assertEquals("Please enter a command!", exception.getMessage());
    }

    /** Verifies that a missing input reference produces the same safe parser error as blank input. */
    @Test
    public void parseMessage_nullInput_exceptionThrown() {
        ZaruException exception = assertThrows(ZaruException.class, () -> Parser.parseMessage(null));

        assertEquals("Please enter a command!", exception.getMessage());
    }

    /** Verifies that a slash argument without a value produces an error. */
    @Test
    public void parseMessage_missingKeyValue_exceptionThrown() {
        ZaruException exception = assertThrows(ZaruException.class, () ->
                Parser.parseMessage("deadline submit report /by"));

        assertEquals("Please provide a value after /by.", exception.getMessage());
    }

    /** Verifies that unsupported command words produce an error. */
    @Test
    public void parseMessage_unknownCommand_exceptionThrown() {
        ZaruException exception = assertThrows(ZaruException.class, () ->
                Parser.parseMessage("dance"));

        assertEquals("Sorry, I don't know what that means ;w;", exception.getMessage());
    }

    /** Verifies that repeated whitespace around arguments is accepted. */
    @Test
    public void parseMessage_repeatedWhitespace_returnsMatchingCommand() throws ZaruException {
        Command command = Parser.parseMessage("  deadline   submit   report   /by   2026-12-10   1430  ");

        assertInstanceOf(DeadlineCommand.class, command);
    }

    /** Verifies that a repeated keyed argument is rejected instead of silently overwritten. */
    @Test
    public void parseMessage_duplicateKeyedArgument_exceptionThrown() {
        ZaruException exception = assertThrows(ZaruException.class, () ->
                Parser.parseMessage("deadline report /by 2026-12-10 /by 2026-12-11"));

        assertEquals("The /by parameter can only be specified once.", exception.getMessage());
    }

    /** Verifies that parameters unsupported by a command are rejected. */
    @Test
    public void parseMessage_unsupportedKeyedArgument_exceptionThrown() {
        ZaruException exception = assertThrows(ZaruException.class, () ->
                Parser.parseMessage("todo read book /by tomorrow"));

        assertEquals("The /by parameter is not valid for the todo command.", exception.getMessage());
    }

    /** Verifies that commands without positional arguments reject additional text. */
    @Test
    public void parseMessage_unexpectedPositionalArgument_exceptionThrown() {
        ZaruException exception = assertThrows(ZaruException.class, () -> Parser.parseMessage("list now"));

        assertEquals("The list command does not accept additional text.", exception.getMessage());
    }

    /** Verifies that a trailing slash is reported as an incomplete parameter. */
    @Test
    public void parseMessage_trailingSlash_exceptionThrown() {
        ZaruException exception = assertThrows(ZaruException.class, () -> Parser.parseMessage("todo read book /"));

        assertEquals("Please provide a parameter after /.", exception.getMessage());
    }

    /** Verifies that each command advertises its own accepted keyed arguments. */
    @Test
    public void getAllowedKeyedArguments_commandTypes_returnsOwnedArgumentSets() {
        assertEquals(Set.of(), new TodoCommand("read book").getAllowedKeyedArguments());
        assertEquals(Set.of("by"), new DeadlineCommand("report", "2026-12-10").getAllowedKeyedArguments());
        assertEquals(Set.of("from", "to"),
                new EventCommand("meeting", "2026-12-10 1000", "2026-12-10 1100")
                        .getAllowedKeyedArguments());
    }

    /** Verifies that each command declares whether it accepts a positional argument. */
    @Test
    public void acceptsPositionalArgument_commandTypes_returnsOwnedArgumentRules() {
        assertFalse(new ByeCommand().acceptsPositionalArgument());
        assertFalse(new ListCommand().acceptsPositionalArgument());
        assertFalse(new SortCommand().acceptsPositionalArgument());
        assertTrue(new TodoCommand("read book").acceptsPositionalArgument());
    }
}
