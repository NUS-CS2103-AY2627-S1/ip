package crack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ParserTest {
    @Test
    public void parse_commandAndArguments_splitApart() throws CrackException {
        Parser.Parsed parsed = Parser.parse("deadline return book /by 2/12/2020");
        assertEquals(Command.DEADLINE, parsed.command());
        assertEquals("return book /by 2/12/2020", parsed.arguments());
    }

    @Test
    public void parse_commandWithNothingAfterIt_givesBlankArguments() throws CrackException {
        assertEquals("", Parser.parse("list").arguments());
    }

    @Test
    public void parse_unknownCommand_throws() {
        assertThrows(CrackException.class, () -> Parser.parse("blah"));
    }

    @Test
    public void parseTodo_blankDescription_throws() {
        assertThrows(CrackException.class, () -> Parser.parseTodo(""));
    }

    @Test
    public void parseDeadline_wellFormed_buildsTheTask() throws CrackException {
        assertEquals("[D][ ] return book (by: Dec 2 2020, 3:00 PM)",
                Parser.parseDeadline("return book /by 2/12/2020 1500").toString());
    }

    @Test
    public void parseDeadline_missingBy_throws() {
        assertThrows(CrackException.class, () -> Parser.parseDeadline("return book"));
        assertThrows(CrackException.class, () -> Parser.parseDeadline("return book /by "));
    }

    @Test
    public void parseEvent_wellFormed_buildsTheTask() throws CrackException {
        assertEquals("[E][ ] carnival (from: Dec 1 2020 to: Dec 3 2020)",
                Parser.parseEvent("carnival /from 1/12/2020 /to 3/12/2020").toString());
    }

    @Test
    public void parseEvent_missingFromOrTo_throws() {
        assertThrows(CrackException.class, () -> Parser.parseEvent("carnival /to 3/12/2020"));
        assertThrows(CrackException.class, () -> Parser.parseEvent("carnival /from 1/12/2020"));
    }

    @Test
    public void parse_extraSpacesAndTabs_stillFindsCommandAndArguments() throws CrackException {
        assertEquals(Command.TODO, Parser.parse("todo   read book").command());
        assertEquals("read book", Parser.parse("todo   read book").arguments());
        assertEquals(Command.LIST, Parser.parse("list   ").command());
        assertEquals("read book", Parser.parse("todo\tread book").arguments());
    }

    @Test
    public void parseIndex_validNumber_becomesZeroBased() throws CrackException {
        assertEquals(0, Parser.parseIndex("1", 3));
        assertEquals(2, Parser.parseIndex("3", 3));
    }

    @Test
    public void parseIndex_outOfRangeOrNotANumber_throws() {
        assertThrows(CrackException.class, () -> Parser.parseIndex("0", 3));
        assertThrows(CrackException.class, () -> Parser.parseIndex("4", 3));
        assertThrows(CrackException.class, () -> Parser.parseIndex("two", 3));
        assertThrows(CrackException.class, () -> Parser.parseIndex("", 3));
        assertThrows(CrackException.class, () -> Parser.parseIndex("1", 0));
    }

    @Test
    public void parseKeyword_wordGiven_returnsIt() throws CrackException {
        assertEquals("book", Parser.parseKeyword("book"));
    }

    @Test
    public void parseKeyword_blank_throws() {
        assertThrows(CrackException.class, () -> Parser.parseKeyword(""));
    }

    @Test
    public void parse_findCommand_recognised() throws CrackException {
        Parser.Parsed parsed = Parser.parse("find book");
        assertEquals(Command.FIND, parsed.command());
        assertEquals("book", parsed.arguments());
    }

    @Test
    public void parseDate_blank_throws() {
        assertThrows(CrackException.class, () -> Parser.parseDate(""));
    }
}
