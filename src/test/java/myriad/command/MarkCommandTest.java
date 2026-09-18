package myriad.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import myriad.MyriadException;
import myriad.storage.Storage;
import myriad.task.TaskList;
import myriad.task.ToDo;
import myriad.ui.Ui;

/**
 * Tests for {@link MarkCommand#execute}: that the numbered task, and only
 * that task, becomes done, that the user is shown it, and that the change
 * is saved. How a task number is checked is tested in TaskNumberCommandTest;
 * here it only matters that a rejected number changes nothing.
 * <p>
 * Every test saves inside a JUnit-managed temporary directory, so the real
 * data file at {@code data/myriad.txt} is never touched.
 */
public class MarkCommandTest {

    /** What a recorded reply puts between its lines, on every platform. */
    private static final String NEWLINE = "\n";

    /** JUnit creates this per test and deletes it afterwards. */
    @TempDir
    private Path tempDir;

    /** Builds a list of not-done ToDos with the given descriptions, in order. */
    private static TaskList taskListOf(String... descriptions) {
        TaskList tasks = new TaskList();
        for (String description : descriptions) {
            tasks.add(new ToDo(description));
        }
        return tasks;
    }

    /** Path to a data file inside the temp directory, not yet created. */
    private Path dataFile() {
        return tempDir.resolve("myriad.txt");
    }

    /** A Storage pointed at the temp data file. */
    private Storage storageAtTempFile() {
        return new Storage(dataFile().toString());
    }

    @Test
    public void execute_validNumber_onlyThatTaskMarkedDone() throws MyriadException {
        TaskList tasks = taskListOf("first", "second", "third");

        new MarkCommand(2).execute(tasks, new Ui(false), storageAtTempFile());

        assertFalse(tasks.get(0).isDone());
        assertTrue(tasks.get(1).isDone());
        assertFalse(tasks.get(2).isDone());
    }

    @Test
    public void execute_validNumber_taskShownInDoneState() throws MyriadException {
        Ui ui = new Ui(false);

        new MarkCommand(1).execute(taskListOf("read book"), ui, storageAtTempFile());

        assertEquals("Beautiful! That one's painted in:" + NEWLINE
                + "  [T][X] read book",
                ui.getResponse());
    }

    @Test
    public void execute_validNumber_doneStateSaved() throws MyriadException, IOException {
        new MarkCommand(2).execute(taskListOf("first", "second"), new Ui(false), storageAtTempFile());

        assertEquals("T | 0 | first\nT | 1 | second\n", Files.readString(dataFile()));
    }

    @Test
    public void execute_taskAlreadyDone_staysDone() throws MyriadException {
        // Marking is setting, not toggling, so repeating it is harmless.
        TaskList tasks = taskListOf("read book");
        tasks.markDone(0);

        new MarkCommand(1).execute(tasks, new Ui(false), storageAtTempFile());

        assertTrue(tasks.get(0).isDone());
    }

    @Test
    public void execute_numberOutOfRange_nothingChangedOrSaved() {
        TaskList tasks = taskListOf("read book");
        Ui ui = new Ui(false);

        assertThrows(MyriadException.class, () -> new MarkCommand(2).execute(tasks, ui, storageAtTempFile()));

        assertFalse(tasks.get(0).isDone());
        assertEquals("", ui.getResponse());
        assertFalse(Files.exists(dataFile()));
    }
}
