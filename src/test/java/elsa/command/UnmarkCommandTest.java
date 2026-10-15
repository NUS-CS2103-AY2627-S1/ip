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

/** Tests {@link UnmarkCommand}, which records a task as not done after all. */
public class UnmarkCommandTest {

    private static Storage storeIn(Path folder) {
        return new Storage(folder.resolve("tasks.txt").toString());
    }

    @Test
    public void execute_aTaskThatWasDone_putsItBackAndConfirms(@TempDir Path folder)
            throws ElsaException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Storage storage = storeIn(folder);
        new MarkCommand(1).execute(tasks, new Ui(), storage);

        String reply = new UnmarkCommand(1).execute(tasks, new Ui(), storage);

        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("Back into the cold. I've marked this task as not done yet:\n"
                + "  [T][ ] read book", reply);
    }

    @Test
    public void execute_aTask_writesTheChangeToTheStore(@TempDir Path folder) throws ElsaException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Storage storage = storeIn(folder);
        new MarkCommand(1).execute(tasks, new Ui(), storage);

        new UnmarkCommand(1).execute(tasks, new Ui(), storage);

        TaskList reloaded = storage.load().tasks();
        assertEquals("[T][ ] read book", reloaded.get(0).toString(),
                "the task came back from the disk still done");
    }

    /** Unmarking something already not done leaves it alone rather than toggling it. */
    @Test
    public void execute_taskAlreadyNotDone_leavesItNotDone(@TempDir Path folder)
            throws ElsaException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        new UnmarkCommand(1).execute(tasks, new Ui(), storeIn(folder));

        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }
}
