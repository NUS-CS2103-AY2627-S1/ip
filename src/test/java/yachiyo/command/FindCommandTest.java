package yachiyo.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import yachiyo.storage.Storage;
import yachiyo.task.NumberedTask;
import yachiyo.task.Task;
import yachiyo.task.TaskList;
import yachiyo.task.ToDo;
import yachiyo.ui.Ui;

/**
 * Tests keyword-filtered display behavior of {@link FindCommand}.
 */
public class FindCommandTest {
    @Test
    public void execute_noMatchingTasks_noMatchingTasksMessageShown() {
        Task task = new ToDo("Submit report");
        TaskList tasks = new TaskList(List.of(task));
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();

        new FindCommand("book").execute(tasks, ui, storage);

        assertEquals("book", ui.noMatchKeyword);
        assertNull(ui.headerKeyword);
        assertEquals(List.of(), ui.shownTasks);
        assertEquals(List.of(task), tasks.getTasks());
        assertEquals(0, storage.saveCallCount);
    }

    @Test
    public void execute_matchingTasks_headerAndOriginalTaskNumbersShown() {
        Task firstMatchingTask = new ToDo("Read book");
        Task nonMatchingTask = new ToDo("Submit report");
        Task secondMatchingTask = new ToDo("Return BOOK");
        TaskList tasks = new TaskList(List.of(
                firstMatchingTask,
                nonMatchingTask,
                secondMatchingTask
        ));
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();

        new FindCommand("book").execute(tasks, ui, storage);

        assertEquals("book", ui.headerKeyword);
        assertNull(ui.noMatchKeyword);
        assertEquals(
                List.of(
                        new NumberedTask(1, firstMatchingTask),
                        new NumberedTask(3, secondMatchingTask)
                ),
                ui.shownTasks
        );
        assertEquals(
                List.of(firstMatchingTask, nonMatchingTask, secondMatchingTask),
                tasks.getTasks()
        );
        assertEquals(0, storage.saveCallCount);
    }

    /**
     * UI test double that records keyword-search output calls.
     */
    private static final class RecordingUi extends Ui {
        private String headerKeyword;
        private String noMatchKeyword;
        private final List<NumberedTask> shownTasks = new ArrayList<>();

        @Override
        public void showMatchingTasksHeader(String keyword) {
            headerKeyword = keyword;
        }

        @Override
        public void showNoMatchingTasks(String keyword) {
            noMatchKeyword = keyword;
        }

        @Override
        public void showIndexedTask(int taskNumber, Task task) {
            shownTasks.add(new NumberedTask(taskNumber, task));
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
