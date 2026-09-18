package myriad.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import myriad.storage.Storage;
import myriad.task.TaskList;
import myriad.task.ToDo;
import myriad.ui.Ui;

/**
 * Tests for {@link ExitCommand}: it is the one command that ends the
 * session, and it does so only through {@link ExitCommand#isExit()}. The
 * farewell is shown by whoever runs the command (see MyriadTest), so
 * executing it must neither say anything nor change anything.
 */
public class ExitCommandTest {

    /** JUnit creates this per test and deletes it afterwards. */
    @TempDir
    private Path tempDir;

    @Test
    public void isExit_exitCommand_true() {
        assertTrue(new ExitCommand().isExit());
    }

    @Test
    public void execute_anyList_nothingShownChangedOrSaved() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        Ui ui = new Ui(false);

        new ExitCommand().execute(tasks, ui, new Storage(tempDir.resolve("myriad.txt").toString()));

        assertEquals("", ui.getResponse());
        assertEquals(1, tasks.size());
        assertFalse(Files.exists(tempDir.resolve("myriad.txt")));
    }
}
