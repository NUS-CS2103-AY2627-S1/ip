package luna.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import luna.LunaException;
import luna.command.ArchiveCommand;
import luna.command.Command;
import luna.command.DeadlineCommand;
import luna.command.DeleteCommand;
import luna.command.EventCommand;
import luna.command.ExitCommand;
import luna.command.FindCommand;
import luna.command.HelpCommand;
import luna.command.ListArchivedCommand;
import luna.command.ListCommand;
import luna.command.MarkCommand;
import luna.command.TodoCommand;
import luna.command.UnmarkCommand;

/**
 * Tests for {@link Parser#parse(String)}.
 */
public class ParserTest {
    @Test
    public void parse_listCommand_returnsListCommand() throws LunaException {
        Command command = Parser.parse("list");

        assertInstanceOf(ListCommand.class, command);
    }

    @Test
    public void parse_listArchivedCommand_returnsListArchivedCommand() throws LunaException {
        Command command = Parser.parse("list archived");

        assertInstanceOf(ListArchivedCommand.class, command);
    }

    @Test
    public void parse_listWithUnsupportedArguments_throwsLunaException() {
        LunaException exception = assertThrows(LunaException.class, () -> Parser.parse("list archive"));

        assertEquals("Please use list or list archived.", exception.getMessage());
    }

    @Test
    public void parse_byeCommand_returnsExitCommand() throws LunaException {
        Command command = Parser.parse("bye");

        assertInstanceOf(ExitCommand.class, command);
    }

    @Test
    public void parse_markCommand_returnsMarkCommand() throws LunaException {
        Command command = Parser.parse("mark 1");

        assertInstanceOf(MarkCommand.class, command);
    }

    @Test
    public void parse_unmarkCommand_returnsUnmarkCommand() throws LunaException {
        Command command = Parser.parse("unmark 1");

        assertInstanceOf(UnmarkCommand.class, command);
    }

    @Test
    public void parse_todoCommand_returnsTodoCommand() throws LunaException {
        Command command = Parser.parse("todo read book");

        assertInstanceOf(TodoCommand.class, command);
    }

    @Test
    public void parse_deadlineCommand_returnsDeadlineCommand() throws LunaException {
        Command command = Parser.parse("deadline submit report /by 2026-08-27");

        assertInstanceOf(DeadlineCommand.class, command);
    }

    @Test
    public void parse_eventCommand_returnsEventCommand() throws LunaException {
        Command command = Parser.parse("event meeting /from 2026-08-27T09:00 /to 2026-08-27T10:00");

        assertInstanceOf(EventCommand.class, command);
    }

    @Test
    public void parse_deleteCommand_returnsDeleteCommand() throws LunaException {
        Command command = Parser.parse("delete 1");

        assertInstanceOf(DeleteCommand.class, command);
    }

    @Test
    public void parse_findCommand_returnsFindCommand() throws LunaException {
        Command command = Parser.parse("find book");

        assertInstanceOf(FindCommand.class, command);
    }

    @Test
    public void parse_helpCommand_returnsHelpCommand() throws LunaException {
        Command command = Parser.parse("help");

        assertInstanceOf(HelpCommand.class, command);
    }

    @Test
    public void parse_helpWithUnexpectedArgument_throwsLunaException() {
        LunaException exception = assertThrows(LunaException.class, () -> Parser.parse("help commands"));

        assertEquals("Please use help without additional parameters.", exception.getMessage());
    }

    @Test
    public void parse_archiveIndexCommand_returnsArchiveCommand() throws LunaException {
        Command command = Parser.parse("archive 1");

        assertInstanceOf(ArchiveCommand.class, command);
    }

    @Test
    public void parse_archiveAllCommand_returnsArchiveCommand() throws LunaException {
        Command command = Parser.parse("archive all");

        assertInstanceOf(ArchiveCommand.class, command);
    }

    @Test
    public void parse_mixedCaseCommandWord_returnsCorrectCommand() throws LunaException {
        Command command = Parser.parse("ToDo read book");

        assertInstanceOf(TodoCommand.class, command);
    }

    @Test
    public void parse_unknownCommand_throwsLunaException() {
        assertThrows(LunaException.class, () -> Parser.parse("unknown"));
    }

    @Test
    public void parse_nullOrBlankInput_throwsLunaException() {
        assertThrows(LunaException.class, () -> Parser.parse(null));
        assertThrows(LunaException.class, () -> Parser.parse("   \t  "));
    }

    @Test
    public void parse_commandWithLeadingTrailingAndRepeatedWhitespace_returnsCorrectCommand()
            throws LunaException {
        Command command = Parser.parse("  todo    read book   ");

        assertInstanceOf(TodoCommand.class, command);
    }

    @Test
    public void parse_byeWithUnexpectedArgument_throwsLunaException() {
        LunaException exception = assertThrows(LunaException.class, () -> Parser.parse("bye now"));

        assertEquals("Please use bye without additional parameters.", exception.getMessage());
    }
}
