package xiaozhi.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import xiaozhi.exception.XiaoZhiException;
import xiaozhi.storage.Storage;
import xiaozhi.task.Deadline;
import xiaozhi.task.TaskList;
import xiaozhi.task.Todo;
import xiaozhi.testutil.OutputCapture;
import xiaozhi.ui.Ui;

/**
 * Tests {@link AddCommand}, using a real {@link Storage} pointed at a
 * temporary file (as {@code StorageTest} does) so its save side effect can
 * be checked too, and {@link OutputCapture} to check what it reports
 * through {@link Ui} without needing a mocking library.
 */
public class AddCommandTest {

    @TempDir
    Path tempDir;

    @Test
    public void execute_addsTaskAndReportsAndSaves() throws Exception {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage(tempDir.resolve("xiaozhi.txt").toString());
        AddCommand command = new AddCommand(new Todo("read book"));

        String output = OutputCapture.capture(() ->
                command.execute(tasks, ui, storage, new CommandHistory()));

        assertEquals(1, tasks.size());
        assertEquals("read book", tasks.get(0).getDescription());
        assertEquals(
                "It is done." + System.lineSeparator()
                        + "  [T][ ] read book" + System.lineSeparator()
                        + "Now 1 task(s) rest among your intentions." + System.lineSeparator(),
                output);
        assertEquals(1, storage.load().size());
    }

    @Test
    public void isUndoable_returnsTrue() {
        AddCommand command = new AddCommand(new Todo("read book"));

        assertTrue(command.isUndoable());
    }

    @Test
    public void undo_afterExecute_removesTheAddedTaskAndReportsAndSaves() throws Exception {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage(tempDir.resolve("xiaozhi.txt").toString());
        AddCommand command = new AddCommand(new Todo("read book"));
        command.execute(tasks, ui, storage, new CommandHistory());

        String output = OutputCapture.capture(() -> command.undo(tasks, ui, storage));

        assertEquals(0, tasks.size());
        assertEquals(
                "What no longer serves you has been released." + System.lineSeparator()
                        + "  [T][ ] read book" + System.lineSeparator()
                        + "Now 0 task(s) remain." + System.lineSeparator(),
                output);
        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void execute_taskAlreadyInList_throwsAndLeavesListAndStorageUnchanged() throws Exception {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage(tempDir.resolve("xiaozhi.txt").toString());
        new AddCommand(new Todo("read book")).execute(tasks, ui, storage, new CommandHistory());
        AddCommand duplicate = new AddCommand(new Todo("read book"));

        XiaoZhiException thrown = assertThrows(XiaoZhiException.class, () ->
                duplicate.execute(tasks, ui, storage, new CommandHistory()));

        assertEquals("This task already lives among your intentions: [T][ ] read book", thrown.getMessage());
        assertEquals(1, tasks.size());
        assertEquals(1, storage.load().size());
    }

    @Test
    public void execute_sameDescriptionButDifferentTaskType_isNotADuplicate() throws Exception {
        // A todo and a deadline that happen to share a description are
        // different tasks (one has no date, the other does), not duplicates.
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage(tempDir.resolve("xiaozhi.txt").toString());
        new AddCommand(new Todo("return book")).execute(tasks, ui, storage, new CommandHistory());
        AddCommand deadlineCommand = new AddCommand(new Deadline("return book", LocalDate.of(2019, 12, 2)));

        deadlineCommand.execute(tasks, ui, storage, new CommandHistory());

        assertEquals(2, tasks.size());
    }

    @Test
    public void execute_taskAlreadyInListButDone_isStillADuplicate() throws Exception {
        // Completion status should not let the same task back in through the
        // front door; only the description (and any dates) matter here.
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage(tempDir.resolve("xiaozhi.txt").toString());
        new AddCommand(new Todo("read book")).execute(tasks, ui, storage, new CommandHistory());
        tasks.get(0).markAsDone();
        AddCommand duplicate = new AddCommand(new Todo("read book"));

        assertThrows(XiaoZhiException.class, () -> duplicate.execute(tasks, ui, storage, new CommandHistory()));
    }
}
