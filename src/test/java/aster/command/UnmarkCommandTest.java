package aster.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import aster.exception.AsterException;
import aster.storage.Storage;
import aster.task.Task;
import aster.task.TaskList;
import aster.task.Todo;
import aster.ui.Ui;

/**
 * Tests what {@code unmark} reports, and what it leaves behind, when the task it names is
 * already not done.
 *
 * <p>This mirrors {@code MarkCommandTest}: the two commands answer the same way in the
 * same situation, and are tested apart so that a change to one cannot be hidden by the
 * other.
 */
class UnmarkCommandTest {
    @TempDir
    Path tempDir;

    @Test
    void execute_taskDone_unmarksItAndSaves() throws AsterException {
        TaskList tasks = taskList(true);
        List<String> lines = new ArrayList<>();

        new UnmarkCommand("1").execute(tasks, new Ui(lines::add), storage());

        assertEquals(List.of("Alright, I've marked this task as not done yet:",
                "  [T][ ] read book"), lines);
        assertFalse(tasks.get(0).isDone());
        assertTrue(Files.exists(dataFile()), "unmarking a task must save the list");
    }

    @Test
    void execute_taskAlreadyNotDone_reportsItAndSavesNothing() throws AsterException {
        TaskList tasks = taskList(false);
        List<String> lines = new ArrayList<>();

        new UnmarkCommand("1").execute(tasks, new Ui(lines::add), storage());

        assertEquals(List.of("This task is already marked as not done:",
                "  [T][ ] read book"), lines);
        assertFalse(tasks.get(0).isDone(), "the task must be left as it was");
        assertFalse(Files.exists(dataFile()),
                "a task already in the asked-for state must not be saved again");
    }

    // ---------- helpers ----------

    /**
     * Returns a list holding one todo, done or not as asked.
     *
     * @param isDone whether the task starts out done.
     * @return a task list holding that one task.
     */
    private static TaskList taskList(boolean isDone) {
        TaskList tasks = new TaskList();
        Task task = new Todo("read book");
        if (isDone) {
            task.markAsDone();
        }
        tasks.add(task);
        return tasks;
    }

    /**
     * Returns the path this test's tasks would be saved to, which no test creates itself.
     *
     * @return the data file inside this test's temporary directory.
     */
    private Path dataFile() {
        return tempDir.resolve("aster.txt");
    }

    /**
     * Returns a store pointed at this test's temporary directory.
     *
     * @return a store that can never touch the real {@code data/aster.txt}.
     */
    private Storage storage() {
        return new Storage(dataFile());
    }
}
