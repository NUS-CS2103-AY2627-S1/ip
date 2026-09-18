package student.project.bob.parser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests the value-preserving behavior of parsed commands.
 */
public class CommandTest {
    /**
     * Verifies that a command preserves its type and original input.
     */
    @Test
    public void constructor_command_preservesTypeAndInput() {
        Command command = new Command(Command.Type.MARK, "mark 2");

        assertAll(
                () -> assertEquals(Command.Type.MARK, command.getType()),
                () -> assertEquals("mark 2", command.getInput()));
    }

    /**
     * Verifies that every supported command type can be stored and retrieved unchanged.
     */
    @Test
    public void constructor_eachCommandType_preservesType() {
        for (Command.Type type : Command.Type.values()) {
            Command command = new Command(type, type.name().toLowerCase());

            assertEquals(type, command.getType());
            assertEquals(type.name().toLowerCase(), command.getInput());
        }
    }
}
