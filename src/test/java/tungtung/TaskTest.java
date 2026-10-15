package tungtung;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests completion-state behavior shared by all task types. */
class TaskTest {
    @Test
    void task_newTask_isNotDone() {
        Task task = new Task("read book");

        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    void task_setDone_marksTaskComplete() {
        Task task = new Task("read book");

        task.setDone();

        assertEquals("X", task.getStatusIcon());
        assertEquals("[X] read book", task.toString());
    }

    @Test
    void task_setUndone_clearsCompletion() {
        Task task = new Task("read book");
        task.setDone();

        task.setUndone();

        assertEquals(" ", task.getStatusIcon());
    }
}
