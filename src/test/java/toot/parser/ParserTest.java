package toot.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import toot.TootException;
import toot.task.Deadline;
import toot.task.Event;
import toot.task.Task;
import toot.task.Todo;

/**
 * Tests command and task parsing behavior.
 */
public class ParserTest {
    @Test
    public void parse_commandWithSurroundingWhitespace_returnsTypeAndTrimmedArguments() throws TootException {
        ParsedCommand command = Parser.parse("  todo   read book  ");

        assertEquals(CommandType.TODO, command.type());
        assertEquals("read book", command.arguments());
    }

    @Test
    public void parse_emptyUnknownAndArgumentBearingList_throwsSpecificErrors() {
        TootException emptyError = assertThrows(TootException.class, () -> Parser.parse("   "));
        TootException unknownError = assertThrows(TootException.class, () -> Parser.parse("dance"));
        TootException listError = assertThrows(TootException.class, () -> Parser.parse("list now"));

        assertEquals("Toot didn't hear a command. Type a command such as 'todo read book'. (・・?)",
                emptyError.getMessage());
        assertEquals("Toot doesn't know that command. "
                + "Try: todo, deadline, event, list, find, mark, unmark, delete, help, or bye. (・・?)",
                unknownError.getMessage());
        assertEquals(unknownError.getMessage(), listError.getMessage());
    }

    @Test
    public void parseFindKeyword_presentAndMissingKeyword_returnsKeywordOrThrows() throws TootException {
        assertEquals("read book", Parser.parseFindKeyword(Parser.parse("find read book")));

        TootException missingKeyword = assertThrows(TootException.class, () ->
                Parser.parseFindKeyword(Parser.parse("find")));

        assertEquals("The find keyword cannot be empty. Try: find KEYWORD", missingKeyword.getMessage());
    }

    @Test
    public void parseTask_validAddCommands_createsTypedTasks() throws TootException {
        Task todo = Parser.parseTask(Parser.parse("todo read book"));
        Task deadline = Parser.parseTask(Parser.parse("deadline return book /by 2026-08-31"));
        Task event = Parser.parseTask(Parser.parse("event project meeting /from Mon 2pm /to Mon 3pm"));

        assertInstanceOf(Todo.class, todo);
        Deadline parsedDeadline = assertInstanceOf(Deadline.class, deadline);
        assertEquals("return book", parsedDeadline.getDescription());
        assertEquals(LocalDate.of(2026, 8, 31), parsedDeadline.getBy());
        Event parsedEvent = assertInstanceOf(Event.class, event);
        assertEquals("project meeting", parsedEvent.getDescription());
        assertEquals("Mon 2pm", parsedEvent.getFrom());
        assertEquals("Mon 3pm", parsedEvent.getTo());
    }

    @Test
    public void parseTask_malformedAddCommands_rejectsMissingFieldsAndInvalidDates() throws TootException {
        TootException emptyTodo = assertThrows(TootException.class, () ->
                Parser.parseTask(Parser.parse("todo")));
        TootException missingBy = assertThrows(TootException.class, () ->
                Parser.parseTask(Parser.parse("deadline return book")));
        TootException invalidDate = assertThrows(TootException.class, () ->
                Parser.parseTask(Parser.parse("deadline return book /by 2026-02-30")));
        TootException missingTo = assertThrows(TootException.class, () ->
                Parser.parseTask(Parser.parse("event meeting /from noon")));

        assertEquals("The todo description cannot be empty. Try: todo DESCRIPTION", emptyTodo.getMessage());
        assertEquals("A deadline needs '/by' before its due date. "
                + "Try: deadline DESCRIPTION /by YYYY-MM-DD", missingBy.getMessage());
        assertEquals("The deadline date must use YYYY-MM-DD and be a real calendar date. "
                + "Try: deadline DESCRIPTION /by 2019-12-02", invalidDate.getMessage());
        assertEquals("An event needs '/to' before its end time. "
                + "Try: event DESCRIPTION /from START /to END", missingTo.getMessage());
    }

    @Test
    public void parseTask_delimiterInsideWord_keepsSearchingForSeparateDelimiter() throws TootException {
        Event event = assertInstanceOf(Event.class,
                Parser.parseTask(Parser.parse("event discuss /today /from Monday /to Tuesday")));

        assertEquals("discuss /today", event.getDescription());
        assertEquals("Monday", event.getFrom());
        assertEquals("Tuesday", event.getTo());
    }

    @Test
    public void parseTask_repeatedDelimiters_rejectsAmbiguousFields() {
        TootException deadlineError = assertThrows(TootException.class, () ->
                Parser.parseTask(Parser.parse("deadline read /by 2026-09-14 /by 2026-09-15")));
        TootException eventFromError = assertThrows(TootException.class, () ->
                Parser.parseTask(Parser.parse("event meet /from Mon /from Tue /to Wed")));
        TootException eventToError = assertThrows(TootException.class, () ->
                Parser.parseTask(Parser.parse("event meet /from Mon /to Tue /to Wed")));

        assertEquals("A deadline can have only one '/by'.", deadlineError.getMessage());
        assertEquals("An event can have only one '/from' and one '/to'.", eventFromError.getMessage());
        assertEquals(eventFromError.getMessage(), eventToError.getMessage());
    }

    @Test
    public void parseTaskIndex_validAndInvalidNumbers_returnsIndexOrSpecificError() throws TootException {
        assertEquals(1, Parser.parseTaskIndex(Parser.parse("mark 2"), 3));

        TootException missing = assertThrows(TootException.class, () ->
                Parser.parseTaskIndex(Parser.parse("delete"), 3));
        TootException nonNumeric = assertThrows(TootException.class, () ->
                Parser.parseTaskIndex(Parser.parse("unmark two"), 3));
        TootException emptyList = assertThrows(TootException.class, () ->
                Parser.parseTaskIndex(Parser.parse("mark 1"), 0));
        TootException outOfRange = assertThrows(TootException.class, () ->
                Parser.parseTaskIndex(Parser.parse("delete 4"), 3));

        assertEquals("Toot needs a task number after 'delete'. Try: delete 1", missing.getMessage());
        assertEquals("The task number for 'unmark' must be a whole number. Try: unmark 1",
                nonNumeric.getMessage());
        assertEquals("The task list is empty, so there is nothing to mark.", emptyList.getMessage());
        assertEquals("Task 4 does not exist. Choose a number from 1 to 3.", outOfRange.getMessage());
    }
}
