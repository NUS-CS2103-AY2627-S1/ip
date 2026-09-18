package luna.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import luna.LunaException;
import luna.storage.Storage;
import luna.task.Task;
import luna.task.TaskList;
import luna.task.Todo;
import luna.ui.Ui;

/**
 * Tests for persistence failures in {@link ArchiveCommand}.
 */
public class ArchiveCommandTest {
    @Test
    public void execute_archiveWriteFails_keepsActiveTasks() {
        TaskList tasks = new TaskList(new Todo("read book"));
        ArchiveCommand command = new ArchiveCommand("archive 1");

        LunaException exception = assertThrows(LunaException.class, () ->
                command.execute(tasks, new Ui(false), new FailingArchiveStorage()));

        assertEquals("I could not archive your tasks to disk.", exception.getMessage());
        assertEquals(1, tasks.size());
    }

    @Test
    public void execute_activeSaveFails_keepsActiveTasksAfterArchiving() {
        TaskList tasks = new TaskList(new Todo("read book"));
        ArchiveThenFailSaveStorage storage = new ArchiveThenFailSaveStorage();
        ArchiveCommand command = new ArchiveCommand("archive 1");

        LunaException exception = assertThrows(LunaException.class, () ->
                command.execute(tasks, new Ui(false), storage));

        assertEquals("Archiving was saved, but I could not update your active task file. "
                + "Your active list was left unchanged.", exception.getMessage());
        assertEquals(1, tasks.size());
        assertEquals(List.of("read book"), storage.getArchivedDescriptions());
    }

    /**
     * Storage double that cannot write archive entries.
     */
    private static class FailingArchiveStorage extends Storage {
        @Override
        public void archiveTasks(List<Task> tasks) throws IOException {
            throw new IOException("Archive unavailable");
        }
    }

    /**
     * Storage double that archives successfully but cannot save active tasks.
     */
    private static class ArchiveThenFailSaveStorage extends Storage {
        private final List<Task> archivedTasks = new ArrayList<>();

        @Override
        public void archiveTasks(List<Task> tasks) {
            archivedTasks.addAll(tasks);
        }

        @Override
        public void saveTasks(TaskList tasks) throws IOException {
            throw new IOException("Active storage unavailable");
        }

        /**
         * Returns the descriptions captured by this test archive.
         *
         * @return Archived task descriptions.
         */
        public List<String> getArchivedDescriptions() {
            return archivedTasks.stream()
                    .map(Task::getDescription)
                    .toList();
        }
    }
}
