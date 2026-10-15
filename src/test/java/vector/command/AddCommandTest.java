package vector.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

public class AddCommandTest {
    private static final String TEMP_FILE = "temp_add_test.txt";
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
    public void execute_validTask_addsTaskAndReturnsString() {
        try {
            Todo todo = new Todo("test task");
            AddCommand command = new AddCommand(todo);
            String result = command.execute(tasks, ui, storage);

            assertEquals(1, tasks.size());
            assertEquals("test task", tasks.get(0).getDescription());
            assertTrue(result.contains("Data logged. New task added to the matrix"));
            assertTrue(result.contains("test task"));
        } catch (VectorException e) {
            fail("Exception thrown on valid add command execution: " + e.getMessage());
        }
    }

    @Test
    public void execute_duplicateTask_throwsException() {
        try {
            Todo todo1 = new Todo("test task");
            AddCommand command1 = new AddCommand(todo1);
            command1.execute(tasks, ui, storage);

            Todo todo2 = new Todo("test task");
            AddCommand command2 = new AddCommand(todo2);
            command2.execute(tasks, ui, storage);

            fail("Should have thrown exception on duplicate task");
        } catch (VectorException e) {
            assertEquals("An identical task already exists in the matrix.", e.getMessage());
        }
    }

    private void assertTrue(boolean condition) {
        org.junit.jupiter.api.Assertions.assertTrue(condition);
    }
}
