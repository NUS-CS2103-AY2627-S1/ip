package lumi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests task status changes, rendering, and description matching. */
public class TaskTest {
    @Test
    public void hasDescriptionContaining_exactPartialAndMixedCaseKeywords_trueReturned() {
        Task task = new Task("Read Book Reviews");

        assertTrue(task.hasDescriptionContaining("Read Book Reviews"));
        assertTrue(task.hasDescriptionContaining("Book"));
        assertTrue(task.hasDescriptionContaining("book reviews"));
        assertTrue(task.hasDescriptionContaining("READ"));
    }

    @Test
    public void hasDescriptionContaining_absentKeyword_falseReturned() {
        Task task = new Task("read book");

        assertFalse(task.hasDescriptionContaining("magazine"));
    }

    @Test
    public void markAndUnmarkTask_statusAndDisplayUpdated() {
        Task task = new Task("read book");

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] read book", task.toString());

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("[X] read book", task.toString());

        task.markAsNotDone();
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] read book", task.toString());
    }
}
