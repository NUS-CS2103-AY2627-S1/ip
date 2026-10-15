package thomas.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
 * Tests DeleteCommand.execute.
 * The four effects of a change -- the list, the file, the undo step and the
 * reply -- are pinned separately, as they are for AddCommand. What is particular
 * to a delete is that the removed task has to be kept for the undo: nothing else
 * holds it once the list has let go, so a step recorded without it could not put
 * it back.
 * Each case gets its own save file in a temporary folder.
 */
public class DeleteCommandTest {

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
    public void execute_middleTask_showsRemovedTaskAndCountLeft() throws ThomasException {
        TaskList tasks = listOf("read book", "buy milk", "swim");

        String reply = new DeleteCommand(2).execute(tasks, new Ui(), storage(), historyFor("delete 2"));

        // The count is the size after the removal, not before it.
        assertEquals("Uncoupled! I've left this wagon in the siding:\n"
                + "   [T][ ] buy milk\n"
                + "That's 2 wagon(s) behind me now.", reply);
    }

    @Test
    public void execute_onlyTask_countIsZero() throws ThomasException {
        String reply = new DeleteCommand(1)
                .execute(listOf("read book"), new Ui(), storage(), historyFor("delete 1"));

        assertEquals("Uncoupled! I've left this wagon in the siding:\n"
                + "   [T][ ] read book\n"
                + "That's 0 wagon(s) behind me now.", reply);
    }

    // ---- the list ----

    @Test
    public void execute_middleTask_laterTasksShiftDown() throws ThomasException {
        TaskList tasks = listOf("read book", "buy milk", "swim");

        new DeleteCommand(2).execute(tasks, new Ui(), storage(), historyFor("delete 2"));

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("[T][ ] swim", tasks.get(1).toString());
    }

    // ---- the save file ----

    @Test
    public void execute_anyTask_writesTheShortenedListToTheSaveFile() throws ThomasException, IOException {
        TaskList tasks = listOf("read book", "buy milk", "swim");

        new DeleteCommand(2).execute(tasks, new Ui(), storage(), historyFor("delete 2"));

        assertEquals(List.of("T | 0 | read book", "T | 0 | swim"), Files.readAllLines(saveFile()));
    }

    // ---- the undo step ----

    @Test
    public void execute_middleTask_undoPutsItBackAtItsOwnNumber() throws ThomasException {
        TaskList tasks = listOf("read book", "buy milk", "swim");
        History history = historyFor("delete 2");

        new DeleteCommand(2).execute(tasks, new Ui(), storage(), history);

        assertEquals("delete 2", history.undo(tasks));
        assertEquals("[T][ ] buy milk", tasks.get(1).toString());
        assertEquals(3, tasks.size());
    }

    @Test
    public void execute_doneTask_undoPutsItBackStillDone() throws ThomasException {
        TaskList tasks = listOf("read book");
        tasks.get(0).markAsDone();
        History history = historyFor("delete 1");

        new DeleteCommand(1).execute(tasks, new Ui(), storage(), history);
        history.undo(tasks);

        // The very task is kept, tick and all, rather than a fresh copy made.
        assertEquals("[T][X] read book", tasks.get(0).toString());
    }

    // ---- a number that names no task ----

    @Test
    public void execute_numberPastTheEnd_exceptionThrown() {
        TaskList tasks = listOf("read book");

        ThomasException e = assertThrows(ThomasException.class, () -> new DeleteCommand(2)
                .execute(tasks, new Ui(), storage(), historyFor("delete 2")));
        assertEquals("There's no wagon 2 on my train! I'm only pulling 1 wagon(s).", e.getMessage());
    }

    @Test
    public void execute_numberPastTheEnd_nothingRecordedToUndo() {
        TaskList tasks = listOf("read book");
        History history = historyFor("delete 2");

        assertThrows(ThomasException.class, () -> new DeleteCommand(2)
                .execute(tasks, new Ui(), storage(), history));

        // Nothing was removed, so an undo must not find a step to put a null back.
        assertThrows(ThomasException.class, () -> history.undo(tasks));
        assertEquals(1, tasks.size());
    }

    @Test
    public void execute_numberPastTheEnd_saveFileNotWritten() {
        TaskList tasks = listOf("read book");

        assertThrows(ThomasException.class, () -> new DeleteCommand(2)
                .execute(tasks, new Ui(), storage(), historyFor("delete 2")));

        assertFalse(Files.exists(saveFile()));
    }

    // ---- a save that fails ----

    @Test
    public void execute_saveFileIsAFolder_warningPrecedesConfirmation() throws ThomasException, IOException {
        Files.createDirectory(saveFile());
        TaskList tasks = listOf("read book");

        String reply = new DeleteCommand(1).execute(tasks, new Ui(), storage(), historyFor("delete 1"));

        assertEquals("Cinders and ashes! I couldn't save your tasks: '" + saveFile()
                + "' is a folder, and I need it to be a file. Move the folder out of the way.\n"
                + "Uncoupled! I've left this wagon in the siding:\n"
                + "   [T][ ] read book\n"
                + "That's 0 wagon(s) behind me now.", reply);
        assertEquals(0, tasks.size());
    }

    // ---- not an exit ----

    @Test
    public void isExit_anyDeleteCommand_isFalse() {
        assertFalse(new DeleteCommand(1).isExit());
    }
}
