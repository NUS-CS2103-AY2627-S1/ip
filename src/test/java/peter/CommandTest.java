package peter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Command}.
 */
public class CommandTest {
    @Test
    public void fromKeyword_recognisedLowercaseKeyword_returnsMatchingCommand() {
        assertEquals(Command.TODO, Command.fromKeyword("todo"));
        assertEquals(Command.DEADLINE, Command.fromKeyword("deadline"));
        assertEquals(Command.EVENT, Command.fromKeyword("event"));
        assertEquals(Command.SCHEDULE, Command.fromKeyword("schedule"));
        assertEquals(Command.FIND, Command.fromKeyword("find"));
        assertEquals(Command.LIST, Command.fromKeyword("list"));
        assertEquals(Command.MARK, Command.fromKeyword("mark"));
        assertEquals(Command.UNMARK, Command.fromKeyword("unmark"));
        assertEquals(Command.DELETE, Command.fromKeyword("delete"));
        assertEquals(Command.BYE, Command.fromKeyword("bye"));
    }

    @Test
    public void fromKeyword_mixedCaseKeyword_isCaseInsensitive() {
        assertEquals(Command.TODO, Command.fromKeyword("ToDo"));
        assertEquals(Command.BYE, Command.fromKeyword("BYE"));
    }

    @Test
    public void fromKeyword_unrecognisedKeyword_returnsUnknown() {
        assertEquals(Command.UNKNOWN, Command.fromKeyword("fly"));
    }

    @Test
    public void fromKeyword_emptyKeyword_returnsUnknown() {
        assertEquals(Command.UNKNOWN, Command.fromKeyword(""));
    }
}
