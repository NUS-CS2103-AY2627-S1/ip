package panda.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import panda.command.AddDeadlineCommand;
import panda.command.AddEventCommand;
import panda.command.AddTodoCommand;
import panda.command.ByeCommand;
import panda.command.Command;
import panda.command.DeleteTaskCommand;
import panda.command.DisplayDateCommand;
import panda.command.FindCommand;
import panda.command.ListTasksCommand;
import panda.command.MarkTaskCommand;
import panda.command.SaveBeforeExitCommand;
import panda.command.TodayCommand;
import panda.command.UnmarkTaskCommand;
import panda.exception.parser.IllegalDateTimeException;
import panda.exception.parser.InvalidArgumentException;
import panda.exception.parser.NoCommandFoundException;

/**
 * Contains unit tests for {@link Parser}.
 */
public class ParserTest {

    @Test
    public void parse_validByeCommand_returnsByeCommand() throws Exception {
        Command command = Parser.parse("bye");
        assertInstanceOf(ByeCommand.class, command);
    }

    @Test
    public void parse_byeWithArguments_throwsInvalidArgumentException() {
        assertThrows(InvalidArgumentException.class, () -> Parser.parse("bye now"));
    }

    @Test
    public void parse_validSaveCommand_returnsSaveBeforeExitCommand() throws Exception {
        Command command = Parser.parse("save");
        assertInstanceOf(SaveBeforeExitCommand.class, command);
    }

    @Test
    public void parse_saveWithArguments_throwsInvalidArgumentException() {
        assertThrows(InvalidArgumentException.class, () -> Parser.parse("save now"));
    }

    @Test
    public void parse_validListCommand_returnsListTasksCommand() throws Exception {
        Command command = Parser.parse("list");
        assertInstanceOf(ListTasksCommand.class, command);
    }

    @Test
    public void parse_listWithArguments_throwsInvalidArgumentException() {
        assertThrows(InvalidArgumentException.class, () -> Parser.parse("list all"));
    }

    @Test
    public void parse_validTodoCommand_returnsAddTodoCommand() throws Exception {
        Command command = Parser.parse("todo read book");
        assertInstanceOf(AddTodoCommand.class, command);
    }

    @Test
    public void parse_todoMissingDescription_throwsInvalidArgumentException() {
        assertThrows(InvalidArgumentException.class, () -> Parser.parse("todo"));
        assertThrows(InvalidArgumentException.class, () -> Parser.parse("todo   "));
    }

    @Test
    public void parse_validDeadlineCommand_returnsAddDeadlineCommand() throws Exception {
        Command command = Parser.parse("deadline return book /by 15/10/2026 18:00");
        assertInstanceOf(AddDeadlineCommand.class, command);
    }

    @Test
    public void parse_deadlineMissingDelimiterOrArguments_throwsInvalidArgumentException() {
        assertThrows(InvalidArgumentException.class, () -> Parser.parse("deadline return book"));
        assertThrows(InvalidArgumentException.class, () -> Parser.parse("deadline /by 15/10/2026"));
    }

    @Test
    public void parse_deadlineInvalidDate_throwsIllegalDateTimeException() {
        assertThrows(IllegalDateTimeException.class, () -> Parser.parse("deadline return book /by not-a-date"));
    }

    @Test
    public void parse_validEventCommand_returnsAddEventCommand() throws Exception {
        Command command = Parser.parse("event meeting /from 15/10/2026 14:00 /to 15/10/2026 16:00");
        assertInstanceOf(AddEventCommand.class, command);
    }

    @Test
    public void parse_eventMissingParts_throwsInvalidArgumentException() {
        assertThrows(InvalidArgumentException.class, () -> Parser.parse("event meeting /from 15/10/2026 14:00"));
        assertThrows(InvalidArgumentException.class, () ->
                Parser.parse("event /from 15/10/2026 14:00 /to 15/10/2026 16:00"));
    }

    @Test
    public void parse_eventInvalidDate_throwsIllegalDateTimeException() {
        assertThrows(IllegalDateTimeException.class, () ->
                Parser.parse("event meeting /from invalid-date /to 15/10/2026 16:00"));
    }

    @Test
    public void parse_validMarkCommand_returnsMarkTaskCommand() throws Exception {
        Command command = Parser.parse("mark 2");
        assertInstanceOf(MarkTaskCommand.class, command);
    }

    @Test
    public void parse_markNonNumeric_throwsInvalidArgumentException() {
        assertThrows(InvalidArgumentException.class, () -> Parser.parse("mark"));
        assertThrows(InvalidArgumentException.class, () -> Parser.parse("mark abc"));
    }

    @Test
    public void parse_validUnmarkCommand_returnsUnmarkTaskCommand() throws Exception {
        Command command = Parser.parse("unmark 2");
        assertInstanceOf(UnmarkTaskCommand.class, command);
    }

    @Test
    public void parse_validDeleteCommand_returnsDeleteTaskCommand() throws Exception {
        Command command = Parser.parse("delete 3");
        assertInstanceOf(DeleteTaskCommand.class, command);
    }

    @Test
    public void parse_taskNumberOverflow_throwsInvalidArgumentException() {
        for (String keyword : new String[] {"mark", "unmark", "delete"}) {
            InvalidArgumentException exception = assertThrows(InvalidArgumentException.class, () ->
                    Parser.parse(keyword + " 2147483648"));
            assertEquals(
                    new InvalidArgumentException(keyword, keyword + " TASK_NUMBER").getMessage(),
                    exception.getMessage());
        }
    }

    @Test
    public void parse_invalidTaskNumbers_throwsInvalidArgumentException() {
        for (String keyword : new String[] {"mark", "unmark", "delete"}) {
            for (String argument : new String[] {"", "abc", "-1", "+1", "1.5"}) {
                assertThrows(InvalidArgumentException.class, () -> Parser.parse(keyword + " " + argument));
            }
        }
    }

    @Test
    public void parse_validTodayCommand_returnsTodayCommand() throws Exception {
        Command command = Parser.parse("today");
        assertInstanceOf(TodayCommand.class, command);
    }

    @Test
    public void parse_todayWithArguments_throwsInvalidArgumentException() {
        assertThrows(InvalidArgumentException.class, () -> Parser.parse("today tomorrow"));
    }

    @Test
    public void parse_validDisplayDateCommand_returnsDisplayDateCommand() throws Exception {
        Command command = Parser.parse("display /date 15/10/2026");
        assertInstanceOf(DisplayDateCommand.class, command);
    }

    @Test
    public void parse_displayMissingDate_throwsInvalidArgumentException() {
        assertThrows(InvalidArgumentException.class, () -> Parser.parse("display"));
        assertThrows(InvalidArgumentException.class, () -> Parser.parse("display /date"));
    }

    @Test
    public void parse_displayInvalidDate_throwsIllegalDateTimeException() {
        assertThrows(IllegalDateTimeException.class, () -> Parser.parse("display /date invalid-date"));
    }

    @Test
    public void parse_validFindCommand_returnsFindCommand() throws Exception {
        Command command = Parser.parse("find book");
        assertInstanceOf(FindCommand.class, command);
    }

    @Test
    public void parse_findMissingKeyword_throwsInvalidArgumentException() {
        assertThrows(InvalidArgumentException.class, () -> Parser.parse("find"));
        assertThrows(InvalidArgumentException.class, () -> Parser.parse("find   "));
    }

    @Test
    public void parse_emptyOrBlankInput_throwsNoCommandFoundException() {
        assertThrows(NoCommandFoundException.class, () -> Parser.parse(""));
        assertThrows(NoCommandFoundException.class, () -> Parser.parse("   "));
    }

    @Test
    public void parse_unknownCommand_throwsNoCommandFoundException() {
        assertThrows(NoCommandFoundException.class, () -> Parser.parse("hello world"));
        assertThrows(NoCommandFoundException.class, () -> Parser.parse("foobar"));
    }

    @Test
    public void parse_inputWithSurroundingWhitespace_parsedSuccessfully() throws Exception {
        Command command = Parser.parse("   list   ");
        assertInstanceOf(ListTasksCommand.class, command);
    }
}
