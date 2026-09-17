package wobble.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import wobble.exceptions.WobbleException;
import wobble.tasks.Deadline;
import wobble.tasks.Event;
import wobble.tasks.Task;
import wobble.tasks.Todo;

/** Tests the command parser's core task-creation and validation logic. */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseTask_todoCommand_createsTodo() throws WobbleException {
        Task task = parser.parseTask("todo read book");

        assertInstanceOf(Todo.class, task);
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    void parseTask_mixedCaseAndExtraWhitespace_normalizesCommand() throws WobbleException {
        Task task = parser.parseTask("  TODO   read   book  ");

        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    void parseTask_deadlineCommand_createsDeadlineWithDate() throws WobbleException {
        Task task = parser.parseTask("deadline submit report /by 2026-08-27 1800");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals(LocalDateTime.of(2026, 8, 27, 18, 0), deadline.getBy());
    }

    @Test
    void parseTask_dateOnlyDeadline_isDueAtEndOfDay() throws WobbleException {
        Task task = parser.parseTask("deadline submit report /by 2099-08-27");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals(LocalDateTime.of(2099, 8, 27, 23, 59, 59, 999_999_999), deadline.getBy());
        assertEquals("[D][ ] submit report (by: Aug 27 2099)", deadline.toString());
    }

    @Test
    void parseTask_eventCommand_createsEventWithDateRange() throws WobbleException {
        Task task = parser.parseTask("event meeting /from 2026/08/27 /to 2026.08.28");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals(LocalDateTime.of(2026, 8, 27, 0, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2026, 8, 28, 0, 0), event.getTo());
    }

    @Test
    void parseTask_emptyTodoDescription_throwsWobbleException() {
        assertThrows(WobbleException.class, () -> parser.parseTask("todo"));
    }

    @Test
    void parseTask_nullOrBlankCommand_throwsWobbleException() {
        assertThrows(WobbleException.class, () -> parser.parseTask(null));
        assertThrows(WobbleException.class, () -> parser.parseTask("   "));
    }

    @Test
    void parseTask_unknownCommand_throwsWobbleException() {
        assertThrows(WobbleException.class, () -> parser.parseTask("blah"));
    }

    @Test
    void parseTask_malformedDeadline_throwsWobbleException() {
        WobbleException exception = assertThrows(WobbleException.class,
                () -> parser.parseTask("deadline submit report"));

        assertEquals("a deadline needs a description and a /by date.", exception.getMessage());
    }

    @Test
    void parseTask_invalidDeadlineDate_throwsWobbleException() {
        assertThrows(WobbleException.class,
                () -> parser.parseTask("deadline submit report /by not-a-date"));
    }

    @Test
    void parseTask_invalidDeadlineTime_showsUsefulDiagnostic() {
        WobbleException exception = assertThrows(WobbleException.class,
                () -> parser.parseTask("deadline submit report /by 2026-08-27 24:00"));

        assertEquals("the deadline date or time is invalid. Check that the date exists and the time is between "
                        + "00:00 and 23:59. Type help for formats.",
                exception.getMessage());
        assertFalse(exception.shouldSuggestCommand());
    }

    @Test
    void parseTask_repeatedDeadlineParameter_throwsWobbleException() {
        WobbleException exception = assertThrows(WobbleException.class,
                () -> parser.parseTask("deadline submit report /by 2026-08-27 /by 2026-08-28"));

        assertEquals("a deadline accepts only one /by date.", exception.getMessage());
    }

    @Test
    void parseTask_missingDeadlineDate_throwsWobbleException() {
        assertThrows(WobbleException.class,
                () -> parser.parseTask("deadline submit report /by"));
    }

    @Test
    void parseTask_malformedDeadlineSeparator_throwsWobbleException() {
        WobbleException exception = assertThrows(WobbleException.class,
                () -> parser.parseTask("deadline submit report /by2026-08-27"));

        assertEquals("a deadline needs a description and a /by date.", exception.getMessage());
    }

    @Test
    void parseTask_emptyDeadlineDescription_throwsWobbleException() {
        assertThrows(WobbleException.class,
                () -> parser.parseTask("deadline /by 2026-08-27"));
    }

    @Test
    void parseTask_repeatedEventParameter_throwsWobbleException() {
        WobbleException exception = assertThrows(WobbleException.class,
                () -> parser.parseTask("event meeting /from 2026-08-27 /to 2026-08-28 /to 2026-08-29"));

        assertEquals("an event accepts only one /from time and one /to time.", exception.getMessage());
    }

    @Test
    void parseTask_missingEventParameter_throwsWobbleException() {
        assertThrows(WobbleException.class,
                () -> parser.parseTask("event meeting /from 2026-08-27"));
        assertThrows(WobbleException.class,
                () -> parser.parseTask("event meeting /to 2026-08-28"));
    }

    @Test
    void parseTask_malformedEventSeparator_throwsWobbleException() {
        WobbleException exception = assertThrows(WobbleException.class,
                () -> parser.parseTask("event meeting /from2026-08-27 /to 2026-08-28"));

        assertEquals("an event needs a description, a /from time, and a /to time.", exception.getMessage());
    }

    @Test
    void parseTask_missingEventDate_throwsWobbleException() {
        assertThrows(WobbleException.class,
                () -> parser.parseTask("event meeting /from /to 2026-08-28"));
        assertThrows(WobbleException.class,
                () -> parser.parseTask("event meeting /from 2026-08-27 /to"));
    }

    @Test
    void parseTask_nonIncreasingEventRange_throwsWobbleException() {
        WobbleException exception = assertThrows(WobbleException.class,
                () -> parser.parseTask("event meeting /from 2026-08-28 /to 2026-08-27"));

        assertEquals("an event must end after it starts.", exception.getMessage());
        assertFalse(exception.shouldSuggestCommand());
    }

    @Test
    void parseTask_invalidEventTime_showsUsefulDiagnostic() {
        WobbleException exception = assertThrows(WobbleException.class,
                () -> parser.parseTask("event meeting /from 2026-08-27 2200 /to 2026-08-27 24:00"));

        assertEquals("the event date or time is invalid. Check that the date exists and the time is between "
                        + "00:00 and 23:59. Type help for formats.",
                exception.getMessage());
        assertFalse(exception.shouldSuggestCommand());
    }

    @Test
    void parseTask_emptyEventDescription_throwsWobbleException() {
        assertThrows(WobbleException.class,
                () -> parser.parseTask("event /from 2026-08-27 /to 2026-08-28"));
    }

    @Test
    void parseTask_controlCharacterInDescription_throwsWobbleException() {
        assertThrows(WobbleException.class,
                () -> parser.parseTask("todo read\u0000book"));
    }

    @Test
    void parseDueDate_validDate_returnsDate() throws WobbleException {
        assertEquals(LocalDateTime.of(2026, 8, 27, 0, 0).toLocalDate(),
                parser.parseDueDate("due on 2026/08/27"));
    }

    @Test
    void parseDueDate_missingDate_throwsWobbleException() {
        assertThrows(WobbleException.class, () -> parser.parseDueDate("due on"));
    }

    @Test
    void parseDueDate_invalidDate_suppressesCommandSuggestion() {
        WobbleException exception = assertThrows(WobbleException.class,
                () -> parser.parseDueDate("due on 2027-02-31"));

        assertFalse(exception.shouldSuggestCommand());
    }

    @Test
    void parseDueDate_mixedCaseAndExtraWhitespace_returnsDate() throws WobbleException {
        assertEquals(LocalDateTime.of(2026, 8, 27, 0, 0).toLocalDate(),
                parser.parseDueDate("  DUE   ON   2026/08/27  "));
    }

    @Test
    void parseDueDate_wrongCommand_throwsWobbleException() {
        WobbleException exception = assertThrows(WobbleException.class,
                () -> parser.parseDueDate("deadline 2026-08-27"));

        assertEquals("the due-on command must use the format: due on <date>.", exception.getMessage());
    }

    @Test
    void parseDueDate_dottedDate_returnsDate() throws WobbleException {
        assertEquals(LocalDateTime.of(2026, 8, 27, 0, 0).toLocalDate(),
                parser.parseDueDate("due on 2026.08.27"));
    }

    @Test
    void parseReminderDays_missingRange_usesSevenDays() throws WobbleException {
        assertEquals(7, parser.parseReminderDays("reminders"));
    }

    @Test
    void parseReminderDays_invalidRange_throwsWobbleException() {
        assertThrows(WobbleException.class, () -> parser.parseReminderDays("reminders tomorrow"));
        assertThrows(WobbleException.class, () -> parser.parseReminderDays("reminders -1"));
    }

    @Test
    void parseReminderDays_validRanges_returnsRequestedDays() throws WobbleException {
        assertEquals(0, parser.parseReminderDays("reminders 0"));
        assertEquals(30, parser.parseReminderDays("reminders 30"));
        assertEquals(36500, parser.parseReminderDays("reminders 36500"));
    }

    @Test
    void parseReminderDays_wrongCommand_throwsWobbleException() {
        WobbleException exception = assertThrows(WobbleException.class,
                () -> parser.parseReminderDays("reminder 7"));

        assertEquals("the reminders command must use the format: reminders [days].", exception.getMessage());
    }

    @Test
    void parseReminderDays_multipleArguments_throwsWobbleException() {
        assertThrows(WobbleException.class,
                () -> parser.parseReminderDays("reminders 7 extra"));
    }

    @Test
    void parseReminderDays_oversizedRange_throwsWobbleException() {
        assertThrows(WobbleException.class,
                () -> parser.parseReminderDays("reminders " + Integer.MAX_VALUE));
    }

    @Test
    void parseReminderDays_numberTooLarge_throwsWobbleException() {
        assertThrows(WobbleException.class,
                () -> parser.parseReminderDays("reminders 999999999999999999999999"));
    }

    @Test
    void normalizeCommand_nullAndWhitespace_returnsNormalizedText() {
        assertEquals("", Parser.normalizeCommand(null));
        assertEquals("todo read book", Parser.normalizeCommand("  todo\tread  book  "));
    }

    @Test
    void parseTaskNumber_validValue_returnsInteger() throws WobbleException {
        assertEquals(12, Parser.parseTaskNumber("12"));
    }

    @Test
    void parseTaskNumber_nullOrOverflow_throwsWobbleException() {
        assertThrows(WobbleException.class, () -> Parser.parseTaskNumber(null));
        assertThrows(WobbleException.class, () -> Parser.parseTaskNumber("999999999999999999"));
    }

    @Test
    void parseTaskNumber_specialCharacters_throwsWobbleException() {
        assertThrows(WobbleException.class, () -> Parser.parseTaskNumber("+1"));
    }
}
