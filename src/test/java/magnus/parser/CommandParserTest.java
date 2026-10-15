package magnus.parser;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests parsing of command keywords and argument fields.
 */
public class CommandParserTest {
    private final CommandParser parser = new CommandParser();

    @Test
    public void parse_blankInput_returnsEmptyArray() {
        assertArrayEquals(new String[0], this.parser.parse("   "));
    }

    @Test
    public void parse_commandWithoutArguments_returnsCommandOnly() {
        assertArrayEquals(new String[] { "list" }, this.parser.parse("list"));
    }

    @Test
    public void parse_descriptionWithSpaces_preservesDescription() {
        assertArrayEquals(
                new String[] { "todo", "read a book" },
                this.parser.parse("todo read a book"));
    }

    @Test
    public void parse_eventCommand_preservesArgumentsForCommandSpecificParsing() {
        assertArrayEquals(
                new String[] {
                    "event", "meeting /from 02/09/2026 1500 /to 02/09/2026 1600"
                },
                this.parser.parse("event meeting /from 02/09/2026 1500 /to 02/09/2026 1600"));
    }

    @Test
    public void parse_missingDelimitedField_preservesRawArguments() {
        assertArrayEquals(
                new String[] { "deadline", "submit report /by" },
                this.parser.parse("deadline submit report /by"));
    }

    @Test
    public void parse_nullInput_returnsEmptyArray() {
        assertArrayEquals(new String[0], this.parser.parse(null));
    }
}
