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

public class MarkCommandTest {
    private static final String TEMP_FILE = "temp_mark_test.txt";
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
    public void execute_validIndex_marksTaskAndReturnsString() {
        try {
            MarkCommand command = new MarkCommand(0);
            String result = command.execute(tasks, ui, storage);

            assertEquals("X", tasks.get(0).getStatusIcon());
            assertTrue(result.contains("Status updated to: COMPLETE"));
            assertTrue(result.contains("[X]"));
        } catch (VectorException e) {
            fail("Exception thrown on valid mark command: " + e.getMessage());
        }
    }

    @Test
    public void execute_invalidIndex_throwsException() {
        try {
            MarkCommand command = new MarkCommand(1);
            command.execute(tasks, ui, storage);
            fail("Should have thrown exception on invalid index");
        } catch (VectorException e) {
            assertTrue(e.getMessage().contains("That task number does not exist in your list."));
        }
    }
}
