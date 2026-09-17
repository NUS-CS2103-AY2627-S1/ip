package bro.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Verifies that Command stores normalized action and argument data. */
class CommandTest {
    /** A command trims its arguments while preserving the action. */
    @Test
    void constructor_whitespaceArguments_storesTrimmedText() {
        Command command = new Command("todo", "  read book  ");

        assertEquals("todo", command.getAction());
        assertEquals("read book", command.getArguments());
        assertTrue(command.hasArguments());
    }

    /** A command with blank arguments reports that it has no arguments. */
    @Test
    void hasArguments_blankArguments_returnsFalse() {
        Command command = new Command("list", "   ");

        assertEquals("", command.getArguments());
        assertFalse(command.hasArguments());
    }

    /** Null command components are rejected instead of creating incomplete commands. */
    @Test
    void constructor_nullComponent_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Command(null, "arguments"));
        assertThrows(NullPointerException.class, () -> new Command("todo", null));
    }
}
