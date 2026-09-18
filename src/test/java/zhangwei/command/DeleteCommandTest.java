package zhangwei.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
 * Tests {@link DeleteCommand}. Deleting is the command that can destroy the
 * user's data, so the tests check both that the right task goes and that a bad
 * task number changes nothing at all.
 */
public class DeleteCommandTest {

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

    /** Returns a list of three todos named "task 1", "task 2" and "task 3". */
    private TaskList threeTasks() {
        TaskList tasks = new TaskList();
        for (int i = 1; i <= 3; i++) {
            tasks.add(new Todo("task " + i));
        }
        return tasks;
    }

    @Test
    public void execute_validTaskNumber_taskRemoved() throws ZhangWeiException {
        TaskList tasks = threeTasks();
        new DeleteCommand(2).execute(tasks, new Ui(), storage);
        assertEquals(2, tasks.size());
        assertEquals("task 1", tasks.get(1).getDescription());
        assertEquals("task 3", tasks.get(2).getDescription());
    }

    @Test
    public void execute_lastTaskNumber_lastTaskRemoved() throws ZhangWeiException {
        TaskList tasks = threeTasks();
        new DeleteCommand(3).execute(tasks, new Ui(), storage);
        assertEquals("task 2", tasks.get(2).getDescription());
    }

    @Test
    public void execute_validTaskNumber_confirmationShown() throws ZhangWeiException {
        new DeleteCommand(1).execute(threeTasks(), new Ui(), storage);
        String output = captured.toString();
        assertTrue(output.contains("I've chewed this task away"));
        assertTrue(output.contains("task 1"));
        assertTrue(output.contains("Now you have 2 tasks in your grove."));
    }

    @Test
    public void execute_validTaskNumber_remainingTasksSaved() throws Exception {
        new DeleteCommand(1).execute(threeTasks(), new Ui(), storage);
        assertEquals(List.of("T | 0 | task 2", "T | 0 | task 3"),
                Files.readAllLines(saveFile));
    }

    @Test
    public void execute_taskNumberAboveSize_exceptionThrown() {
        assertThrows(ZhangWeiException.class,
                () -> new DeleteCommand(4).execute(threeTasks(), new Ui(), storage));
    }

    @Test
    public void execute_zeroTaskNumber_exceptionThrown() {
        assertThrows(ZhangWeiException.class,
                () -> new DeleteCommand(0).execute(threeTasks(), new Ui(), storage));
    }

    @Test
    public void execute_emptyList_exceptionThrown() {
        assertThrows(ZhangWeiException.class,
                () -> new DeleteCommand(1).execute(new TaskList(), new Ui(), storage));
    }

    @Test
    public void execute_invalidTaskNumber_listLeftUnchanged() {
        TaskList tasks = threeTasks();
        assertThrows(ZhangWeiException.class,
                () -> new DeleteCommand(9).execute(tasks, new Ui(), storage));
        assertEquals(3, tasks.size());
    }

    @Test
    public void execute_invalidTaskNumber_nothingSaved() {
        // The bounds check runs before any change, so no save should happen.
        assertThrows(ZhangWeiException.class,
                () -> new DeleteCommand(9).execute(threeTasks(), new Ui(), storage));
        assertFalse(Files.exists(saveFile));
    }

    @Test
    public void isExit_deleteCommand_falseReturned() {
        assertFalse(new DeleteCommand(1).isExit());
    }
}
