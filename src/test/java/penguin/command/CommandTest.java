package penguin.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import penguin.exception.PenguinException;
import penguin.parser.Parser;
import penguin.storage.Storage;
import penguin.task.Task;
import penguin.task.TaskList;
import penguin.task.Todo;
import penguin.ui.Ui;

/**
 * Tests command execution, persistence, and transactional recovery.
 */
class CommandTest {
    @Test
    void parseExit_validCommand_returnsExitCommand() throws Exception {
        Command command = Parser.parseCommand("bye");

        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.isExit());
    }

    @Test
    void parseList_validCommand_returnsNonExitCommand() throws Exception {
        Command command = Parser.parseCommand("list");

        assertInstanceOf(ListCommand.class, command);
        assertFalse(command.isExit());
    }

    @Test
    void addCommand_validTask_addsAndPersistsIt(@TempDir Path tempDir)
            throws Exception {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(
                tempDir.resolve("data/tasks.txt").toString());
        AddCommand command = new AddCommand(new Todo("read book"));

        command.execute(tasks, new Ui(), storage);

        assertEquals(1, tasks.size());
        assertEquals("T | 0 | read book", storage.loadTaskLines().get(0));
    }

    @Test
    void addCommand_saveFails_restoresTaskStateAndHistory(
            @TempDir Path tempDir) {
        TaskList tasks = new TaskList();

        assertThrows(PenguinException.class,
                () -> new AddCommand(new Todo("read book")).execute(
                        tasks, new Ui(false), new Storage(tempDir.toString())));

        assertEquals(0, tasks.size());
        assertThrows(IllegalStateException.class, tasks::undo);
    }

    @Test
    void isExit_exitCommand_returnsTrue() {
        assertTrue(new ExitCommand().isExit());
    }

    @Test
    void undoCommand_markedTask_restoresAndPersistsPreviousState(
            @TempDir Path tempDir) throws Exception {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));
        Storage storage = new Storage(
                tempDir.resolve("data/tasks.txt").toString());
        new MarkCommand(0).execute(tasks, new Ui(), storage);

        new UndoCommand().execute(tasks, new Ui(), storage);

        assertEquals("T | 0 | read book", storage.loadTaskLines().get(0));
    }

    @Test
    void deleteCommand_validIndex_removesAndPersistsChange(
            @TempDir Path tempDir) throws Exception {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));
        Storage storage = new Storage(
                tempDir.resolve("data/tasks.txt").toString());

        new DeleteCommand(0).execute(tasks, new Ui(), storage);

        assertEquals(0, tasks.size());
        assertTrue(storage.loadTaskLines().isEmpty());
    }

    @Test
    void deleteCommand_saveFails_restoresTaskStateAndHistory(
            @TempDir Path tempDir) {
        TaskList tasks = new TaskList();
        tasks.addLoadedTask(new Todo("read book"));

        assertThrows(PenguinException.class,
                () -> new DeleteCommand(0).execute(
                        tasks, new Ui(false), new Storage(tempDir.toString())));

        assertEquals("[T][ ] read book", tasks.getTasks().get(0).toString());
        assertThrows(IllegalStateException.class, tasks::undo);
    }

    @Test
    void deleteCommand_invalidIndex_throwsAndPreservesState(
            @TempDir Path tempDir) throws Exception {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));
        Storage storage = new Storage(
                tempDir.resolve("data/tasks.txt").toString());
        storage.saveTaskLines(tasks.toStorageLines());

        assertThrows(PenguinException.class,
                () -> new DeleteCommand(1).execute(tasks, new Ui(), storage));

        assertEquals(1, tasks.size());
        assertEquals("T | 0 | read book", storage.loadTaskLines().get(0));
    }

    @Test
    void markAndUnmarkCommands_validIndex_updateAndPersistState(
            @TempDir Path tempDir) throws Exception {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));
        Storage storage = new Storage(
                tempDir.resolve("data/tasks.txt").toString());

        new MarkCommand(0).execute(tasks, new Ui(), storage);
        assertEquals("T | 1 | read book", storage.loadTaskLines().get(0));

        new UnmarkCommand(0).execute(tasks, new Ui(), storage);
        assertEquals("T | 0 | read book", storage.loadTaskLines().get(0));
    }

    @Test
    void markAndUnmarkCommands_invalidIndex_throwAndPreserveState(
            @TempDir Path tempDir) throws Exception {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));
        Storage storage = new Storage(
                tempDir.resolve("data/tasks.txt").toString());
        storage.saveTaskLines(tasks.toStorageLines());

        assertThrows(PenguinException.class,
                () -> new MarkCommand(-1).execute(tasks, new Ui(), storage));
        assertThrows(PenguinException.class,
                () -> new UnmarkCommand(1).execute(tasks, new Ui(), storage));

        assertEquals("T | 0 | read book", storage.loadTaskLines().get(0));
        assertEquals(" ", tasks.getTasks().get(0).getStatus());
    }

    @Test
    void markCommand_saveFails_restoresTaskState(@TempDir Path tempDir) {
        TaskList tasks = new TaskList();
        tasks.addLoadedTask(new Todo("read book"));

        assertThrows(PenguinException.class,
                () -> new MarkCommand(0).execute(
                        tasks, new Ui(false), new Storage(tempDir.toString())));

        assertEquals(" ", tasks.getTasks().get(0).getStatus());
        assertThrows(IllegalStateException.class, tasks::undo);
    }

    @Test
    void unmarkCommand_saveFails_restoresTaskState(@TempDir Path tempDir) {
        Task completedTask = new Todo("read book");
        completedTask.markDone();
        TaskList tasks = new TaskList();
        tasks.addLoadedTask(completedTask);

        assertThrows(PenguinException.class,
                () -> new UnmarkCommand(0).execute(
                        tasks, new Ui(false), new Storage(tempDir.toString())));

        assertEquals("X", tasks.getTasks().get(0).getStatus());
        assertThrows(IllegalStateException.class, tasks::undo);
    }

    @Test
    void undoCommand_saveFails_restoresTaskStateAndHistory(
            @TempDir Path tempDir) {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));
        tasks.markTask(0);

        assertThrows(PenguinException.class,
                () -> new UndoCommand().execute(
                        tasks, new Ui(false), new Storage(tempDir.toString())));

        assertEquals("X", tasks.getTasks().get(0).getStatus());
        tasks.undo();
        assertEquals(" ", tasks.getTasks().get(0).getStatus());
    }

}
