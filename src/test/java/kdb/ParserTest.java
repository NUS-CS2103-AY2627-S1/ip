package kdb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/** Tests conversion of raw input into commands and arguments. */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseTodoWithArguments_returnsTodoAndFullDescription() {
        Parser.ParsedCommand result = parser.parse("todo finish the report");

        assertEquals(CommandType.TODO, result.getCommand());
        assertEquals("finish the report", result.getArguments());
    }

    @Test
    void parseCommandWithExtraSpaces_trimsArguments() {
        Parser.ParsedCommand result = parser.parse("  deadline   submit report /by 2/12/2019 1800  ");

        assertEquals(CommandType.DEADLINE, result.getCommand());
        assertEquals("submit report /by 2/12/2019 1800", result.getArguments());
    }

    @Test
    void parseUnknownCommand_returnsUnknownWithArguments() {
        Parser.ParsedCommand result = parser.parse("remove everything");

        assertEquals(CommandType.UNKNOWN, result.getCommand());
        assertEquals("everything", result.getArguments());
    }

    @Test
    void parseCommandWithoutArguments_returnsEmptyArguments() {
        Parser.ParsedCommand result = parser.parse("list");

        assertEquals(CommandType.LIST, result.getCommand());
        assertEquals("", result.getArguments());
    }

    @Test
    void parseBlankInput_returnsUnknownCommandWithoutArguments() {
        Parser.ParsedCommand result = parser.parse("   ");

        assertNotNull(result);
        assertEquals(CommandType.UNKNOWN, result.getCommand());
        assertEquals("", result.getArguments());
    }

    @Test
    void parseNullInput_returnsUnknownCommandWithoutArguments() {
        Parser.ParsedCommand result = parser.parse(null);

        assertNotNull(result);
        assertEquals(CommandType.UNKNOWN, result.getCommand());
        assertEquals("", result.getArguments());
    }
}
