package whimsybot.parser;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

/** Tests splitting command words from their arguments. */
class ParserTest {
    @Test
    void parse_commandWithArguments_returnsWordAndTrimmedArguments() {
        assertArrayEquals(new String[] {"todo", "buy milk"}, Parser.parse("todo   buy milk"));
    }

    @Test
    void parse_commandWithoutArguments_returnsEmptyArguments() {
        assertArrayEquals(new String[] {"list", ""}, Parser.parse("list"));
    }

    @Test
    void parse_commandWithIrregularWhitespace_normalizesCommandWord() {
        assertArrayEquals(new String[] {"todo", "buy milk"}, Parser.parse("  TODO    buy milk  "));
    }

    @Test
    void parse_blankCommand_returnsEmptyParts() {
        assertArrayEquals(new String[] {"", ""}, Parser.parse("   "));
    }
}
