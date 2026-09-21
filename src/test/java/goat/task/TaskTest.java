package goat.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests the behavior every kind of task shares: the done flag, the symbol it
 * shows, and the two renderings built on top of it.
 *
 * The screen wording and the saved wording are deliberately different -- "X"
 * against "1" -- so that one can change without breaking the other. Both are
 * pinned here.
 */
public class TaskTest {

    @Test
    public void newTask_always_startsNotDone() {
        assertEquals(" ", new Task("read book").getStatusIcon());
    }

    @Test
    public void markAsDone_notDoneTask_statusIconBecomesX() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void markAsDone_alreadyDone_staysDone() {
        Task task = new Task("read book");
        task.markAsDone();
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void markAsNotDone_doneTask_statusIconBecomesSpace() {
        Task task = new Task("read book");
        task.markAsDone();
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void markAsNotDone_alreadyNotDone_staysNotDone() {
        Task task = new Task("read book");
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void toString_notDone_showsEmptyBox() {
        assertEquals("[ ] read book", new Task("read book").toString());
    }

    @Test
    public void toString_done_showsX() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("[X] read book", task.toString());
    }

    @Test
    public void toFileFormat_notDone_writesZero() {
        // The file uses 0/1, not the "X" shown on screen.
        assertEquals("0 | read book", new Task("read book").toFileFormat());
    }

    @Test
    public void toFileFormat_done_writesOne() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("1 | read book", task.toFileFormat());
    }
}
