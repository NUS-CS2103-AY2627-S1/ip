package yuno.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import yuno.command.ByeCommand;
import yuno.command.ClearCommand;
import yuno.command.DeadlineCommand;
import yuno.command.DeleteCommand;
import yuno.command.EventCommand;
import yuno.command.FindByDateCommand;
import yuno.command.FindByNameCommand;
import yuno.command.HelpCommand;
import yuno.command.ListCommand;
import yuno.command.MarkCommand;
import yuno.command.SortCommand;
import yuno.command.TodoCommand;
import yuno.command.UnmarkCommand;
import yuno.exception.UnknownCommandException;

class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parse_invalidCommand_throwsUnknownCommandException() {
        UnknownCommandException firstException = assertThrows(
                UnknownCommandException.class, () -> parser.parse(""));
        UnknownCommandException secondException = assertThrows(
                UnknownCommandException.class, () -> parser.parse("CLEAR"));
        UnknownCommandException thirdException = assertThrows(
                UnknownCommandException.class, () -> parser.parse("randCommand"));
        UnknownCommandException fourthException = assertThrows(
                UnknownCommandException.class, () -> parser.parse("date 2026-08-30"));

        String expectedMessage =
                "Did you look at what you typed? That's just a random command.";
        assertEquals(expectedMessage, firstException.getMessage());
        assertEquals(expectedMessage, secondException.getMessage());
        assertEquals(expectedMessage, thirdException.getMessage());
        assertEquals(expectedMessage, fourthException.getMessage());
    }

    @Test
    void parse_commandWithSurroundingSpaces_returnsCorrectCommand() throws UnknownCommandException {
        assertInstanceOf(ClearCommand.class, parser.parse("   clear   "));
    }

    @Test
    void parse_commandWithRepeatedSpaces_returnsCorrectCommand() throws UnknownCommandException {
        assertInstanceOf(TodoCommand.class, parser.parse("todo    read book"));
        assertInstanceOf(FindByDateCommand.class, parser.parse("find   /date    2026-08-30"));
    }

    @Test
    void parse_commandSeparatedByTab_throwsUnknownCommandException() {
        assertThrows(UnknownCommandException.class, () -> parser.parse("todo\tread book"));
    }

    @Test
    void parse_whitespaceOnlyInput_throwsUnknownCommandException() {
        assertThrows(UnknownCommandException.class, () -> parser.parse("   \t   "));
    }

    @Test
    void parse_clearCommand_returnsClearCommand() throws UnknownCommandException {
        assertInstanceOf(ClearCommand.class, parser.parse("clear"));
    }

    @Test
    void parse_listCommand_returnsListCommand() throws UnknownCommandException {
        assertInstanceOf(ListCommand.class, parser.parse("list"));
    }

    @Test
    void parse_helpCommand_returnsHelpCommand() throws UnknownCommandException {
        assertInstanceOf(HelpCommand.class, parser.parse("help"));
    }

    @Test
    void parse_byeCommand_returnsByeCommand() throws UnknownCommandException {
        assertInstanceOf(ByeCommand.class, parser.parse("bye"));
    }

    @Test
    void parse_todoCommand_returnsTodoCommand() throws UnknownCommandException {
        assertInstanceOf(TodoCommand.class, parser.parse("todo addedTask"));
    }

    @Test
    void parse_deadlineCommand_returnsDeadlineCommand() throws UnknownCommandException {
        assertInstanceOf(DeadlineCommand.class, parser.parse("deadline addedTask"));
    }

    @Test
    void parse_eventCommand_returnsEventCommand() throws UnknownCommandException {
        assertInstanceOf(EventCommand.class, parser.parse("event addedTask"));
    }

    @Test
    void parse_markCommand_returnsMarkCommand() throws UnknownCommandException {
        assertInstanceOf(MarkCommand.class, parser.parse("mark addedInt"));
    }

    @Test
    void parse_unmarkCommand_returnsUnmarkCommand() throws UnknownCommandException {
        assertInstanceOf(UnmarkCommand.class, parser.parse("unmark addedInt"));
    }

    @Test
    void parse_deleteCommand_returnsDeleteCommand() throws UnknownCommandException {
        assertInstanceOf(DeleteCommand.class, parser.parse("delete addedInt"));
    }

    @Test
    void parse_findByDateCommand_returnsFindByDateCommand() throws UnknownCommandException {
        assertInstanceOf(FindByDateCommand.class, parser.parse("find /date 2026-08-30"));
        assertInstanceOf(FindByDateCommand.class, parser.parse("find /date"));
    }

    @Test
    void parse_findByNameCommand_returnsFindByNameCommand() throws UnknownCommandException {
        assertInstanceOf(FindByNameCommand.class, parser.parse("find addedName"));
        assertInstanceOf(FindByNameCommand.class, parser.parse("find /datefoo"));
    }

    @Test
    void parse_sortCommand_returnsSortCommand() throws UnknownCommandException {
        assertInstanceOf(SortCommand.class, parser.parse("sort"));
        assertInstanceOf(SortCommand.class, parser.parse("sort /order desc"));
    }
}
