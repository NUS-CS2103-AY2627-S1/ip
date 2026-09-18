package sozius.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class CommandTest {

    @Test
    public void getCommand_fullNames_returnsMatchingCommand() {
        assertEquals(Command.LIST, Command.getCommand("list"));
        assertEquals(Command.MARK, Command.getCommand("mark"));
        assertEquals(Command.UNMARK, Command.getCommand("unmark"));
        assertEquals(Command.TODO, Command.getCommand("todo"));
        assertEquals(Command.DEADLINE, Command.getCommand("deadline"));
        assertEquals(Command.EVENT, Command.getCommand("event"));
        assertEquals(Command.DELETE, Command.getCommand("delete"));
        assertEquals(Command.FIND, Command.getCommand("find"));
        assertEquals(Command.HELP, Command.getCommand("help"));
    }

    @Test
    public void getCommand_aliases_returnsMatchingCommand() {
        assertEquals(Command.LIST, Command.getCommand("ls"));
        assertEquals(Command.MARK, Command.getCommand("m"));
        assertEquals(Command.UNMARK, Command.getCommand("um"));
        assertEquals(Command.TODO, Command.getCommand("td"));
        assertEquals(Command.DEADLINE, Command.getCommand("dl"));
        assertEquals(Command.EVENT, Command.getCommand("e"));
        assertEquals(Command.DELETE, Command.getCommand("del"));
        assertEquals(Command.FIND, Command.getCommand("f"));
        assertEquals(Command.HELP, Command.getCommand("h"));
    }

    @Test
    public void getCommand_unknownWord_returnsNull() {
        assertNull(Command.getCommand("bye"));
        assertNull(Command.getCommand("hello"));
        assertNull(Command.getCommand(""));
    }

    @Test
    public void getCommand_wrongCase_returnsNull() {
        // commands are case-sensitive
        assertNull(Command.getCommand("LIST"));
        assertNull(Command.getCommand("Todo"));
    }

    @Test
    public void getCommand_wordWithTrailingSpace_returnsNull() {
        // parsing relies on the caller splitting off arguments first
        assertNull(Command.getCommand("list "));
    }
}
