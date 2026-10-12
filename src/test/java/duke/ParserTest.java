package duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ParserTest {

    @Test
    public void parseCommand_lowercaseKeyword_returnsCommand() throws DuckeException {
        assertEquals(Command.LIST, Parser.parseCommand("list"));
    }

    @Test
    public void parseCommand_keywordWithArgument_ignoresArgument() throws DuckeException {
        assertEquals(Command.MARK, Parser.parseCommand("mark 2"));
        assertEquals(Command.DEADLINE, Parser.parseCommand("deadline return book /by 2019-10-15"));
    }

    @Test
    public void parseCommand_mixedCaseKeyword_returnsCommand() throws DuckeException {
        assertEquals(Command.TODO, Parser.parseCommand("ToDo read book"));
        assertEquals(Command.BYE, Parser.parseCommand("BYE"));
    }

    @Test
    public void parseCommand_shortAlias_returnsCommand() throws DuckeException {
        assertEquals(Command.TODO, Parser.parseCommand("t read book"));
        assertEquals(Command.DEADLINE, Parser.parseCommand("d return book /by 2019-10-15"));
        assertEquals(Command.DELETE, Parser.parseCommand("del 2"));
    }

    @Test
    public void parseCommand_aliasIsCaseInsensitive_returnsCommand() throws DuckeException {
        assertEquals(Command.LIST, Parser.parseCommand("LS"));
    }

    @Test
    public void parseCommand_unknownKeyword_exceptionThrown() {
        assertThrows(DuckeException.class, () -> Parser.parseCommand("blah"));
    }

    @Test
    public void parseCommand_emptyInput_exceptionThrown() {
        assertThrows(DuckeException.class, () -> Parser.parseCommand(""));
    }

    @Test
    public void parseArgument_singleWordArgument_returnsArgument() {
        assertEquals("2", Parser.parseArgument("mark 2"));
    }

    @Test
    public void parseArgument_multiWordArgument_preservesSpacing() {
        assertEquals("read book", Parser.parseArgument("todo read book"));
        assertEquals("return book /by 2019-10-15",
                Parser.parseArgument("deadline return book /by 2019-10-15"));
    }

    @Test
    public void parseArgument_keywordOnly_returnsEmptyString() {
        assertEquals("", Parser.parseArgument("list"));
    }

    @Test
    public void parseArgument_emptyInput_returnsEmptyString() {
        assertEquals("", Parser.parseArgument(""));
    }
}
