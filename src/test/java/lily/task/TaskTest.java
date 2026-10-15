package lily.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Task}. Lighter coverage than {@link TaskListTest} or the parser/storage
 * tests, since most of this class is simple state (a description and a done flag), but
 * {@link Task#toFileString()} and {@link Task#toString()} are still worth pinning down
 * directly: they are the exact text every other part of the app (display and the save
 * file) depends on this class producing correctly.
 */
public class TaskTest {

    @Test
    public void getStatusIcon_newTask_returnsSpace() {
        Task task = new Task("read book");
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void getStatusIcon_markedDone_returnsX() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void markAsUndone_previouslyMarkedTask_statusIconRevertsToSpace() {
        Task task = new Task("read book");
        task.markAsDone();
        task.markAsUndone();
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void toString_newTask_showsBlankBoxAndDescription() {
        Task task = new Task("read book");
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    public void toString_markedDone_showsXInBox() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("[X] read book", task.toString());
    }

    @Test
    public void toFileString_newTask_formattedAsToDoTypeRecord() {
        Task task = new Task("read book");
        assertEquals("T | 0 | read book", task.toFileString());
    }

    @Test
    public void toFileString_markedDone_doneFlagIsOne() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("T | 1 | read book", task.toFileString());
    }
}
