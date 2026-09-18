package milo.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import milo.exception.MiloException;
import milo.storage.Storage;
import milo.task.TaskList;
import milo.task.Todo;
import milo.ui.Ui;

class UndoCommandTest {
    @TempDir
    Path tempDir;

    private Storage newStorage() {
        return new Storage(tempDir.resolve("milo.txt").toString());
    }

    @Test
    void execute_afterAdd_removesTheAddedTask() throws MiloException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("already here"));

        tasks.saveSnapshot();
        new AddCommand(new Todo("read book")).execute(tasks, new Ui(), newStorage());
        assertEquals(2, tasks.size());

        new UndoCommand().execute(tasks, new Ui(), newStorage());
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] already here", tasks.get(0).toString());
    }

    @Test
    void execute_afterDelete_bringsTheTaskBack() throws MiloException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        tasks.add(new Todo("b"));

        tasks.saveSnapshot();
        new DeleteCommand("1").execute(tasks, new Ui(), newStorage());
        assertEquals(1, tasks.size());

        new UndoCommand().execute(tasks, new Ui(), newStorage());
        assertEquals(2, tasks.size());
        assertEquals("[T][ ] a", tasks.get(0).toString());
        assertEquals("[T][ ] b", tasks.get(1).toString());
    }

    @Test
    void execute_afterMark_returnsTaskToNotDone() throws MiloException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        tasks.saveSnapshot();
        new MarkCommand("1", true).execute(tasks, new Ui(), newStorage());
        assertEquals("[T][X] read book", tasks.get(0).toString());

        new UndoCommand().execute(tasks, new Ui(), newStorage());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    void execute_nothingChangedYet_throwsMiloException() {
        TaskList tasks = new TaskList();
        assertThrows(MiloException.class, () -> new UndoCommand().execute(tasks, new Ui(), newStorage()));
    }

    @Test
    void execute_calledTwice_secondCallThrows() throws MiloException {
        TaskList tasks = new TaskList();
        tasks.saveSnapshot();
        new AddCommand(new Todo("read book")).execute(tasks, new Ui(), newStorage());

        new UndoCommand().execute(tasks, new Ui(), newStorage());
        assertThrows(MiloException.class, () -> new UndoCommand().execute(tasks, new Ui(), newStorage()));
    }

    @Test
    void execute_writesTheRestoredListToDisk() throws Exception {
        TaskList tasks = new TaskList();
        Storage storage = newStorage();
        tasks.saveSnapshot();
        new AddCommand(new Todo("read book")).execute(tasks, new Ui(), storage);

        new UndoCommand().execute(tasks, new Ui(), storage);
        assertEquals("", java.nio.file.Files.readString(tempDir.resolve("milo.txt")).trim());
    }

    @Test
    void isUndoable_undoItself_isNotUndoable() {
        assertFalse(new UndoCommand().isUndoable());
    }

    @Test
    void isUndoable_listChangingCommands_areUndoable() {
        assertTrue(new AddCommand(new Todo("a")).isUndoable());
        assertTrue(new DeleteCommand("1").isUndoable());
        assertTrue(new MarkCommand("1", true).isUndoable());
    }

    @Test
    void isUndoable_readOnlyCommands_areNotUndoable() {
        assertFalse(new ListCommand().isUndoable());
        assertFalse(new FindCommand("book").isUndoable());
        assertFalse(new ExitCommand().isUndoable());
    }

    @Test
    void isExit_undoCommand_doesNotEndTheSession() {
        assertFalse(new UndoCommand().isExit());
    }
}
