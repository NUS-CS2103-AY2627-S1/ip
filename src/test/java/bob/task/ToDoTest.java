package bob.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests completion state and display behavior shared by tasks. */
class ToDoTest {

    @Test
    void newTodo_hasNotDoneStatusAndExpectedDisplay() {
        ToDo task = new ToDo("read book");

        assertEquals(TaskStatus.NOT_DONE, task.getStatus());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    void markAndUnmark_updatesStatusAndDisplay() {
        ToDo task = new ToDo("read book");

        task.markAsDone();
        assertEquals(TaskStatus.DONE, task.getStatus());
        assertEquals("[T][X] read book", task.toString());

        task.markAsNotDone();
        assertEquals(TaskStatus.NOT_DONE, task.getStatus());
        assertEquals("[T][ ] read book", task.toString());
    }
}
