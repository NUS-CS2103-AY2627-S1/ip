package jason.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jason.exception.InvalidIndexException;
import jason.storage.Storage;
import jason.task.TaskList;
import jason.task.ToDo;
import jason.ui.Ui;

/** Tests the state changes and exit behavior of executable commands. */
class CommandTest {
    @TempDir
    Path temporaryDirectory;

    private Storage storage() {
        return new Storage(temporaryDirectory.resolve("tasks.txt").toString());
    }

    @Test
    void addCommand_execute_addsTaskAndPersistsIt() {
        TaskList tasks = new TaskList();
        Storage storage = storage();

        new AddCommand(new ToDo("read book")).execute(tasks, new Ui(), storage);

        assertTrue(tasks.isValidIndex(1));
        assertTrue(storage.load().get(0).getDescription().equals("read book"));
    }

    @Test
    void markAndUnmarkCommands_execute_changeCompletionState() throws InvalidIndexException {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        Storage storage = storage();

        new MarkCommand(1).execute(tasks, new Ui(), storage);
        assertTrue(tasks.get(1).isCompleted());

        new UnmarkCommand(1).execute(tasks, new Ui(), storage);
        assertFalse(tasks.get(1).isCompleted());
    }

    @Test
    void deleteCommand_execute_removesTask() throws InvalidIndexException {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        new DeleteCommand(1).execute(tasks, new Ui(), storage());

        assertTrue(tasks.size() == 0);
    }

    @Test
    void commands_invalidIndex_throwInvalidIndexException() {
        TaskList tasks = new TaskList();
        Storage storage = storage();

        assertThrows(InvalidIndexException.class, () ->
                new MarkCommand(1).execute(tasks, new Ui(), storage));
        assertThrows(InvalidIndexException.class, () ->
                new UnmarkCommand(1).execute(tasks, new Ui(), storage));
        assertThrows(InvalidIndexException.class, () ->
                new DeleteCommand(1).execute(tasks, new Ui(), storage));
    }

    @Test
    void listCommand_execute_doesNotChangeTaskList() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        new ListCommand().execute(tasks, new Ui(), storage());

        assertTrue(tasks.size() == 1);
    }

    @Test
    void exitCommand_isExit_returnsTrue() {
        assertTrue(new ExitCommand().isExit());
        assertFalse(new ListCommand().isExit());
    }

    @Test
    void findCommand_execute_doesNotChangeTaskList() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        new FindCommand("book").execute(tasks, new Ui(), storage());

        assertTrue(tasks.size() == 1);
    }

    @Test
    void findCommand_blankKeyword_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new FindCommand(" "));
    }
}
