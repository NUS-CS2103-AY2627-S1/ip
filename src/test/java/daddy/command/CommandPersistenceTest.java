package daddy.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import daddy.exception.DaddyException;
import daddy.storage.Storage;
import daddy.task.Task;
import daddy.task.TaskList;
import daddy.task.Todo;
import daddy.ui.Ui;

/**
 * Tests command validation and rollback when task persistence cannot complete.
 */
class CommandPersistenceTest {
    /** Provides an isolated path for creating an intentionally unusable storage location. */
    @TempDir
    Path temporaryDirectory;

    /** Verifies that equivalent task details are rejected before storage is changed. */
    @Test
    void addCommand_equivalentTask_throwsWithoutChangingList() {
        Task existingTask = new Todo("Read Book");
        TaskList tasks = new TaskList();
        tasks.add(existingTask);
        AddCommand command = new AddCommand(new Todo("read book"));

        DaddyException exception = assertThrows(DaddyException.class, () ->
                command.execute(tasks, new Ui(ignored -> { }), null));

        assertTrue(exception.getMessage().contains("already in the list"));
        assertEquals(1, tasks.size());
        assertSame(existingTask, tasks.getTaskAt(0));
    }

    /**
     * Verifies that a failed save removes an unsaved addition from the in-memory list.
     *
     * @throws IOException if the intentionally blocking file cannot be created
     */
    @Test
    void addCommand_saveFails_rollsBackAddition() throws IOException {
        TaskList tasks = new TaskList();
        AddCommand command = new AddCommand(new Todo("read book"));

        assertThrows(DaddyException.class, () ->
                command.execute(tasks, new Ui(ignored -> { }), createUnwritableStorage()));

        assertEquals(0, tasks.size());
    }

    /**
     * Verifies that a failed save restores a task removed from the in-memory list.
     *
     * @throws IOException if the intentionally blocking file cannot be created
     */
    @Test
    void deleteCommand_saveFails_rollsBackDeletion() throws IOException {
        Task task = new Todo("read book");
        TaskList tasks = new TaskList();
        tasks.add(task);

        assertThrows(DaddyException.class, () ->
                new DeleteCommand(0).execute(tasks, new Ui(ignored -> { }), createUnwritableStorage()));

        assertEquals(1, tasks.size());
        assertSame(task, tasks.getTaskAt(0));
    }

    /**
     * Verifies that failed saves restore the previous done status for mark and unmark commands.
     *
     * @throws IOException if the intentionally blocking file cannot be created
     */
    @Test
    void statusCommands_saveFails_rollBackStatus() throws IOException {
        Task task = new Todo("read book");
        TaskList tasks = new TaskList();
        tasks.add(task);

        assertThrows(DaddyException.class, () ->
                new MarkCommand(0).execute(tasks, new Ui(ignored -> { }), createUnwritableStorage()));
        assertFalse(task.isDone());

        task.markAsDone();
        assertThrows(DaddyException.class, () ->
                new UnmarkCommand(0).execute(tasks, new Ui(ignored -> { }), createUnwritableStorage()));
        assertTrue(task.isDone());
    }

    /**
     * Creates storage whose parent path is a regular file and therefore cannot hold task data.
     *
     * @return storage that fails every save attempt
     * @throws IOException if the blocking file cannot be created
     */
    private Storage createUnwritableStorage() throws IOException {
        Path blockingParent = temporaryDirectory.resolve("not-a-folder");
        if (!Files.exists(blockingParent)) {
            Files.writeString(blockingParent, "blocking file");
        }
        return new Storage(blockingParent.resolve("daddy.txt"), temporaryDirectory.resolve("duke.txt"));
    }
}
