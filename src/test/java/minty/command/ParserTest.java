package minty.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import minty.exception.MintyException;
import minty.task.Deadline;
import minty.task.Event;
import minty.task.Todo;

/**
 * Tests command parsing behavior.
 */
public class ParserTest {

    @Test
    public void parseDeadline_repeatedOrPartialParameter_rejectsMalformedInput() {
        MintyException repeated = assertThrows(MintyException.class, () ->
                Parser.parseDeadline("deadline work /by 2026-09-20 /by 2026-09-21"));
        assertEquals("Whoops! Use /by only once.", repeated.getMessage());
        assertThrows(MintyException.class, () -> Parser.parseDeadline("deadline work /by2026-09-20"));
        assertThrows(MintyException.class, () -> Parser.parseDeadline("deadline work /bypass 2026-09-20"));
    }

    @Test
    public void parseEvent_repeatedParameters_rejectsBothMarkers() {
        assertThrows(MintyException.class, () -> Parser.parseEvent(
                "event work /from 2026-09-20 /from 2026-09-21 /to 2026-09-22"));
        assertThrows(MintyException.class, () -> Parser.parseEvent(
                "event work /from 2026-09-20 /to 2026-09-21 /to 2026-09-22"));
    }

    @Test
    public void parseDeadline_invalidCalendarDates_rejectsAndAcceptsLeapDay() throws MintyException {
        for (String date : new String[] {"2026-02-30", "2026-02-29", "2026-13-01", "+10000-01-01"}) {
            assertThrows(MintyException.class, () -> Parser.parseDeadline("deadline work /by " + date));
        }
        assertEquals("D | 0 | work | 2028-02-29",
                Parser.parseDeadline("deadline work /by 2028-02-29").toDataString());
    }

    @Test
    public void parseTaskIndex_validTaskNumber_returnsZeroBasedIndex()
            throws MintyException {
        int actualIndex =
                Parser.parseTaskIndex("mark 2", CommandType.MARK, 3);

        assertEquals(1, actualIndex);
    }

    @Test
    public void parseTaskIndex_missingTaskNumber_throwsMintyException() {
        assertThrows(MintyException.class, () ->
                Parser.parseTaskIndex("mark", CommandType.MARK, 3));
    }

    @Test
    public void parseTaskIndex_nonNumericTaskNumber_throwsMintyException() {
        assertThrows(MintyException.class, () ->
                Parser.parseTaskIndex("mark two", CommandType.MARK, 3));
    }

    @Test
    public void parseTaskIndex_zeroTaskNumber_throwsMintyException() {
        assertThrows(MintyException.class, () ->
                Parser.parseTaskIndex("mark 0", CommandType.MARK, 3));
    }

    @Test
    public void parseTaskIndex_taskNumberAboveTaskCount_throwsMintyException() {
        assertThrows(MintyException.class, () ->
                Parser.parseTaskIndex("mark 4", CommandType.MARK, 3));
    }

    @Test
    public void parseTodo_validDescription_returnsTodo() throws MintyException {
        Todo todo = Parser.parseTodo("todo read book");

        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void parseTodo_missingDescription_throwsMintyException() {
        assertThrows(MintyException.class, () -> Parser.parseTodo("todo"));
    }

    @Test
    public void parseDeadline_validDetails_returnsDeadline() throws MintyException {
        Deadline deadline = Parser.parseDeadline("deadline submit report /by 2026-09-10");

        assertEquals("D | 0 | submit report | 2026-09-10", deadline.toDataString());
    }

    @Test
    public void parseDeadline_missingBySeparator_throwsMintyException() {
        assertThrows(MintyException.class, () ->
                Parser.parseDeadline("deadline submit report"));
    }

    @Test
    public void parseDeadline_missingDescription_throwsMintyException() {
        assertThrows(MintyException.class, () ->
                Parser.parseDeadline("deadline /by 2026-09-10"));
    }

    @Test
    public void parseDeadline_missingDate_throwsMintyException() {
        assertThrows(MintyException.class, () ->
                Parser.parseDeadline("deadline submit report /by"));
    }

    @Test
    public void parseDeadline_invalidDate_throwsMintyException() {
        assertThrows(MintyException.class, () ->
                Parser.parseDeadline("deadline submit report /by 10-09-2026"));
    }

    @Test
    public void parseEvent_validDetails_returnsEvent() throws MintyException {
        Event event = Parser.parseEvent(
                "event conference /from 2026-09-10 /to 2026-09-12");

        assertEquals("E | 0 | conference | 2026-09-10 | 2026-09-12",
                event.toDataString());
    }

    @Test
    public void parseEvent_missingFromSeparator_throwsMintyException() {
        assertThrows(MintyException.class, () ->
                Parser.parseEvent("event conference /to 2026-09-12"));
    }

    @Test
    public void parseEvent_missingToSeparator_throwsMintyException() {
        assertThrows(MintyException.class, () ->
                Parser.parseEvent("event conference /from 2026-09-10"));
    }

    @Test
    public void parseEvent_toBeforeFromSeparator_throwsMintyException() {
        assertThrows(MintyException.class, () -> Parser.parseEvent(
                "event conference /to 2026-09-12 /from 2026-09-10"));
    }

    @Test
    public void parseEvent_endBeforeStart_throwsMintyException() {
        assertThrows(MintyException.class, () -> Parser.parseEvent(
                "event conference /from 2026-09-12 /to 2026-09-10"));
    }

    @Test
    public void parseEvent_sameStartAndEnd_returnsEvent() throws MintyException {
        Event event = Parser.parseEvent(
                "event conference /from 2026-09-10 /to 2026-09-10");

        assertEquals("E | 0 | conference | 2026-09-10 | 2026-09-10",
                event.toDataString());
    }

    @Test
    public void parseOnDate_validDate_returnsDate() throws MintyException {
        assertEquals(LocalDate.of(2026, 9, 10), Parser.parseOnDate("on 2026-09-10"));
    }

    @Test
    public void parseOnDate_missingDate_throwsMintyException() {
        assertThrows(MintyException.class, () -> Parser.parseOnDate("on"));
    }

    @Test
    public void parseOnDate_invalidDate_throwsMintyException() {
        assertThrows(MintyException.class, () -> Parser.parseOnDate("on 10-09-2026"));
    }

    @Test
    public void parseFindKeyword_validKeyword_returnsKeyword() throws MintyException {
        assertEquals("read book", Parser.parseFindKeyword("find read book"));
    }

    @Test
    public void parseFindKeyword_missingKeyword_throwsMintyException() {
        assertThrows(MintyException.class, () -> Parser.parseFindKeyword("find"));
    }

    @Test
    public void parseCommandType_argumentPolicies_returnsMatchingCommandTypes() {
        assertEquals(CommandType.LIST, Parser.parseCommandType("list"));
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("list all"));
        assertEquals(CommandType.FIND, Parser.parseCommandType("find book"));
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("findbook"));
    }

    @Test
    public void parse_recognizedAndUnknownCommands_returnsMatchingCommandTypes() {
        assertInstanceOf(RemindersCommand.class, Parser.parse("reminders"));
        assertInstanceOf(RemindersCommand.class, Parser.parse("remind"));
        assertInstanceOf(UnknownCommand.class, Parser.parse("reminders 14"));
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(TodoCommand.class, Parser.parse("todo read book"));
        assertInstanceOf(DeadlineCommand.class,
                Parser.parse("deadline submit /by 2026-09-10"));
        assertInstanceOf(EventCommand.class,
                Parser.parse("event conference /from 2026-09-10 /to 2026-09-12"));
        assertInstanceOf(UnknownCommand.class, Parser.parse("dance"));
    }
}
