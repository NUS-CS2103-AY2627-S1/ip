package dennis.command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dennis.gui.DialogUi;
import dennis.storage.Storage;
import dennis.task.TaskList;

/**
 * Tests for {@link ExitCommand#execute}: that it shows the farewell message
 * and that {@link ExitCommand#isExit()} reports {@code true}, the signal
 * {@code Dennis}'s main loop and {@code MainWindow} both rely on to stop.
 */
public class ExitCommandTest {

    @TempDir
    private Path tempDir;

    @Test
    public void execute_showsGoodbyeMessage() {
        DialogUi ui = new DialogUi();

        new ExitCommand().execute(new TaskList(), ui, new Storage(tempDir.resolve("dennis.txt")));

        assertTrue(ui.drain().contains("Time for a cat-nap. Bye!"));
    }

    @Test
    public void isExit_returnsTrue() {
        assertTrue(new ExitCommand().isExit());
    }
}
