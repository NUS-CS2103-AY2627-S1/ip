package shrek.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void markAsDone_notDoneTask_marksDone() {
        Task task = new Todo("read book");
        assertFalse(task.isDone());
        task.markAsDone();
        assertTrue(task.isDone());
    }

    @Test
    public void markAsNotDone_doneTask_marksNotDone() {
        Task task = new Todo("read book");
        task.markAsDone();
        task.markAsNotDone();
        assertFalse(task.isDone());
    }

    @Test
    public void getStatusIcon_notDone_returnsSpace() {
        Task task = new Todo("read book");
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void getStatusIcon_done_returnsX() {
        Task task = new Todo("read book");
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void toString_notDone_showsDescriptionWithEmptyBox() {
        Task task = new Todo("read book");
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void toString_done_showsDescriptionWithCheckedBox() {
        Task task = new Todo("read book");
        task.markAsDone();
        assertEquals("[T][X] read book", task.toString());
    }

    @Test
    public void taggedTask_sortsTagsAndUsesCanonicalForm() {
        Task task = new Todo("play game", List.of("#Weekend", "#fun"));

        assertEquals(List.of("#fun", "#weekend"), task.getTags());
        assertEquals("[T][ ] play game #fun #weekend", task.toString());
        assertEquals("T | 0 | play game | #fun,#weekend", task.toSaveFormat());
    }

    @Test
    public void addTags_invalidBatch_doesNotPartiallyMutateTask() {
        Task task = new Todo("play game", List.of("#fun"));

        assertThrows(IllegalArgumentException.class, () -> task.addTags(List.of("#school", "#school")));
        assertEquals(List.of("#fun"), task.getTags());
    }

    @Test
    public void removeTags_missingBatch_doesNotPartiallyMutateTask() {
        Task task = new Todo("play game", List.of("#fun", "#school"));

        assertThrows(IllegalArgumentException.class, () -> task.removeTags(List.of("#fun", "#missing")));
        assertEquals(List.of("#fun", "#school"), task.getTags());
    }
}
