package ev.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ev.EvException;
import ev.storage.Storage;
import ev.task.TaskList;
import ev.task.Todo;
import ev.ui.Ui;

public class ExitCommandTest {

    @TempDir
    Path workFolder;

    @Test
    public void isExit_always_true() {
        assertTrue(new ExitCommand().isExit());
    }

    @Test
    public void execute_saysNothingAndChangesNothing() throws EvException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Ui ui = new Ui();
        Path file = workFolder.resolve("ev.txt");

        new ExitCommand().execute(tasks, ui, new Storage(file));

        assertEquals("", ui.takeResponse());
        assertEquals(1, tasks.size());
        assertTrue(Files.notExists(file));
    }
}
