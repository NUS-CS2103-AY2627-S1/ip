package yourhand.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import yourhand.exceptions.StorageException;
import yourhand.exceptions.YourHandException;
import yourhand.storage.Storage;
import yourhand.tasks.Event;
import yourhand.tasks.TaskDateTime;
import yourhand.tasks.TaskList;
import yourhand.tasks.Todo;
import yourhand.ui.Ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests command execution and preservation of task state when persistence fails. */
class CommandTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void addTask_validTask_savesAndReportsSuccess() throws YourHandException {
        TaskList taskList = new TaskList();
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        new AddTaskCommand(new Todo("read book"))
                .execute(taskList, ui(output), storage("tasks.txt"));

        assertEquals(1, taskList.size());
        assertTrue(output.toString().contains("I'll note this down for you haiz"));
    }

    @Test
    void addTask_saveFails_rollsBackTask() {
        TaskList taskList = new TaskList();
        Path directoryAsFile = temporaryDirectory.resolve("blocked");
        createDirectory(directoryAsFile);

        assertThrowsYourHandException(() -> new AddTaskCommand(new Todo("read book"))
                .execute(taskList, silentUi(), new Storage(directoryAsFile)));

        assertTrue(taskList.isEmpty());
    }

    @Test
    void deleteTask_saveFails_restoresRemovedTask() throws YourHandException {
        TaskList taskList = new TaskList();
        Todo task = new Todo("read book");
        taskList.add(task);
        Path directoryAsFile = temporaryDirectory.resolve("blocked");
        createDirectory(directoryAsFile);

        assertThrowsYourHandException(() -> new DeleteCommand(1)
                .execute(taskList, silentUi(), new Storage(directoryAsFile)));

        assertEquals(task, taskList.getTask(1));
    }

    @Test
    void updateStatus_saveFails_restoresOriginalStatus() throws YourHandException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        Path directoryAsFile = temporaryDirectory.resolve("blocked");
        createDirectory(directoryAsFile);

        assertThrowsYourHandException(() -> new TaskStatusCommand(1, true)
                .execute(taskList, silentUi(), new Storage(directoryAsFile)));

        assertFalse(taskList.getTask(1).getStatusIcon().equals("X"));
    }

    @Test
    void updateStatus_repeatedCommand_reportsNoChange() throws YourHandException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Storage storage = storage("tasks.txt");

        new TaskStatusCommand(1, true).execute(taskList, ui(output), storage);
        new TaskStatusCommand(1, true).execute(taskList, ui(output), storage);

        assertTrue(output.toString().contains("already done"));
    }

    @Test
    void viewSchedule_eventOverlappingDate_reportsEvent() throws YourHandException {
        TaskList taskList = new TaskList();
        taskList.add(new Event("conference",
                new TaskDateTime(LocalDate.of(2026, 9, 9)),
                new TaskDateTime(LocalDate.of(2026, 9, 11))));
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        new ViewScheduleCommand(LocalDate.of(2026, 9, 10))
                .execute(taskList, ui(output), storage("tasks.txt"));

        assertTrue(output.toString().contains("conference"));
    }

    @Test
    void displayCommands_reportTheirResults() throws YourHandException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = ui(output);
        Storage storage = storage("tasks.txt");

        new FindCommand("book").execute(taskList, ui, storage);
        new ListCommand().execute(taskList, ui, storage);
        new HelpCommand().execute(taskList, ui, storage);

        String text = output.toString();
        assertTrue(text.contains("read book"));
        assertTrue(text.contains("todo DESCRIPTION"));
    }

    @Test
    void exitCommand_isExitAndReportsGoodbye() throws YourHandException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ExitCommand command = new ExitCommand();

        command.execute(new TaskList(), ui(output), storage("tasks.txt"));

        assertTrue(command.isExit());
        assertTrue(output.toString().contains("See you never :)"));
    }

    private Storage storage(String fileName) {
        return new Storage(temporaryDirectory.resolve(fileName));
    }

    private Ui ui(ByteArrayOutputStream output) {
        return new Ui(new PrintStream(output));
    }

    private Ui silentUi() {
        return ui(new ByteArrayOutputStream());
    }

    private void createDirectory(Path path) {
        try {
            Files.createDirectory(path);
        } catch (Exception exception) {
            throw new AssertionError("Unable to create save-failure fixture", exception);
        }
    }

    private void assertThrowsYourHandException(ThrowingCommand action) {
        StorageException exception = assertThrows(StorageException.class, action::run);
        assertTrue(exception.getMessage().contains("couldn't save"));
    }

    @FunctionalInterface
    private interface ThrowingCommand {
        void run() throws YourHandException;
    }
}
