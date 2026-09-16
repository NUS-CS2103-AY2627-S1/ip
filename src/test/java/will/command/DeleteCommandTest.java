package will.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import will.Storage;
import will.TaskList;
import will.Ui;
import will.WillException;
import will.task.Todo;

public class DeleteCommandTest {

    @Test
    public void execute_validIndex_removesTheTaskAndShrinksTheList(@TempDir Path tempDir) throws WillException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("buy milk"));
        Storage storage = new Storage(tempDir.resolve("will.txt"));

        new DeleteCommand(0).execute(tasks, new Ui(), storage);

        assertEquals(1, tasks.size());
        assertEquals("buy milk", tasks.get(0).getDescription());
    }

    @Test
    public void execute_afterDeleting_storageNoLongerHasTheTask(@TempDir Path tempDir) throws WillException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Storage storage = new Storage(tempDir.resolve("will.txt"));

        new DeleteCommand(0).execute(tasks, new Ui(), storage);

        assertEquals(0, storage.load(new Ui()).size());
    }

    @Test
    public void execute_indexPastEndOfList_throwsWillException(@TempDir Path tempDir) {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Storage storage = new Storage(tempDir.resolve("will.txt"));

        assertThrows(WillException.class, () -> new DeleteCommand(1).execute(tasks, new Ui(), storage));
    }
}
