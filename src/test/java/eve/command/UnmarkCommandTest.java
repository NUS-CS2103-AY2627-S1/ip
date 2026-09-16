package eve.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import eve.EveException;
import eve.Storage;
import eve.Ui;
import eve.task.TaskList;
import eve.task.ToDo;

/** Tests {@link UnmarkCommand}: marking a task not done by its 1-based number. */
public class UnmarkCommandTest {
    @TempDir
    private Path tempDir;

    private Storage newStorage() {
        return new Storage(tempDir.resolve("eve.txt").toString());
    }

    @Test
    public void execute_validNumber_marksThatTaskNotDoneAndSaves() throws EveException {
        TaskList tasks = new TaskList();
        ToDo task = new ToDo("read book");
        task.markAsDone();
        tasks.add(task);
        Storage storage = newStorage();

        new UnmarkCommand(1).execute(tasks, new Ui(), storage);

        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("[T][ ] read book", storage.load().get(0).toString());
    }

    @Test
    public void execute_outOfRangeNumber_throws() {
        TaskList tasks = new TaskList();

        assertThrows(EveException.class, () -> new UnmarkCommand(1).execute(tasks, new Ui(), newStorage()));
    }
}
