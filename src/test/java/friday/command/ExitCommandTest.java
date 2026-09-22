package friday.command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests commands that exit the application. */
class ExitCommandTest {

    @Test
    void isExit_exitCommand_returnsTrue() {
        ExitCommand command = new ExitCommand();

        assertTrue(command.isExit());
    }
}
