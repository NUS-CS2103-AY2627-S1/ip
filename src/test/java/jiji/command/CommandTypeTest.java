package jiji.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import jiji.exception.JijiUnknownCommandException;

/**
 * Unit tests for {@link CommandType}.
 */
public class CommandTypeTest {

    @Test
    public void getCommandWord_returnsExpectedValues() {
        assertEquals("bye", CommandType.BYE.getCommandWord());
        assertEquals("list", CommandType.LIST.getCommandWord());
        assertEquals("mark", CommandType.MARK.getCommandWord());
        assertEquals("unmark", CommandType.UNMARK.getCommandWord());
        assertEquals("delete", CommandType.DELETE.getCommandWord());
        assertEquals("todo", CommandType.TODO.getCommandWord());
        assertEquals("deadline", CommandType.DEADLINE.getCommandWord());
        assertEquals("event", CommandType.EVENT.getCommandWord());
        assertEquals("find", CommandType.FIND.getCommandWord());
        assertEquals("help", CommandType.HELP.getCommandWord());
        assertEquals("stats", CommandType.STATS.getCommandWord());
        assertEquals("schedule", CommandType.SCHEDULE.getCommandWord());
    }

    @Test
    public void from_validWords_returnsCorrectType() throws JijiUnknownCommandException {
        assertEquals(CommandType.TODO, CommandType.from("todo"));
        assertEquals(CommandType.DEADLINE, CommandType.from("DEADLINE"));
        assertEquals(CommandType.EVENT, CommandType.from("Event"));
        assertEquals(CommandType.STATS, CommandType.from("stats"));
        assertEquals(CommandType.STATS, CommandType.from("statistics"));
        assertEquals(CommandType.STATS, CommandType.from("STATISTICS"));
        assertEquals(CommandType.SCHEDULE, CommandType.from("  schedule  "));
    }

    @Test
    public void from_unknownOrNull_throwsJijiUnknownCommandException() {
        assertThrows(JijiUnknownCommandException.class, () -> CommandType.from(null));
        assertThrows(JijiUnknownCommandException.class, () -> CommandType.from("unknown"));
        assertThrows(JijiUnknownCommandException.class, () -> CommandType.from("foo"));
    }
}
