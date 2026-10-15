package jeff.parser;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ParserTest {
    @Test
    public void parseCommandType_recognizedWords_returnMatchingCommand() {
        assertEquals(Parser.Command.BYE, Parser.parseCommandType("bye"));
        assertEquals(Parser.Command.LIST, Parser.parseCommandType("list"));
        assertEquals(Parser.Command.MARK, Parser.parseCommandType("mark 1"));
        assertEquals(Parser.Command.UNMARK, Parser.parseCommandType("unmark 1"));
        assertEquals(Parser.Command.DELETE, Parser.parseCommandType("delete 1"));
        assertEquals(Parser.Command.TODO, Parser.parseCommandType("todo read book"));
        assertEquals(Parser.Command.DEADLINE, Parser.parseCommandType("deadline return book /by 2019-10-15"));
        assertEquals(Parser.Command.EVENT, Parser.parseCommandType("event trip /from 2019-11-01 /to 2019-11-05"));
        assertEquals(Parser.Command.FIND, Parser.parseCommandType("find book"));
        assertEquals(Parser.Command.SORT, Parser.parseCommandType("sort"));
    }

    @Test
    public void parseCommandType_unrecognizedOrEmptyWord_returnsUnknown() {
        assertEquals(Parser.Command.UNKNOWN, Parser.parseCommandType("frobnicate"));
        assertEquals(Parser.Command.UNKNOWN, Parser.parseCommandType(""));
    }

    @Test
    public void parseCommandType_isCaseSensitive() {
        assertEquals(Parser.Command.UNKNOWN, Parser.parseCommandType("LIST"));
    }

    @Test
    public void getArguments_noArguments_returnsEmptyString() {
        assertEquals("", Parser.getArguments("list"));
    }

    @Test
    public void getArguments_withArguments_returnsTrimmedRemainder() {
        assertEquals("read book", Parser.getArguments("todo   read book"));
    }

    @Test
    public void splitDeadlineArgs_validArguments_splitsIntoDescriptionAndBy() {
        String[] parts = Parser.splitDeadlineArgs("return book /by 2019-10-15");
        assertArrayEquals(new String[] { "return book", "2019-10-15" }, parts);
    }

    @Test
    public void splitDeadlineArgs_missingBySegment_throwsArrayIndexOutOfBoundsException() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> Parser.splitDeadlineArgs("return book"));
    }

    @Test
    public void splitDeadlineArgs_byLabelWithNothingAfterIt_returnsEmptyByField() {
        String[] parts = Parser.splitDeadlineArgs("return book /by");
        assertArrayEquals(new String[] { "return book", "" }, parts);
    }

    @Test
    public void splitEventArgs_validArguments_splitsIntoDescriptionFromAndTo() {
        String[] parts = Parser.splitEventArgs("trip /from 2019-11-01 /to 2019-11-05");
        assertArrayEquals(new String[] { "trip", "2019-11-01", "2019-11-05" }, parts);
    }

    @Test
    public void splitEventArgs_missingToSegment_throwsArrayIndexOutOfBoundsException() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> Parser.splitEventArgs("trip /from 2019-11-01"));
    }

    @Test
    public void splitEventArgs_missingBothSegments_throwsArrayIndexOutOfBoundsException() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> Parser.splitEventArgs("trip"));
    }

    @Test
    public void parseTaskIndex_validNumber_returnsZeroBasedIndex() {
        assertEquals(0, Parser.parseTaskIndex("1", 3));
        assertEquals(2, Parser.parseTaskIndex("3", 3));
    }

    @Test
    public void parseTaskIndex_nonNumeric_throwsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> Parser.parseTaskIndex("abc", 3));
    }

    @Test
    public void parseTaskIndex_outOfRange_throwsIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> Parser.parseTaskIndex("0", 3));
        assertThrows(IndexOutOfBoundsException.class, () -> Parser.parseTaskIndex("4", 3));
    }

    @Test
    public void parseTaskIndex_negativeNumber_throwsIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> Parser.parseTaskIndex("-5", 3));
    }

    @Test
    public void parseTaskIndex_emptyString_throwsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> Parser.parseTaskIndex("", 3));
    }
}
