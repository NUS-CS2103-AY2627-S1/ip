package chai.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests keyword matching inherited by todo tasks. */
public class TodoTest {

    @Test
    public void constructor_blankDescription_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new Todo("  "));
    }

    @Test
    public void containsKeyword_exactPartialAndDifferentCase_matches() {
        Todo todo = new Todo("Read Book From Library");

        assertTrue(todo.containsKeyword("Read Book From Library"));
        assertTrue(todo.containsKeyword("book"));
        assertTrue(todo.containsKeyword("LIBRARY"));
    }

    @Test
    public void containsKeyword_keywordAbsent_doesNotMatch() {
        Todo todo = new Todo("Read Book From Library");

        assertFalse(todo.containsKeyword("report"));
    }
}
