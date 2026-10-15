package thomas.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import thomas.ThomasException;
import thomas.storage.Storage;
import thomas.task.Task;
import thomas.task.TaskList;
import thomas.task.TodoTask;
import thomas.ui.Ui;

/**
 * Tests MarkCommand.execute.
 * What is particular to a mark is that the undo step must carry the flag as it
 * was before, not the opposite of what it became: marking a task that is
 * already done changes nothing, and an undo that simply unmarked it would take
 * away a tick this command never put there. Both sides of that are pinned.
 * Each case gets its own save file in a temporary folder.
 */
public class MarkCommandTest {

    /** Folder JUnit makes fresh for each test and deletes afterwards */
    @TempDir
    private Path folder;

    /**
     * Returns the save file each case works on, inside the temporary folder.
     *
     * @return Path to this case's save file.
     */
    private Path saveFile() {
        return folder.resolve("tasklist.txt");
    }

    /**
     * Returns a storage over this case's save file.
     *
     * @return New storage.
     */
    private Storage storage() {
        return new Storage(saveFile().toString());
    }

    /**
     * Returns a list holding to-dos with the given descriptions, in order.
     *
     * @param descriptions Task text, one per task.
     * @return List holding those tasks.
     */
    private static TaskList listOf(String... descriptions) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (String description : descriptions) {
            tasks.add(new TodoTask(description));
        }
        return new TaskList(tasks);
    }

    /**
     * Returns a history that has been told a line is being carried out.
     *
     * @param typedLine The line the command under test was asked for with.
     * @return History ready for the command to push onto.
     */
    private static History historyFor(String typedLine) {
        History history = new History();
        history.startCommand(typedLine);
        return history;
    }

    // ---- the reply ----

    @Test
    public void execute_notDoneTask_confirmsWithTheTickShown() throws ThomasException {
        String reply = new MarkCommand(1).execute(listOf("read book"), new Ui(), storage(), historyFor("mark 1"));

        assertEquals("Delivered, right on time! This wagon is done:\n   [T][X] read book", reply);
    }

    @Test
    public void execute_alreadyDoneTask_confirmsAllTheSame() throws ThomasException {
        TaskList tasks = listOf("read book");
        tasks.get(0).markAsDone();

        String reply = new MarkCommand(1).execute(tasks, new Ui(), storage(), historyFor("mark 1"));

        // Not a mistake worth refusing: the task ends up in the state asked for.
        assertEquals("Delivered, right on time! This wagon is done:\n   [T][X] read book", reply);
    }

    // ---- the list ----

    @Test
    public void execute_secondOfThree_onlyThatTaskIsDone() throws ThomasException {
        TaskList tasks = listOf("read book", "buy milk", "swim");

        new MarkCommand(2).execute(tasks, new Ui(), storage(), historyFor("mark 2"));

        assertFalse(tasks.get(0).isDone());
        assertTrue(tasks.get(1).isDone());
        assertFalse(tasks.get(2).isDone());
        assertEquals(3, tasks.size());
    }

    // ---- the save file ----

    @Test
    public void execute_anyTask_writesTheDoneFlagToTheSaveFile() throws ThomasException, IOException {
        TaskList tasks = listOf("read book", "buy milk");

        new MarkCommand(2).execute(tasks, new Ui(), storage(), historyFor("mark 2"));

        assertEquals(List.of("T | 0 | read book", "T | 1 | buy milk"), Files.readAllLines(saveFile()));
    }

    // ---- the undo step ----

    @Test
    public void execute_notDoneTask_undoClearsTheTick() throws ThomasException {
        TaskList tasks = listOf("read book");
        History history = historyFor("mark 1");

        new MarkCommand(1).execute(tasks, new Ui(), storage(), history);

        assertEquals("mark 1", history.undo(tasks));
        assertFalse(tasks.get(0).isDone());
    }

    @Test
    public void execute_alreadyDoneTask_undoLeavesTheTick() throws ThomasException {
        TaskList tasks = listOf("read book");
        tasks.get(0).markAsDone();
        History history = historyFor("mark 1");

        new MarkCommand(1).execute(tasks, new Ui(), storage(), history);
        history.undo(tasks);

        // The step recorded "was done", so undoing restores done rather than
        // flipping to not done.
        assertTrue(tasks.get(0).isDone());
    }

    // ---- a number that names no task ----

    @Test
    public void execute_numberPastTheEnd_exceptionThrown() {
        TaskList tasks = listOf("read book");

        ThomasException e = assertThrows(ThomasException.class, () -> new MarkCommand(2)
                .execute(tasks, new Ui(), storage(), historyFor("mark 2")));
        assertEquals("There's no wagon 2 on my train! I'm only pulling 1 wagon(s).", e.getMessage());
    }

    @Test
    public void execute_zero_exceptionThrown() {
        TaskList tasks = listOf("read book");

        // The parser lets 0 through as a whole number; the list is what refuses it.
        ThomasException e = assertThrows(ThomasException.class, () -> new MarkCommand(0)
                .execute(tasks, new Ui(), storage(), historyFor("mark 0")));
        assertEquals("There's no wagon 0 on my train! I'm only pulling 1 wagon(s).", e.getMessage());
    }

    @Test
    public void execute_numberPastTheEnd_nothingRecordedToUndo() {
        TaskList tasks = listOf("read book");
        History history = historyFor("mark 2");

        assertThrows(ThomasException.class, () -> new MarkCommand(2)
                .execute(tasks, new Ui(), storage(), history));

        assertThrows(ThomasException.class, () -> history.undo(tasks));
        assertFalse(tasks.get(0).isDone());
    }

    @Test
    public void execute_numberPastTheEnd_saveFileNotWritten() {
        TaskList tasks = listOf("read book");

        assertThrows(ThomasException.class, () -> new MarkCommand(2)
                .execute(tasks, new Ui(), storage(), historyFor("mark 2")));

        assertFalse(Files.exists(saveFile()));
    }

    // ---- a save that fails ----

    @Test
    public void execute_saveFileIsAFolder_warningPrecedesConfirmation() throws ThomasException, IOException {
        Files.createDirectory(saveFile());
        TaskList tasks = listOf("read book");

        String reply = new MarkCommand(1).execute(tasks, new Ui(), storage(), historyFor("mark 1"));

        assertEquals("Cinders and ashes! I couldn't save your tasks: '" + saveFile()
                + "' is a folder, and I need it to be a file. Move the folder out of the way.\n"
                + "Delivered, right on time! This wagon is done:\n   [T][X] read book", reply);
        assertTrue(tasks.get(0).isDone());
    }

    // ---- not an exit ----

    @Test
    public void isExit_anyMarkCommand_isFalse() {
        assertFalse(new MarkCommand(1).isExit());
    }
}
