package dingleberry.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dingleberry.model.TaskList;
import dingleberry.model.Todo;
import dingleberry.persistence.Storage;
import dingleberry.ui.Ui;

/** Tests searching tasks by keyword. */
class FindCommandTest {
    /** Redirects storage writes to a temporary folder during tests. */
    @TempDir
    private Path tempDir;

    @Test
    void executeMatchingKeywordShowsCaseInsensitiveMatches() throws Exception {
        TaskList tasks = new TaskList(new Todo("Read lecture notes"),
                new Todo("submit assignment"));
        RecordingUi ui = new RecordingUi();
        Storage storage = new Storage(tempDir.resolve("dingleberry.txt")
                .toString());

        new FindCommand("LECTURE").execute(tasks, ui, storage);

        assertEquals(1, ui.shownTasks.size());
        assertEquals("Read lecture notes",
            ui.shownTasks.get(0).getDescription());
        assertEquals(2, tasks.size());
    }

    @Test
    void executeNonMatchingKeywordShowsEmptyList() throws Exception {
        TaskList tasks = new TaskList(new Todo("Read lecture notes"));
        RecordingUi ui = new RecordingUi();
        Storage storage = new Storage(tempDir.resolve("dingleberry.txt")
                .toString());

        new FindCommand("holiday").execute(tasks, ui, storage);

        assertEquals(0, ui.shownTasks.size());
        assertEquals(1, tasks.size());
    }

    /** Captures the task list passed to the UI without printing it. */
    private static final class RecordingUi extends Ui {
        /** Stores the task list most recently shown by the UI. */
        private TaskList shownTasks;

        @Override
        public void showTaskList(final TaskList tasks) {
            shownTasks = tasks;
        }
    }
}
