package niulai.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import niulai.NiuLaiException;
import niulai.model.Task;
import niulai.model.TaskList;
import niulai.model.TaskStatus;
import niulai.model.Todo;
import niulai.service.Storage;
import niulai.service.StorageException;
import niulai.service.Ui;

/** Tests that persistence commands preserve in-memory state when saving fails. */
class PersistenceCommandTest {
    /** Temporary directory used by successful setup saves before undo failures. */
    @TempDir
    Path temporaryDirectory;

    /** A storage implementation that always fails before writing task data. */
    private static class FailingStorage extends Storage {
        /** Creates storage that fails every save attempt. */
        FailingStorage() {
            super("unused.txt");
        }

        /** Always simulates a save failure. */
        @Override
        public void save(TaskList tasks) throws IOException {
            throw new IOException("Simulated save failure.");
        }
    }

    /** A storage implementation that reports a specific expected persistence failure. */
    private static class TypedFailingStorage extends Storage {
        /** Creates storage that rejects saves because its loaded file changed externally. */
        TypedFailingStorage() {
            super("unused.txt");
        }

        /** Always simulates an external data-file change. */
        @Override
        public void save(TaskList tasks) throws IOException {
            throw new StorageException(StorageException.EXTERNAL_CHANGE_MESSAGE);
        }
    }

    /** Verifies that a failed add removes only the task that was just appended. */
    @Test
    void execute_addSaveFails_removesAddedTask() {
        Task originalTask = new Todo("existing task");
        Task addedTask = new Todo("new task");
        TaskList tasks = new TaskList(originalTask);

        assertThrows(NiuLaiException.class,
                () -> new AddCommand(addedTask).execute(tasks, new Ui(), new FailingStorage()));

        assertEquals(1, tasks.size());
        assertSame(originalTask, tasks.get(0));
    }

    /** Verifies that commands preserve actionable typed storage messages while rolling back. */
    @Test
    void execute_typedStorageFailure_specificMessageReturnedAndTaskRemoved() {
        Task originalTask = new Todo("existing task");
        TaskList tasks = new TaskList(originalTask);

        NiuLaiException exception = assertThrows(NiuLaiException.class,
                () -> new AddCommand(new Todo("new task"))
                        .execute(tasks, new Ui(), new TypedFailingStorage()));

        assertEquals("NOOO!!! " + StorageException.EXTERNAL_CHANGE_MESSAGE,
                exception.getMessage());
        assertEquals(1, tasks.size());
        assertSame(originalTask, tasks.get(0));
    }

    /** Verifies that a failed delete restores the exact task at its original position. */
    @Test
    void execute_deleteSaveFails_restoresDeletedTask() {
        Task deletedTask = new Todo("first task");
        Task followingTask = new Todo("second task");
        TaskList tasks = new TaskList(deletedTask, followingTask);

        assertThrows(NiuLaiException.class,
                () -> new DeleteCommand(0).execute(tasks, new Ui(), new FailingStorage()));

        assertEquals(2, tasks.size());
        assertSame(deletedTask, tasks.get(0));
        assertSame(followingTask, tasks.get(1));
    }

    /** Verifies that a failed mark restores the task's pending status. */
    @Test
    void execute_markSaveFails_restoresPendingStatus() {
        Task task = new Todo("read book");
        TaskList tasks = new TaskList(task);

        assertThrows(NiuLaiException.class,
                () -> new MarkCommand(0).execute(tasks, new Ui(), new FailingStorage()));

        assertEquals(TaskStatus.PENDING, task.getStatus());
    }

    /** Verifies that a failed unmark restores the task's completed status. */
    @Test
    void execute_unmarkSaveFails_restoresCompletedStatus() {
        Task task = new Todo("read book");
        task.markAsDone();
        TaskList tasks = new TaskList(task);

        assertThrows(NiuLaiException.class,
                () -> new UnmarkCommand(0).execute(tasks, new Ui(), new FailingStorage()));

        assertEquals(TaskStatus.COMPLETED, task.getStatus());
    }

    /** Verifies that marking an already completed task is rejected before persistence. */
    @Test
    void execute_markAlreadyCompleted_exceptionExplainsNoOp() {
        Task task = new Todo("read book");
        task.markAsDone();
        TaskList tasks = new TaskList(task);

        NiuLaiException exception = assertThrows(NiuLaiException.class,
                () -> new MarkCommand(0).execute(tasks, new Ui(), new FailingStorage()));

        assertEquals("NOOO!!! That task is already marked as done.", exception.getMessage());
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
    }

    /** Verifies that unmarking an already pending task is rejected before persistence. */
    @Test
    void execute_unmarkAlreadyPending_exceptionExplainsNoOp() {
        Task task = new Todo("read book");
        TaskList tasks = new TaskList(task);

        NiuLaiException exception = assertThrows(NiuLaiException.class,
                () -> new UnmarkCommand(0).execute(tasks, new Ui(), new FailingStorage()));

        assertEquals("NOOO!!! That task is already marked as not done.",
                exception.getMessage());
        assertEquals(TaskStatus.PENDING, task.getStatus());
    }

    /** Verifies a failed add undo puts the removed task back in memory. */
    @Test
    void undo_addSaveFails_restoresAddedTask() throws NiuLaiException {
        Task task = new Todo("new task");
        TaskList tasks = new TaskList();
        Command.UndoAction undo = new AddCommand(task).execute(
                tasks,
                new Ui(),
                new Storage(temporaryDirectory.resolve("add.txt").toString()));

        assertThrows(NiuLaiException.class, () -> undo.undo(tasks, new FailingStorage()));

        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(0));
    }

    /** Verifies a failed delete undo removes the task it could not persist restoring. */
    @Test
    void undo_deleteSaveFails_keepsTaskDeleted() throws NiuLaiException {
        Task deletedTask = new Todo("deleted task");
        Task remainingTask = new Todo("remaining task");
        TaskList tasks = new TaskList(deletedTask, remainingTask);
        Command.UndoAction undo = new DeleteCommand(0).execute(
                tasks,
                new Ui(),
                new Storage(temporaryDirectory.resolve("delete.txt").toString()));

        assertThrows(NiuLaiException.class, () -> undo.undo(tasks, new FailingStorage()));

        assertEquals(1, tasks.size());
        assertSame(remainingTask, tasks.get(0));
    }

    /** Verifies a failed status undo restores the status present before that undo attempt. */
    @Test
    void undo_markSaveFails_keepsTaskCompleted() throws NiuLaiException {
        Task task = new Todo("read book");
        TaskList tasks = new TaskList(task);
        Command.UndoAction undo = new MarkCommand(0).execute(
                tasks,
                new Ui(),
                new Storage(temporaryDirectory.resolve("mark.txt").toString()));

        assertThrows(NiuLaiException.class, () -> undo.undo(tasks, new FailingStorage()));

        assertEquals(TaskStatus.COMPLETED, task.getStatus());
    }
}
