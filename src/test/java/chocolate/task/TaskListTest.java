package chocolate.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import chocolate.exception.ChocolateException;

/**
 * Tests mutations and bounds checking in {@link TaskList}.
 */
public class TaskListTest {
    @Test
    public void taskOperations_validIndexes_updateListAndStatus() throws ChocolateException {
        TaskList tasks = new TaskList();
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("write report");

        tasks.add(firstTask);
        tasks.add(secondTask);
        tasks.mark(1);

        assertEquals(2, tasks.size());
        assertFalse(firstTask.isDone());
        assertTrue(secondTask.isDone());

        tasks.unmark(1);
        Task deletedTask = tasks.delete(0);

        assertFalse(secondTask.isDone());
        assertEquals(firstTask, deletedTask);
        assertEquals(1, tasks.size());
        assertEquals(secondTask, tasks.get(0));
    }

    @Test
    public void indexedOperations_invalidIndexes_exceptionWithoutMutation() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertThrows(ChocolateException.class, () -> tasks.mark(-1));
        assertThrows(ChocolateException.class, () -> tasks.unmark(1));
        assertThrows(ChocolateException.class, () -> tasks.delete(5));
        assertEquals(1, tasks.size());
        assertFalse(tasks.get(0).isDone());
    }

    @Test
    public void getAll_returnedView_cannotBeModified() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertThrows(UnsupportedOperationException.class, () ->
                tasks.getAll().add(new Todo("write report")));
        assertEquals(1, tasks.size());
    }

    @Test
    public void contains_sameDisplayedTask_returnsTrue() throws ChocolateException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertTrue(tasks.contains(new Todo("read book")));
        assertFalse(tasks.contains(new Todo("write report")));
        tasks.mark(0);
        assertTrue(tasks.contains(new Todo("read book")));
    }

    @Test
    public void find_keywordWithDifferentCase_returnsMatchingDescriptions() {
        TaskList tasks = new TaskList();
        Todo firstMatch = new Todo("Read Book");
        Todo nonMatch = new Todo("write report");
        Todo secondMatch = new Todo("return book");
        tasks.add(firstMatch);
        tasks.add(nonMatch);
        tasks.add(secondMatch);

        List<Task> matchingTasks = tasks.find("BOOK");

        assertEquals(List.of(firstMatch, secondMatch), matchingTasks);
    }

    @Test
    public void find_noMatchingDescription_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertTrue(tasks.find("meeting").isEmpty());
    }
}
