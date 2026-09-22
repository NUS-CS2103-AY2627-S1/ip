package steph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import steph.task.Deadline;
import steph.task.Event;
import steph.task.ToDo;

/**
 * Unit tests for {@link Parser}.
 *
 * <p>{@code Parser}'s methods are all {@code static} and pure -- they read only
 * their arguments and return a value or throw a {@link StephException} -- which
 * makes them a natural fit for JUnit: every case is a plain "given these
 * inputs, expect this result (or this exception)" check with no task list, UI,
 * or save file to set up.
 *
 * <p>The tests are grouped by the method under test. The private
 * {@code parseDateTime} helper is exercised indirectly through
 * {@link Parser#parseDeadline} and {@link Parser#parseEvent}.
 */
public class ParserTest {

    /** Task count used by the "in range" {@code parseTaskIndex} cases; any value &gt; 2 works. */
    private static final int TASK_COUNT = 3;

    // ====================================================================
    // parseCommand
    // ====================================================================

    @Test
    public void parseCommand_bareKeyword_returnsMatchingCommand() throws StephException {
        assertEquals(Command.LIST, Parser.parseCommand("list"));
    }

    @Test
    public void parseCommand_keywordFollowedByArguments_returnsMatchingCommand() throws StephException {
        // Only the first word decides the command; the rest is arguments.
        assertEquals(Command.TODO, Parser.parseCommand("todo read book"));
        assertEquals(Command.DEADLINE, Parser.parseCommand("deadline return book /by 2019-10-15"));
    }

    @Test
    public void parseCommand_unknownKeyword_exceptionThrown() {
        assertThrows(StephException.class, () -> Parser.parseCommand("sing a song"));
    }

    @Test
    public void parseCommand_wrongCase_exceptionThrown() {
        // Keyword matching is exact, so "List" is not the same as "list".
        assertThrows(StephException.class, () -> Parser.parseCommand("List"));
    }

    @Test
    public void parseCommand_emptyInput_exceptionThrown() {
        assertThrows(StephException.class, () -> Parser.parseCommand(""));
    }

    // ====================================================================
    // parseArguments
    // ====================================================================

    @Test
    public void parseArguments_keywordOnly_returnsEmptyString() {
        assertEquals("", Parser.parseArguments("list"));
    }

    @Test
    public void parseArguments_keywordAndArguments_returnsArgumentText() {
        assertEquals("read book", Parser.parseArguments("todo read book"));
    }

    @Test
    public void parseArguments_surroundingWhitespace_returnsTrimmedArgumentText() {
        assertEquals("read book", Parser.parseArguments("todo    read book  "));
    }

    @Test
    public void parseArguments_keywordThenOnlySpaces_returnsEmptyString() {
        assertEquals("", Parser.parseArguments("list    "));
    }

    // ====================================================================
    // parseTaskIndex
    //
    // Contract: a 1-based number in 1..taskCount is converted to a 0-based
    // index; anything else throws a StephException.
    // ====================================================================

    @Test
    public void parseTaskIndex_firstTaskNumber_returnsZero() throws StephException {
        assertEquals(0, Parser.parseTaskIndex("1", TASK_COUNT, "mark"));
    }

    @Test
    public void parseTaskIndex_middleTaskNumber_returnsZeroBasedIndex() throws StephException {
        assertEquals(1, Parser.parseTaskIndex("2", TASK_COUNT, "mark"));
    }

    @Test
    public void parseTaskIndex_lastTaskNumber_returnsZeroBasedIndex() throws StephException {
        assertEquals(TASK_COUNT - 1, Parser.parseTaskIndex("3", TASK_COUNT, "delete"));
    }

    @Test
    public void parseTaskIndex_numberJustPastEnd_exceptionThrown() {
        assertThrows(StephException.class, () -> Parser.parseTaskIndex("4", TASK_COUNT, "mark"));
    }

    @Test
    public void parseTaskIndex_zero_exceptionThrown() {
        // 0 - 1 == -1, which is below the valid range.
        assertThrows(StephException.class, () -> Parser.parseTaskIndex("0", TASK_COUNT, "unmark"));
    }

    @Test
    public void parseTaskIndex_negativeNumber_exceptionThrown() {
        assertThrows(StephException.class, () -> Parser.parseTaskIndex("-2", TASK_COUNT, "delete"));
    }

    @Test
    public void parseTaskIndex_emptyTaskList_exceptionThrown() {
        // With no tasks, no number can be in range.
        assertThrows(StephException.class, () -> Parser.parseTaskIndex("1", 0, "mark"));
    }

    @Test
    public void parseTaskIndex_nonNumericText_exceptionThrown() {
        assertThrows(StephException.class, () -> Parser.parseTaskIndex("abc", TASK_COUNT, "mark"));
    }

    @Test
    public void parseTaskIndex_emptyArguments_exceptionThrown() {
        assertThrows(StephException.class, () -> Parser.parseTaskIndex("", TASK_COUNT, "mark"));
    }

    @Test
    public void parseTaskIndex_blankArguments_exceptionThrown() {
        assertThrows(StephException.class, () -> Parser.parseTaskIndex("   ", TASK_COUNT, "mark"));
    }

    @Test
    public void parseTaskIndex_numberWithSurroundingSpaces_exceptionThrown() {
        // Integer.parseInt does not tolerate whitespace, so " 2 " is rejected.
        assertThrows(StephException.class, () -> Parser.parseTaskIndex(" 2 ", TASK_COUNT, "mark"));
    }

    @Test
    public void parseTaskIndex_decimalNumber_exceptionThrown() {
        assertThrows(StephException.class, () -> Parser.parseTaskIndex("1.5", TASK_COUNT, "mark"));
    }

    @Test
    public void parseTaskIndex_numberLargerThanIntMax_exceptionThrown() {
        // Overflows int, so Integer.parseInt throws and the method reports it.
        assertThrows(StephException.class, () -> Parser.parseTaskIndex("99999999999999", TASK_COUNT, "mark"));
    }

    @Test
    public void parseTaskIndex_invalidInput_messageQuotesCommandWord() {
        StephException thrown = assertThrows(StephException.class, () ->
                Parser.parseTaskIndex("nope", TASK_COUNT, "delete"));
        assertTrue(thrown.getMessage().contains("delete"),
                "error message should tell the user which command to retry");
    }

    // ====================================================================
    // parseFind
    // ====================================================================

    @Test
    public void parseFind_nonEmptyKeyword_returnsKeyword() throws StephException {
        assertEquals("book", Parser.parseFind("book"));
    }

    @Test
    public void parseFind_multiWordKeyword_returnedUnchanged() throws StephException {
        // The whole remaining text is the search phrase, spaces included.
        assertEquals("read book", Parser.parseFind("read book"));
    }

    @Test
    public void parseFind_emptyKeyword_exceptionThrown() {
        // "find" on its own would otherwise match every task.
        assertThrows(StephException.class, () -> Parser.parseFind(""));
    }

    // ====================================================================
    // parseToDo
    // ====================================================================

    @Test
    public void parseToDo_nonEmptyName_todoWithThatName() throws StephException {
        ToDo todo = Parser.parseToDo("read book");
        assertEquals("read book", todo.getName());
        assertEquals(false, todo.isDone());
    }

    @Test
    public void parseToDo_emptyName_exceptionThrown() {
        assertThrows(StephException.class, () -> Parser.parseToDo(""));
    }

    // ====================================================================
    // parseDeadline
    //
    // Expected argument shape: "<name> /by <yyyy-mm-dd> [HHmm]".
    // ====================================================================

    @Test
    public void parseDeadline_nameAndDate_deadlineBuilt() throws StephException {
        Deadline deadline = Parser.parseDeadline("return book /by 2019-10-15");
        assertEquals("return book", deadline.getName());
        // Date-only input displays without a time.
        assertTrue(deadline.toString().contains("Oct 15 2019"), deadline.toString());
    }

    @Test
    public void parseDeadline_nameDateAndTime_timeKept() throws StephException {
        Deadline deadline = Parser.parseDeadline("return book /by 2019-10-15 1800");
        // 18:00 shown in 12-hour form; case of the AM/PM marker varies by JDK locale data.
        assertTrue(deadline.toString().toLowerCase().contains("6:00pm"), deadline.toString());
    }

    @Test
    public void parseDeadline_missingByMarker_exceptionThrown() {
        assertThrows(StephException.class, () -> Parser.parseDeadline("return book 2019-10-15"));
    }

    @Test
    public void parseDeadline_emptyName_exceptionThrown() {
        assertThrows(StephException.class, () -> Parser.parseDeadline("/by 2019-10-15"));
    }

    @Test
    public void parseDeadline_emptyDate_exceptionThrown() {
        assertThrows(StephException.class, () -> Parser.parseDeadline("return book /by"));
    }

    @Test
    public void parseDeadline_unreadableDate_exceptionThrown() {
        assertThrows(StephException.class, () -> Parser.parseDeadline("return book /by tomorrow"));
    }

    // ====================================================================
    // parseEvent
    //
    // Expected argument shape:
    // "<name> /from <yyyy-mm-dd> [HHmm] /to <yyyy-mm-dd> [HHmm]".
    // ====================================================================

    @Test
    public void parseEvent_nameFromAndTo_eventBuilt() throws StephException {
        ParsedEvent parsed = Parser.parseEvent("project meeting /from 2019-10-15 /to 2019-10-16");
        Event event = parsed.event();
        assertEquals("project meeting", event.getName());
        String shown = event.toString();
        assertTrue(shown.contains("Oct 15 2019"), shown);
        assertTrue(shown.contains("Oct 16 2019"), shown);
        assertFalse(parsed.isForce());
    }

    @Test
    public void parseEvent_withoutForceFlag_isForceFalse() throws StephException {
        ParsedEvent parsed = Parser.parseEvent("project meeting /from 2019-10-15 /to 2019-10-16");
        assertFalse(parsed.isForce());
    }

    @Test
    public void parseEvent_withTrailingForceFlag_isForceTrueAndForceStrippedFromDates() throws StephException {
        ParsedEvent parsed = Parser.parseEvent("project meeting /from 2019-10-15 /to 2019-10-16 /force");
        assertTrue(parsed.isForce());
        // "/force" must not have leaked into the "to" date text.
        String shown = parsed.event().toString();
        assertTrue(shown.contains("Oct 16 2019"), shown);
    }

    @Test
    public void parseEvent_missingFromMarker_exceptionThrown() {
        assertThrows(StephException.class, () -> Parser.parseEvent("project meeting /to 2019-10-16"));
    }

    @Test
    public void parseEvent_missingToMarker_exceptionThrown() {
        assertThrows(StephException.class, () -> Parser.parseEvent("project meeting /from 2019-10-15"));
    }

    @Test
    public void parseEvent_toMarkerBeforeFromMarker_exceptionThrown() {
        // The markers must appear in order, so "/to ... /from ..." is rejected.
        assertThrows(StephException.class, () -> Parser.parseEvent("project meeting /to 2019-10-16 /from 2019-10-15"));
    }

    @Test
    public void parseEvent_emptyName_exceptionThrown() {
        assertThrows(StephException.class, () -> Parser.parseEvent("/from 2019-10-15 /to 2019-10-16"));
    }

    @Test
    public void parseEvent_emptyFrom_exceptionThrown() {
        assertThrows(StephException.class, () -> Parser.parseEvent("project meeting /from /to 2019-10-16"));
    }

    @Test
    public void parseEvent_emptyTo_exceptionThrown() {
        assertThrows(StephException.class, () -> Parser.parseEvent("project meeting /from 2019-10-15 /to"));
    }

    @Test
    public void parseEvent_unreadableDate_exceptionThrown() {
        assertThrows(StephException.class, () -> Parser.parseEvent("project meeting /from someday /to 2019-10-16"));
    }

    // ====================================================================
    // parseEvent -- start must come before end
    //
    // Contract: "/from" must be strictly before "/to", except that a
    // same-day date-only "/from"/"/to" pair (both default to midnight) is a
    // valid whole-day event, since a date-only end means "through the end
    // of that day" (see Event#clashesWith and DateTimes#expandIfMidnight).
    // ====================================================================

    @Test
    public void parseEvent_startAfterEnd_exceptionThrown() {
        assertThrows(StephException.class, () ->
                Parser.parseEvent("exam /from 2019-10-20 /to 2019-10-15"));
    }

    @Test
    public void parseEvent_startEqualsEndWithTime_exceptionThrown() {
        assertThrows(StephException.class, () ->
                Parser.parseEvent("exam /from 2019-10-15 1400 /to 2019-10-15 1400"));
    }

    @Test
    public void parseEvent_startOneMinuteAfterEnd_exceptionThrown() {
        assertThrows(StephException.class, () ->
                Parser.parseEvent("exam /from 2019-10-15 1401 /to 2019-10-15 1400"));
    }

    @Test
    public void parseEvent_startBeforeEndByOneMinute_eventBuilt() throws StephException {
        ParsedEvent parsed = Parser.parseEvent("exam /from 2019-10-15 1359 /to 2019-10-15 1400");
        assertEquals("exam", parsed.event().getName());
    }

    @Test
    public void parseEvent_sameDayDateOnlyStartAndEnd_wholeDayEventBuilt() throws StephException {
        // "/from 2019-10-15 /to 2019-10-15" both default to midnight, but a
        // date-only end means "through the end of that day," so this is a
        // valid whole-day event rather than a backwards or empty range.
        ParsedEvent parsed = Parser.parseEvent("campout /from 2019-10-15 /to 2019-10-15");
        assertEquals("campout", parsed.event().getName());
    }

    @Test
    public void parseEvent_startAfterEnd_messageNamesBothValues() {
        StephException thrown = assertThrows(StephException.class, () ->
                Parser.parseEvent("exam /from 2019-10-20 /to 2019-10-15"));
        assertTrue(thrown.getMessage().contains("Oct 20 2019"), thrown.getMessage());
        assertTrue(thrown.getMessage().contains("Oct 15 2019"), thrown.getMessage());
    }

    @Test
    public void parseEvent_forceFlagButNoMarkers_exceptionThrown() {
        assertThrows(StephException.class, () -> Parser.parseEvent("camp /force"));
    }

    @Test
    public void parseCommand_leadingSpace_exceptionThrown() {
        // The caller (Steph) trims the line first; the parser itself sees an empty first word.
        assertThrows(StephException.class, () -> Parser.parseCommand(" list"));
    }
}
