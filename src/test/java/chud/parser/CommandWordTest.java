package chud.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CommandWordTest {
    @Test
    void fromCommandWord_everyRecognizedWord_returnsMatchingCommandWord() {
        assertEquals(CommandWord.LIST, CommandWord.fromCommandWord("list"));
        assertEquals(CommandWord.MARK, CommandWord.fromCommandWord("mark"));
        assertEquals(CommandWord.UNMARK, CommandWord.fromCommandWord("unmark"));
        assertEquals(CommandWord.DELETE, CommandWord.fromCommandWord("delete"));
        assertEquals(CommandWord.TODO, CommandWord.fromCommandWord("todo"));
        assertEquals(CommandWord.DEADLINE, CommandWord.fromCommandWord("deadline"));
        assertEquals(CommandWord.EVENT, CommandWord.fromCommandWord("event"));
        assertEquals(CommandWord.ON, CommandWord.fromCommandWord("on"));
        assertEquals(CommandWord.BYE, CommandWord.fromCommandWord("bye"));
    }

    @Test
    void fromCommandWord_unrecognizedWord_returnsUnknown() {
        assertEquals(CommandWord.UNKNOWN, CommandWord.fromCommandWord("frobnicate"));
    }

    @Test
    void fromCommandWord_wrongCase_returnsUnknown() {
        // Matching is exact/case-sensitive: "Todo" must not match "todo".
        assertEquals(CommandWord.UNKNOWN, CommandWord.fromCommandWord("Todo"));
    }

    @Test
    void fromCommandWord_emptyString_returnsUnknown() {
        assertEquals(CommandWord.UNKNOWN, CommandWord.fromCommandWord(""));
    }
}
