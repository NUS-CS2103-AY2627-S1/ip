package megia.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import megia.exception.ErrorCode;
import megia.exception.UserInputException;
import megia.model.Deadline;
import megia.model.Event;
import megia.model.ParsedCommand;

class TaskParserTest {
    private final TaskParser taskParser = new TaskParser();

    @Test
    void parseNewTask_sameDayEvent_returnsExpectedEvent() throws UserInputException {
        Event event = parseEvent("meeting /on 2/12/2019 /from 1400 /to 1600");

        assertEquals("meeting", event.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 14, 0), event.getStartTime());
        assertEquals(LocalDateTime.of(2019, 12, 2, 16, 0), event.getEndTime());
    }

    @Test
    void parseNewTask_completeEndpoints_returnsExpectedEvent() throws UserInputException {
        Event event = parseEvent(
                "conference /from 2019-12-02 1400 /to 2019-12-03 1600");

        assertEquals("conference", event.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 14, 0), event.getStartTime());
        assertEquals(LocalDateTime.of(2019, 12, 3, 16, 0), event.getEndTime());
    }

    @Test
    void parseNewTask_missingEventMarkers_preservesErrorPrecedence() {
        assertEventError("meeting /to 2019-12-02 1600",
                ErrorCode.EVENT_FROM_MARKER_MISSING);
        assertEventError("meeting /from 2019-12-02 1400",
                ErrorCode.EVENT_TO_MARKER_MISSING);
    }

    @Test
    void parseNewTask_duplicateEventMarker_preservesErrorPrecedence() {
        assertEventError("meeting /from 2019-12-02 1400 /from 2019-12-03 1400",
                ErrorCode.DUPLICATE_MARKER);
        assertEventError(
                "meeting /to 2019-12-02 1600 /to 2019-12-03 1600 /from 2019-12-02 1400",
                ErrorCode.DUPLICATE_MARKER);
        assertEventError(
                "meeting /on 2019-12-02 /on 2019-12-03 /from 1400 /to 1600",
                ErrorCode.DUPLICATE_MARKER);
    }

    @Test
    void parseNewTask_missingOrDuplicateDeadlineParameters_reportsSpecificErrors() {
        assertDeadlineError("report", ErrorCode.DEADLINE_BY_MARKER_MISSING);
        assertDeadlineError("report /by", ErrorCode.DEADLINE_BY_VALUE_MISSING);
        assertDeadlineError("/by 2019-12-02 1800", ErrorCode.DEADLINE_DESCRIPTION_MISSING);
        assertDeadlineError(
                "report /by 2019-12-02 1800 /by 2019-12-03 1800",
                ErrorCode.DUPLICATE_MARKER);
    }

    @Test
    void parseNewTask_malformedEventMarkers_preservesErrorPrecedence() {
        assertEventError("meeting /frm 2019-12-02 1400 /to 2019-12-02 1600",
                ErrorCode.EVENT_FROM_MARKER_MISSING);
        assertEventError("meeting /from 2019-12-02 1400 /too 2019-12-02 1600",
                ErrorCode.EVENT_TO_MARKER_MISSING);
    }

    @Test
    void parseNewTask_outOfOrderEventMarkers_preservesErrorCode() {
        assertEventError(
                "meeting /to 2019-12-02 1600 /from 2019-12-02 1400",
                ErrorCode.EVENT_MARKERS_OUT_OF_ORDER);
        assertEventError(
                "meeting /from 2019-12-02 1400 /on 2019-12-02 /to 2019-12-02 1600",
                ErrorCode.EVENT_MARKERS_OUT_OF_ORDER);
    }

    @Test
    void parseNewTask_malformedEventValues_preservesErrorCodes() {
        assertEventError("meeting /on 2023-02-29 /from 1400 /to 1600",
                ErrorCode.EVENT_DATE_INVALID);
        assertEventError("meeting /on 30/2/2024 /from 1400 /to 1600",
                ErrorCode.EVENT_DATE_INVALID);
        assertEventError("meeting /on 2019-12-02 /from 2400 /to 1600",
                ErrorCode.EVENT_START_TIME_INVALID);
        assertEventError("meeting /from 2019-12-02 1400 /to 2019-12-02 2400",
                ErrorCode.EVENT_END_TIME_INVALID);
        assertEventError("meeting /on 2019-12-02 /from 1600 /to 1600",
                ErrorCode.EVENT_END_NOT_AFTER_START);
        assertEventError(
                "meeting /from 2019-12-02 1600 /to 2019-12-02 1400",
                ErrorCode.EVENT_END_NOT_AFTER_START);
    }

    @Test
    void parseNewTask_leapDayAndBothDateFormats_createValidTasks() throws UserInputException {
        Deadline isoDeadline = parseDeadline("submit report /by 2024-02-29 0000");
        Deadline slashDeadline = parseDeadline("submit report /by 29/2/2024 2359");
        Event isoEvent = parseEvent("iso meeting /on 2024-02-29 /from 0000 /to 0001");
        Event slashEvent = parseEvent("slash meeting /on 29/2/2024 /from 2358 /to 2359");

        assertEquals(LocalDateTime.of(2024, 2, 29, 0, 0), isoDeadline.getDeadline());
        assertEquals(LocalDateTime.of(2024, 2, 29, 23, 59), slashDeadline.getDeadline());
        assertEquals(LocalDate.of(2024, 2, 29), isoEvent.getStartTime().toLocalDate());
        assertEquals(LocalTime.of(0, 0), isoEvent.getStartTime().toLocalTime());
        assertEquals(LocalDate.of(2024, 2, 29), slashEvent.getStartTime().toLocalDate());
        assertEquals(LocalTime.of(23, 59), slashEvent.getEndTime().toLocalTime());
    }

    @Test
    void parseNewTask_existingWhitespaceAroundMarkers_remainsAccepted() throws UserInputException {
        Deadline deadline = parseDeadline("  submit report   /by   29/2/2024 1800  ");
        Event event = parseEvent(
                "  team meeting   /on   29/2/2024   /from   0900   /to   1000  ");

        assertEquals("submit report", deadline.getDescription());
        assertEquals("team meeting", event.getDescription());
        assertEquals(LocalDateTime.of(2024, 2, 29, 18, 0), deadline.getDeadline());
        assertEquals(LocalDateTime.of(2024, 2, 29, 9, 0), event.getStartTime());
        assertEquals(LocalDateTime.of(2024, 2, 29, 10, 0), event.getEndTime());
    }

    private Event parseEvent(String body) throws UserInputException {
        return assertInstanceOf(
                Event.class, taskParser.parseNewTask(new ParsedCommand("event", body)));
    }

    private Deadline parseDeadline(String body) throws UserInputException {
        return assertInstanceOf(
                Deadline.class, taskParser.parseNewTask(new ParsedCommand("deadline", body)));
    }

    private void assertEventError(String body, ErrorCode expectedErrorCode) {
        ParsedCommand command = new ParsedCommand("event", body);
        UserInputException exception = assertThrows(
                UserInputException.class, () -> taskParser.parseNewTask(command));
        assertEquals(expectedErrorCode, exception.getErrorCode());
    }

    private void assertDeadlineError(String body, ErrorCode expectedErrorCode) {
        ParsedCommand command = new ParsedCommand("deadline", body);
        UserInputException exception = assertThrows(
                UserInputException.class, () -> taskParser.parseNewTask(command));
        assertEquals(expectedErrorCode, exception.getErrorCode());
    }
}
