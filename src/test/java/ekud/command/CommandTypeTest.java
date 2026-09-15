package ekud.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link CommandType} keyword lookup and help text.
 */
public class CommandTypeTest {
    @Test
    public void fromKeyword_knownWord_returnsType() {
        assertEquals(CommandType.TODO, CommandType.fromKeyword("todo"));
        assertEquals(CommandType.DEADLINE, CommandType.fromKeyword("deadline"));
        assertEquals(CommandType.EVENT, CommandType.fromKeyword("event"));
        assertEquals(CommandType.LIST, CommandType.fromKeyword("list"));
        assertEquals(CommandType.ON, CommandType.fromKeyword("on"));
        assertEquals(CommandType.FIND, CommandType.fromKeyword("find"));
        assertEquals(CommandType.MARK, CommandType.fromKeyword("mark"));
        assertEquals(CommandType.UNMARK, CommandType.fromKeyword("unmark"));
        assertEquals(CommandType.DELETE, CommandType.fromKeyword("delete"));
        assertEquals(CommandType.HELP, CommandType.fromKeyword("help"));
        assertEquals(CommandType.BYE, CommandType.fromKeyword("bye"));
    }

    @Test
    public void getKeyword_matchesFromKeyword() {
        for (CommandType type : CommandType.values()) {
            assertEquals(type, CommandType.fromKeyword(type.getKeyword()));
        }
    }

    @Test
    public void fromKeyword_unknownWord_returnsNull() {
        assertNull(CommandType.fromKeyword("hello"));
        assertNull(CommandType.fromKeyword("TODO"));
    }

    @Test
    public void getHelpList_includesAllKeywords() {
        assertEquals(
                "todo, deadline, event, list, on, find, mark, unmark, delete, help, or bye",
                CommandType.getHelpList());
    }
}
