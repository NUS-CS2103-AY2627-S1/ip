package nyonbot.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class TaskListTest {
    @Test
    void addGetAndSize_tasks_returnsInsertionOrder() {
        TaskList tasks = new TaskList();
        Task first = new Task("first");
        Task second = new Task("second");

        tasks.add(first);
        tasks.add(second);

        assertEquals(2, tasks.size());
        assertEquals(first, tasks.get(0));
        assertEquals(second, tasks.get(1));
    }

    @Test
    void addAll_nonEmptyCollection_addsTasksInCollectionOrder() {
        TaskList tasks = new TaskList();
        Task first = new Task("first");
        Task second = new Task("second");

        assertTrue(tasks.addAll(List.of(first, second)));
        assertEquals(List.of(first, second), tasks.stream().toList());
    }

    @Test
    void removeByIndex_validIndex_removesAndReturnsTask() {
        TaskList tasks = new TaskList();
        Task task = new Task("task");
        tasks.add(task);

        assertEquals(task, tasks.remove(0));
        assertEquals(0, tasks.size());
    }

    @Test
    void removeByObject_presentAndAbsentTask_returnsExpectedResult() {
        TaskList tasks = new TaskList();
        Task task = new Task("task");
        tasks.add(task);

        assertTrue(tasks.remove(task));
        assertFalse(tasks.remove(task));
    }

    @Test
    void get_invalidIndex_throwsIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> new TaskList().get(0));
    }

    @Test
    void iterator_multipleTasks_visitsInsertionOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Task("first"));
        tasks.add(new Task("second"));
        List<String> names = new ArrayList<>();

        for (Task task : tasks) {
            names.add(task.getName());
        }

        assertEquals(List.of("first", "second"), names);
    }

    @Test
    void clear_nonEmptyList_removesAllTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Task("task"));

        tasks.clear();

        assertTrue(tasks.isEmpty());
        assertEquals(0, tasks.size());
    }

    @Test
    void isEmpty_newAndPopulatedList_returnsExpectedState() {
        TaskList tasks = new TaskList();

        assertTrue(tasks.isEmpty());

        tasks.add(new Task("task"));

        assertFalse(tasks.isEmpty());
    }
}
