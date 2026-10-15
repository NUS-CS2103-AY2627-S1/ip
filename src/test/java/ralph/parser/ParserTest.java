package ralph.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import ralph.exception.RalphException;

class ParserTest {

    @Test
    void parseCommand_nullEmpty_throwsRalphException() {
        Parser p = new Parser();
        assertThrows(RalphException.class, () -> p.parseCommand(null));
        assertThrows(RalphException.class, () -> p.parseCommand("   "));
    }

    @Test
    void parseCommand_normalInput_parsedLowercaseAndArgs() throws RalphException {
        Parser p = new Parser();
        Parser.Parsed parsed = p.parseCommand("   ToDo   read book  ");
        assertEquals("todo", parsed.command());
        assertEquals("read book", parsed.args());
    }

    @Test
    void parseCommand_singleWord_returnsEmptyArgs() throws RalphException {
        Parser p = new Parser();
        Parser.Parsed parsed = p.parseCommand("list");
        assertEquals("list", parsed.command());
        assertEquals("", parsed.args());
    }

    @Test
    void parseTaskIndex_empty_throws() {
        assertThrows(RalphException.class, () -> Parser.parseTaskIndex("", "mark"));
    }

    @Test
    void parseTaskIndex_nonNumeric_throws() {
        assertThrows(RalphException.class, () -> Parser.parseTaskIndex("abc", "mark"));
    }

    @Test
    void parseTaskIndex_nonPositive_throws() {
        assertThrows(RalphException.class, () -> Parser.parseTaskIndex("0", "mark"));
        assertThrows(RalphException.class, () -> Parser.parseTaskIndex("-3", "mark"));
    }

    @Test
    void parseTaskIndex_numeric_returnsZeroBased() {
        try {
            int idx = Parser.parseTaskIndex("2", "mark");
            assertEquals(1, idx);
        } catch (RalphException e) {
            fail("Unexpected exception for numeric input");
        }
    }

    @Test
    void getRequiredDescription_missingForCommands_throws() {
        assertThrows(RalphException.class, () -> Parser.getRequiredDescription("", "todo"));
        assertThrows(RalphException.class, () -> Parser.getRequiredDescription("   ", "deadline"));
        assertThrows(RalphException.class, () -> Parser.getRequiredDescription(null, "event"));
        assertThrows(RalphException.class, () -> Parser.getRequiredDescription("", "find"));
    }

    @Test
    void getRequiredDescription_present_returnsTrimmed() throws RalphException {
        String res = Parser.getRequiredDescription("  do homework  ", "todo");
        assertEquals("do homework", res);

        String findRes = Parser.getRequiredDescription("  book  ", "find");
        assertEquals("book", findRes);
    }

    @Test
    void getRequiredDescription_unknownCommand_returnsEmptyString() throws RalphException {
        // Unknown commands are allowed to return an empty string rather than throwing.
        String res = Parser.getRequiredDescription("", "unknowncmd");
        assertEquals("", res);
    }

    @Test
    void parseDateTime_validFormats_parsedCorrectly() throws RalphException {
        LocalDateTime dt1 = Parser.parseDateTime("2023-09-01");
        assertEquals(LocalDateTime.of(2023, 9, 1, 0, 0), dt1);

        LocalDateTime dt2 = Parser.parseDateTime("2023-09-01 18:30");
        assertEquals(LocalDateTime.of(2023, 9, 1, 18, 30), dt2);

        LocalDateTime dt3 = Parser.parseDateTime("2023-09-01T07:15");
        assertEquals(LocalDateTime.of(2023, 9, 1, 7, 15), dt3);
    }

    @Test
    void parseDateTime_invalid_throwsRalphException() {
        assertThrows(RalphException.class, () -> Parser.parseDateTime("not a date"));
        assertThrows(RalphException.class, () -> Parser.parseDateTime(""));
    }
}
