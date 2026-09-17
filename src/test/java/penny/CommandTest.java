package penny;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * Tests command recognition and exception details exposed to callers.
 */
public class CommandTest {
    @Test
    void parse_knownCommands_acceptsUpperAndLowerCase() throws PennyException {
        for (Command command : Command.values()) {
            assertEquals(command, Command.parse(command.name()));
            assertEquals(command, Command.parse(command.name().toLowerCase(Locale.ROOT)));
        }
        assertEquals(Command.TODO, Command.parse("ToDo"));
        assertEquals(Command.UNKNOWN, Command.parse("nonsense"));
    }

    @Test
    void parse_blankCommand_reportsError() {
        for (String input : new String[]{"", " ", "\t"}) {
            assertEquals("Sorry I didn't quite catch that.",
                    assertThrows(PennyException.class, () -> Command.parse(input)).getMessage());
        }
    }

    @Test
    void exception_withCause_preservesDetails() {
        Exception cause = new IllegalArgumentException("cause");
        PennyException error = new PennyException("message", cause);
        assertEquals("message", error.getMessage());
        assertSame(cause, error.getCause());
    }
}
