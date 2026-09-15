package chatty.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests command keywords and argument policies.
 */
public class CommandTypeTest {
    /** Verifies every command exposes its configured keyword. */
    @Test
    public void getKeyword_allCommands_expectedKeywordsReturned() {
        assertEquals("bye", CommandType.BYE.getKeyword());
        assertEquals("list", CommandType.LIST.getKeyword());
        assertEquals("find", CommandType.FIND.getKeyword());
        assertEquals("mark", CommandType.MARK.getKeyword());
        assertEquals("unmark", CommandType.UNMARK.getKeyword());
        assertEquals("delete", CommandType.DELETE.getKeyword());
        assertEquals("todo", CommandType.TODO.getKeyword());
        assertEquals("deadline", CommandType.DEADLINE.getKeyword());
        assertEquals("event", CommandType.EVENT.getKeyword());
        assertEquals("recurring", CommandType.RECURRING.getKeyword());
        assertEquals("", CommandType.UNKNOWN.getKeyword());
    }

    /** Verifies only commands designed for trailing text accept arguments. */
    @Test
    public void acceptsArguments_allCommands_expectedPoliciesReturned() {
        assertFalse(CommandType.BYE.acceptsArguments());
        assertFalse(CommandType.LIST.acceptsArguments());
        assertFalse(CommandType.UNKNOWN.acceptsArguments());
        assertTrue(CommandType.FIND.acceptsArguments());
        assertTrue(CommandType.MARK.acceptsArguments());
        assertTrue(CommandType.UNMARK.acceptsArguments());
        assertTrue(CommandType.DELETE.acceptsArguments());
        assertTrue(CommandType.TODO.acceptsArguments());
        assertTrue(CommandType.DEADLINE.acceptsArguments());
        assertTrue(CommandType.EVENT.acceptsArguments());
        assertTrue(CommandType.RECURRING.acceptsArguments());
    }
}
