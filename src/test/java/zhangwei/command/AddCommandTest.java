package zhangwei.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import zhangwei.ZhangWeiException;
import zhangwei.storage.Storage;
import zhangwei.task.TaskList;
import zhangwei.task.Todo;
import zhangwei.ui.Ui;

/**
 * Tests {@link AddCommand}. Adding must do three things together: change the
 * list, tell the user, and save to disk. These tests check all three, because
 * forgetting the save is a mistake the user would only notice after restarting.
 */
public class AddCommandTest {

    // JUnit injects a fresh temporary folder here; it must not be private.
    @TempDir
    Path tempDir;

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream captured;
    private Path saveFile;
    private Storage storage;

    @BeforeEach
    public void setUp() {
        captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        saveFile = tempDir.resolve("tasks.txt");
        storage = new Storage(saveFile.toString());
    }

    @AfterEach
    public void restoreOutput() {
        System.setOut(originalOut);
    }

    @Test
    public void execute_emptyList_taskAdded() throws ZhangWeiException {
        TaskList tasks = new TaskList();
        new AddCommand(new Todo("read book")).execute(tasks, new Ui(), storage);
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(1).toString());
    }

    @Test
    public void execute_listWithTasks_taskAddedAtTheEnd() throws ZhangWeiException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        new AddCommand(new Todo("second")).execute(tasks, new Ui(), storage);
        assertEquals("second", tasks.get(2).getDescription());
    }

    @Test
    public void execute_taskAdded_confirmationShown() throws ZhangWeiException {
        new AddCommand(new Todo("read book")).execute(new TaskList(), new Ui(), storage);
        String output = captured.toString();
        assertTrue(output.contains("I've planted this task"));
        assertTrue(output.contains("read book"));
        assertTrue(output.contains("Now you have 1 task in your grove."));
    }

    @Test
    public void execute_taskAdded_listSavedToDisk() throws Exception {
        new AddCommand(new Todo("read book")).execute(new TaskList(), new Ui(), storage);
        assertEquals(List.of("T | 0 | read book"), Files.readAllLines(saveFile));
    }

    @Test
    public void isExit_addCommand_falseReturned() {
        assertFalse(new AddCommand(new Todo("read book")).isExit());
    }
}
