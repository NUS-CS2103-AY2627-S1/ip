package yachiyo.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import yachiyo.storage.Storage;
import yachiyo.task.Task;
import yachiyo.task.TaskList;
import yachiyo.task.ToDo;
import yachiyo.ui.Ui;

/**
 * Tests farewell and application-exit behavior of {@link ExitCommand}.
 */
public class ExitCommandTest {
    @Test
    public void execute_tasksPresent_exitShownWithoutChangingOrSavingTasks() {
        Task task = new ToDo("Read book");
        TaskList tasks = new TaskList(List.of(task));
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();

        new ExitCommand().execute(tasks, ui, storage);

        assertEquals(1, ui.showExitCallCount);
        assertEquals(List.of(task), tasks.getTasks());
        assertEquals(0, storage.saveCallCount);
    }

    @Test
    public void isExit_exitCommand_trueReturned() {
        assertTrue(new ExitCommand().isExit());
    }

    /**
     * UI test double that records farewell display calls.
     */
    private static final class RecordingUi extends Ui {
        private int showExitCallCount;

        @Override
        public void showExit() {
            showExitCallCount++;
        }
    }

    /**
     * Storage test double that records unexpected save attempts.
     */
    private static final class RecordingStorage extends Storage {
        private int saveCallCount;

        private RecordingStorage() {
            super(Path.of("unused"));
        }

        @Override
        public void saveTasks(List<Task> tasks) {
            saveCallCount++;
        }
    }
}
