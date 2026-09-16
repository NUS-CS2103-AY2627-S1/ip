package nova.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void getStatusIcon_newTask_returnsNotDone() {
        Task task = new Task("read book");

        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void markDone_notDoneTask_marksDone() {
        Task task = new Task("read book");

        task.markDone();

        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void unmarkUndone_doneTask_marksNotDone() {
        Task task = new Task("read book");
        task.markDone();

        task.unmarkUndone();

        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void getDescription_returnsDescription() {
        Task task = new Task("read book");

        assertEquals("read book", task.getDescription());
    }

    @Test
    public void toString_newTask_returnsFormattedTask() {
        Task task = new Task("read book");

        assertEquals("[ ] read book", task.toString());
    }
}