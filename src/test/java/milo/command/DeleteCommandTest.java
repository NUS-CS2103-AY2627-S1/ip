package milo.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import milo.exception.MiloException;
import milo.storage.Storage;
import milo.task.TaskList;
import milo.task.Todo;
import milo.ui.Ui;

class DeleteCommandTest {
    @TempDir
    Path tempDir;

    private Storage newStorage() {
        return new Storage(tempDir.resolve("milo.txt").toString());
    }

    @Test
    void execute_validNumber_removesThatTask() throws MiloException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        tasks.add(new Todo("b"));
        new DeleteCommand("1").execute(tasks, new Ui(), newStorage());
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] b", tasks.get(0).toString());
    }

    @Test
    void execute_lastTask_listBecomesEmpty() throws MiloException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        new DeleteCommand("1").execute(tasks, new Ui(), newStorage());
        assertEquals(0, tasks.size());
    }

    @Test
    void execute_invalidNumber_throwsAndLeavesListUnchanged() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        assertThrows(MiloException.class, () ->
                new DeleteCommand("xyz").execute(tasks, new Ui(), newStorage()));
        assertEquals(1, tasks.size());
    }

    @Test
    void execute_onEmptyList_throwsMiloException() {
        TaskList tasks = new TaskList();
        assertThrows(MiloException.class, () ->
                new DeleteCommand("1").execute(tasks, new Ui(), newStorage()));
    }
}
