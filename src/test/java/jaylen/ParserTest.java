package jaylen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests conversion of user input into structured commands. */
public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    public void parse_eventCommand_returnsAllParts() {
        ParsedCommand command = parser.parse(
                "event project meeting /from 2019-12-09 1400 /to 2019-12-09 1600");

        assertEquals(ParsedCommand.Type.EVENT, command.getType());
        assertEquals("project meeting", command.getDescription());
        assertEquals("2019-12-09 1400", command.getFirstDetail());
        assertEquals("2019-12-09 1600", command.getSecondDetail());
    }

    @Test
    public void parse_deleteCommand_returnsOneBasedTaskNumber() {
        ParsedCommand command = parser.parse("delete 3");

        assertEquals(ParsedCommand.Type.DELETE, command.getType());
        assertEquals(3, command.getTaskNumber());
    }

    @Test
    public void parse_findCommand_returnsKeyword() {
        ParsedCommand command = parser.parse("find Book return");

        assertEquals(ParsedCommand.Type.FIND, command.getType());
        assertEquals("Book return", command.getDescription());
    }

    @Test
    public void parse_todoWithoutDescription_throwsException() {
        JaylenException exception = assertThrows(JaylenException.class, () -> parser.parse("todo"));

        assertEquals("The description of a todo cannot be empty.", exception.getMessage());
    }

    @Test
    public void parse_commandWithExtraWhitespace_normalizesInput() {
        ParsedCommand command = parser.parse("  todo    prepare   slides  ");

        assertEquals(ParsedCommand.Type.TODO, command.getType());
        assertEquals("prepare slides", command.getDescription());
    }

    @Test
    public void parse_blankCommand_throwsSpecificException() {
        JaylenException exception = assertThrows(JaylenException.class, () -> parser.parse("   "));

        assertEquals("Please enter a command.", exception.getMessage());
    }

    @Test
    public void parse_deleteWithoutTaskNumber_throwsSpecificException() {
        JaylenException exception = assertThrows(JaylenException.class, () -> parser.parse("delete"));

        assertEquals("A delete command must include a task number.", exception.getMessage());
    }

    @Test
    public void parse_deadlineWithoutDescription_throwsSpecificException() {
        JaylenException exception = assertThrows(JaylenException.class, () ->
                parser.parse("deadline /by 2026-09-25"));

        assertEquals("The description of a deadline cannot be empty.", exception.getMessage());
    }

    @Test
    public void parse_eventWithRepeatedMarker_throwsSpecificException() {
        JaylenException exception = assertThrows(JaylenException.class, () ->
                parser.parse("event class /from 2026-09-25 1000 "
                        + "/from 2026-09-25 1100 /to 2026-09-25 1200"));

        assertEquals("An event can contain only one /from and one /to marker.",
                exception.getMessage());
    }

    @Test
    public void parse_descriptionContainingStorageDelimiter_throwsSpecificException() {
        JaylenException exception = assertThrows(JaylenException.class, () ->
                parser.parse("todo review A | B"));

        assertEquals("Task descriptions cannot contain the | character.", exception.getMessage());
    }
}
