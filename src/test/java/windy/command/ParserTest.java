package windy.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import windy.exception.InvalidInputFormatException;

public class ParserTest {

    @Test
    public void parseCommand_supportedCommands_returnsMatchingCommandObjects()
            throws InvalidInputFormatException {
        assertInstanceOf(ByeCommand.class, Parser.parseCommand("bye", 0));
        assertInstanceOf(ListCommand.class, Parser.parseCommand("list", 0));
        assertInstanceOf(MarkCommand.class, Parser.parseCommand("mark 1", 1));
        assertInstanceOf(UnmarkCommand.class, Parser.parseCommand("unmark 1", 1));
        assertInstanceOf(DeleteCommand.class, Parser.parseCommand("delete 1", 1));
        assertInstanceOf(AddTaskCommand.class, Parser.parseCommand("todo read book", 0));
        assertInstanceOf(AddTaskCommand.class,
                Parser.parseCommand("deadline return book /by 2026-9-1", 0));
        assertInstanceOf(AddTaskCommand.class,
                Parser.parseCommand("event holiday /from 2026-9-1 /to 2026-9-2", 0));
        assertInstanceOf(FindCommand.class, Parser.parseCommand("find book", 0));
        assertInstanceOf(DateCommand.class, Parser.parseCommand("date 2026-9-1", 0));
    }

    @Test
    public void parseCommand_unknownCommand_throwsInvalidInputFormatException() {
        assertThrows(InvalidInputFormatException.class, () -> Parser.parseCommand("dance", 0));
    }

    @Test
    public void parseCommand_repeatedDateMarker_showsCommandFormat() {
        Executable parse = () -> Parser.parseCommand("deadline book /by 2026-9-13 /by 2026-9-14", 0);
        InvalidInputFormatException exception = assertThrows(InvalidInputFormatException.class, parse);

        assertEquals("Invalid command format. Use: deadline DESCRIPTION /by yyyy-M-d",
                exception.getMessage());
    }

    @Test
    public void parseCommand_reservedSeparator_rejectsDescription() {
        Executable parse = () -> Parser.parseCommand("todo buy | milk", 0);
        InvalidInputFormatException exception = assertThrows(InvalidInputFormatException.class, parse);

        assertEquals("Task description cannot contain '|'.", exception.getMessage());
    }

    @Test
    public void parseCommand_impossibleDate_showsUnifiedDateError() {
        Executable parse = () -> Parser.parseCommand("deadline book /by 2019-2-30", 0);
        InvalidInputFormatException exception = assertThrows(InvalidInputFormatException.class, parse);

        assertEquals("Invalid Date. Use yyyy-M-d", exception.getMessage());
    }

    @Test
    public void parseCommand_missingDescriptionOrDateMarker_showsUsage() {
        assertParseError("todo", 0, "Task description cannot be empty. Use: todo DESCRIPTION");
        assertParseError("deadline /by 2026-9-3", 0,
                "Task description cannot be empty. Use: deadline DESCRIPTION /by yyyy-M-d");
        assertParseError("deadline book 2026-9-3", 0,
                "Invalid command format. Use: deadline DESCRIPTION /by yyyy-M-d");
        assertParseError("event trip /from 2026-9-3", 0,
                "Invalid command format. Use: event DESCRIPTION /from yyyy-M-d /to yyyy-M-d");
        assertParseError("event trip /from 2026-9-3 /to", 0,
                "Invalid command format. Use: event DESCRIPTION /from yyyy-M-d /to yyyy-M-d");
    }

    @Test
    public void parseCommand_extraArguments_showsCommandSpecificUsage() {
        assertParseError("list extra", 0, "The list command does not accept arguments. Use: list");
        assertParseError("bye extra", 0, "The bye command does not accept arguments. Use: bye");
        assertParseError("mark 1 extra", 1, "Invalid format. Please use: mark TASK_NUMBER");
        assertParseError("find book extra", 0, "Invalid format. Please use: find keyword");
    }

    @Test
    public void parseTaskNumber_validNumber_returnsZeroBasedIndex() throws InvalidInputFormatException {
        assertEquals(1, Parser.parseTaskNumber("2", 4));
    }

    @Test
    public void parseTaskNumber_invalidNumber_throwsInvalidInputFormatException() {
        assertThrows(InvalidInputFormatException.class, () -> Parser.parseTaskNumber("adffa", 3));
    }

    @Test
    public void parseTaskNumber_tooLargeNumber_throwsInvalidInputFormatException() {
        assertThrows(InvalidInputFormatException.class, () -> Parser.parseTaskNumber("5", 3));
    }

    @Test
    public void parseTaskNumber_zero_throwsInvalidInputFormatException() {
        assertThrows(InvalidInputFormatException.class, () -> Parser.parseTaskNumber("0", 3));
    }

    @Test
    public void parseTaskNumber_emptyTaskList_throwsInvalidInputFormatException() {
        assertThrows(InvalidInputFormatException.class, () -> Parser.parseTaskNumber("1", 0));
    }

    @Test
    public void parseTaskNumber_negativeAndOverflowingNumbers_showRangeError() {
        assertParseError("mark -1", 3, "Task number must be between 1 and 3.");
        assertParseError("mark 999999999999999999999", 3,
                "Task number must be between 1 and 3.");
    }

    @Test
    public void parseNewTask_nonTaskCommand_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> Parser.parseNewTask("list", CommandType.LIST));
    }

    private void assertParseError(String input, int taskCount, String expectedMessage) {
        InvalidInputFormatException exception = assertThrows(
                InvalidInputFormatException.class, () -> Parser.parseCommand(input, taskCount));
        assertEquals(expectedMessage, exception.getMessage());
    }
}
