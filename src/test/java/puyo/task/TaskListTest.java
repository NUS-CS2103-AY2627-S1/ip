package puyo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@code TaskList}.
 */
public class TaskListTest {

    private TaskList taskList;
    private Task sampleTask;

    /**
     * Dummy implementation of {@code Task} for testing purposes.
     */
    private static class DummyTask extends Task {
        public DummyTask(String name) {
            super(name, TaskType.TODO);
        }
    }

    @BeforeEach
    public void setUp() {
        taskList = new TaskList();
        sampleTask = new DummyTask("Read Book");
    }

    @Test
    public void constructor_emptyConstructor_initializesEmptyList() {
        TaskList newTaskList = new TaskList();
        assertTrue(newTaskList.isEmpty());
        assertEquals(0, newTaskList.size());
    }

    @Test
    public void constructor_withExistingList_initializesCorrectly() {
        ArrayList<Task> initialList = new ArrayList<>();
        initialList.add(sampleTask);

        TaskList newTaskList = new TaskList(initialList);
        assertEquals(1, newTaskList.size());
        assertEquals(sampleTask, newTaskList.get(0));
    }

    @Test
    public void add_validTask_addsToList() {
        assertTrue(taskList.isEmpty());
        taskList.add(sampleTask);

        assertFalse(taskList.isEmpty());
        assertEquals(1, taskList.size());
        assertEquals(sampleTask, taskList.get(0));
    }

    @Test
    public void remove_validIndex_removesAndReturnsTask() {
        taskList.add(sampleTask);
        Task removedTask = taskList.remove(0);

        assertEquals(sampleTask, removedTask);
        assertTrue(taskList.isEmpty());
        assertEquals(0, taskList.size());
    }

    @Test
    public void remove_invalidIndex_throwsIndexOutOfBoundsException() {
        taskList.add(sampleTask);
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.remove(5));
    }

    @Test
    public void getTasks_returnsUnderlyingArrayList() {
        taskList.add(sampleTask);
        ArrayList<Task> internalList = taskList.getTasks();

        assertEquals(1, internalList.size());
        assertEquals(sampleTask, internalList.get(0));
    }
}
