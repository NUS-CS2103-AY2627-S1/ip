package snoopy.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests metadata associated with supported command types.
 */
public class CommandTypeTest {
    @Test
    public void getCommandSummary_allSupportedCommands_excludesUnknownType() {
        assertEquals("bye, list, mark, unmark, delete, find, update, todo, deadline, event",
                CommandType.getCommandSummary());
    }

    @Test
    public void getKeyword_everyCommand_returnsConfiguredKeyword() {
        assertEquals("bye", CommandType.BYE.getKeyword());
        assertEquals("list", CommandType.LIST.getKeyword());
        assertEquals("mark", CommandType.MARK.getKeyword());
        assertEquals("unmark", CommandType.UNMARK.getKeyword());
        assertEquals("delete", CommandType.DELETE.getKeyword());
        assertEquals("find", CommandType.FIND.getKeyword());
        assertEquals("todo", CommandType.TODO.getKeyword());
        assertEquals("deadline", CommandType.DEADLINE.getKeyword());
        assertEquals("event", CommandType.EVENT.getKeyword());
        assertEquals("", CommandType.UNKNOWN.getKeyword());
    }

}
