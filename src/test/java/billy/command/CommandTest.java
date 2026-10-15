package billy.command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import billy.BillyException;
import billy.parser.CommandWord;
import billy.storage.Storage;
import billy.task.Deadline;
import billy.task.Event;
import billy.task.TaskDate;
import billy.task.TaskList;
import billy.task.Todo;
import billy.ui.Ui;

/**
 * Tests the numbering {@link Command} shares with the commands that show part of
 * the list.
 *
 * <p>The number beside a task is its place in the whole list, not its place among
 * the matches. That is what lets the user mark or delete a task straight after
 * finding it, and it is the one thing about this method that is easy to get wrong
 * and expensive when wrong: numbering the results instead would send {@code mark}
 * to a different task than the one the user just read.
 */
public class CommandTest {

    /** Builds a list holding the given descriptions as todos, in order. */
    private static TaskList listOf(String... descriptions) {
        TaskList tasks = new TaskList();
        for (String description : descriptions) {
            tasks.add(new Todo(description));
        }
        return tasks;
    }

    @Test
    public void numberMatching_someTasksMatch_numbersFollowTheWholeList() {
        TaskList tasks = listOf("read book", "return book", "read paper");

        List<String> found = Command.numberMatching(tasks,
                task -> task.descriptionContains("read"));

        // The second match is task 3 of the list, not task 2 of the results.
        assertEquals(List.of("1.[T][ ] read book", "3.[T][ ] read paper"), found);
    }

    @Test
    public void numberMatching_lastTaskOnlyMatch_keepsItsOwnNumber() {
        TaskList tasks = listOf("first", "second", "third");

        List<String> found = Command.numberMatching(tasks,
                task -> task.descriptionContains("third"));

        assertEquals(List.of("3.[T][ ] third"), found);
    }

    @Test
    public void numberMatching_everyTaskMatches_numberedFromOne() {
        TaskList tasks = listOf("first", "second");

        // This is how ListCommand asks for the whole list.
        List<String> lines = Command.numberMatching(tasks, task -> true);

        assertEquals(List.of("1.[T][ ] first", "2.[T][ ] second"), lines);
    }

    @Test
    public void numberMatching_nothingMatches_noLines() {
        TaskList tasks = listOf("read book");

        assertTrue(Command.numberMatching(tasks, task -> false).isEmpty());
    }

    @Test
    public void numberMatching_emptyList_noLines() {
        assertTrue(Command.numberMatching(new TaskList(), task -> true).isEmpty());
    }

    // ---------------------------------------------------------------
    // help
    // ---------------------------------------------------------------

    @Test
    public void execute_help_showsEveryCommandAndShorterWord() {
        // Worth testing through the command rather than only through
        // CommandWord, since a HelpCommand that built the listing and forgot to
        // show it would pass every test over there.
        Ui ui = new Ui();
        ui.startCollecting();
        new HelpCommand().execute(new TaskList(), ui, new Storage(Path.of("unused.txt")));
        String shown = ui.stopCollecting();

        for (CommandWord command : CommandWord.values()) {
            assertTrue(shown.contains(command.getKeyword()), command.getKeyword() + " should be shown");
            for (String alias : command.getAliases()) {
                assertTrue(shown.contains(alias), alias + " should be shown");
            }
        }
    }

    @Test
    public void execute_help_touchesNeitherTheListNorTheSaveFile() {
        // The only command that does neither. Storage points at a file that does
        // not exist and must stay that way: writing to it would be a bug the
        // user would only find when it overwrote something.
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Path unwritten = Path.of("build", "should-not-be-written.txt");

        Ui ui = new Ui();
        ui.startCollecting();
        new HelpCommand().execute(tasks, ui, new Storage(unwritten));
        ui.stopCollecting();

        assertEquals(1, tasks.size());
        assertFalse(Files.exists(unwritten), "help must not write a save file");
    }

    // ---------------------------------------------------------------
    // What Billy remarks on
    // ---------------------------------------------------------------

    /** Runs a command against a list and returns everything Billy said. */
    private static String say(Command command, TaskList tasks) throws BillyException {
        Ui ui = new Ui();
        ui.startCollecting();
        command.execute(tasks, ui, new Storage(Path.of("build", "command-test.txt")));
        return ui.stopCollecting();
    }

    @Test
    public void execute_markTheLastOutstandingTask_saysSoOutright() throws BillyException {
        TaskList tasks = listOf("read book");
        assertTrue(say(new MarkCommand(1, true), tasks).contains("whole list conquered"));
    }

    @Test
    public void execute_markOneOfSeveral_noSuchRemark() throws BillyException {
        // The remark must wait for the last one. Firing on every mark would make
        // it meaningless, which is the easy way to get this wrong.
        TaskList tasks = listOf("read book", "write essay");
        assertFalse(say(new MarkCommand(1, true), tasks).contains("whole list conquered"));
    }

    @Test
    public void execute_deleteTheLastTask_saysTheListIsEmpty() throws BillyException {
        TaskList tasks = listOf("read book");
        String said = say(new DeleteCommand(1), tasks);
        assertTrue(said.contains("last of them"), said);
        // A count of zero would be the wrong thing to report here.
        assertFalse(said.contains("0 tasks"), said);
    }

    @Test
    public void execute_deleteOneOfSeveral_reportsTheCountInstead() throws BillyException {
        TaskList tasks = listOf("read book", "write essay");
        String said = say(new DeleteCommand(1), tasks);
        assertTrue(said.contains("1 task on the books"), said);
        assertFalse(said.contains("last of them"), said);
    }

    // ---------------------------------------------------------------
    // Warning about a duplicate
    // ---------------------------------------------------------------

    @Test
    public void execute_addATaskAlreadyOnTheList_warnsAndNamesTheUndo() throws BillyException {
        TaskList tasks = listOf("read book");
        String said = say(new AddCommand(new Todo("read book")), tasks);

        assertTrue(said.contains("same as task 1"), said);
        // The number offered must be the new task, not the original: deleting
        // the original would leave the accidental copy sitting in its place.
        assertTrue(said.contains("delete 2"), said);
        // Still added. Refusing it would decide for the user that two errands
        // worded alike cannot both be real.
        assertEquals(2, tasks.size());
    }

    @Test
    public void execute_addATaskMatchingSeveral_namesThemAll() throws BillyException {
        TaskList tasks = listOf("read book", "write essay", "read book");
        String said = say(new AddCommand(new Todo("read book")), tasks);
        assertTrue(said.contains("same as tasks 1 and 3"), said);
        assertTrue(said.contains("delete 4"), said);
    }

    @Test
    public void execute_addAnUnrelatedTask_noWarning() throws BillyException {
        TaskList tasks = listOf("read book");
        assertFalse(say(new AddCommand(new Todo("write essay")), tasks).contains("Heads up"));
    }

    // ---------------------------------------------------------------
    // on
    // ---------------------------------------------------------------

    /** Builds a list holding one todo, one deadline and one event, in that order. */
    private static TaskList mixedList() throws BillyException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", TaskDate.parse("2019-12-02 1800")));
        tasks.add(new Event("conference", TaskDate.parse("2019-12-04"),
                TaskDate.parse("2019-12-06")));
        return tasks;
    }

    @Test
    public void execute_onADayWithTasks_showsThemWithTheirRealNumbers() throws BillyException {
        String said = say(new OnCommand(LocalDate.of(2019, 12, 2)), mixedList());
        assertTrue(said.contains("Dec 2 2019"), said);
        // Numbered 2 because it is the second task in the whole list, not the
        // first of the matches. That is what lets `mark 2` follow straight on.
        assertTrue(said.contains("2.[D][ ] return book"), said);
        assertFalse(said.contains("read book"), said);
    }

    @Test
    public void execute_onADayInTheMiddleOfAnEvent_eventShown() throws BillyException {
        // An event covers every day it spans, not just the day it starts, so the
        // 5th finds an event running from the 4th to the 6th.
        String said = say(new OnCommand(LocalDate.of(2019, 12, 5)), mixedList());
        assertTrue(said.contains("3.[E][ ] conference"), said);
    }

    @Test
    public void execute_onAnEmptyDay_saysSoRatherThanShowingAnEmptyHeading() throws BillyException {
        String said = say(new OnCommand(LocalDate.of(2019, 1, 1)), mixedList());
        assertTrue(said.contains("Nothing on Jan 1 2019"), said);
        assertFalse(said.contains("has in store"), said);
    }

    @Test
    public void execute_onADay_listLeftAlone() throws BillyException {
        TaskList tasks = mixedList();
        say(new OnCommand(LocalDate.of(2019, 12, 2)), tasks);
        assertEquals(3, tasks.size());
    }

    // ---------------------------------------------------------------
    // find
    // ---------------------------------------------------------------

    @Test
    public void execute_findAWordThatMatches_shownWithRealNumbers() throws BillyException {
        String said = say(new FindCommand("book"), mixedList());
        assertTrue(said.contains("1.[T][ ] read book"), said);
        assertTrue(said.contains("2.[D][ ] return book"), said);
        assertFalse(said.contains("conference"), said);
    }

    @Test
    public void execute_findAWordThatMatchesNothing_quotesTheWordBack() throws BillyException {
        // The word is quoted so a typo in the search itself is easy to spot.
        String said = say(new FindCommand("zebra"), mixedList());
        assertTrue(said.contains("'zebra'"), said);
    }

    @Test
    public void execute_findMatchingOnlyLaterTasks_numbersNotRestarted() throws BillyException {
        // The fault worth guarding against: numbering the results rather than the
        // list would show this as 1, and `delete 1` would then hit the wrong task.
        String said = say(new FindCommand("conference"), mixedList());
        assertTrue(said.contains("3.[E][ ] conference"), said);
        assertFalse(said.contains("1.[E]"), said);
    }

    // ---------------------------------------------------------------
    // unmark
    // ---------------------------------------------------------------

    @Test
    public void execute_unmarkADoneTask_reportedAsNotDoneAgain() throws BillyException {
        TaskList tasks = listOf("read book");
        say(new MarkCommand(1, true), tasks);
        String said = say(new MarkCommand(1, false), tasks);

        assertTrue(said.contains("Un-done!"), said);
        assertTrue(said.contains("[T][ ] read book"), said);
    }

    @Test
    public void execute_unmarkTheLastDoneTask_noCongratulation() throws BillyException {
        // Unmarking must take the list back out of the finished state, or Billy
        // would congratulate the user for a list they just reopened.
        TaskList tasks = listOf("read book");
        say(new MarkCommand(1, true), tasks);
        assertFalse(say(new MarkCommand(1, false), tasks).contains("conquered"), "unmark");
    }

    // ---------------------------------------------------------------
    // What happens when the list cannot be written
    // ---------------------------------------------------------------

    @Test
    public void execute_saveFails_changeKeptAndTheUserWarned(@TempDir Path folder)
            throws BillyException {
        // A folder cannot be written to as if it were a file, which is the
        // sturdiest way to make saving fail on any operating system.
        Path unwritable = folder.resolve("billy.txt");
        assertDoesNotThrow(() -> Files.createDirectory(unwritable));

        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        ui.startCollecting();
        new AddCommand(new Todo("read book")).execute(tasks, ui, new Storage(unwritable));
        String said = ui.stopCollecting();

        // The change itself worked, so the confirmation still stands; only the
        // saving of it failed, and the user is told it will not outlive the run.
        assertTrue(said.contains("Consider it written down"), said);
        assertTrue(said.contains("couldn't save"), said);
        assertTrue(said.contains("will be lost"), said);
        assertEquals(1, tasks.size());
    }

    @Test
    public void execute_saveFails_reportedAsAnError(@TempDir Path folder) throws BillyException {
        // The window shows failures differently, and it can only do that if this
        // path goes through showError rather than show.
        Path unwritable = folder.resolve("billy.txt");
        assertDoesNotThrow(() -> Files.createDirectory(unwritable));

        Ui ui = new Ui();
        ui.startCollecting();
        new AddCommand(new Todo("read book"))
                .execute(new TaskList(), ui, new Storage(unwritable));
        assertTrue(ui.isErrorCollected());
        ui.stopCollecting();
    }
}
