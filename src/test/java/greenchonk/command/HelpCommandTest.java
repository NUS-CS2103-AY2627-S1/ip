package greenchonk.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import greenchonk.command.CommandTestSupport.RecordingStorage;
import greenchonk.command.CommandTestSupport.RecordingUi;
import greenchonk.task.TaskList;

class HelpCommandTest {
    @Test
    void execute_anyTaskList_helpShownWithoutSaving() {
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();
        HelpCommand command = new HelpCommand();

        command.execute(new TaskList(), ui, storage);

        assertTrue(ui.isHelpShown());
        assertFalse(command.isExit());
        assertEquals(0, storage.getSaveCount());
    }
}
