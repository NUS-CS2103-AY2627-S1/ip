package soar.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests ordered task mutations, rollback helpers, snapshots, and date matching.
 */
public class TaskListTest {
    /** Verifies that construction and snapshots cannot bypass list ownership. */
    @Test
    public void constructorAndSnapshot_externalMutation_doesNotChangeTaskList() {
        ToDo original = new ToDo("original");
        ArrayList<Task> initialTasks = new ArrayList<>(List.of(original));
        TaskList tasks = new TaskList(initialTasks);

        initialTasks.add(new ToDo("added outside"));

        assertEquals(1, tasks.size());
        assertSame(original, tasks.get(0));
        assertThrows(UnsupportedOperationException.class, () ->
                tasks.asList().add(new ToDo("bypass")));
    }

    /** Verifies add, delete, and restoration at the original position. */
    @Test
    public void addDeleteAndRestore_validTasks_preservesOrder() {
        ToDo first = new ToDo("first");
        ToDo second = new ToDo("second");
        TaskList tasks = new TaskList(List.of(first));

        tasks.add(second);
        Task removed = tasks.delete(0);
        tasks.restoreDeletedTask(0, removed);

        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
        assertThrows(NullPointerException.class, () -> tasks.add(null));
        assertThrows(NullPointerException.class, () -> tasks.restoreDeletedTask(0, null));
    }

    /** Verifies completion changes and restoration of both possible earlier states. */
    @Test
    public void markUnmarkAndRestore_validTask_restoresCompletionState() {
        ToDo task = new ToDo("test state");
        TaskList tasks = new TaskList(List.of(task));

        assertSame(task, tasks.mark(0));
        assertTrue(task.isDone());
        tasks.restoreCompletion(0, false);
        assertFalse(task.isDone());

        tasks.mark(0);
        assertSame(task, tasks.unmark(0));
        assertFalse(task.isDone());
        tasks.restoreCompletion(0, true);
        assertTrue(task.isDone());
    }

    /** Verifies replacement returns the original and retains list size and position. */
    @Test
    public void replace_existingTask_replacesAtOriginalPosition() {
        ToDo first = new ToDo("first");
        ToDo original = new ToDo("original");
        ToDo replacement = new ToDo("replacement");
        TaskList tasks = new TaskList(List.of(first, original));

        assertSame(original, tasks.replace(1, replacement));

        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(replacement, tasks.get(1));
        assertThrows(NullPointerException.class, () -> tasks.replace(1, null));
    }

    /** Verifies matching indices for deadlines and inclusive dated event ranges. */
    @Test
    public void findIndicesOn_mixedTasks_returnsOriginalMatchingIndices() {
        TaskList tasks = new TaskList(List.of(
                new ToDo("undated"),
                new Deadline("due", LocalDate.of(2019, 10, 15)),
                new Event("conference", "2019-10-14", "2019-10-16"),
                new Event("free-form", "noon", "1pm")));

        assertEquals(List.of(1, 2), tasks.findIndicesOn(LocalDate.of(2019, 10, 15)));
        assertEquals(List.of(2), tasks.findIndicesOn(LocalDate.of(2019, 10, 16)));
        assertTrue(tasks.findIndicesOn(LocalDate.of(2019, 10, 17)).isEmpty());
        assertThrows(UnsupportedOperationException.class, () ->
                tasks.findIndicesOn(LocalDate.of(2019, 10, 15)).add(3));
    }

    /** Verifies description searches preserve order and ignore task metadata. */
    @Test
    public void findByDescription_mixedTasks_returnsOnlyDescriptionMatches() {
        ToDo first = new ToDo("read book");
        Deadline second = new Deadline("return book", LocalDate.of(2019, 6, 6));
        Deadline metadataOnly = new Deadline("submit report", LocalDate.of(2019, 6, 6));
        TaskList tasks = new TaskList(List.of(first, second, metadataOnly));

        assertEquals(List.of(first, second), tasks.findByDescription("book"));
        assertTrue(tasks.findByDescription("Book").isEmpty());
        assertTrue(tasks.findByDescription("June").isEmpty());
        assertThrows(UnsupportedOperationException.class, () ->
                tasks.findByDescription("book").add(new ToDo("another book")));
    }
}
