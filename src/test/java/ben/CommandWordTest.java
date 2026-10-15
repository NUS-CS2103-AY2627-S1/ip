package ben;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests for {@link CommandWord#fromString(String)}. */
class CommandWordTest {
    @Test
    void fromString_lowerCaseKeyword_matchesEnumConstant() {
        assertEquals(CommandWord.TODO, CommandWord.fromString("todo"));
    }

    @Test
    void fromString_upperCaseKeyword_matchesEnumConstant() {
        assertEquals(CommandWord.DEADLINE, CommandWord.fromString("DEADLINE"));
    }

    @Test
    void fromString_unrecognisedWord_returnsUnknown() {
        assertEquals(CommandWord.UNKNOWN, CommandWord.fromString("frobnicate"));
    }

    @Test
    void fromString_emptyString_returnsUnknown() {
        assertEquals(CommandWord.UNKNOWN, CommandWord.fromString(""));
    }
}
