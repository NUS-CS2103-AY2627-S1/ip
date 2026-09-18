package herta.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import herta.exception.HertaException;

/**
 * Tests the command-boundary normalization and character policy.
 */
class CommandTokenizerTest {

    @Test
    void normalize_outerWhitespaceAndKeywordSeparator_returnsStableCommand() throws Exception {
        assertEquals("todo read   book", CommandTokenizer.normalize(" \t todo\t read   book \t"));
    }

    @Test
    void normalize_nullAndUnsupportedCharacters_reportsSyntaxGuidance() {
        assertThrows(HertaException.class, () -> CommandTokenizer.normalize(null));
        assertThrows(HertaException.class, () -> CommandTokenizer.normalize("todo read\nbook"));
        assertThrows(HertaException.class, () -> CommandTokenizer.normalize("todo\u2003read"));
    }

    @Test
    void normalize_emptyHorizontalWhitespace_returnsEmptyCommand() throws Exception {
        assertEquals("", CommandTokenizer.normalize(" \t  "));
    }

    @Test
    void normalize_lengthBoundary_acceptsMaximumAndRejectsOverlongCommand() throws Exception {
        String maximumCommand = "a".repeat(ParserLimits.MAX_COMMAND_LENGTH);
        String overlongCommand = maximumCommand + "a";

        assertEquals(maximumCommand, CommandTokenizer.normalize(maximumCommand));
        assertThrows(HertaException.class, () -> CommandTokenizer.normalize(overlongCommand));
    }

    @Test
    void normalize_supplementaryUnicodeDescription_preservesCodePointsExactly() throws Exception {
        String command = "todo plan 🚀 launch";

        assertEquals(command, CommandTokenizer.normalize(command));
    }

    @Test
    void normalize_keywordWithHorizontalWhitespace_removesTrailingSeparator() throws Exception {
        assertEquals("list", CommandTokenizer.normalize("list \t  "));
    }

    @Test
    void normalize_unsupportedWhitespaceAndFormatCharacters_rejectsInput() {
        String[] invalidInputs = {
            "todo read\rbook", "todo read\nbook", "todo read\u000Bbook",
            "todo read\u00A0book", "todo read\u2003book", "todo read\u202Ebook"
        };

        for (String input : invalidInputs) {
            assertThrows(HertaException.class, () -> CommandTokenizer.normalize(input));
        }
    }
}
