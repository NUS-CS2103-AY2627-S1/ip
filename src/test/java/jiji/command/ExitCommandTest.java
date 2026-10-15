package jiji.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jiji.storage.Storage;
import jiji.task.TaskList;
import jiji.ui.Ui;

/**
 * Unit tests for {@link ExitCommand}.
 */
public class ExitCommandTest {

    @TempDir
    public Path tempDir;

    @Test
    public void execute_returnsGoodbyeMessage() {
        ExitCommand command = new ExitCommand();
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage(tempDir.resolve("test.txt").toString());

        String result = command.execute(tasks, ui, storage);
        assertEquals(ui.formatGoodbye(), result);
        assertTrue(command.isExit());
    }

    @Test
    public void defaultCommand_isExit_returnsFalse() {
        Command dummyCommand = new Command() {
            @Override
            public String execute(TaskList tasks, Ui ui, Storage storage) {
                return "";
            }
        };
        assertFalse(dummyCommand.isExit());
    }
}
