package marcus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests tagging behavior shared by all task types.
 */
class TagTest {
    /** Verifies that tags are displayed and stored in lowercase. */
    @Test
    void addTag_uppercaseTag_displaysAndStoresLowercaseTag() {
        Todo todo = new Todo("watch a movie");

        todo.addTag("#Fun");

        assertEquals("[T][ ] watch a movie #fun", todo.toString());
        assertEquals("T | 0 | watch a movie | #fun", todo.toFileString());
    }

    /** Verifies that adding the same tag twice does not duplicate it. */
    @Test
    void addTag_duplicateTags_keepsOnlyOneTag() {
        Todo todo = new Todo("watch a movie");

        assertTrue(todo.addTag("#fun"));
        assertFalse(todo.addTag("#FUN"));

        assertEquals("#fun", todo.getTagsString());
    }

    /** Verifies that one tag can be removed without removing other tags. */
    @Test
    void removeTag_oneOfSeveralTags_removesOnlySelectedTag() {
        Todo todo = new Todo("watch a movie");
        todo.addTag("#fun");
        todo.addTag("#weekend");

        assertTrue(todo.removeTag("#FUN"));

        assertEquals("#weekend", todo.getTagsString());
    }

    /** Verifies that removing all tags leaves the task untagged. */
    @Test
    void removeAllTags_taggedTask_removesEveryTag() {
        Todo todo = new Todo("watch a movie");
        todo.addTag("#fun");
        todo.addTag("#weekend");

        todo.removeAllTags();

        assertEquals("[T][ ] watch a movie", todo.toString());
        assertEquals("T | 0 | watch a movie", todo.toFileString());
    }

    /** Verifies that deadlines and events support the same tagging behavior as to-dos. */
    @Test
    void addTag_deadlineAndEvent_supportTags() {
        Deadline deadline = new Deadline("submit report", LocalDate.parse("2026-09-15"));
        Event event = new Event("team meeting", "2026-09-15 14:00", "2026-09-15 16:00");

        deadline.addTag("#school");
        event.addTag("#school");

        assertTrue(deadline.toString().contains("submit report #school (by:"));
        assertTrue(event.toString().contains("team meeting #school (from:"));
        assertTrue(deadline.toFileString().endsWith("| #school"));
        assertTrue(event.toFileString().endsWith("| #school"));
    }
}
