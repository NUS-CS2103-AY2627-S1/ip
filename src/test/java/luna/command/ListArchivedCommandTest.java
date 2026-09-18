package luna.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import luna.LunaException;
import luna.storage.Storage;
import luna.task.Task;
import luna.task.TaskList;
import luna.task.Todo;
import luna.ui.Ui;

/**
 * Tests archive loading behavior in {@link ListArchivedCommand}.
 */
public class ListArchivedCommandTest {
    @Test
    public void execute_archivedTasksLoad_displaysTasks() throws LunaException {
        Ui ui = new Ui(false);
        ListArchivedCommand command = new ListArchivedCommand();

        command.execute(new TaskList(), ui, new ArchivedTaskStorage());

        assertEquals("These tasks are resting in your archive:\n1. [T][ ] read book",
                ui.consumeLatestResponse());
    }

    @Test
    public void execute_archiveReadFails_throwsUserFacingLunaException() {
        ListArchivedCommand command = new ListArchivedCommand();

        LunaException exception = assertThrows(LunaException.class, () ->
                command.execute(new TaskList(), new Ui(false), new UnreadableArchiveStorage()));

        assertEquals("I could not load your archived tasks.", exception.getMessage());
    }

    @Test
    public void execute_archiveContentIsInvalid_throwsUserFacingLunaException() {
        ListArchivedCommand command = new ListArchivedCommand();

        LunaException exception = assertThrows(LunaException.class, () ->
                command.execute(new TaskList(), new Ui(false), new InvalidArchiveStorage()));

        assertEquals("I could not load your archived tasks.", exception.getMessage());
    }

    /**
     * Storage double containing one archived task.
     */
    private static class ArchivedTaskStorage extends Storage {
        @Override
        public List<Task> loadArchivedTasks() {
            return List.of(new Todo("read book"));
        }
    }

    /**
     * Storage double that cannot read the archive file.
     */
    private static class UnreadableArchiveStorage extends Storage {
        @Override
        public List<Task> loadArchivedTasks() throws IOException {
            throw new IOException("Archive unavailable");
        }
    }

    /**
     * Storage double that reports malformed archive content.
     */
    private static class InvalidArchiveStorage extends Storage {
        @Override
        public List<Task> loadArchivedTasks() throws LunaException {
            throw new LunaException("Invalid archive");
        }
    }
}
