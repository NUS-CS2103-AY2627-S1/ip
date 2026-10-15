package task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void getDescReturnsCorrectDescription() {
        Task task = new Task("date Sunny");
        assertEquals("date Sunny", task.getDesc());
    }

    @Test
    public void newTaskIsNotMarked() {
        Task task = new Task("date Sunny");
        assertFalse(task.isDone());
    }

    @Test
    public void newTaskHasCorrectStatusIcon() {
        Task task = new Task("date Sunny");
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void markMarksTaskAsDone() {
        Task task = new Task("date Sunny");
        task.mark();
        assertTrue(task.isDone());
    }

    @Test
    public void markedTaskHasCorrectStatusIcon() {
        Task task = new Task("date Sunny");
        task.mark();
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void unmarkMarksTaskAsNotDone() {
        Task task = new Task("date Sunny");
        task.mark();
        task.unmark();
        assertFalse(task.isDone());
    }

    @Test
    public void unmarkedTaskHasCorrectStatusIcon() {
        Task task = new Task("date Sunny");
        task.mark();
        task.unmark();
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void toStringReturnsCorrectFormat() {
        Task task = new Task("date Sunny");
        assertEquals("[ ] date Sunny", task.toString());
    }

    @Test
    public void markedTaskToStringReturnsCorrectFormat() {
        Task task = new Task("date Sunny");
        task.mark();
        assertEquals("[X] date Sunny", task.toString());
    }
}