package myriad.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import myriad.MyriadException;
import myriad.storage.Storage;
import myriad.task.Task;
import myriad.task.TaskList;
import myriad.task.ToDo;
import myriad.ui.Ui;

/**
 * Tests for {@link AddCommand#execute}. Adding does three things in a fixed
 * order: change the list, acknowledge the change, then save. Each is
 * checked on its own here, along with the one ordering that matters to the
 * user: a failed save must not undo an add that already happened.
 * <p>
 * Every test saves inside a JUnit-managed temporary directory, so the real
 * data file at {@code data/myriad.txt} is never touched.
 */
public class AddCommandTest {

    /** What a recorded reply puts between its lines, on every platform. */
    private static final String NEWLINE = "\n";

    /** JUnit creates this per test and deletes it afterwards. */
    @TempDir
    private Path tempDir;

    /** Path to a data file inside the temp directory, not yet created. */
    private Path dataFile() {
        return tempDir.resolve("myriad.txt");
    }

    /** A Storage pointed at the temp data file. */
    private Storage storageAtTempFile() {
        return new Storage(dataFile().toString());
    }

    @Test
    public void execute_emptyList_taskAppended() throws MyriadException {
        TaskList tasks = new TaskList();
        Task task = new ToDo("read book");

        new AddCommand(task).execute(tasks, new Ui(false), storageAtTempFile());

        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(0));
    }

    @Test
    public void execute_nonEmptyList_taskAppendedAtEnd() throws MyriadException {
        // Appending keeps the numbers of tasks already shown to the user.
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("first"));
        Task task = new ToDo("second");

        new AddCommand(task).execute(tasks, new Ui(false), storageAtTempFile());

        assertEquals(2, tasks.size());
        assertSame(task, tasks.get(1));
    }

    @Test
    public void execute_toDo_additionAcknowledgedWithNewSize() throws MyriadException {
        Ui ui = new Ui(false);

        new AddCommand(new ToDo("read book")).execute(new TaskList(), ui, storageAtTempFile());

        assertEquals("A fresh stroke on the canvas:" + NEWLINE
                + "[T][ ] read book" + NEWLINE
                + "Your canvas now holds 1 task.",
                ui.getResponse());
    }

    @Test
    public void execute_toDo_wholeListSaved() throws MyriadException, IOException {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("first"));

        new AddCommand(new ToDo("second")).execute(tasks, new Ui(false), storageAtTempFile());

        assertEquals("T | 0 | first\nT | 0 | second\n", Files.readString(dataFile()));
    }

    @Test
    public void execute_saveFails_taskStillAddedAndErrorThrown() {
        // Pointing Storage at a directory makes every save fail. The add and
        // its acknowledgement happen first, so the task stays in the list for
        // the rest of the session even though it never reached the disk.
        TaskList tasks = new TaskList();
        Ui ui = new Ui(false);
        AddCommand command = new AddCommand(new ToDo("read book"));
        Storage failingStorage = new Storage(tempDir.toString());

        MyriadException e = assertThrows(MyriadException.class, () -> command.execute(tasks, ui, failingStorage));

        assertTrue(e.getMessage().startsWith("Couldn't save your tasks to disk"), e.getMessage());
        assertEquals(1, tasks.size());
        assertTrue(ui.getResponse().startsWith("A fresh stroke on the canvas:"), ui.getResponse());
    }

    @Test
    public void execute_duplicateTask_errorThrownAndNothingChanged() throws MyriadException, IOException {
        TaskList tasks = new TaskList();
        Ui ui = new Ui(false);
        Storage storage = storageAtTempFile();
        new AddCommand(new ToDo("read book")).execute(tasks, ui, storage);
        String savedBefore = Files.readString(dataFile());
        ui.startResponse();

        MyriadException e = assertThrows(MyriadException.class, () ->
                new AddCommand(new ToDo("read book")).execute(tasks, ui, storage));

        assertEquals("That task is already in your list: [T][ ] read book", e.getMessage());
        assertEquals(1, tasks.size());
        // Refused before anything else happens: no acknowledgement, no save.
        assertEquals("", ui.getResponse());
        assertEquals(savedBefore, Files.readString(dataFile()));
    }

    @Test
    public void execute_duplicateOfDoneTask_errorShowsExistingTask() throws MyriadException {
        // The message shows the task already in the list, done mark and all,
        // rather than the new one, so the user can see why it was refused.
        TaskList tasks = new TaskList();
        Task existing = new ToDo("read book");
        existing.setDone(true);
        tasks.add(existing);

        MyriadException e = assertThrows(MyriadException.class, () ->
                new AddCommand(new ToDo("read book")).execute(tasks, new Ui(false), storageAtTempFile()));

        assertEquals("That task is already in your list: [T][X] read book", e.getMessage());
    }
}
