package puyo.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import puyo.PuyoException;
import puyo.command.AddCommand;
import puyo.command.ByeCommand;
import puyo.command.Command;
import puyo.command.DeleteCommand;
import puyo.command.ListCommand;
import puyo.command.MarkCommand;
import puyo.command.UnknownCommand;
import puyo.command.UnmarkCommand;
import puyo.command.ViewScheduleCommand;

/**
 * Tests for {@code Parser}.
 */
public class ParserTest {

    // -------------------------------------------------------------------------
    // 1. Basic & Simple Commands
    // -------------------------------------------------------------------------

    @Test
    public void parse_emptyInput_exceptionThrown() {
        PuyoException ex = assertThrows(PuyoException.class, () -> Parser.parse("   "));
        assertEquals("Please enter a non-empty valid command!", ex.getMessage());
    }

    @Test
    public void parse_byeCommand_returnsByeCommand() throws Exception {
        Command c = Parser.parse("bye");
        assertInstanceOf(ByeCommand.class, c);
    }

    @Test
    public void parse_listCommand_returnsListCommand() throws Exception {
        Command c = Parser.parse("list");
        assertInstanceOf(ListCommand.class, c);
    }

    @Test
    public void parse_unknownCommand_returnsUnknownCommand() throws Exception {
        Command c = Parser.parse("foobar 123");
        assertInstanceOf(UnknownCommand.class, c);
    }

    // -------------------------------------------------------------------------
    // 2. Mark / Unmark / Delete Commands
    // -------------------------------------------------------------------------

    @Test
    public void parse_markValidIndex_returnsMarkCommand() throws Exception {
        Command c = Parser.parse("mark 2");
        assertInstanceOf(MarkCommand.class, c);
    }

    @Test
    public void parse_markInvalidIndex_exceptionThrown() {
        assertThrows(PuyoException.class, () -> Parser.parse("mark abc"));
        assertThrows(PuyoException.class, () -> Parser.parse("mark"));
    }

    @Test
    public void parse_unmarkValidIndex_returnsUnmarkCommand() throws Exception {
        Command c = Parser.parse("unmark 5");
        assertInstanceOf(UnmarkCommand.class, c);
    }

    @Test
    public void parse_deleteValidIndex_returnsDeleteCommand() throws Exception {
        Command c = Parser.parse("delete 1");
        assertInstanceOf(DeleteCommand.class, c);
    }

    @Test
    public void parse_deleteMissingIndex_exceptionThrown() {
        assertThrows(PuyoException.class, () -> Parser.parse("delete "));
    }

    // -------------------------------------------------------------------------
    // 3. ToDo Commands
    // -------------------------------------------------------------------------

    @Test
    public void parse_todoValidDescription_returnsAddCommand() throws Exception {
        Command c = Parser.parse("todo read book");
        assertInstanceOf(AddCommand.class, c);
    }

    @Test
    public void parse_todoEmptyDescription_exceptionThrown() {
        PuyoException ex = assertThrows(PuyoException.class, () -> Parser.parse("todo "));
        assertEquals("The description of a todo can't be empty!", ex.getMessage());
    }

    // -------------------------------------------------------------------------
    // 4. Deadline Commands
    // -------------------------------------------------------------------------

    @Test
    public void parse_deadlineValidDateTime_returnsAddCommand() throws Exception {
        Command c = Parser.parse("deadline return book /by 2026-12-02 1800");
        assertInstanceOf(AddCommand.class, c);
    }

    @Test
    public void parse_deadlineMissingByFlag_exceptionThrown() {
        PuyoException ex = assertThrows(PuyoException.class, () -> Parser.parse("deadline return book 2026-12-02"));
        assertEquals("Please enter a valid deadline by using '/by'!", ex.getMessage());
    }

    @Test
    public void parse_deadlineInvalidDateFormat_exceptionThrown() {
        assertThrows(PuyoException.class, () -> Parser.parse("deadline return book /by 02-12-2026"));
    }

    // -------------------------------------------------------------------------
    // 5. Event Commands
    // -------------------------------------------------------------------------

    @Test
    public void parse_eventValidDateTime_returnsAddCommand() throws Exception {
        Command c = Parser.parse("event project meeting /from 2026-08-28 1400 /to 2026-08-28 1600");
        assertInstanceOf(AddCommand.class, c);
    }

    @Test
    public void parse_eventReversedFlags_exceptionThrown() {
        PuyoException ex = assertThrows(PuyoException.class, () ->
                Parser.parse("event project meeting /to 2026-08-28 1600 /from 2026-08-28 1400")
        );
        assertEquals("Please enter a valid event timing by putting '/from' before '/to'!", ex.getMessage());
    }

    @Test
    public void parse_eventMissingToFlag_exceptionThrown() {
        assertThrows(PuyoException.class, () -> Parser.parse("event project meeting /from 2026-08-28 1400"));
    }

    // -------------------------------------------------------------------------
    // 6. DateTime Parsing Helper
    // -------------------------------------------------------------------------

    @Test
    public void parseDateTime_validInputs_returnsLocalDateTime() {
        LocalDateTime dt1 = Parser.parseDateTime("2026-08-28 1800");
        assertNotNull(dt1);
        assertEquals(18, dt1.getHour());

        LocalDateTime dt2 = Parser.parseDateTime("2026-08-28");
        assertNotNull(dt2);
        assertEquals(0, dt2.getHour());
    }

    @Test
    public void parseDateTime_invalidInput_returnsNull() {
        assertNull(Parser.parseDateTime("28-08-2026"));
        assertNull(Parser.parseDateTime("invalid date"));
    }

    // Regression tests below were developed with ChatGPT assistance.

    @Test
    public void parse_whitespaceAndUppercase_returnsCommand() throws Exception {
        assertInstanceOf(ListCommand.class, Parser.parse("  LIST  "));
        assertInstanceOf(MarkCommand.class, Parser.parse("\tMARK\t1  "));
        assertInstanceOf(AddCommand.class, Parser.parse("  TODO\tread book  "));
        assertInstanceOf(AddCommand.class, Parser.parse("DEADLINE report /BY 2026-09-18 2359"));
        assertInstanceOf(AddCommand.class,
                Parser.parse("EVENT meeting /FROM 2026-09-18 1400 /TO 2026-09-18 1500"));
    }

    @Test
    public void parse_nonPositiveOrOverflowingIndex_throwsException() {
        for (String command : new String[] {"mark", "unmark", "delete"}) {
            for (String index : new String[] {"0", "-1", "-2147483648", "2147483648"}) {
                assertThrows(PuyoException.class, () -> Parser.parse(command + " " + index));
            }
        }
    }

    @Test
    public void parseDateTime_impossibleDatesAndTimes_returnsNull() {
        assertNull(Parser.parseDateTime("2026-02-30"));
        assertNull(Parser.parseDateTime("2026-02-29 1200"));
        assertNull(Parser.parseDateTime("2026-04-31 0900"));
        assertNull(Parser.parseDateTime("2026-13-01"));
        assertNull(Parser.parseDateTime("2026-09-18 2400"));
        assertNull(Parser.parseDateTime("2026-09-18 1260"));
    }

    @Test
    public void parseDateTime_leapDayAndMidnight_returnsExactDateTime() {
        assertEquals(LocalDateTime.of(2028, 2, 29, 0, 0), Parser.parseDateTime("2028-02-29"));
        assertEquals(LocalDateTime.of(2028, 2, 29, 23, 59), Parser.parseDateTime("2028-02-29 2359"));
        assertEquals(LocalDateTime.of(2026, 9, 18, 0, 0), Parser.parseDateTime("2026-09-18 0000"));
    }

    @Test
    public void parseDateTime_supportedYears_preservesSavedDate() {
        for (String raw : new String[] {"0000-01-01 0000", "-0001-01-01 1200", "2026-09-18 2359"}) {
            LocalDateTime dateTime = Parser.parseDateTime(raw);
            assertNotNull(dateTime);
            assertEquals(raw, dateTime.format(Parser.SAVE_DATETIME));
            assertEquals(dateTime, Parser.parseDateTime(dateTime.format(Parser.SAVE_DATETIME)));
        }
    }

    @Test
    public void parse_impossibleDeadlineOrEventDate_throwsException() {
        assertThrows(PuyoException.class, () -> Parser.parse("deadline report /by 2026-02-30"));
        assertThrows(PuyoException.class, () ->
                Parser.parse("event meeting /from 2026-02-30 1200 /to 2026-03-01 1200"));
        assertThrows(PuyoException.class, () ->
                Parser.parse("event meeting /from 2026-04-30 1200 /to 2026-04-31 1200"));
    }

    @Test
    public void parse_emptyDeadlineOrEventField_throwsException() {
        assertThrows(PuyoException.class, () -> Parser.parse("deadline /by 2026-09-18"));
        assertThrows(PuyoException.class, () -> Parser.parse("deadline report /by"));
        assertThrows(PuyoException.class, () ->
                Parser.parse("event /from 2026-09-18 1400 /to 2026-09-18 1600"));
        assertThrows(PuyoException.class, () -> Parser.parse("event meeting /from /to 2026-09-18 1600"));
        assertThrows(PuyoException.class, () -> Parser.parse("event meeting /from 2026-09-18 1400 /to"));
    }

    @Test
    public void parse_viewScheduleValidDate_returnsCommand() throws Exception {
        assertInstanceOf(ViewScheduleCommand.class, Parser.parse("  VIEWSCHEDULE\t2028-02-29  "));
    }

    @Test
    public void parse_viewScheduleInvalidDate_throwsException() {
        assertThrows(PuyoException.class, () -> Parser.parse("viewschedule"));
        assertThrows(PuyoException.class, () -> Parser.parse("viewschedule 2026-02-29"));
        assertThrows(PuyoException.class, () -> Parser.parse("viewschedule 2026-09-18 1200"));
    }
}
