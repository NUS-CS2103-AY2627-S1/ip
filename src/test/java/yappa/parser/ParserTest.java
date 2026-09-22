package yappa.parser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import yappa.command.AddCommand;
import yappa.command.DeleteCommand;
import yappa.command.ExitCommand;
import yappa.command.FindCommand;
import yappa.command.HelpCommand;
import yappa.command.ListCommand;
import yappa.command.MarkCommand;
import yappa.command.UnmarkCommand;
import yappa.exception.YappaException;

/** Tests conversion of user input into executable commands. */
public class ParserTest {

    /** Verifies that simple commands are parsed into their matching command types. */
    @Test
    public void parse_simpleCommands_returnsMatchingCommandTypes() throws YappaException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(FindCommand.class, Parser.parse("find report"));
        assertInstanceOf(HelpCommand.class, Parser.parse("help"));
    }

    /** Verifies that valid task commands produce add commands. */
    @Test
    public void parse_taskCommands_returnsAddCommands() throws YappaException {
        assertInstanceOf(AddCommand.class, Parser.parse("todo read a book"));
        assertInstanceOf(AddCommand.class,
                Parser.parse("deadline submit report /by 02/12/2026 1800"));
        assertInstanceOf(AddCommand.class,
                Parser.parse("event project meeting /from 02/12/2026 1400 /to 02/12/2026 1600"));
    }

    /** Verifies that missing or invalid task numbers are rejected. */
    @Test
    public void parse_invalidTaskNumber_throwsYappaException() {
        assertThrows(YappaException.class, () -> Parser.parse("mark"));
        assertThrows(YappaException.class, () -> Parser.parse("mark 0"));
        assertThrows(YappaException.class, () -> Parser.parse("delete first"));
    }

    /** Verifies that incomplete task commands are rejected. */
    @Test
    public void parse_incompleteTaskCommand_throwsYappaException() {
        assertThrows(YappaException.class, () -> Parser.parse("todo"));
        assertThrows(YappaException.class, () -> Parser.parse("deadline submit report"));
        assertThrows(YappaException.class,
                () -> Parser.parse("event meeting /from 02/12/2026 1400"));
        assertThrows(YappaException.class, () -> Parser.parse("find"));
    }

    /** Verifies that blank and unknown input is rejected. */
    @Test
    public void parse_blankOrUnknownInput_throwsYappaException() {
        assertThrows(YappaException.class, () -> Parser.parse("   "));
        assertThrows(YappaException.class, () -> Parser.parse("remind me later"));
    }
}
