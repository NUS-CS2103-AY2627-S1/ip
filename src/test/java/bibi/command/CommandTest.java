package bibi.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bibi.BibiException;
import bibi.Storage;
import bibi.Ui;
import bibi.task.Deadline;
import bibi.task.Event;
import bibi.task.Task;
import bibi.task.TaskList;
import bibi.task.Todo;

/**
 * Tests each command by running it against a real task list and reading back
 * what it said and what it changed.
 */
public class CommandTest {

    private static Ui createCapturingUi() {
        Ui ui = new Ui();
        ui.startCapture();
        return ui;
    }

    private static String run(Command command, TaskList tasks, Storage storage)
            throws BibiException {
        Ui ui = createCapturingUi();
        command.execute(tasks, ui, storage);
        return ui.takeCapturedReply().text();
    }

    private static Storage storageIn(Path folder) {
        return new Storage(folder.resolve("bibi.txt"));
    }

    @Test
    public void addCommand_newTask_addsConfirmsAndSaves(@TempDir Path tempDir)
            throws BibiException, IOException {
        TaskList tasks = new TaskList();
        Storage storage = storageIn(tempDir);

        String said = run(new AddCommand(new Todo("read book")), tasks, storage);

        assertEquals(1, tasks.size());
        assertTrue(said.contains("Added."));
        assertTrue(said.contains("1 task"));
        assertEquals("T | 0 | read book", Files.readString(storage.getFilePath()).strip());
    }

    @Test
    public void addCommand_taskAlreadyPresent_rejectedAndListUnchanged(@TempDir Path tempDir)
            throws BibiException {
        TaskList tasks = new TaskList(new Todo("read book"));

        BibiException thrown = assertThrows(BibiException.class, () ->
                run(new AddCommand(new Todo("read book")), tasks, storageIn(tempDir)));

        assertTrue(thrown.getMessage().contains("as task 1"));
        assertEquals(1, tasks.size());
    }

    @Test
    public void listCommand_emptyList_saysSoAndSuggestsWhatToDo(@TempDir Path tempDir)
            throws BibiException {
        String said = run(new ListCommand(), new TaskList(), storageIn(tempDir));

        assertTrue(said.contains("Nothing on the list"));
        assertTrue(said.contains("todo"));
    }

    @Test
    public void listCommand_severalTasks_numbersThemFromOne(@TempDir Path tempDir)
            throws BibiException {
        TaskList tasks = new TaskList(new Todo("first"), new Todo("second"));

        String said = run(new ListCommand(), tasks, storageIn(tempDir));

        assertTrue(said.contains("1. [T][ ] first"));
        assertTrue(said.contains("2. [T][ ] second"));
    }

    @Test
    public void markCommand_validNumber_marksAndSaves(@TempDir Path tempDir)
            throws BibiException, IOException {
        TaskList tasks = new TaskList(new Todo("read book"));
        Storage storage = storageIn(tempDir);

        String said = run(new MarkCommand(1), tasks, storage);

        assertTrue(said.contains("Task 1 done."));
        assertTrue(said.contains("[T][X] read book"));
        assertEquals("[T][X] read book", tasks.get(1).toString());
        assertEquals("T | 1 | read book", Files.readString(storage.getFilePath()).strip());
    }

    @Test
    public void markCommand_numberPastTheEnd_exceptionThrown(@TempDir Path tempDir)
            throws BibiException {
        TaskList tasks = new TaskList(new Todo("only one"));

        assertThrows(BibiException.class, () -> run(new MarkCommand(2), tasks, storageIn(tempDir)));
    }

    @Test
    public void markCommand_taskAlreadyDone_saysNothingChanged(@TempDir Path tempDir)
            throws BibiException {
        // Confirming a change that did not happen is how a user comes to believe
        // they ticked off a task they did not.
        TaskList tasks = new TaskList(new Todo("read book"));
        Storage storage = storageIn(tempDir);
        run(new MarkCommand(1), tasks, storage);

        String said = run(new MarkCommand(1), tasks, storage);

        assertTrue(said.contains("Task 1 was already done."));
        assertFalse(said.contains("Look at us getting things done."));
        assertTrue(said.contains("[T][X] read book"));
    }

    @Test
    public void unmarkCommand_taskAlreadyOpen_saysNothingChanged(@TempDir Path tempDir)
            throws BibiException {
        TaskList tasks = new TaskList(new Todo("read book"));
        Storage storage = storageIn(tempDir);

        String said = run(new UnmarkCommand(1), tasks, storage);

        assertTrue(said.contains("Task 1 was never done"));
        assertFalse(said.contains("We're not making progress"));
        assertTrue(said.contains("[T][ ] read book"));
        // Nothing changed, so nothing was written.
        assertFalse(Files.exists(storage.getFilePath()));
    }

    @Test
    public void markCommand_taskAlreadyDone_doesNotWarnAboutUnsavedWork(@TempDir Path tempDir)
            throws BibiException, IOException {
        // A save failure warns that the change is applied but unsaved. Under a
        // reply that has just said nothing changed, that warning cannot be true,
        // so a command that changes nothing must not attempt a save at all.
        Path inTheWay = Files.createDirectory(tempDir.resolve("bibi.txt"));
        TaskList tasks = new TaskList(new Todo("read book"));
        Storage storage = new Storage(inTheWay);
        run(new MarkCommand(1), tasks, storage);

        String said = run(new MarkCommand(1), tasks, storage);

        assertTrue(said.contains("was already done"));
        assertFalse(said.contains("isn't saved"));
    }

    @Test
    public void unmarkCommand_completedTask_reopensIt(@TempDir Path tempDir)
            throws BibiException {
        Task done = new Todo("read book");
        done.markComplete();
        TaskList tasks = new TaskList(done);

        String said = run(new UnmarkCommand(1), tasks, storageIn(tempDir));

        assertTrue(said.contains("open again"));
        assertTrue(said.contains("[T][ ] read book"));
        assertEquals("[T][ ] read book", tasks.get(1).toString());
    }

    @Test
    public void deleteCommand_validNumber_removesAndShortensTheList(@TempDir Path tempDir)
            throws BibiException {
        TaskList tasks = new TaskList(new Todo("first"), new Todo("second"));

        String said = run(new DeleteCommand(1), tasks, storageIn(tempDir));

        assertTrue(said.contains("off the list"));
        // The removed task is named, because once it is gone the numbers shift
        // and the number alone no longer says which task it was.
        assertTrue(said.contains("[T][ ] first"));
        assertTrue(said.contains("Your list holds 1 task."));
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] second", tasks.get(1).toString());
    }

    @Test
    public void helpCommand_always_listsTheCommands(@TempDir Path tempDir) throws BibiException {
        String said = run(new HelpCommand(false), new TaskList(), storageIn(tempDir));

        assertTrue(said.contains("deadline <description> /by <time>"));
        assertTrue(said.contains("sort"));
        assertTrue(said.contains("undo"));
    }

    @Test
    public void helpCommand_withoutTheFlag_omitsTheExamples(@TempDir Path tempDir) throws BibiException {
        String said = run(new HelpCommand(false), new TaskList(), storageIn(tempDir));

        // The short help is what someone who forgot a command word needs; the
        // reference detail behind the flag would bury it.
        assertFalse(said.contains("Examples:"));
        assertFalse(said.contains("d/M/yy uses 00-99 for 2000-2099."));
        assertTrue(said.contains("Run help --examples"));
    }

    @Test
    public void helpCommand_withTheFlag_addsExamplesAfterTheCommandList(@TempDir Path tempDir)
            throws BibiException {
        String said = run(new HelpCommand(true), new TaskList(), storageIn(tempDir));

        // The examples follow the list rather than replacing it, so the flag
        // reads as the short help with more added.
        assertTrue(said.contains("Usage: <command> [arguments]"));
        assertTrue(said.indexOf("Usage: <command> [arguments]") < said.indexOf("Examples:"));
        assertTrue(said.contains("deadline return book /by 21/12/2026 1800"));
        assertTrue(said.contains("d/M/yy uses 00-99 for 2000-2099."));
    }

    @Test
    public void exitCommand_always_saysGoodbyeAndEndsTheSession(@TempDir Path tempDir)
            throws BibiException {
        ExitCommand command = new ExitCommand();

        String said = run(command, new TaskList(), storageIn(tempDir));

        assertTrue(said.contains("See you."));
        assertTrue(command.isExit());
    }

    @Test
    public void isExit_everyCommandExceptExit_false() {
        assertFalse(new ListCommand().isExit());
        assertFalse(new HelpCommand(false).isExit());
        assertFalse(new SortCommand().isExit());
        assertFalse(new MarkCommand(1).isExit());
    }

    @Test
    public void sortCommand_emptyList_saysThereIsNothingToSort(@TempDir Path tempDir)
            throws BibiException {
        String said = run(new SortCommand(), new TaskList(), storageIn(tempDir));

        assertTrue(said.contains("Nothing to sort yet"));
    }

    @Test
    public void sortCommand_mixedTasks_ordersThemAndSaves(@TempDir Path tempDir)
            throws BibiException, IOException {
        TaskList tasks = new TaskList(
                new Todo("undated"),
                new Deadline("later", "2019-12-01"),
                new Deadline("sooner", "2019-10-15"));
        Storage storage = storageIn(tempDir);

        String said = run(new SortCommand(), tasks, storage);

        assertTrue(said.contains("Sorted it, but not your life. Earliest first:"));
        assertEquals("[D][ ] sooner (by: 15 Oct 2019)", tasks.get(1).toString());
        assertEquals("[T][ ] undated", tasks.get(3).toString());
        assertTrue(Files.readString(storage.getFilePath()).startsWith("D | 0 | sooner"));
    }

    @Test
    public void findCommand_matchingTasks_keepTheirNumbersFromTheFullList(@TempDir Path tempDir)
            throws BibiException {
        TaskList tasks = new TaskList(
                new Todo("walk dog"), new Todo("read book"), new Todo("return book"));

        String said = run(new FindCommand("book"), tasks, storageIn(tempDir));

        // The numbers must be the ones list shows, so a match can be marked
        // straight away without listing everything first.
        assertTrue(said.contains("2. [T][ ] read book"));
        assertTrue(said.contains("3. [T][ ] return book"));
        assertFalse(said.contains("walk dog"));
    }

    @Test
    public void findCommand_noMatch_saysSoWithTheKeyword(@TempDir Path tempDir)
            throws BibiException {
        TaskList tasks = new TaskList(new Todo("walk dog"));

        assertTrue(run(new FindCommand("zzz"), tasks, storageIn(tempDir))
                .contains("Nothing matches 'zzz'"));
    }

    @Test
    public void onCommand_tasksOnThatDate_shownWithTheirNumbers(@TempDir Path tempDir)
            throws BibiException {
        TaskList tasks = new TaskList(
                new Todo("undated"),
                new Deadline("pay fees", "2019-10-15"),
                new Event("camp", "2019-10-14", "2019-10-16"));

        String said = run(new OnCommand(LocalDate.of(2019, 10, 15)), tasks, storageIn(tempDir));

        assertTrue(said.contains("2. [D][ ] pay fees"));
        // A multi-day event counts on every day it runs.
        assertTrue(said.contains("3. [E][ ] camp"));
        assertFalse(said.contains("undated"));
    }

    @Test
    public void onCommand_nothingOnThatDate_saysSo(@TempDir Path tempDir) throws BibiException {
        TaskList tasks = new TaskList(new Todo("undated"));

        assertTrue(run(new OnCommand(LocalDate.of(2019, 10, 15)), tasks, storageIn(tempDir))
                .contains("Nothing on 15 Oct 2019"));
    }

    @Test
    public void saveTasks_unwritableLocation_reportedWithoutLosingTheChange(@TempDir Path tempDir)
            throws BibiException, IOException {
        // A folder standing where the save file belongs: the add must still
        // happen in memory, with the failure reported rather than thrown.
        Path inTheWay = tempDir.resolve("bibi.txt");
        Files.createDirectory(inTheWay);
        TaskList tasks = new TaskList();

        String said = run(new AddCommand(new Todo("read book")), tasks, new Storage(inTheWay));

        assertEquals(1, tasks.size());
        assertTrue(said.contains("Added."));
        assertTrue(said.contains("is a folder"));
        assertTrue(said.contains("The change is applied in memory but isn't saved."));
        assertTrue(said.contains("Don't repeat it."));
    }

    @Test
    public void socialCommand_greetingOrThanks_repliesWithoutSavingOrChangingTasks(@TempDir Path tempDir)
            throws BibiException {
        TaskList tasks = new TaskList(new Todo("say hello"));
        Storage storage = storageIn(tempDir);

        String greeting = run(new SocialCommand(false), tasks, storage);
        String thanks = run(new SocialCommand(true), tasks, storage);

        assertEquals("Hey. I'm here. Type help if you need the rundown.", greeting);
        assertEquals("You're welcome. You handle the doing; I'll handle the remembering.", thanks);
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] say hello", tasks.get(1).toString());
        assertFalse(Files.exists(storage.getFilePath()));
    }
}
