package martin.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for command parsing.
 */
public class CommandTest {

    @Test
    public void from_findCommand_returnsFindCommand() {
        assertEquals(Command.FIND, Command.from("find book"));
    }
}
