package cheecken;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Checks task completion state and storage output.
 */
class TaskTest {
    /**
     * Verifies new task_is unmarked.
     */
    @Test
    void newTask_isUnmarked() {
        Task task = new Task("read book");

        assertEquals("[ ] read book", task.toString());
        assertEquals("T | 0 | read book", task.toStorageString());
    }

    /**
     * Verifies mark with unmarked task: marks task as done.
     */
    @Test
    void mark_unmarkedTask_marksTaskAsDone() {
        Task task = new Task("read book");

        task.mark();

        assertEquals("[X] read book", task.toString());
        assertEquals("T | 1 | read book", task.toStorageString());
    }

    /**
     * Verifies unmark with marked task: marks task as not done.
     */
    @Test
    void unmark_markedTask_marksTaskAsNotDone() {
        Task task = new Task("read book");
        task.mark();

        task.unmark();

        assertEquals("[ ] read book", task.toString());
        assertEquals("T | 0 | read book", task.toStorageString());
    }

    /**
     * Verifies mark with already marked task: remains marked.
     */
    @Test
    void mark_alreadyMarkedTask_remainsMarked() {
        Task task = new Task("read book");

        task.mark();
        task.mark();

        assertEquals("[X] read book", task.toString());
    }

    /**
     * Verifies unmark with unmarked task: remains unmarked.
     */
    @Test
    void unmark_unmarkedTask_remainsUnmarked() {
        Task task = new Task("read book");

        task.unmark();

        assertEquals("[ ] read book", task.toString());
    }
}
