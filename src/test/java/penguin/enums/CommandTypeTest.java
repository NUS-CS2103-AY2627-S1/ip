package penguin.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * Tests command-type keyword mappings.
 */
class CommandTypeTest {
    @Test
    void commandType_fromKeyword_validKeyword_returnsMatchingType() {
        assertEquals(CommandType.FIND, CommandType.fromKeyword("find"));
    }

    @Test
    void commandType_fromKeyword_unknownKeyword_returnsNull() {
        assertNull(CommandType.fromKeyword("unknown"));
    }

    @Test
    void commandType_getKeyword_validType_returnsKeyword() {
        assertEquals("deadline", CommandType.DEADLINE.getKeyword());
    }
}
