package alice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CommandTest {

    @Test
    void parse_returnsCorrectCommand() {
        assertEquals(Command.BYE, Command.parse("bye"));
        assertEquals(Command.LIST, Command.parse("list"));
        assertEquals(Command.MARK, Command.parse("mark"));
        assertEquals(Command.UNMARK, Command.parse("unmark"));
        assertEquals(Command.DELETE, Command.parse("delete"));
        assertEquals(Command.TODO, Command.parse("todo"));
        assertEquals(Command.DEADLINE, Command.parse("deadline"));
        assertEquals(Command.EVENT, Command.parse("event"));
        assertEquals(Command.VIEW, Command.parse("view"));
        assertEquals(Command.UNDO, Command.parse("undo"));
        assertEquals(Command.UNKNOWN, Command.parse("unknown"));
        assertEquals(Command.UNKNOWN, Command.parse(""));
        assertEquals(Command.UNKNOWN, Command.parse(null));
    }

    @Test
    void parse_ignoresExtraSpacesAndArguments() {
        assertEquals(Command.BYE, Command.parse("  bye  "));
        assertEquals(Command.MARK, Command.parse("mark 2"));
        assertEquals(Command.DEADLINE, Command.parse("deadline return /by 2024-12-25"));
    }

    @Test
    void isAddCommand_returnsCorrectly() {
        assertTrue(Command.TODO.isAddCommand());
        assertTrue(Command.DEADLINE.isAddCommand());
        assertTrue(Command.EVENT.isAddCommand());
        assertFalse(Command.BYE.isAddCommand());
        assertFalse(Command.LIST.isAddCommand());
        assertFalse(Command.MARK.isAddCommand());
        assertFalse(Command.UNMARK.isAddCommand());
        assertFalse(Command.DELETE.isAddCommand());
        assertFalse(Command.VIEW.isAddCommand());
        assertFalse(Command.UNDO.isAddCommand());
        assertFalse(Command.UNKNOWN.isAddCommand());
    }

    @Test
    void requiresIndex_returnsCorrectly() {
        assertTrue(Command.MARK.requiresIndex());
        assertTrue(Command.UNMARK.requiresIndex());
        assertTrue(Command.DELETE.requiresIndex());
        assertFalse(Command.BYE.requiresIndex());
        assertFalse(Command.LIST.requiresIndex());
        assertFalse(Command.TODO.requiresIndex());
        assertFalse(Command.DEADLINE.requiresIndex());
        assertFalse(Command.EVENT.requiresIndex());
        assertFalse(Command.VIEW.requiresIndex());
        assertFalse(Command.UNDO.requiresIndex());
        assertFalse(Command.UNKNOWN.requiresIndex());
    }
}
