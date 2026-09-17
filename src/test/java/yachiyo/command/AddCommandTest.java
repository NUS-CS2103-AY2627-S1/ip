package yachiyo.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static yachiyo.TestAssertions.assertYachiyoException;
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
 * Tests task addition, persistence, and confirmation by {@link AddCommand}.
 */
public class AddCommandTest {
    @Test
    public void execute_emptyList_taskAddedSavedAndShown() throws YachiyoException {
        Task task = new ToDo("Read book");
        TaskList tasks = new TaskList();
        RecordingStorage storage = new RecordingStorage(false);
        RecordingUi ui = new RecordingUi();

        new AddCommand(task).execute(tasks, ui, storage);

        assertEquals(List.of(task), tasks.getTasks());
        assertEquals(List.of(task), storage.savedTasks);
        assertSame(task, ui.addedTask);
        assertEquals(1, ui.totalCount);
    }

    @Test
    public void execute_nonEmptyList_taskAddedToEnd() throws YachiyoException {
        Task firstTask = new ToDo("Read book");
        Task addedTask = new ToDo("Return book");
        TaskList tasks = new TaskList(List.of(firstTask));
        RecordingStorage storage = new RecordingStorage(false);
        RecordingUi ui = new RecordingUi();

        new AddCommand(addedTask).execute(tasks, ui, storage);

        assertEquals(List.of(firstTask, addedTask), tasks.getTasks());
        assertEquals(List.of(firstTask, addedTask), storage.savedTasks);
        assertEquals(2, ui.totalCount);
    }

    @Test
    public void execute_storageFails_taskListUnchangedAndConfirmationNotShown() {
        Task task = new ToDo("Read book");
        TaskList tasks = new TaskList();
        RecordingStorage storage = new RecordingStorage(true);
        RecordingUi ui = new RecordingUi();

        assertYachiyoException(
                SYSTEM_ERROR, () -> new AddCommand(task).execute(tasks, ui, storage));
        assertTrue(tasks.isEmpty());
        assertEquals(0, ui.showAddedCallCount);
    }

    /**
     * Storage test double that records saved tasks or simulates a save failure.
     */
    private static final class RecordingStorage extends Storage {
        private final boolean shouldFail;
        private List<Task> savedTasks;

        private RecordingStorage(boolean shouldFail) {
            super(Path.of("unused"));
            this.shouldFail = shouldFail;
        }

        @Override
        public void saveTasks(List<Task> tasks) throws YachiyoException {
            if (shouldFail) {
                throw new YachiyoException(SYSTEM_ERROR, "Simulated save failure");
            }
            savedTasks = List.copyOf(tasks);
        }
    }

    /**
     * UI test double that records task-added confirmations.
     */
    private static final class RecordingUi extends Ui {
        private Task addedTask;
        private int totalCount;
        private int showAddedCallCount;

        @Override
        public void showTaskAdded(Task task, int totalCount) {
            this.addedTask = task;
            this.totalCount = totalCount;
            showAddedCallCount++;
        }
    }
}
