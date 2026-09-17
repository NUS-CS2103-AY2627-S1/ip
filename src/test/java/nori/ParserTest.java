package nori;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ParserTest {
    @Test
    void parse_todoCommand_returnsCommandAndArguments() throws NoriException {
        ParsedCommand parsedCommand = Parser.parse("todo read book");

        assertEquals(Command.TODO, parsedCommand.getCommand());
        assertEquals("read book", parsedCommand.getArguments());
    }

    @Test
    void parse_unknownCommand_throwsNoriException() {
        assertThrows(NoriException.class, () -> Parser.parse("unknown command"));
    }

    @Test
    void parse_commandWithMixedCaseAndWhitespace_normalizesCommand() throws NoriException {
        ParsedCommand parsedCommand = Parser.parse("  ToDo   read book  ");

        assertEquals(Command.TODO, parsedCommand.getCommand());
        assertEquals("read book", parsedCommand.getArguments());
    }

    @Test
    void parse_blankCommand_throwsHelpfulException() {
        NoriException exception = assertThrows(NoriException.class, () -> Parser.parse(" \t "));

        assertEquals("I didn't catch a command. Type help to see what I understand.", exception.getMessage());
    }

    @Test
    void parse_nullCommand_throwsHelpfulException() {
        assertThrows(NoriException.class, () -> Parser.parse(null));
    }
}
