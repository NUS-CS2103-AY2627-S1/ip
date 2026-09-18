package nimbus.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parse_knownCommand_returnsTypeAndArgument() {
        ParsedCommand command = parser.parse("  deadline submit report /by 2026-08-21  ");

        assertEquals(CommandType.DEADLINE, command.type());
        assertEquals("submit report /by 2026-08-21", command.argument());
    }

    @Test
    void parse_unknownCommand_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, parser.parse("dance now").type());
    }

    @Test
    void parse_findCommand_returnsFindTypeAndKeyword() {
        ParsedCommand command = parser.parse("find book");

        assertEquals(CommandType.FIND, command.type());
        assertEquals("book", command.argument());
    }

    @Test
    void parse_updateCommand_returnsUpdateTypeAndArgument() {
        ParsedCommand command = parser.parse("update 2 revised description");

        assertEquals(CommandType.UPDATE, command.type());
        assertEquals("2 revised description", command.argument());
    }

    @Test
    void parse_emptyInput_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, parser.parse("   ").type());
    }

    @Test
    void parse_tabSeparatedCommand_returnsTypeAndArgument() {
        ParsedCommand command = parser.parse("todo\tread book");

        assertEquals(CommandType.TODO, command.type());
        assertEquals("read book", command.argument());
    }
}
