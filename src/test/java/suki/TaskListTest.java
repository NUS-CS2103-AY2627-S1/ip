package suki;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TaskList}, in particular the bounds checking it centralises
 * on behalf of its callers.
 */
public class TaskListTest {

    @Test
    public void newList_isEmpty() {
        TaskList tasks = new TaskList();
        assertTrue(tasks.isEmpty());
        assertEquals(0, tasks.size());
    }

    @Test
    public void add_thenGet_returnsSameTask() throws SukiException {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        tasks.add(todo);

        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
        assertEquals(todo, tasks.get(0));
    }

    @Test
    public void remove_shrinksListAndReturnsRemovedTask() throws SukiException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));

        Task removed = tasks.remove(0);
        assertEquals("[T][ ] first", removed.toString());
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] second", tasks.get(0).toString());
    }

    @Test
    public void get_indexOutOfRange_throws() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("only task"));

        assertThrows(SukiException.class, () -> tasks.get(1));
        assertThrows(SukiException.class, () -> tasks.get(-1));
    }

    @Test
    public void remove_indexOutOfRange_throws() {
        TaskList tasks = new TaskList();
        assertThrows(SukiException.class, () -> tasks.remove(0));
    }

    /**
     * Guards the whole A-Assertions increment: if the build stops passing -ea,
     * every other assertion in the codebase quietly stops checking anything,
     * and this is the test that notices.
     */
    @Test
    public void add_null_failsWhileAssertionsAreEnabled() {
        assertThrows(AssertionError.class, () -> new TaskList().add(null));
    }

    @Test
    public void get_outOfRangeMessage_reportsActualSize() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("only task"));

        SukiException e = assertThrows(SukiException.class, () -> tasks.get(5));
        assertTrue(e.getMessage().contains("1 task"),
                "the message should tell the user how many tasks there are");
    }
}
