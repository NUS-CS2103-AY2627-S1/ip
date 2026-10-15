package cheecken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Checks input normalization and command boundaries for every supported command.
 */
class ParserTest {
    private final Parser parser = new Parser();

    /**
     * Verifies normalize with blank input: returns empty string.
     * @param input input supplied by the test case
     */
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n", "\u2003"})
    void normalize_blankInput_returnsEmptyString(String input) {
        assertEquals("", parser.normalize(input));
        assertNull(parser.parseCommand(parser.normalize(input)));
    }

    /**
     * Verifies normalize with surrounding whitespace: preserves internal spacing.
     * @param input input supplied by the test case
     */
    @ParameterizedTest
    @ValueSource(strings = {"todo two  words", "\t todo two  words \n", "\u2003todo two  words\u2003"})
    void normalize_surroundingWhitespace_preservesInternalSpacing(String input) {
        assertEquals("todo two  words", parser.normalize(input));
    }

    /**
     * Verifies parse command with supported keyword: accepts case and whitespace.
     * @param command command name used in error guidance
     */
    @ParameterizedTest
    @EnumSource(CommandType.class)
    void parseCommand_supportedKeyword_acceptsCaseAndWhitespace(CommandType command) {
        String keyword = command.name().toLowerCase(Locale.ROOT);
        assertEquals(command, parser.parseCommand(keyword));
        assertEquals(command, parser.parseCommand(command.name()));
        assertEquals(command, parser.parseCommand(keyword + " argument"));
        assertEquals(command, parser.parseCommand(command.name() + "\targument"));
        assertNull(parser.parseCommand(keyword + "suffix"));
        assertNull(parser.parseCommand("prefix" + keyword));
    }

    /**
     * Verifies matches with keyword boundary: accepts exact keyword or space.
     * @param command command name used in error guidance
     */
    @ParameterizedTest
    @EnumSource(CommandType.class)
    void matches_keywordBoundary_acceptsExactKeywordOrSpace(CommandType command) {
        String keyword = command.name().toLowerCase(Locale.ROOT);
        assertTrue(command.matches(keyword));
        assertTrue(command.matches(keyword + " argument"));
        assertFalse(command.matches(keyword + "suffix"));
        assertFalse(command.matches(""));
    }
}
