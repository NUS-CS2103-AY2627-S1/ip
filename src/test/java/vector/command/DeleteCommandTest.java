package vector.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import vector.VectorException;
import vector.storage.Storage;
import vector.task.TaskList;
import vector.task.Todo;
import vector.ui.Ui;

public class DeleteCommandTest {
    private static final String TEMP_FILE = "temp_delete_test.txt";
    private Storage storage;
    private TaskList tasks;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        File file = new File(TEMP_FILE);
        if (file.exists()) {
            file.delete();
        }
        storage = new Storage(TEMP_FILE);
        tasks = new TaskList();
        try {
            tasks.add(new Todo("task 1"));
            tasks.add(new Todo("task 2"));
        } catch (VectorException e) {
            fail("Failed to setup tasks");
        }
        ui = new Ui();
    }

    @AfterEach
    public void tearDown() {
        File file = new File(TEMP_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void execute_validIndex_deletesTaskAndReturnsString() {
        try {
            DeleteCommand command = new DeleteCommand(0);
            String result = command.execute(tasks, ui, storage);

            assertEquals(1, tasks.size());
            assertEquals("task 2", tasks.get(0).getDescription());
            assertTrue(result.contains("Task erased from memory banks"));
            assertTrue(result.contains("task 1"));
        } catch (VectorException e) {
            fail("Exception thrown on valid delete command: " + e.getMessage());
        }
    }

    @Test
    public void execute_invalidIndex_throwsException() {
        try {
            DeleteCommand command = new DeleteCommand(2);
            command.execute(tasks, ui, storage);
            fail("Should have thrown exception on invalid index");
        } catch (VectorException e) {
            assertTrue(e.getMessage().contains("That task number does not exist in your list."));
        }
    }
}
