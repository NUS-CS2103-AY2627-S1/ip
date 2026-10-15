package whimsybot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task-list storage, mutation, and capacity behavior. */
class TaskListTest {
    @Test
    void constructor_withSavedTasks_preservesOrderAndSize() {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        TaskList taskList = new TaskList(List.of(first, second));

        assertEquals(2, taskList.size());
        assertSame(first, taskList.get(0));
        assertSame(second, taskList.get(1));
    }

    @Test
    void addAndDeleteTask_updatesSizeAndShiftsRemainingTasks() {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        TaskList taskList = new TaskList(List.of(first, second));

        Todo third = new Todo("third");
        taskList.add(third);
        assertEquals(3, taskList.size());

        assertSame(first, taskList.delete(0));
        assertEquals(2, taskList.size());
        assertSame(second, taskList.get(0));
        assertSame(third, taskList.get(1));
    }

    @Test
    void markAndUnmarkTask_changesTaskState() {
        Todo task = new Todo("task");
        TaskList taskList = new TaskList(List.of(task));

        taskList.mark(0);
        assertTrue(task.isDone());

        taskList.unmark(0);
        assertFalse(task.isDone());
    }

    @Test
    void addingOneHundredTasks_reachesCapacityWithoutOverflow() {
        TaskList taskList = new TaskList(List.of());

        for (int i = 0; i < 100; i++) {
            taskList.add(new Todo("task " + i));
        }

        assertEquals(100, taskList.size());
        assertFalse(taskList.canAdd());
    }
}
