package greenchonk.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import greenchonk.task.TaskStatus;
import greenchonk.task.Todo;

class UpdateStatusCommandTest {
    @Test
    void execute_markExistingTask_taskMarkedSavedAndReported() throws GreenChonkException {
        Task task = new Todo("read book");
        TaskList tasks = new TaskList(List.of(task));
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();

        new UpdateStatusCommand(1, TaskStatus.DONE, "mark").execute(tasks, ui, storage);

        assertTrue(task.isDone());
        assertEquals(1, storage.getSaveCount());
        assertSame(task, ui.getUpdatedTask());
        assertEquals(TaskStatus.DONE, ui.getUpdatedStatus());
    }

    @Test
    void execute_unmarkExistingTask_taskUnmarkedSavedAndReported() throws GreenChonkException {
        Task task = new Todo("read book");
        task.markAsDone();
        TaskList tasks = new TaskList(List.of(task));
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();

        new UpdateStatusCommand(1, TaskStatus.NOT_DONE, "unmark")
                .execute(tasks, ui, storage);

        assertFalse(task.isDone());
        assertEquals(1, storage.getSaveCount());
        assertSame(task, ui.getUpdatedTask());
        assertEquals(TaskStatus.NOT_DONE, ui.getUpdatedStatus());
    }

    @Test
    void execute_missingTask_exceptionThrownWithoutSaving() {
        TaskList tasks = new TaskList(List.of(new Todo("only task")));
        RecordingStorage storage = new RecordingStorage();

        GreenChonkException exception = assertThrows(GreenChonkException.class, () ->
                new UpdateStatusCommand(0, TaskStatus.DONE, "mark")
                        .execute(tasks, new RecordingUi(), storage));

        assertEquals("Task 0 does not exist. Choose a number from 1 to 1.",
                exception.getMessage());
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    void execute_markSaveFails_originalNotDoneStatusRestoredAndNotReported() {
        Task task = new Todo("read book");
        TaskList tasks = new TaskList(List.of(task));
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();
        storage.failNextSave();

        GreenChonkException exception = assertThrows(GreenChonkException.class, () ->
                new UpdateStatusCommand(1, TaskStatus.DONE, "mark")
                        .execute(tasks, ui, storage));

        assertEquals("save failed", exception.getMessage());
        assertFalse(task.isDone());
        assertEquals(1, storage.getSaveCount());
        assertNull(ui.getUpdatedTask());
    }

    @Test
    void execute_unmarkSaveFails_originalDoneStatusRestoredAndNotReported() {
        Task task = new Todo("read book");
        task.markAsDone();
        TaskList tasks = new TaskList(List.of(task));
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();
        storage.failNextSave();

        GreenChonkException exception = assertThrows(GreenChonkException.class, () ->
                new UpdateStatusCommand(1, TaskStatus.NOT_DONE, "unmark")
                        .execute(tasks, ui, storage));

        assertEquals("save failed", exception.getMessage());
        assertTrue(task.isDone());
        assertEquals(1, storage.getSaveCount());
        assertNull(ui.getUpdatedTask());
    }
}
