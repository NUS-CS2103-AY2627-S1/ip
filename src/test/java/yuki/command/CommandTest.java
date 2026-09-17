package yuki.command;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import yuki.exception.YukiException;
import yuki.storage.Storage;
import yuki.task.Task;
import yuki.task.TaskList;
import yuki.task.ToDo;
import yuki.ui.Ui;

/** Tests command execution, including persistence-failure rollback behavior. */
class CommandTest {
    @Test
    void execute_addCommand_taskAddedSavedAndDisplayed() {
        Task task = new ToDo("read book");
        TaskList tasks = new TaskList();
        RecordingStorage storage = new RecordingStorage();
        Ui ui = Ui.createSilentUi();

        new AddCommand(task).execute(tasks, ui, storage);

        assertAll(() -> assertSame(task, tasks.getTask(1)), () ->
                assertEquals(List.of(task), storage.savedTasks), () ->
                assertTrue(ui.getLastResponse().contains("I've added it")));
    }

    @Test
    void execute_addCommandSaveFails_additionRolledBackAndNoSuccessShown() {
        TaskList tasks = new TaskList();
        Ui ui = Ui.createSilentUi();

        assertThrows(YukiException.class, () ->
                new AddCommand(new ToDo("read book")).execute(tasks, ui, new FailingStorage()));

        assertAll(() -> assertEquals(0, tasks.size()), () ->
                assertEquals("", ui.getLastResponse()));
    }

    @Test
    void execute_markAndUnmarkCommands_statusSavedAndDisplayed() {
        Task task = new ToDo("read book");
        TaskList tasks = new TaskList(List.of(task));
        RecordingStorage storage = new RecordingStorage();
        Ui ui = Ui.createSilentUi();

        new MarkCommand(1).execute(tasks, ui, storage);
        String markedResponse = ui.getLastResponse();
        boolean isSavedAsDone = storage.savedTasks.get(0).isDone();
        new UnmarkCommand(1).execute(tasks, ui, storage);

        assertAll(() -> assertTrue(isSavedAsDone), () ->
                assertTrue(markedResponse.contains("[T][X] read book")), () ->
                assertFalse(task.isDone()), () ->
                assertTrue(ui.getLastResponse().contains("[T][ ] read book")));
    }

    @Test
    void execute_markCommandSaveFails_statusRolledBack() {
        Task task = new ToDo("read book");
        TaskList tasks = new TaskList(List.of(task));

        assertThrows(YukiException.class, () ->
                new MarkCommand(1).execute(tasks, Ui.createSilentUi(), new FailingStorage()));

        assertFalse(task.isDone());
    }

    @Test
    void execute_unmarkCommandSaveFails_statusRolledBack() {
        Task task = new ToDo("read book");
        task.markAsDone();
        TaskList tasks = new TaskList(List.of(task));

        assertThrows(YukiException.class, () ->
                new UnmarkCommand(1).execute(tasks, Ui.createSilentUi(), new FailingStorage()));

        assertTrue(task.isDone());
    }

    @Test
    void execute_deleteCommand_taskSavedWithoutDeletedItemThenRemoved() {
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        TaskList tasks = new TaskList(List.of(first, second));
        RecordingStorage storage = new RecordingStorage();
        Ui ui = Ui.createSilentUi();

        new DeleteCommand(1).execute(tasks, ui, storage);

        assertAll(() -> assertEquals(List.of(second), storage.savedTasks), () ->
                assertSame(second, tasks.getTask(1)), () ->
                assertTrue(ui.getLastResponse().contains("[T][ ] first")));
    }

    @Test
    void execute_deleteCommandSaveFails_taskListUnchanged() {
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        TaskList tasks = new TaskList(List.of(first, second));

        assertThrows(YukiException.class, () ->
                new DeleteCommand(1).execute(tasks, Ui.createSilentUi(), new FailingStorage()));

        assertAll(() -> assertEquals(2, tasks.size()), () ->
                assertSame(first, tasks.getTask(1)), () ->
                assertSame(second, tasks.getTask(2)));
    }

    @Test
    void execute_displayAndExitCommands_expectedResponsesAndExitFlagsReturned() {
        TaskList tasks = new TaskList(List.of(new ToDo("read book"), new ToDo("buy food")));
        Ui ui = Ui.createSilentUi();
        Storage storage = new RecordingStorage();

        Command listCommand = new ListCommand();
        listCommand.execute(tasks, ui, storage);
        String listResponse = ui.getLastResponse();
        new FindCommand("book").execute(tasks, ui, storage);
        String findResponse = ui.getLastResponse();
        Command exitCommand = new ExitCommand();
        exitCommand.execute(tasks, ui, storage);

        assertAll(() -> assertTrue(listResponse.contains("2.[T][ ] buy food")), () ->
                assertTrue(findResponse.contains("1.[T][ ] read book")), () ->
                assertFalse(findResponse.contains("buy food")), () ->
                assertEquals("...Goodbye.", ui.getLastResponse()), () ->
                assertFalse(listCommand.isExit()), () -> assertTrue(exitCommand.isExit()));
    }

    /** Storage test double that records the latest saved snapshot. */
    private static class RecordingStorage extends Storage {
        private List<Task> savedTasks = new ArrayList<>();

        @Override
        public void saveTasks(List<Task> tasks) {
            savedTasks = new ArrayList<>(tasks);
        }
    }

    /** Storage test double that simulates a write failure. */
    private static class FailingStorage extends Storage {
        @Override
        public void saveTasks(List<Task> tasks) {
            throw new YukiException("Unable to save tasks.");
        }
    }
}
