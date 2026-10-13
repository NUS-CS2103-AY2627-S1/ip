package ruby.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    void newTask_isNotDone() {
        Task task = new Task("read book");
        assertEquals(" ", task.getStatusIcon());
        assertEquals("0 | read book", task.toDataString());
    }

    @Test
    void toString_includesStatusIconAndDescription() {
        Task task = new Task("read book");
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    void markAsDone_updatesStatusIconToX() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    void markAsDone_updatesToStringAndDataString() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("[X] read book", task.toString());
        assertEquals("1 | read book", task.toDataString());
    }

    @Test
    void markAsNotDone_afterBeingDone_revertsStatus() {
        Task task = new Task("read book");
        task.markAsDone();
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] read book", task.toString());
        assertEquals("0 | read book", task.toDataString());
    }

    @Test
    void markAsDone_twice_staysDone() {
        Task task = new Task("read book");
        task.markAsDone();
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    void markAsNotDone_whenAlreadyNotDone_staysNotDone() {
        Task task = new Task("read book");
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] read book", task.toString());
    }
}
