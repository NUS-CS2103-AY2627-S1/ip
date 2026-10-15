package bingusdingus.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import bingusdingus.task.Deadline;
import bingusdingus.task.Event;
import bingusdingus.task.Task;
import bingusdingus.task.Todo;

/** Tests command classification and conversion of commands into tasks. */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseCommandType_recognizesSupportedCommandsAndRejectsUnknownCommands() {
        assertEquals(CommandType.BYE, parser.parseCommandType("bye"));
        assertEquals(CommandType.LIST, parser.parseCommandType("list"));
        assertEquals(CommandType.UNDO, parser.parseCommandType("undo"));
        assertEquals(CommandType.UNKNOWN, parser.parseCommandType("undo 1"));
        assertEquals(CommandType.FIND, parser.parseCommandType("find book"));
        assertEquals(CommandType.MARK, parser.parseCommandType("mark 1"));
        assertEquals(CommandType.UNMARK, parser.parseCommandType("unmark 1"));
        assertEquals(CommandType.DELETE, parser.parseCommandType("delete 1"));
        assertEquals(CommandType.TASK, parser.parseCommandType("todo buy milk"));
        assertEquals(CommandType.TASK,
                parser.parseCommandType("deadline return book /by 2026-09-02"));
        assertEquals(CommandType.TASK,
                parser.parseCommandType("event meeting /from 2026-09-02 0900 /to 2026-09-02 1000"));
        assertEquals(CommandType.UNKNOWN, parser.parseCommandType(null));
        assertEquals(CommandType.UNKNOWN, parser.parseCommandType("mark"));
        assertEquals(CommandType.UNKNOWN, parser.parseCommandType("unknown command"));
    }

    @Test
    void parseTask_createsEachTaskTypeWithValidValues() throws InvalidTaskCommandException {
        Task todo = parser.parseTask("todo buy milk");
        assertInstanceOf(Todo.class, todo);
        assertEquals("buy milk", todo.getDescription());

        Task deadline = parser.parseTask("deadline return book /by 2/12/2019 1800");
        assertInstanceOf(Deadline.class, deadline);
        assertEquals("[D][ ] return book (by: Dec 02 2019 6:00 PM)", deadline.toString());

        Task event = parser.parseTask("event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600");
        assertInstanceOf(Event.class, event);
        assertEquals("[E][ ] project meeting (from: Oct 15 2019 2:00 PM to: Oct 15 2019 4:00 PM)",
                event.toString());
    }

    @Test
    void parseTask_rejectsMissingAndMalformedTaskDetails() {
        InvalidTaskCommandException emptyTodo = assertThrows(
                InvalidTaskCommandException.class, () -> parser.parseTask("todo"));
        assertEquals("I've got no idea watchu talkin' about", emptyTodo.getMessage());

        InvalidTaskCommandException missingDeadline = assertThrows(
                InvalidTaskCommandException.class, () -> parser.parseTask("deadline return book"));
        assertEquals("deadline requires a description and a date", missingDeadline.getMessage());

        InvalidTaskCommandException invalidDate = assertThrows(
                InvalidTaskCommandException.class, () -> parser.parseTask("deadline return book /by not-a-date"));
        assertEquals("deadline date/time must use yyyy-mm-dd or d/M/yyyy HHmm", invalidDate.getMessage());

        InvalidTaskCommandException missingEventPart = assertThrows(
                InvalidTaskCommandException.class, () -> parser.parseTask("event meeting /from 2026-09-02 0900"));
        assertEquals("event requires a description, start, and end", missingEventPart.getMessage());

        InvalidTaskCommandException invalidEventRange = assertThrows(InvalidTaskCommandException.class, () ->
                parser.parseTask("event meeting /from 2026-09-02 1000 /to 2026-09-02 0900"));
        assertEquals("event end date/time must be later than the start date/time", invalidEventRange.getMessage());
    }

    @Test
    void parseCommandType_rejectsMalformedWhitespaceAndNearMatches() {
        assertEquals(CommandType.UNKNOWN, parser.parseCommandType(" list"));
        assertEquals(CommandType.UNKNOWN, parser.parseCommandType("list "));
        assertEquals(CommandType.UNKNOWN, parser.parseCommandType("todo  buy milk"));
        assertEquals(CommandType.UNKNOWN, parser.parseCommandType("mark\t1"));
        assertEquals(CommandType.MARK, parser.parseCommandType("mark 1 extra"));
    }

    @Test
    void parseTask_rejectsMalformedWhitespace() {
        InvalidTaskCommandException repeatedSpaces = assertThrows(
                InvalidTaskCommandException.class, () -> parser.parseTask("todo  buy milk"));
        assertEquals("I've got no idea watchu talkin' about", repeatedSpaces.getMessage());

        InvalidTaskCommandException trailingSpace = assertThrows(
                InvalidTaskCommandException.class, () -> parser.parseTask("todo buy milk "));
        assertEquals("I've got no idea watchu talkin' about", trailingSpace.getMessage());
    }

    @Test
    void parseTask_rejectsDuplicatedParametersAndUnsupportedDescriptionCharacters() {
        String duplicatedParameterCommand =
                "deadline return book /by 2026-09-02 /by 2026-09-03";
        Executable duplicatedParameterAction = () -> parser.parseTask(duplicatedParameterCommand);
        InvalidTaskCommandException duplicatedParameter =
                assertThrows(InvalidTaskCommandException.class, duplicatedParameterAction);
        assertEquals("deadline requires exactly one /by parameter", duplicatedParameter.getMessage());

        InvalidTaskCommandException unsupportedCharacter = assertThrows(
                InvalidTaskCommandException.class, () -> parser.parseTask("todo task | backup"));
        assertEquals("Task description contains an unsupported character", unsupportedCharacter.getMessage());
    }
}
