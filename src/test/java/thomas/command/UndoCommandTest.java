package thomas.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import thomas.ThomasException;
import thomas.storage.Storage;
import thomas.task.TaskList;
import thomas.task.TodoTask;
import thomas.ui.Ui;

/**
 * Tests UndoCommand.execute.
 * How a step is applied is HistoryTest's business; what this command adds is
 * the save and the reply. The save is the part worth pinning: every command that
 * changed the list wrote the file as it went, so a list put back only in memory
 * would be the old one again on the next run.
 * Each case gets its own save file in a temporary folder.
 */
public class UndoCommandTest {

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
     * Returns a list of two to-dos and a history holding the add of the second,
     * as the session that typed {@code todo buy milk} would leave them.
     *
     * @param tasks The list to fill.
     * @return History with one step, for the second add.
     */
    private static History afterAddingTwo(TaskList tasks) throws ThomasException {
        tasks.add(new TodoTask("read book"));
        tasks.add(new TodoTask("buy milk"));
        History history = new History();
        history.startCommand("todo buy milk");
        history.pushRemove(2);
        history.startCommand("undo");
        return history;
    }

    // ---- the reply ----

    @Test
    public void execute_afterAnAdd_quotesTheLineAndReportsTheCount() throws ThomasException {
        TaskList tasks = new TaskList();
        History history = afterAddingTwo(tasks);

        String reply = new UndoCommand().execute(tasks, new Ui(), storage(), history);

        // The line quoted is the one being reversed, not the "undo" itself.
        assertEquals("Reversing! I've backed out of 'todo buy milk'.\n"
                + "That's 1 wagon(s) behind me now.", reply);
    }

    // ---- the list ----

    @Test
    public void execute_afterAnAdd_listIsAsItWasBefore() throws ThomasException {
        TaskList tasks = new TaskList();
        History history = afterAddingTwo(tasks);

        new UndoCommand().execute(tasks, new Ui(), storage(), history);

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    // ---- the save file ----

    @Test
    public void execute_afterAnAdd_writesTheRestoredListToTheSaveFile() throws ThomasException, IOException {
        TaskList tasks = new TaskList();
        History history = afterAddingTwo(tasks);

        new UndoCommand().execute(tasks, new Ui(), storage(), history);

        assertEquals(List.of("T | 0 | read book"), Files.readAllLines(saveFile()));
    }

    // ---- undo records nothing itself ----

    @Test
    public void execute_twiceAfterOneChange_secondIsRefused() throws ThomasException {
        TaskList tasks = new TaskList();
        History history = afterAddingTwo(tasks);
        new UndoCommand().execute(tasks, new Ui(), storage(), history);

        // No redo: the first undo pushed nothing, so there is nothing left.
        ThomasException e = assertThrows(ThomasException.class, () -> new UndoCommand()
                .execute(tasks, new Ui(), storage(), history));
        assertEquals("I can't reverse any further! There's nothing to undo.", e.getMessage());
        assertEquals(1, tasks.size());
    }

    // ---- nothing to undo ----

    @Test
    public void execute_nothingToUndo_exceptionThrown() {
        TaskList tasks = new TaskList();

        ThomasException e = assertThrows(ThomasException.class, () -> new UndoCommand()
                .execute(tasks, new Ui(), storage(), new History()));
        assertEquals("I can't reverse any further! There's nothing to undo.", e.getMessage());
    }

    @Test
    public void execute_nothingToUndo_saveFileNotWritten() {
        assertThrows(ThomasException.class, () -> new UndoCommand()
                .execute(new TaskList(), new Ui(), storage(), new History()));

        assertFalse(Files.exists(saveFile()));
    }

    // ---- a save that fails ----

    @Test
    public void execute_saveFileIsAFolder_warningPrecedesConfirmation() throws ThomasException, IOException {
        Files.createDirectory(saveFile());
        TaskList tasks = new TaskList();
        History history = afterAddingTwo(tasks);

        String reply = new UndoCommand().execute(tasks, new Ui(), storage(), history);

        assertEquals("Cinders and ashes! I couldn't save your tasks: '" + saveFile()
                + "' is a folder, and I need it to be a file. Move the folder out of the way.\n"
                + "Reversing! I've backed out of 'todo buy milk'.\n"
                + "That's 1 wagon(s) behind me now.", reply);
        assertEquals(1, tasks.size());
    }

    // ---- not an exit ----

    @Test
    public void isExit_anyUndoCommand_isFalse() {
        assertFalse(new UndoCommand().isExit());
    }
}
