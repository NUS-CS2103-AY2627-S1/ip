package verity.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    void constructor_newTask_notDoneByDefault() {
        Task task = new Task("read book");

        assertEquals("read book", task.getDescription());
        assertFalse(task.getStatusIcon().equals("X"));
    }

    @Test
    void getStatusIcon_notDone_blankSpace() {
        Task task = new Task("read book");

        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void getStatusIcon_markedDone_xReturned() {
        Task task = new Task("read book");

        task.markAsDone();

        assertEquals("X", task.getStatusIcon());
    }

    @Test
    void markAsNotDone_previouslyMarkedDone_revertsToNotDone() {
        Task task = new Task("read book");
        task.markAsDone();

        task.markAsNotDone();

        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void toString_notDone_showsBlankStatusIcon() {
        Task task = new Task("read book");

        assertEquals("[ ] read book", task.toString());
    }

    @Test
    void toString_done_showsXStatusIcon() {
        Task task = new Task("read book");
        task.markAsDone();

        assertEquals("[X] read book", task.toString());
    }

    @Test
    void toSaveFormat_notDone_startsWithZero() {
        Task task = new Task("read book");

        assertEquals("0 | read book", task.toSaveFormat());
    }

    @Test
    void toSaveFormat_done_startsWithOne() {
        Task task = new Task("read book");
        task.markAsDone();

        assertEquals("1 | read book", task.toSaveFormat());
    }
}
