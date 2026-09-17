package yachiyo.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static yachiyo.TestAssertions.assertYachiyoException;
import static yachiyo.exception.ErrorCategory.INVALID_OPERATION;
import static yachiyo.exception.ErrorCategory.SYSTEM_ERROR;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import yachiyo.exception.YachiyoException;
import yachiyo.storage.Storage;
import yachiyo.task.Deadline;
import yachiyo.task.Task;
import yachiyo.task.TaskList;
import yachiyo.task.ToDo;
import yachiyo.ui.Ui;

/**
 * Tests task description editing, persistence, and confirmation.
 */
public class EditDescriptionCommandTest {
    private static final LocalDateTime DUE_DATE_TIME =
            LocalDateTime.of(2026, 9, 20, 17, 0);

    @Test
    public void execute_completedDeadline_onlyDescriptionChangedAndSaved()
            throws YachiyoException {
        Deadline originalTask = new Deadline("Submit draft", DUE_DATE_TIME);
        originalTask.markAsDone();
        TaskList tasks = new TaskList(originalTask);
        RecordingStorage storage = new RecordingStorage(false);
        RecordingUi ui = new RecordingUi();

        new EditDescriptionCommand(1, "Submit final report").execute(tasks, ui, storage);

        Task editedTask = tasks.get(1);
        assertNotSame(originalTask, editedTask);
        assertEquals("Submit final report", editedTask.getDescription());
        assertEquals(DUE_DATE_TIME, ((Deadline) editedTask).getBy());
        assertTrue(editedTask.isCompleted());
        assertEquals(List.of(editedTask), storage.savedTasks);
        assertSame(editedTask, ui.editedTask);
    }

    @Test
    public void execute_emptyList_exceptionThrownWithoutSavingOrShowing() {
        RecordingStorage storage = new RecordingStorage(false);
        RecordingUi ui = new RecordingUi();

        assertYachiyoException(INVALID_OPERATION, () ->
                new EditDescriptionCommand(1, "Return book")
                        .execute(new TaskList(), ui, storage));
        assertEquals(0, storage.saveCallCount);
        assertEquals(0, ui.showEditedCallCount);
    }

    @Test
    public void execute_taskNumberOutsideList_exceptionThrownWithoutChanges() {
        Task originalTask = new ToDo("Read book");
        TaskList tasks = new TaskList(originalTask);
        RecordingStorage storage = new RecordingStorage(false);
        RecordingUi ui = new RecordingUi();

        assertYachiyoException(INVALID_OPERATION, () ->
                new EditDescriptionCommand(2, "Return book").execute(tasks, ui, storage));
        assertEquals(List.of(originalTask), tasks.getTasks());
        assertEquals(0, storage.saveCallCount);
        assertEquals(0, ui.showEditedCallCount);
    }

    @Test
    public void execute_storageFails_taskListUnchangedAndConfirmationNotShown() {
        Task originalTask = new ToDo("Read book");
        TaskList tasks = new TaskList(originalTask);
        RecordingStorage storage = new RecordingStorage(true);
        RecordingUi ui = new RecordingUi();

        assertYachiyoException(SYSTEM_ERROR, () ->
                new EditDescriptionCommand(1, "Return book").execute(tasks, ui, storage));
        assertEquals("Read book", tasks.getTasks().getFirst().getDescription());
        assertEquals(0, ui.showEditedCallCount);
    }

    /**
     * Storage test double that records saved tasks or simulates a save failure.
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
     * UI test double that records task-edited confirmations.
     */
    private static final class RecordingUi extends Ui {
        private Task editedTask;
        private int showEditedCallCount;

        @Override
        public void showTaskEdited(Task task) {
            editedTask = task;
            showEditedCallCount++;
        }
    }
}
