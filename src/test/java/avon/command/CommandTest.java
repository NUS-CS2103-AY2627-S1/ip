package avon.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import avon.exception.AvonException;
import avon.exception.DuplicateTaskException;
import avon.exception.InvalidTaskNumberException;
import avon.exception.StorageException;
import avon.storage.Storage;
import avon.task.TaskList;
import avon.task.Todo;
import avon.ui.Ui;

class CommandTest {
    @TempDir
    Path temporaryDirectory;

    private ByteArrayOutputStream output;
    private Ui ui;
    private Storage failingStorage;
    private Storage workingStorage;

    @BeforeEach
    void setUp() {
        output = new ByteArrayOutputStream();
        ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));
        failingStorage = new FailingStorage();
        workingStorage = new Storage(temporaryDirectory.resolve("avon.txt"));
    }

    @Test
    void addCommand_saveFails_restoresTaskListAndShowsNoSuccess() {
        TaskList taskList = new TaskList();

        assertThrows(StorageException.class, () ->
                new AddCommand(new Todo("read Hamlet")).execute(taskList, ui, failingStorage));

        assertEquals(0, taskList.size());
        assertEquals("", output.toString());
    }

    @Test
    void addCommand_duplicateTask_throwsDuplicateTaskExceptionAndDoesNotAdd() {
        TaskList taskList = new TaskList(List.of(new Todo("read Hamlet")));

        assertThrows(DuplicateTaskException.class, () ->
                new AddCommand(new Todo("read Hamlet")).execute(taskList, ui, failingStorage));

        assertEquals(1, taskList.size());
        assertEquals("", output.toString());
    }

    @Test
    void deleteCommand_saveFails_restoresTaskAtOriginalPositionAndShowsNoSuccess() {
        TaskList taskList = new TaskList(List.of(new Todo("first"), new Todo("second")));

        assertThrows(StorageException.class, () ->
                new DeleteCommand(1).execute(taskList, ui, failingStorage));

        assertEquals(2, taskList.size());
        assertEquals("first", taskList.getTask(0).getDescription());
        assertEquals("", output.toString());
    }

    @Test
    void markCommand_saveFails_restoresCompletionStateAndShowsNoSuccess() {
        TaskList taskList = new TaskList(List.of(new Todo("read Hamlet")));

        assertThrows(StorageException.class, () ->
                new MarkCommand(1).execute(taskList, ui, failingStorage));

        assertFalse(taskList.getTask(0).isDone());
        assertEquals("", output.toString());
    }

    @Test
    void unmarkCommand_saveFails_restoresCompletionStateAndShowsNoSuccess() {
        Todo todo = new Todo("read Hamlet");
        todo.markAsDone();
        TaskList taskList = new TaskList(List.of(todo));

        assertThrows(StorageException.class, () ->
                new UnmarkCommand(1).execute(taskList, ui, failingStorage));

        assertTrue(taskList.getTask(0).isDone());
        assertEquals("", output.toString());
    }

    @Test
    void addCommand_success_savesTaskAndShowsConfirmation() throws AvonException {
        TaskList taskList = new TaskList();

        new AddCommand(new Todo("read Hamlet")).execute(taskList, ui, workingStorage);

        assertEquals(1, taskList.size());
        assertEquals("read Hamlet", taskList.getTask(0).getDescription());
        assertEquals("""
                Avon:\tBy thy command, I've added this task:
                        [T][ ] read Hamlet
                Avon:\tNow thou hast 1 task in thy list.
                """, output.toString());
    }

    @Test
    void deleteCommand_success_removesTaskAndShowsConfirmation() throws AvonException {
        TaskList taskList = new TaskList(List.of(new Todo("first"), new Todo("second")));

        new DeleteCommand(1).execute(taskList, ui, workingStorage);

        assertEquals(1, taskList.size());
        assertEquals("second", taskList.getTask(0).getDescription());
        assertEquals("""
                Avon:\tSo be it! I've removed this task:
                        [T][ ] first
                Avon:\tNow thou hast 1 task in thy list.
                """, output.toString());
    }

    @Test
    void markAndUnmarkCommands_success_updateTaskAndShowConfirmations() throws AvonException {
        TaskList taskList = new TaskList(List.of(new Todo("read Hamlet")));

        new MarkCommand(1).execute(taskList, ui, workingStorage);
        assertTrue(taskList.getTask(0).isDone());
        new UnmarkCommand(1).execute(taskList, ui, workingStorage);

        assertFalse(taskList.getTask(0).isDone());
        assertEquals("""
                Avon:\tTis well! Thy noble task is now fulfilled:
                        [T][X] read Hamlet
                Avon:\tThy noble task is undone once more:
                        [T][ ] read Hamlet
                """, output.toString());
    }

    @Test
    void taskNumberCommand_emptyOrOutOfRangeList_throwsInvalidTaskNumberException() {
        assertThrows(InvalidTaskNumberException.class, () ->
                new MarkCommand(1).execute(new TaskList(), ui, workingStorage));
        assertThrows(InvalidTaskNumberException.class, () ->
                new DeleteCommand(2).execute(
                        new TaskList(List.of(new Todo("only"))), ui, workingStorage));
    }

    @Test
    void displayCommands_execute_showExpectedContentWithoutMutatingTasks() {
        TaskList taskList = new TaskList(List.of(
                new Todo("read Hamlet"), new Todo("write essay"), new Todo("rehearse Hamlet")));

        new ListCommand().execute(taskList, ui, workingStorage);
        new FindCommand("rehearse").execute(taskList, ui, workingStorage);
        new HelpCommand().execute(taskList, ui, workingStorage);
        ExitCommand exitCommand = new ExitCommand();
        exitCommand.execute(taskList, ui, workingStorage);

        assertEquals(3, taskList.size());
        assertTrue(exitCommand.isExit());
        assertTrue(output.toString().contains("1.[T][ ] read Hamlet"));
        assertTrue(output.toString().contains("Here are the matching tasks in thy list:\n"
                + "        3.[T][ ] rehearse Hamlet"));
        assertTrue(output.toString().contains("deadline DESCRIPTION /by yyyy-MM-dd [HHmm]"));
        assertTrue(output.toString().contains("Fare thee well!"));
    }

    private static class FailingStorage extends Storage {
        private FailingStorage() {
            super(Path.of("unused"));
        }

        /** {@inheritDoc} */
        @Override
        public void save(TaskList taskList) throws StorageException {
            throw new StorageException("Simulated save failure.");
        }
    }
}
