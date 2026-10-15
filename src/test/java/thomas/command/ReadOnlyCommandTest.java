package thomas.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import thomas.ThomasException;
import thomas.storage.Storage;
import thomas.task.DeadlineTask;
import thomas.task.Task;
import thomas.task.TaskList;
import thomas.task.TodoTask;
import thomas.ui.Ui;

/**
 * Tests the commands that change nothing: list, find, on and bye.
 * Together in one file because what matters about all four is the same: each
 * hands back what Ui words for it, and none saves or records an undo step. How
 * the listings are worded and numbered is UiTest's business; what is checked
 * here is that each command asks for the right one, and that the list, the file
 * and the history are all left as they were.
 * The exit flag is pinned for all four as well, since it is the one thing the
 * read loop asks of a command after running it, and bye is the only command
 * that may answer yes.
 * Each case gets its own save file in a temporary folder, so that a save can be
 * detected as the file appearing.
 */
public class ReadOnlyCommandTest {

    private static final LocalDate DEC_02 = LocalDate.of(2019, 12, 2);

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
     * Returns a list holding the given tasks, in order.
     *
     * @param tasks Tasks to hold, in list order.
     * @return List holding those tasks.
     */
    private static TaskList listOf(Task... tasks) {
        return new TaskList(new ArrayList<>(List.of(tasks)));
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

    // ---- list ----

    @Test
    public void listExecute_severalTasks_returnsTheWholeListNumbered() throws ThomasException {
        TaskList tasks = listOf(new TodoTask("read book"), new TodoTask("buy milk"));

        String reply = new ListCommand().execute(tasks, new Ui(), storage(), historyFor("list"));

        assertEquals("Here is every wagon on my train:\n1. [T][ ] read book\n2. [T][ ] buy milk", reply);
    }

    @Test
    public void listExecute_emptyList_returnsTheHeaderAlone() throws ThomasException {
        String reply = new ListCommand().execute(listOf(), new Ui(), storage(), historyFor("list"));

        assertEquals("Here is every wagon on my train:", reply);
    }

    @Test
    public void listExecute_anyList_savesNothingAndRecordsNothing() throws ThomasException {
        TaskList tasks = listOf(new TodoTask("read book"));
        History history = historyFor("list");

        new ListCommand().execute(tasks, new Ui(), storage(), history);

        assertFalse(Files.exists(saveFile()));
        assertThrows(ThomasException.class, () -> history.undo(tasks));
        assertEquals(1, tasks.size());
    }

    // ---- find ----

    @Test
    public void findExecute_keywordInSomeTasks_returnsThoseNumberedByListPosition() throws ThomasException {
        TaskList tasks = listOf(new TodoTask("read book"), new TodoTask("swim"), new TodoTask("buy book"));

        String reply = new FindCommand("book").execute(tasks, new Ui(), storage(), historyFor("find book"));

        assertEquals("I searched the yard and found these wagons:\n1. [T][ ] read book\n3. [T][ ] buy book",
                reply);
    }

    @Test
    public void findExecute_keywordInNoTask_returnsTheHeaderAlone() throws ThomasException {
        TaskList tasks = listOf(new TodoTask("read book"));

        String reply = new FindCommand("swim").execute(tasks, new Ui(), storage(), historyFor("find swim"));

        assertEquals("I searched the yard and found these wagons:", reply);
    }

    @Test
    public void findExecute_anyKeyword_savesNothingAndRecordsNothing() throws ThomasException {
        TaskList tasks = listOf(new TodoTask("read book"));
        History history = historyFor("find book");

        new FindCommand("book").execute(tasks, new Ui(), storage(), history);

        assertFalse(Files.exists(saveFile()));
        assertThrows(ThomasException.class, () -> history.undo(tasks));
        assertEquals(1, tasks.size());
    }

    // ---- on ----

    @Test
    public void onExecute_deadlineOnThatDay_returnsItNumberedByListPosition() throws ThomasException {
        TaskList tasks = listOf(new TodoTask("read book"), new DeadlineTask("return book", DEC_02.atTime(18, 0)));

        String reply = new OnCommand(DEC_02).execute(tasks, new Ui(), storage(), historyFor("on 2019-12-02"));

        assertEquals("Here is my timetable for Dec 02 2019:\n2. [D][ ] return book (by: Dec 02 2019, 6:00 PM)",
                reply);
    }

    @Test
    public void onExecute_nothingOnThatDay_returnsTheHeaderAlone() throws ThomasException {
        TaskList tasks = listOf(new TodoTask("read book"));

        String reply = new OnCommand(DEC_02).execute(tasks, new Ui(), storage(), historyFor("on 2019-12-02"));

        assertEquals("Here is my timetable for Dec 02 2019:", reply);
    }

    @Test
    public void onExecute_anyDay_savesNothingAndRecordsNothing() throws ThomasException {
        TaskList tasks = listOf(new DeadlineTask("return book", DEC_02.atTime(18, 0)));
        History history = historyFor("on 2019-12-02");

        new OnCommand(DEC_02).execute(tasks, new Ui(), storage(), history);

        assertFalse(Files.exists(saveFile()));
        assertThrows(ThomasException.class, () -> history.undo(tasks));
        assertEquals(1, tasks.size());
    }

    // ---- bye ----

    @Test
    public void exitExecute_anyList_returnsTheFarewell() {
        String reply = new ExitCommand().execute(listOf(new TodoTask("read book")), new Ui(), storage(),
                historyFor("bye"));

        assertEquals("Off to the sheds! Peep peep, see you down the line!", reply);
    }

    @Test
    public void exitExecute_anyList_savesNothingAndRecordsNothing() {
        TaskList tasks = listOf(new TodoTask("read book"));
        History history = historyFor("bye");

        new ExitCommand().execute(tasks, new Ui(), storage(), history);

        // Every change was saved as it was made, so bye has nothing to write.
        assertFalse(Files.exists(saveFile()));
        assertThrows(ThomasException.class, () -> history.undo(tasks));
    }

    // ---- which command ends the session ----

    @Test
    public void isExit_exitCommand_isTrue() {
        assertTrue(new ExitCommand().isExit());
    }

    @Test
    public void isExit_listCommand_isFalse() {
        assertFalse(new ListCommand().isExit());
    }

    @Test
    public void isExit_findCommand_isFalse() {
        assertFalse(new FindCommand("book").isExit());
    }

    @Test
    public void isExit_onCommand_isFalse() {
        assertFalse(new OnCommand(DEC_02).isExit());
    }
}
