package sophon.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import sophon.exception.SophonException;
import sophon.model.Deadline;
import sophon.model.Event;
import sophon.model.Todo;

public class ParserTest {
    @Test
    public void parseTaskIndex_validTaskNumbers_returnsZeroBasedIndex() throws SophonException {
        assertEquals(0, Parser.parseTaskIndex("mark 1", "mark", "Missing task number."));
        assertEquals(9, Parser.parseTaskIndex("mark 10", "mark", "Missing task number."));
        assertEquals(2, Parser.parseTaskIndex("mark     3     ", "mark", "Missing task number."));
    }

    @Test
    public void parseTaskIndex_missingTaskNumber_throwsExceptionWithGivenMessage() {
        SophonException exception = assertThrows(
                SophonException.class, () -> Parser.parseTaskIndex("mark", "mark", "Missing task number."));

        assertEquals("Missing task number.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_nonNumericTaskNumber_throwsException() {
        SophonException exception = assertThrows(
                SophonException.class, () -> Parser.parseTaskIndex("mark one", "mark", "Missing task number."));

        assertEquals("Task numbers must be written as numerals.", exception.getMessage());
    }

    @Test
    public void parse_findCommand_returnsFindCommandWithKeyword() throws SophonException {
        Command command = Parser.parse("find book");

        assertEquals(Command.Type.FIND, command.getType());
        assertEquals("book", command.getKeyword());
    }

    @Test
    public void parse_findCommandWithoutKeyword_throwsException() {
        SophonException exception = assertThrows(SophonException.class, () -> Parser.parse("find"));

        assertEquals("Tell me what signal to search for.", exception.getMessage());
    }

    @Test
    public void parse_commandsWithoutData_returnsExpectedTypes() throws SophonException {
        assertEquals(Command.Type.BYE, Parser.parse("bye").getType());
        assertEquals(Command.Type.LIST, Parser.parse("list").getType());
        assertEquals(Command.Type.UNKNOWN, Parser.parse("hello").getType());
    }

    @Test
    public void parse_validTaskCommands_returnsCommandsWithTasks() throws SophonException {
        Command todo = Parser.parse("todo read book");
        Command deadline = Parser.parse("deadline return book /by 2019-10-15");
        Command event = Parser.parse("event meeting /from 2019-10-15 /to 2019-10-16");

        assertEquals(Command.Type.ADD_TODO, todo.getType());
        assertTrue(todo.getTask() instanceof Todo);
        assertEquals(Command.Type.ADD_DEADLINE, deadline.getType());
        assertTrue(deadline.getTask() instanceof Deadline);
        assertEquals(Command.Type.ADD_EVENT, event.getType());
        assertTrue(event.getTask() instanceof Event);
    }

    @Test
    public void parse_validIndexCommands_returnsZeroBasedIndexes() throws SophonException {
        assertEquals(0, Parser.parse("mark 1").getTaskIndex());
        assertEquals(1, Parser.parse("unmark 2").getTaskIndex());
        assertEquals(2, Parser.parse("delete 3").getTaskIndex());
    }

    @Test
    public void parse_invalidDates_throwsHelpfulException() {
        SophonException deadlineException = assertThrows(
                SophonException.class, () -> Parser.parse("deadline return book /by tomorrow"));
        SophonException eventException = assertThrows(
                SophonException.class, () -> Parser.parse("event meeting /from Monday /to Tuesday"));

        assertEquals("Please enter the date in yyyy-MM-dd format.", deadlineException.getMessage());
        assertEquals("Please enter the date in yyyy-MM-dd format.", eventException.getMessage());
    }

    @Test
    public void parse_taskDetailsContainingFileSeparator_throwsException() {
        SophonException todoException = assertThrows(
                SophonException.class, () -> Parser.parse("todo read | book"));
        SophonException deadlineException = assertThrows(
                SophonException.class, () -> Parser.parse("deadline return book /by 2019 | 10 | 15"));
        SophonException eventException = assertThrows(
                SophonException.class, () -> Parser.parse("event meeting /from 2019-10-15 /to 2019 | 10 | 16"));

        assertEquals("Please do not use \" | \" in task details.", todoException.getMessage());
        assertEquals("Please do not use \" | \" in task details.", deadlineException.getMessage());
        assertEquals("Please do not use \" | \" in task details.", eventException.getMessage());
    }

    @Test
    public void parse_incompleteTaskCommands_throwsSpecificExceptions() {
        assertParseFailure("todo", "You have given me nothing to observe.\nA todo requires a description.");
        assertParseFailure("deadline", "You have told me neither what must be done nor when.\n"
                + "A deadline requires both.");
        assertParseFailure("deadline return book", "I know what must be done, but not when.\n"
                + "Specify when it is due using /by.");
        assertParseFailure("deadline return book /by", "I see the task, but its deadline remains unknown.\n"
                + "Tell me when it is due.");
        assertParseFailure("deadline /by 2019-10-15", "I know when, but not what.\n"
                + "Give the deadline a description.");
        assertParseFailure("event", "You have told me neither what will happen nor when.\n"
                + "An event requires both.");
        assertParseFailure("event meeting", "I know what will happen, but not when.\n"
                + "Tell me when it begins and when it ends.");
        assertParseFailure("event meeting /to 2019-10-16", "I see when it ends, but not when it begins.\n"
                + "Tell me when it begins.");
        assertParseFailure("event meeting /from 2019-10-15", "I see when it begins, but not when it ends.\n"
                + "Specify an end time using /to.");
        assertParseFailure("event meeting /from 2019-10-15 /to", "I see when it begins, but its end remains unknown.\n"
                + "Tell me when it ends.");
        assertParseFailure("event /from 2019-10-15 /to 2019-10-16", "I know when, but not what.\n"
                + "Give the event a description.");
    }

    private void assertParseFailure(String input, String expectedMessage) {
        SophonException exception = assertThrows(SophonException.class, () -> Parser.parse(input));
        assertEquals(expectedMessage, exception.getMessage());
    }
}
