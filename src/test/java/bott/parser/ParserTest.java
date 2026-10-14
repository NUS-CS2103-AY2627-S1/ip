package bott.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import bott.BottException;
import bott.task.Deadline;
import bott.task.Event;
import bott.task.FixedDurationTask;
import bott.task.Todo;

/**
 * Tests every public method of {@link Parser}. {@code parseDate} is private and has no test of
 * its own; its behavior (valid/invalid "yyyy-MM-dd" input) is exercised through the
 * {@code parseDeadline}/{@code parseEvent} date-related test cases below.
 */
public class ParserTest {

    // ---- getCommandWord ----

    @Test
    public void getCommandWord_commandWithArgs_returnsFirstWord() {
        assertEquals("todo", Parser.getCommandWord("todo read book"));
    }

    @Test
    public void getCommandWord_commandWithNoArgs_returnsWholeInput() {
        assertEquals("bye", Parser.getCommandWord("bye"));
    }

    @Test
    public void getCommandWord_emptyInput_returnsEmptyString() {
        assertEquals("", Parser.getCommandWord(""));
    }

    // ---- getArguments ----

    @Test
    public void getArguments_commandWithArgs_returnsTextAfterCommand() {
        assertEquals("read book", Parser.getArguments("todo read book"));
    }

    @Test
    public void getArguments_commandWithNoArgs_returnsEmptyString() {
        assertEquals("", Parser.getArguments("list"));
    }

    @Test
    public void getArguments_emptyInput_returnsEmptyString() {
        assertEquals("", Parser.getArguments(""));
    }

    // ---- parseTodo ----

    @Test
    public void parseTodo_validDescription_returnsTodoWithDescription() throws BottException {
        Todo todo = Parser.parseTodo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void parseTodo_emptyArgs_exceptionThrown() {
        BottException exception = assertThrows(BottException.class, () -> Parser.parseTodo(""));
        assertEquals("A todo needs a description. Try: todo <description>", exception.getMessage());
    }

    @Test
    public void parseTodo_whitespaceOnlyArgs_exceptionThrown() {
        assertThrows(BottException.class, () -> Parser.parseTodo("   "));
    }

    // ---- parseDeadline ----

    @Test
    public void parseDeadline_validDescriptionAndDate_returnsDeadline() throws BottException {
        Deadline deadline = Parser.parseDeadline("return book /by 2019-10-15");
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void parseDeadline_emptyArgs_exceptionThrown() {
        BottException exception = assertThrows(BottException.class, () -> Parser.parseDeadline(""));
        assertEquals("A deadline needs a description. Try: deadline <description> /by <yyyy-MM-dd>",
                exception.getMessage());
    }

    @Test
    public void parseDeadline_missingByMarker_exceptionThrown() {
        BottException exception = assertThrows(BottException.class,
                () -> Parser.parseDeadline("return book"));
        assertEquals("A deadline needs a \"/by\" date. Try: deadline <description> /by <yyyy-MM-dd>",
                exception.getMessage());
    }

    @Test
    public void parseDeadline_emptyDescription_exceptionThrown() {
        BottException exception = assertThrows(BottException.class,
                () -> Parser.parseDeadline("/by 2019-10-15"));
        assertEquals("A deadline needs a description. Try: deadline <description> /by <yyyy-MM-dd>",
                exception.getMessage());
    }

    @Test
    public void parseDeadline_emptyByDate_exceptionThrown() {
        BottException exception = assertThrows(BottException.class,
                () -> Parser.parseDeadline("return book /by"));
        assertEquals("The \"by\" date of a deadline cannot be empty. "
                + "Try: deadline <description> /by <yyyy-MM-dd>", exception.getMessage());
    }

    @Test
    public void parseDeadline_wrongDateFormat_exceptionThrown() {
        BottException exception = assertThrows(BottException.class,
                () -> Parser.parseDeadline("return book /by 15/10/2019"));
        assertEquals("The \"by\" date must be in yyyy-MM-dd format (e.g. 2019-10-15). "
                + "\"15/10/2019\" is not a valid date.", exception.getMessage());
    }

    @Test
    public void parseDeadline_nonexistentCalendarDate_exceptionThrown() {
        assertThrows(BottException.class, () -> Parser.parseDeadline("return book /by 2021-02-29"));
    }

    // ---- parseEvent ----

    @Test
    public void parseEvent_validDescriptionAndDates_returnsEvent() throws BottException {
        Event event = Parser.parseEvent("trip /from 2019-08-04 /to 2019-08-05");
        assertEquals("[E][ ] trip (from: Aug 04 2019 to: Aug 05 2019)", event.toString());
    }

    @Test
    public void parseEvent_emptyArgs_exceptionThrown() {
        BottException exception = assertThrows(BottException.class, () -> Parser.parseEvent(""));
        assertEquals("An event needs a description. Try: event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>",
                exception.getMessage());
    }

    @Test
    public void parseEvent_missingFromMarker_exceptionThrown() {
        BottException exception = assertThrows(BottException.class, () -> Parser.parseEvent("trip"));
        assertEquals("An event needs a \"/from\" start date. "
                + "Try: event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>", exception.getMessage());
    }

    @Test
    public void parseEvent_missingToMarker_exceptionThrown() {
        BottException exception = assertThrows(BottException.class,
                () -> Parser.parseEvent("trip /from 2019-08-04"));
        assertEquals("An event needs a \"/to\" end date after its \"/from\" start date. "
                + "Try: event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>", exception.getMessage());
    }

    @Test
    public void parseEvent_emptyDescription_exceptionThrown() {
        BottException exception = assertThrows(BottException.class,
                () -> Parser.parseEvent("/from 2019-08-04 /to 2019-08-05"));
        assertEquals("An event needs a description. Try: event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>",
                exception.getMessage());
    }

    @Test
    public void parseEvent_emptyFromDate_exceptionThrown() {
        BottException exception = assertThrows(BottException.class,
                () -> Parser.parseEvent("trip /from /to 2019-08-05"));
        assertEquals("The \"from\" start date of an event cannot be empty. "
                + "Try: event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>", exception.getMessage());
    }

    @Test
    public void parseEvent_emptyToDate_exceptionThrown() {
        BottException exception = assertThrows(BottException.class,
                () -> Parser.parseEvent("trip /from 2019-08-04 /to"));
        assertEquals("The \"to\" end date of an event cannot be empty. "
                + "Try: event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>", exception.getMessage());
    }

    @Test
    public void parseEvent_wrongFromDateFormat_exceptionThrown() {
        BottException exception = assertThrows(BottException.class,
                () -> Parser.parseEvent("trip /from bad-date /to 2019-08-05"));
        assertEquals("The \"from\" date must be in yyyy-MM-dd format (e.g. 2019-10-15). "
                + "\"bad-date\" is not a valid date.", exception.getMessage());
    }

    @Test
    public void parseEvent_wrongToDateFormat_exceptionThrown() {
        BottException exception = assertThrows(BottException.class,
                () -> Parser.parseEvent("trip /from 2019-08-04 /to bad-date"));
        assertEquals("The \"to\" date must be in yyyy-MM-dd format (e.g. 2019-10-15). "
                + "\"bad-date\" is not a valid date.", exception.getMessage());
    }

    // ---- parseFixedDuration ----

    @Test
    public void parseFixedDuration_hoursOnly_returnsTask() throws BottException {
        FixedDurationTask task = Parser.parseFixedDuration("read sales report /for 2h");
        assertEquals("[F][ ] read sales report (for: 2h)", task.toString());
    }

    @Test
    public void parseFixedDuration_minutesOnly_returnsTask() throws BottException {
        FixedDurationTask task = Parser.parseFixedDuration("call bank /for 45m");
        assertEquals("[F][ ] call bank (for: 45m)", task.toString());
    }

    @Test
    public void parseFixedDuration_combinedNoSpace_returnsTask() throws BottException {
        FixedDurationTask task = Parser.parseFixedDuration("deep work /for 1h30m");
        assertEquals("[F][ ] deep work (for: 1h 30m)", task.toString());
    }

    @Test
    public void parseFixedDuration_combinedWithSpace_returnsTask() throws BottException {
        FixedDurationTask task = Parser.parseFixedDuration("deep work /for 1h 30m");
        assertEquals("[F][ ] deep work (for: 1h 30m)", task.toString());
    }

    @Test
    public void parseFixedDuration_emptyArgs_exceptionThrown() {
        BottException exception = assertThrows(BottException.class,
                () -> Parser.parseFixedDuration(""));
        assertEquals("A fixed-duration task needs a description. Try: duration <description> /for <2h30m>",
                exception.getMessage());
    }

    @Test
    public void parseFixedDuration_missingForMarker_exceptionThrown() {
        BottException exception = assertThrows(BottException.class,
                () -> Parser.parseFixedDuration("read sales report"));
        assertEquals("A fixed-duration task needs a \"/for\" duration. "
                + "Try: duration <description> /for <2h30m>", exception.getMessage());
    }

    @Test
    public void parseFixedDuration_emptyDescription_exceptionThrown() {
        BottException exception = assertThrows(BottException.class,
                () -> Parser.parseFixedDuration("/for 2h"));
        assertEquals("A fixed-duration task needs a description. Try: duration <description> /for <2h30m>",
                exception.getMessage());
    }

    @Test
    public void parseFixedDuration_emptyDuration_exceptionThrown() {
        BottException exception = assertThrows(BottException.class,
                () -> Parser.parseFixedDuration("read sales report /for"));
        assertEquals("The \"for\" duration of a fixed-duration task cannot be empty. "
                + "Try: duration <description> /for <2h30m>", exception.getMessage());
    }

    @Test
    public void parseFixedDuration_noUnit_exceptionThrown() {
        BottException exception = assertThrows(BottException.class,
                () -> Parser.parseFixedDuration("read sales report /for 2"));
        assertEquals("\"2\" is not a valid duration. Use a number with a unit, e.g. 2h, 30m, or 1h30m.",
                exception.getMessage());
    }

    @Test
    public void parseFixedDuration_nonNumericDuration_exceptionThrown() {
        assertThrows(BottException.class,
                () -> Parser.parseFixedDuration("read sales report /for a while"));
    }

    @Test
    public void parseFixedDuration_zeroDuration_exceptionThrown() {
        BottException exception = assertThrows(BottException.class,
                () -> Parser.parseFixedDuration("read sales report /for 0m"));
        assertEquals("A fixed-duration task must need more than 0 minutes. "
                + "Try: duration <description> /for <2h30m>", exception.getMessage());
    }

    @Test
    public void parseFixedDuration_combinedMinutesAbove59_exceptionThrown() {
        BottException exception = assertThrows(BottException.class,
                () -> Parser.parseFixedDuration("read sales report /for 1h90m"));
        assertEquals("In a combined duration like 1h30m, the minutes must be 0-59. "
                + "Try: duration <description> /for <2h30m>", exception.getMessage());
    }

    // ---- parseFind ----

    @Test
    public void parseFind_validKeyword_returnsTrimmedKeyword() throws BottException {
        assertEquals("book", Parser.parseFind("  book  "));
    }

    @Test
    public void parseFind_emptyArgs_exceptionThrown() {
        BottException exception = assertThrows(BottException.class, () -> Parser.parseFind(""));
        assertEquals("Please specify a keyword to search for. Try: find <keyword>", exception.getMessage());
    }

    @Test
    public void parseFind_whitespaceOnlyArgs_exceptionThrown() {
        assertThrows(BottException.class, () -> Parser.parseFind("   "));
    }

    // ---- parseTaskNumber ----

    @Test
    public void parseTaskNumber_validNumberWithinRange_returnsTaskNumber() throws BottException {
        assertEquals(2, Parser.parseTaskNumber("mark", "2", 3));
    }

    @Test
    public void parseTaskNumber_lowerBoundaryNumber_returnsOne() throws BottException {
        assertEquals(1, Parser.parseTaskNumber("mark", "1", 1));
    }

    @Test
    public void parseTaskNumber_upperBoundaryNumber_returnsTaskCount() throws BottException {
        assertEquals(5, Parser.parseTaskNumber("mark", "5", 5));
    }

    @Test
    public void parseTaskNumber_surroundingWhitespace_trimmedAndParsed() throws BottException {
        assertEquals(2, Parser.parseTaskNumber("mark", "  2  ", 3));
    }

    @Test
    public void parseTaskNumber_emptyArgs_exceptionThrown() {
        BottException exception = assertThrows(BottException.class,
                () -> Parser.parseTaskNumber("delete", "", 3));
        assertEquals("Please specify a task number. Try: delete <task number>", exception.getMessage());
    }

    @Test
    public void parseTaskNumber_whitespaceOnlyArgs_exceptionThrown() {
        assertThrows(BottException.class, () -> Parser.parseTaskNumber("delete", "   ", 3));
    }

    @Test
    public void parseTaskNumber_nonNumericArgs_exceptionThrown() {
        BottException exception = assertThrows(BottException.class,
                () -> Parser.parseTaskNumber("unmark", "abc", 3));
        assertEquals("\"abc\" is not a valid task number. Try: unmark <task number>", exception.getMessage());
    }

    @Test
    public void parseTaskNumber_decimalArgs_exceptionThrown() {
        assertThrows(BottException.class, () -> Parser.parseTaskNumber("mark", "2.5", 3));
    }

    @Test
    public void parseTaskNumber_zero_exceptionThrown() {
        BottException exception = assertThrows(BottException.class,
                () -> Parser.parseTaskNumber("mark", "0", 3));
        assertEquals("There is no task number 0 in your list. You currently have 3 task(s).",
                exception.getMessage());
    }

    @Test
    public void parseTaskNumber_negativeNumber_exceptionThrown() {
        assertThrows(BottException.class, () -> Parser.parseTaskNumber("mark", "-1", 3));
    }

    @Test
    public void parseTaskNumber_numberAboveTaskCount_exceptionThrown() {
        BottException exception = assertThrows(BottException.class,
                () -> Parser.parseTaskNumber("mark", "4", 3));
        assertEquals("There is no task number 4 in your list. You currently have 3 task(s).",
                exception.getMessage());
    }

    @Test
    public void parseTaskNumber_emptyTaskList_exceptionThrown() {
        assertThrows(BottException.class, () -> Parser.parseTaskNumber("mark", "1", 0));
    }
}
