package milo.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

class MarkCommandTest {
    @TempDir
    Path tempDir;

    private Storage newStorage() {
        return new Storage(tempDir.resolve("milo.txt").toString());
    }

    @Test
    void execute_markTrue_setsTaskDone() throws MiloException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        new MarkCommand("1", true).execute(tasks, new Ui(), newStorage());
        assertEquals("[T][X] read book", tasks.get(0).toString());
    }

    @Test
    void execute_markFalse_setsTaskNotDone() throws MiloException {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        tasks.add(todo);
        new MarkCommand("1", false).execute(tasks, new Ui(), newStorage());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    void execute_invalidNumber_throwsAndLeavesListUnchanged() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertThrows(MiloException.class, () ->
                new MarkCommand("abc", true).execute(tasks, new Ui(), newStorage()));
        assertEquals("[T][ ] read book", tasks.asArrayList().get(0).toString());
    }

    @Test
    void execute_outOfRangeNumber_throwsMiloException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertThrows(MiloException.class, () ->
                new MarkCommand("5", true).execute(tasks, new Ui(), newStorage()));
    }

    @Test
    void execute_onlyMarksTheRequestedTask() throws MiloException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        tasks.add(new Todo("b"));
        new MarkCommand("1", true).execute(tasks, new Ui(), newStorage());
        assertEquals("[T][X] a", tasks.get(0).toString());
        assertEquals("[T][ ] b", tasks.get(1).toString());
    }

    @Test
    void execute_isNotExitCommand() {
        assertTrue(!new MarkCommand("1", true).isExit());
    }
}
