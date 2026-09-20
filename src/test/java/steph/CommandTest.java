package steph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Command#fromKeyword(String)}, the lookup from the word
 * a user types to the command it names.
 */
public class CommandTest {

    @Test
    public void fromKeyword_everyKeyword_returnsMatchingConstant() {
        assertEquals(Command.LIST, Command.fromKeyword("list"));
        assertEquals(Command.MARK, Command.fromKeyword("mark"));
        assertEquals(Command.UNMARK, Command.fromKeyword("unmark"));
        assertEquals(Command.TODO, Command.fromKeyword("todo"));
        assertEquals(Command.DEADLINE, Command.fromKeyword("deadline"));
        assertEquals(Command.EVENT, Command.fromKeyword("event"));
        assertEquals(Command.DELETE, Command.fromKeyword("delete"));
        assertEquals(Command.FIND, Command.fromKeyword("find"));
    }

    @Test
    public void fromKeyword_unknownKeyword_returnsNull() {
        assertNull(Command.fromKeyword("sing"));
    }

    @Test
    public void fromKeyword_emptyKeyword_returnsNull() {
        assertNull(Command.fromKeyword(""));
    }

    @Test
    public void fromKeyword_enumNameInsteadOfKeyword_returnsNull() {
        assertNull(Command.fromKeyword("TODO"));
    }
}
