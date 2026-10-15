package bobo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import bobo.exception.BoboException;

/**
 * JUnit tests for TaskList class.
 */
public class TaskListTest {

    @Test
    public void testAddAndDeleteTask() throws BoboException {
        TaskList tasks = new TaskList();
        assertEquals(0, tasks.size());

        Task todo = new Todo("read book");
        tasks.add(todo);
        assertEquals(1, tasks.size());
        assertEquals(todo, tasks.get(1));

        Task removed = tasks.delete(1);
        assertEquals(todo, removed);
        assertEquals(0, tasks.size());
    }

    @Test
    public void testMarkAndUnmarkTask() throws BoboException {
        TaskList tasks = new TaskList();
        Task todo = new Todo("buy groceries");
        tasks.add(todo);

        assertFalse(tasks.get(1).isDone());

        tasks.mark(1);
        assertTrue(tasks.get(1).isDone());

        tasks.unmark(1);
        assertFalse(tasks.get(1).isDone());
    }

    @Test
    public void testDeleteOutOfBounds_exceptionThrown() {
        TaskList tasks = new TaskList();
        assertThrows(BoboException.class, () -> tasks.delete(1));
    }

    @Test
    public void testMarkOutOfBounds_exceptionThrown() {
        TaskList tasks = new TaskList();
        assertThrows(BoboException.class, () -> tasks.mark(0));
    }

    @Test
    public void testFindTasks() {
        TaskList tasks = new TaskList();
        Task todo1 = new Todo("read book");
        Task todo2 = new Todo("write code");
        Task todo3 = new Todo("return book to library");
        tasks.add(todo1);
        tasks.add(todo2);
        tasks.add(todo3);

        java.util.List<Task> found = tasks.findTasks("book");
        assertEquals(2, found.size());
        assertEquals(todo1, found.get(0));
        assertEquals(todo3, found.get(1));
    }
}
