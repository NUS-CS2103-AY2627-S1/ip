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

public class ListCommandTest {
    private static final String TEMP_FILE = "temp_list_test.txt";
    private Storage storage;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        File file = new File(TEMP_FILE);
        if (file.exists()) {
            file.delete();
        }
        storage = new Storage(TEMP_FILE);
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
    public void execute_emptyList_returnsEmptyMessage() {
        TaskList tasks = new TaskList();
        ListCommand command = new ListCommand();
        String result = command.execute(tasks, ui, storage);
        assertEquals("Your task list is empty.", result);
    }

    @Test
    public void execute_populatedList_returnsListString() {
        TaskList tasks = new TaskList();
        try {
            tasks.add(new Todo("task 1"));
            tasks.add(new Todo("task 2"));
            ListCommand command = new ListCommand();
            String result = command.execute(tasks, ui, storage);
            assertTrue(result.contains("Accessing database... Current task matrix:"));
            assertTrue(result.contains("1.[T][ ] task 1"));
            assertTrue(result.contains("2.[T][ ] task 2"));
        } catch (VectorException e) {
            fail("Exception thrown on valid list command: " + e.getMessage());
        }
    }
}
