package noms.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

import noms.exception.DuplicateTaskException;
import noms.exception.NomsException;
import noms.exception.StorageException;
import noms.storage.Storage;
import noms.task.Task;
import noms.task.TaskList;
import noms.task.ToDo;
import noms.ui.Ui;

/**
 * Tests {@link AddCommand#execute}, which is non-trivial because it both
 * mutates the task list and persists it. The tests assert those observable
 * effects (the task is in the list and on disk) rather than the console
 * output, which the console UI regression plan already covers. Standard
 * output is redirected during each test so the command's prints do not clutter
 * the test log.
 */
public class AddCommandTest {

    @TempDir
    private Path tempDir;

    private final PrintStream realOut = System.out;
    private Ui ui;
    private Storage storage;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        ui = new Ui();
        storage = new Storage(tempDir.toString(), "noms.txt");
    }

    @AfterEach
    public void tearDown() {
        System.setOut(realOut);
    }

    @Test
    public void execute_addsTaskToList() throws NomsException {
        TaskList tasks = new TaskList();
        Task todo = new ToDo("read book");

        new AddCommand(todo).execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertSame(todo, tasks.get(0));
    }

    @Test
    public void execute_persistsTaskToStorage() throws IOException, NomsException {
        TaskList tasks = new TaskList();

        new AddCommand(new ToDo("read book")).execute(tasks, ui, storage);

        // A fresh load from the same file must see the saved task.
        assertEquals(1, storage.load().size());
        assertEquals("read book", storage.load().get(0).getDescription());
    }

    @Test
    public void execute_saveFails_throwsAndRemovesAddedTask() throws IOException {
        Path blockedDirectory = tempDir.resolve("blocked-directory");
        Files.writeString(blockedDirectory, "not a directory");
        Storage blockedStorage = new Storage(blockedDirectory.toString(), "noms.txt");
        TaskList tasks = new TaskList();

        assertThrows(StorageException.class,
                () -> new AddCommand(new ToDo("read book"))
                        .execute(tasks, ui, blockedStorage));
        assertEquals(0, tasks.size());
    }

    @Test
    public void execute_duplicateTask_throwsWithoutChangingListOrStorage()
            throws IOException, NomsException {
        ToDo existingTask = new ToDo("read book");
        existingTask.markAsDone();
        TaskList tasks = new TaskList(existingTask);
        storage.save(tasks.asList());

        assertThrows(DuplicateTaskException.class,
                () -> new AddCommand(new ToDo("read book")).execute(tasks, ui, storage));
        assertEquals(1, tasks.size());
        assertEquals(1, storage.load().size());
    }

    @Test
    public void isExit_returnsFalse() {
        assertFalse(new AddCommand(new ToDo("read book")).isExit());
    }
}
