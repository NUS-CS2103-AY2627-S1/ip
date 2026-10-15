package gunna;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import gunna.command.Command;
import gunna.command.DeadlineCommand;
import gunna.command.DeleteCommand;
import gunna.command.EventCommand;
import gunna.command.ExitCommand;
import gunna.command.FindCommand;
import gunna.command.InvalidCommand;
import gunna.command.ListCommand;
import gunna.command.MarkCommand;
import gunna.command.SearchCommand;
import gunna.command.SortCommand;
import gunna.command.TodoCommand;
import gunna.command.UnmarkCommand;

/**
 * JUnit test class for testing the Parser class.
 * Tests cover parsing of all command types and error handling.
 */
public class ParserTest {

    // Test parse method for basic commands

    @Test
    public void parse_byeCommand_returnsExitCommand() throws DukeException {
        Command command = Parser.parse("bye");
        assertTrue(command instanceof ExitCommand,
                "Parsing 'bye' should return ExitCommand");
    }

    @Test
    public void parse_listCommand_returnsListCommand() throws DukeException {
        Command command = Parser.parse("list");
        assertTrue(command instanceof ListCommand,
                "Parsing 'list' should return ListCommand");
    }

    @Test
    public void parse_unknownCommand_returnsInvalidCommand() throws DukeException {
        Command command = Parser.parse("unknown");
        assertTrue(command instanceof InvalidCommand,
                "Parsing unknown command should return InvalidCommand");
    }

    @Test
    public void parse_emptyString_returnsInvalidCommand() throws DukeException {
        Command command = Parser.parse("");
        assertTrue(command instanceof InvalidCommand,
                "Parsing empty string should return InvalidCommand");
    }

    // Test mark command parsing

    @Test
    public void parse_markWithNumber_returnsMarkCommand() throws DukeException {
        Command command = Parser.parse("mark 1");
        assertTrue(command instanceof MarkCommand,
                "Parsing 'mark 1' should return MarkCommand");
    }

    @Test
    public void parse_markWithSpaces_returnsMarkCommand() throws DukeException {
        Command command = Parser.parse("mark   5");
        assertTrue(command instanceof MarkCommand,
                "Parsing 'mark   5' with extra spaces should return MarkCommand");
    }

    @Test
    public void parse_markWithoutNumber_throwsException() {
        assertThrows(DukeException.class, () -> {
            Parser.parse("mark");
        }, "Parsing 'mark' without number should throw DukeException");
    }

    @Test
    public void parse_markWithInvalidNumber_throwsException() {
        assertThrows(DukeException.class, () -> {
            Parser.parse("mark abc");
        }, "Parsing 'mark abc' should throw DukeException");
    }

    @Test
    public void parse_markWithOnlySpaces_throwsException() {
        assertThrows(DukeException.class, () -> {
            Parser.parse("mark   ");
        }, "Parsing 'mark' with only spaces should throw DukeException");
    }

    // Test unmark command parsing

    @Test
    public void parse_unmarkWithNumber_returnsUnmarkCommand() throws DukeException {
        Command command = Parser.parse("unmark 2");
        assertTrue(command instanceof UnmarkCommand,
                "Parsing 'unmark 2' should return UnmarkCommand");
    }

    @Test
    public void parse_unmarkWithoutNumber_throwsException() {
        assertThrows(DukeException.class, () -> {
            Parser.parse("unmark");
        }, "Parsing 'unmark' without number should throw DukeException");
    }

    @Test
    public void parse_unmarkWithInvalidNumber_throwsException() {
        assertThrows(DukeException.class, () -> {
            Parser.parse("unmark xyz");
        }, "Parsing 'unmark xyz' should throw DukeException");
    }

    // Test delete command parsing

    @Test
    public void parse_deleteWithNumber_returnsDeleteCommand() throws DukeException {
        Command command = Parser.parse("delete 3");
        assertTrue(command instanceof DeleteCommand,
                "Parsing 'delete 3' should return DeleteCommand");
    }

    @Test
    public void parse_deleteWithoutNumber_throwsException() {
        assertThrows(DukeException.class, () -> {
            Parser.parse("delete");
        }, "Parsing 'delete' without number should throw DukeException");
    }

    @Test
    public void parse_deleteWithInvalidNumber_throwsException() {
        assertThrows(DukeException.class, () -> {
            Parser.parse("delete notanumber");
        }, "Parsing 'delete notanumber' should throw DukeException");
    }

    // Test todo command parsing

    @Test
    public void parse_todoWithDescription_returnsTodoCommand() throws DukeException {
        Command command = Parser.parse("todo read book");
        assertTrue(command instanceof TodoCommand,
                "Parsing 'todo read book' should return TodoCommand");
    }

    @Test
    public void parse_todoWithoutDescription_returnsTodoCommand() throws DukeException {
        Command command = Parser.parse("todo");
        assertTrue(command instanceof TodoCommand,
                "Parsing 'todo' without description should return TodoCommand (with empty description)");
    }

    @Test
    public void parse_todoWithOnlySpaces_returnsTodoCommand() throws DukeException {
        Command command = Parser.parse("todo   ");
        assertTrue(command instanceof TodoCommand,
                "Parsing 'todo' with only spaces should return TodoCommand");
    }

    // Test deadline command parsing

    @Test
    public void parse_deadlineWithValidFormat_returnsDeadlineCommand() throws DukeException {
        Command command = Parser.parse("deadline submit report /by Monday");
        assertTrue(command instanceof DeadlineCommand,
                "Parsing valid deadline command should return DeadlineCommand");
    }

    @Test
    public void parse_deadlineWithoutBy_throwsException() {
        assertThrows(DukeException.class, () -> {
            Parser.parse("deadline submit report");
        }, "Parsing deadline without '/by' should throw DukeException");
    }

    @Test
    public void parse_deadlineWithEmptyDescription_returnsDeadlineCommand() throws DukeException {
        Command command = Parser.parse("deadline /by Monday");
        assertTrue(command instanceof DeadlineCommand,
                "Parsing deadline with empty description should return DeadlineCommand");
    }

    @Test
    public void parse_deadlineWithEmptyBy_returnsDeadlineCommand() throws DukeException {
        Command command = Parser.parse("deadline submit report /by ");
        assertTrue(command instanceof DeadlineCommand,
                "Parsing deadline with empty /by should return DeadlineCommand");
    }

    @Test
    public void parse_deadlineWithByAtEnd_returnsDeadlineCommand() throws DukeException {
        Command command = Parser.parse("deadline submit report /by");
        assertTrue(command instanceof DeadlineCommand,
                "Parsing deadline with /by at end should return DeadlineCommand");
    }

    @Test
    public void parse_deadlineWithoutSlash_throwsException() {
        assertThrows(DukeException.class, () -> {
            Parser.parse("deadline submit report by Monday");
        }, "Parsing deadline without '/' before 'by' should throw DukeException");
    }

    // Test event command parsing

    @Test
    public void parse_eventWithValidFormat_returnsEventCommand() throws DukeException {
        Command command = Parser.parse("event project meeting /from Mon 2pm /to 4pm");
        assertTrue(command instanceof EventCommand,
                "Parsing valid event command should return EventCommand");
    }

    @Test
    public void parse_eventWithoutFrom_throwsException() {
        assertThrows(DukeException.class, () -> {
            Parser.parse("event project meeting /to 4pm");
        }, "Parsing event without '/from' should throw DukeException");
    }

    @Test
    public void parse_eventWithoutTo_throwsException() {
        assertThrows(DukeException.class, () -> {
            Parser.parse("event project meeting /from 2pm");
        }, "Parsing event without '/to' should throw DukeException");
    }

    @Test
    public void parse_eventWithEmptyDescription_returnsEventCommand() throws DukeException {
        Command command = Parser.parse("event /from 2pm /to 4pm");
        assertTrue(command instanceof EventCommand,
                "Parsing event with empty description should return EventCommand");
    }

    @Test
    public void parse_eventWithEmptyFrom_returnsEventCommand() throws DukeException {
        Command command = Parser.parse("event meeting /from  /to 4pm");
        assertTrue(command instanceof EventCommand,
                "Parsing event with empty /from should return EventCommand");
    }

    @Test
    public void parse_eventWithEmptyTo_returnsEventCommand() throws DukeException {
        Command command = Parser.parse("event meeting /from 2pm /to ");
        assertTrue(command instanceof EventCommand,
                "Parsing event with empty /to should return EventCommand");
    }

    @Test
    public void parse_eventWithToAtEnd_returnsEventCommand() throws DukeException {
        Command command = Parser.parse("event meeting /from 2pm /to");
        assertTrue(command instanceof EventCommand,
                "Parsing event with /to at end should return EventCommand");
    }

    @Test
    public void parse_eventWithWrongOrder_throwsException() {
        assertThrows(DukeException.class, () -> {
            Parser.parse("event meeting /to 4pm /from 2pm");
        }, "Parsing event with /to before /from should throw DukeException");
    }

    @Test
    public void parse_eventWithoutSlashes_throwsException() {
        assertThrows(DukeException.class, () -> {
            Parser.parse("event meeting from 2pm to 4pm");
        }, "Parsing event without '/' before 'from' and 'to' should throw DukeException");
    }

    // Test find/on command parsing

    @Test
    public void parse_onWithDate_returnsFindCommand() throws DukeException {
        Command command = Parser.parse("on 2024-06-15");
        assertTrue(command instanceof FindCommand,
                "Parsing 'on 2024-06-15' should return FindCommand");
    }

    @Test
    public void parse_onWithoutDate_returnsFindCommand() throws DukeException {
        Command command = Parser.parse("on");
        assertTrue(command instanceof FindCommand,
                "Parsing 'on' without date should return FindCommand (with empty date)");
    }

    @Test
    public void parse_onWithSpaces_returnsFindCommand() throws DukeException {
        Command command = Parser.parse("on   2024-06-15");
        assertTrue(command instanceof FindCommand,
                "Parsing 'on' with extra spaces should return FindCommand");
    }

    // Test search/find command parsing

    @Test
    public void parse_findWithKeyword_returnsSearchCommand() throws DukeException {
        Command command = Parser.parse("find book");
        assertTrue(command instanceof SearchCommand,
                "Parsing 'find book' should return SearchCommand");
    }

    @Test
    public void parse_findWithoutKeyword_returnsSearchCommand() throws DukeException {
        Command command = Parser.parse("find");
        assertTrue(command instanceof SearchCommand,
                "Parsing 'find' without keyword should return SearchCommand (with empty keyword)");
    }

    @Test
    public void parse_findWithSpaces_returnsSearchCommand() throws DukeException {
        Command command = Parser.parse("find   book");
        assertTrue(command instanceof SearchCommand,
                "Parsing 'find' with extra spaces should return SearchCommand");
    }

    @Test
    public void parse_findWithMultipleWords_returnsSearchCommand() throws DukeException {
        Command command = Parser.parse("find read book");
        assertTrue(command instanceof SearchCommand,
                "Parsing 'find' with multiple words should return SearchCommand");
    }

    // Test edge cases

    @Test
    public void parse_commandWithLeadingSpaces_returnsInvalidCommand() throws DukeException {
        Command command = Parser.parse("  list");
        assertTrue(command instanceof InvalidCommand,
                "Command with leading spaces should return InvalidCommand");
    }

    @Test
    public void parse_commandWithTrailingSpaces_returnsInvalidCommand() throws DukeException {
        Command command = Parser.parse("list   ");
        assertTrue(command instanceof InvalidCommand,
                "Command with trailing spaces should return InvalidCommand (does not match exactly)");
    }

    @Test
    public void parse_mixedCaseCommand_returnsInvalidCommand() throws DukeException {
        Command command = Parser.parse("List");
        assertTrue(command instanceof InvalidCommand,
                "Mixed case command should return InvalidCommand");
    }

    @Test
    public void parse_todoStartingCommand_returnsInvalidCommand() throws DukeException {
        Command command = Parser.parse("todolist");
        assertTrue(command instanceof InvalidCommand,
                "Command starting with 'todo' but different should return InvalidCommand");
    }

    @Test
    public void parse_deadlineWithMultipleBy_usesFirstBy() throws DukeException {
        Command command = Parser.parse("deadline task /by Monday /by Tuesday");
        assertTrue(command instanceof DeadlineCommand,
                "Parsing deadline with multiple /by should use first occurrence");
    }

    @Test
    public void parse_eventWithMultipleFromTo_usesFirstOccurrences() throws DukeException {
        Command command = Parser.parse("event task /from 2pm /to 4pm /from 6pm /to 8pm");
        assertTrue(command instanceof EventCommand,
                "Parsing event with multiple /from /to should use first occurrences");
    }

    @Test
    public void parse_markWithZero_returnsMarkCommand() throws DukeException {
        Command command = Parser.parse("mark 0");
        assertTrue(command instanceof MarkCommand,
                "Parsing 'mark 0' should return MarkCommand (will be handled as invalid index later)");
    }

    @Test
    public void parse_markWithNegative_returnsMarkCommand() throws DukeException {
        Command command = Parser.parse("mark -1");
        assertTrue(command instanceof MarkCommand,
                "Parsing 'mark -1' should return MarkCommand (will be handled as invalid index later)");
    }

    @Test
    public void parse_deadlineWithSpacesAroundDelimiter_parsesCorrectly() throws DukeException {
        Command command = Parser.parse("deadline task  /by  Monday");
        assertTrue(command instanceof DeadlineCommand,
                "Parsing deadline with spaces around /by should work");
    }

    // Test sort command parsing

    @Test
    public void parse_sortWithValidCriterion_returnsSortCommand() throws DukeException {
        Command statusCommand = Parser.parse("sort status");
        assertTrue(statusCommand instanceof SortCommand,
                "Parsing 'sort status' should return SortCommand");

        Command descCommand = Parser.parse("sort description");
        assertTrue(descCommand instanceof SortCommand,
                "Parsing 'sort description' should return SortCommand");

        Command dateCommand = Parser.parse("sort date");
        assertTrue(dateCommand instanceof SortCommand,
                "Parsing 'sort date' should return SortCommand");
    }

    @Test
    public void parse_sortCaseInsensitive_returnsSortCommand() throws DukeException {
        Command command1 = Parser.parse("sort STATUS");
        assertTrue(command1 instanceof SortCommand,
                "Parsing 'sort STATUS' should return SortCommand");

        Command command2 = Parser.parse("sort Description");
        assertTrue(command2 instanceof SortCommand,
                "Parsing 'sort Description' should return SortCommand");

        Command command3 = Parser.parse("sort DATE");
        assertTrue(command3 instanceof SortCommand,
                "Parsing 'sort DATE' should return SortCommand");
    }

    @Test
    public void parse_sortWithoutCriterion_throwsException() {
        DukeException exception = assertThrows(DukeException.class, () -> {
            Parser.parse("sort");
        }, "Parsing 'sort' without criterion should throw DukeException");
        assertEquals("Sort criterion required.\n"
                + "     Usage: sort status / sort description / sort date", exception.getMessage());
    }

    @Test
    public void parse_sortWithInvalidCriterion_throwsException() {
        DukeException exception = assertThrows(DukeException.class, () -> {
            Parser.parse("sort banana");
        }, "Parsing 'sort banana' should throw DukeException");
        assertEquals("Invalid sort criterion.\n"
                + "     Usage: sort status / sort description / sort date", exception.getMessage());

        assertThrows(DukeException.class, () -> {
            Parser.parse("sort abc");
        }, "Parsing 'sort abc' should throw DukeException");
    }
}
