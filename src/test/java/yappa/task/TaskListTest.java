package yappa.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import yappa.exception.YappaException;

/**
 * Tests task-list mutations and validation of task indices.
 */
public class TaskListTest {

    /**
     * Verifies that the list constructor makes a defensive copy.
     */
    @Test
    public void constructor_sourceListChanges_doesNotChangeTaskList() {
        List<Task> source = new ArrayList<>();
        source.add(new Todo("read book"));
        TaskList tasks = new TaskList(source);

        source.clear();

        assertEquals(1, tasks.size());
    }

    /**
     * Verifies that adding and removing tasks updates list contents correctly.
     *
     * @throws YappaException If a valid task index is unexpectedly rejected.
     */
    @Test
    public void addAndRemove_validTasks_updatesListAndReturnsRemovedTask() throws YappaException {
        TaskList tasks = new TaskList();
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("write notes");

        tasks.add(firstTask);
        tasks.add(secondTask);
        Task removedTask = tasks.remove(0);

        assertEquals(firstTask, removedTask);
        assertEquals(1, tasks.size());
        assertEquals(secondTask, tasks.iterator().next());
    }

    /**
     * Verifies that marking and unmarking update a task's completion state.
     *
     * @throws YappaException If a valid task index is unexpectedly rejected.
     */
    @Test
    public void markAndUnmark_validIndex_updatesCompletionState() throws YappaException {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        Task markedTask = tasks.mark(0);
        assertTrue(markedTask.toString().contains("[X]"));

        Task unmarkedTask = tasks.unmark(0);
        assertFalse(unmarkedTask.toString().contains("[X]"));
    }

    /**
     * Verifies that invalid mutations fail without changing the task list.
     */
    @Test
    public void mutations_invalidIndices_throwYappaExceptionWithoutChangingList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(YappaException.class, () -> tasks.mark(-1));
        assertThrows(YappaException.class, () -> tasks.unmark(1));
        assertThrows(YappaException.class, () -> tasks.remove(2));
        assertEquals(1, tasks.size());
        assertEquals("[T] [ ] read book", tasks.iterator().next().toString());
    }

    /**
     * Verifies formatting of empty and populated task lists.
     */
    @Test
    public void toString_emptyAndPopulatedLists_formatsNumberedTasks() {
        assertEquals("No tasks", new TaskList().toString());

        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Todo("write notes", true)));
        String expected = "\t1.[T] [ ] read book" + System.lineSeparator()
                + "\t2.[T] [X] write notes" + System.lineSeparator();

        assertEquals(expected, tasks.toString());
    }
}
