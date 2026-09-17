package crystal.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import crystal.task.TaskList;

/**
 * Tests exit identification and farewell output.
 */
public class ExitCommandTest extends CommandTestBase {
    /** Verifies farewell output and the exit signal. */
    @Test
    public void execute_exitCommand_displaysFarewellAndSignalsExit() {
        ExitCommand command = new ExitCommand();

        command.execute(new TaskList(), createUi(), createStorage());

        assertTrue(command.isExit());
        assertEquals("Crystal: Byeee!!! You did amazing today. See you soon!\n",
                getOutput());
    }
}
