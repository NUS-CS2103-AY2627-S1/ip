package panda.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests matching task descriptions against find keywords.
 */
public class TaskDescriptionMatchTest {

    @Test
    public void hasKeyword_matchingWholeKeyword_returnsTrue() {
        Task task = new Todo("read a library book");
        assertTrue(task.hasKeyword("library"));
        assertTrue(task.hasKeyword("book"));
        assertTrue(task.hasKeyword("LIBRARY"));
    }

    @Test
    public void hasKeyword_partialOrNonMatchingKeyword_returnsFalse() {
        Task task = new Todo("read a library book");
        assertFalse(task.hasKeyword("lib"));
        assertFalse(task.hasKeyword("write"));
    }
}
