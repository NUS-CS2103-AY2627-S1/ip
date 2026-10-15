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

public class FindCommandTest {
    private static final String TEMP_FILE = "temp_find_test.txt";
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
    public void execute_matchingTasks_returnsMatchingListString() {
        TaskList tasks = new TaskList();
        try {
            tasks.add(new Todo("read book"));
            tasks.add(new Todo("buy groceries"));
            tasks.add(new Todo("return book"));
            FindCommand command = new FindCommand("book");
            String result = command.execute(tasks, ui, storage);
            assertTrue(result.contains("Search protocol complete. Query results:"));
            assertTrue(result.contains("1.[T][ ] read book"));
            assertTrue(result.contains("2.[T][ ] return book"));
            assertFalse(result.contains("buy groceries"));
        } catch (VectorException e) {
            fail("Exception thrown on valid find command: " + e.getMessage());
        }
    }

    @Test
    public void execute_noMatchingTasks_returnsEmptySearchMessage() {
        TaskList tasks = new TaskList();
        try {
            tasks.add(new Todo("read book"));
            FindCommand command = new FindCommand("laptop");
            String result = command.execute(tasks, ui, storage);
            assertEquals("No matching tasks found.", result);
        } catch (VectorException e) {
            fail("Exception thrown on valid find command: " + e.getMessage());
        }
    }

    private void assertFalse(boolean condition) {
        org.junit.jupiter.api.Assertions.assertFalse(condition);
    }
}
