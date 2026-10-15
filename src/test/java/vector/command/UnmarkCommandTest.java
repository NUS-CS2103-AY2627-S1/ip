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

public class UnmarkCommandTest {
    private static final String TEMP_FILE = "temp_unmark_test.txt";
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
            Todo task = new Todo("task 1");
            task.markAsDone();
            tasks.add(task);
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
    public void execute_validIndex_unmarksTaskAndReturnsString() {
        try {
            UnmarkCommand command = new UnmarkCommand(0);
            String result = command.execute(tasks, ui, storage);

            assertEquals(" ", tasks.get(0).getStatusIcon());
            assertTrue(result.contains("Status updated to: PENDING"));
            assertTrue(result.contains("[ ]"));
        } catch (VectorException e) {
            fail("Exception thrown on valid unmark command: " + e.getMessage());
        }
    }

    @Test
    public void execute_invalidIndex_throwsException() {
        try {
            UnmarkCommand command = new UnmarkCommand(1);
            command.execute(tasks, ui, storage);
            fail("Should have thrown exception on invalid index");
        } catch (VectorException e) {
            assertTrue(e.getMessage().contains("That task number does not exist in your list."));
        }
    }
}
