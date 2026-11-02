package megia.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TaskTest {
    private static final Task TASK = new Task("Review PR #42 [urgent]");

    @Test
    void hasDescriptionContaining_matchingWord_returnsTrue() {
        assertTrue(TASK.hasDescriptionContaining("Review"));
    }

    @Test
    void hasDescriptionContaining_matchingPhraseWithSpaces_returnsTrue() {
        assertTrue(TASK.hasDescriptionContaining("PR #42"));
    }

    @Test
    void hasDescriptionContaining_matchingSpecialCharacters_returnsTrue() {
        assertTrue(TASK.hasDescriptionContaining("[urgent]"));
    }

    @Test
    void hasDescriptionContaining_caseMismatch_returnsFalse() {
        assertFalse(TASK.hasDescriptionContaining("review"));
    }

    @Test
    void hasDescriptionContaining_absentText_returnsFalse() {
        assertFalse(TASK.hasDescriptionContaining("meeting"));
    }
}
