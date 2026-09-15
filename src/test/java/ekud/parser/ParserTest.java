package ekud.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ekud.EkudException;
import ekud.command.ByeCommand;
import ekud.command.Command;
import ekud.command.CommandType;
import ekud.command.DeadlineCommand;
import ekud.command.DeleteCommand;
import ekud.command.EventCommand;
import ekud.command.FindCommand;
import ekud.command.HelpCommand;
import ekud.command.ListCommand;
import ekud.command.MarkCommand;
import ekud.command.OnCommand;
import ekud.command.TaskCreatingCommand;
import ekud.command.TodoCommand;
import ekud.command.UnmarkCommand;
import ekud.task.Deadline;
import ekud.task.Event;
import ekud.task.Task;
import ekud.task.Todo;

/**
 * Tests {@link Parser} command recognition, argument checks, and flag matching.
 */
public class ParserTest {
    @Test
    public void parse_todo_returnsTodoCommand() throws EkudException {
        Command command = Parser.parse("todo read book");
        assertInstanceOf(TodoCommand.class, command);
        Task task = ((TaskCreatingCommand) command).createTask();
        assertInstanceOf(Todo.class, task);
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void parse_deadline_returnsDeadlineCommand() throws EkudException {
        Command command = Parser.parse("deadline return book /by 2019-12-02");
        assertInstanceOf(DeadlineCommand.class, command);
        Task task = ((TaskCreatingCommand) command).createTask();
        assertInstanceOf(Deadline.class, task);
        assertEquals("[D][ ] return book (by: Dec 02 2019)", task.toString());
    }

    @Test
    public void parse_event_returnsEventCommand() throws EkudException {
        Command command = Parser.parse("event meeting /from 2019-12-02 1400 /to 2019-12-02 1600");
        assertInstanceOf(EventCommand.class, command);
        Task task = ((TaskCreatingCommand) command).createTask();
        assertInstanceOf(Event.class, task);
        assertEquals("[E][ ] meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)",
                task.toString());

        Task overnight = ((TaskCreatingCommand) Parser.parse(
                "event camp /from 2019-12-01 1600 /to 2019-12-02 1400")).createTask();
        assertEquals("camp", overnight.getDescription());
    }

    @Test
    public void parse_listAndBye_success() throws EkudException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        Command bye = Parser.parse("bye");
        assertInstanceOf(ByeCommand.class, bye);
        assertTrue(bye.isExit());
        assertFalse(Parser.parse("list").isExit());
    }

    @Test
    public void parse_help_success() throws EkudException {
        Command command = Parser.parse("help");
        assertInstanceOf(HelpCommand.class, command);
        assertFalse(command.isExit());
    }

    @Test
    public void parse_helpWithArguments_throwsEkudException() {
        EkudException exception = assertThrows(EkudException.class, () -> Parser.parse("help me"));
        assertEquals("The \"help\" command does not take any arguments.", exception.getMessage());
    }

    @Test
    public void parse_mark_returnsMarkCommand() throws EkudException {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
    }

    @Test
    public void parse_find_returnsFindCommand() throws EkudException {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        assertInstanceOf(FindCommand.class, Parser.parse("find read book"));
    }

    @Test
    public void parse_findWithoutKeyword_throwsEkudException() {
        EkudException exception = assertThrows(EkudException.class, () -> Parser.parse("find"));
        assertEquals("Please provide a keyword, e.g. find book.", exception.getMessage());
        assertThrows(EkudException.class, () -> Parser.parse("find   "));
    }

    @Test
    public void parse_trimsWhitespace() throws EkudException {
        assertInstanceOf(ListCommand.class, Parser.parse("  list  "));
        Task task = ((TaskCreatingCommand) Parser.parse("  todo   read book  ")).createTask();
        assertEquals("read book", task.getDescription());
    }

    @Test
    public void parse_blankInput_throwsEkudException() {
        EkudException exception = assertThrows(EkudException.class, () -> Parser.parse(""));
        assertEquals("Please enter a command (" + CommandType.getHelpList() + ").", exception.getMessage());
        assertThrows(EkudException.class, () -> Parser.parse("   "));
        assertThrows(EkudException.class, () -> Parser.parse(null));
    }

    @Test
    public void parse_unknownCommand_throwsEkudException() {
        EkudException exception = assertThrows(EkudException.class, () -> Parser.parse("hello"));
        assertEquals("I don't recognize that command. Try " + CommandType.getHelpList() + ".",
                exception.getMessage());
    }

    @Test
    public void parse_commandWordsAreCaseSensitive() {
        assertThrows(EkudException.class, () -> Parser.parse("TODO read book"));
        assertThrows(EkudException.class, () -> Parser.parse("List"));
    }

    @Test
    public void parse_listWithArguments_throwsEkudException() {
        EkudException exception = assertThrows(EkudException.class, () -> Parser.parse("list all"));
        assertEquals("The \"list\" command does not take any arguments.", exception.getMessage());
    }

    @Test
    public void parse_todoWithoutDescription_throwsEkudException() {
        EkudException exception = assertThrows(EkudException.class, () -> Parser.parse("todo"));
        assertEquals("The description of a todo cannot be empty.", exception.getMessage());
    }

    @Test
    public void parse_eventEndBeforeStart_throwsEkudException() {
        EkudException exception = assertThrows(EkudException.class, () -> Parser.parse(
                "event meeting /from 2019-12-02 1600 /to 2019-12-02 1400"));
        assertEquals("The event end date/time cannot be before the start date/time.",
                exception.getMessage());
    }

    @Test
    public void parseOneBasedIndex_missingOrInvalid_throwsEkudException() {
        EkudException missing = assertThrows(EkudException.class, () ->
                Parser.parseOneBasedIndex("mark", ""));
        assertEquals("Please provide a task number, e.g. mark 1.", missing.getMessage());

        EkudException extra = assertThrows(EkudException.class, () ->
                Parser.parseOneBasedIndex("delete", "1 2"));
        assertEquals("The \"delete\" command takes exactly one task number.", extra.getMessage());

        EkudException notNumber = assertThrows(EkudException.class, () ->
                Parser.parseOneBasedIndex("unmark", "abc"));
        assertEquals("Task number must be an integer, e.g. unmark 1.", notNumber.getMessage());
    }

    @Test
    public void parseOneBasedIndex_validNumber_success() throws EkudException {
        assertEquals(3, Parser.parseOneBasedIndex("mark", "3"));
    }

    @Test
    public void parse_on_returnsOnCommand() throws EkudException {
        assertInstanceOf(OnCommand.class, Parser.parse("on 2019-12-02"));
        assertInstanceOf(OnCommand.class, Parser.parse("on 2/12/2019 1800"));
    }

    @Test
    public void parse_onWithoutDate_throwsEkudException() {
        EkudException exception = assertThrows(EkudException.class, () -> Parser.parse("on"));
        assertEquals("Please provide a date, e.g. on 2019-12-02.", exception.getMessage());
    }

    @Test
    public void parse_unmarkAndDelete_returnIndexCommands() throws EkudException {
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 2"));
    }

    @Test
    public void parse_byeWithArguments_throwsEkudException() {
        EkudException exception = assertThrows(EkudException.class, () -> Parser.parse("bye now"));
        assertEquals("The \"bye\" command does not take any arguments.", exception.getMessage());
    }

    @Test
    public void parse_deadlineEmptyDescriptionOrBy_throwsEkudException() {
        EkudException blank = assertThrows(EkudException.class, () -> Parser.parse("deadline"));
        assertEquals("The description of a deadline cannot be empty.", blank.getMessage());

        EkudException missingBy = assertThrows(EkudException.class, () -> Parser.parse("deadline return book"));
        assertEquals("Please provide a deadline using /by, e.g. deadline return book /by 2019-12-02.",
                missingBy.getMessage());

        EkudException emptyDescription = assertThrows(EkudException.class, () -> Parser.parse(
                "deadline /by 2019-12-02"));
        assertEquals("The description of a deadline cannot be empty.", emptyDescription.getMessage());

        EkudException emptyBy = assertThrows(EkudException.class, () -> Parser.parse("deadline return book /by"));
        assertEquals("Please provide a date/time after /by, e.g. deadline return book /by 2019-12-02.",
                emptyBy.getMessage());
    }

    @Test
    public void parse_eventMissingParts_throwsEkudException() {
        EkudException blank = assertThrows(EkudException.class, () -> Parser.parse("event"));
        assertEquals("The description of an event cannot be empty.", blank.getMessage());

        EkudException missingFlags = assertThrows(EkudException.class, () -> Parser.parse("event meeting"));
        assertTrue(missingFlags.getMessage().contains("Please provide both /from and /to"));

        EkudException reversed = assertThrows(EkudException.class, () -> Parser.parse(
                "event meeting /to 2019-12-02 1600 /from 2019-12-02 1400"));
        assertTrue(reversed.getMessage().contains("Please put /from before /to"));

        EkudException emptyDescription = assertThrows(EkudException.class, () -> Parser.parse(
                "event /from 2019-12-02 /to 2019-12-03"));
        assertEquals("The description of an event cannot be empty.", emptyDescription.getMessage());

        EkudException emptyFrom = assertThrows(EkudException.class, () -> Parser.parse(
                "event meeting /from /to 2019-12-03"));
        assertEquals("Please provide a start date/time after /from.", emptyFrom.getMessage());

        EkudException emptyTo = assertThrows(EkudException.class, () -> Parser.parse(
                "event meeting /from 2019-12-02 /to"));
        assertEquals("Please provide an end date/time after /to.", emptyTo.getMessage());
    }

    @Test
    public void parse_eventEndDateBeforeStartDate_throwsEkudException() {
        EkudException exception = assertThrows(EkudException.class, () -> Parser.parse(
                "event camp /from 2019-12-03 /to 2019-12-02"));
        assertEquals("The event end date/time cannot be before the start date/time.",
                exception.getMessage());
    }

    @Test
    public void indexOfFlag_matchesWholeTokenOnly() {
        assertEquals(12, Parser.indexOfFlag("return book /by 2019-12-02", "/by"));
        assertEquals(0, Parser.indexOfFlag("/by 2019-12-02", "/by"));
        assertEquals(2, Parser.indexOfFlag("x /by", "/by"));
        // "/by" inside "/bye" must not count as the deadline flag.
        assertEquals(-1, Parser.indexOfFlag("say goodbye /bye later", "/by"));
        assertEquals(-1, Parser.indexOfFlag("no flags here", "/by"));
    }
}
