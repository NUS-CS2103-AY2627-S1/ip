package alzara.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import alzara.storage.Storage;
import alzara.task.TaskList;
import alzara.ui.Ui;

/**
 * Tests for {@link ExitCommand}. Read-only, so {@link Storage} is only
 * passed through (never used) - each test still supplies one pointed at a
 * fresh JUnit {@code @TempDir} to match {@link Command#execute}'s signature.
 */
class ExitCommandTest {
    // execute() should show the goodbye message and leave the task list untouched.
    @Test
    void execute_showsGoodbyeMessageAndLeavesMemoryUntouched(@TempDir File dataDir) {
        TaskList memory = new TaskList(new ArrayList<>());
        Ui ui = new Ui(true);

        new ExitCommand().execute(memory, ui, new Storage(dataDir));

        assertEquals("Our audience has ended. Until we meet again.", ui.getAndClearResponse());
        assertEquals(0, memory.size());
    }

    // isExit() should always return true, signalling the program to stop.
    @Test
    void isExit_alwaysReturnsTrue() {
        assertTrue(new ExitCommand().isExit());
    }
}
