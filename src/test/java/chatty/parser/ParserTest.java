package chatty.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import chatty.exception.ChattyException;
import chatty.task.Deadline;
import chatty.task.Event;
import chatty.task.RecurrenceUnit;
import chatty.task.RecurringTask;
import chatty.task.Task;
import chatty.task.Todo;

/**
 * Tests command, task, and task-number parsing behavior.
 */
public class ParserTest {
    /** Verifies that supported command inputs map to their command types. */
    @Test
    public void parseCommand_supportedInputs_correctCommandTypes() {
        assertEquals(CommandType.BYE, Parser.parseCommand("bye"));
        assertEquals(CommandType.LIST, Parser.parseCommand("list"));
        assertEquals(CommandType.FIND, Parser.parseCommand("find book"));
        assertEquals(CommandType.MARK, Parser.parseCommand("mark 2"));
        assertEquals(CommandType.UNMARK, Parser.parseCommand("unmark 2"));
        assertEquals(CommandType.DELETE, Parser.parseCommand("delete 2"));
        assertEquals(CommandType.TODO, Parser.parseCommand("todo read book"));
        assertEquals(CommandType.DEADLINE,
                Parser.parseCommand("deadline submit report /by 2026-10-15"));
        assertEquals(CommandType.EVENT,
                Parser.parseCommand("event meeting /from 2pm /to 4pm"));
        assertEquals(CommandType.RECURRING,
                Parser.parseCommand("recurring meeting /on 2026-09-14 /every 1 week"));
    }

    /** Verifies that unsupported command inputs map to {@link CommandType#UNKNOWN}. */
    @Test
    public void parseCommand_unsupportedInputs_unknownCommand() {
        assertEquals(CommandType.UNKNOWN, Parser.parseCommand(""));
        assertEquals(CommandType.UNKNOWN, Parser.parseCommand("list everything"));
        assertEquals(CommandType.UNKNOWN, Parser.parseCommand("goodbye"));
        assertEquals(CommandType.UNKNOWN, Parser.parseCommand("todos read book"));
    }

    /**
     * Verifies that a valid find command returns its stripped keyword.
     *
     * @throws ChattyException if a valid keyword is rejected unexpectedly.
     */
    @Test
    public void parseFindKeyword_validKeyword_keywordReturned() throws ChattyException {
        assertEquals("book", Parser.parseFindKeyword("find   book"));
    }

    /** Verifies that a find command without a keyword raises {@link ChattyException}. */
    @Test
    public void parseFindKeyword_missingKeyword_exceptionThrown() {
        assertThrows(ChattyException.class, () -> Parser.parseFindKeyword("find"));
    }

    /**
     * Verifies that valid add commands create tasks with all supplied details.
     *
     * @throws ChattyException if a valid command is rejected unexpectedly.
     */
    @Test
    public void parseTask_validAddCommands_tasksCreatedWithDetails() throws ChattyException {
        Task parsedTodo = Parser.parseTask("todo read book", CommandType.TODO);
        Todo todo = assertInstanceOf(Todo.class, parsedTodo);
        assertEquals("read book", todo.getDescription());

        Task parsedDeadline = Parser.parseTask(
                "deadline submit report /by 2026-10-15", CommandType.DEADLINE);
        Deadline deadline = assertInstanceOf(Deadline.class, parsedDeadline);
        assertEquals("submit report", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 10, 15), deadline.getDueDate());

        Task parsedEvent = Parser.parseTask(
                "event project meeting /from 2pm /to 4pm", CommandType.EVENT);
        Event event = assertInstanceOf(Event.class, parsedEvent);
        assertEquals("project meeting", event.getDescription());
        assertEquals("2pm", event.getFrom());
        assertEquals("4pm", event.getTo());

        Task parsedRecurringTask = Parser.parseTask(
                "recurring project meeting /on 2026-09-14 /every 1 week",
                CommandType.RECURRING);
        RecurringTask recurringTask = assertInstanceOf(RecurringTask.class, parsedRecurringTask);
        assertEquals("project meeting", recurringTask.getDescription());
        assertEquals(LocalDate.of(2026, 9, 14), recurringTask.getNextOccurrenceDate());
        assertEquals(1, recurringTask.getInterval());
        assertEquals(RecurrenceUnit.WEEK, recurringTask.getRecurrenceUnit());
    }

    /** Verifies that missing or invalid task details raise {@link ChattyException}. */
    @Test
    public void parseTask_missingOrInvalidDetails_exceptionThrown() {
        assertThrows(ChattyException.class, () ->
                Parser.parseTask("todo", CommandType.TODO));
        assertThrows(ChattyException.class, () ->
                Parser.parseTask("deadline submit report", CommandType.DEADLINE));
        assertThrows(ChattyException.class, () ->
                Parser.parseTask("deadline submit report /by 2026-02-30", CommandType.DEADLINE));
        assertThrows(ChattyException.class, () -> Parser.parseTask(
                "deadline submit report /by 2026-09-20 /by 2026-09-21",
                CommandType.DEADLINE));
        assertThrows(ChattyException.class, () ->
                Parser.parseTask("event meeting /from 2pm", CommandType.EVENT));
        assertThrows(ChattyException.class, () -> Parser.parseTask(
                "event meeting /from 2pm /to 3pm /to 4pm", CommandType.EVENT));
        assertThrows(ChattyException.class, () ->
                Parser.parseTask("recurring meeting /on 2026-09-14", CommandType.RECURRING));
        assertThrows(ChattyException.class, () -> Parser.parseTask(
                "recurring meeting /on 2026-02-30 /every 1 week", CommandType.RECURRING));
        assertThrows(ChattyException.class, () -> Parser.parseTask(
                "recurring meeting /on 2026-09-14 /every 0 weeks", CommandType.RECURRING));
        assertThrows(ChattyException.class, () -> Parser.parseTask(
                "recurring meeting /on 2026-09-14 /every 1 fortnight", CommandType.RECURRING));
        assertThrows(ChattyException.class, () -> Parser.parseTask(
                "recurring meeting /on 2026-09-14 /every 1 week /every 2 weeks",
                CommandType.RECURRING));
        assertThrows(ChattyException.class, () ->
                Parser.parseTask("todo read | revise", CommandType.TODO));
        assertThrows(ChattyException.class, () -> Parser.parseTask(
                "event meeting /from room | one /to 4pm", CommandType.EVENT));
        assertThrows(ChattyException.class, () ->
                Parser.parseTask("list", CommandType.LIST));
    }

    /**
     * Verifies that valid task numbers are parsed as one-based integers.
     *
     * @throws ChattyException if a valid task number is rejected unexpectedly.
     */
    @Test
    public void parseTaskNumber_validNumber_numberReturned() throws ChattyException {
        assertEquals(12, Parser.parseTaskNumber("mark 12", CommandType.MARK));
        assertEquals(3, Parser.parseTaskNumber("delete   3", CommandType.DELETE));
    }

    /** Verifies that missing or non-integer task numbers raise {@link ChattyException}. */
    @Test
    public void parseTaskNumber_missingOrNonIntegerNumber_exceptionThrown() {
        assertThrows(ChattyException.class, () ->
                Parser.parseTaskNumber("mark", CommandType.MARK));
        assertThrows(ChattyException.class, () ->
                Parser.parseTaskNumber("delete two", CommandType.DELETE));
        assertThrows(ChattyException.class, () ->
                Parser.parseTaskNumber("unmark 1.5", CommandType.UNMARK));
    }
}
