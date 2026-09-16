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

public class MarkCommandTest {

    @Test
    public void execute_validIndex_marksTheTaskDone(@TempDir Path tempDir) throws WillException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Storage storage = new Storage(tempDir.resolve("will.txt"));

        new MarkCommand(0).execute(tasks, new Ui(), storage);

        assertEquals("X", tasks.get(0).getStatusIcon());
    }

    @Test
    public void execute_indexPastEndOfList_throwsWillException(@TempDir Path tempDir) {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Storage storage = new Storage(tempDir.resolve("will.txt"));

        assertThrows(WillException.class, () -> new MarkCommand(5).execute(tasks, new Ui(), storage));
    }

    @Test
    public void execute_negativeIndex_throwsWillException(@TempDir Path tempDir) {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Storage storage = new Storage(tempDir.resolve("will.txt"));

        assertThrows(WillException.class, () -> new MarkCommand(-1).execute(tasks, new Ui(), storage));
    }

    @Test
    public void execute_indexPastEndOfList_leavesListUnchanged(@TempDir Path tempDir) {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Storage storage = new Storage(tempDir.resolve("will.txt"));

        assertThrows(WillException.class, () -> new MarkCommand(5).execute(tasks, new Ui(), storage));

        assertEquals(1, tasks.size());
        assertEquals(" ", tasks.get(0).getStatusIcon());
    }
}
