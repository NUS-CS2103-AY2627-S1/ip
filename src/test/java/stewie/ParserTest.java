package stewie;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import stewie.parser.Parser;

/**
 * Verifies command parsing and rejection of ambiguous user input.
 */
public class ParserTest {
    @Test
    public void getTaskIndex_fail() {
        // inputs contains more than 2 words, test fails and return -1
        assertEquals(-1, Parser.getTaskIndex("mark task 1"));

        // second part of command is not integer, test fails and returns -1
        assertEquals(-1, Parser.getTaskIndex("delete one"));
    }

    @Test
    public void getTaskIndex_success() {
        // index is one less than task number
        // valid input with mark
        assertEquals(2, Parser.getTaskIndex("mark 3"));

        // valid input with delete and spaces before, between, and after
        assertEquals(4, Parser.getTaskIndex("     delete      5      "));

        // valid input with unmark and two-digit integer
        assertEquals(10, Parser.getTaskIndex("UNMARK 11"));
    }

    @Test
    public void parseUpdate_success() {
        assertEquals(1, Parser.getUpdateTaskIndex("update 2 buy a book"));
        assertEquals("buy a book", Parser.parseUpdateDescription("update 2 buy a book"));

        assertEquals("buy a book", Parser.parseUpdate("update 2 buy a book d/25 Dec 2026")[0]);
        assertEquals("25 dec 2026", Parser.parseUpdate("update 2 buy a book d/25 dec 2026")[1]);
        assertEquals("11 Aug 2026", Parser.parseUpdate("update 2 from/11 Aug 2026")[2]);
        assertEquals("12 Aug 2026", Parser.parseUpdate("update 2 from/11 Aug 2026 to/12 Aug 2026")[3]);
    }

    @Test
    public void parseUpdate_fail() {
        assertEquals(-1, Parser.getUpdateTaskIndex("update two book"));
        assertEquals("", Parser.parseUpdateDescription("update 2"));
    }
    /**
     * Verifies marker boundaries do not split words within descriptions.
     */
    @Test
    public void parseCreation_preservesCommandWords() {
        assertArrayEquals(new String[] {"review deadline", "1/1/2026"},
                Parser.parseDeadline("deadline review deadline /by 1/1/2026"));
        assertArrayEquals(new String[] {"prevent event", "1/1/2026", "2/1/2026"},
                Parser.parseEvent("event prevent event /from 1/1/2026 /to 2/1/2026"));
    }

    /**
     * Verifies duplicate aliases and repeated event fields cannot discard user input.
     */
    @Test
    public void parseFields_rejectsAmbiguousInput() {
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseUpdate("update 1 d/1/1/2026 by/2/1/2026"));
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseEvent("event trip /from 1/1/2026 /to 2/1/2026 /to 3/1/2026"));
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseDeadline("deadline report /by"));
    }

    /**
     * Verifies task numbers cannot wrap around or contain signs or extra arguments.
     */
    @Test
    public void parseIndex_rejectsInvalidNumbers() {
        for (String number : new String[] {"-2147483648", "2147483648", "+1", "0", "1 extra"}) {
            assertEquals(-1, Parser.getTaskIndex("mark " + number));
        }
    }
}
