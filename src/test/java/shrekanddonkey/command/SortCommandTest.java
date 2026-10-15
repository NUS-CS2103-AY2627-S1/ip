package shrekanddonkey.command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import shrekanddonkey.storage.Storage;
import shrekanddonkey.task.Deadline;
import shrekanddonkey.task.TaskList;
import shrekanddonkey.task.Todo;
import shrekanddonkey.ui.Ui;

/**
 * Tests executing {@link SortCommand}.
 */
public class SortCommandTest {
    @Test
    public void execute_tasksWithDates_displaysSortedTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("submit report", LocalDateTime.of(2026, 10, 15, 18, 0)));
        RecordingUi ui = new RecordingUi();

        new SortCommand().execute(tasks, ui, new Storage("./data/test.txt"));

        String output = ui.getRecordedOutput();
        assertTrue(output.indexOf("1.[D][ ] submit report") < output.indexOf("2.[T][ ] read book"));
        assertTrue(ui.wasDividerShown);
    }

    private static class RecordingUi extends Ui {
        private boolean wasDividerShown;

        @Override
        public void showDivider() {
            wasDividerShown = true;
        }
    }
}
