package yachiyo.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static yachiyo.TestAssertions.assertYachiyoException;
import static yachiyo.exception.ErrorCategory.INVALID_OPERATION;
import static yachiyo.exception.ErrorCategory.SYSTEM_ERROR;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import yachiyo.exception.YachiyoException;
import yachiyo.storage.Storage;
import yachiyo.task.Task;
import yachiyo.task.TaskList;
import yachiyo.task.ToDo;
import yachiyo.ui.Ui;

/**
 * Tests task completion, persistence, and confirmation by {@link MarkCommand}.
 */
public class MarkCommandTest {
    @Test
    public void execute_incompleteTask_taskMarkedSavedAndShown() throws YachiyoException {
        Task targetTask = new ToDo("Read book");
        Task otherTask = new ToDo("Return book");
        TaskList tasks = new TaskList(List.of(targetTask, otherTask));
        RecordingStorage storage = new RecordingStorage(false);
        RecordingUi ui = new RecordingUi();

        new MarkCommand(1).execute(tasks, ui, storage);

        assertTrue(targetTask.isCompleted());
        assertEquals(
                tasks.getTasks().stream().map(Task::toFileFormat).toList(),
                storage.savedTasks.stream().map(Task::toFileFormat).toList()
        );
        assertSame(targetTask, ui.markedTask);
        assertEquals(1, ui.remainingCount);
    }

    @Test
    public void execute_emptyList_exceptionThrownWithoutSavingOrShowing() {
        TaskList tasks = new TaskList();
        RecordingStorage storage = new RecordingStorage(false);
        RecordingUi ui = new RecordingUi();

        assertYachiyoException(
                INVALID_OPERATION, () -> new MarkCommand(1).execute(tasks, ui, storage));
        assertEquals(0, storage.saveCallCount);
        assertEquals(0, ui.totalCallCount());
    }

    @Test
    public void execute_taskNumberOutsideList_exceptionThrownWithoutChanges() {
        Task task = new ToDo("Read book");
        TaskList tasks = new TaskList(List.of(task));
        RecordingStorage storage = new RecordingStorage(false);
        RecordingUi ui = new RecordingUi();

        assertYachiyoException(
                INVALID_OPERATION, () -> new MarkCommand(2).execute(tasks, ui, storage));
        assertFalse(task.isCompleted());
        assertEquals(0, storage.saveCallCount);
        assertEquals(0, ui.totalCallCount());
    }

    @Test
    public void execute_alreadyCompletedTask_alreadyMarkedShownWithoutSaving()
            throws YachiyoException {
        Task task = new ToDo("Read book");
        task.markAsDone();
        RecordingStorage storage = new RecordingStorage(false);
        RecordingUi ui = new RecordingUi();

        new MarkCommand(1).execute(new TaskList(List.of(task)), ui, storage);

        assertSame(task, ui.alreadyMarkedTask);
        assertEquals(1, ui.showAlreadyMarkedCallCount);
        assertEquals(0, ui.showMarkedCallCount);
        assertEquals(0, storage.saveCallCount);
    }

    @Test
    public void execute_storageFails_taskUnchangedAndConfirmationNotShown() {
        Task task = new ToDo("Read book");
        TaskList tasks = new TaskList(List.of(task));
        RecordingStorage storage = new RecordingStorage(true);
        RecordingUi ui = new RecordingUi();

        assertYachiyoException(
                SYSTEM_ERROR, () -> new MarkCommand(1).execute(tasks, ui, storage));
        assertFalse(task.isCompleted());
        assertEquals(0, ui.showMarkedCallCount);
    }

    /**
     * Storage test double that records saves or simulates a save failure.
     */
    private static final class RecordingStorage extends Storage {
        private final boolean shouldFail;
        private List<Task> savedTasks;
        private int saveCallCount;

        private RecordingStorage(boolean shouldFail) {
            super(Path.of("unused"));
            this.shouldFail = shouldFail;
        }

        @Override
        public void saveTasks(List<Task> tasks) throws YachiyoException {
            saveCallCount++;
            if (shouldFail) {
                throw new YachiyoException(SYSTEM_ERROR, "Simulated save failure");
            }
            savedTasks = List.copyOf(tasks);
        }
    }

    /**
     * UI test double that records marked and already-marked messages.
     */
    private static final class RecordingUi extends Ui {
        private Task markedTask;
        private Task alreadyMarkedTask;
        private int remainingCount;
        private int showMarkedCallCount;
        private int showAlreadyMarkedCallCount;

        @Override
        public void showTaskMarked(Task task, int remainingCount) {
            markedTask = task;
            this.remainingCount = remainingCount;
            showMarkedCallCount++;
        }

        @Override
        public void showAlreadyMarked(Task task) {
            alreadyMarkedTask = task;
            showAlreadyMarkedCallCount++;
        }

        private int totalCallCount() {
            return showMarkedCallCount + showAlreadyMarkedCallCount;
        }
    }
}
