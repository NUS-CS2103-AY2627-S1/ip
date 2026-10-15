package dingleberry.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dingleberry.model.TaskList;
import dingleberry.model.Todo;
import dingleberry.persistence.Storage;
import dingleberry.ui.Ui;

/** Tests the command contract for ending the application. */
class ExitCommandTest {
    /** Redirects storage writes to a temporary folder during tests. */
    @TempDir
    private Path tempDir;

    @Test
    void executeExitCommandLeavesTasksUnchanged() throws Exception {
        TaskList tasks = new TaskList(new Todo("read lecture notes"));
        Storage storage = new Storage(tempDir.resolve("dingleberry.txt")
                .toString());

        new ExitCommand().execute(tasks, new Ui(), storage);

        assertEquals(1, tasks.size());
        assertTrue(new ExitCommand().isExit());
    }
}
