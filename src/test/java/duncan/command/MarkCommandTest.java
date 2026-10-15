package duncan.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import duncan.DuncanException;
import duncan.Storage;
import duncan.Ui;
import duncan.task.TaskList;
import duncan.task.Todo;

public class MarkCommandTest {
    @TempDir
    Path tempDir;

    private Storage newStorage() {
        return new Storage(tempDir.resolve("tasks.txt").toString());
    }

    @Test
    public void execute_validIndex_taskMarkedDone() throws DuncanException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        new MarkCommand(0).execute(tasks, new Ui(), newStorage());

        assertEquals("X", tasks.get(0).getStatusIcon());
    }

    @Test
    public void execute_indexOutOfRange_exceptionThrownAndTaskUnchanged() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        MarkCommand command = new MarkCommand(5);
        Storage storage = newStorage();
        Ui ui = new Ui();

        DuncanException e = assertThrows(DuncanException.class, () -> command.execute(tasks, ui, storage));

        assertEquals("HEY! this task number is bad", e.getMessage());
        assertEquals(" ", tasks.get(0).getStatusIcon());
    }

    @Test
    public void execute_taskAlreadyDone_noticeShownAndNothingSaved() throws DuncanException {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        tasks.add(todo);
        Ui ui = new Ui();
        Storage storage = newStorage();

        new MarkCommand(0).execute(tasks, ui, storage);

        assertEquals("Task 1 is already marked as done" + System.lineSeparator(), ui.flushOutput());
        assertEquals("X", tasks.get(0).getStatusIcon());
        assertTrue(storage.load().isEmpty());
    }
}
