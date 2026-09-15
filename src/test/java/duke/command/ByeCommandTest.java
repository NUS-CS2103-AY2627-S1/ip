package duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import duke.storage.Storage;
import duke.task.TaskList;
import duke.ui.CapturingUi;

public class ByeCommandTest {

    @TempDir
    Path tempDir;

    @Test
    public void isExit_returnsTrue() {
        assertTrue(new ByeCommand().isExit());
    }

    @Test
    public void execute_savesAndShowsGoodbye() throws Exception {
        Path file = tempDir.resolve("duke.txt");

        TaskList tasks = new TaskList();
        tasks.setStorage(new Storage(file.toString()));
        tasks.addTodo("read book");

        CapturingUi ui = new CapturingUi();

        new ByeCommand().execute(tasks, ui);

        assertTrue(ui.isGoodbyeShown());
        assertTrue(Files.exists(file));
        assertEquals("T | 0 | read book" + System.lineSeparator(), Files.readString(file));
    }
}
