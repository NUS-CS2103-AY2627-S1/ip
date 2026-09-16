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

/** Tests {@link MarkCommand}: marking a task done by its 1-based number. */
public class MarkCommandTest {
    @TempDir
    private Path tempDir;

    private Storage newStorage() {
        return new Storage(tempDir.resolve("eve.txt").toString());
    }

    @Test
    public void execute_validNumber_marksThatTaskDoneAndSaves() throws EveException {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        Storage storage = newStorage();

        new MarkCommand(1).execute(tasks, new Ui(), storage);

        assertEquals("[T][X] read book", tasks.get(0).toString());
        assertEquals("[T][X] read book", storage.load().get(0).toString());
    }

    @Test
    public void execute_outOfRangeNumber_throws() {
        TaskList tasks = new TaskList();

        assertThrows(EveException.class, () -> new MarkCommand(1).execute(tasks, new Ui(), newStorage()));
    }
}
