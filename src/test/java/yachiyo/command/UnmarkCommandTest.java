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
 * Tests task unmarking, persistence, and confirmation by {@link UnmarkCommand}.
 */
public class UnmarkCommandTest {
    @Test
    public void execute_completedTask_taskUnmarkedSavedAndShown() throws YachiyoException {
        Task targetTask = new ToDo("Read book");
        targetTask.markAsDone();
        Task otherTask = new ToDo("Return book");
        TaskList tasks = new TaskList(List.of(targetTask, otherTask));
        RecordingStorage storage = new RecordingStorage(false);
        RecordingUi ui = new RecordingUi();

        new UnmarkCommand(1).execute(tasks, ui, storage);

        assertFalse(targetTask.isCompleted());
        assertEquals(
                tasks.getTasks().stream().map(Task::toFileFormat).toList(),
                storage.savedTasks.stream().map(Task::toFileFormat).toList()
        );
        assertSame(targetTask, ui.unmarkedTask);
        assertEquals(2, ui.remainingCount);
    }

    @Test
    public void execute_emptyList_exceptionThrownWithoutSavingOrShowing() {
        TaskList tasks = new TaskList();
        RecordingStorage storage = new RecordingStorage(false);
        RecordingUi ui = new RecordingUi();

        assertYachiyoException(
                INVALID_OPERATION, () -> new UnmarkCommand(1).execute(tasks, ui, storage));
        assertEquals(0, storage.saveCallCount);
        assertEquals(0, ui.totalCallCount());
    }

    @Test
    public void execute_taskNumberOutsideList_exceptionThrownWithoutChanges() {
        Task task = new ToDo("Read book");
        task.markAsDone();
        TaskList tasks = new TaskList(List.of(task));
        RecordingStorage storage = new RecordingStorage(false);
        RecordingUi ui = new RecordingUi();

        assertYachiyoException(
                INVALID_OPERATION, () -> new UnmarkCommand(2).execute(tasks, ui, storage));
        assertTrue(task.isCompleted());
        assertEquals(0, storage.saveCallCount);
        assertEquals(0, ui.totalCallCount());
    }

    @Test
    public void execute_alreadyIncompleteTask_alreadyUnmarkedShownWithoutSaving()
            throws YachiyoException {
        Task task = new ToDo("Read book");
        RecordingStorage storage = new RecordingStorage(false);
        RecordingUi ui = new RecordingUi();

        new UnmarkCommand(1).execute(new TaskList(List.of(task)), ui, storage);

        assertSame(task, ui.alreadyUnmarkedTask);
        assertEquals(1, ui.showAlreadyUnmarkedCallCount);
        assertEquals(0, ui.showUnmarkedCallCount);
        assertEquals(0, storage.saveCallCount);
    }

    @Test
    public void execute_storageFails_taskUnchangedAndConfirmationNotShown() {
        Task task = new ToDo("Read book");
        task.markAsDone();
        TaskList tasks = new TaskList(List.of(task));
        RecordingStorage storage = new RecordingStorage(true);
        RecordingUi ui = new RecordingUi();

        assertYachiyoException(
                SYSTEM_ERROR, () -> new UnmarkCommand(1).execute(tasks, ui, storage));
        assertTrue(task.isCompleted());
        assertEquals(0, ui.showUnmarkedCallCount);
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
     * UI test double that records unmarked and already-unmarked messages.
     */
    private static final class RecordingUi extends Ui {
        private Task unmarkedTask;
        private Task alreadyUnmarkedTask;
        private int remainingCount;
        private int showUnmarkedCallCount;
        private int showAlreadyUnmarkedCallCount;

        @Override
        public void showTaskUnmarked(Task task, int remainingCount) {
            unmarkedTask = task;
            this.remainingCount = remainingCount;
            showUnmarkedCallCount++;
        }

        @Override
        public void showAlreadyUnmarked(Task task) {
            alreadyUnmarkedTask = task;
            showAlreadyUnmarkedCallCount++;
        }

        private int totalCallCount() {
            return showUnmarkedCallCount + showAlreadyUnmarkedCallCount;
        }
    }
}
