package lumi.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import lumi.command.CommandType;
import lumi.exception.LumiException;
import lumi.task.Deadline;
import lumi.task.Event;
import lumi.task.Task;
import lumi.task.Todo;

/**
 * Tests command, task, and task-index parsing at the boundary between user
 * input and Lumi's domain objects.
 */
public class ParserTest {
    @Test
    public void parseCommandType_exactCommands_correctTypesReturned() throws LumiException {
        assertEquals(CommandType.TODO, Parser.parseCommandType("todo"));
        assertEquals(CommandType.DEADLINE, Parser.parseCommandType("deadline"));
        assertEquals(CommandType.EVENT, Parser.parseCommandType("event"));
        assertEquals(CommandType.LIST, Parser.parseCommandType("list"));
        assertEquals(CommandType.FIND, Parser.parseCommandType("find"));
        assertEquals(CommandType.MARK, Parser.parseCommandType("mark"));
        assertEquals(CommandType.UNMARK, Parser.parseCommandType("unmark"));
        assertEquals(CommandType.DELETE, Parser.parseCommandType("delete"));
        assertEquals(CommandType.SNOOZE, Parser.parseCommandType("snooze"));
        assertEquals(CommandType.BYE, Parser.parseCommandType("bye"));
    }

    @Test
    public void parseCommandType_commandsThatAcceptArguments_correctTypesReturned()
            throws LumiException {
        assertEquals(CommandType.TODO,
                Parser.parseCommandType("todo read book"));
        assertEquals(CommandType.DEADLINE,
                Parser.parseCommandType("deadline return book /by 2019-10-15"));
        assertEquals(CommandType.EVENT,
                Parser.parseCommandType("event meeting /from Monday /to Tuesday"));
        assertEquals(CommandType.FIND, Parser.parseCommandType("find book"));
        assertEquals(CommandType.MARK, Parser.parseCommandType("mark 1"));
        assertEquals(CommandType.UNMARK, Parser.parseCommandType("unmark 1"));
        assertEquals(CommandType.DELETE, Parser.parseCommandType("delete 1"));
        assertEquals(CommandType.SNOOZE,
                Parser.parseCommandType("snooze 1 /to 2019-10-20"));
    }

    @Test
    public void parseCommandType_emptyInput_exceptionThrown() {
        assertLumiExceptionMessage("Signal unclear: please enter a command.", () -> Parser.parseCommandType(""));
    }

    @Test
    public void parseCommandType_unknownPartialOrExtraInput_exceptionThrown() {
        String expectedMessage = "Signal unclear: I don't recognize that command. "
                + "Try todo, deadline, event, list, find, mark, unmark, delete, snooze, or bye.";

        assertLumiExceptionMessage(expectedMessage, () -> Parser.parseCommandType("unknown"));
        assertLumiExceptionMessage(expectedMessage, () -> Parser.parseCommandType("todos"));
        assertLumiExceptionMessage(expectedMessage, () -> Parser.parseCommandType("findbook"));
        assertLumiExceptionMessage(expectedMessage, () -> Parser.parseCommandType("list later"));
        assertLumiExceptionMessage(expectedMessage, () -> Parser.parseCommandType("bye now"));
        assertLumiExceptionMessage(expectedMessage, () -> Parser.parseCommandType(" todo"));
    }

    @Test
    public void parseFindKeyword_validMultiWordKeyword_trimmedKeywordReturned()
            throws LumiException {
        assertEquals("return book", Parser.parseFindKeyword("find   return book   "));
    }

    @Test
    public void parseFindKeyword_missingKeyword_exceptionThrown() {
        assertLumiExceptionMessage(
                "Signal unclear: tell me what to find. Try: find <keyword>", () -> Parser.parseFindKeyword("find"));
        assertLumiExceptionMessage(
                "Signal unclear: tell me what to find. Try: find <keyword>", () -> Parser.parseFindKeyword("find   "));
    }

    @Test
    public void parseSnoozeRequest_validRequest_indexAndDateTimeReturned()
            throws LumiException {
        Parser.SnoozeRequest request = Parser.parseSnoozeRequest(
                "snooze 2 /to 20/10/2019 1800", 3);

        assertEquals(1, request.taskIndex());
        assertEquals(LocalDateTime.of(2019, 10, 20, 18, 0), request.newDateTime());
    }

    @Test
    public void parseSnoozeRequest_invalidSyntax_exceptionThrown() {
        String missingTaskMessage = "Signal unclear: tell me which task to snooze. "
                + "Try: snooze <task number> /to <when>";
        String missingDateMessage = "Signal unclear: tell me when to snooze the task until. "
                + "Try: snooze <task number> /to <when>";

        assertLumiExceptionMessage(missingTaskMessage, () ->
                Parser.parseSnoozeRequest("snooze", 3));
        assertLumiExceptionMessage(missingTaskMessage, () ->
                Parser.parseSnoozeRequest("snooze /to 2019-10-20", 3));
        assertLumiExceptionMessage(missingDateMessage, () ->
                Parser.parseSnoozeRequest("snooze 2", 3));
        assertLumiExceptionMessage(missingDateMessage, () ->
                Parser.parseSnoozeRequest("snooze 2 /towards 2019-10-20", 3));
        assertLumiExceptionMessage("Signal unclear: the /to value cannot be empty.", () ->
                Parser.parseSnoozeRequest("snooze 2 /to", 3));
        assertLumiExceptionMessage("Signal unclear: the /to parameter can only be used once.", () ->
                Parser.parseSnoozeRequest(
                        "snooze 2 /to 2019-10-20 /to 2019-10-21", 3));
    }

    @Test
    public void parseSnoozeRequest_invalidTaskNumberOrDate_exceptionThrown() {
        assertLumiExceptionMessage("Signal unclear: the task number must be a whole number.", () ->
                Parser.parseSnoozeRequest("snooze first /to 2019-10-20", 3));
        assertLumiExceptionMessage("Signal unclear: choose a task number from 1 to 3.", () ->
                Parser.parseSnoozeRequest("snooze 0 /to 2019-10-20", 3));
        assertLumiExceptionMessage("Signal unclear: choose a task number from 1 to 3.", () ->
                Parser.parseSnoozeRequest("snooze 4 /to 2019-10-20", 3));
        assertLumiExceptionMessage("Signal unclear: there are no tasks to snooze yet.", () ->
                Parser.parseSnoozeRequest("snooze 1 /to 2019-10-20", 0));
        assertLumiExceptionMessage(
                "Signal unclear: use a date like 2019-10-15 or 2/12/2019, "
                        + "optionally followed by a 24-hour time such as 1800.", () ->
                                Parser.parseSnoozeRequest("snooze 2 /to 2019-02-29", 3));
    }

    @Test
    public void parseTask_validTodo_todoCreated() throws LumiException {
        Task task = Parser.parseTask("todo   read book   ", CommandType.TODO);

        Todo todo = assertInstanceOf(Todo.class, task);
        assertEquals("read book", todo.getDescription());
        assertFalse(todo.isDone());
    }

    @Test
    public void parseTask_validDeadline_deadlineCreated() throws LumiException {
        Task task = Parser.parseTask("deadline return book /by 2/12/2019 1800",
                CommandType.DEADLINE);

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getBy());
        assertFalse(deadline.isDone());
    }

    @Test
    public void parseTask_validEvent_eventCreated() throws LumiException {
        Task task = Parser.parseTask(
                "event project meeting /from 2019-10-16 1400 /to 2019-10-16 1600",
                CommandType.EVENT);

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("project meeting", event.getDescription());
        assertEquals(LocalDateTime.of(2019, 10, 16, 14, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2019, 10, 16, 16, 0), event.getTo());
        assertFalse(event.isDone());
    }

    @Test
    public void parseTask_separatorTextInsideDescription_onlyCompleteTokenUsed()
            throws LumiException {
        Deadline deadline = assertInstanceOf(Deadline.class, Parser.parseTask(
                "deadline review /bypass syntax /by 2019-10-15", CommandType.DEADLINE));

        assertEquals("review /bypass syntax", deadline.getDescription());
    }

    @Test
    public void parseTask_todoWithoutDescription_exceptionThrown() {
        assertLumiExceptionMessage("Signal unclear: a todo needs a description. Try: todo <description>", () ->
                Parser.parseTask("todo   ", CommandType.TODO));
    }

    @Test
    public void parseTask_malformedDeadline_exceptionThrown() {
        assertLumiExceptionMessage(
                "Signal unclear: a deadline needs a due date. "
                        + "Try: deadline <description> /by <when>", () ->
                                Parser.parseTask("deadline return book", CommandType.DEADLINE));
        assertLumiExceptionMessage(
                "Signal unclear: a deadline needs a due date. "
                        + "Try: deadline <description> /by <when>", () -> Parser.parseTask(
                        "deadline return book /bypass tomorrow", CommandType.DEADLINE));
        assertLumiExceptionMessage(
                "Signal unclear: a deadline needs a description before /by.", () ->
                        Parser.parseTask("deadline /by 2019-10-15", CommandType.DEADLINE));
        assertLumiExceptionMessage("Signal unclear: the /by value cannot be empty.", () ->
                Parser.parseTask("deadline return book /by", CommandType.DEADLINE));
        assertLumiExceptionMessage("Signal unclear: the /by parameter can only be used once.", () ->
                Parser.parseTask(
                        "deadline return book /by 2019-10-15 /by 2019-10-16",
                        CommandType.DEADLINE));
    }

    @Test
    public void parseTask_malformedEvent_exceptionThrown() {
        String missingDetailsMessage = "Signal unclear: an event needs start and end details. "
                + "Try: event <description> /from <start> /to <end>";

        assertLumiExceptionMessage(missingDetailsMessage, () -> Parser.parseTask("event meeting", CommandType.EVENT));
        assertLumiExceptionMessage("Signal unclear: put /from before /to in an event.", () ->
                Parser.parseTask(
                        "event meeting /to 2019-10-16 /from 2019-10-15",
                        CommandType.EVENT));
        assertLumiExceptionMessage(
                "Signal unclear: an event needs a description before /from.", () -> Parser.parseTask(
                        "event /from 2019-10-15 /to 2019-10-16", CommandType.EVENT));
        assertLumiExceptionMessage("Signal unclear: the /from value cannot be empty.", () -> Parser.parseTask(
                        "event meeting /from /to 2019-10-16", CommandType.EVENT));
        assertLumiExceptionMessage("Signal unclear: the /to value cannot be empty.", () -> Parser.parseTask(
                        "event meeting /from 2019-10-15 /to", CommandType.EVENT));
        assertLumiExceptionMessage("Signal unclear: the /from parameter can only be used once.", () ->
                Parser.parseTask(
                        "event meeting /from 2019-10-15 /from 2019-10-16 /to 2019-10-17",
                        CommandType.EVENT));
        assertLumiExceptionMessage("Signal unclear: the /to parameter can only be used once.", () ->
                Parser.parseTask(
                        "event meeting /from 2019-10-15 /to 2019-10-16 /to 2019-10-17",
                        CommandType.EVENT));
    }

    @Test
    public void parseTask_eventEndNotAfterStart_exceptionThrown() {
        String expectedMessage = "Signal unclear: an event's end must be later than its start.";

        assertLumiExceptionMessage(expectedMessage, () -> Parser.parseTask(
                "event meeting /from 2019-10-16 1600 /to 2019-10-16 1400",
                CommandType.EVENT));
        assertLumiExceptionMessage(expectedMessage, () -> Parser.parseTask(
                "event meeting /from 2019-10-16 1400 /to 2019-10-16 1400",
                CommandType.EVENT));
    }

    @Test
    public void parseTask_invalidDate_exceptionThrown() {
        assertLumiExceptionMessage(
                "Signal unclear: use a date like 2019-10-15 or 2/12/2019, "
                        + "optionally followed by a 24-hour time such as 1800.", () -> Parser.parseTask(
                        "deadline impossible /by 2019-02-29", CommandType.DEADLINE));
    }

    @Test
    public void parseTask_nonTaskCommandType_exceptionThrown() {
        assertLumiExceptionMessage("Signal unclear: I don't recognize that task type.", () ->
                Parser.parseTask("list", CommandType.LIST));
    }

    @Test
    public void parseTaskIndex_firstAndLastTask_zeroBasedIndexesReturned() throws LumiException {
        assertEquals(0,
                Parser.parseTaskIndex("mark 1", CommandType.MARK, 3));
        assertEquals(2,
                Parser.parseTaskIndex("delete 3", CommandType.DELETE, 3));
    }

    @Test
    public void parseTaskIndex_missingNumber_actionSpecificExceptionThrown() {
        assertLumiExceptionMessage(
                "Signal unclear: tell me which task to mark. Try: mark <task number>", () ->
                        Parser.parseTaskIndex("mark", CommandType.MARK, 1));
        assertLumiExceptionMessage(
                "Signal unclear: tell me which task to unmark. Try: unmark <task number>", () ->
                        Parser.parseTaskIndex("unmark   ", CommandType.UNMARK, 1));
        assertLumiExceptionMessage(
                "Signal unclear: tell me which task to delete. Try: delete <task number>", () ->
                        Parser.parseTaskIndex("delete", CommandType.DELETE, 1));
    }

    @Test
    public void parseTaskIndex_nonWholeNumber_exceptionThrown() {
        String expectedMessage = "Signal unclear: the task number must be a whole number.";

        assertLumiExceptionMessage(expectedMessage, () -> Parser.parseTaskIndex("mark first", CommandType.MARK, 3));
        assertLumiExceptionMessage(expectedMessage, () -> Parser.parseTaskIndex("mark 1.5", CommandType.MARK, 3));
        assertLumiExceptionMessage(expectedMessage, () -> Parser.parseTaskIndex("mark 1 2", CommandType.MARK, 3));
        assertLumiExceptionMessage(expectedMessage, () -> Parser.parseTaskIndex(
                        "mark 999999999999999999999", CommandType.MARK, 3));
    }

    @Test
    public void parseTaskIndex_emptyTaskList_exceptionThrown() {
        assertLumiExceptionMessage("Signal unclear: there are no tasks to delete yet.", () ->
                Parser.parseTaskIndex("delete 1", CommandType.DELETE, 0));
    }

    @Test
    public void parseTaskIndex_numberOutsideList_exceptionThrown() {
        String expectedMessage = "Signal unclear: choose a task number from 1 to 3.";

        assertLumiExceptionMessage(expectedMessage, () -> Parser.parseTaskIndex("mark 0", CommandType.MARK, 3));
        assertLumiExceptionMessage(expectedMessage, () -> Parser.parseTaskIndex("mark -1", CommandType.MARK, 3));
        assertLumiExceptionMessage(expectedMessage, () -> Parser.parseTaskIndex("mark 4", CommandType.MARK, 3));
    }

    /** Verifies both the checked exception type and its user-facing explanation. */
    private static void assertLumiExceptionMessage(String expectedMessage, Executable action) {
        LumiException exception = assertThrows(LumiException.class, action);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
