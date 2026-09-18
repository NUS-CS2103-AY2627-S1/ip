package zhangwei.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

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
 * Tests {@link UnmarkCommand}, the mirror image of {@link MarkCommand}: it must
 * undo a done status, leave the other tasks alone, and save the change.
 */
public class UnmarkCommandTest {

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

    /** Returns three todos, all already marked as done. */
    private TaskList threeDoneTasks() {
        TaskList tasks = new TaskList();
        for (int i = 1; i <= 3; i++) {
            tasks.add(new Todo("task " + i));
            tasks.get(i).markAsDone();
        }
        return tasks;
    }

    @Test
    public void execute_doneTask_taskMarkedNotDone() throws ZhangWeiException {
        TaskList tasks = threeDoneTasks();
        new UnmarkCommand(2).execute(tasks, new Ui(), storage);
        assertFalse(tasks.get(2).isDone());
    }

    @Test
    public void execute_doneTask_otherTasksUnchanged() throws ZhangWeiException {
        TaskList tasks = threeDoneTasks();
        new UnmarkCommand(2).execute(tasks, new Ui(), storage);
        assertTrue(tasks.get(1).isDone());
        assertTrue(tasks.get(3).isDone());
    }

    @Test
    public void execute_alreadyNotDoneTask_staysNotDone() throws ZhangWeiException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("task 1"));
        new UnmarkCommand(1).execute(tasks, new Ui(), storage);
        assertFalse(tasks.get(1).isDone());
    }

    @Test
    public void execute_doneTask_confirmationShown() throws ZhangWeiException {
        new UnmarkCommand(1).execute(threeDoneTasks(), new Ui(), storage);
        String output = captured.toString();
        assertTrue(output.contains("This task is not done yet"));
        assertTrue(output.contains("[T][ ] task 1"));
    }

    @Test
    public void execute_doneTask_notDoneStatusSaved() throws Exception {
        new UnmarkCommand(1).execute(threeDoneTasks(), new Ui(), storage);
        assertEquals("T | 0 | task 1", Files.readAllLines(saveFile).get(0));
    }

    @Test
    public void execute_taskNumberAboveSize_exceptionThrown() {
        assertThrows(ZhangWeiException.class,
                () -> new UnmarkCommand(4).execute(threeDoneTasks(), new Ui(), storage));
    }

    @Test
    public void execute_emptyList_exceptionThrown() {
        assertThrows(ZhangWeiException.class,
                () -> new UnmarkCommand(1).execute(new TaskList(), new Ui(), storage));
    }

    @Test
    public void isExit_unmarkCommand_falseReturned() {
        assertFalse(new UnmarkCommand(1).isExit());
    }
}
