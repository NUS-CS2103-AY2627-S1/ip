package thomas.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import thomas.ThomasException;
import thomas.storage.Storage;
import thomas.task.DeadlineTask;
import thomas.task.EventTask;
import thomas.task.TaskList;
import thomas.task.TodoTask;
import thomas.ui.Ui;

/**
 * Tests AddCommand.execute.
 * A command is the one place where four things have to happen together: the
 * list changes, the file is written, the undo step is recorded, and the user is
 * told. Each is pinned separately, because each can be forgotten separately --
 * a command that appends and confirms but never saves looks right for the whole
 * session and loses the task on the next run.
 * The save-failure branch of Command.save is reached here too, by putting a
 * folder where the save file should be. The text-UI plan cannot set that up, so
 * this is the only suite that checks the warning is shown and the command still
 * completes.
 * Each case gets its own save file in a temporary folder.
 */
public class AddCommandTest {

    private static final LocalDateTime DEC_02_2PM = LocalDateTime.of(2019, 12, 2, 14, 0);
    private static final LocalDateTime DEC_02_4PM = LocalDateTime.of(2019, 12, 2, 16, 0);

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
     * Returns a history that has been told a line is being carried out, as the
     * read loop tells it before every command.
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
    public void execute_emptyList_confirmsTaskAndCountOfOne() throws ThomasException {
        TaskList tasks = new TaskList();

        String reply = new AddCommand(new TodoTask("read book"))
                .execute(tasks, new Ui(), storage(), historyFor("todo read book"));

        assertEquals("Coupled up! This wagon is on the train now:\n"
                + "   [T][ ] read book\n"
                + "That's 1 wagon(s) behind me now.", reply);
    }

    @Test
    public void execute_nonEmptyList_countIsTheNewSize() throws ThomasException {
        TaskList tasks = new TaskList();
        tasks.add(new TodoTask("read book"));

        String reply = new AddCommand(new TodoTask("buy milk"))
                .execute(tasks, new Ui(), storage(), historyFor("todo buy milk"));

        assertEquals("Coupled up! This wagon is on the train now:\n"
                + "   [T][ ] buy milk\n"
                + "That's 2 wagon(s) behind me now.", reply);
    }

    @Test
    public void execute_deadline_replyShowsTheDeadlineForm() throws ThomasException {
        // The task words itself: the command never asks which kind it holds.
        String reply = new AddCommand(new DeadlineTask("return book", DEC_02_4PM))
                .execute(new TaskList(), new Ui(), storage(), historyFor("deadline ..."));

        assertEquals("Coupled up! This wagon is on the train now:\n"
                + "   [D][ ] return book (by: Dec 02 2019, 4:00 PM)\n"
                + "That's 1 wagon(s) behind me now.", reply);
    }

    @Test
    public void execute_event_replyShowsTheEventForm() throws ThomasException {
        String reply = new AddCommand(new EventTask("meeting", DEC_02_2PM, DEC_02_4PM))
                .execute(new TaskList(), new Ui(), storage(), historyFor("event ..."));

        assertEquals("Coupled up! This wagon is on the train now:\n"
                + "   [E][ ] meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)\n"
                + "That's 1 wagon(s) behind me now.", reply);
    }

    // ---- the list ----

    @Test
    public void execute_anyTask_taskGoesOnTheEnd() throws ThomasException {
        TaskList tasks = new TaskList();
        tasks.add(new TodoTask("read book"));

        new AddCommand(new TodoTask("buy milk"))
                .execute(tasks, new Ui(), storage(), historyFor("todo buy milk"));

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] buy milk", tasks.get(1).toString());
    }

    // ---- the save file ----

    @Test
    public void execute_anyTask_writesTheWholeListToTheSaveFile() throws ThomasException, IOException {
        TaskList tasks = new TaskList();
        tasks.add(new TodoTask("read book"));

        new AddCommand(new TodoTask("buy milk"))
                .execute(tasks, new Ui(), storage(), historyFor("todo buy milk"));

        assertEquals(List.of("T | 0 | read book", "T | 0 | buy milk"), Files.readAllLines(saveFile()));
    }

    // ---- the undo step ----

    @Test
    public void execute_anyTask_undoTakesItBackOut() throws ThomasException {
        TaskList tasks = new TaskList();
        tasks.add(new TodoTask("read book"));
        History history = historyFor("todo buy milk");

        new AddCommand(new TodoTask("buy milk")).execute(tasks, new Ui(), storage(), history);

        // The step removes by number, so it must carry the new task's number and
        // not, say, the size before the add.
        assertEquals("todo buy milk", history.undo(tasks));
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    // ---- a refused duplicate ----

    @Test
    public void execute_duplicate_exceptionThrown() throws ThomasException {
        TaskList tasks = new TaskList();
        tasks.add(new TodoTask("read book"));

        ThomasException e = assertThrows(ThomasException.class, () -> new AddCommand(new TodoTask("read book"))
                .execute(tasks, new Ui(), storage(), historyFor("todo read book")));
        assertEquals("Bust my buffers! That wagon is already on my train, at number 1:\n   [T][ ] read book",
                e.getMessage());
    }

    @Test
    public void execute_duplicate_nothingRecordedToUndo() throws ThomasException {
        TaskList tasks = new TaskList();
        tasks.add(new TodoTask("read book"));
        History history = historyFor("todo read book");

        assertThrows(ThomasException.class, () -> new AddCommand(new TodoTask("read book"))
                .execute(tasks, new Ui(), storage(), history));

        // The list refuses before the step is pushed, so a later undo must not
        // find a step that would remove the original.
        assertThrows(ThomasException.class, () -> history.undo(tasks));
        assertEquals(1, tasks.size());
    }

    @Test
    public void execute_duplicate_saveFileNotWritten() throws ThomasException {
        TaskList tasks = new TaskList();
        tasks.add(new TodoTask("read book"));

        assertThrows(ThomasException.class, () -> new AddCommand(new TodoTask("read book"))
                .execute(tasks, new Ui(), storage(), historyFor("todo read book")));

        // Nothing changed, so nothing was saved: the file was never created.
        assertFalse(Files.exists(saveFile()));
    }

    // ---- a save that fails ----

    @Test
    public void execute_saveFileIsAFolder_warningPrecedesConfirmation() throws ThomasException, IOException {
        Files.createDirectory(saveFile());
        TaskList tasks = new TaskList();

        String reply = new AddCommand(new TodoTask("read book"))
                .execute(tasks, new Ui(), storage(), historyFor("todo read book"));

        // The task is kept in memory and confirmed; the warning sits on the
        // line above so the user learns it will not survive a restart.
        assertEquals("Cinders and ashes! I couldn't save your tasks: '" + saveFile()
                + "' is a folder, and I need it to be a file. Move the folder out of the way.\n"
                + "Coupled up! This wagon is on the train now:\n"
                + "   [T][ ] read book\n"
                + "That's 1 wagon(s) behind me now.", reply);
        assertEquals(1, tasks.size());
    }

    @Test
    public void execute_saveFileIsAFolder_stepStillRecorded() throws ThomasException, IOException {
        Files.createDirectory(saveFile());
        TaskList tasks = new TaskList();
        History history = historyFor("todo read book");

        new AddCommand(new TodoTask("read book")).execute(tasks, new Ui(), storage(), history);

        // The change happened in memory, so it is one the user can take back.
        assertEquals("todo read book", history.undo(tasks));
        assertEquals(0, tasks.size());
    }

    // ---- not an exit ----

    @Test
    public void isExit_anyAddCommand_isFalse() {
        assertFalse(new AddCommand(new TodoTask("read book")).isExit());
    }
}
