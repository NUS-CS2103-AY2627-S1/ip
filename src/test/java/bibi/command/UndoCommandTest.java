package bibi.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bibi.BibiException;
import bibi.Reply;
import bibi.Storage;
import bibi.Ui;
import bibi.task.Deadline;
import bibi.task.Task;
import bibi.task.TaskList;
import bibi.task.Todo;

/**
 * Tests single-step undo through commands, including saving and rejected changes.
 */
public class UndoCommandTest {
    private static Reply run(Command command, TaskList tasks, Storage storage) throws BibiException {
        Ui ui = new Ui();
        ui.startCapture();
        command.execute(tasks, ui, storage);
        return ui.takeCapturedReply();
    }

    @Test
    public void execute_eachChangingCommand_restoresAndSavesPreviousList(@TempDir Path tempDir)
            throws BibiException, IOException {
        List<Command> changes = List.of(new AddCommand(new Todo("added")), new DeleteCommand(1),
                new MarkCommand(1), new UnmarkCommand(2), new SortCommand());
        List<String> expectedSavedTasks = List.of("T | 0 | undated", "D | 1 | due | 2019-12-02 1800");

        for (Command change : changes) {
            Task due = new Deadline("due", "2019-12-02 1800");
            due.markComplete();
            TaskList tasks = new TaskList(new Todo("undated"), due);
            Storage storage = new Storage(tempDir.resolve("bibi.txt"));
            run(change, tasks, storage);

            Reply reply = run(new UndoCommand(), tasks, storage);

            assertEquals("Fickle, aren't ya? Last change undone. Your list holds 2 tasks.\n"
                    + "1. [T][ ] undated\n2. [D][X] due (by: 02 Dec 2019 6:00PM)", reply.text());
            assertFalse(reply.isError());
            assertEquals(expectedSavedTasks, Files.readAllLines(storage.getFilePath()));
            assertEquals(expectedSavedTasks, storage.load().tasks().stream().map(Task::toFileFormat).toList());
            assertThrows(BibiException.class, tasks::undo);
        }
    }

    @Test
    public void execute_addedOnlyTask_restoresAndSavesEmptyList(@TempDir Path tempDir)
            throws BibiException, IOException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("bibi.txt"));
        run(new AddCommand(new Todo("added")), tasks, storage);

        Reply reply = run(new UndoCommand(), tasks, storage);

        assertEquals("Fickle, aren't ya? Last change undone. Your list holds 0 tasks.", reply.text());
        assertTrue(tasks.isEmpty());
        assertEquals("", Files.readString(storage.getFilePath()));
    }

    @Test
    public void execute_removedOnlyTask_restoresTaskAndSingularCount(@TempDir Path tempDir)
            throws BibiException, IOException {
        TaskList tasks = new TaskList(new Todo("original"));
        Storage storage = new Storage(tempDir.resolve("bibi.txt"));
        run(new DeleteCommand(1), tasks, storage);

        Reply reply = run(new UndoCommand(), tasks, storage);

        assertEquals("Fickle, aren't ya? Last change undone. Your list holds 1 task.\n"
                + "1. [T][ ] original", reply.text());
        assertEquals(List.of("T | 0 | original"), Files.readAllLines(storage.getFilePath()));
    }

    @Test
    public void execute_noPreviousChange_rejectsWithoutSaving(@TempDir Path tempDir)
            throws BibiException {
        TaskList tasks = new TaskList(new Todo("original"));
        Storage storage = new Storage(tempDir.resolve("bibi.txt"));

        BibiException thrown = assertThrows(BibiException.class, () -> run(new UndoCommand(), tasks, storage));

        assertEquals("There is no change to undo in this session. "
                + "Make a list change first, then use undo, for example: undo.", thrown.getMessage());
        assertEquals("[T][ ] original", tasks.get(1).toString());
        assertFalse(Files.exists(storage.getFilePath()));
    }

    @Test
    public void execute_repeatedUndo_rejectsWithoutRedoingOrSaving(@TempDir Path tempDir)
            throws BibiException, IOException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("bibi.txt"));
        run(new AddCommand(new Todo("first")), tasks, storage);
        run(new AddCommand(new Todo("second")), tasks, storage);
        run(new UndoCommand(), tasks, storage);

        assertThrows(BibiException.class, () -> run(new UndoCommand(), tasks, storage));

        assertEquals(List.of("T | 0 | first"), Files.readAllLines(storage.getFilePath()));
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] first", tasks.get(1).toString());
    }

    @Test
    public void execute_failedNoOpAndViewingCommands_preservePreviousChange(@TempDir Path tempDir)
            throws BibiException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("bibi.txt"));
        run(new AddCommand(new Todo("added")), tasks, storage);

        assertThrows(BibiException.class, () -> run(new AddCommand(new Todo("ADDED")), tasks, storage));
        assertThrows(BibiException.class, () -> run(new DeleteCommand(2), tasks, storage));
        assertThrows(BibiException.class, () -> run(new MarkCommand(0), tasks, storage));
        List<Command> otherCommands = List.of(new UnmarkCommand(1), new SortCommand(), new ListCommand(),
                new FindCommand("added"), new OnCommand(LocalDate.of(2019, 12, 2)), new HelpCommand(false),
                new SocialCommand(false), new SocialCommand(true));
        for (Command command : otherCommands) {
            run(command, tasks, storage);
        }

        run(new UndoCommand(), tasks, storage);

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void execute_previousSaveFailed_stillRestoresAppliedChange(@TempDir Path tempDir)
            throws BibiException, IOException {
        Path blockedPath = Files.createDirectory(tempDir.resolve("blocked.txt"));
        TaskList tasks = new TaskList();
        Reply failedSave = run(new AddCommand(new Todo("added")), tasks, new Storage(blockedPath));
        Storage workingStorage = new Storage(tempDir.resolve("bibi.txt"));

        Reply reply = run(new UndoCommand(), tasks, workingStorage);

        assertTrue(failedSave.isError());
        assertFalse(reply.isError());
        assertTrue(tasks.isEmpty());
        assertEquals("", Files.readString(workingStorage.getFilePath()));
    }

    @Test
    public void execute_undoSaveFails_keepsRestoredStateAndReportsError(@TempDir Path tempDir)
            throws BibiException, IOException {
        TaskList tasks = new TaskList();
        run(new AddCommand(new Todo("added")), tasks, new Storage(tempDir.resolve("bibi.txt")));
        Path blockedPath = Files.createDirectory(tempDir.resolve("blocked.txt"));

        Reply reply = run(new UndoCommand(), tasks, new Storage(blockedPath));

        assertTrue(reply.isError());
        assertTrue(reply.text().contains("Fickle, aren't ya? Last change undone. Your list holds 0 tasks."));
        assertTrue(reply.text().contains("is a folder"));
        assertTrue(tasks.isEmpty());
        assertThrows(BibiException.class, tasks::undo);
    }

    @Test
    public void execute_loadedSession_doesNotRetainUndoHistory(@TempDir Path tempDir)
            throws BibiException, IOException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("bibi.txt"));
        run(new AddCommand(new Todo("saved")), tasks, storage);
        TaskList loadedTasks = new TaskList(storage.load().tasks());

        assertThrows(BibiException.class, () -> run(new UndoCommand(), loadedTasks, storage));

        assertEquals(List.of("T | 0 | saved"), Files.readAllLines(storage.getFilePath()));
        assertEquals("[T][ ] saved", loadedTasks.get(1).toString());
    }
}
