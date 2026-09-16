package eve.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import eve.EveException;
import eve.Storage;
import eve.Ui;
import eve.task.Deadline;
import eve.task.TaskList;
import eve.task.ToDo;

/** Tests {@link AddCommand}: adding a task, and rejecting an exact duplicate. */
public class AddCommandTest {
    @TempDir
    private Path tempDir;

    private Storage newStorage() {
        return new Storage(tempDir.resolve("eve.txt").toString());
    }

    @Test
    public void execute_newTask_addsItAndSavesIt() throws EveException {
        TaskList tasks = new TaskList();
        Storage storage = newStorage();

        new AddCommand(new ToDo("read book")).execute(tasks, new Ui(), storage);

        assertEquals(1, tasks.size());
        assertEquals(1, storage.load().size());
    }

    @Test
    public void execute_exactDuplicate_throwsAndDoesNotAddASecondCopy() throws EveException {
        TaskList tasks = new TaskList();
        Storage storage = newStorage();
        new AddCommand(new ToDo("read book")).execute(tasks, new Ui(), storage);

        EveException exception = assertThrows(EveException.class, () ->
                new AddCommand(new ToDo("read book")).execute(tasks, new Ui(), storage));

        assertTrue(exception.getMessage().contains("already"));
        assertEquals(1, tasks.size());
    }

    @Test
    public void execute_sameDescriptionDifferentTypeIsNotADuplicate() throws EveException {
        TaskList tasks = new TaskList();
        Storage storage = newStorage();
        new AddCommand(new ToDo("read book")).execute(tasks, new Ui(), storage);

        new AddCommand(new Deadline("read book", LocalDate.of(2020, 1, 1))).execute(tasks, new Ui(), storage);

        assertEquals(2, tasks.size());
    }
}
