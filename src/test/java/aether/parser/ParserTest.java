package aether.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import aether.exception.AetherException;
import aether.task.Deadline;
import aether.task.Event;
import aether.task.Todo;

class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseRecognisesCommandsAndTrimsArguments() throws AetherException {
        Command command = parser.parse("  deadline   return book /by 2019-10-15  ");

        assertEquals(CommandType.DEADLINE, command.getType());
        assertEquals("return book /by 2019-10-15", command.getArguments());
    }

    @Test
    void parseRejectsEmptyUnknownAndUnexpectedArguments() {
        assertError(null, "Please type a command");
        assertError("   ", "Please type a command");
        assertError("find", "search keyword cannot be empty");
        assertError("remind buy milk", "I don't recognise that command");
        assertError("list today", "I don't recognise that command");
        assertError("bye now", "I don't recognise that command");
    }

    @Test
    void parseFindKeepsTheSearchKeyword() throws AetherException {
        Command command = parser.parse("  find   BOOK  ");

        assertEquals(CommandType.FIND, command.getType());
        assertEquals("BOOK", command.getArguments());
    }

    @Test
    void parseRecognisesSortAndRejectsSortArguments() throws AetherException {
        assertEquals(CommandType.SORT, parser.parse("  sort  ").getType());
        assertError("sort tomorrow", "I don't recognise that command");
    }

    @Test
    void createTaskBuildsEachSupportedTaskType() throws AetherException {
        Todo todo = assertInstanceOf(Todo.class, parser.createTask(parser.parse("todo borrow book")));
        Deadline deadline = assertInstanceOf(
                Deadline.class, parser.createTask(parser.parse("deadline return book /by 2019-10-15")));
        Event event = assertInstanceOf(
                Event.class, parser.createTask(parser.parse("event project meeting /from 2019-10-15 /to 2019-10-16")));

        assertEquals("borrow book", todo.getDescription());
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.of(2019, 10, 15), deadline.getBy());
        assertEquals("project meeting", event.getDescription());
        assertEquals(LocalDate.of(2019, 10, 15), event.getFrom());
        assertEquals(LocalDate.of(2019, 10, 16), event.getTo());
    }

    @Test
    void createTaskRejectsCommandsThatDoNotCreateTasks() throws AetherException {
        Command listCommand = parser.parse("list");

        assertThrows(IllegalArgumentException.class, () -> parser.createTask(listCommand));
    }

    @Test
    void createTaskRejectsMissingOrEmptyRequiredFields() throws AetherException {
        assertCreateTaskError("todo", "description of a todo cannot be empty");
        assertCreateTaskError("deadline return book", "deadline needs a /by date");
        assertCreateTaskError("deadline /by 2019-10-15", "description of a deadline cannot be empty");
        assertCreateTaskError("deadline return book /by", "/by date of a deadline cannot be empty");
        assertCreateTaskError("event project meeting /from 2019-10-15", "event needs /from and /to dates");
        assertCreateTaskError("event /from 2019-10-15 /to 2019-10-16", "description of an event cannot be empty");
        assertCreateTaskError("event project meeting /from /to 2019-10-16", "/from date of an event cannot be empty");
        assertCreateTaskError("event project meeting /from 2019-10-15 /to", "/to date of an event cannot be empty");
    }

    @Test
    void createTaskRejectsOutOfOrderMarkersAndInvalidDates() throws AetherException {
        assertCreateTaskError("event project meeting /to 2019-10-16 /from 2019-10-15", "Put /from before /to");
        assertCreateTaskError("deadline return book /by 2019-02-30", "/by date must be a valid date");
        assertCreateTaskError("event project meeting /from invalid /to 2019-10-16", "/from date must be a valid date");
        assertCreateTaskError("event project meeting /from 2019-10-15 /to invalid", "/to date must be a valid date");
    }

    @Test
    void createTaskRejectsDuplicateEmbeddedAndInvalidDateMarkers() throws AetherException {
        assertCreateTaskError("deadline read /bypass notes", "deadline needs a /by date");
        assertCreateTaskError("deadline submit /by 2026-09-15 /by 2026-09-16", "Use /by only once");
        assertCreateTaskError("event camp /from 2026-09-15 /from 2026-09-16 /to 2026-09-17",
                "Use /from only once");
        assertCreateTaskError("event camp /from 2026-09-15 /to 2026-09-16 /to 2026-09-17",
                "Use /to only once");
        assertCreateTaskError("event camp /from 2026-09-16 /to 2026-09-16", "/to date must be after");
        assertCreateTaskError("event camp /from 2026-09-17 /to 2026-09-16", "/to date must be after");
    }

    @Test
    void parseTaskIndexConvertsOneBasedNumbersAndRejectsInvalidValues() throws AetherException {
        Command mark = parser.parse("mark 2");

        assertEquals(1, parser.parseTaskIndex(mark, 2));
        assertTaskIndexError("mark", 2, "Please give a task number after mark");
        assertTaskIndexError("mark no", 2, "task number must be a whole number");
        assertTaskIndexError("mark 0", 2, "task number does not exist");
        assertTaskIndexError("mark 3", 2, "task number does not exist");
    }

    private void assertCreateTaskError(String input, String expectedMessage) throws AetherException {
        Command command = parser.parse(input);
        AetherException exception = assertThrows(AetherException.class, () -> parser.createTask(command));
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    private void assertTaskIndexError(String input, int taskCount, String expectedMessage) throws AetherException {
        Command command = parser.parse(input);
        AetherException exception = assertThrows(
                AetherException.class, () -> parser.parseTaskIndex(command, taskCount));
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    private void assertError(String input, String expectedMessage) {
        AetherException exception = assertThrows(AetherException.class, () -> parser.parse(input));
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
}
