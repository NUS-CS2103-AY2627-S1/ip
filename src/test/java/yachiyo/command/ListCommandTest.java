package yachiyo.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import yachiyo.storage.Storage;
import yachiyo.task.Task;
import yachiyo.task.TaskList;
import yachiyo.task.ToDo;
import yachiyo.ui.Ui;

/**
 * Tests task-list display behavior of {@link ListCommand}.
 */
public class ListCommandTest {
    @Test
    public void execute_emptyList_emptyListPassedToUiWithoutSaving() {
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();

        new ListCommand().execute(new TaskList(), ui, storage);

        assertEquals(List.of(), ui.shownTasks);
        assertEquals(1, ui.showListCallCount);
        assertEquals(0, storage.saveCallCount);
    }

    @Test
    public void execute_tasksPresent_orderedTasksPassedToUiWithoutSaving() {
        Task firstTask = new ToDo("Read book");
        Task secondTask = new ToDo("Return book");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();

        new ListCommand().execute(tasks, ui, storage);

        assertEquals(List.of(firstTask, secondTask), ui.shownTasks);
        assertEquals(1, ui.showListCallCount);
        assertEquals(0, storage.saveCallCount);
    }

    @Test
    public void isExit_listCommand_falseReturned() {
        assertFalse(new ListCommand().isExit());
    }

    /**
     * UI test double that records task-list display calls.
     */
    private static final class RecordingUi extends Ui {
        private List<Task> shownTasks;
        private int showListCallCount;

        @Override
        public void showTaskList(List<Task> tasks) {
            shownTasks = List.copyOf(tasks);
            showListCallCount++;
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
