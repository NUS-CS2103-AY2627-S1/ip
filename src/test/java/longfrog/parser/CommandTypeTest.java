package longfrog.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CommandTypeTest {
    @Test
    void fromKeyword_everyKnownKeyword_returnsItsCommandTypeRegardlessOfCase() {
        for (CommandType commandType : CommandType.values()) {
            String keyword = commandType.name().toLowerCase();
            assertEquals(commandType, CommandType.fromKeyword(keyword).orElseThrow());
            assertEquals(commandType, CommandType.fromKeyword(keyword.toUpperCase()).orElseThrow());
        }
    }

    @Test
    void fromKeyword_unknownKeyword_returnsEmptyResult() {
        assertTrue(CommandType.fromKeyword("dance").isEmpty());
    }
}
