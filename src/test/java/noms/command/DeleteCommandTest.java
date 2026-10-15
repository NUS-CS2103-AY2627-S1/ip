package noms.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import noms.exception.NomsException;
import noms.exception.StorageException;
import noms.storage.Storage;
import noms.task.Task;
import noms.task.TaskList;
import noms.task.ToDo;
import noms.ui.Ui;

/**
 * Tests {@link DeleteCommand#execute}, which validates the task number
 * against the current list size, removes the task, and persists the result.
 * The tests assert the resulting list state and disk contents, and the
 * error path for an out-of-range number; console output is left to the
 * console UI regression plan.
 */
public class DeleteCommandTest {

    @TempDir
    private Path tempDir;

    private final PrintStream realOut = System.out;
    private Ui ui;
    private Storage storage;
    private TaskList tasks;
    private Task first;
    private Task second;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        ui = new Ui();
        storage = new Storage(tempDir.toString(), "noms.txt");
        tasks = new TaskList();
        first = new ToDo("read book");
        second = new ToDo("buy milk");
        tasks.add(first);
        tasks.add(second);
    }

    @AfterEach
    public void tearDown() {
        System.setOut(realOut);
    }

    @Test
    public void execute_validNumber_removesThatTask() throws NomsException {
        new DeleteCommand("delete 1").execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertSame(second, tasks.get(0));
    }

    @Test
    public void execute_validNumber_persistsRemainingTasks() throws NomsException, IOException {
        new DeleteCommand("delete 1").execute(tasks, ui, storage);

        assertEquals(1, storage.load().size());
        assertEquals("buy milk", storage.load().get(0).getDescription());
    }

    @Test
    public void execute_outOfRangeNumber_throwsNomsExceptionAndKeepsList() {
        assertThrows(NomsException.class,
                () -> new DeleteCommand("delete 5").execute(tasks, ui, storage));
        assertEquals(2, tasks.size());
    }

    @Test
    public void execute_emptyList_throwsNomsException() {
        TaskList emptyTasks = new TaskList();

        assertThrows(NomsException.class,
                () -> new DeleteCommand("delete 1").execute(emptyTasks, ui, storage));
    }

    @Test
    public void execute_saveFails_throwsAndRestoresTaskOrder() throws IOException {
        Path blockedDirectory = tempDir.resolve("blocked-directory");
        Files.writeString(blockedDirectory, "not a directory");
        Storage blockedStorage = new Storage(blockedDirectory.toString(), "noms.txt");

        assertThrows(StorageException.class,
                () -> new DeleteCommand("delete 1").execute(tasks, ui, blockedStorage));
        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
    }
}
