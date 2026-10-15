package duncan.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import duncan.DuncanException;
import duncan.Storage;
import duncan.Ui;
import duncan.task.TaskList;
import duncan.task.Todo;

public class DeleteCommandTest {
    @TempDir
    Path tempDir;

    private Storage newStorage() {
        return new Storage(tempDir.resolve("tasks.txt").toString());
    }

    @Test
    public void execute_validIndex_taskRemovedAndSizeDecreases() throws DuncanException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));

        new DeleteCommand(0).execute(tasks, new Ui(), newStorage());

        assertEquals(1, tasks.size());
        assertEquals("second", tasks.get(0).getDescription());
    }

    @Test
    public void execute_indexOutOfRange_exceptionThrownAndListUnchanged() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        DeleteCommand command = new DeleteCommand(5);
        Storage storage = newStorage();
        Ui ui = new Ui();

        DuncanException e = assertThrows(DuncanException.class, () -> command.execute(tasks, ui, storage));

        assertEquals("HEY! this task number is bad", e.getMessage());
        assertEquals(1, tasks.size());
    }
}
