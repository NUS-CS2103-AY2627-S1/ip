package crow.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CommandTypeTest {
    @Test
    void parseCommandWord_knownCommand_isCaseInsensitive() {
        assertEquals(CommandType.DEADLINE, CommandType.parseCommandWord("deadline"));
        assertEquals(CommandType.DEADLINE, CommandType.parseCommandWord("dEADline"));
        assertEquals(CommandType.FIND, CommandType.parseCommandWord("find"));
        assertEquals(CommandType.BYE, CommandType.parseCommandWord("BYE"));
    }

    @Test
    void parseCommandWord_unknownCommand_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.parseCommandWord("blah"));
        assertEquals(CommandType.UNKNOWN, CommandType.parseCommandWord(""));
    }
}
