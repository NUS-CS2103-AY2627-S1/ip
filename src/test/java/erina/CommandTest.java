package erina;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Command}, the list of instructions Erina understands.
 */
public class CommandTest {
    @Test
    public void fromKeyword_everyCommandsOwnKeyword_returnsThatCommand() throws ErinaException {
        for (Command command : Command.values()) {
            assertEquals(command, Command.fromKeyword(command.getKeyword()));
        }
    }

    @Test
    public void fromKeyword_unknownWord_throwsPointingToHelp() {
        ErinaException e = assertThrows(ErinaException.class, () -> Command.fromKeyword("blah"));
        assertTrue(e.getMessage().contains("help"));
    }

    @Test
    public void describeAll_everyCommand_appearsInDeclaredOrder() {
        String help = Command.describeAll();

        int previous = -1;
        for (Command command : Command.values()) {
            int position = help.indexOf(command.getUsage());
            assertTrue(position > previous, "out of order or missing: " + command.getUsage());
            previous = position;
        }
    }
}
