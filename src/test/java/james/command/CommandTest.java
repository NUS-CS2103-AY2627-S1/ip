package james.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Command}.
 */
public class CommandTest {

    /**
     * Tests that valueOf maps valid uppercase command names to their corresponding Command enum constants.
     */
    @Test
    public void valueOf_validCommandNames_returnsCorrectEnumConstant() {
        assertEquals(Command.DELETE, Command.valueOf("DELETE"));
        assertEquals(Command.TODO, Command.valueOf("TODO"));
        assertEquals(Command.EVENT, Command.valueOf("EVENT"));
        assertEquals(Command.DEADLINE, Command.valueOf("DEADLINE"));
        assertEquals(Command.MARK, Command.valueOf("MARK"));
        assertEquals(Command.UNMARK, Command.valueOf("UNMARK"));
        assertEquals(Command.LIST, Command.valueOf("LIST"));
        assertEquals(Command.BYE, Command.valueOf("BYE"));
        assertEquals(Command.LIST_BY_DATE, Command.valueOf("LIST_BY_DATE"));
        assertEquals(Command.FIND, Command.valueOf("FIND"));
        assertEquals(Command.UNDO, Command.valueOf("UNDO"));
        assertEquals(Command.RANDOM_STICKER, Command.valueOf("RANDOM_STICKER"));
    }

    /**
     * Tests that valueOf throws IllegalArgumentException when an invalid command name is given.
     */
    @Test
    public void valueOf_invalidCommandName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Command.valueOf("INVALID_COMMAND"));
    }

    /**
     * Tests that values returns an array containing all defined Command enum constants.
     */
    @Test
    public void values_allDefinedCommands_containsTwelveCommands() {
        Command[] commands = Command.values();
        assertNotNull(commands);
        assertEquals(12, commands.length);
    }
}
