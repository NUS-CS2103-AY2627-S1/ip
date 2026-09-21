package pdd.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pdd.storage.Storage;
import pdd.task.TaskList;
import pdd.ui.Ui;

public class ExitCommandTest {
    @TempDir
    Path tempDir;

    @Test
    public void isExit_returnsTrue() {
        assertTrue(new ExitCommand().isExit());
    }

    @Test
    public void execute_showsGoodbyeMessage() {
        List<String> lines = new ArrayList<>();
        Ui ui = new Ui(lines::add);
        TaskList tasks = new TaskList(new ArrayList<>());
        Storage storage = new Storage(tempDir.resolve("pdd.txt").toString());

        new ExitCommand().execute(tasks, ui, storage);

        assertEquals("Bye. Hope to see you again soon!", String.join("\n", lines));
    }
}
