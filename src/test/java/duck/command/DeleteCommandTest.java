package duck.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
 * Tests task deletion, validation, and rollback behavior provided by {@link DeleteCommand}.
 */
public class DeleteCommandTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void execute_validTaskNumber_deletesAndPersistsSelectedTask()
            throws DuckException, IOException {
        Task firstTask = new Todo("first");
        Task secondTask = new Todo("second");
        Task thirdTask = new Todo("third");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask, thirdTask));
        Path dataFile = this.temporaryDirectory.resolve("duck.txt");

        new DeleteCommand(2).execute(tasks, new Ui(), new Storage(dataFile.toString()));

        assertEquals(List.of(firstTask, thirdTask), tasks.getTasksSnapshot());
        assertEquals(List.of("T | 0 | first", "T | 0 | third"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    public void execute_taskNumberOutsideList_throwsWithoutChangingTasks() {
        Task originalTask = new Todo("original");
        TaskList tasks = new TaskList(List.of(originalTask));
        Storage storage = new Storage(this.temporaryDirectory.resolve("duck.txt").toString());

        DuckException zeroException = assertThrows(DuckException.class, () ->
                new DeleteCommand(0).execute(tasks, new Ui(), storage));
        DuckException pastEndException = assertThrows(DuckException.class, () ->
                new DeleteCommand(2).execute(tasks, new Ui(), storage));

        String expectedMessage = "That task number does not exist. "
                + "Use list to check the available task numbers.";
        assertEquals(expectedMessage, zeroException.getMessage());
        assertEquals(expectedMessage, pastEndException.getMessage());
        assertEquals(List.of(originalTask), tasks.getTasksSnapshot());
        assertFalse(Files.exists(this.temporaryDirectory.resolve("duck.txt")));
    }

    @Test
    public void execute_saveFails_restoresTaskAtOriginalPosition() {
        Task firstTask = new Todo("first");
        Task secondTask = new Todo("second");
        Task thirdTask = new Todo("third");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask, thirdTask));

        DuckException exception = assertThrows(DuckException.class, () ->
                new DeleteCommand(2).execute(tasks, new Ui(), new FailingStorage()));

        assertEquals("Simulated save failure.", exception.getMessage());
        assertEquals(3, tasks.getTaskCount());
        assertSame(firstTask, tasks.get(0));
        assertSame(secondTask, tasks.get(1));
        assertSame(thirdTask, tasks.get(2));
    }
}
