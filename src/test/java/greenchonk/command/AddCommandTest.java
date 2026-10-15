package greenchonk.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import greenchonk.command.CommandTestSupport.RecordingStorage;
import greenchonk.command.CommandTestSupport.RecordingUi;
import greenchonk.exception.GreenChonkException;
import greenchonk.task.Task;
import greenchonk.task.TaskList;
import greenchonk.task.Todo;

class AddCommandTest {
    @Test
    void execute_validTask_taskAddedSavedAndReported() throws GreenChonkException {
        Task task = new Todo("read book");
        TaskList tasks = new TaskList();
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();

        new AddCommand(task).execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(0));
        assertEquals(1, storage.getSaveCount());
        assertSame(task, ui.getAddedTask());
        assertEquals(1, ui.getTaskCount());
    }

    @Test
    void execute_saveFails_taskAdditionRolledBackAndNotReported() {
        TaskList tasks = new TaskList();
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();
        storage.failNextSave();

        GreenChonkException exception = assertThrows(GreenChonkException.class, () ->
                new AddCommand(new Todo("read book")).execute(tasks, ui, storage));

        assertEquals("save failed", exception.getMessage());
        assertTrue(tasks.isEmpty());
        assertEquals(1, storage.getSaveCount());
        assertNull(ui.getAddedTask());
    }

    @Test
    void execute_equivalentTask_exceptionThrownWithoutSaving() {
        Todo existingTask = new Todo("read book");
        existingTask.markAsDone();
        TaskList tasks = new TaskList(List.of(existingTask));
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();

        GreenChonkException exception = assertThrows(GreenChonkException.class, () ->
                new AddCommand(new Todo("read book")).execute(tasks, ui, storage));

        assertEquals("Green Chonk is already carrying a task with those details.",
                exception.getMessage());
        assertEquals(1, tasks.size());
        assertEquals(0, storage.getSaveCount());
        assertNull(ui.getAddedTask());
    }
}
