package greenchonk.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import greenchonk.command.CommandTestSupport.RecordingStorage;
import greenchonk.command.CommandTestSupport.RecordingUi;
import greenchonk.task.TaskList;

class ExitCommandTest {
    @Test
    void execute_exitCommand_goodbyeShownWithoutSaving() {
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();
        ExitCommand command = new ExitCommand();

        command.execute(new TaskList(), ui, storage);

        assertTrue(ui.isGoodbyeShown());
        assertTrue(command.isExit());
        assertEquals(0, storage.getSaveCount());
    }
}
