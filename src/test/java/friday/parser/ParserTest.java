package friday.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import friday.command.Command;

/** Tests conversion of user input into commands. */
class ParserTest {

    @Test
    void parse_byeInput_returnsExitCommand() {
        Command command = Parser.parse("bye");

        assertTrue(command.isExit());
    }

    @Test
    void parse_byeWithArguments_returnsRegularCommand() {
        Command command = Parser.parse("bye now");

        assertFalse(command.isExit());
    }

    @Test
    void parse_nullInput_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> Parser.parse(null));
    }

    @Test
    void parse_blankInput_returnsRegularCommand() {
        Command command = Parser.parse("   ");

        assertFalse(command.isExit());
    }
}
