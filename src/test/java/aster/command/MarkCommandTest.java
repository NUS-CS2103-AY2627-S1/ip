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
 * Tests what {@code mark} reports, and what it leaves behind, when the task it names is
 * already done.
 *
 * <p>Marking a task that is already done asks for a state the task is already in, so it
 * is answered rather than refused: the reply says so and nothing changes. The reply is
 * compared in full, and the saved file is checked for absence rather than for content,
 * because a command that never saves cannot have created it.
 *
 * <p>The first case is the ordinary one, so that a change which silenced every reply, or
 * which stopped saving altogether, could not pass by leaving only the already-done case
 * working.
 */
class MarkCommandTest {
    @TempDir
    Path tempDir;

    @Test
    void execute_taskNotDone_marksItAndSaves() throws AsterException {
        TaskList tasks = taskList(false);
        List<String> lines = new ArrayList<>();

        new MarkCommand("1").execute(tasks, new Ui(lines::add), storage());

        assertEquals(List.of("Nice! I've marked this task as done:",
                "  [T][X] read book"), lines);
        assertTrue(tasks.get(0).isDone());
        assertTrue(Files.exists(dataFile()), "marking a task must save the list");
    }

    @Test
    void execute_taskAlreadyDone_reportsItAndSavesNothing() throws AsterException {
        TaskList tasks = taskList(true);
        List<String> lines = new ArrayList<>();

        new MarkCommand("1").execute(tasks, new Ui(lines::add), storage());

        assertEquals(List.of("This task is already marked as done:",
                "  [T][X] read book"), lines);
        assertTrue(tasks.get(0).isDone(), "the task must be left as it was");
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
