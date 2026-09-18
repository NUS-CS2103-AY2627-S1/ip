package duck.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import duck.DuckException;
import duck.storage.Storage;
import duck.task.Task;
import duck.task.TaskList;
import duck.task.Todo;
import duck.ui.Ui;

/**
 * Tests task addition and rollback behavior provided by {@link AddCommand}.
 */
public class AddCommandTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void constructor_nullTask_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddCommand(null));
    }

    @Test
    public void execute_validTask_addsAndPersistsTask() throws DuckException, IOException {
        Task existingTask = new Todo("existing");
        Task addedTask = new Todo("added");
        TaskList tasks = new TaskList(List.of(existingTask));
        Path dataFile = this.temporaryDirectory.resolve("duck.txt");

        new AddCommand(addedTask).execute(tasks, new Ui(), new Storage(dataFile.toString()));

        assertEquals(2, tasks.getTaskCount());
        assertSame(existingTask, tasks.get(0));
        assertSame(addedTask, tasks.get(1));
        assertEquals(List.of("T | 0 | existing", "T | 0 | added"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    public void execute_duplicateTask_throwsDuckExceptionWithoutChangingTaskList() {
        Task existingTask = new Todo("read book");
        existingTask.markAsDone();
        TaskList tasks = new TaskList(List.of(existingTask));
        AddCommand command = new AddCommand(new Todo("read book"));

        DuckException exception = assertThrows(DuckException.class, () ->
                command.execute(tasks, new Ui(), new FailingStorage()));

        assertEquals("That task already exists as task 1.", exception.getMessage());
        assertEquals(List.of(existingTask), tasks.getTasksSnapshot());
    }

    @Test
    public void execute_saveFails_rollsBackAddedTask() {
        Task existingTask = new Todo("existing");
        TaskList tasks = new TaskList(List.of(existingTask));
        AddCommand command = new AddCommand(new Todo("unsaved"));

        DuckException exception = assertThrows(DuckException.class, () ->
                command.execute(tasks, new Ui(), new FailingStorage()));

        assertEquals("Simulated save failure.", exception.getMessage());
        assertEquals(List.of(existingTask), tasks.getTasksSnapshot());
    }
}
