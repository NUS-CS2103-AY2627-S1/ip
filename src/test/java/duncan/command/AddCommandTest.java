package duncan.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import duncan.DuncanException;
import duncan.Storage;
import duncan.Ui;
import duncan.task.Task;
import duncan.task.TaskList;
import duncan.task.Todo;

public class AddCommandTest {
    @TempDir
    Path tempDir;

    @Test
    public void execute_addsGivenTask_taskAppearsInList() throws DuncanException {
        TaskList tasks = new TaskList();
        Task task = new Todo("read book");
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString());

        new AddCommand(task).execute(tasks, new Ui(), storage);

        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(0));
    }

    @Test
    public void execute_duplicateTask_exceptionThrownAndListUnchanged() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        AddCommand command = new AddCommand(new Todo("Read  Book"));
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString());
        Ui ui = new Ui();

        DuncanException e = assertThrows(DuncanException.class, () -> command.execute(tasks, ui, storage));

        assertEquals("HEY! this task is already in your list", e.getMessage());
        assertEquals(1, tasks.size());
    }
}
