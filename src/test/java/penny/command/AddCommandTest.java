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
 * Unit test suite for {@link AddCommand}.
 * Verifies adding tasks, updating persistent storage, and error handling.
 */
public class AddCommandTest {

    @TempDir
    private Path tempDir;

    private TaskList taskList;
    private Ui ui;
    private Storage storage;

    @BeforeEach
    public void setUp() {
        taskList = new TaskList();
        ui = new Ui();
        storage = new Storage(tempDir.resolve("tasks.txt"));
    }

    @Test
    public void execute_validTask_addsTaskAndSavesToStorage() throws PennyException {
        Todo todo = new Todo("read book");
        AddCommand command = new AddCommand(todo);

        String result = command.execute(taskList, ui, storage);
        assertEquals(1, taskList.size());
        assertEquals(todo, taskList.get(0));
        assertNotNull(result);
        assertTrue(result.contains("read book"));
        assertEquals(1, storage.load().size());
    }

    @Test
    public void constructor_nullTask_assertionError() {
        assertThrows(AssertionError.class, () -> {
            new AddCommand(null);
        });
    }
}
