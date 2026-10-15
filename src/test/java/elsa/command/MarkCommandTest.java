package elsa.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import elsa.ElsaException;
import elsa.storage.Storage;
import elsa.task.TaskList;
import elsa.task.Todo;
import elsa.ui.Ui;

/** Tests {@link MarkCommand}, which records a task as done. */
public class MarkCommandTest {

    private static Storage storeIn(Path folder) {
        return new Storage(folder.resolve("tasks.txt").toString());
    }

    private static TaskList twoTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("buy milk"));
        return tasks;
    }

    /**
     * The task marked is the one the user numbered, counting from 1. An
     * off-by-one here marks the wrong task and still reports success.
     */
    @Test
    public void execute_theSecondTask_marksThatOneAndConfirms(@TempDir Path folder)
            throws ElsaException {
        TaskList tasks = twoTasks();

        String reply = new MarkCommand(2).execute(tasks, new Ui(), storeIn(folder));

        assertEquals("[T][ ] read book", tasks.get(0).toString(), "the wrong task was marked");
        assertEquals("[T][X] buy milk", tasks.get(1).toString());
        assertEquals("Let it go! I've marked this task as done:\n  [T][X] buy milk", reply);
    }

    @Test
    public void execute_aTask_writesTheChangeToTheStore(@TempDir Path folder) throws ElsaException {
        TaskList tasks = twoTasks();
        Storage storage = storeIn(folder);

        new MarkCommand(1).execute(tasks, new Ui(), storage);

        TaskList reloaded = storage.load().tasks();
        assertEquals("[T][X] read book", reloaded.get(0).toString(),
                "the task came back from the disk still undone");
    }

    /** Marking something already done leaves it done rather than turning it back. */
    @Test
    public void execute_taskAlreadyDone_leavesItDone(@TempDir Path folder) throws ElsaException {
        TaskList tasks = twoTasks();
        Storage storage = storeIn(folder);
        new MarkCommand(1).execute(tasks, new Ui(), storage);

        new MarkCommand(1).execute(tasks, new Ui(), storage);

        assertEquals("[T][X] read book", tasks.get(0).toString());
    }
}
