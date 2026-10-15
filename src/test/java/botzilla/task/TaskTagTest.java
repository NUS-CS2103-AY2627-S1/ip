package botzilla.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Tests the tag/untag behavior defined on the base Task class
 * (addTag, removeTag, getTags), exercised through ToDoTask since Task
 * itself is abstract.
 */
public class TaskTagTest {

    // ---- addTag() ----
    @Test
    void addTag_newTag_addsTagAndReturnsConfirmation() {
        Task task = new ToDoTask("read book");

        String result = task.addTag("fun");

        assertEquals("Tagged \"read book\" with #fun!", result);
        assertTrue(task.getTags().contains("fun"));
    }

    @Test
    void addTag_withLeadingHash_stripsHashBeforeStoring() {
        Task task = new ToDoTask("read book");

        task.addTag("#fun");

        assertTrue(task.getTags().contains("fun"));
        assertFalse(task.getTags().contains("#fun"));
    }

    @Test
    void addTag_duplicatePlainTag_leavesTagsUnchangedAndReturnsAlreadyTaggedMessage() {
        Task task = new ToDoTask("read book");
        task.addTag("fun");

        String result = task.addTag("fun");

        assertEquals("\"read book\" is already tagged #fun.", result);
        assertEquals(1, task.getTags().size());
    }

    @Test
    void addTag_hashPrefixedDuplicateOfPlainTag_isTreatedAsSameTag() {
        Task task = new ToDoTask("read book");
        task.addTag("fun");

        String result = task.addTag("#fun");

        assertEquals("\"read book\" is already tagged #fun.", result);
        assertEquals(1, task.getTags().size());
    }

    // ---- removeTag() ----
    @Test
    void removeTag_existingTag_removesTagAndReturnsConfirmation() {
        Task task = new ToDoTask("read book");
        task.addTag("fun");

        String result = task.removeTag("fun");

        assertEquals("Removed #fun from \"read book\"!", result);
        assertFalse(task.getTags().contains("fun"));
    }

    @Test
    void removeTag_nonExistentTag_leavesTagsUnchangedAndReturnsNotFoundMessage() {
        Task task = new ToDoTask("read book");

        String result = task.removeTag("nope");

        assertEquals("\"read book\" doesn't have the tag #nope.", result);
        assertTrue(task.getTags().isEmpty());
    }

    @Test
    void removeTag_withLeadingHash_removesUnderlyingTag() {
        Task task = new ToDoTask("read book");
        task.addTag("fun");

        task.removeTag("#fun");

        assertTrue(task.getTags().isEmpty());
    }

    // ---- getTags() ----
    @Test
    void getTags_multipleTagsAdded_preservesInsertionOrder() {
        Task task = new ToDoTask("read book");
        task.addTag("b");
        task.addTag("a");
        task.addTag("c");

        assertEquals(List.of("b", "a", "c"), new ArrayList<>(task.getTags()));
    }

    @Test
    void getTags_returnsUnmodifiableView() {
        Task task = new ToDoTask("read book");
        task.addTag("fun");

        Set<String> tags = task.getTags();

        assertThrows(UnsupportedOperationException.class, () -> tags.add("urgent"));
    }

    // ---- toString() / toFileString() suffix behavior ----
    @Test
    void toString_noTags_matchesUntaggedFormat() {
        Task task = new ToDoTask("read book");

        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    void toString_withTags_appendsHashtagsInInsertionOrder() {
        Task task = new ToDoTask("read book");
        task.addTag("fun");
        task.addTag("urgent");

        assertEquals("[T][ ] read book #fun #urgent", task.toString());
    }

    @Test
    void toFileString_noTags_omitsTagsField() {
        Task task = new ToDoTask("read book");

        assertEquals("T | 0 | read book", task.toFileString());
    }

    @Test
    void toFileString_withTags_appendsCommaSeparatedTagsField() {
        Task task = new ToDoTask("read book");
        task.addTag("fun");
        task.addTag("urgent");

        assertEquals("T | 0 | read book | fun,urgent", task.toFileString());
    }
}
