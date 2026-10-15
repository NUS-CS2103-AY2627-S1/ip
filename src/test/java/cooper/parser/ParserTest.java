package cooper.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import cooper.exception.CooperException;
import cooper.task.Deadline;
import cooper.task.Event;
import cooper.task.ToDo;

/** Tests the conversion of supported date strings into {@link LocalDateTime} values. */
public class ParserTest {
    private static final String INVALID_DATE_MESSAGE =
            "Invalid date. Use yyyy-MM-dd or dd-MM-yyyy, and HH:mm optionally.";

    @Test
    public void parseAction_knownCommandWithMixedCaseAndWhitespace_returnsAction() {
        assertEquals(Action.TODO, Parser.parseAction("  ToDo read a book  "));
    }

    @Test
    public void parseAction_emptyInput_throwsCooperException() {
        CooperException exception = assertThrows(CooperException.class, () ->
                Parser.parseAction("   "));

        assertEquals("Please enter a command!", exception.getMessage());
    }

    @Test
    public void parseAction_unknownCommand_throwsCooperException() {
        CooperException exception = assertThrows(CooperException.class, () ->
                Parser.parseAction("dance now"));

        assertEquals("I don't understand this command: dance", exception.getMessage());
    }

    @Test
    public void parseTaskNumber_positiveInteger_returnsTaskNumber() {
        assertEquals(1, Parser.parseTaskNumber("mark 1", "syntax error"));
        assertEquals(123, Parser.parseTaskNumber("delete 123", "syntax error"));
    }

    @Test
    public void parseTaskNumber_wrongNumberOfArguments_throwsSyntaxError() {
        CooperException missingIndex = assertThrows(CooperException.class, () ->
                Parser.parseTaskNumber("mark", "syntax error"));
        CooperException extraArgument = assertThrows(CooperException.class, () ->
                Parser.parseTaskNumber("mark 1 now", "syntax error"));

        assertEquals("syntax error", missingIndex.getMessage());
        assertEquals("syntax error", extraArgument.getMessage());
    }

    @Test
    public void parseTaskNumber_nonPositiveOrNonNumericIndex_throwsCooperException() {
        assertEquals("The index isn't valid!", assertThrows(CooperException.class, () ->
                Parser.parseTaskNumber("mark 0", "syntax error")).getMessage());
        assertEquals("The index isn't valid!", assertThrows(CooperException.class, () ->
                Parser.parseTaskNumber("mark -1", "syntax error")).getMessage());
        assertEquals("The index isn't valid!", assertThrows(CooperException.class, () ->
                Parser.parseTaskNumber("mark one", "syntax error")).getMessage());
    }

    @Test
    public void parseFindKeyword_keywordPresent_returnsKeyword() {
        assertEquals("project book", Parser.parseFindKeyword("  find   project book  "));
    }

    @Test
    public void parseFindKeyword_keywordMissing_throwsCooperException() {
        CooperException exception = assertThrows(CooperException.class, () ->
                Parser.parseFindKeyword("find   "));

        assertEquals("I need a keyword to find matching tasks!", exception.getMessage());
    }

    @Test
    public void parseTodo_descriptionPresent_returnsTodo() {
        ToDo todo = Parser.parseTodo("todo read a book");

        assertEquals("T | 0 | read a book", todo.toDataString());
    }

    @Test
    public void parseTodo_missingDescription_throwsCooperException() {
        CooperException exception = assertThrows(CooperException.class, () ->
                Parser.parseTodo("todo"));

        assertEquals("I need a task description. Use: todo <description>",
                exception.getMessage());
    }

    @Test
    public void parseDeadline_validCommand_returnsDeadline() {
        Deadline deadline = Parser.parseDeadline("deadline submit report /by 2026-08-30 23:59");

        assertEquals("D | 0 | submit report | 2026-08-30T23:59", deadline.toDataString());
    }

    @Test
    public void parseDeadline_missingTitleOrDelimiter_throwsCooperException() {
        assertEquals("I can't keep track of tasks with no name!", assertThrows(CooperException.class, () ->
                Parser.parseDeadline("deadline /by 2026-08-30")).getMessage());
        assertEquals("I need exactly one deadline. Use: deadline <description> /by <date>",
                assertThrows(CooperException.class, () ->
                        Parser.parseDeadline("deadline submit report 2026-08-30")).getMessage());
    }

    @Test
    public void parseEvent_validCommand_returnsEvent() {
        Event event = Parser.parseEvent(
                "event project meeting /from 2026-08-30 14:00 /to 2026-08-30 16:00");

        assertEquals("E | 0 | project meeting | 2026-08-30T14:00 | 2026-08-30T16:00",
                event.toDataString());
    }

    @Test
    public void parseEvent_missingTitleOrFromDelimiter_throwsCooperException() {
        assertEquals("I need a title for this event!", assertThrows(CooperException.class, () ->
                Parser.parseEvent("event /from 2026-08-30 /to 2026-08-31")).getMessage());
        assertEquals("I need an event title and start date. Use: event <description> /from <date> /to <date>",
                assertThrows(CooperException.class, () ->
                        Parser.parseEvent("event project meeting /to 2026-08-31")).getMessage());
    }

    @Test
    public void parseDate_yearMonthDayDate_returnsStartOfDay() {
        assertEquals(LocalDateTime.of(2026, 8, 28, 0, 0),
                Parser.parseDate("2026-08-28"));
    }

    @Test
    public void parseDate_dayMonthYearDate_returnsStartOfDay() {
        assertEquals(LocalDateTime.of(2026, 8, 28, 0, 0),
                Parser.parseDate("28-08-2026"));
    }

    @Test
    public void parseDate_supportedDateTimeFormats_returnsSpecifiedTime() {
        assertEquals(LocalDateTime.of(2026, 8, 28, 9, 5),
                Parser.parseDate("2026-08-28 09:05"));
        assertEquals(LocalDateTime.of(2026, 8, 28, 23, 59),
                Parser.parseDate("28-08-2026 23:59"));
    }

    @Test
    public void parseDate_slashSeparatorsAndSurroundingWhitespace_returnsParsedDateTime() {
        assertEquals(LocalDateTime.of(2026, 8, 28, 14, 30),
                Parser.parseDate("  28/08/2026 14:30  "));
    }

    @Test
    public void parseDate_validLeapDay_returnsParsedDateTime() {
        assertEquals(LocalDateTime.of(2024, 2, 29, 0, 0),
                Parser.parseDate("2024-02-29"));
    }

    @Test
    public void parseDate_invalidCalendarDate_throwsCooperException() {
        CooperException exception = assertThrows(CooperException.class, () ->
                Parser.parseDate("2025-02-29"));

        assertEquals(INVALID_DATE_MESSAGE, exception.getMessage());
    }

    @Test
    public void parseDate_invalidTime_throwsCooperException() {
        CooperException exception = assertThrows(CooperException.class, () ->
                Parser.parseDate("2026-08-28 24:00"));

        assertEquals(INVALID_DATE_MESSAGE, exception.getMessage());
    }

    @Test
    public void parseDate_unsupportedFormat_throwsCooperException() {
        CooperException exception = assertThrows(CooperException.class, () ->
                Parser.parseDate("August 28, 2026"));

        assertEquals(INVALID_DATE_MESSAGE, exception.getMessage());
    }
}
