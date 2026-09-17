package crystal.command;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import crystal.task.Todo;

/**
 * Tests behavior shared by ordinary commands.
 */
public class CommandTest {
    /** Verifies that an ordinary command does not request application exit. */
    @Test
    public void isExit_ordinaryCommand_returnsFalse() {
        Command command = new AddCommand(new Todo("read book"));

        assertFalse(command.isExit());
    }
}
