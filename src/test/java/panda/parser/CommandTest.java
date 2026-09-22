package panda.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import panda.exception.InvalidCommandException;

/**
 * Tests command recognition and the argument rules associated with each keyword.
 */
class CommandTest {

    @Test
    void findCommand_supportedMessages_returnsMatchingCommands()
            throws InvalidCommandException {
        assertEquals(Command.BYE, Command.findCommand("bye"));
        assertEquals(Command.LIST, Command.findCommand("list"));
        assertEquals(Command.LIST, Command.findCommand("list 2026-09-14"));
        assertEquals(Command.TODO, Command.findCommand("todo read book"));
        assertEquals(Command.DEADLINE, Command.findCommand("deadline submit /by 2026-09-14 10:00"));
        assertEquals(Command.EVENT,
                Command.findCommand("event camp /from 2026-09-14 10:00 /to 2026-09-15 10:00"));
    }

    @Test
    void matches_argumentRules_acceptsOnlyValidKeywordBoundary() {
        assertTrue(Command.TODO.matches("todo"));
        assertTrue(Command.TODO.matches("todo read book"));
        assertFalse(Command.TODO.matches("todolist"));
        assertFalse(Command.TODO.matches("Todo read book"));
        assertTrue(Command.BYE.matches("bye"));
        assertFalse(Command.BYE.matches("bye now"));
        assertFalse(Command.BYE.matches("bye "));
    }

    @Test
    void findCommand_unsupportedMessages_throwsInvalidCommandException() {
        assertThrows(InvalidCommandException.class, () -> Command.findCommand(""));
        assertThrows(InvalidCommandException.class, () -> Command.findCommand("   "));
        assertThrows(InvalidCommandException.class, () -> Command.findCommand("bye now"));
        assertThrows(InvalidCommandException.class, () -> Command.findCommand("listall"));
    }

    @Test
    void accessors_allCommands_returnConfiguredKeywords() {
        assertEquals("bye", Command.BYE.getKeyword());
        assertEquals("list", Command.LIST.getKeyword());
        assertEquals("find", Command.FIND.getKeyword());
        assertEquals("mark", Command.MARK.getKeyword());
        assertEquals("unmark", Command.UNMARK.getKeyword());
        assertEquals("update", Command.UPDATE.getKeyword());
        assertEquals("delete", Command.DELETE.getKeyword());
        assertEquals("event", Command.EVENT.getKeyword());
        assertEquals("deadline", Command.DEADLINE.getKeyword());
        assertEquals("todo", Command.TODO.getKeyword());
    }
}
