package bob.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests conversion from command keywords to command types. */
class CommandTypeTest {

    @Test
    void fromKeyword_knownKeywords_returnsMatchingCommandTypes() {
        for (CommandType commandType : CommandType.values()) {
            assertEquals(commandType, CommandType.fromKeyword(commandType.getKeyword()).orElseThrow());
        }
    }

    @Test
    void fromKeyword_mixedCaseKeyword_returnsMatchingCommandType() {
        assertEquals(CommandType.DEADLINE, CommandType.fromKeyword("DeAdLiNe").orElseThrow());
    }

    @Test
    void fromKeyword_unknownKeyword_returnsEmptyOptional() {
        assertTrue(CommandType.fromKeyword("unknown").isEmpty());
    }
}
