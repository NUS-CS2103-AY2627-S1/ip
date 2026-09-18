package myriad.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
 * Tests for {@link DeleteCommand#execute}. Deleting is the one command that
 * renumbers tasks, so besides removing the right task, reporting it and
 * saving, the tests check that the tasks after it move up one place.
 * <p>
 * Every test saves inside a JUnit-managed temporary directory, so the real
 * data file at {@code data/myriad.txt} is never touched.
 */
public class DeleteCommandTest {

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
    public void execute_middleTask_removedAndLaterTasksShiftUp() throws MyriadException {
        TaskList tasks = taskListOf("first", "second", "third");

        new DeleteCommand(2).execute(tasks, new Ui(false), storageAtTempFile());

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] first", tasks.get(0).toString());
        assertEquals("[T][ ] third", tasks.get(1).toString());
    }

    @Test
    public void execute_validNumber_removedTaskShownWithNewSize() throws MyriadException {
        Ui ui = new Ui(false);

        new DeleteCommand(2).execute(taskListOf("first", "second", "third"), ui, storageAtTempFile());

        assertEquals("Painted over. I've removed this task:" + NEWLINE
                + "  [T][ ] second" + NEWLINE
                + "Your canvas now holds 2 tasks.",
                ui.getResponse());
    }

    @Test
    public void execute_validNumber_remainingTasksSaved() throws MyriadException, IOException {
        new DeleteCommand(1).execute(taskListOf("first", "second"), new Ui(false), storageAtTempFile());

        assertEquals("T | 0 | second\n", Files.readString(dataFile()));
    }

    @Test
    public void execute_onlyTask_emptyListSaved() throws MyriadException, IOException {
        // An empty data file, not a missing one: the save still has to
        // overwrite whatever the previous save wrote.
        TaskList tasks = taskListOf("read book");

        new DeleteCommand(1).execute(tasks, new Ui(false), storageAtTempFile());

        assertEquals(0, tasks.size());
        assertEquals("", Files.readString(dataFile()));
    }

    @Test
    public void execute_numberOutOfRange_nothingChangedOrSaved() {
        TaskList tasks = taskListOf("first", "second");
        Ui ui = new Ui(false);

        assertThrows(MyriadException.class, () -> new DeleteCommand(3).execute(tasks, ui, storageAtTempFile()));

        assertEquals(2, tasks.size());
        assertEquals("", ui.getResponse());
        assertFalse(Files.exists(dataFile()));
    }
}
