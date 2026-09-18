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
 * Tests {@link MarkCommand}: the right task becomes done, its neighbours do
 * not, the change reaches the save file, and an impossible task number is
 * refused before anything is changed.
 */
public class MarkCommandTest {

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

    private TaskList threeTasks() {
        TaskList tasks = new TaskList();
        for (int i = 1; i <= 3; i++) {
            tasks.add(new Todo("task " + i));
        }
        return tasks;
    }

    @Test
    public void execute_validTaskNumber_taskMarkedDone() throws ZhangWeiException {
        TaskList tasks = threeTasks();
        new MarkCommand(2).execute(tasks, new Ui(), storage);
        assertTrue(tasks.get(2).isDone());
    }

    @Test
    public void execute_validTaskNumber_otherTasksUnchanged() throws ZhangWeiException {
        TaskList tasks = threeTasks();
        new MarkCommand(2).execute(tasks, new Ui(), storage);
        assertFalse(tasks.get(1).isDone());
        assertFalse(tasks.get(3).isDone());
    }

    @Test
    public void execute_alreadyDoneTask_staysDone() throws ZhangWeiException {
        TaskList tasks = threeTasks();
        tasks.get(1).markAsDone();
        new MarkCommand(1).execute(tasks, new Ui(), storage);
        assertTrue(tasks.get(1).isDone());
    }

    @Test
    public void execute_validTaskNumber_confirmationShown() throws ZhangWeiException {
        new MarkCommand(1).execute(threeTasks(), new Ui(), storage);
        String output = captured.toString();
        assertTrue(output.contains("This task is done"));
        assertTrue(output.contains("[T][X] task 1"));
    }

    @Test
    public void execute_validTaskNumber_doneStatusSaved() throws Exception {
        new MarkCommand(1).execute(threeTasks(), new Ui(), storage);
        assertEquals("T | 1 | task 1", Files.readAllLines(saveFile).get(0));
    }

    @Test
    public void execute_taskNumberAboveSize_exceptionThrown() {
        assertThrows(ZhangWeiException.class,
                () -> new MarkCommand(4).execute(threeTasks(), new Ui(), storage));
    }

    @Test
    public void execute_emptyList_exceptionThrown() {
        assertThrows(ZhangWeiException.class,
                () -> new MarkCommand(1).execute(new TaskList(), new Ui(), storage));
    }

    @Test
    public void execute_invalidTaskNumber_noTaskMarked() {
        TaskList tasks = threeTasks();
        assertThrows(ZhangWeiException.class,
                () -> new MarkCommand(0).execute(tasks, new Ui(), storage));
        for (int i = 1; i <= tasks.size(); i++) {
            assertFalse(tasks.get(i).isDone());
        }
    }

    @Test
    public void isExit_markCommand_falseReturned() {
        assertFalse(new MarkCommand(1).isExit());
    }
}
