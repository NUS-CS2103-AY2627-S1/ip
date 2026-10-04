package penny.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import penny.common.PennyException;
import penny.storage.Storage;
import penny.task.TaskList;
import penny.task.Todo;
import penny.ui.Ui;

/**
 * Unit test suite for {@link MarkCommand}.
 * Verifies marking tasks done, updating storage, and exception handling for invalid indices.
 */
public class MarkCommandTest {

    @TempDir
    private Path tempDir;

    private TaskList taskList;
    private Ui ui;
    private Storage storage;
    private Todo sampleTodo;

    @BeforeEach
    public void setUp() {
        taskList = new TaskList();
        sampleTodo = new Todo("borrow book");
        taskList.add(sampleTodo);
        ui = new Ui();
        storage = new Storage(tempDir.resolve("tasks.txt"));
    }

    @Test
    public void execute_validIndex_marksTaskDoneAndSavesToStorage() throws PennyException {
        MarkCommand command = new MarkCommand(0);
        String result = command.execute(taskList, ui, storage);

        assertTrue(sampleTodo.isDone());
        assertNotNull(result);
        assertTrue(result.contains("borrow book"));
        assertEquals("1", storage.load().get(0).toFileFormat().split(" \\| ")[1]);
    }

    @Test
    public void execute_invalidIndex_throwsPennyException() {
        MarkCommand command = new MarkCommand(10);
        assertThrows(PennyException.class, () -> {
            command.execute(taskList, ui, storage);
        });
    }

    @Test
    public void constructor_negativeIndex_assertionError() {
        assertThrows(AssertionError.class, () -> {
            new MarkCommand(-1);
        });
    }
}
