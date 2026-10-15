package wwaffle.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import wwaffle.exception.WWaffleException;

class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseCommandType_knownCommand_returnsMatchingType() {
        assertEquals(CommandType.TODO, parser.parseCommandType("todo read book"));
        assertEquals(CommandType.DEADLINE,
                parser.parseCommandType("deadline submit work /by 2026-08-31"));
        assertEquals(CommandType.FIND, parser.parseCommandType("find book"));
        assertEquals(CommandType.CLEAR, parser.parseCommandType("clear"));
        assertEquals(CommandType.BYE, parser.parseCommandType("bye"));
    }

    @Test
    void parseCommandType_unknownCommand_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, parser.parseCommandType("dance"));
    }

    @Test
    void parseTaskIndex_validOneBasedNumber_returnsZeroBasedIndex() throws WWaffleException {
        assertEquals(1, parser.parseTaskIndex("mark 2", "mark", 3));
    }

    @Test
    void parseTaskIndex_invalidArguments_throwWWaffleException() {
        assertThrows(WWaffleException.class, () -> parser.parseTaskIndex("mark", "mark", 3));
        assertThrows(WWaffleException.class, () -> parser.parseTaskIndex("mark two", "mark", 3));
        assertThrows(WWaffleException.class, () -> parser.parseTaskIndex("mark 4", "mark", 3));
    }
}
