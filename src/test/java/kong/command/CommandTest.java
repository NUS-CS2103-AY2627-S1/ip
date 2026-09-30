package kong.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import kong.exception.KongException;
import kong.storage.Storage;
import kong.task.Task;
import kong.task.TaskList;
import kong.task.Todo;
import kong.ui.Ui;

class CommandTest {
    @TempDir
    Path temporaryDirectory;

    private static class StubCommand extends Command {
        @Override
        public void execute(TaskList tasks, Ui ui, Storage storage) throws KongException {
            // No-op for testing base helper methods
        }
    }

    private static class FailingStorage extends Storage {
        private final String failureMessage;

        public FailingStorage(String filePath, String failureMessage) {
            super(filePath);
            this.failureMessage = failureMessage;
        }

        @Override
        public void saveTasks(ArrayList<Task> tasks) throws IOException {
            throw new IOException(failureMessage);
        }
    }

    @Test
    void isExit_defaultImplementation_returnsFalse() {
        Command command = new StubCommand();

        assertFalse(command.isExit());
    }

    @Test
    void parseTaskIndex_validNumber_returnsZeroBasedIndex() throws KongException {
        Command command = new StubCommand();

        assertEquals(0, command.parseTaskIndex("1", "delete"));
        assertEquals(4, command.parseTaskIndex(" 5 ", "mark"));
    }

    @Test
    void parseTaskIndex_nonNumericArgument_throwsKongException() {
        Command command = new StubCommand();

        KongException exception = assertThrows(KongException.class, () ->
                command.parseTaskIndex("one", "delete"));

        assertEquals("Invalid command. A delete command needs to be followed by a number.",
                exception.getMessage());
    }

    @Test
    void invalidTaskNumberException_formatsCorrectCount() {
        Command command = new StubCommand();
        TaskList emptyList = new TaskList();
        TaskList populatedList = new TaskList();
        populatedList.add(new Todo("sample"));

        assertEquals("This task number is invalid. You currently have 0 tasks in your list.",
                command.invalidTaskNumberException(emptyList).getMessage());
        assertEquals("This task number is invalid. You currently have 1 tasks in your list.",
                command.invalidTaskNumberException(populatedList).getMessage());
    }

    @Test
    void saveTasks_storageIoExceptionWithDetail_wrapsInKongException() {
        Command command = new StubCommand();
        TaskList tasks = new TaskList();
        Storage failingStorage = new FailingStorage(temporaryDirectory.resolve("dummy.txt").toString(),
                "Disk full");

        KongException exception = assertThrows(KongException.class, () ->
                command.saveTasks(tasks, failingStorage));

        assertEquals("Unable to save tasks to disk: Disk full", exception.getMessage());
    }

    @Test
    void saveTasks_storageIoExceptionBlankDetail_wrapsInKongExceptionWithoutColon() {
        Command command = new StubCommand();
        TaskList tasks = new TaskList();
        Storage failingStorage = new FailingStorage(temporaryDirectory.resolve("dummy.txt").toString(),
                " ");

        KongException exception = assertThrows(KongException.class, () ->
                command.saveTasks(tasks, failingStorage));

        assertEquals("Unable to save tasks to disk.", exception.getMessage());
    }
}
