package noms.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
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
import noms.task.TaskList;
import noms.task.ToDo;
import noms.ui.Ui;

/**
 * Tests {@link MarkCommand#execute}, which validates the task number, sets
 * the task's completion state, and persists it. The tests assert the task's
 * status icon in the list and after reloading from disk, plus the error path
 * for an invalid number.
 */
public class MarkCommandTest {

    @TempDir
    private Path tempDir;

    private final PrintStream realOut = System.out;
    private Ui ui;
    private Storage storage;
    private TaskList tasks;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        ui = new Ui();
        storage = new Storage(tempDir.toString(), "noms.txt");
        tasks = new TaskList();
        tasks.add(new ToDo("read book"));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(realOut);
    }

    @Test
    public void execute_validNumber_marksTaskDone() throws NomsException {
        new MarkCommand("mark 1").execute(tasks, ui, storage);

        assertEquals("X", tasks.get(0).getStatusIcon());
    }

    @Test
    public void execute_validNumber_persistsDoneState() throws NomsException, IOException {
        new MarkCommand("mark 1").execute(tasks, ui, storage);

        assertEquals("X", storage.load().get(0).getStatusIcon());
    }

    @Test
    public void execute_alreadyMarked_remindsUser() throws NomsException {
        new MarkCommand("mark 1").execute(tasks, ui, storage);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        new MarkCommand("mark 1").execute(tasks, ui, storage);

        assertTrue(output.toString().contains("already marked as done"));
    }

    @Test
    public void execute_nonNumericArgument_throwsNomsException() {
        assertThrows(NomsException.class,
                () -> new MarkCommand("mark abc").execute(tasks, ui, storage));
    }

    @Test
    public void execute_emptyList_throwsNomsException() {
        TaskList emptyTasks = new TaskList();

        assertThrows(NomsException.class,
                () -> new MarkCommand("mark 1").execute(emptyTasks, ui, storage));
    }

    @Test
    public void execute_outOfRangeNumber_throwsNomsException() {
        assertThrows(NomsException.class,
                () -> new MarkCommand("mark 2").execute(tasks, ui, storage));
    }

    @Test
    public void execute_saveFails_throwsAndRestoresUnmarkedState() throws IOException {
        Path blockedDirectory = tempDir.resolve("blocked-directory");
        Files.writeString(blockedDirectory, "not a directory");
        Storage blockedStorage = new Storage(blockedDirectory.toString(), "noms.txt");

        assertThrows(StorageException.class,
                () -> new MarkCommand("mark 1").execute(tasks, ui, blockedStorage));
        assertEquals(" ", tasks.get(0).getStatusIcon());
    }
}
