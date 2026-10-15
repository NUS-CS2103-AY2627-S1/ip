package nob.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nob.exception.NobException;
import nob.storage.Storage;
import nob.task.TaskList;
import nob.task.Todo;
import nob.ui.Ui;

/**
 * Tests commands that operate directly on the task list and console UI.
 */
public class CommandTest {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private PrintStream originalOutput;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        originalOutput = System.out;
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        ui = new Ui();
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOutput);
    }

    @Test
    public void clearCommand_nonEmptyList_tasksClearedAndSaved(@TempDir Path temporaryDirectory)
            throws IOException {
        Path dataFile = temporaryDirectory.resolve("data").resolve("nob.txt");
        Storage storage = new Storage(dataFile);
        TaskList tasks = new TaskList(1);
        tasks.addTask(new Todo("read book"));

        new ClearCommand().execute(tasks, ui, storage);

        assertEquals(0, tasks.getTaskCount());
        assertTrue(Files.exists(dataFile));
        assertEquals("", Files.readString(dataFile));
        assertOutput("Noted. I've cleared the entire task list.\n");
    }

    @Test
    public void clearCommand_storageUnavailable_errorAndConfirmationPrinted(@TempDir Path temporaryDirectory)
            throws IOException {
        Path parentFile = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(parentFile, "content");
        Storage storage = new Storage(parentFile.resolve("nob.txt"));

        new ClearCommand().execute(new TaskList(0), ui, storage);

        assertOutput("I couldn't save the task list to disk. Your changes are kept for this run.\n"
                + "Noted. I've cleared the entire task list.\n");
    }

    @Test
    public void exitCommand_execute_goodbyePrinted(@TempDir Path temporaryDirectory) {
        new ExitCommand().execute(new TaskList(0), ui, createStorage(temporaryDirectory));

        assertOutput("Goodbye! Hope to see you soon mate!\n");
    }

    @Test
    public void helpCommand_execute_helpPrinted(@TempDir Path temporaryDirectory) {
        new HelpCommand().execute(new TaskList(0), ui, createStorage(temporaryDirectory));

        assertTrue(output.toString(StandardCharsets.UTF_8).contains("Here are the commands you can use:"));
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("  bye"));
    }

    @Test
    public void listCommand_execute_taskListPrinted(@TempDir Path temporaryDirectory) throws NobException {
        TaskList tasks = new TaskList(1);
        tasks.addTask(new Todo("read book"));

        new ListCommand().execute(tasks, ui, createStorage(temporaryDirectory));

        assertOutput("Here are the tasks in your list:\n1.[T] read book [ ]\n");
    }

    @Test
    public void clearCommand_nonEmptyList_oldTaskIsNotRetained(@TempDir Path temporaryDirectory) {
        TaskList tasks = new TaskList(1);
        tasks.addTask(new Todo("read book"));

        new ClearCommand().execute(tasks, ui, createStorage(temporaryDirectory));

        assertNull(tasks.getTasks()[0]);
    }

    private Storage createStorage(Path temporaryDirectory) {
        return new Storage(temporaryDirectory.resolve("data").resolve("nob.txt"));
    }

    private void assertOutput(String expectedOutput) {
        assertEquals(expectedOutput.replace("\n", System.lineSeparator()), output.toString(StandardCharsets.UTF_8));
    }
}
