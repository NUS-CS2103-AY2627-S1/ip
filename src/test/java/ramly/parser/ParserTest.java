package ramly.parser;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ramly.command.Command;
import ramly.command.DeadlineCommand;
import ramly.command.ExitCommand;
import ramly.command.FindCommand;
import ramly.command.UndoCommand;
import ramly.command.UnknownCommand;
import ramly.exception.CommandFormatException;

/** Tests command classification and construction. */
public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    public void getCommandType_knownCommands_returnsExpectedTypes() {
        assertEquals(CommandType.BYE, parser.getCommandType("bye"));
        assertEquals(CommandType.LIST, parser.getCommandType("list"));
        assertEquals(CommandType.DEADLINE,
                parser.getCommandType("deadline return book /by 2019-10-15"));
    }

    @Test
    public void getCommandType_unknownInput_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, parser.getCommandType("hello"));
    }

    @Test
    public void parse_knownAndUnknownInputs_returnsMatchingCommands() {
        assertInstanceOf(ExitCommand.class, parser.parse("bye"));
        assertInstanceOf(DeadlineCommand.class,
                parser.parse("deadline return book /by 2019-10-15"));
        assertInstanceOf(UnknownCommand.class, parser.parse("hello"));
    }

    @Test
    public void parse_flexibleWhitespace_returnsCommand() {
        Command command = parser.parse(" \t deadline   return book   /by   2019-10-15  ");

        assertInstanceOf(DeadlineCommand.class, command);
    }

    @Test
    public void parseEvent_eventCommand_returnsThreeParts() {
        assertArrayEquals(new String[] {"meeting", "Monday", "Tuesday"},
                parser.parseEvent("event meeting /from Monday /to Tuesday"));
    }
    @Test
    public void parse_findCommand_returnsFindCommand() {
        assertEquals(CommandType.FIND, parser.getCommandType("find book"));
        assertInstanceOf(FindCommand.class, parser.parse("find book"));
    }

    @Test
    public void parse_nullInput_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> parser.parse(null));
    }

    @Test
    public void parse_undoCommand_returnsUndoCommand() {
        assertEquals(CommandType.UNDO, parser.getCommandType("undo"));
        assertInstanceOf(UndoCommand.class, parser.parse("undo"));
    }

    @Test
    public void parse_undoWithArguments_throwsFormatException() {
        CommandFormatException exception = assertThrows(CommandFormatException.class,
                () -> parser.parse("undo 1"));

        assertEquals("Use this format: undo", exception.getMessage());
    }

    @Test
    public void parse_undoWithDifferentCase_returnsUnknownCommand() {
        assertInstanceOf(UnknownCommand.class, parser.parse("Undo"));
        assertInstanceOf(UnknownCommand.class, parser.parse("UNDO"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "todo",
        "deadline report",
        "deadline report /by",
        "deadline report /by 2026-01-01 /by 2026-02-01",
        "event meeting /from 2026-01-01 0900",
        "event meeting /from 2026-01-01 0900 /from 2026-01-01 0930 /to 2026-01-01 1000",
        "event meeting /to 2026-01-01 1000 /from 2026-01-01 0900",
        "event meeting /from 2026-01-01 0900 /to 2026-01-01 1000 /to later",
        "find",
        "list extra",
        "bye now"
    })
    public void parse_invalidStructure_throwsFormatException(String input) {
        assertThrows(CommandFormatException.class, () -> parser.parse(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"mark", "mark 0", "mark -1", "mark 1.5", "delete +1", "unmark two"})
    public void parse_invalidTaskNumber_throwsFormatException(String input) {
        assertThrows(CommandFormatException.class, () -> parser.parse(input));
    }

    @Test
    public void parse_overflowingTaskNumber_reportsSpecificError() {
        CommandFormatException exception = assertThrows(CommandFormatException.class,
                () -> parser.parse("delete 999999999999999999999999"));

        assertEquals("That trail-marker number is too large.", exception.getMessage());
    }

    @Test
    public void parse_controlCharacter_throwsFormatException() {
        assertThrows(CommandFormatException.class, () -> parser.parse("todo first\nsecond"));
    }

    @Test
    public void parse_blankInput_throwsFormatException() {
        assertThrows(CommandFormatException.class, () -> parser.parse("   \t  "));
    }
}
