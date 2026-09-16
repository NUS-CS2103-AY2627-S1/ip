package bobby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests command state changes, persistence, output, and rollback behavior. */
public class CommandTest {
    @TempDir
    private Path temporaryDirectory;

    /** Verifies that adding a unique task updates memory, disk, and the UI. */
    @Test
    public void addCommand_uniqueTask_taskAddedSavedAndShown() throws BobbyException {
        TaskList tasks = new TaskList();
        Task task = new ToDo("read book");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);
        Storage storage = createStorage("add.txt");

        new AddCommand(task).execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(0));
        assertEquals("Boop! I've tucked this task into the list:" + System.lineSeparator()
                + "[T][ ] read book" + System.lineSeparator()
                + "Bobby's task count is now 1." + System.lineSeparator(), outputText(output));
        assertEquals(1, storage.load().size());
    }

    /** Verifies that an equivalent task is rejected without changing the list. */
    @Test
    public void addCommand_duplicateTask_exceptionThrownAndListUnchanged() {
        Task originalTask = new ToDo("Read Book");
        TaskList tasks = new TaskList(List.of(originalTask));

        BobbyException exception = assertThrows(BobbyException.class, () ->
                new AddCommand(new ToDo(" read   book ")).execute(
                        tasks, createUi(new ByteArrayOutputStream()), createStorage("duplicate.txt")));

        assertEquals("Error! This task already exists in the list.", exception.getMessage());
        assertEquals(1, tasks.size());
        assertSame(originalTask, tasks.get(0));
    }

    /** Verifies that a failed add save removes the tentative task. */
    @Test
    public void addCommand_saveFails_additionRolledBack() {
        TaskList tasks = new TaskList(List.of(new ToDo("existing task")));

        assertThrows(BobbyException.class, () -> new AddCommand(new ToDo("new task")).execute(
                tasks, createUi(new ByteArrayOutputStream()), createFailingStorage()));

        assertEquals(1, tasks.size());
        assertEquals("existing task", tasks.get(0).getDescription());
    }

    /** Verifies a null add-command task violates the command contract. */
    @Test
    public void addCommand_nullTask_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new AddCommand(null));
    }

    /** Verifies that deleting a task updates memory, disk, and the UI. */
    @Test
    public void deleteCommand_validIndex_taskDeletedSavedAndShown() throws BobbyException {
        Task firstTask = new ToDo("first task");
        Task secondTask = new ToDo("second task");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Storage storage = createStorage("delete.txt");

        new DeleteCommand("delete 1").execute(tasks, createUi(output), storage);

        assertEquals(1, tasks.size());
        assertSame(secondTask, tasks.get(0));
        assertTrue(outputText(output).contains("Poof! This task has left the building:"));
        assertEquals("second task", storage.load().get(0).getDescription());
    }

    /** Verifies that a failed delete save restores the task at its original position. */
    @Test
    public void deleteCommand_saveFails_deletionRolledBackAtOriginalIndex() {
        Task firstTask = new ToDo("first task");
        Task secondTask = new ToDo("second task");
        Task thirdTask = new ToDo("third task");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask, thirdTask));

        assertThrows(BobbyException.class, () -> new DeleteCommand("delete 2").execute(
                tasks, createUi(new ByteArrayOutputStream()), createFailingStorage()));

        assertEquals(3, tasks.size());
        assertSame(firstTask, tasks.get(0));
        assertSame(secondTask, tasks.get(1));
        assertSame(thirdTask, tasks.get(2));
    }

    /** Verifies successful mark and unmark commands update and persist completion state. */
    @Test
    public void markAndUnmarkCommands_validIndex_stateSavedAndShown() throws BobbyException {
        Task task = new ToDo("read book");
        TaskList tasks = new TaskList(List.of(task));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);
        Storage storage = createStorage("status.txt");

        new MarkCommand("mark 1").execute(tasks, ui, storage);

        assertTrue(task.isDone());
        assertTrue(storage.load().get(0).isDone());
        assertTrue(outputText(output).contains("Woohoo! This task is officially done:"));

        new UnmarkCommand("unmark 1").execute(tasks, ui, storage);

        assertFalse(task.isDone());
        assertFalse(storage.load().get(0).isDone());
        assertTrue(outputText(output).contains("Plot twist! This task is back in action:"));
    }

    /** Verifies a failed mark save restores an originally incomplete task. */
    @Test
    public void markCommand_saveFails_incompleteStateRestored() {
        Task task = new ToDo("read book");
        TaskList tasks = new TaskList(List.of(task));

        assertThrows(BobbyException.class, () -> new MarkCommand("mark 1").execute(
                tasks, createUi(new ByteArrayOutputStream()), createFailingStorage()));

        assertFalse(task.isDone());
    }

    /** Verifies a failed unmark save restores an originally complete task. */
    @Test
    public void unmarkCommand_saveFails_completeStateRestored() {
        Task task = new ToDo("read book");
        task.markAsDone();
        TaskList tasks = new TaskList(List.of(task));

        assertThrows(BobbyException.class, () -> new UnmarkCommand("unmark 1").execute(
                tasks, createUi(new ByteArrayOutputStream()), createFailingStorage()));

        assertTrue(task.isDone());
    }

    /** Verifies that read-only and exit commands delegate to the UI without changing tasks. */
    @Test
    public void readOnlyAndExitCommands_execute_expectedOutputAndExitFlagsReturned() throws BobbyException {
        Task task = new ToDo("read book");
        TaskList tasks = new TaskList(List.of(task));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);
        Storage storage = createStorage("read-only.txt");
        Command listCommand = new ListCommand();
        Command findCommand = new FindCommand("book");
        Command exitCommand = new ExitCommand();

        listCommand.execute(tasks, ui, storage);
        findCommand.execute(tasks, ui, storage);
        exitCommand.execute(tasks, ui, storage);

        assertFalse(listCommand.isExit());
        assertFalse(findCommand.isExit());
        assertTrue(exitCommand.isExit());
        assertEquals(1, tasks.size());
        assertTrue(outputText(output).contains("Ta-da! Here's your task parade:"));
        assertTrue(outputText(output).contains("Detective Bobby found these matching tasks:"));
        assertTrue(outputText(output).contains("Toodles! Bobby is off to recharge the silly batteries."));
    }

    /** Creates a UI whose output can be inspected by a test. */
    private Ui createUi(ByteArrayOutputStream output) {
        return new Ui(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    /** Creates storage within this test's temporary directory. */
    private Storage createStorage(String filename) {
        return new Storage(temporaryDirectory.resolve(filename).toString());
    }

    /** Creates storage that fails every save operation. */
    private Storage createFailingStorage() {
        return new FailingStorage(temporaryDirectory.resolve("failing.txt").toString());
    }

    /** Decodes captured UTF-8 output. */
    private String outputText(ByteArrayOutputStream output) {
        return output.toString(StandardCharsets.UTF_8);
    }

    /** Storage implementation used to exercise command rollback paths. */
    private static class FailingStorage extends Storage {
        FailingStorage(String filePath) {
            super(filePath);
        }

        @Override
        public void save(TaskList tasks) throws BobbyException {
            throw new BobbyException("Simulated save failure.");
        }
    }
}
