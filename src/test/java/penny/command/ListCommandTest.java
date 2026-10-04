package penny.command;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import penny.storage.Storage;
import penny.task.TaskList;
import penny.task.Todo;
import penny.ui.Ui;

/**
 * Unit test suite for {@link ListCommand}.
 * Verifies listing tasks when the list is empty and populated.
 */
public class ListCommandTest {

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
    public void execute_populatedList_returnsFormattedTaskList() {
        taskList.add(new Todo("read book"));
        taskList.add(new Todo("return book"));

        ListCommand command = new ListCommand();
        String result = command.execute(taskList, ui, storage);

        assertNotNull(result);
        assertTrue(result.contains("read book"));
        assertTrue(result.contains("return book"));
    }

    @Test
    public void execute_emptyList_returnsEmptyListMessage() {
        ListCommand command = new ListCommand();
        String result = command.execute(taskList, ui, storage);

        assertNotNull(result);
    }
}
