package ted.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ted.TedException;

/**
 * Tests {@link CommandType}, which matches the first word the user types to a command.
 */
public class CommandTypeTest {
    /**
     * Verifies that every command is found by its own keyword.
     */
    @Test
    public void fromKeyword_everyKeyword_returnsItsCommand() throws TedException {
        for (CommandType commandType : CommandType.values()) {
            assertEquals(commandType, CommandType.fromKeyword(commandType.getKeyword()));
        }
    }

    /**
     * Verifies that a keyword is recognized in any mix of upper and lower case.
     */
    @Test
    public void fromKeyword_mixedCase_recognized() throws TedException {
        assertEquals(CommandType.DEADLINE, CommandType.fromKeyword("DeadLine"));
    }

    /**
     * Verifies that an unknown word is reported along with every keyword Ted understands.
     */
    @Test
    public void fromKeyword_unknownWord_listsEveryKeyword() {
        TedException e = assertThrows(TedException.class, () -> CommandType.fromKeyword("blah"));
        assertTrue(e.getMessage().contains("\"blah\""));
        assertTrue(e.getMessage().contains("todo, deadline, event, list, mark, unmark, delete, find, tag, untag, bye"));
    }
}
