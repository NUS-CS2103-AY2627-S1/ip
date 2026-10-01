package bany;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bany.tasks.Task;
import bany.tasks.ToDo;

/** Tests the task-marking behavior provided by {@link TaskStorage}. */
class TaskStorageTest {
    private TaskStorage taskStorage;
    private Task firstTask;
    private Task secondTask;

    /** Creates a fresh task list before each test so tests remain independent. */
    @BeforeEach
    void setUp() {
        taskStorage = new TaskStorage();
        firstTask = new ToDo("Read book", 1);
        secondTask = new ToDo("Submit assignment", 2);
        taskStorage.addTask(firstTask);
        taskStorage.addTask(secondTask);
    }

    @Test
    void markTask_validIndex_marksAndReturnsTask() {
        Task markedTask = taskStorage.markTask(0);

        assertAll(() -> assertSame(
                firstTask, markedTask), () -> assertTrue(
                        firstTask.isDone()), () -> assertFalse(
                secondTask.isDone()));
    }

    @Test
    void markTask_lastValidIndex_marksLastTask() {
        Task markedTask = taskStorage.markTask(taskStorage.getSize() - 1);

        assertAll(() -> assertSame(
                secondTask, markedTask), () -> assertTrue(
                secondTask.isDone()), () -> assertFalse(
                        firstTask.isDone()));
    }

    @Test
    void markTask_alreadyMarkedTask_remainsMarked() {
        firstTask.mark();

        Task markedTask = taskStorage.markTask(0);

        assertAll(() -> assertSame(
                firstTask, markedTask), () -> assertTrue(
                        firstTask.isDone()), () -> assertEquals(
                                2, taskStorage.getSize()));
    }

    @Test
    void markTask_invalidIndex_returnsNullWithoutChangingTasks() {
        TaskStorage emptyStorage = new TaskStorage();

        assertAll(() -> assertNull(emptyStorage.markTask(0)), () -> assertNull(
                        taskStorage.markTask(-1)), () -> assertNull(
                        taskStorage.markTask(taskStorage.getSize())), () -> assertFalse(
                        firstTask.isDone()), () -> assertFalse(secondTask.isDone()), () -> assertEquals(
                        2, taskStorage.getSize()));
    }
}
