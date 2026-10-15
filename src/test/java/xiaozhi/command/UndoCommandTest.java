package xiaozhi.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import xiaozhi.exception.XiaoZhiException;
import xiaozhi.storage.Storage;
import xiaozhi.task.TaskList;
import xiaozhi.task.Todo;
import xiaozhi.ui.Ui;

/**
 * Tests {@link UndoCommand} itself: that it refuses to run with nothing to
 * undo, and that it delegates to whatever command {@link CommandHistory}
 * hands it. The specific reversal behavior for each undoable command
 * belongs to that command's own test (e.g. {@link AddCommandTest}); this
 * class only checks that {@code UndoCommand} pops history and calls
 * {@code undo} on the result.
 */
public class UndoCommandTest {

    @TempDir
    Path tempDir;

    @Test
    public void execute_emptyHistory_throws() {
        UndoCommand command = new UndoCommand();
        CommandHistory history = new CommandHistory();

        XiaoZhiException thrown = assertThrows(XiaoZhiException.class, () ->
                command.execute(new TaskList(), new Ui(),
                        new Storage(tempDir.resolve("xiaozhi.txt").toString()), history));

        assertEquals("There is nothing left to undo; the past cannot be unmade twice.", thrown.getMessage());
    }

    @Test
    public void execute_historyHasOneCommand_reversesItAndEmptiesHistory() throws Exception {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage(tempDir.resolve("xiaozhi.txt").toString());
        CommandHistory history = new CommandHistory();
        AddCommand addCommand = new AddCommand(new Todo("read book"));
        addCommand.execute(tasks, ui, storage, history);
        history.push(addCommand);
        UndoCommand undoCommand = new UndoCommand();

        undoCommand.execute(tasks, ui, storage, history);

        assertEquals(0, tasks.size());
        assertTrue(history.isEmpty());
    }
}
