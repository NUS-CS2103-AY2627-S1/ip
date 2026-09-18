package zhangwei.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Task}, the done/not-done state and the text every task type
 * shares. The subclasses build their own text on top of this one, so getting
 * it wrong here would show up in every task the user sees.
 */
public class TaskTest {

    @Test
    public void constructor_newTask_notDone() {
        Task task = new Task("read book");
        assertFalse(task.isDone());
    }

    @Test
    public void getDescription_taskCreatedWithDescription_sameDescriptionReturned() {
        assertEquals("read book", new Task("read book").getDescription());
    }

    @Test
    public void getStatusIcon_notDoneTask_spaceReturned() {
        assertEquals(" ", new Task("read book").getStatusIcon());
    }

    @Test
    public void getStatusIcon_doneTask_crossReturned() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void markAsDone_notDoneTask_becomesDone() {
        Task task = new Task("read book");
        task.markAsDone();
        assertTrue(task.isDone());
    }

    @Test
    public void markAsDone_alreadyDoneTask_staysDone() {
        Task task = new Task("read book");
        task.markAsDone();
        task.markAsDone();
        assertTrue(task.isDone());
    }

    @Test
    public void markAsNotDone_doneTask_becomesNotDone() {
        Task task = new Task("read book");
        task.markAsDone();
        task.markAsNotDone();
        assertFalse(task.isDone());
    }

    @Test
    public void markAsNotDone_alreadyNotDoneTask_staysNotDone() {
        Task task = new Task("read book");
        task.markAsNotDone();
        assertFalse(task.isDone());
    }

    @Test
    public void toString_notDoneTask_emptyBoxShown() {
        assertEquals("[ ] read book", new Task("read book").toString());
    }

    @Test
    public void toString_doneTask_crossedBoxShown() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("[X] read book", task.toString());
    }
}
