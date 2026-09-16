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

public class UnmarkCommandTest {

    @Test
    public void execute_alreadyDoneTask_marksItNotDone(@TempDir Path tempDir) throws WillException {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        tasks.add(todo);
        Storage storage = new Storage(tempDir.resolve("will.txt"));

        new UnmarkCommand(0).execute(tasks, new Ui(), storage);

        assertEquals(" ", tasks.get(0).getStatusIcon());
    }

    @Test
    public void execute_indexPastEndOfList_throwsWillException(@TempDir Path tempDir) {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Storage storage = new Storage(tempDir.resolve("will.txt"));

        assertThrows(WillException.class, () -> new UnmarkCommand(1).execute(tasks, new Ui(), storage));
    }
}
