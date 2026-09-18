package benjamin.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import benjamin.BenjaminException;
import benjamin.command.AddCommand;
import benjamin.command.Command;
import benjamin.command.DeleteCommand;
import benjamin.command.ExitCommand;
import benjamin.command.FindCommand;
import benjamin.command.ListCommand;
import benjamin.command.MarkCommand;
import benjamin.command.OnCommand;
import benjamin.command.UnmarkCommand;
import benjamin.task.Task;

public class ParserTest {
    @Test
    public void parse_bye_returnsExitCommand() throws BenjaminException {
        Command command = Parser.parse("bye");

        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.isExit());
    }

    @Test
    public void parse_mixedCaseKeyword_stillRecognized() throws BenjaminException {
        assertInstanceOf(ExitCommand.class, Parser.parse("ByE"));
        assertInstanceOf(ListCommand.class, Parser.parse("LIST"));
    }

    @Test
    public void parse_list_returnsListCommandThatDoesNotExit() throws BenjaminException {
        Command command = Parser.parse("list");

        assertInstanceOf(ListCommand.class, command);
        assertFalse(command.isExit());
    }

    @Test
    public void parse_eachTaskKeyword_returnsAddCommand() throws BenjaminException {
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class, Parser.parse("deadline essay /by 2019-10-15"));
        assertInstanceOf(AddCommand.class,
                Parser.parse("event camp /from 2019-08-06 /to 2019-08-08"));
    }

    @Test
    public void parse_mark_returnsMarkCommand() throws BenjaminException {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
    }

    @Test
    public void parse_unmark_returnsUnmarkCommand() throws BenjaminException {
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
    }

    @Test
    public void parse_delete_returnsDeleteCommand() throws BenjaminException {
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
    }

    @Test
    public void parse_on_returnsOnCommand() throws BenjaminException {
        assertInstanceOf(OnCommand.class, Parser.parse("on 2019-10-15"));
    }

    @Test
    public void parse_find_returnsFindCommand() throws BenjaminException {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    public void parseKeyword_wordAfterFind_returnsIt() throws BenjaminException {
        assertEquals("book", Parser.parseKeyword("find book"));
    }

    @Test
    public void parseKeyword_severalWords_keepsThemAll() throws BenjaminException {
        assertEquals("read book", Parser.parseKeyword("find read book"));
    }

    @Test
    public void parseKeyword_extraSpaces_trimmedAway() throws BenjaminException {
        assertEquals("book", Parser.parseKeyword("find    book   "));
    }

    @Test
    public void parseKeyword_missingKeyword_exceptionThrown() {
        BenjaminException exception = assertThrows(
                BenjaminException.class, () -> Parser.parseKeyword("find"));
        assertEquals("Please provide a keyword after find.", exception.getMessage());
    }

    @Test
    public void parse_unknownWord_exceptionThrown() {
        BenjaminException exception = assertThrows(BenjaminException.class, () -> Parser.parse("blah"));
        assertTrue(exception.getMessage().contains("I answer to list, todo"));
    }

    @Test
    public void parse_emptyInput_exceptionThrown() {
        assertThrows(BenjaminException.class, () -> Parser.parse(""));
    }

    @Test
    public void parse_outOfRangeNumber_acceptedUntilTheCommandRuns() throws BenjaminException {
        // The list is not known at parse time, so only the wording is checked here.
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 999"));
    }

    @Test
    public void parseTask_todo_keepsDescription() throws BenjaminException {
        Task task = Parser.parseTask("todo read book", CommandType.TODO);

        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void parseTask_todoWithoutDescription_exceptionThrown() {
        BenjaminException exception = assertThrows(
                BenjaminException.class, () -> Parser.parseTask("todo", CommandType.TODO));
        assertEquals("The description of a todo cannot be empty.", exception.getMessage());
    }

    @Test
    public void parseTask_todoWithOnlySpaces_exceptionThrown() {
        assertThrows(BenjaminException.class, () -> Parser.parseTask("todo    ", CommandType.TODO));
    }

    @Test
    public void parseTask_deadline_readsDescriptionAndDate() throws BenjaminException {
        Task task = Parser.parseTask("deadline return book /by 2/12/2019 1800",
                CommandType.DEADLINE);

        assertEquals("[D][ ] return book (by: Dec 02 2019, 6:00pm)", task.toString());
    }

    @Test
    public void parseTask_deadlineWithoutBy_exceptionThrown() {
        BenjaminException exception = assertThrows(
                BenjaminException.class, () -> Parser.parseTask("deadline return book", CommandType.DEADLINE));
        assertEquals("A deadline needs a /by date or time.", exception.getMessage());
    }

    @Test
    public void parseTask_deadlineWithEmptyBy_exceptionThrown() {
        assertThrows(
                BenjaminException.class, () -> Parser.parseTask("deadline return book /by", CommandType.DEADLINE));
    }

    @Test
    public void parseTask_deadlineWithoutDescription_exceptionThrown() {
        assertThrows(
                BenjaminException.class, () -> Parser.parseTask("deadline /by 2019-10-15", CommandType.DEADLINE));
    }

    @Test
    public void parseTask_deadlineWithUnreadableDate_exceptionThrown() {
        assertThrows(BenjaminException.class, () ->
                Parser.parseTask("deadline return book /by June 6th", CommandType.DEADLINE));
    }

    @Test
    public void parseTask_event_readsBothEnds() throws BenjaminException {
        Task task = Parser.parseTask("event camp /from 2019-08-06 1400 /to 2019-08-08 1600",
                CommandType.EVENT);

        assertEquals("[E][ ] camp (from: Aug 06 2019, 2:00pm to: Aug 08 2019, 4:00pm)",
                task.toString());
    }

    @Test
    public void parseTask_eventWithoutFrom_exceptionThrown() {
        assertThrows(
                BenjaminException.class, () -> Parser.parseTask("event camp /to 2019-08-08", CommandType.EVENT));
    }

    @Test
    public void parseTask_eventWithoutTo_exceptionThrown() {
        assertThrows(
                BenjaminException.class, () -> Parser.parseTask("event camp /from 2019-08-06", CommandType.EVENT));
    }

    @Test
    public void parseTaskNumber_validNumber_returnsIt() throws BenjaminException {
        assertEquals(2, Parser.parseTaskNumber("mark 2", "mark"));
        assertEquals(12, Parser.parseTaskNumber("delete   12  ", "delete"));
    }

    @Test
    public void parseTaskNumber_missingNumber_exceptionThrown() {
        BenjaminException exception = assertThrows(
                BenjaminException.class, () -> Parser.parseTaskNumber("mark", "mark"));
        assertEquals("Please provide a task number after mark.", exception.getMessage());
    }

    @Test
    public void parseTaskNumber_notANumber_exceptionThrown() {
        BenjaminException exception = assertThrows(
                BenjaminException.class, () -> Parser.parseTaskNumber("mark abc", "mark"));
        assertEquals("The task number after mark must be a whole number.", exception.getMessage());
    }

    @Test
    public void parseDate_validDate_returnsThatDay() throws BenjaminException {
        assertEquals(LocalDate.of(2019, 10, 15), Parser.parseDate("on 2019-10-15"));
    }

    @Test
    public void parseDate_dateWithTime_dropsTheTime() throws BenjaminException {
        assertEquals(LocalDate.of(2019, 10, 15), Parser.parseDate("on 2019-10-15 1800"));
    }

    @Test
    public void parseDate_missingDate_exceptionThrown() {
        BenjaminException exception = assertThrows(BenjaminException.class, () -> Parser.parseDate("on"));
        assertEquals("Please provide a date after on.", exception.getMessage());
    }

    @Test
    public void parseDate_unreadableDate_exceptionThrown() {
        assertThrows(BenjaminException.class, () -> Parser.parseDate("on rubbish"));
    }
}
