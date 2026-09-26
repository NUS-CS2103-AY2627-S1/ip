package amadeus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Task#descriptionContains(String)}, which backs the
 * "find" command, and for the tag list added by {@link Task#setTags(List)}.
 */
public class TaskTest {

    @Test
    void descriptionContains_keywordInDescription_returnsTrue() {
        assertTrue(new Todo("read book").descriptionContains("book"));
    }

    /** Case is ignored, so the user does not have to match how they typed it originally. */
    @Test
    void descriptionContains_differentCase_returnsTrue() {
        assertTrue(new Todo("Read Book").descriptionContains("book"));
    }

    @Test
    void descriptionContains_keywordAbsent_returnsFalse() {
        assertFalse(new Todo("read book").descriptionContains("magazine"));
    }

    /** A task starts with no tags, since tagging is optional. */
    @Test
    void getTags_newTask_returnsEmptyList() {
        assertTrue(new Todo("read book").getTags().isEmpty());
    }

    @Test
    void toString_noTags_omitsTagBrackets() {
        assertEquals("[T][ ] read book", new Todo("read book").toString());
    }

    /** Tags are shown in the order they were set, each with its leading "#". */
    @Test
    void toString_tagsSet_appendsTagsInBrackets() {
        Todo todo = new Todo("read book");
        todo.setTags(List.of("#fun", "#urgent"));
        assertEquals("[T][ ] read book [#fun, #urgent]", todo.toString());
    }
}
