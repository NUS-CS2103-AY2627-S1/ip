package greenchonk.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import greenchonk.command.CommandTestSupport.RecordingStorage;
import greenchonk.command.CommandTestSupport.RecordingUi;
import greenchonk.exception.GreenChonkException;
import greenchonk.task.Task;
import greenchonk.task.TaskList;
import greenchonk.task.Todo;

class DeleteCommandTest {
    @Test
    void execute_existingTask_taskDeletedSavedAndReported() throws GreenChonkException {
        Task firstTask = new Todo("first");
        Task deletedTask = new Todo("delete me");
        Task lastTask = new Todo("last");
        TaskList tasks = new TaskList(List.of(firstTask, deletedTask, lastTask));
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();

        new DeleteCommand(2).execute(tasks, ui, storage);

        assertEquals(2, tasks.size());
        assertSame(firstTask, tasks.get(0));
        assertSame(lastTask, tasks.get(1));
        assertEquals(1, storage.getSaveCount());
        assertSame(deletedTask, ui.getDeletedTask());
        assertEquals(2, ui.getRemainingTaskCount());
    }

    @Test
    void execute_emptyTaskList_exceptionThrownWithoutSaving() {
        TaskList tasks = new TaskList();
        RecordingStorage storage = new RecordingStorage();

        GreenChonkException exception = assertThrows(GreenChonkException.class, () ->
                new DeleteCommand(1).execute(tasks, new RecordingUi(), storage));

        assertEquals("There are no tasks to delete yet.", exception.getMessage());
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    void execute_taskNumberOutsideList_exceptionThrownWithoutSaving() {
        TaskList tasks = new TaskList(List.of(new Todo("only task")));
        RecordingStorage storage = new RecordingStorage();

        GreenChonkException exception = assertThrows(GreenChonkException.class, () ->
                new DeleteCommand(2).execute(tasks, new RecordingUi(), storage));

        assertEquals("Task 2 does not exist. Choose a number from 1 to 1.",
                exception.getMessage());
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    void execute_saveFails_originalOrderRestoredAndDeletionNotReported() {
        Task firstTask = new Todo("first");
        Task middleTask = new Todo("middle");
        Task lastTask = new Todo("last");
        TaskList tasks = new TaskList(List.of(firstTask, middleTask, lastTask));
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();
        storage.failNextSave();

        GreenChonkException exception = assertThrows(GreenChonkException.class, () ->
                new DeleteCommand(2).execute(tasks, ui, storage));

        assertEquals("save failed", exception.getMessage());
        assertEquals(3, tasks.size());
        assertSame(firstTask, tasks.get(0));
        assertSame(middleTask, tasks.get(1));
        assertSame(lastTask, tasks.get(2));
        assertEquals(1, storage.getSaveCount());
        assertNull(ui.getDeletedTask());
    }
}
