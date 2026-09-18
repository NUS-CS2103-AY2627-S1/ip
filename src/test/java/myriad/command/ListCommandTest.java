package myriad.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import myriad.storage.Storage;
import myriad.task.TaskList;
import myriad.task.ToDo;
import myriad.ui.Ui;

/**
 * Tests for {@link ListCommand#execute}: that every task is shown with the
 * number the user types to mark, unmark or delete it, and that listing
 * changes nothing.
 */
public class ListCommandTest {

    /** What a recorded reply puts between its lines, on every platform. */
    private static final String NEWLINE = "\n";

    /** JUnit creates this per test and deletes it afterwards. */
    @TempDir
    private Path tempDir;

    /** Runs a list command on {@code tasks} and returns the reply. */
    private String listAndGetReply(TaskList tasks) {
        Ui ui = new Ui(false);
        new ListCommand().execute(tasks, ui, new Storage(tempDir.resolve("myriad.txt").toString()));
        return ui.getResponse();
    }

    @Test
    public void execute_severalTasks_allShownWithTaskNumbers() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        tasks.add(new ToDo("join club"));
        tasks.markDone(1);

        assertEquals("Here's your canvas so far:" + NEWLINE
                + "1.[T][ ] read book" + NEWLINE
                + "2.[T][X] join club",
                listAndGetReply(tasks));
    }

    @Test
    public void execute_emptyList_headerOnly() {
        // Known limitation: an empty list gets the same header as a full one,
        // with nothing under it, rather than a message saying it is empty.
        assertEquals("Here's your canvas so far:", listAndGetReply(new TaskList()));
    }

    @Test
    public void execute_anyList_listUnchangedAndNothingSaved() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        listAndGetReply(tasks);

        assertEquals(1, tasks.size());
        assertFalse(tasks.get(0).isDone());
        assertFalse(Files.exists(tempDir.resolve("myriad.txt")));
    }

    @Test
    public void isExit_listCommand_false() {
        assertFalse(new ListCommand().isExit());
    }
}
