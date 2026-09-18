package computah.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests task behavior in {@link Task}.
 */
public class TaskTest {
    @Test
    public void hasDescriptionContaining_keywordPresent_returnsTrue() {
        Task task = new Task("read book");

        assertTrue(task.hasDescriptionContaining("book"));
    }

    @Test
    public void hasDescriptionContaining_keywordAbsent_returnsFalse() {
        Task task = new Task("read book");

        assertFalse(task.hasDescriptionContaining("movie"));
    }

    @Test
    public void hasDescriptionContaining_keywordWithDifferentCase_returnsFalse() {
        Task task = new Task("read book");

        assertFalse(task.hasDescriptionContaining("Book"));
    }
}
