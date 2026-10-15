package taterror.parser;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Parser}.
 */
public class ParserTest {

    @Test
    public void parseCommandType_everyRecognizedKeyword_returnsMatchingType() {
        assertEquals(CommandType.BYE, Parser.parseCommandType("bye"));
        assertEquals(CommandType.LIST, Parser.parseCommandType("list"));
        assertEquals(CommandType.MARK, Parser.parseCommandType("mark 1"));
        assertEquals(CommandType.UNMARK, Parser.parseCommandType("unmark 1"));
        assertEquals(CommandType.DELETE, Parser.parseCommandType("delete 1"));
        assertEquals(CommandType.TODO, Parser.parseCommandType("todo read book"));
        assertEquals(CommandType.DEADLINE, Parser.parseCommandType("deadline return book /by 2019-10-15"));
        assertEquals(CommandType.EVENT, Parser.parseCommandType("event meeting /from 2pm /to 4pm"));
        assertEquals(CommandType.FIND, Parser.parseCommandType("find book"));
        assertEquals(CommandType.PRIORITY, Parser.parseCommandType("priority 1 high"));
    }

    @Test
    public void parseCommandType_bareKeywordWithNoArguments_stillRecognized() {
        assertEquals(CommandType.TODO, Parser.parseCommandType("todo"));
        assertEquals(CommandType.DEADLINE, Parser.parseCommandType("deadline"));
        assertEquals(CommandType.EVENT, Parser.parseCommandType("event"));
        assertEquals(CommandType.FIND, Parser.parseCommandType("find"));
        assertEquals(CommandType.PRIORITY, Parser.parseCommandType("priority"));
    }

    @Test
    public void parseCommandType_unrecognizedInput_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("blahblahblah"));
    }

    @Test
    public void parseTaskIndex_convertsOneBasedInputToZeroBasedIndex() {
        assertEquals(1, Parser.parseTaskIndex("mark 2", "mark"));
        assertEquals(0, Parser.parseTaskIndex("delete 1", "delete"));
    }

    @Test
    public void parseTaskIndex_trailingOrExtraWhitespace_stillParses() {
        assertEquals(0, Parser.parseTaskIndex("mark 1 ", "mark"));
        assertEquals(1, Parser.parseTaskIndex("delete  2", "delete"));
    }

    @Test
    public void parseArguments_bareKeyword_returnsEmptyString() {
        assertEquals("", Parser.parseArguments("todo", "todo"));
    }

    @Test
    public void parseArguments_keywordPlusText_returnsTextAfterKeyword() {
        assertEquals("read book", Parser.parseArguments("todo read book", "todo"));
    }

    @Test
    public void splitDeadlineArgs_missingBySeparator_returnsNull() {
        assertNull(Parser.splitDeadlineArgs("return book"));
    }

    @Test
    public void splitDeadlineArgs_wellFormed_returnsDescriptionAndBy() {
        assertArrayEquals(
                new String[] {"return book", "2019-10-15"},
                Parser.splitDeadlineArgs("return book /by 2019-10-15"));
    }

    @Test
    public void splitEventArgs_missingFromOrTo_returnsNull() {
        assertNull(Parser.splitEventArgs("meeting /from 2pm"));
        assertNull(Parser.splitEventArgs("meeting /to 4pm"));
        assertNull(Parser.splitEventArgs("meeting"));
    }

    @Test
    public void splitEventArgs_wellFormed_returnsDescriptionFromAndTo() {
        assertArrayEquals(
                new String[] {"meeting", "2pm", "4pm"},
                Parser.splitEventArgs("meeting /from 2pm /to 4pm"));
    }

    @Test
    public void extractPriorityFlag_noFlag_returnsOriginalArgumentsAndNullLevel() {
        String[] result = Parser.extractPriorityFlag("read book");
        assertEquals("read book", result[0]);
        assertNull(result[1]);
    }

    @Test
    public void extractPriorityFlag_withFlag_stripsFlagAndReturnsRawLevel() {
        String[] result = Parser.extractPriorityFlag("read book /priority high");
        assertEquals("read book", result[0]);
        assertEquals("high", result[1]);
    }
}
