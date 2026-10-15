package greenchonk.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import greenchonk.command.CommandTestSupport.RecordingStorage;
import greenchonk.command.CommandTestSupport.RecordingUi;
import greenchonk.task.TaskList;

class ListCommandTest {
    @Test
    void execute_anyTaskList_sameTaskListShownWithoutSaving() {
        TaskList tasks = new TaskList();
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();
        ListCommand command = new ListCommand();

        command.execute(tasks, ui, storage);

        assertSame(tasks, ui.getListedTasks());
        assertFalse(command.isExit());
        assertEquals(0, storage.getSaveCount());
    }
}
