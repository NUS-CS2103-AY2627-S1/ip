package ramly.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import ramly.exception.TaskValidationException;

/** Tests task collection mutation and access. */
public class TaskListTest {
    @Test
    public void addAndGet_task_returnsStoredTask() {
        TaskList tasks = new TaskList(new ArrayList<>());
        Task task = new Todo("read a book");

        tasks.add(task);

        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(0));
    }

    @Test
    public void remove_existingIndex_returnsRemovedTaskAndUpdatesSize() {
        Task task = new Todo("read a book");
        TaskList tasks = new TaskList(new ArrayList<>());
        tasks.add(task);

        assertSame(task, tasks.remove(0));
        assertEquals(0, tasks.size());
    }

    @Test
    public void find_keyword_returnsCaseInsensitiveMatches() {
        Task firstMatchingTask = new Todo("Read a Book");
        Task otherTask = new Todo("Write report");
        Task secondMatchingTask = new Todo("Return BOOK");
        TaskList tasks = new TaskList(new ArrayList<>());
        tasks.add(firstMatchingTask);
        tasks.add(otherTask);
        tasks.add(secondMatchingTask);

        assertEquals(java.util.List.of(firstMatchingTask, secondMatchingTask), tasks.find("book"));
    }

    @Test
    public void constructor_nullCollection_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new TaskList(null));
    }

    @Test
    public void constructor_collectionContainingNull_throwsAssertionError() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(null);

        assertThrows(AssertionError.class, () -> new TaskList(tasks));
    }

    @Test
    public void add_nullTask_throwsAssertionError() {
        TaskList tasks = new TaskList(new ArrayList<>());

        assertThrows(AssertionError.class, () -> tasks.add(null));
    }

    @Test
    public void add_normalizedDuplicate_throwsValidationException() {
        TaskList tasks = new TaskList(new ArrayList<>());
        tasks.add(new Todo("Read   Book"));

        assertThrows(TaskValidationException.class, () -> tasks.add(new Todo("read book")));
    }

    @Test
    public void add_sameDescriptionWithDifferentDeadline_addsBoth() {
        TaskList tasks = new TaskList(new ArrayList<>());
        tasks.add(new Deadline("submit", "2026-10-01"));

        tasks.add(new Deadline("submit", "2026-10-02"));

        assertEquals(2, tasks.size());
    }

    @Test
    public void add_sameEventUsingDifferentDateFormats_throwsValidationException() {
        TaskList tasks = new TaskList(new ArrayList<>());
        tasks.add(new Event("meeting", "2026-10-01 0900", "2026-10-01 1000"));

        assertThrows(TaskValidationException.class,
                () -> tasks.add(new Event("MEETING", "1/10/2026 0900", "1/10/2026 1000")));
    }

    @Test
    public void find_nullKeyword_throwsAssertionError() {
        TaskList tasks = new TaskList(new ArrayList<>());

        assertThrows(AssertionError.class, () -> tasks.find(null));
    }

    @Test
    public void undo_addedTask_removesTaskAndConsumesHistory() {
        Task task = new Todo("read a book");
        TaskList tasks = new TaskList(new ArrayList<>());
        tasks.add(task);

        UndoResult result = tasks.undo();

        assertEquals(UndoResult.Action.ADD, result.getAction());
        assertSame(task, result.getTask());
        assertEquals(0, tasks.size());
        assertNull(tasks.undo());
    }

    @Test
    public void undo_deletedTask_restoresOriginalPosition() {
        Task firstTask = new Todo("first");
        Task deletedTask = new Todo("second");
        Task thirdTask = new Todo("third");
        TaskList tasks = new TaskList(new ArrayList<>());
        tasks.add(firstTask);
        tasks.add(deletedTask);
        tasks.add(thirdTask);
        tasks.remove(1);

        UndoResult result = tasks.undo();

        assertEquals(UndoResult.Action.DELETE, result.getAction());
        assertSame(deletedTask, tasks.get(1));
        assertSame(thirdTask, tasks.get(2));
    }

    @Test
    public void undo_statusChanges_restoresPreviousStatusIncludingNoOps() {
        Task task = new Todo("read a book");
        TaskList tasks = new TaskList(new ArrayList<>());
        tasks.add(task);

        tasks.mark(0);
        tasks.undo();
        assertEquals("[ ]", task.getStatusIcon());

        task.mark();
        tasks.unmark(0);
        tasks.undo();
        assertEquals("[✓]", task.getStatusIcon());

        tasks.mark(0);
        tasks.undo();
        assertEquals("[✓]", task.getStatusIcon());

        task.unmark();
        tasks.unmark(0);
        tasks.undo();
        assertEquals("[ ]", task.getStatusIcon());
    }
}
