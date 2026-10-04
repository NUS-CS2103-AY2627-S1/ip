package kelore.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import kelore.exception.KeloreInputException;

/** Tests command and task-number parsing performed by {@link Parser}. */
public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    public void parseCommand_allSupportedCommandForms_returnsMatchingCommands() throws Exception {
        assertEquals(Command.BYE, parser.parseCommand("bye"));
        assertEquals(Command.LIST, parser.parseCommand("list"));
        assertEquals(Command.MARK, parser.parseCommand("mark 1"));
        assertEquals(Command.UNMARK, parser.parseCommand("unmark 1"));
        assertEquals(Command.DELETE, parser.parseCommand("delete 1"));
        assertEquals(Command.TODO, parser.parseCommand("todo read book"));
        assertEquals(Command.DEADLINE, parser.parseCommand("deadline submit /by 1/9/2026 1800"));
        assertEquals(Command.EVENT, parser.parseCommand("event meeting /from x /to y"));
        assertEquals(Command.ON, parser.parseCommand("on 1/9/2026"));
        assertEquals(Command.FREE, parser.parseCommand("free 4"));
        assertEquals(Command.FIND, parser.parseCommand("find book"));
    }

    @Test
    public void parseCommand_argumentGivenToArgumentlessCommand_exceptionThrown() {
        assertThrows(KeloreInputException.class, () -> parser.parseCommand("list now"));
    }

    @Test
    public void parseCommand_argumentCommandWithoutArgument_returnsCommand() throws Exception {
        assertEquals(Command.MARK, parser.parseCommand("mark"));
    }

    @Test
    public void parseCommand_unknownOrPartialCommand_exceptionThrown() {
        assertThrows(KeloreInputException.class, () -> parser.parseCommand("dance"));
        assertThrows(KeloreInputException.class, () -> parser.parseCommand("todoist work"));
        assertThrows(KeloreInputException.class, () -> parser.parseCommand(""));
        assertThrows(KeloreInputException.class, () -> parser.parseCommand(null));
    }

    @Test
    public void parseCommand_surroundingOrRepeatedWhitespace_returnsMatchingCommand()
            throws Exception {
        assertEquals(Command.TODO, parser.parseCommand("  todo   read book  "));
        assertEquals(Command.MARK, parser.parseCommand("mark\t1"));
        assertEquals(Command.LIST, parser.parseCommand(" list "));
    }

    @Test
    public void parseTaskNumber_integerWithSurroundingWhitespace_returnsInteger() throws Exception {
        assertEquals(12, parser.parseTaskNumber("mark   12  "));
    }

    @Test
    public void parseTaskNumber_missingOrNonIntegerNumber_exceptionThrown() {
        String oversizedNumber = "999999999999999999999";

        assertThrows(KeloreInputException.class, () -> parser.parseTaskNumber("mark"));
        assertThrows(KeloreInputException.class, () -> parser.parseTaskNumber("mark one"));
        assertThrows(KeloreInputException.class, () -> parser.parseTaskNumber("mark 1.5"));
        assertThrows(KeloreInputException.class, () -> parser.parseTaskNumber("mark 1 extra"));
        assertThrows(
                KeloreInputException.class, () -> parser.parseTaskNumber("mark " + oversizedNumber));
    }

    @Test
    public void parseTaskNumber_negativeInteger_returnsNegativeInteger() throws Exception {
        assertEquals(-2, parser.parseTaskNumber("delete -2"));
    }
}
