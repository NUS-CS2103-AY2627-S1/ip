package oz.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import oz.exception.OzException;

/**
 * Tests command-word and argument parsing.
 */
public class CommandParserTest {

    @Test
    public void parse_commandWithoutArguments_returnsEmptyArguments() throws OzException {
        ParsedCommand command = CommandParser.parse("  list  ");

        assertEquals("list", command.commandWord());
        assertEquals("", command.arguments());
    }

    @Test
    public void parse_commandWithArguments_returnsTrimmedParts() throws OzException {
        ParsedCommand command = CommandParser.parse("  todo   read book  ");

        assertEquals("todo", command.commandWord());
        assertEquals("read book", command.arguments());
    }

    @Test
    public void parse_multilineArguments_exceptionThrown() {
        assertThrows(OzException.class, () -> CommandParser.parse("todo first\nsecond"));
    }
}
