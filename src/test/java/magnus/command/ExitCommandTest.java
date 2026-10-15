package magnus.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import magnus.exception.CommandSyntaxException;

/**
 * Tests output and argument validation for {@link ExitCommand}.
 */
public class ExitCommandTest {
    @Test
    public void execute_withoutArguments_returnsFarewell() throws CommandSyntaxException {
        String response = new ExitCommand().execute();

        assertEquals("\tThe board is set aside for now. Goodbye, and see you next game!", response);
    }

    @Test
    public void execute_withArguments_throwsCommandSyntaxException() {
        CommandSyntaxException exception = assertThrows(
                CommandSyntaxException.class, () -> new ExitCommand().execute("now"));

        assertEquals("\tThat move has extra pieces - the bye command does not accept arguments.\n"
                + "\tUsage: bye", exception.getMessage());
    }
}
