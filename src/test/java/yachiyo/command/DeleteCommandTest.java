package yachiyo.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
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
 * Tests task deletion, persistence, and confirmation by {@link DeleteCommand}.
 */
public class DeleteCommandTest {
    @Test
    public void execute_validTaskNumber_taskDeletedSavedAndShown() throws YachiyoException {
        Task remainingTask = new ToDo("Read book");
        Task deletedTask = new ToDo("Return book");
        TaskList tasks = new TaskList(List.of(remainingTask, deletedTask));
        RecordingStorage storage = new RecordingStorage(false);
        RecordingUi ui = new RecordingUi();

        new DeleteCommand(2).execute(tasks, ui, storage);

        assertEquals(List.of(remainingTask), tasks.getTasks());
        assertEquals(List.of(remainingTask), storage.savedTasks);
        assertSame(deletedTask, ui.deletedTask);
        assertEquals(1, ui.totalCount);
    }

    @Test
    public void execute_emptyList_exceptionThrownWithoutSavingOrShowing() {
        TaskList tasks = new TaskList();
        RecordingStorage storage = new RecordingStorage(false);
        RecordingUi ui = new RecordingUi();

        assertYachiyoException(
                INVALID_OPERATION, () -> new DeleteCommand(1).execute(tasks, ui, storage));
        assertEquals(0, storage.saveCallCount);
        assertEquals(0, ui.showDeletedCallCount);
    }

    @Test
    public void execute_taskNumberOutsideList_exceptionThrownWithoutChanges() {
        Task task = new ToDo("Read book");
        TaskList tasks = new TaskList(List.of(task));
        RecordingStorage storage = new RecordingStorage(false);
        RecordingUi ui = new RecordingUi();

        assertYachiyoException(
                INVALID_OPERATION, () -> new DeleteCommand(2).execute(tasks, ui, storage));
        assertEquals(List.of(task), tasks.getTasks());
        assertEquals(0, storage.saveCallCount);
        assertEquals(0, ui.showDeletedCallCount);
    }

    @Test
    public void execute_storageFails_taskListUnchangedAndConfirmationNotShown() {
        Task task = new ToDo("Read book");
        TaskList tasks = new TaskList(List.of(task));
        RecordingStorage storage = new RecordingStorage(true);
        RecordingUi ui = new RecordingUi();

        assertYachiyoException(
                SYSTEM_ERROR, () -> new DeleteCommand(1).execute(tasks, ui, storage));
        assertEquals(List.of(task), tasks.getTasks());
        assertEquals(0, ui.showDeletedCallCount);
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
     * UI test double that records task-deleted confirmations.
     */
    private static final class RecordingUi extends Ui {
        private Task deletedTask;
        private int totalCount;
        private int showDeletedCallCount;

        @Override
        public void showTaskDeleted(Task task, int totalCount) {
            deletedTask = task;
            this.totalCount = totalCount;
            showDeletedCallCount++;
        }
    }
}
