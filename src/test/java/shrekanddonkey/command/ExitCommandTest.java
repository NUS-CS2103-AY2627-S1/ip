package shrekanddonkey.command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import shrekanddonkey.storage.Storage;
import shrekanddonkey.task.TaskList;
import shrekanddonkey.ui.Ui;

/**
 * Tests executing {@link ExitCommand}.
 */
public class ExitCommandTest {

    @Test
    public void execute_exitCommand_showsGoodbyeMessageAndReturnsTrueForIsExit() {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage("./data/test.txt");

        ExitCommand command = new ExitCommand();
        command.execute(tasks, ui, storage);

        assertTrue(command.isExit());
        assertTrue(ui.getRecordedOutput().contains("GET OUT OF MY SWAMP!"));
    }
}
