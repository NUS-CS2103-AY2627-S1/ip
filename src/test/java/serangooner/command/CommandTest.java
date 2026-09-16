package serangooner.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import serangooner.Parser;
import serangooner.SerangoonerException;
import serangooner.storage.Storage;
import serangooner.task.Deadline;
import serangooner.task.TaskList;
import serangooner.task.Todo;
import serangooner.ui.Ui;

public class CommandTest {
    private final Ui ui = new Ui();

    private static Storage storageIn(Path directory) {
        return new Storage(directory.resolve("tasks.txt"));
    }

    @Test
    public void execute_addCommand_addsTaskReportsItAndSavesIt(@TempDir Path directory) {
        TaskList tasks = new TaskList();
        Storage storage = storageIn(directory);

        String response = new AddCommand(new Todo("read book")).execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertTrue(response.startsWith("added todo: [T][ ] read book"));
        assertEquals(1, storage.load().tasks().size());
    }

    @Test
    public void execute_markCommand_marksTaskAndSavesIt(@TempDir Path directory) {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        Storage storage = storageIn(directory);

        new MarkCommand(1).execute(tasks, ui, storage);

        assertTrue(tasks.getTasks().get(0).isDone());
        assertTrue(storage.load().tasks().get(0).isDone());
    }

    @Test
    public void execute_unmarkCommand_marksTaskIncomplete(@TempDir Path directory) {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        Storage storage = storageIn(directory);
        new MarkCommand(1).execute(tasks, ui, storage);

        new UnmarkCommand(1).execute(tasks, ui, storage);

        assertFalse(tasks.getTasks().get(0).isDone());
    }

    @Test
    public void execute_markCommandOutOfRange_exceptionThrownAndNothingSaved(
            @TempDir Path directory) {
        TaskList tasks = new TaskList();
        Storage storage = storageIn(directory);

        assertThrows(SerangoonerException.class, () ->
                new MarkCommand(1).execute(tasks, ui, storage));
        assertTrue(storage.load().tasks().isEmpty());
    }

    @Test
    public void execute_deleteCommand_removesTaskAndSavesIt(@TempDir Path directory) {
        TaskList tasks = new TaskList(List.of(new Todo("read book"), new Todo("write essay")));
        Storage storage = storageIn(directory);

        String response = new DeleteCommand(1).execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertTrue(response.startsWith("deleted task: [T][ ] read book"));
        assertEquals(1, storage.load().tasks().size());
    }

    @Test
    public void execute_undoCommand_reversesTheLastChange(@TempDir Path directory) {
        TaskList tasks = new TaskList();
        Storage storage = storageIn(directory);
        new AddCommand(new Todo("read book")).execute(tasks, ui, storage);

        new UndoCommand().execute(tasks, ui, storage);

        assertEquals(0, tasks.size());
        assertTrue(storage.load().tasks().isEmpty());
    }

    @Test
    public void execute_undoCommandWithNothingToUndo_saysSo(@TempDir Path directory) {
        String response = new UndoCommand().execute(new TaskList(), ui, storageIn(directory));
        assertTrue(response.startsWith("there's nothing to undo"));
    }

    @Test
    public void execute_undoCommandWithNothingToUndo_leavesTheSaveFileUntouched(@TempDir Path directory)
            throws IOException {
        Path file = directory.resolve("tasks.txt");
        List<String> original = List.of("T | 0 | read book", "T | 7 | line a later fix may recover");
        Files.write(file, original);

        new UndoCommand().execute(new TaskList(), ui, new Storage(file));

        assertEquals(original, Files.readAllLines(file));
    }

    @Test
    public void execute_listCommand_showsEveryTask(@TempDir Path directory) {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        String response = new ListCommand().execute(tasks, ui, storageIn(directory));

        assertTrue(response.contains(" 1. [T][ ] read book"));
    }

    @Test
    public void execute_onCommand_showsOnlyTasksInTheRange(@TempDir Path directory) {
        TaskList tasks = new TaskList(List.of(
                new Deadline("submit ip", "2026-09-01"),
                new Deadline("submit tp", "2026-09-09")));

        String response = new OnCommand(Parser.parseDateRange("on 2026-09-01"))
                .execute(tasks, ui, storageIn(directory));

        assertTrue(response.contains("submit ip"));
        assertFalse(response.contains("submit tp"));
    }

    @Test
    public void execute_helpCommand_listsEveryCommand(@TempDir Path directory) {
        String response = new HelpCommand().execute(new TaskList(), ui, storageIn(directory));

        assertTrue(response.startsWith("serangooner commands:"));
        for (CommandType command : CommandType.values()) {
            assertTrue(response.contains(command.getSyntax() + " - " + command.getDescription()));
        }
    }

    @Test
    public void execute_exitCommand_saysBye(@TempDir Path directory) {
        assertEquals("bye~", new ExitCommand().execute(new TaskList(), ui, storageIn(directory)));
    }

    @Test
    public void isExit_exitCommand_returnsTrue() {
        assertTrue(new ExitCommand().isExit());
    }

    @Test
    public void isExit_anyOtherCommand_returnsFalse() {
        assertFalse(new ListCommand().isExit());
        assertFalse(new AddCommand(new Todo("read book")).isExit());
        assertFalse(new UndoCommand().isExit());
    }

    @Test
    public void execute_addCommandOnNonEmptyList_appendsAndReportsTheNewCount(
            @TempDir Path directory) {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        Storage storage = storageIn(directory);

        String response = new AddCommand(new Deadline("submit ip", "2026-09-01"))
                .execute(tasks, ui, storage);

        assertEquals(2, tasks.size());
        assertEquals("[D][ ] submit ip (by: 01 Sep 2026)",
                tasks.getTasks().get(1).toString());
        assertTrue(response.contains("you now have 2 pending task(s)"));
    }

    @Test
    public void execute_markCommand_showsTheMarkedTask(@TempDir Path directory) {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        String response = new MarkCommand(1).execute(tasks, ui, storageIn(directory));

        assertTrue(response.startsWith("marked task as done: [T][✓] read book"));
    }

    @Test
    public void execute_unmarkCommand_showsTheTaskAndSavesIt(@TempDir Path directory) {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        Storage storage = storageIn(directory);
        new MarkCommand(1).execute(tasks, ui, storage);

        String response = new UnmarkCommand(1).execute(tasks, ui, storage);

        assertTrue(response.startsWith("marked task as incomplete: [T][ ] read book"));
        assertFalse(storage.load().tasks().get(0).isDone());
    }

    @Test
    public void execute_unmarkCommandOutOfRange_exceptionThrownAndNothingSaved(
            @TempDir Path directory) {
        TaskList tasks = new TaskList();
        Storage storage = storageIn(directory);

        assertThrows(SerangoonerException.class, () ->
                new UnmarkCommand(1).execute(tasks, ui, storage));
        assertTrue(storage.load().tasks().isEmpty());
    }

    @Test
    public void execute_deleteCommandOutOfRange_exceptionThrownAndListUntouched(
            @TempDir Path directory) {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        Storage storage = storageIn(directory);

        assertThrows(SerangoonerException.class, () ->
                new DeleteCommand(2).execute(tasks, ui, storage));
        assertEquals(1, tasks.size());
    }

    @Test
    public void execute_undoCommandAfterMark_leavesTheTaskIncompleteAndSavesIt(
            @TempDir Path directory) {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        Storage storage = storageIn(directory);
        new MarkCommand(1).execute(tasks, ui, storage);

        new UndoCommand().execute(tasks, ui, storage);

        assertFalse(tasks.getTasks().get(0).isDone());
        assertFalse(storage.load().tasks().get(0).isDone());
    }

    @Test
    public void execute_undoCommandAfterDelete_putsTheTaskBackAndSavesIt(
            @TempDir Path directory) {
        TaskList tasks = new TaskList(List.of(new Todo("read book"), new Todo("write essay")));
        Storage storage = storageIn(directory);
        new DeleteCommand(1).execute(tasks, ui, storage);

        new UndoCommand().execute(tasks, ui, storage);

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] read book", tasks.getTasks().get(0).toString());
        assertEquals(2, storage.load().tasks().size());
    }

    @Test
    public void execute_listCommandOnEmptyList_saysThereIsNothing(@TempDir Path directory) {
        String response = new ListCommand().execute(new TaskList(), ui, storageIn(directory));

        assertFalse(response.isBlank());
        assertFalse(response.contains(" 1. "));
    }

    @Test
    public void execute_onCommandMatchingNothing_saysSo(@TempDir Path directory) {
        TaskList tasks = new TaskList(List.of(new Deadline("submit ip", "2026-09-01")));

        String response = new OnCommand(Parser.parseDateRange("on 2026-10-01"))
                .execute(tasks, ui, storageIn(directory));

        assertFalse(response.contains("submit ip"));
    }

    @Test
    public void execute_onCommandOverASpan_showsEveryTaskInIt(@TempDir Path directory) {
        TaskList tasks = new TaskList(List.of(
                new Deadline("submit ip", "2026-09-01"),
                new Deadline("submit tp", "2026-09-09"),
                new Deadline("submit cp", "2026-10-01")));

        String response = new OnCommand(Parser.parseDateRange("on 2026-09-01 to 2026-09-09"))
                .execute(tasks, ui, storageIn(directory));

        assertTrue(response.contains("submit ip"));
        assertTrue(response.contains("submit tp"));
        assertFalse(response.contains("submit cp"));
    }

    @Test
    public void execute_readOnlyCommands_leaveTheSaveFileAlone(@TempDir Path directory) {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        Storage storage = storageIn(directory);

        new ListCommand().execute(tasks, ui, storage);
        new HelpCommand().execute(tasks, ui, storage);
        new ExitCommand().execute(tasks, ui, storage);

        assertTrue(storage.load().tasks().isEmpty());
    }

    @Test
    public void execute_addCommandWhenSavingFails_exceptionThrownAndTheTaskStaysInMemory(
            @TempDir Path directory) throws IOException {
        // A directory sitting where the save file should be cannot be written.
        Path file = directory.resolve("tasks.txt");
        Files.createDirectory(file);
        TaskList tasks = new TaskList();

        assertThrows(SerangoonerException.class, () -> new AddCommand(new Todo("read book"))
                .execute(tasks, ui, new Storage(file)));

        assertEquals(1, tasks.size());
    }

    @Test
    public void execute_deleteCommandOutOfRange_exceptionCarriesAMessageForTheUser(
            @TempDir Path directory) {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        SerangoonerException exception = assertThrows(SerangoonerException.class, () ->
                new DeleteCommand(2).execute(tasks, ui, storageIn(directory)));

        assertFalse(exception.getMessage().isBlank());
    }

    @Test
    public void execute_findCommand_showsOnlyTheMatchingTasks(@TempDir Path directory) {
        TaskList tasks = new TaskList(List.of(new Todo("read book"),
                new Todo("write essay"), new Deadline("return book", "2026-09-01")));

        String response = new FindCommand("book").execute(tasks, ui, storageIn(directory));

        assertTrue(response.startsWith("matching tasks:"));
        assertTrue(response.contains("read book"));
        assertTrue(response.contains("return book"));
        assertFalse(response.contains("write essay"));
    }

    @Test
    public void execute_findCommand_showsTheNumbersFromTheFullList(@TempDir Path directory) {
        TaskList tasks = new TaskList(List.of(new Todo("write essay"), new Todo("read book")));

        String response = new FindCommand("book").execute(tasks, ui, storageIn(directory));

        assertTrue(response.contains(" 2. [T][ ] read book"));
    }

    @Test
    public void execute_findCommandMatchingNothing_saysSo(@TempDir Path directory) {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        String response = new FindCommand("essay").execute(tasks, ui, storageIn(directory));

        assertTrue(response.startsWith("no task mentions 'essay'"));
    }

    @Test
    public void execute_findCommand_leavesTheSaveFileAlone(@TempDir Path directory) {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        Storage storage = storageIn(directory);

        new FindCommand("book").execute(tasks, ui, storage);

        assertTrue(storage.load().tasks().isEmpty());
    }

    @Test
    public void isExit_findCommand_returnsFalse() {
        assertFalse(new FindCommand("book").isExit());
    }

    @Test
    public void execute_addCommandForADuplicate_exceptionThrownAndNothingSaved(
            @TempDir Path directory) {
        TaskList tasks = new TaskList();
        Storage storage = storageIn(directory);
        new AddCommand(new Todo("read book")).execute(tasks, ui, storage);

        assertThrows(SerangoonerException.class, () ->
                new AddCommand(new Todo("Read Book")).execute(tasks, ui, storage));

        assertEquals(1, tasks.size());
        assertEquals(1, storage.load().tasks().size());
    }
}
