package esther.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskTest {
    @Test
    public void hasKeyword_matchingKeywordIgnoringCase_returnsTrue() {
        Task task = new Todo("Read a book");

        assertTrue(task.hasKeyword("book"));
        assertTrue(task.hasKeyword("BOOK"));
    }

    @Test
    public void hasKeyword_missingKeyword_returnsFalse() {
        Task task = new Todo("Read a book");

        assertFalse(task.hasKeyword("movie"));
    }
}
