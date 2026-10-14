package bingus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests operations on {@link TaskList}.
 */
class TaskListTest {
    /**
     * Verifies that keyword searching ignores case and preserves task order.
     */
    @Test
    void findByKeyword_matchingDescriptions_returnsMatchingTasksInOrder() {
        Todo firstTask = new Todo("read book");
        Todo unrelatedTask = new Todo("buy groceries");
        Todo lastTask = new Todo("return BOOK");
        TaskList taskList = new TaskList(List.of(firstTask, unrelatedTask, lastTask));

        List<Task> matchingTasks = taskList.findByKeyword("book");

        assertEquals(List.of(firstTask, lastTask), matchingTasks);
    }

    /**
     * Verifies that replacing a task preserves the list's size and surrounding task order.
     */
    @Test
    void replace_existingTask_replacesTaskAndReturnsOriginalTask() {
        Todo firstTask = new Todo("read book");
        Todo originalTask = new Todo("buy groceries");
        Todo lastTask = new Todo("return book");
        Todo replacementTask = new Todo("buy fruit");
        TaskList taskList = new TaskList(List.of(firstTask, originalTask, lastTask));

        Task replacedTask = taskList.replace(1, replacementTask);

        assertEquals(originalTask, replacedTask);
        assertEquals(3, taskList.size());
        assertEquals(List.of(firstTask, replacementTask, lastTask), taskList.getTasksSnapshot());
    }

    /**
     * Verifies that a search without matching descriptions returns an empty list.
     */
    @Test
    void findByKeyword_noMatchingDescriptions_returnsEmptyList() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));

        assertEquals(List.of(), taskList.findByKeyword("groceries"));
    }

    /**
     * Verifies that construction copies the supplied list and snapshots cannot be modified.
     */
    @Test
    void getTasksSnapshot_taskListIsIndependentAndSnapshotIsUnmodifiable() {
        List<Task> originalTasks = new ArrayList<>(List.of(new Todo("read book")));
        TaskList taskList = new TaskList(originalTasks);
        originalTasks.add(new Todo("buy groceries"));

        List<Task> snapshot = taskList.getTasksSnapshot();
        assertEquals(1, snapshot.size());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new Todo("write tests")));
    }

    /**
     * Verifies that adding, retrieving, and removing tasks preserves their expected order.
     */
    @Test
    void addGetAndRemove_tasks_updatesListInOrder() {
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("write tests");
        TaskList taskList = new TaskList();

        taskList.add(firstTask);
        taskList.add(0, secondTask);

        assertEquals(List.of(secondTask, firstTask), taskList.getTasksSnapshot());
        assertEquals(secondTask, taskList.get(0));
        assertEquals(secondTask, taskList.remove(0));
        assertEquals(List.of(firstTask), taskList.getTasksSnapshot());
    }
}
