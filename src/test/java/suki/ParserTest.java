package suki;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Parser}, covering both well-formed commands and the
 * malformed ones that should produce a helpful error rather than a crash.
 */
public class ParserTest {

    @Test
    public void parse_commandWithArguments_splitsOnFirstSpaceOnly() {
        Parser.ParsedInput parsed = Parser.parse("deadline return book /by 2019-10-15");
        assertEquals(CommandType.DEADLINE, parsed.commandType());
        assertEquals("deadline", parsed.commandWord());
        assertEquals("return book /by 2019-10-15", parsed.arguments());
    }

    @Test
    public void parse_commandWithoutArguments_givesEmptyArguments() {
        Parser.ParsedInput parsed = Parser.parse("list");
        assertEquals(CommandType.LIST, parsed.commandType());
        assertEquals("", parsed.arguments());
    }

    @Test
    public void parse_multipleWhitespace_splitsCommandCleanly() {
        Parser.ParsedInput parsed = Parser.parse("  deadline   return book /by 2019-10-15  ");
        assertEquals(CommandType.DEADLINE, parsed.commandType());
        assertEquals("return book /by 2019-10-15", parsed.arguments());
    }

    @Test
    public void parse_unknownCommand_mapsToUnknown() {
        assertEquals(CommandType.UNKNOWN, Parser.parse("blah blah").commandType());
    }

    @Test
    public void parseIndex_validNumber_convertsToZeroBased() throws SukiException {
        assertEquals(0, Parser.parseIndex("1", "mark"));
        assertEquals(4, Parser.parseIndex("5", "delete"));
    }

    @Test
    public void parseIndex_notANumber_throws() {
        assertThrows(SukiException.class, () -> Parser.parseIndex("one", "mark"));
        assertThrows(SukiException.class, () -> Parser.parseIndex("", "mark"));
        assertThrows(SukiException.class, () -> Parser.parseIndex("0", "mark"));
        assertThrows(SukiException.class, () -> Parser.parseIndex("-1", "mark"));
    }

    @Test
    public void parseTodo_emptyDescription_throws() {
        assertThrows(SukiException.class, () -> Parser.parseTodo(""));
    }

    @Test
    public void parseDeadline_validInput_buildsDeadline() throws SukiException {
        Deadline deadline = Parser.parseDeadline("return book /by 2019-10-15");
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void parseDeadline_extraSpacesAroundDelimiter_isAccepted() throws SukiException {
        Deadline deadline = Parser.parseDeadline("return book   /by   2019-10-15");
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void parseDeadline_missingByClause_throws() {
        assertThrows(SukiException.class, () -> Parser.parseDeadline("return book"));
    }

    @Test
    public void parseDeadline_emptyDescription_throws() {
        assertThrows(SukiException.class, () -> Parser.parseDeadline(" /by 2019-10-15"));
    }

    @Test
    public void parseEvent_validInput_buildsEvent() throws SukiException {
        Event event = Parser.parseEvent("meeting /from 2019-10-15 1400 /to 2019-10-15 1600");
        assertEquals("[E][ ] meeting (from: Oct 15 2019, 2:00PM to: Oct 15 2019, 4:00PM)",
                event.toString());
    }

    @Test
    public void parseEvent_missingToClause_throws() {
        assertThrows(SukiException.class, () -> Parser.parseEvent("meeting /from 2019-10-15"));
    }

    @Test
    public void parseEvent_endBeforeOrAtStart_throws() {
        assertThrows(SukiException.class,
                () -> Parser.parseEvent("meeting /from 2019-10-15 1600 /to 2019-10-15 1400"));
        assertThrows(SukiException.class,
                () -> Parser.parseEvent("meeting /from 2019-10-15 1400 /to 2019-10-15 1400"));
    }

    @Test
    public void requireNoArguments_unexpectedArguments_throws() {
        assertThrows(SukiException.class, () -> Parser.requireNoArguments("now", "list"));
    }
}
