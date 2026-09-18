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
 * Tests for {@link UnmarkCommand#execute}, the mirror image of
 * MarkCommandTest: the numbered task, and only that task, goes back to not
 * done, the user is shown it, and the change is saved.
 * <p>
 * Every test saves inside a JUnit-managed temporary directory, so the real
 * data file at {@code data/myriad.txt} is never touched.
 */
public class UnmarkCommandTest {

    /** What a recorded reply puts between its lines, on every platform. */
    private static final String NEWLINE = "\n";

    /** JUnit creates this per test and deletes it afterwards. */
    @TempDir
    private Path tempDir;

    /** Builds a list of done ToDos with the given descriptions, in order. */
    private static TaskList doneTaskListOf(String... descriptions) {
        TaskList tasks = new TaskList();
        for (String description : descriptions) {
            tasks.add(new ToDo(description));
            tasks.markDone(tasks.size() - 1);
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
    public void execute_validNumber_onlyThatTaskMarkedNotDone() throws MyriadException {
        TaskList tasks = doneTaskListOf("first", "second", "third");

        new UnmarkCommand(2).execute(tasks, new Ui(false), storageAtTempFile());

        assertTrue(tasks.get(0).isDone());
        assertFalse(tasks.get(1).isDone());
        assertTrue(tasks.get(2).isDone());
    }

    @Test
    public void execute_validNumber_taskShownInNotDoneState() throws MyriadException {
        Ui ui = new Ui(false);

        new UnmarkCommand(1).execute(doneTaskListOf("read book"), ui, storageAtTempFile());

        assertEquals("Back to a sketch. Marked as not done:" + NEWLINE
                + "  [T][ ] read book",
                ui.getResponse());
    }

    @Test
    public void execute_validNumber_notDoneStateSaved() throws MyriadException, IOException {
        new UnmarkCommand(2).execute(doneTaskListOf("first", "second"), new Ui(false), storageAtTempFile());

        assertEquals("T | 1 | first\nT | 0 | second\n", Files.readString(dataFile()));
    }

    @Test
    public void execute_taskNotDone_staysNotDone() throws MyriadException {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        new UnmarkCommand(1).execute(tasks, new Ui(false), storageAtTempFile());

        assertFalse(tasks.get(0).isDone());
    }

    @Test
    public void execute_numberOutOfRange_nothingChangedOrSaved() {
        TaskList tasks = doneTaskListOf("read book");
        Ui ui = new Ui(false);

        assertThrows(MyriadException.class, () -> new UnmarkCommand(0).execute(tasks, ui, storageAtTempFile()));

        assertTrue(tasks.get(0).isDone());
        assertEquals("", ui.getResponse());
        assertFalse(Files.exists(dataFile()));
    }
}
