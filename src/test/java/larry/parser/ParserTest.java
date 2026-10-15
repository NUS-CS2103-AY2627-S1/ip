package larry.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import larry.command.AddCommand;
import larry.command.DateQueryCommand;
import larry.command.DeleteCommand;
import larry.command.EditCommand;
import larry.command.ExitCommand;
import larry.command.FindCommand;
import larry.command.ListCommand;
import larry.command.MarkCommand;
import larry.command.UnmarkCommand;
import larry.exception.LarryException;

/**
 * Tests conversion of user input into Larry commands.
 */
class ParserTest {
    @Test
    void parseCommand_exitAndListCommands_correctCommandReturned() throws LarryException {
        assertInstanceOf(ExitCommand.class, Parser.parseCommand("bye"));
        assertInstanceOf(ListCommand.class, Parser.parseCommand("list"));
        assertInstanceOf(ListCommand.class, Parser.parseCommand("  list  "));
    }

    @Test
    void parseCommand_validTaskCommands_addCommandReturned() throws LarryException {
        assertInstanceOf(AddCommand.class,
                Parser.parseCommand("todo read book"));
        assertInstanceOf(AddCommand.class,
                Parser.parseCommand("deadline return book /by 6-6-2026 1800"));
        assertInstanceOf(AddCommand.class,
                Parser.parseCommand("event meeting /from 6-8-2026 1400 /to 6-8-2026 1600"));
    }

    @Test
    void parseCommand_validTaskActionCommands_correctCommandReturned() throws LarryException {
        assertInstanceOf(MarkCommand.class, Parser.parseCommand("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parseCommand("unmark 2"));
        assertInstanceOf(DeleteCommand.class, Parser.parseCommand("delete 3"));
    }

    @Test
    void parseCommand_allSupportedEditFields_editCommandReturned() throws LarryException {
        assertInstanceOf(EditCommand.class,
                Parser.parseCommand("  edit 1 /description discuss /by and /to markers  "));
        assertInstanceOf(EditCommand.class,
                Parser.parseCommand("edit 2 /by 6-6-2026 1800"));
        assertInstanceOf(EditCommand.class,
                Parser.parseCommand("edit 3 /from 6-8-2026 1400"));
        assertInstanceOf(EditCommand.class,
                Parser.parseCommand("edit 3 /to 6-8-2026 1600"));
    }

    @Test
    void parseCommand_validDateQuery_dateQueryCommandReturned() throws LarryException {
        assertInstanceOf(DateQueryCommand.class, Parser.parseCommand("on 6-9-2026"));
    }

    @Test
    void parseCommand_validFindQuery_findCommandReturned() throws LarryException {
        assertInstanceOf(FindCommand.class, Parser.parseCommand("find book"));
    }

    @Test
    void parseCommand_unknownOrNearMatchCommand_exceptionThrown() {
        assertThrows(LarryException.class, () -> Parser.parseCommand("unknown"));
        assertThrows(LarryException.class, () -> Parser.parseCommand("Bye"));
        assertThrows(LarryException.class, () -> Parser.parseCommand("list tasks"));
        assertThrows(LarryException.class, () -> Parser.parseCommand("finder book"));
        assertThrows(LarryException.class, () -> Parser.parseCommand("todoList task"));
    }

    @Test
    void parseCommand_missingArgument_exceptionThrown() {
        assertThrows(LarryException.class, () -> Parser.parseCommand("todo"));
        assertThrows(LarryException.class, () -> Parser.parseCommand("deadline"));
        assertThrows(LarryException.class, () -> Parser.parseCommand("event"));
        assertThrows(LarryException.class, () -> Parser.parseCommand("on"));
        assertThrows(LarryException.class, () -> Parser.parseCommand("find"));
        assertThrows(LarryException.class, () -> Parser.parseCommand("mark"));
        assertThrows(LarryException.class, () -> Parser.parseCommand("unmark"));
        assertThrows(LarryException.class, () -> Parser.parseCommand("delete"));
        assertThrows(LarryException.class, () -> Parser.parseCommand("edit"));
    }

    @Test
    void parseCommand_invalidTaskIndex_exceptionThrown() {
        assertThrows(LarryException.class, () -> Parser.parseCommand("mark zero"));
        assertThrows(LarryException.class, () -> Parser.parseCommand("mark 0"));
        assertThrows(LarryException.class, () -> Parser.parseCommand("unmark -1"));
        assertThrows(LarryException.class, () -> Parser.parseCommand("delete 1.5"));
        assertThrows(LarryException.class, () -> Parser.parseCommand("delete 1 extra"));
    }

    @Test
    void parseCommand_invalidEditIndex_specificExceptionThrown() {
        assertEditError("ERROR: The edit task index must be a positive whole number.",
                "edit zero /description updated");
        assertEditError("ERROR: The edit task index must be a positive whole number.",
                "edit 0 /description updated");
        assertEditError("ERROR: The edit task index must be a positive whole number.",
                "edit -1 /description updated");
        assertEditError("ERROR: The edit task index must be a positive whole number.",
                "edit 1.5 /description updated");
        assertEditError("ERROR: The edit task index must be a positive whole number.",
                "edit zero");
    }

    @Test
    void parseCommand_malformedEditSyntax_specificExceptionThrown() {
        assertEditError("ERROR: Use edit INDEX /description DESCRIPTION, /by DATE_TIME, "
                        + "/from DATE_TIME, or /to DATE_TIME.",
                "edit");
        assertEditError("ERROR: Use edit INDEX /description DESCRIPTION, /by DATE_TIME, "
                        + "/from DATE_TIME, or /to DATE_TIME.",
                "edit 1");
    }

    @Test
    void parseCommand_unsupportedEditField_specificExceptionThrown() {
        assertEditError("ERROR: Edit field must be /description, /by, /from, or /to.",
                "edit 1 description updated");
        assertEditError("ERROR: Edit field must be /description, /by, /from, or /to.",
                "edit 1 /at 6-6-2026 1800");
        assertEditError("ERROR: Edit field must be /description, /by, /from, or /to.",
                "edit 1 /Description updated");
    }

    @Test
    void parseCommand_blankEditValue_specificExceptionThrown() {
        assertEditError("ERROR: The edit replacement value cannot be blank.",
                "edit 1 /description");
        assertEditError("ERROR: The edit replacement value cannot be blank.",
                "edit 1 /by   ");
    }

    @Test
    void parseCommand_invalidEditDateTime_specificExceptionThrown() {
        assertEditError("ERROR: The value for /by is not a valid date and time.",
                "edit 1 /by tomorrow");
        assertEditError("ERROR: The value for /from is not a valid date and time.",
                "edit 1 /from 25:00");
        assertEditError("ERROR: The value for /to is not a valid date and time.",
                "edit 1 /to 31-2-2026 1200");
    }

    @Test
    void parseCommand_malformedDeadline_exceptionThrown() {
        assertThrows(LarryException.class, () ->
                Parser.parseCommand("deadline return book"));
        assertThrows(LarryException.class, () ->
                Parser.parseCommand("deadline /by 6-6-2026 1800"));
        assertThrows(LarryException.class, () ->
                Parser.parseCommand("deadline return book /by"));
        assertThrows(LarryException.class, () ->
                Parser.parseCommand("deadline return book /by 31-2-2026 1800"));
    }

    @Test
    void parseCommand_malformedEvent_exceptionThrown() {
        assertThrows(LarryException.class, () ->
                Parser.parseCommand("event meeting"));
        assertThrows(LarryException.class, () ->
                Parser.parseCommand("event /from 6-8-2026 1400 /to 6-8-2026 1600"));
        assertThrows(LarryException.class, () ->
                Parser.parseCommand("event meeting /from 6-8-2026 1400"));
        assertThrows(LarryException.class, () ->
                Parser.parseCommand("event meeting /to 6-8-2026 1600"));
        assertThrows(LarryException.class, () ->
                Parser.parseCommand("event meeting /from 25:00 /to 6-8-2026 1600"));
    }

    @Test
    void parseCommand_nullInput_nullPointerExceptionThrown() {
        assertThrows(NullPointerException.class, () -> Parser.parseCommand(null));
    }

    @Test
    void parseCommand_invalidInputs_personalitySpecificMessagesReturned() {
        assertErrorMessage("todo",
                "ERROR: EVIL LARRY cannot bind a nameless task. Use: todo DESCRIPTION.");
        assertErrorMessage("deadline task /by impossible",
                "ERROR: EVIL LARRY rejects that deadline date. Try: 06-09-2026 1800.");
        assertErrorMessage("event meeting /from Mon /to Tue",
                "ERROR: EVIL LARRY rejects that event date. Try: 06-09-2026 1400.");
        assertErrorMessage("event meeting /from 06-09-2026 1500 /to 06-09-2026 1400",
                "ERROR: EVIL LARRY refuses to bend time. An event must end after it starts.");
        assertErrorMessage("on tomorrow",
                "ERROR: EVIL LARRY cannot rule that date. Try: on 06-09-2026.");
        assertErrorMessage("find",
                "ERROR: EVIL LARRY needs a keyword before he can hunt. Use: find KEYWORD.");
        assertErrorMessage("mark zero",
                "ERROR: EVIL LARRY demands a positive task number after mark.");
    }

    private static void assertErrorMessage(String command, String expectedMessage) {
        LarryException exception = assertThrows(LarryException.class, () -> Parser.parseCommand(command));
        assertEquals(expectedMessage, exception.getMessage());
    }
    /**
     * Verifies the precise message returned for an invalid edit command.
     */
    private static void assertEditError(String expectedMessage, String command) {
        assertErrorMessage(command, expectedMessage);
    }
}
