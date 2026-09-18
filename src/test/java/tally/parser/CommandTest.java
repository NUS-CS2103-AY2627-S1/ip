package tally.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests that each command knows whether carrying it out can change the tally. */
public class CommandTest {
    @Test
    public void canChangeTally_commandsThatOnlyRead_isFalse() {
        assertFalse(Command.LIST.canChangeTally());
        assertFalse(Command.FIND.canChangeTally());
        assertFalse(Command.FREE.canChangeTally());
        assertFalse(Command.BYE.canChangeTally());
    }

    @Test
    public void canChangeTally_commandsThatAddRemoveOrAlter_isTrue() {
        assertTrue(Command.TODO.canChangeTally());
        assertTrue(Command.DEADLINE.canChangeTally());
        assertTrue(Command.EVENT.canChangeTally());
        assertTrue(Command.WINDOW.canChangeTally());
        assertTrue(Command.MARK.canChangeTally());
        assertTrue(Command.UNMARK.canChangeTally());
        assertTrue(Command.DELETE.canChangeTally());
    }
}
