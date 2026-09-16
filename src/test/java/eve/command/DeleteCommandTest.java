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

/** Tests {@link DeleteCommand}: removing a task by its 1-based number. */
public class DeleteCommandTest {
    @TempDir
    private Path tempDir;

    private Storage newStorage() {
        return new Storage(tempDir.resolve("eve.txt").toString());
    }

    @Test
    public void execute_validNumber_removesThatTaskAndSaves() throws EveException {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        tasks.add(new ToDo("return book"));
        Storage storage = newStorage();

        new DeleteCommand(1).execute(tasks, new Ui(), storage);

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] return book", tasks.get(0).toString());
        assertEquals(1, storage.load().size());
    }

    @Test
    public void execute_outOfRangeNumber_throwsAndLeavesListUntouched() {
        TaskList tasks = new TaskList();

        assertThrows(EveException.class, () -> new DeleteCommand(1).execute(tasks, new Ui(), newStorage()));
        assertEquals(0, tasks.size());
    }
}
