package bob.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import bob.command.AddCommand;
import bob.command.Command;
import bob.command.DeleteCommand;
import bob.command.ExitCommand;
import bob.command.FindCommand;
import bob.command.ListCommand;
import bob.command.MarkCommand;
import bob.command.UpdateCommand;
import bob.exception.BobException;

/**
 * Tests the behavior of {@link Parser}.
 */
public class ParserTest {

    @Test
    public void parse_byeCommand_returnsExitCommand() throws BobException {
        Command command = Parser.parse("bye");
        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.isExit());
    }

    @Test
    public void parse_listCommand_returnsListCommand() throws BobException {
        Command command = Parser.parse("list");
        assertInstanceOf(ListCommand.class, command);
    }

    @Test
    public void parse_markCommand_returnsMarkCommand() throws BobException {
        Command command = Parser.parse("mark 2");
        assertInstanceOf(MarkCommand.class, command);
    }

    @Test
    public void parse_unmarkCommand_returnsMarkCommand() throws BobException {
        Command command = Parser.parse("unmark 2");
        assertInstanceOf(MarkCommand.class, command);
    }

    @Test
    public void parse_markInvalidInteger_throwsBobException() {
        assertThrows(BobException.class, () -> Parser.parse("mark abc"));
        assertThrows(BobException.class, () -> Parser.parse("mark"));
    }

    @Test
    public void parse_deleteCommand_returnsDeleteCommand() throws BobException {
        Command command = Parser.parse("delete 1");
        assertInstanceOf(DeleteCommand.class, command);
    }

    @Test
    public void parse_deleteInvalidInteger_throwsBobException() {
        assertThrows(BobException.class, () -> Parser.parse("delete xyz"));
        assertThrows(BobException.class, () -> Parser.parse("delete"));
    }

    @Test
    void parse_nonPositiveTaskIndex_throwsOutOfBoundsError() {
        BobException markException = assertThrows(BobException.class, () -> Parser.parse("mark 0"));
        BobException deleteException = assertThrows(BobException.class, () -> Parser.parse("delete -1"));
        BobException updateException = assertThrows(
                BobException.class, () -> Parser.parse("update 0 /desc new name"));

        assertEquals("Error: taskId out of bounds", markException.getMessage());
        assertEquals("Error: taskId out of bounds", deleteException.getMessage());
        assertEquals("Error: taskId out of bounds", updateException.getMessage());
    }

    @Test
    public void parse_todoCommand_returnsAddCommand() throws BobException {
        Command command = Parser.parse("todo read book");
        assertInstanceOf(AddCommand.class, command);
    }

    @Test
    public void parse_todoEmptyDescription_throwsBobException() {
        assertThrows(BobException.class, () -> Parser.parse("todo"));
        assertThrows(BobException.class, () -> Parser.parse("todo   "));
    }

    @Test
    public void parse_deadlineCommand_returnsAddCommand() throws BobException {
        Command command = Parser.parse("deadline submit report /by 11/11/26 18:45");
        assertInstanceOf(AddCommand.class, command);
    }

    @Test
    public void parse_deadlineMissingByOrDate_throwsBobException() {
        assertThrows(BobException.class, () -> Parser.parse("deadline submit report"));
        assertThrows(BobException.class, () -> Parser.parse("deadline submit report /by "));
        assertThrows(BobException.class, () -> Parser.parse("deadline /by 11/11/26 18:45"));
    }

    @Test
    public void parse_deadlineInvalidDateFormat_throwsBobException() {
        assertThrows(BobException.class, () -> Parser.parse("deadline submit report /by 2026-11-11"));
        assertThrows(BobException.class, () -> Parser.parse("deadline submit report /by not-a-date"));
    }

    @Test
    void parse_nonexistentDate_throwsBobException() {
        assertThrows(BobException.class, () -> Parser.parse("deadline error /by 31/09/26 10:00"));
        assertThrows(BobException.class, () -> Parser.parse(
                "event error /from 31/09/26 10:00 /to 01/10/26 10:00"));
        assertThrows(BobException.class, () -> Parser.parse("update 1 /by 31/09/26 10:00"));
    }

    @Test
    public void parse_eventCommand_returnsAddCommand() throws BobException {
        Command command = Parser.parse("event team meeting /from 11/11/26 14:00 /to 11/11/26 16:00");
        assertInstanceOf(AddCommand.class, command);
    }

    @Test
    public void parse_eventMissingParts_throwsBobException() {
        assertThrows(BobException.class, () -> Parser.parse("event meeting /from 11/11/26 14:00"));
        assertThrows(BobException.class, () -> Parser.parse("event meeting /to 11/11/26 16:00"));
        assertThrows(BobException.class, () -> Parser.parse("event /from 11/11/26 14:00 /to 11/11/26 16:00"));
    }

    @Test
    public void parse_eventInvalidDateFormat_throwsBobException() {
        assertThrows(BobException.class, () -> Parser.parse("event meeting /from invalid /to 11/11/26 16:00"));
        assertThrows(BobException.class, () -> Parser.parse("event meeting /from 11/11/26 14:00 /to invalid"));
    }

    @Test
    public void parse_findCommand_returnsFindCommand() throws BobException {
        Command command = Parser.parse("find book");
        assertInstanceOf(FindCommand.class, command);
    }

    @Test
    public void parse_findEmptyKeyword_throwsBobException() {
        assertThrows(BobException.class, () -> Parser.parse("find"));
        assertThrows(BobException.class, () -> Parser.parse("find   "));
    }

    @Test
    public void parseUpdateCommand_validDesc_returnsUpdateCommand() throws BobException {
        Command command = Parser.parse("update 1 /desc new task description");
        assertInstanceOf(UpdateCommand.class, command);
    }

    @Test
    public void parseUpdateCommand_validDeadlineBy_returnsUpdateCommand() throws BobException {
        Command command = Parser.parse("update 2 /by 12/12/26 23:59");
        assertInstanceOf(UpdateCommand.class, command);
    }

    @Test
    public void parseUpdateCommand_validEventFromAndTo_returnsUpdateCommand() throws BobException {
        Command command = Parser.parse("update 3 /from 15/12/26 10:00 /to 15/12/26 12:00");
        assertInstanceOf(UpdateCommand.class, command);
    }

    @Test
    public void parseUpdateCommand_multiFieldArbitraryOrder_returnsUpdateCommand() throws BobException {
        Command command = Parser.parse("update 3 /to 15/12/26 12:00 /desc new event /from 15/12/26 10:00");
        assertInstanceOf(UpdateCommand.class, command);
    }

    @Test
    public void parseUpdateCommand_duplicateFlags_throwsBobException() {
        assertThrows(BobException.class, () -> Parser.parse("update 1 /desc A /desc B"));
        assertThrows(BobException.class, () -> Parser.parse("update 2 /by 11/11/26 10:00 /by 12/12/26 10:00"));
    }

    @Test
    public void parseUpdateCommand_unrecognizedFlag_throwsBobException() {
        assertThrows(BobException.class, () -> Parser.parse("update 1 /tag important"));
        assertThrows(BobException.class, () -> Parser.parse("update 1 /invalid"));
    }

    @Test
    public void parseUpdateCommand_preambleText_throwsBobException() {
        assertThrows(BobException.class, () -> Parser.parse("update 1 preamble text /desc new"));
    }

    @Test
    public void parseUpdateCommand_noFlagsOrEmpty_throwsBobException() {
        assertThrows(BobException.class, () -> Parser.parse("update 1"));
        assertThrows(BobException.class, () -> Parser.parse("update"));
        assertThrows(BobException.class, () -> Parser.parse("update   "));
    }

    @Test
    public void parseUpdateCommand_emptyFlagValues_throwsBobException() {
        assertThrows(BobException.class, () -> Parser.parse("update 1 /desc"));
        assertThrows(BobException.class, () -> Parser.parse("update 1 /desc "));
        assertThrows(BobException.class, () -> Parser.parse("update 2 /by "));
        assertThrows(BobException.class, () -> Parser.parse("update 3 /from "));
        assertThrows(BobException.class, () -> Parser.parse("update 3 /to "));
    }

    @Test
    public void parseUpdateCommand_invalidIndex_throwsBobException() {
        assertThrows(BobException.class, () -> Parser.parse("update abc /desc new name"));
        assertThrows(BobException.class, () -> Parser.parse("update 0 /desc new name"));
        assertThrows(BobException.class, () -> Parser.parse("update -1 /desc new name"));
    }

    @Test
    public void parseUpdateCommand_invalidDateFormat_throwsBobException() {
        assertThrows(BobException.class, () -> Parser.parse("update 2 /by 2026-12-12"));
        assertThrows(BobException.class, () -> Parser.parse("update 3 /from not-a-date"));
        assertThrows(BobException.class, () -> Parser.parse("update 3 /to 12-12-2026 10:00"));
    }

    @Test
    public void parse_unknownOrEmptyCommand_throwsBobException() {
        assertThrows(BobException.class, () -> Parser.parse("foobar"));
        assertThrows(BobException.class, () -> Parser.parse(""));
        assertThrows(BobException.class, () -> Parser.parse("   "));
        assertThrows(BobException.class, () -> Parser.parse(null));
    }
}
