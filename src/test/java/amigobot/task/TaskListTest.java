package amigobot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests for TaskList operations: add, delete, get, and size.
 */
public class TaskListTest {

    @Test
    public void addTask_singleTask_sizeIncreases() {
        TaskList list = new TaskList();
        list.addTask(new Todo("read book"));
        assertEquals(1, list.size());
    }

    @Test
    public void addTask_multipleTasks_tracksAll() {
        TaskList list = new TaskList();
        list.addTask(new Todo("task one"));
        list.addTask(new Todo("task two"));
        list.addTask(new Todo("task three"));
        assertEquals(3, list.size());
        assertEquals("task two", list.getTask(1).getDescription());
    }

    @Test
    public void deleteTask_validIndex_returnsRemovedTask() {
        TaskList list = new TaskList();
        Task first = new Todo("keep me");
        Task second = new Todo("delete me");
        list.addTask(first);
        list.addTask(second);

        Task removed = list.deleteTask(1);
        assertEquals("delete me", removed.getDescription());
        assertEquals(1, list.size());
        assertEquals("keep me", list.getTask(0).getDescription());
    }

    @Test
    public void deleteTask_invalidIndex_throwsException() {
        TaskList list = new TaskList();
        list.addTask(new Todo("only task"));
        assertThrows(IndexOutOfBoundsException.class, () -> list.deleteTask(5));
    }

    @Test
    public void getTask_invalidIndex_throwsException() {
        TaskList list = new TaskList();
        assertThrows(IndexOutOfBoundsException.class, () -> list.getTask(0));
    }

    @Test
    public void size_emptyList_returnsZero() {
        TaskList list = new TaskList();
        assertEquals(0, list.size());
    }

    @Test
    public void findTasks_matchingKeyword_returnsMatches() {
        TaskList list = new TaskList();
        list.addTask(new Todo("read book"));
        list.addTask(new Todo("return book"));
        list.addTask(new Todo("buy groceries"));
        assertEquals(2, list.findTasks("book").size());
    }

    @Test
    public void findTasks_caseInsensitive_returnsMatches() {
        TaskList list = new TaskList();
        list.addTask(new Todo("read Book"));
        list.addTask(new Todo("buy groceries"));
        assertEquals(1, list.findTasks("BOOK").size());
        assertEquals("read Book", list.findTasks("book").get(0).getDescription());
    }

    @Test
    public void findTasks_noMatch_returnsEmptyList() {
        TaskList list = new TaskList();
        list.addTask(new Todo("read book"));
        list.addTask(new Todo("buy groceries"));
        assertEquals(0, list.findTasks("xyz").size());
    }
}
