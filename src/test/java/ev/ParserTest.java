package ev;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Contains unit tests for {@link Parser}.
 */
public class ParserTest {
    @Test
    public void requireNoArguments_extraText_throwsEvException() {
        EvException thrown = assertThrows(
                EvException.class, () -> Parser.requireNoArguments("sort", "buy"));

        assertEquals("The sort command takes no extra words.", thrown.getMessage());
    }

    @Test
    public void requireNoArguments_noText_doesNotThrow() {
        assertDoesNotThrow(() -> Parser.requireNoArguments("sort", ""));
    }

    @Test
    public void parseTaskIndex_validNumber_returnsZeroBasedIndex() throws EvException {
        assertEquals(0, Parser.parseTaskIndex("1", 3));
        assertEquals(2, Parser.parseTaskIndex("3", 3));
    }

    @Test
    public void parseTaskIndex_notANumber_throwsEvException() {
        EvException thrown = assertThrows(EvException.class, () -> Parser.parseTaskIndex("abc", 3));
        assertEquals("Task number must be a number.", thrown.getMessage());
    }

    @Test
    public void parseTaskIndex_outOfRange_throwsEvException() {
        assertThrows(EvException.class, () -> Parser.parseTaskIndex("0", 3));
        assertThrows(EvException.class, () -> Parser.parseTaskIndex("4", 3));
    }

    @Test
    public void parseDeadline_validInput_returnsDeadline() throws EvException {
        assertEquals("[D][ ] return book (by: Sep 18 2026, 6:00PM)",
                Parser.parseDeadline("return book /by 2026-09-18 1800").toString());
    }

    @Test
    public void parseDeadline_missingByTime_throwsEvException() {
        EvException thrown = assertThrows(EvException.class, () -> Parser.parseDeadline("return book"));
        assertEquals("A deadline needs a description and a /by time.", thrown.getMessage());
    }

    @Test
    public void parseDeadline_invalidDate_throwsEvException() {
        EvException thrown = assertThrows(
                EvException.class, () -> Parser.parseDeadline("return book /by Sunday"));

        assertEquals("Dates must look like 2026-09-18 1800.", thrown.getMessage());
    }

    @Test
    public void parseTodo_descriptionWithSeparator_throwsEvException() {
        assertThrows(EvException.class, () -> Parser.parseTodo("alpha | beta"));
    }

    @Test
    public void parseDeadline_impossibleDate_throwsEvException() {
        assertThrows(EvException.class, () -> Parser.parseDeadline("x /by 2026-02-30 1800"));
        assertThrows(EvException.class, () -> Parser.parseDeadline("x /by 2026-02-29 1800"));
    }

    @Test
    public void parseDeadline_hourOutOfRange_throwsEvException() {
        assertThrows(EvException.class, () -> Parser.parseDeadline("x /by 2026-09-18 2400"));
    }

    @Test
    public void parseDeadline_leapDay_returnsDeadline() throws EvException {
        assertEquals("[D][ ] x (by: Feb 29 2024, 6:00PM)",
                Parser.parseDeadline("x /by 2024-02-29 1800").toString());
    }

    @Test
    public void parseSearchKeyword_empty_throwsEvException() {
        assertThrows(EvException.class, () -> Parser.parseSearchKeyword(""));
    }

    @Test
    public void parseEvent_endBeforeStart_throwsEvException() {
        assertThrows(
                EvException.class, () -> Parser.parseEvent("x /from 2026-09-20 1600 /to 2026-09-20 1400"));
    }

    @Test
    public void parseCommand_surroundingWhitespace_stillParses() throws EvException {
        assertEquals(Command.LIST, Parser.parseCommand(" list "));
        assertEquals(Command.TODO, Parser.parseCommand("\ttodo x"));
    }

    @Test
    public void parseCommand_help_returnsHelpCommand() throws EvException {
        assertEquals(Command.HELP, Parser.parseCommand("help"));
    }

    @Test
    public void parseArguments_extraWhitespace_returnsTrimmedText() {
        assertEquals("read book", Parser.parseArguments("  todo   read book  "));
    }

}
