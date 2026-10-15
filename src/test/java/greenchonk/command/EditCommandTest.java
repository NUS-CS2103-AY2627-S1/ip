package greenchonk.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import greenchonk.command.CommandTestSupport.RecordingStorage;
import greenchonk.command.CommandTestSupport.RecordingUi;
import greenchonk.exception.GreenChonkException;
import greenchonk.task.Deadline;
import greenchonk.task.Event;
import greenchonk.task.Task;
import greenchonk.task.TaskList;
import greenchonk.task.Todo;

class EditCommandTest {
    private static final LocalDate AUGUST_28 = LocalDate.of(2026, 8, 28);
    private static final LocalDate AUGUST_29 = LocalDate.of(2026, 8, 29);
    private static final LocalDate AUGUST_30 = LocalDate.of(2026, 8, 30);

    @Test
    void execute_editDescription_descriptionChangedAndOtherDetailsPreserved()
            throws GreenChonkException {
        Deadline originalTask = new Deadline("submit draft", AUGUST_28);
        originalTask.markAsDone();
        TaskList tasks = new TaskList(List.of(originalTask));
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();

        EditCommand.editDescription(1, "submit final report")
                .execute(tasks, ui, storage);

        Deadline editedTask = (Deadline) tasks.get(0);
        assertEquals("submit final report", editedTask.getDescription());
        assertEquals(AUGUST_28, editedTask.getDueDate());
        assertTrue(editedTask.isDone());
        assertEquals(1, storage.getSaveCount());
        assertSame(originalTask, ui.getEditedOriginalTask());
        assertSame(editedTask, ui.getEditedTask());
    }

    @Test
    void execute_editDueDate_dueDateChangedAndDescriptionPreserved()
            throws GreenChonkException {
        Deadline originalTask = new Deadline("submit report", AUGUST_28);
        TaskList tasks = new TaskList(List.of(originalTask));

        EditCommand.editDueDate(1, AUGUST_30)
                .execute(tasks, new RecordingUi(), new RecordingStorage());

        Deadline editedTask = (Deadline) tasks.get(0);
        assertEquals("submit report", editedTask.getDescription());
        assertEquals(AUGUST_30, editedTask.getDueDate());
        assertFalse(editedTask.isDone());
    }

    @Test
    void execute_editEventDates_datesChangedAndOtherDetailsPreserved()
            throws GreenChonkException {
        Event originalTask = new Event("conference", AUGUST_28, AUGUST_30);
        originalTask.markAsDone();
        TaskList tasks = new TaskList(List.of(originalTask));
        RecordingStorage storage = new RecordingStorage();

        EditCommand.editStartDate(1, AUGUST_29)
                .execute(tasks, new RecordingUi(), storage);
        EditCommand.editEndDate(1, AUGUST_29)
                .execute(tasks, new RecordingUi(), storage);

        Event editedTask = (Event) tasks.get(0);
        assertEquals("conference", editedTask.getDescription());
        assertEquals(AUGUST_29, editedTask.getStartDate());
        assertEquals(AUGUST_29, editedTask.getEndDate());
        assertTrue(editedTask.isDone());
        assertEquals(2, storage.getSaveCount());
    }

    @Test
    void execute_dateFieldDoesNotApply_exceptionThrownWithoutSaving() {
        Task originalTask = new Todo("read book");
        TaskList tasks = new TaskList(List.of(originalTask));
        RecordingStorage storage = new RecordingStorage();

        GreenChonkException exception = assertThrows(GreenChonkException.class, () ->
                EditCommand.editDueDate(1, AUGUST_30)
                        .execute(tasks, new RecordingUi(), storage));

        assertEquals("Task 1 is not a deadline. Use /by only with a deadline.",
                exception.getMessage());
        assertSame(originalTask, tasks.get(0));
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    void execute_eventFieldDoesNotApply_exceptionThrownWithoutSaving() {
        Task originalTask = new Deadline("submit report", AUGUST_28);
        TaskList tasks = new TaskList(List.of(originalTask));
        RecordingStorage storage = new RecordingStorage();

        GreenChonkException exception = assertThrows(GreenChonkException.class, () ->
                EditCommand.editEndDate(1, AUGUST_30)
                        .execute(tasks, new RecordingUi(), storage));

        assertEquals("Task 1 is not an event. Use /from and /to only with an event.",
                exception.getMessage());
        assertSame(originalTask, tasks.get(0));
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    void execute_editEventToInvalidRange_exceptionThrownWithoutChangingTask() {
        Event originalTask = new Event("conference", AUGUST_29, AUGUST_30);
        TaskList tasks = new TaskList(List.of(originalTask));
        RecordingStorage storage = new RecordingStorage();

        GreenChonkException exception = assertThrows(GreenChonkException.class, () ->
                EditCommand.editEndDate(1, AUGUST_28)
                        .execute(tasks, new RecordingUi(), storage));

        assertEquals("An event's end date cannot be before its start date. "
                        + "Try /to 2026-08-29 or later.",
                exception.getMessage());
        assertSame(originalTask, tasks.get(0));
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    void execute_editEventFromInvalidRange_exceptionThrownWithoutChangingTask() {
        Event originalTask = new Event("conference", AUGUST_28, AUGUST_29);
        TaskList tasks = new TaskList(List.of(originalTask));
        RecordingStorage storage = new RecordingStorage();

        GreenChonkException exception = assertThrows(GreenChonkException.class, () ->
                EditCommand.editStartDate(1, AUGUST_30)
                        .execute(tasks, new RecordingUi(), storage));

        assertEquals("An event's start date cannot be after its end date. "
                        + "Try /from 2026-08-29 or earlier.",
                exception.getMessage());
        assertSame(originalTask, tasks.get(0));
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    void execute_saveFails_originalTaskRestoredAndEditNotReported() {
        Task originalTask = new Todo("original description");
        TaskList tasks = new TaskList(List.of(originalTask));
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();
        storage.failNextSave();

        GreenChonkException exception = assertThrows(GreenChonkException.class, () ->
                EditCommand.editDescription(1, "replacement description")
                        .execute(tasks, ui, storage));

        assertEquals("save failed", exception.getMessage());
        assertSame(originalTask, tasks.get(0));
        assertEquals(1, storage.getSaveCount());
        assertNull(ui.getEditedOriginalTask());
        assertNull(ui.getEditedTask());
    }

    @Test
    void execute_duplicateDescription_originalTasksPreservedWithoutSaving() {
        Todo originalTask = new Todo("read book");
        Todo existingTask = new Todo("buy milk");
        existingTask.markAsDone();
        TaskList tasks = new TaskList(List.of(originalTask, existingTask));
        RecordingStorage storage = new RecordingStorage();
        RecordingUi ui = new RecordingUi();

        assertThrows(GreenChonkException.class, () ->
                EditCommand.editDescription(1, "buy milk").execute(tasks, ui, storage));

        assertSame(originalTask, tasks.get(0));
        assertSame(existingTask, tasks.get(1));
        assertEquals(0, storage.getSaveCount());
        assertNull(ui.getEditedTask());
    }

    @Test
    void execute_duplicateDate_originalDeadlinePreservedWithoutSaving() {
        Deadline originalTask = new Deadline("submit report", AUGUST_28);
        TaskList tasks = new TaskList(List.of(originalTask,
                new Deadline("submit report", AUGUST_30)));
        RecordingStorage storage = new RecordingStorage();

        assertThrows(GreenChonkException.class, () ->
                EditCommand.editDueDate(1, AUGUST_30).execute(tasks, new RecordingUi(), storage));

        assertSame(originalTask, tasks.get(0));
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    void execute_unchangedDescription_taskRemainsValid() throws GreenChonkException {
        Todo originalTask = new Todo("read book");
        originalTask.markAsDone();
        TaskList tasks = new TaskList(List.of(originalTask));

        EditCommand.editDescription(1, "read book")
                .execute(tasks, new RecordingUi(), new RecordingStorage());

        assertEquals("read book", tasks.get(0).getDescription());
        assertTrue(tasks.get(0).isDone());
        assertEquals(1, tasks.size());
    }
}
