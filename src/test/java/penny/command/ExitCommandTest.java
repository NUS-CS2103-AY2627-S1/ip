package penny.command;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import penny.storage.Storage;
import penny.task.TaskList;
import penny.ui.Ui;

/**
 * Unit test suite for {@link ExitCommand}.
 * Verifies application exit signaling and goodbye message retrieval.
 */
public class ExitCommandTest {

    @Test
    public void isExit_invoked_returnsTrue() {
        ExitCommand command = new ExitCommand();
        assertTrue(command.isExit());
    }

    @Test
    public void execute_invoked_returnsGoodbyeMessage() {
        ExitCommand command = new ExitCommand();
        TaskList taskList = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage("dummy.txt");

        String result = command.execute(taskList, ui, storage);
        assertNotNull(result);
        assertTrue(!result.isEmpty());
    }
}
