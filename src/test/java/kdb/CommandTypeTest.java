package kdb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests conversion of command words into command types. */
class CommandTypeTest {
    @Test
    void fromWord_acceptsCommandWordsRegardlessOfCase() {
        assertEquals(CommandType.TODO, CommandType.fromWord("todo"));
        assertEquals(CommandType.DEADLINE, CommandType.fromWord("DeAdLiNe"));
        assertEquals(CommandType.EVENT, CommandType.fromWord("EVENT"));
    }

    @Test
    void fromWord_unknownWord_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.fromWord("assist"));
    }

    @Test
    void fromWord_nullOrBlankWord_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.fromWord(null));
        assertEquals(CommandType.UNKNOWN, CommandType.fromWord("   "));
    }
}
