package arin.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Verifies internal task-data assumptions with Java assertions enabled.
 */
class TaskAssertionsTest {
    @Test
    void constructor_nullDescription_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Todo(null));
    }

    @Test
    void constructor_blankDescription_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Todo(" \t "));
    }

    @Test
    void constructor_nullDeadlineDate_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Deadline("return book", null));
    }

    @Test
    void constructor_invalidEventStart_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Event("meeting", null, "4pm"));
        assertThrows(AssertionError.class, () -> new Event("meeting", " ", "4pm"));
    }

    @Test
    void constructor_invalidEventEnd_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Event("meeting", "2pm", null));
        assertThrows(AssertionError.class, () -> new Event("meeting", "2pm", " "));
    }

    @Test
    void constructor_unicodeWhitespace_preservesParserValidationRules() {
        // The parser uses trim(), which retains the Unicode em space.
        String emSpace = "\u2003";

        assertEquals(emSpace, new Todo(emSpace).getDescription());
        assertEquals("[E][ ] meeting (from: " + emSpace + " to: " + emSpace + ")",
                new Event("meeting", emSpace, emSpace).toString());
    }
}
