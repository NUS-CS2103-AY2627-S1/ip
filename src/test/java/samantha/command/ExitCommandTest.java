package samantha.command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import samantha.model.TaskList;
import samantha.storage.Storage;
import samantha.ui.Ui;

class ExitCommandTest {
    @Test
    void exitCommand_isExitTrueAndExecutionDoesNotChangeTasks() throws Exception {
        TaskList tasks = new TaskList();
        ExitCommand command = new ExitCommand();

        command.execute(tasks, new Ui(), new Storage());

        assertTrue(command.isExit());
        assertTrue(tasks.asList().isEmpty());
    }
}
