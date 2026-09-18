package herta.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

/**
 * Tests task-list state changes and the index-based query operations used by commands.
 */
class TaskListTest {

    @Test
    void addAndRemove_updateSizeAndReturnRemovedTask() {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");

        assertEquals(0, tasks.size());
        tasks.add(todo);
        assertEquals(1, tasks.size());
        assertEquals(todo, tasks.get(0));
        assertEquals(todo, tasks.remove(0));
        assertEquals(0, tasks.size());
    }

    @Test
    void add_duplicateTaskWithEquivalentWhitespace_allowsDuplicate() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        tasks.add(new Todo("  read   book  "));

        assertEquals(2, tasks.size());
        assertEquals("read book", tasks.get(0).getDescription());
        assertEquals("  read   book  ", tasks.get(1).getDescription());
    }

    @Test
    void constructor_nullTask_rejectsItImmediately() {
        assertThrows(NullPointerException.class, () ->
                new TaskList(Collections.singletonList(null)));
    }

    @Test
    void replaceWith_copiesReplacementContents() {
        TaskList tasks = new TaskList(List.of(new Todo("old")));
        TaskList replacement = new TaskList(List.of(new Todo("new")));

        tasks.replaceWith(replacement);

        assertEquals(1, tasks.size());
        assertEquals("new", tasks.get(0).getDescription());
        replacement.add(new Todo("later"));
        assertEquals(1, tasks.size());
    }

    @Test
    void markUnmarkAndRestoreStatus_updateTaskStatus() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertFalse(tasks.get(0).isCompleted());
        tasks.markTask(0);
        assertTrue(tasks.get(0).isCompleted());
        tasks.markTask(0);
        assertTrue(tasks.get(0).isCompleted());

        tasks.unmarkTask(0);
        assertFalse(tasks.get(0).isCompleted());
        tasks.unmarkTask(0);
        assertFalse(tasks.get(0).isCompleted());

        tasks.restoreStatus(0, true);
        assertTrue(tasks.get(0).isCompleted());
        tasks.restoreStatus(0, false);
        assertFalse(tasks.get(0).isCompleted());
    }

    @Test
    void findMatchingIndices_returnsOnlyMatchingTasksInStoredOrder() {
        TaskList tasks = new TaskList(List.of(
                new Todo("buy milk"),
                new Deadline("submit report", LocalDateTime.of(2019, 10, 15, 18, 0)),
                new Event("project meeting",
                        LocalDateTime.of(2019, 10, 14, 23, 0),
                        LocalDateTime.of(2019, 10, 16, 1, 0))));

        assertEquals(List.of(1, 2),
                tasks.findMatchingIndices(task -> task.occursOn(LocalDate.of(2019, 10, 15))));
    }

    @Test
    void getSortedIndices_returnsSortedIndicesWithoutChangingStoredOrder() {
        Todo todo = new Todo("buy milk");
        Deadline later = new Deadline("later", LocalDateTime.of(2019, 10, 16, 18, 0));
        Deadline earlier = new Deadline("earlier", LocalDateTime.of(2019, 10, 15, 18, 0));
        TaskList tasks = new TaskList(List.of(todo, later, earlier));

        List<Integer> sortedIndices = tasks.getSortedIndices(Comparator.comparing(
                task -> task.getScheduledDateTime().orElse(LocalDateTime.MAX)));

        assertEquals(List.of(2, 1, 0), sortedIndices);
        assertEquals(todo, tasks.get(0));
        assertEquals(later, tasks.get(1));
        assertEquals(earlier, tasks.get(2));
    }

    @Test
    void asUnmodifiableList_rejectsStructuralChanges() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(UnsupportedOperationException.class, () ->
                tasks.asUnmodifiableList().add(new Todo("write book")));
    }

    @Test
    void iterator_returnsTasksInStoredOrderAndRejectsRemoval() {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        TaskList tasks = new TaskList(List.of(first, second));
        Iterator<Task> iterator = tasks.iterator();

        assertEquals(first, iterator.next());
        assertEquals(second, iterator.next());
        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    void taskList_nullArguments_rejectThem() {
        TaskList tasks = new TaskList();

        assertThrows(NullPointerException.class, () -> new TaskList(null));
        assertThrows(NullPointerException.class, () -> tasks.add(null));
        assertThrows(NullPointerException.class, () -> tasks.replaceWith(null));
        assertThrows(NullPointerException.class, () -> tasks.findMatchingIndices(null));
        assertThrows(NullPointerException.class, () -> tasks.getSortedIndices(null));
    }

    @Test
    void taskList_invalidIndices_rejectAccessAndStatusUpdates() {
        TaskList tasks = new TaskList(List.of(new Todo("task")));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.remove(1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.markTask(1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.unmarkTask(1));
    }

    @Test
    void replaceWith_selfReplacement_preservesTheTasks() {
        TaskList tasks = new TaskList(List.of(new Todo("first"), new Todo("second")));

        tasks.replaceWith(tasks);

        assertEquals(List.of("first", "second"), List.of(
                tasks.get(0).getDescription(), tasks.get(1).getDescription()));
    }

    @Test
    void matchingAndSorting_emptyList_returnEmptyResults() {
        TaskList tasks = new TaskList();

        assertEquals(List.of(), tasks.findMatchingIndices(task -> true));
        assertEquals(List.of(), tasks.getSortedIndices(Comparator.comparing(Task::getDescription)));
    }

    @Test
    void getSortedIndices_equalKeys_preservesStoredOrder() {
        LocalDateTime scheduledTime = LocalDateTime.of(2019, 10, 15, 18, 0);
        Deadline first = new Deadline("first", scheduledTime);
        Deadline second = new Deadline("second", scheduledTime);
        Deadline third = new Deadline("third", scheduledTime);
        TaskList tasks = new TaskList(List.of(first, second, third));

        List<Integer> sortedIndices = tasks.getSortedIndices(Comparator.comparing(
                task -> task.getScheduledDateTime().orElse(LocalDateTime.MAX)));

        assertEquals(List.of(0, 1, 2), sortedIndices);
    }

    @Test
    void iterator_afterLastTask_throwsNoSuchElementException() {
        Iterator<Task> iterator = new TaskList(List.of(new Todo("task"))).iterator();

        iterator.next();

        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void add_atMaximumTaskCount_rejectsNextTaskWithoutChangingOrder() {
        Todo sharedTask = new Todo("shared task");
        TaskList tasks = new TaskList(Collections.nCopies(TaskList.MAXIMUM_TASK_COUNT, sharedTask));

        assertEquals(TaskList.MAXIMUM_TASK_COUNT, tasks.size());
        assertEquals(sharedTask, tasks.get(0));
        assertEquals(sharedTask, tasks.get(TaskList.MAXIMUM_TASK_COUNT - 1));
        assertThrows(IllegalArgumentException.class, () -> tasks.add(sharedTask));
        assertEquals(TaskList.MAXIMUM_TASK_COUNT, tasks.size());
        assertEquals(sharedTask, tasks.get(TaskList.MAXIMUM_TASK_COUNT - 1));
    }
}
