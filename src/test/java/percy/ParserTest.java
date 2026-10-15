package percy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Parser}, which is the class most worth testing: it does the
 * string-wrangling and validation that the rest of the program relies on.
 */
public class ParserTest {

    // ---- parse(String) -----------------------------------------------------

    @Test
    public void parse_commandWithArguments_splitsWordFromArguments() {
        Parser.Command c = Parser.parse("deadline return book /by 2019-10-15");
        assertEquals(Parser.CommandType.DEADLINE, c.getType());
        assertEquals("return book /by 2019-10-15", c.getArguments());
    }

    @Test
    public void parse_commandWithoutArguments_argumentsAreEmptyString() {
        Parser.Command c = Parser.parse("list");
        assertEquals(Parser.CommandType.LIST, c.getType());
        assertEquals("", c.getArguments());
    }

    @Test
    public void parse_surroundingAndRepeatedWhitespace_isTrimmed() {
        Parser.Command c = Parser.parse("   todo    read book   ");
        assertEquals(Parser.CommandType.TODO, c.getType());
        assertEquals("read book", c.getArguments());
    }

    @Test
    public void parse_unrecognisedOrEmptyInput_isUnknown() {
        assertEquals(Parser.CommandType.UNKNOWN, Parser.parse("blah blah").getType());
        assertEquals(Parser.CommandType.UNKNOWN, Parser.parse("").getType());
        assertEquals(Parser.CommandType.UNKNOWN, Parser.parse("   ").getType());
    }

    // ---- parseIndex(String) ---------------------------------------------------

    @Test
    public void parseIndex_validNumber_convertedToZeroBasedIndex() throws PercyException {
        assertEquals(0, Parser.parseIndex("1"));
        assertEquals(41, Parser.parseIndex("  42  "));
    }

    @Test
    public void parseIndex_missingOrNonNumeric_throwsPercyException() {
        assertThrows(PercyException.class, () -> Parser.parseIndex(""));
        assertThrows(PercyException.class, () -> Parser.parseIndex("abc"));
        assertThrows(PercyException.class, () -> Parser.parseIndex("1.5"));
        assertThrows(PercyException.class, () -> Parser.parseIndex("1 2"));
    }

    // ---- parseDeadline(String) ---------------------------------------------

    @Test
    public void parseDeadline_validInput_buildsDeadlineWithParsedDate() throws PercyException {
        Deadline d = Parser.parseDeadline("return book /by 2019-10-15");
        assertEquals("D | 0 | return book | 2019-10-15", d.toFileFormat());
        assertEquals("return book (by: Oct 15 2019)", d.getDescription());
    }

    @Test
    public void parseDeadline_extraWhitespaceAroundParts_isTrimmed() throws PercyException {
        Deadline d = Parser.parseDeadline("  return book   /by 2019-10-15  ");
        assertEquals("return book", d.getRawDescription());
    }

    @Test
    public void parseDeadline_missingDescriptionOrByDate_throwsPercyException() {
        assertThrows(PercyException.class, () -> Parser.parseDeadline(""));
        assertThrows(PercyException.class, () -> Parser.parseDeadline("return book"));
        assertThrows(PercyException.class, () -> Parser.parseDeadline("return book /by "));
        assertThrows(PercyException.class, () -> Parser.parseDeadline(" /by 2019-10-15"));
    }

    @Test
    public void parseDeadline_dateNotIsoFormat_throwsPercyException() {
        assertThrows(PercyException.class, () -> Parser.parseDeadline("return book /by 15-10-2019"));
        assertThrows(PercyException.class, () -> Parser.parseDeadline("return book /by tomorrow"));
        assertThrows(PercyException.class, () -> Parser.parseDeadline("return book /by 2019-13-40"));
    }

    // ---- parseEvent(String) ----------------------------------------------------

    @Test
    public void parseEvent_validInput_buildsEventWithBothDates() throws PercyException {
        Event e = Parser.parseEvent("project meeting /from 2019-10-10 /to 2019-10-12");
        assertEquals("E | 0 | project meeting | 2019-10-10 | 2019-10-12", e.toFileFormat());
        assertEquals("project meeting (from: Oct 10 2019 to: Oct 12 2019)", e.getDescription());
    }

    @Test
    public void parseEvent_anyPartMissing_throwsPercyException() {
        assertThrows(PercyException.class, () -> Parser.parseEvent(""));
        assertThrows(PercyException.class, () -> Parser.parseEvent("project /from 2019-10-10"));
        assertThrows(PercyException.class, () -> Parser.parseEvent("project /to 2019-10-12"));
        assertThrows(PercyException.class, () -> Parser.parseEvent(" /from 2019-10-10 /to 2019-10-12"));
        assertThrows(PercyException.class, () -> Parser.parseEvent("project /from  /to 2019-10-12"));
    }

    @Test
    public void parseEvent_invalidDate_throwsPercyException() {
        assertThrows(PercyException.class,
                () -> Parser.parseEvent("project /from 2019-10-10 /to next-week"));
    }

    // ---- parseTodo(String) ---------------------------------------------------

    @Test
    public void parseTodo_nonEmptyDescription_buildsTodo() throws PercyException {
        Todo t = Parser.parseTodo("read book");
        assertEquals("T | 0 | read book", t.toFileFormat());
    }

    @Test
    public void parseTodo_emptyDescription_throwsPercyException() {
        assertThrows(PercyException.class, () -> Parser.parseTodo(""));
    }

    @Test
    public void parseTodo_errorMessage_isInformative() {
        PercyException e = assertThrows(PercyException.class, () -> Parser.parseTodo(""));
        assertTrue(e.getMessage().toLowerCase().contains("todo"));
    }

    // ---- parseUpdate(String) -----------------------------------------------

    @Test
    public void parseUpdate_dateFieldOnly_leavesOthersNull() throws PercyException {
        Parser.UpdateSpec spec = Parser.parseUpdate("2 /by 2019-12-01");
        assertEquals(1, spec.getIndex());
        assertNull(spec.getDescription());
        assertEquals(LocalDate.of(2019, 12, 1), spec.getBy());
        assertNull(spec.getFrom());
        assertNull(spec.getTo());
    }

    @Test
    public void parseUpdate_descriptionAndDates_allCaptured() throws PercyException {
        Parser.UpdateSpec spec = Parser.parseUpdate("3 team sync /from 2020-01-01 /to 2020-01-02");
        assertEquals(2, spec.getIndex());
        assertEquals("team sync", spec.getDescription());
        assertEquals(LocalDate.of(2020, 1, 1), spec.getFrom());
        assertEquals(LocalDate.of(2020, 1, 2), spec.getTo());
        assertNull(spec.getBy());
    }

    @Test
    public void parseUpdate_descriptionOnly() throws PercyException {
        Parser.UpdateSpec spec = Parser.parseUpdate("1 buy more milk");
        assertEquals("buy more milk", spec.getDescription());
        assertNull(spec.getBy());
    }

    @Test
    public void parseUpdate_missingArgsOrNoChange_throwsPercyException() {
        assertThrows(PercyException.class, () -> Parser.parseUpdate(""));
        assertThrows(PercyException.class, () -> Parser.parseUpdate("2"));
        assertThrows(PercyException.class, () -> Parser.parseUpdate("abc /by 2019-12-01"));
        assertThrows(PercyException.class, () -> Parser.parseUpdate("2 /by not-a-date"));
    }
}
