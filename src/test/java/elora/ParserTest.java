package elora;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ParserTest {

    @Test
    void getCommandWord_inputWithArguments_returnsFirstWord() {
        assertEquals("todo", Parser.getCommandWord("todo read book"));
    }

    @Test
    void getCommandWord_inputWithoutArguments_returnsWholeInput() {
        assertEquals("list", Parser.getCommandWord("list"));
    }

    @Test
    void getArguments_inputWithArguments_returnsRemainder() {
        assertEquals("read book", Parser.getArguments("todo read book"));
    }

    @Test
    void getArguments_inputWithoutArguments_returnsEmptyString() {
        assertEquals("", Parser.getArguments("list"));
    }

    @Test
    void getArguments_extraWhitespaceAroundArguments_trimsResult() {
        assertEquals("read book", Parser.getArguments("todo   read book  "));
    }

    @Test
    void getCommandWord_leadingWhitespace_isIgnored() {
        assertEquals("todo", Parser.getCommandWord("   todo read book"));
    }

    @Test
    void getArguments_leadingWhitespaceOnInput_isIgnored() {
        assertEquals("read book", Parser.getArguments("   todo read book"));
    }

    @Test
    void getCommandWord_onlyWhitespace_returnsEmptyString() {
        assertEquals("", Parser.getCommandWord("    "));
    }

    @Test
    void getArguments_onlyWhitespace_returnsEmptyString() {
        assertEquals("", Parser.getArguments("    "));
    }

    @Test
    void parseCommandType_allKnownCommandWords_returnMatchingType() {
        assertEquals(CommandType.BYE, Parser.parseCommandType("bye"));
        assertEquals(CommandType.LIST, Parser.parseCommandType("list"));
        assertEquals(CommandType.MARK, Parser.parseCommandType("mark"));
        assertEquals(CommandType.UNMARK, Parser.parseCommandType("unmark"));
        assertEquals(CommandType.DELETE, Parser.parseCommandType("delete"));
        assertEquals(CommandType.TODO, Parser.parseCommandType("todo"));
        assertEquals(CommandType.DEADLINE, Parser.parseCommandType("deadline"));
        assertEquals(CommandType.EVENT, Parser.parseCommandType("event"));
        assertEquals(CommandType.ON, Parser.parseCommandType("on"));
        assertEquals(CommandType.FIND, Parser.parseCommandType("find"));
        assertEquals(CommandType.SORT, Parser.parseCommandType("sort"));
    }

    @Test
    void parseCommandType_unrecognizedWord_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("blah"));
    }
}
