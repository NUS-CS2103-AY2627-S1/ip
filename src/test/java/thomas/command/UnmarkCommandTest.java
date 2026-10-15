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
 * Tests UnmarkCommand.execute.
 * The mirror image of MarkCommandTest, and kept as its own file for the same
 * reason the command is its own class: the two differ in one call and one
 * message, and a shared test with a flag saying which way round it is would be
 * harder to read than two plain ones. The undo rule is the one to watch: an
 * unmark of a task that is already not done records "was not done", so undoing
 * it must not put a tick on a task that never had one.
 * Each case gets its own save file in a temporary folder.
 */
public class UnmarkCommandTest {

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
     * Returns a list holding done to-dos with the given descriptions, in order.
     * Done rather than not, since an unmark of a task that is already not done
     * is the odd case here rather than the usual one.
     *
     * @param descriptions Task text, one per task.
     * @return List holding those tasks, every one marked done.
     */
    private static TaskList doneListOf(String... descriptions) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (String description : descriptions) {
            Task task = new TodoTask(description);
            task.markAsDone();
            tasks.add(task);
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
    public void execute_doneTask_confirmsWithTheBoxEmptied() throws ThomasException {
        String reply = new UnmarkCommand(1)
                .execute(doneListOf("read book"), new Ui(), storage(), historyFor("unmark 1"));

        assertEquals("Back on the train it goes! This wagon is not done yet:\n   [T][ ] read book", reply);
    }

    @Test
    public void execute_notDoneTask_confirmsAllTheSame() throws ThomasException {
        TaskList tasks = new TaskList();
        tasks.add(new TodoTask("read book"));

        String reply = new UnmarkCommand(1).execute(tasks, new Ui(), storage(), historyFor("unmark 1"));

        assertEquals("Back on the train it goes! This wagon is not done yet:\n   [T][ ] read book", reply);
    }

    // ---- the list ----

    @Test
    public void execute_secondOfThree_onlyThatTaskIsNotDone() throws ThomasException {
        TaskList tasks = doneListOf("read book", "buy milk", "swim");

        new UnmarkCommand(2).execute(tasks, new Ui(), storage(), historyFor("unmark 2"));

        assertTrue(tasks.get(0).isDone());
        assertFalse(tasks.get(1).isDone());
        assertTrue(tasks.get(2).isDone());
    }

    // ---- the save file ----

    @Test
    public void execute_anyTask_writesTheClearedFlagToTheSaveFile() throws ThomasException, IOException {
        TaskList tasks = doneListOf("read book", "buy milk");

        new UnmarkCommand(2).execute(tasks, new Ui(), storage(), historyFor("unmark 2"));

        assertEquals(List.of("T | 1 | read book", "T | 0 | buy milk"), Files.readAllLines(saveFile()));
    }

    // ---- the undo step ----

    @Test
    public void execute_doneTask_undoRestoresTheTick() throws ThomasException {
        TaskList tasks = doneListOf("read book");
        History history = historyFor("unmark 1");

        new UnmarkCommand(1).execute(tasks, new Ui(), storage(), history);

        assertEquals("unmark 1", history.undo(tasks));
        assertTrue(tasks.get(0).isDone());
    }

    @Test
    public void execute_notDoneTask_undoLeavesItNotDone() throws ThomasException {
        TaskList tasks = new TaskList();
        tasks.add(new TodoTask("read book"));
        History history = historyFor("unmark 1");

        new UnmarkCommand(1).execute(tasks, new Ui(), storage(), history);
        history.undo(tasks);

        // The step recorded "was not done", so undoing must not add a tick.
        assertFalse(tasks.get(0).isDone());
    }

    // ---- a number that names no task ----

    @Test
    public void execute_numberPastTheEnd_exceptionThrown() {
        TaskList tasks = doneListOf("read book");

        ThomasException e = assertThrows(ThomasException.class, () -> new UnmarkCommand(2)
                .execute(tasks, new Ui(), storage(), historyFor("unmark 2")));
        assertEquals("There's no wagon 2 on my train! I'm only pulling 1 wagon(s).", e.getMessage());
    }

    @Test
    public void execute_numberPastTheEnd_taskStaysDoneAndNothingToUndo() {
        TaskList tasks = doneListOf("read book");
        History history = historyFor("unmark 2");

        assertThrows(ThomasException.class, () -> new UnmarkCommand(2)
                .execute(tasks, new Ui(), storage(), history));

        assertTrue(tasks.get(0).isDone());
        assertThrows(ThomasException.class, () -> history.undo(tasks));
        assertFalse(Files.exists(saveFile()));
    }

    // ---- a save that fails ----

    @Test
    public void execute_saveFileIsAFolder_warningPrecedesConfirmation() throws ThomasException, IOException {
        Files.createDirectory(saveFile());
        TaskList tasks = doneListOf("read book");

        String reply = new UnmarkCommand(1).execute(tasks, new Ui(), storage(), historyFor("unmark 1"));

        assertEquals("Cinders and ashes! I couldn't save your tasks: '" + saveFile()
                + "' is a folder, and I need it to be a file. Move the folder out of the way.\n"
                + "Back on the train it goes! This wagon is not done yet:\n   [T][ ] read book", reply);
        assertFalse(tasks.get(0).isDone());
    }

    // ---- not an exit ----

    @Test
    public void isExit_anyUnmarkCommand_isFalse() {
        assertFalse(new UnmarkCommand(1).isExit());
    }
}
