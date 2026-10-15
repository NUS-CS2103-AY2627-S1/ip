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

/** Tests {@link DeleteCommand}, which takes one task out of the list. */
public class DeleteCommandTest {

    private static Storage storeIn(Path folder) {
        return new Storage(folder.resolve("tasks.txt").toString());
    }

    /** A list of three todos, named so that positions can be told apart. */
    private static TaskList threeTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        tasks.add(new Todo("third"));
        return tasks;
    }

    /**
     * Deleting from the middle has to close the gap it leaves, or the tasks
     * after it keep numbers that no longer point at them.
     */
    @Test
    public void execute_taskInTheMiddle_removesItAndClosesTheGap(@TempDir Path folder)
            throws ElsaException {
        TaskList tasks = threeTasks();

        String reply = new DeleteCommand(2).execute(tasks, new Ui(), storeIn(folder));

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] first", tasks.get(0).toString());
        assertEquals("[T][ ] third", tasks.get(1).toString(), "the gap was not closed");
        assertEquals("Melted away. I've removed this task:\n"
                + "  [T][ ] second\n"
                + "Now you have 2 tasks in the list.", reply);
    }

    @Test
    public void execute_theOnlyTask_leavesAnEmptyList(@TempDir Path folder) throws ElsaException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        String reply = new DeleteCommand(1).execute(tasks, new Ui(), storeIn(folder));

        assertEquals(0, tasks.size());
        assertEquals("Melted away. I've removed this task:\n"
                + "  [T][ ] read book\n"
                + "Now you have 0 tasks in the list.", reply);
    }

    @Test
    public void execute_anyDeletion_writesTheChangeToTheStore(@TempDir Path folder)
            throws ElsaException {
        TaskList tasks = threeTasks();
        Storage storage = storeIn(folder);

        new DeleteCommand(1).execute(tasks, new Ui(), storage);

        TaskList reloaded = storage.load().tasks();
        assertEquals(2, reloaded.size(), "the deletion never reached the disk");
        assertEquals("[T][ ] second", reloaded.get(0).toString());
    }
}
