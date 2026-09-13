package dulio.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import dulio.command.AddCommand;
import dulio.command.DeleteCommand;
import dulio.command.ExitCommand;
import dulio.command.FindCommand;
import dulio.command.ListCommand;
import dulio.command.MarkCommand;
import dulio.command.UnmarkCommand;
import dulio.exception.DulioException;
import dulio.task.Deadline;
import dulio.task.Event;
import dulio.task.RecurringTask;
import dulio.task.Task;
import dulio.task.Todo;

public class ParserTest {
    @Test
    public void parseDate_validIsoDate_returnsLocalDate() throws DulioException {
        assertEquals(LocalDate.of(2019, 10, 15), Parser.parseDate("2019-10-15"));
    }

    @Test
    public void parseDate_leapYearDate_returnsLocalDate() throws DulioException {
        assertEquals(LocalDate.of(2020, 2, 29), Parser.parseDate("2020-02-29"));
    }

    @Test
    public void parseDate_invalidFormat_throwsDulioException() {
        DulioException exception = assertThrows(DulioException.class, () -> Parser.parseDate("15/10/2019"));
        assertEquals("OOPS!!! Please enter dates in yyyy-MM-dd format.", exception.getMessage());
    }

    @Test
    public void parseDate_impossibleDate_throwsDulioException() {
        assertThrows(DulioException.class, () -> Parser.parseDate("2019-02-29"));
    }

    @Test
    public void parseTask_todoCommand_returnsTodo() throws DulioException {
        Task task = Parser.parseTask("todo borrow book");
        assertInstanceOf(Todo.class, task);
        assertEquals("borrow book", task.getDescription());
    }

    @Test
    public void parseTask_deadlineCommand_returnsDeadlineWithDate() throws DulioException {
        Task task = Parser.parseTask("deadline return book /by 2019-10-15");
        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals(LocalDate.of(2019, 10, 15), deadline.getBy());
    }

    @Test
    public void parseTask_eventCommand_returnsEvent() throws DulioException {
        Task task = Parser.parseTask("event project meeting /from Mon /to Tue");
        Event event = assertInstanceOf(Event.class, task);
        assertEquals("Mon", event.getFrom());
        assertEquals("Tue", event.getTo());
    }

    @Test
    public void parseTask_recurringCommand_returnsRecurringTask() throws DulioException {
        Task task = Parser.parseTask("recurring project meeting /every week");
        RecurringTask recurringTask = assertInstanceOf(RecurringTask.class, task);
        assertEquals("project meeting", recurringTask.getDescription());
        assertEquals("week", recurringTask.getInterval());
    }

    @Test
    public void parseTask_recurringWithoutInterval_throwsDulioException() {
        DulioException exception = assertThrows(
                DulioException.class, () -> Parser.parseTask("recurring project meeting"));
        assertEquals("OOPS!!! A recurring task needs a description and an /every interval.", exception.getMessage());
    }

    @Test
    public void parseStoredTask_recurringRecord_returnsRecurringTask() {
        Task task = Parser.parseStoredTask("R | 1 | project meeting | week");
        RecurringTask recurringTask = assertInstanceOf(RecurringTask.class, task);
        assertTrue(recurringTask.isDone());
        assertEquals("week", recurringTask.getInterval());
    }

    @Test
    public void parseTask_emptyTodo_throwsDulioException() {
        assertThrows(DulioException.class, () -> Parser.parseTask("todo"));
    }

    @Test
    public void parseTask_unknownCommand_throwsDulioException() {
        assertThrows(DulioException.class, () -> Parser.parseTask("blah"));
    }

    @Test
    public void parseTask_deadlineWithoutByDate_throwsDulioException() {
        assertThrows(DulioException.class, () -> Parser.parseTask("deadline return book"));
    }

    @Test
    public void parseTask_eventWithoutEndTime_throwsDulioException() {
        assertThrows(DulioException.class, () -> Parser.parseTask("event project meeting /from Mon"));
    }

    @Test
    public void parseCommand_bye_returnsExitCommand() throws DulioException {
        assertInstanceOf(ExitCommand.class, Parser.parseCommand("bye"));
    }

    @Test
    public void parseCommand_list_returnsListCommand() throws DulioException {
        assertInstanceOf(ListCommand.class, Parser.parseCommand("list"));
    }

    @Test
    public void parseCommand_delete_returnsDeleteCommand() throws DulioException {
        assertInstanceOf(DeleteCommand.class, Parser.parseCommand("delete 2"));
    }

    @Test
    public void parseCommand_mark_returnsMarkCommand() throws DulioException {
        assertInstanceOf(MarkCommand.class, Parser.parseCommand("mark 2"));
    }

    @Test
    public void parseCommand_unmark_returnsUnmarkCommand() throws DulioException {
        assertInstanceOf(UnmarkCommand.class, Parser.parseCommand("unmark 2"));
    }

    @Test
    public void parseCommand_taskCommand_returnsAddCommand() throws DulioException {
        assertInstanceOf(AddCommand.class, Parser.parseCommand("todo borrow book"));
    }

    @Test
    public void parseCommand_find_returnsFindCommand() throws DulioException {
        assertInstanceOf(FindCommand.class, Parser.parseCommand("find book"));
    }

    @Test
    public void parseCommand_emptyFindKeyword_throwsDulioException() {
        assertThrows(DulioException.class, () -> Parser.parseCommand("find"));
    }

    @Test
    public void parseCommand_nonNumericTaskNumber_throwsDulioException() {
        assertThrows(DulioException.class, () -> Parser.parseCommand("delete two"));
    }

    @Test
    public void parseCommand_nonNumericMarkNumber_throwsDulioException() {
        DulioException exception = assertThrows(DulioException.class, () -> Parser.parseCommand("mark one"));
        assertEquals("Invalid task index", exception.getMessage());
    }

    @Test
    public void parseCommand_nonNumericUnmarkNumber_throwsDulioException() {
        DulioException exception = assertThrows(DulioException.class, () -> Parser.parseCommand("unmark one"));
        assertEquals("Invalid task index", exception.getMessage());
    }

    @Test
    public void parseCommand_negativeTaskNumber_throwsDulioException() {
        assertThrows(DulioException.class, () -> Parser.parseCommand("delete -1"));
    }

    @Test
    public void parseCommand_zeroTaskNumber_throwsDulioException() {
        assertThrows(DulioException.class, () -> Parser.parseCommand("delete 0"));
    }

    @Test
    public void parseCommand_blankCommand_throwsDulioException() {
        DulioException exception = assertThrows(DulioException.class, () -> Parser.parseCommand("   "));
        assertEquals("OOPS!!! Please enter a command.", exception.getMessage());
    }

    @Test
    public void parseCommand_repeatedSpaces_throwsDulioException() {
        assertThrows(DulioException.class, () -> Parser.parseCommand("todo  read book"));
    }

    @Test
    public void parseCommand_leadingAndTrailingSpaces_throwsDulioException() {
        assertThrows(DulioException.class, () -> Parser.parseCommand(" todo read book "));
    }

    @Test
    public void parseTask_duplicateDeadlineParameter_throwsDulioException() {
        assertThrows(DulioException.class, () ->
                Parser.parseTask("deadline submit report /by 2025-01-01 /by 2025-01-02"));
    }

    @Test
    public void parseTask_duplicateEventParameter_throwsDulioException() {
        assertThrows(DulioException.class, () ->
                Parser.parseTask("event meeting /from Mon /from Tue /to Wed"));
    }

    @Test
    public void parseTask_impossibleDeadlineDate_throwsDulioException() {
        assertThrows(DulioException.class, () ->
                Parser.parseTask("deadline submit report /by 2025-02-30"));
    }

    @Test
    public void parseStoredTask_malformedRecord_returnsNull() {
        assertNull(Parser.parseStoredTask("D | 0 | submit report | not-a-date"));
    }
}
