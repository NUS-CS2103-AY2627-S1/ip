package nova;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ParserTest {
    @Test
    public void getCommandWord_mixedCase_returnsLowerCase() {
        assertEquals("list", new Parser("  LiSt  ").getCommandWord());
    }

    @Test
    public void getCommandWord_blankInput_returnsEmpty() {
        assertEquals("", new Parser("   ").getCommandWord());
    }

    @Test
    public void getArguments_repeatedSpaces_squeezedIntoSingleSpaces() {
        assertEquals("read book", new Parser("todo   read    book").getArguments());
    }

    @Test
    public void hasArgument_noArgument_returnsFalse() {
        assertFalse(new Parser("list").hasArgument());
        assertTrue(new Parser("todo read book").hasArgument());
    }

    @Test
    public void hasSingleWordArgument_twoWords_returnsFalse() {
        assertTrue(new Parser("mark 2").hasSingleWordArgument());
        assertFalse(new Parser("mark 2 3").hasSingleWordArgument());
        assertFalse(new Parser("mark").hasSingleWordArgument());
    }

    @Test
    public void splitAround_parameterPresent_splitsAndTrims() {
        assertArrayEquals(new String[] {"return book", "2026-08-28"},
                Parser.splitAround("return book /by 2026-08-28", "/by"));
    }

    @Test
    public void splitAround_missingParameter_returnsEmptyArray() {
        assertEquals(0, Parser.splitAround("return book", "/by").length);
    }

    @Test
    public void countOf_repeatedParameter_countsEveryOccurrence() {
        assertEquals(0, Parser.countOf("read book", "/by"));
        assertEquals(1, Parser.countOf("read book /by 2026-08-28", "/by"));
        assertEquals(2, Parser.countOf("read book /by 2026-08-28 /by 2026-09-01", "/by"));
    }
}
