package milo.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
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

import milo.exception.MiloException;
import milo.storage.Storage;
import milo.task.Task;
import milo.task.TaskList;
import milo.task.Todo;
import milo.ui.Ui;

class AddCommandTest {
    @TempDir
    Path tempDir;

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream capturedOut;

    @BeforeEach
    void redirectStdOut() {
        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut));
    }

    @AfterEach
    void restoreStdOut() {
        System.setOut(originalOut);
    }

    @Test
    void getTask_returnsTheTaskPassedToConstructor() {
        Task task = new Todo("read book");
        AddCommand command = new AddCommand(task);
        assertSame(task, command.getTask());
    }

    @Test
    void execute_onEmptyList_addsTheTask() throws MiloException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("milo.txt").toString());
        new AddCommand(new Todo("read book")).execute(tasks, new Ui(), storage);

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    void execute_taskAlreadyOnTheList_throwsAndLeavesTheListAlone() throws MiloException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("milo.txt").toString());
        new AddCommand(new Todo("read book")).execute(tasks, new Ui(), storage);

        AddCommand duplicate = new AddCommand(new Todo("read book"));
        assertThrows(MiloException.class, () -> duplicate.execute(tasks, new Ui(), storage));
        assertEquals(1, tasks.size());
    }

    @Test
    void execute_isNotExitCommand() {
        assertTrue(!new AddCommand(new Todo("read book")).isExit());
    }

    @Test
    void execute_savesTheUpdatedListToDisk() throws Exception {
        TaskList tasks = new TaskList();
        Path file = tempDir.resolve("milo.txt");
        Storage storage = new Storage(file.toString());
        new AddCommand(new Todo("read book")).execute(tasks, new Ui(), storage);

        assertEquals("T | 0 | read book", Files.readString(file).trim());
    }

    @Test
    void execute_printsConfirmationWithUpdatedCount() throws MiloException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("already here"));
        Storage storage = new Storage(tempDir.resolve("milo.txt").toString());
        new AddCommand(new Todo("read book")).execute(tasks, new Ui(), storage);

        String printed = capturedOut.toString();
        assertTrue(printed.contains("Got it — that's on the list now:"));
        assertTrue(printed.contains("[T][ ] read book"));
        assertTrue(printed.contains("That makes 2 tasks to chase."));
    }
}
