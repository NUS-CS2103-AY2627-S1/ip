package xiaozhi.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import xiaozhi.storage.Storage;
import xiaozhi.task.TaskList;
import xiaozhi.testutil.OutputCapture;
import xiaozhi.ui.Ui;

/**
 * Tests {@link ExitCommand}: it signals exit and otherwise does nothing
 * observable (XiaoZhi prints the farewell itself once the loop ends).
 */
public class ExitCommandTest {

    @TempDir
    Path tempDir;

    @Test
    public void isExit_returnsTrue() {
        assertTrue(new ExitCommand().isExit());
    }

    @Test
    public void isUndoable_returnsFalse() {
        assertFalse(new ExitCommand().isUndoable());
    }

    @Test
    public void execute_printsNothingAndLeavesTaskListUnchanged() throws Exception {
        TaskList tasks = new TaskList();
        ExitCommand command = new ExitCommand();

        String output = OutputCapture.capture(() -> command.execute(tasks, new Ui(),
                new Storage(tempDir.resolve("xiaozhi.txt").toString()), new CommandHistory()));

        assertEquals("", output);
        assertEquals(0, tasks.size());
    }
}
