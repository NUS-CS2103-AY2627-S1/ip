package ben;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests for {@link Parser}: command splitting, task building, and index validation. */
class ParserTest {
    @Test
    void commandWord_unrecognisedWord_returnsUnknown() {
        assertEquals(CommandWord.UNKNOWN, Parser.commandWord("frobnicate the widget"));
    }

    @Test
    void commandWord_recognisedWordAnyCase_returnsMatchingEnum() {
        assertEquals(CommandWord.TODO, Parser.commandWord("todo read book"));
        assertEquals(CommandWord.DEADLINE, Parser.commandWord("DEADLINE x /by 2019-01-01"));
    }

    @Test
    void commandWord_leadingWhitespace_stillRecognised() {
        assertEquals(CommandWord.LIST, Parser.commandWord("   list"));
    }

    @Test
    void args_noArgumentsAfterKeyword_returnsEmptyString() {
        assertEquals("", Parser.args("list"));
        assertEquals("read book", Parser.args("todo read book"));
    }

    @Test
    void args_leadingWhitespaceBeforeKeyword_stillReturnsArgs() {
        assertEquals("read book", Parser.args("  todo read book"));
    }

    @Test
    void parseDeadline_validInput_buildsDeadline() throws BenException {
        Deadline d = Parser.parseDeadline("return book /by 2019-12-02");
        assertEquals("[D][ ] return book (by: Dec 2 2019)", d.toString());
    }

    @Test
    void parseDeadline_missingByClause_throwsBenException() {
        assertThrows(BenException.class, () -> Parser.parseDeadline("return book"));
    }

    @Test
    void parseDeadline_emptyDescription_throwsBenException() {
        assertThrows(BenException.class, () -> Parser.parseDeadline("/by 2019-12-02"));
    }

    @Test
    void parseEvent_validInput_buildsEvent() throws BenException {
        Event e = Parser.parseEvent("meeting /from Mon 2pm /to 4pm");
        assertEquals("[E][ ] meeting (from: Mon 2pm to: 4pm)", e.toString());
    }

    @Test
    void parseEvent_missingToClause_throwsBenException() {
        assertThrows(BenException.class, () -> Parser.parseEvent("meeting /from Mon 2pm"));
    }

    @Test
    void parseEvent_sameFromAndTo_throwsBenException() {
        assertThrows(BenException.class, () -> Parser.parseEvent("meeting /from Mon 2pm /to Mon 2pm"));
    }

    @Test
    void parseIndex_nonNumericText_throwsBenException() {
        assertThrows(BenException.class, () -> Parser.parseIndex("abc", "delete"));
    }

    @Test
    void parseIndex_numericTextWithSpaces_parsesToInt() throws BenException {
        assertEquals(3, Parser.parseIndex("  3 ", "mark"));
    }
}
