package rudra.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import rudra.command.DeadlineCommand;
import rudra.command.DeleteCommand;
import rudra.command.EventCommand;
import rudra.command.ExitCommand;
import rudra.command.FindCommand;
import rudra.command.ListCommand;
import rudra.command.MarkCommand;
import rudra.command.SortCommand;
import rudra.command.TodoCommand;
import rudra.command.UnmarkCommand;
import rudra.exception.RudraException;

/**
 * Tests for {@link Parser}.
 */
public class ParserTest {
    @Test
    public void parse_byeCommand_returnsExitCommand() throws RudraException {
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
    }

    @Test
    public void parse_byeCommandWithArguments_throwsRudraException() {
        assertThrows(RudraException.class, () -> Parser.parse("bye now"));
    }

    @Test
    public void parse_listCommand_returnsListCommand() throws RudraException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
    }

    @Test
    public void parse_todoCommand_returnsTodoCommand() throws RudraException {
        assertInstanceOf(TodoCommand.class, Parser.parse("todo borrow book"));
    }

    @Test
    public void parse_deadlineCommand_returnsDeadlineCommand() throws RudraException {
        assertInstanceOf(DeadlineCommand.class, Parser.parse("deadline return book /by 2026-08-28"));
    }

    @Test
    public void parse_eventCommand_returnsEventCommand() throws RudraException {
        assertInstanceOf(EventCommand.class,
                Parser.parse("event project meeting /from 2026-08-28 1400 /to 2026-08-28 1600"));
    }

    @Test
    public void parse_markCommand_returnsMarkCommand() throws RudraException {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 2"));
    }

    @Test
    public void parse_unmarkCommand_returnsUnmarkCommand() throws RudraException {
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 2"));
    }

    @Test
    public void parse_deleteCommand_returnsDeleteCommand() throws RudraException {
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 2"));
    }

    @Test
    public void parse_findCommand_returnsFindCommand() throws RudraException {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    public void parse_sortDeadlineCommand_returnsSortCommand() throws RudraException {
        assertInstanceOf(SortCommand.class, Parser.parse("sort deadline"));
    }

    @Test
    public void parse_unknownCommand_throwsRudraException() {
        RudraException exception = assertThrows(RudraException.class, () -> Parser.parse("blah"));

        assertEquals("I don't recognize that command yet. Try todo, deadline, event, list, mark, unmark, delete,"
                + " find, or sort.",
                exception.getMessage());
    }

    @Test
    public void parse_missingTodoDescription_throwsRudraException() {
        RudraException exception = assertThrows(RudraException.class, () -> Parser.parse("todo"));

        assertEquals("The description of a todo cannot be empty.", exception.getMessage());
    }

    @Test
    public void parse_malformedDeadline_throwsRudraException() {
        RudraException exception = assertThrows(RudraException.class, () -> Parser.parse("deadline return book"));

        assertEquals("Please use: deadline DESCRIPTION /by WHEN", exception.getMessage());
    }

    @Test
    public void parse_malformedEvent_throwsRudraException() {
        RudraException exception = assertThrows(RudraException.class, () -> Parser.parse(
                "event project meeting /from 2026-08-28 1400"));

        assertEquals("Please use: event DESCRIPTION /from START /to END", exception.getMessage());
    }

    @Test
    public void parse_missingFindKeyword_throwsRudraException() {
        RudraException exception = assertThrows(RudraException.class, () -> Parser.parse("find"));

        assertEquals("The keyword for find cannot be empty.", exception.getMessage());
    }

    @Test
    public void parse_missingSortCriterion_throwsRudraException() {
        RudraException exception = assertThrows(RudraException.class, () -> Parser.parse("sort"));

        assertEquals("Please use: sort deadline", exception.getMessage());
    }

    @Test
    public void parse_sortCommandWithExtraArgument_throwsRudraException() {
        RudraException exception = assertThrows(RudraException.class, () -> Parser.parse("sort deadline desc"));

        assertEquals("Please use: sort deadline", exception.getMessage());
    }

    @Test
    public void parse_nonNumericTaskNumber_throwsRudraException() {
        RudraException exception = assertThrows(RudraException.class, () -> Parser.parse("mark two"));

        assertEquals("Task numbers should be whole numbers.", exception.getMessage());
    }

    @Test
    public void parse_zeroTaskNumber_throwsRudraException() {
        RudraException exception = assertThrows(RudraException.class, () -> Parser.parse("delete 0"));

        assertEquals("That task number is out of range.", exception.getMessage());
    }
}
