package bob.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import bob.storage.Storage;
import bob.task.TaskList;

/** Tests the exit behavior of {@link ByeCommand}. */
class ByeCommandTest {

    @Test
    void execute_returnsGoodbyeMessage() {
        String response = new ByeCommand().execute(new TaskList(), new Storage());

        assertEquals("See you next time. Keep reaching for the stars!", response);
    }

    @Test
    void isExit_returnsTrue() {
        assertTrue(new ByeCommand().isExit());
    }
}
