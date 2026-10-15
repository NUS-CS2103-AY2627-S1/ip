package atlas.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests the common state and display behaviour of tasks. */
public class TaskTest {

    @Test
    void newTaskStoresItsDescriptionAndStartsIncomplete() {
        Task task = new Task("read the Odyssey");

        assertEquals("read the Odyssey", task.getDescription());
        assertFalse(task.isDone());
        assertEquals("[ ] read the Odyssey", task.toString());
    }

    @Test
    void markingTaskAsDoneChangesItsStatus() {
        Task task = new Task("read the Odyssey");

        task.markAsDone();

        assertTrue(task.isDone());
        assertEquals("[X] read the Odyssey", task.toString());
    }

    @Test
    void markingTaskAsNotDoneRestoresItsIncompleteStatus() {
        Task task = new Task("read the Odyssey");
        task.markAsDone();

        task.markAsNotDone();

        assertFalse(task.isDone());
        assertEquals("[ ] read the Odyssey", task.toString());
    }

    @Test
    void attachingAPriorityShowsItInTheDisplayForm() {
        Task task = new Task("read the Odyssey");

        task.setPriority(Priority.HIGH);

        assertEquals(Priority.HIGH, task.getPriority());
        assertEquals("[HIGH]", task.getPriorityTag());
        assertEquals("[ ][HIGH] read the Odyssey", task.toString());
    }

    @Test
    void clearingAPriorityRestoresThePlainDisplayForm() {
        Task task = new Task("read the Odyssey");
        task.setPriority(Priority.LOW);

        task.clearPriority();

        assertNull(task.getPriority());
        assertEquals("", task.getPriorityTag());
        assertEquals("[ ] read the Odyssey", task.toString());
    }

    @Test
    void aPrioritySurvivesMarkingTheTaskDone() {
        Task task = new Task("read the Odyssey");
        task.setPriority(Priority.MEDIUM);

        task.markAsDone();

        assertEquals("[X][MEDIUM] read the Odyssey", task.toString());
    }
}
