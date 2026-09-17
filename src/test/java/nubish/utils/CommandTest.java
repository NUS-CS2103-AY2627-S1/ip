package nubish.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests command keyword conversion.
 */
public class CommandTest {
    /**
     * Verifies that a known command keyword maps to the matching command.
     */
    @Test
    public void fromKeyword_knownKeyword_returnsMatchingCommand() {
        assertEquals(Command.TODO, Command.fromKeyword("todo"));
    }

    /**
     * Verifies that command keyword matching is case-sensitive.
     */
    @Test
    public void fromKeyword_uppercaseKeyword_returnsUnknown() {
        assertEquals(Command.UNKNOWN, Command.fromKeyword("TODO"));
    }

    /**
     * Verifies that an unsupported keyword maps to unknown.
     */
    @Test
    public void fromKeyword_unknownKeyword_returnsUnknown() {
        assertEquals(Command.UNKNOWN, Command.fromKeyword("dance"));
    }
}
