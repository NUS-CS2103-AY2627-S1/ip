package herta.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import herta.exception.HertaException;
import herta.task.Deadline;
import herta.task.Event;
import herta.task.TaskDescriptionValidator;
import herta.task.Todo;

/**
 * Tests todo, deadline, and event parsing and validation.
 */
class TaskCreationParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseTodo_nonEmptyDescription_returnsTodo() throws HertaException {
        Todo todo = parser.parseTodo("todo   read book");

        assertEquals("read book", todo.getDescription());
        assertEquals("T | 0 | read book", todo.toStorageString());
    }

    @Test
    void parseTodo_emptyDescription_throwsHelpfulException() {
        HertaException exception = assertThrows(HertaException.class, () ->
                parser.parseTodo("todo   "));

        assertEquals("You forgot the todo description. Try: todo <description>.",
                exception.getMessage());
    }

    @Test
    void parseDeadline_validInput_returnsDeadlineWithParsedDateTime() throws HertaException {
        Deadline deadline = parser.parseDeadline("deadline submit report /by 2/12/2019 1800");

        assertEquals("submit report", deadline.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getBy());
    }

    @Test
    void parseDeadline_dateOnlyFormats_returnsMidnightDeadline() throws HertaException {
        Deadline slashDeadline = parser.parseDeadline("deadline slash report /by 2/12/2019");
        Deadline isoDeadline = parser.parseDeadline("deadline ISO report /by 2019-12-02");

        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), slashDeadline.getBy());
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), isoDeadline.getBy());
    }

    @Test
    void parseDeadline_malformedInput_throwsHelpfulException() {
        HertaException missingDelimiter = assertThrows(HertaException.class, () ->
                parser.parseDeadline("deadline submit report"));
        HertaException invalidDate = assertThrows(HertaException.class, () ->
                parser.parseDeadline("deadline submit report /by 31/02/2019 1800"));
        HertaException invalidSlashDate = assertThrows(HertaException.class, () ->
                parser.parseDeadline("deadline submit report /by 31/02/2019"));
        HertaException invalidIsoDate = assertThrows(HertaException.class, () ->
                parser.parseDeadline("deadline submit report /by 2019-02-30"));

        assertEquals("That deadline format won't work. Use: deadline <description> /by <date/time>.",
                missingDelimiter.getMessage());
        assertEquals("I can't schedule that value. Use a valid date/time, such as 2019-10-15 1800.",
                invalidDate.getMessage());
        assertEquals(invalidDate.getMessage(), invalidSlashDate.getMessage());
        assertEquals(invalidDate.getMessage(), invalidIsoDate.getMessage());
    }

    @Test
    void parseTaskCreation_duplicateMarkersAreReportedDirectly() {
        HertaException deadlineException = assertThrows(HertaException.class, () ->
                parser.parseDeadline("deadline report /by 2019-10-15 /by 2019-10-16"));
        HertaException eventException = assertThrows(HertaException.class, () ->
                parser.parseEvent("event meeting /from 2019-10-15 /from 2019-10-16 "
                        + "/to 2019-10-17"));

        assertEquals("One /by is enough. Use: deadline <description> /by <date/time>.",
                deadlineException.getMessage());
        assertEquals("One /from is enough. Use: event <description> /from <start> /to <end>.",
                eventException.getMessage());
    }

    @Test
    void parseTodo_invalidStorageCharactersAreRejectedBeforeTaskCreation() {
        HertaException delimiterException = assertThrows(HertaException.class, () ->
                parser.parseTodo("todo contains | separator"));
        HertaException controlException = assertThrows(HertaException.class, () ->
                parser.parseTodo("todo contains\u0000control"));

        assertEquals("Keep the description on one line and leave out the storage delimiter and control characters.",
                delimiterException.getMessage());
        assertEquals("Keep the description on one line and leave out the storage delimiter and control characters.",
                controlException.getMessage());
    }

    @Test
    void parseTodo_overlongDescription_returnsCommandGuidance() {
        String description = "a".repeat(TaskDescriptionValidator.MAX_DESCRIPTION_LENGTH + 1);

        HertaException exception = assertThrows(HertaException.class, () ->
                parser.parseTodo("todo " + description));

        assertEquals("Even a task description has limits. Keep it under 1000 characters.",
                exception.getMessage());
    }

    @Test
    void parseDates_outsideBusinessRangeAreRejected() {
        HertaException exception = assertThrows(HertaException.class, () ->
                parser.parseDeadline("deadline old /by 0000-01-01"));

        assertEquals("I can't schedule that value. Use a valid date/time, such as 2019-10-15 1800.",
                exception.getMessage());
    }

    @Test
    void parseEvent_validInput_returnsEventWithParsedRange() throws HertaException {
        Event event = parser.parseEvent(
                "event project meeting /from 2019-10-15 /to 2019-10-16");

        assertEquals("project meeting", event.getDescription());
        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2019, 10, 16, 0, 0), event.getTo());
    }

    @Test
    void parseEvent_mixedDateOnlyFormats_returnsMidnightRange() throws HertaException {
        Event event = parser.parseEvent(
                "event project meeting /from 2/12/2019 /to 2019-12-03");

        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2019, 12, 3, 0, 0), event.getTo());
    }

    @Test
    void parseEvent_nonIncreasingRange_throwsHelpfulException() {
        HertaException exception = assertThrows(HertaException.class, () -> parser.parseEvent(
                "event meeting /from 2019-10-16 /to 2019-10-15"));

        assertEquals("Time moves forward. Make the event end after it starts.", exception.getMessage());
    }

    @Test
    void parseEvent_invalidDateTime_throwsHelpfulException() {
        HertaException exception = assertThrows(HertaException.class, () -> parser.parseEvent(
                "event meeting /from someday /to 2019-10-16"));
        HertaException invalidIsoDate = assertThrows(HertaException.class, () -> parser.parseEvent(
                "event meeting /from 2019-02-30 /to 2019-03-01"));

        assertEquals("Those dates won't do. Use valid dates, such as 2019-10-15 or 2/12/2019 1800.",
                exception.getMessage());
        assertEquals(exception.getMessage(), invalidIsoDate.getMessage());
    }

    @Test
    void parseTaskCreation_missingMarkersAndValues_returnsSpecificGuidance() {
        HertaException deadlineMarker = assertThrows(HertaException.class, () ->
                parser.parseDeadline("deadline report"));
        HertaException deadlineValue = assertThrows(HertaException.class, () ->
                parser.parseDeadline("deadline report /by"));
        HertaException eventMarker = assertThrows(HertaException.class, () ->
                parser.parseEvent("event meeting"));
        HertaException eventStart = assertThrows(HertaException.class, () ->
                parser.parseEvent("event meeting /from /to 2019-10-16"));
        HertaException eventEnd = assertThrows(HertaException.class, () ->
                parser.parseEvent("event meeting /from 2019-10-15 /to"));

        assertEquals("That deadline format won't work. Use: deadline <description> /by <date/time>.",
                deadlineMarker.getMessage());
        assertEquals("A deadline needs a time. Use: deadline <description> /by <date/time>.",
                deadlineValue.getMessage());
        assertEquals("An event needs a start marker: /from <start>.", eventMarker.getMessage());
        assertEquals("An event cannot start from nowhere. Add: /from <start>.",
                eventStart.getMessage());
        assertEquals("An event cannot end nowhere. Add: /to <end>.", eventEnd.getMessage());
    }

    @Test
    void parseTaskCreation_markerLikeTextAndWrongOrder_areHandled() throws HertaException {
        Deadline deadline = parser.parseDeadline(
                "deadline use /by-like text /by 2019-10-15");
        Event event = parser.parseEvent(
                "event /to-not-marker /from 2019-10-15 /to 2019-10-16");
        HertaException wrongOrder = assertThrows(HertaException.class, () ->
                parser.parseEvent("event meeting /to 2019-10-16 /from 2019-10-15"));

        assertEquals("use /by-like text", deadline.getDescription());
        assertEquals("/to-not-marker", event.getDescription());
        assertEquals("That event format won't work. Try: event <description> /from <start> "
                + "/to <end>.", wrongOrder.getMessage());
    }

    @Test
    void parseTaskCreation_missingDescriptionsAndMarkers_reportsGuidance() {
        HertaException deadlineDescription = assertThrows(HertaException.class, () ->
                parser.parseDeadline("deadline /by 2019-10-15"));
        HertaException eventDescription = assertThrows(HertaException.class, () ->
                parser.parseEvent("event /from 2019-10-15 /to 2019-10-16"));
        HertaException duplicateEnd = assertThrows(HertaException.class, () ->
                parser.parseEvent("event meeting /from 2019-10-15 /to 2019-10-16 /to 2019-10-17"));
        HertaException missingEnd = assertThrows(HertaException.class, () ->
                parser.parseEvent("event meeting /from 2019-10-15"));

        assertEquals("Tell me what the deadline is for. Try: deadline <description> /by <date/time>.",
                deadlineDescription.getMessage());
        assertEquals("Tell me what the event is. Try: event <description> /from <start> /to <end>.",
                eventDescription.getMessage());
        assertEquals("One /to is enough. Use: event <description> /from <start> /to <end>.",
                duplicateEnd.getMessage());
        assertEquals("An event needs an end marker: /to <end>.", missingEnd.getMessage());
    }

    @Test
    void parseTaskCreation_invalidDescriptionCharacters_rejectAllTaskTypes() {
        assertThrows(HertaException.class, () -> parser.parseTodo("todo bad\u202Etext"));
        assertThrows(HertaException.class, () ->
                parser.parseDeadline("deadline bad\u202Etext /by 2019-10-15"));
        assertThrows(HertaException.class, () ->
                parser.parseEvent("event bad\u202Etext /from 2019-10-15 /to 2019-10-16"));
    }
}
