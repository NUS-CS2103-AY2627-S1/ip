package will.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import will.Storage;
import will.TaskList;
import will.Ui;
import will.WillException;
import will.task.Event;
import will.task.Todo;

public class AddCommandTest {

    @Test
    public void execute_singleTask_addsItToTheList(@TempDir Path tempDir) throws WillException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("will.txt"));
        Todo todo = new Todo("read book");

        new AddCommand(todo).execute(tasks, new Ui(), storage);

        assertEquals(1, tasks.size());
        assertEquals(todo, tasks.get(0));
    }

    @Test
    public void execute_afterAdding_taskIsPersistedToStorage(@TempDir Path tempDir) throws WillException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("will.txt"));

        new AddCommand(new Todo("read book")).execute(tasks, new Ui(), storage);

        TaskList reloaded = new TaskList(storage.load(new Ui()));
        assertEquals(1, reloaded.size());
        assertEquals("read book", reloaded.get(0).getDescription());
    }

    @Test
    public void execute_nonOverlappingEvent_doesNotThrowOrRemoveAnyTask(@TempDir Path tempDir) throws WillException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("will.txt"));
        new AddCommand(new Event("a", "2019-10-01", "2019-10-02")).execute(tasks, new Ui(), storage);

        new AddCommand(new Event("b", "2019-11-01", "2019-11-02")).execute(tasks, new Ui(), storage);

        assertEquals(2, tasks.size());
    }
}
