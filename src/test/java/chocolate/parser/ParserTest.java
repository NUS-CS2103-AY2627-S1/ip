package chocolate.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import chocolate.exception.ChocolateException;
import chocolate.task.Deadline;
import chocolate.task.Event;
import chocolate.task.Todo;

/**
 * Tests command recognition and validation in {@link Parser}.
 */
public class ParserTest {
    @Test
    public void parse_simpleCommands_correctTypes() throws ChocolateException {
        assertEquals(CommandType.BYE, Parser.parse("bye").getType());
        assertEquals(CommandType.LIST, Parser.parse("list").getType());
        assertEquals(CommandType.ARCHIVE, Parser.parse("archive").getType());
        assertEquals(CommandType.ARCHIVE_LIST, Parser.parse("archive list").getType());
        assertEquals(CommandType.LIST, Parser.parse("  list   ").getType());
    }

    @Test
    public void parseAddCommands_validInputs_structuredTasks() throws ChocolateException {
        ParsedCommand todoCommand = Parser.parse("todo read book");
        Todo todo = assertInstanceOf(Todo.class, todoCommand.getTask());
        assertEquals(CommandType.TODO, todoCommand.getType());
        assertEquals("read book", todo.getDescription());

        ParsedCommand deadlineCommand =
                Parser.parse("deadline return book /by 2019-10-15");
        Deadline deadline = assertInstanceOf(Deadline.class, deadlineCommand.getTask());
        assertEquals(CommandType.DEADLINE, deadlineCommand.getType());
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.of(2019, 10, 15), deadline.getBy());

        ParsedCommand eventCommand =
                Parser.parse("event project meeting /from Monday /to Tuesday");
        Event event = assertInstanceOf(Event.class, eventCommand.getTask());
        assertEquals(CommandType.EVENT, eventCommand.getType());
        assertEquals("project meeting", event.getDescription());
        assertEquals("Monday", event.getStart());
        assertEquals("Tuesday", event.getEnd());
    }

    @Test
    public void parseIndexCommands_validNumbers_zeroBasedIndexes() throws ChocolateException {
        assertEquals(1, Parser.parse("mark 2").getTaskIndex());
        assertEquals(2, Parser.parse("unmark 3").getTaskIndex());
        assertEquals(3, Parser.parse("delete 4").getTaskIndex());
        assertEquals(1, Parser.parse(" mark   2 ").getTaskIndex());
    }

    @Test
    public void parseFind_validKeyword_structuredKeyword() throws ChocolateException {
        ParsedCommand command = Parser.parse("find project meeting");

        assertEquals(CommandType.FIND, command.getType());
        assertEquals("project meeting", command.getKeyword());
    }

    @Test
    public void parse_invalidCommands_exceptionThrown() {
        assertThrows(ChocolateException.class, () -> Parser.parse("unknown"));
        assertThrows(ChocolateException.class, () -> Parser.parse("todo"));
        assertThrows(ChocolateException.class, () -> Parser.parse("mark"));
        assertThrows(ChocolateException.class, () -> Parser.parse("delete abc"));
        assertThrows(ChocolateException.class, () -> Parser.parse("find"));
        assertThrows(ChocolateException.class, () -> Parser.parse("archive all"));
        assertThrows(ChocolateException.class, () ->
                Parser.parse("deadline return book /by 2019-02-30"));
        assertThrows(ChocolateException.class, () ->
                Parser.parse("event meeting /from Monday"));
        assertThrows(ChocolateException.class, () -> Parser.parse("mark 0"));
        assertThrows(ChocolateException.class, () -> Parser.parse("delete -1"));
        assertThrows(ChocolateException.class, () ->
                Parser.parse("deadline report /by 2019-10-15 /by 2019-10-16"));
        assertThrows(ChocolateException.class, () ->
                Parser.parse("event meeting /from 2019-10-15 /to 2019-10-15"));
    }

    @Test
    public void parse_boundaryInputs_reportsSpecificErrors() {
        assertEquals("Task number must be at least 1.",
                assertThrows(ChocolateException.class, () -> Parser.parse("mark -5")).getMessage());
        assertEquals("Please provide a whole number for the task number!",
                assertThrows(ChocolateException.class, () -> Parser.parse("delete 999999999999999999999"))
                        .getMessage());
        assertEquals("Please use: deadline DESCRIPTION /by yyyy-MM-dd.",
                assertThrows(ChocolateException.class, () -> Parser.parse("deadline /by 2019-10-15"))
                        .getMessage());
        ChocolateException duplicateFromException = assertThrows(ChocolateException.class, () ->
                Parser.parse("event meeting /from Mon /from Tue /to Wed"));
        assertEquals("Please use: event DESCRIPTION /from START /to END.",
                duplicateFromException.getMessage());
    }

    @Test
    public void parse_eventWithAscendingIsoDates_createsEvent() throws ChocolateException {
        Event event = assertInstanceOf(Event.class,
                Parser.parse("event conference /from 2019-10-15 /to 2019-10-16").getTask());

        assertEquals("2019-10-15", event.getStart());
        assertEquals("2019-10-16", event.getEnd());
    }
}
