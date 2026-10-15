package rei.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/** Tests conversion between user-entered keywords and {@link Command} values. */
public class CommandTest {
    @Test
    public void fromText_everySupportedKeyword_returnsMatchingCommand() {
        for (Command command : Command.values()) {
            assertEquals(command, Command.fromText(command.getKeyword()));
        }
    }

    @Test
    public void fromText_unknownOrIncorrectCase_returnsNull() {
        assertNull(Command.fromText("unknown"));
        assertNull(Command.fromText("TODO"));
        assertNull(Command.fromText(""));
        assertNull(Command.fromText(null));
    }
}
