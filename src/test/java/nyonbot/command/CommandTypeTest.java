package nyonbot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CommandTypeTest {
    @Test
    void keyword_knownType_returnsCommandKeyword() {
        assertEquals("deadline", CommandType.DEADLINE.keyword());
    }

    @Test
    void toCommandType_knownKeyword_ignoresCase() {
        assertEquals(CommandType.TODO, CommandType.toCommandType("ToDo"));
    }

    @Test
    void toCommandType_unknownKeyword_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.toCommandType("invalid"));
    }
}
