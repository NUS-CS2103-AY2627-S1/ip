package gary.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void constructor_newTask_hasIncompleteStatus() {
        Task task = new Task("read book");

        assertEquals("read book", task.getDescription());
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    void markAsDone_incompleteTask_updatesStatus() {
        Task task = new Task("read book");

        task.markAsDone();

        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("[X] read book", task.toString());
    }

    @Test
    void markAsNotDone_completedTask_updatesStatus() {
        Task task = new Task("read book");
        task.markAsDone();

        task.markAsNotDone();

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    void containsKeyword_variedKeywords_returnsMatchingResult() {
        Task task = new Task("Read Book");

        assertTrue(task.containsKeyword("book"));
        assertTrue(task.containsKeyword("READ"));
        assertTrue(task.containsKeyword("ad bo"));
        assertFalse(task.containsKeyword("return"));
    }
}
