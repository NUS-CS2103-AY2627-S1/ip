package yapbot.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import yapbot.command.AddCommand;
import yapbot.command.ByeCommand;
import yapbot.command.Command;
import yapbot.command.DeleteCommand;
import yapbot.command.FindCommand;
import yapbot.command.ListCommand;
import yapbot.command.MarkCommand;
import yapbot.command.PriorityCommand;
import yapbot.command.UnmarkCommand;
import yapbot.exception.YapBotException;
import yapbot.task.Deadline;
import yapbot.task.Event;
import yapbot.task.Priority;
import yapbot.task.Task;
import yapbot.task.Todo;

public class ParserTest {

    @Test
    public void parse_bye_returnsExitingByeCommand() throws YapBotException {
        Command command = Parser.parse("bye");

        assertInstanceOf(ByeCommand.class, command);
        assertTrue(command.isExit());
    }

    @Test
    public void parse_list_returnsNonExitingListCommand() throws YapBotException {
        Command command = Parser.parse("list");

        assertInstanceOf(ListCommand.class, command);
        assertFalse(command.isExit());
    }

    @Test
    public void parse_mark_returnsMarkCommand() throws YapBotException {
        Command command = Parser.parse("mark 2");

        assertInstanceOf(MarkCommand.class, command);
    }

    @Test
    public void parse_unmark_returnsUnmarkCommand() throws YapBotException {
        Command command = Parser.parse("unmark 2");

        assertInstanceOf(UnmarkCommand.class, command);
    }

    @Test
    public void parse_delete_returnsDeleteCommand() throws YapBotException {
        Command command = Parser.parse("delete 2");

        assertInstanceOf(DeleteCommand.class, command);
    }

    @Test
    public void parse_priority_returnsPriorityCommand() throws YapBotException {
        Command command = Parser.parse("priority 2 high");

        assertInstanceOf(PriorityCommand.class, command);
    }

    @Test
    public void parse_find_returnsFindCommand() throws YapBotException {
        Command command = Parser.parse("find book");

        assertInstanceOf(FindCommand.class, command);
    }

    @Test
    public void parse_findWithNoKeyword_returnsFindCommandWithoutThrowing() throws YapBotException {
        // Like mark/unmark/delete, the keyword is validated in FindCommand.execute(),
        // not at parse time, so this should return normally rather than throw.
        Command command = Parser.parse("find");

        assertInstanceOf(FindCommand.class, command);
    }

    @Test
    public void parse_todo_returnsAddCommandWithMatchingTodoTask() throws YapBotException {
        Command command = Parser.parse("todo read book");

        AddCommand addCommand = assertInstanceOf(AddCommand.class, command);
        Task task = addCommand.getTask();
        assertInstanceOf(Todo.class, task);
        assertEquals("read book", task.getDescription());
        assertEquals("T | 0 | read book", task.toFileFormat());
    }

    @Test
    public void parse_todoWithEmptyDescription_exceptionThrown() {
        assertThrows(YapBotException.class, () -> Parser.parse("todo"));
        assertThrows(YapBotException.class, () -> Parser.parse("todo   "));
    }

    @Test
    public void parse_todoWithPipeCharacter_exceptionThrown() {
        assertThrows(YapBotException.class, () -> Parser.parse("todo read | book"));
    }

    @Test
    public void parse_todoWithPriorityFlag_taskHasMatchingPriorityAndCleanDescription()
            throws YapBotException {
        Command command = Parser.parse("todo read book /priority high");

        Task task = ((AddCommand) command).getTask();
        assertEquals("read book", task.getDescription());
        assertEquals(Priority.HIGH, task.getPriority());
        assertEquals("T | 0 | read book | HIGH", task.toFileFormat());
    }

    @Test
    public void parse_todoWithMixedCasePriorityFlag_priorityMatchedCaseInsensitively()
            throws YapBotException {
        Command command = Parser.parse("todo read book /priority HIGH");

        Task task = ((AddCommand) command).getTask();
        assertEquals(Priority.HIGH, task.getPriority());
    }

    @Test
    public void parse_todoWithoutPriorityFlag_taskHasNoPriority() throws YapBotException {
        Command command = Parser.parse("todo read book");

        Task task = ((AddCommand) command).getTask();
        assertNull(task.getPriority());
    }

    @Test
    public void parse_todoWithPriorityFlagButNoLevel_exceptionThrown() {
        assertThrows(YapBotException.class, () -> Parser.parse("todo read book /priority"));
    }

    @Test
    public void parse_todoWithInvalidPriorityLevel_exceptionThrown() {
        assertThrows(YapBotException.class, () -> Parser.parse("todo read book /priority urgent"));
    }

    @Test
    public void parse_deadline_returnsAddCommandWithMatchingDeadlineTask() throws YapBotException {
        Command command = Parser.parse("deadline return book /by 2019-10-15");

        AddCommand addCommand = assertInstanceOf(AddCommand.class, command);
        Task task = addCommand.getTask();
        assertInstanceOf(Deadline.class, task);
        assertEquals("return book", task.getDescription());
        assertEquals("D | 0 | return book | 2019-10-15", task.toFileFormat());
    }

    @Test
    public void parse_deadlineMissingByClause_exceptionThrown() {
        assertThrows(YapBotException.class, () -> Parser.parse("deadline return book"));
    }

    @Test
    public void parse_deadlineWithInvalidDate_exceptionThrown() {
        assertThrows(YapBotException.class, () -> Parser.parse("deadline return book /by tomorrow"));
    }

    @Test
    public void parse_deadlineWithBlankDescriptionOrDate_exceptionThrown() {
        assertThrows(YapBotException.class, () -> Parser.parse("deadline /by 2019-10-15"));
        assertThrows(YapBotException.class, () -> Parser.parse("deadline return book /by "));
    }

    @Test
    public void parse_deadlineWithPriorityFlag_taskHasMatchingPriorityAndCleanByDate()
            throws YapBotException {
        Command command = Parser.parse("deadline return book /by 2019-10-15 /priority medium");

        Task task = ((AddCommand) command).getTask();
        assertEquals(Priority.MEDIUM, task.getPriority());
        assertEquals("D | 0 | return book | 2019-10-15 | MEDIUM", task.toFileFormat());
    }

    @Test
    public void parse_event_returnsAddCommandWithMatchingEventTask() throws YapBotException {
        Command command = Parser.parse("event project meeting /from Mon 2pm /to 4pm");

        AddCommand addCommand = assertInstanceOf(AddCommand.class, command);
        Task task = addCommand.getTask();
        assertInstanceOf(Event.class, task);
        assertEquals("project meeting", task.getDescription());
        assertEquals("E | 0 | project meeting | Mon 2pm | 4pm", task.toFileFormat());
    }

    @Test
    public void parse_eventMissingToClause_exceptionThrown() {
        assertThrows(YapBotException.class, () -> Parser.parse("event project meeting /from Mon 2pm"));
    }

    @Test
    public void parse_eventWithBlankField_exceptionThrown() {
        assertThrows(YapBotException.class,
                () -> Parser.parse("event project meeting /from  /to 4pm"));
    }

    @Test
    public void parse_eventWithPriorityFlag_taskHasMatchingPriorityAndCleanFromTo() throws YapBotException {
        Command command = Parser.parse("event project meeting /from Mon 2pm /to 4pm /priority low");

        Task task = ((AddCommand) command).getTask();
        assertEquals(Priority.LOW, task.getPriority());
        assertEquals("E | 0 | project meeting | Mon 2pm | 4pm | LOW", task.toFileFormat());
    }

    @Test
    public void parse_blankInput_exceptionThrown() {
        assertThrows(YapBotException.class, () -> Parser.parse(""));
        assertThrows(YapBotException.class, () -> Parser.parse("   "));
    }

    @Test
    public void parse_unrecognizedCommand_exceptionThrown() {
        assertThrows(YapBotException.class, () -> Parser.parse("frobnicate"));
    }

    @Test
    public void parse_wordSharingPrefixWithCommandWord_exceptionThrown() {
        // "markdown" etc. must NOT be misread as the "mark"/"find"/"todo" commands
        // just because they start with the same letters.
        assertThrows(YapBotException.class, () -> Parser.parse("markdown"));
        assertThrows(YapBotException.class, () -> Parser.parse("findable"));
        assertThrows(YapBotException.class, () -> Parser.parse("todoist read book"));
    }

    @Test
    public void parse_leadingAndTrailingWhitespaceAroundCommand_stillRecognized() throws YapBotException {
        Command bye = Parser.parse("  bye  ");
        Command list = Parser.parse("  list  ");

        assertInstanceOf(ByeCommand.class, bye);
        assertInstanceOf(ListCommand.class, list);
    }

    @Test
    public void parse_deadlineWithDuplicateByFlag_exceptionThrown() {
        assertThrows(YapBotException.class,
                () -> Parser.parse("deadline return book /by 2019-10-15 /by 2019-10-16"));
    }

    @Test
    public void parse_eventWithDuplicateFromFlag_exceptionThrown() {
        // Previously this silently dropped the real "/to" value ("4pm") instead of failing.
        assertThrows(YapBotException.class,
                () -> Parser.parse("event trip /from Mon 2pm /from Tue 3pm /to 4pm"));
    }

    @Test
    public void parse_eventWithDuplicateToFlag_exceptionThrown() {
        assertThrows(YapBotException.class,
                () -> Parser.parse("event trip /from Mon 2pm /to 4pm /to 5pm"));
    }

    @Test
    public void parse_eventWithFromDateAfterToDate_exceptionThrown() {
        assertThrows(YapBotException.class,
                () -> Parser.parse("event trip /from 2019-10-16 /to 2019-10-15"));
    }

    @Test
    public void parse_eventWithFromDateSameAsToDate_exceptionThrown() {
        assertThrows(YapBotException.class,
                () -> Parser.parse("event trip /from 2019-10-15 /to 2019-10-15"));
    }

    @Test
    public void parse_eventWithFromDateBeforeToDate_taskCreatedSuccessfully() throws YapBotException {
        Command command = Parser.parse("event trip /from 2019-10-15 /to 2019-10-16");

        assertInstanceOf(AddCommand.class, command);
    }

    @Test
    public void parse_eventWithNonDateFromTo_chronologicalCheckSkipped() throws YapBotException {
        // Free-text times (not valid dates) are left alone: no ordering is enforced on them.
        Command command = Parser.parse("event project meeting /from Mon 2pm /to 4pm");

        assertInstanceOf(AddCommand.class, command);
    }
}
