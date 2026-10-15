package bos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests command and date-time parsing behavior provided by {@link Parser}.
 */
public class ParserTest {

    @Test
    public void parseCommandType_findCommand_findReturned() {
        assertEquals(CommandType.FIND, Parser.parseCommandType("find book"));
    }

    @Test
    public void parseCommandType_leadingAndRepeatedWhitespace_commandReturned() {
        assertEquals(CommandType.MARK, Parser.parseCommandType("  mark   1  "));
    }

    @Test
    public void parseFindKeyword_validCommand_trimmedKeywordReturned() throws BosException {
        assertEquals("read book", Parser.parseFindKeyword("find   read book  "));
    }

    @Test
    public void parseFindKeyword_missingKeyword_exceptionThrown() {
        assertThrows(BosException.class, () -> Parser.parseFindKeyword("find"));
        assertThrows(BosException.class, () -> Parser.parseFindKeyword("find   "));
    }

    @Test
    public void parseTaskIndex_validNumberWithExtraWhitespace_indexReturned() throws BosException {
        assertEquals(1, Parser.parseTaskIndex("  mark   2  ", CommandType.MARK, 2));
    }

    @Test
    public void parseTaskIndex_malformedOrOverflowingNumber_exceptionThrown() {
        assertThrows(
                BosException.class,
                () -> Parser.parseTaskIndex("mark +1", CommandType.MARK, 1));
        assertThrows(
                BosException.class,
                () -> Parser.parseTaskIndex(
                        "mark 999999999999999999999",
                        CommandType.MARK,
                        1));
        assertThrows(
                BosException.class,
                () -> Parser.parseTaskIndex("mark 1 2", CommandType.MARK, 2));
    }

    @Test
    public void parseTask_missingEssentialParameters_exceptionThrown() {
        assertThrows(
                BosException.class,
                () -> Parser.parseTask("deadline report", CommandType.DEADLINE));
        assertThrows(
                BosException.class,
                () -> Parser.parseTask("event meeting /from Monday", CommandType.EVENT));
        assertThrows(
                BosException.class,
                () -> Parser.parseTask("event meeting /from /to Tuesday", CommandType.EVENT));
    }

    @Test
    public void parseTask_storageDelimiterOrControlCharacter_exceptionThrown() {
        assertThrows(
                BosException.class,
                () -> Parser.parseTask("todo read | book", CommandType.TODO));
        assertThrows(
                BosException.class,
                () -> Parser.parseTask("todo read\tbook", CommandType.TODO));
    }

    @Test
    public void parseTask_freeFormDateTime_exceptionThrown() {
        assertThrows(
                BosException.class,
                () -> Parser.parseTask(
                        "event meeting /from 2026-09-11 1000 /to Friday",
                        CommandType.EVENT));
        assertThrows(
                BosException.class,
                () -> Parser.parseTask("deadline report /by Feb 30", CommandType.DEADLINE));
    }

    @Test
    public void formatDateTimeForDisplay_validDateTime_formattedTextReturned() {
        assertEquals("Aug 28 2026, 2:30 PM", Parser.formatDateTimeForDisplay("2026-08-28 1430"));
    }

    @Test
    public void formatDateTimeForDisplay_validLeapDay_formattedTextReturned() {
        assertEquals("Feb 29 2024, 12:00 AM", Parser.formatDateTimeForDisplay("2024-02-29 0000"));
    }

    @Test
    public void formatDateTimeForDisplay_invalidCalendarDate_originalTextReturned() {
        String input = "2023-02-29 1200";

        assertEquals(input, Parser.formatDateTimeForDisplay(input));
    }

    @Test
    public void formatDateTimeForDisplay_invalidTime_originalTextReturned() {
        String input = "2026-08-28 2400";

        assertEquals(input, Parser.formatDateTimeForDisplay(input));
    }

    @Test
    public void formatDateTimeForDisplay_incorrectFormat_originalTextReturned() {
        String input = "28-08-2026 1430";

        assertEquals(input, Parser.formatDateTimeForDisplay(input));
    }

    @Test
    public void formatDateTimeForDisplay_freeFormText_originalTextReturned() {
        String input = "tomorrow evening";

        assertEquals(input, Parser.formatDateTimeForDisplay(input));
    }
}
