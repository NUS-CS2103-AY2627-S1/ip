package vector.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import vector.storage.Storage;
import vector.task.TaskList;
import vector.ui.Ui;

public class ExitCommandTest {
    private static final String TEMP_FILE = "temp_exit_test.txt";
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
    public void execute_exitCommand_returnsGoodbyeMessage() {
        ExitCommand command = new ExitCommand();
        String result = command.execute(tasks, ui, storage);
        assertEquals("System shutting down. End of line.", result);
    }

    @Test
    public void isExit_exitCommand_returnsTrue() {
        ExitCommand command = new ExitCommand();
        assertTrue(command.isExit());
    }
}
