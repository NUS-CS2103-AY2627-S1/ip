package bot.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import bot.exception.BotException;
import bot.task.Deadline;
import bot.task.Event;

/**
 * Tests the parser's higher-value date, task creation, and task index logic.
 */
class ParserTest {
    @Test
    void parse_irregularWhitespace_normalizesCommandAndArguments() {
        ParsedCommand command = Parser.parse("  DEADLINE   submit   report   /by   2019-12-02  ");

        assertEquals(CommandType.DEADLINE, command.getType());
        assertEquals("submit report /by 2019-12-02", command.getArguments());
    }

    @Test
    void parse_nullInput_returnsEmptyUnknownCommand() {
        ParsedCommand command = Parser.parse(null);

        assertEquals(CommandType.UNKNOWN, command.getType());
        assertEquals("", command.getCommandWord());
        assertEquals("", command.getArguments());
    }

    @Test
    void parseTodo_storageSeparatorInDescription_throwsException() {
        assertBotException("Task descriptions cannot contain the | character.", () ->
                Parser.parseTodo("plan | review"));
    }

    @Test
    void parseDeadline_validArguments_returnsDeadline() throws BotException {
        Deadline deadline = Parser.parseDeadline("submit report /by 2019-12-02");

        assertEquals("D | 0 | submit report | 2019-12-02", deadline.toFileString());
    }

    @Test
    void parseDeadline_emptyArguments_throwsException() {
        assertBotException("The description of a deadline cannot be empty.", () ->
                Parser.parseDeadline(""));
    }

    @Test
    void parseDeadline_missingByMarker_throwsException() {
        assertBotException("A deadline must include /by followed by its date.", () ->
                Parser.parseDeadline("submit report 2019-12-02"));
    }

    @Test
    void parseDeadline_emptyDescription_throwsException() {
        assertBotException("The description of a deadline cannot be empty.", () ->
                Parser.parseDeadline("/by 2019-12-02"));
    }

    @Test
    void parseDeadline_emptyDate_throwsException() {
        assertBotException("The date of a deadline cannot be empty.", () ->
                Parser.parseDeadline("submit report /by"));
    }

    @Test
    void parseDeadline_wrongDateFormat_throwsException() {
        assertBotException("Use yyyy-MM-dd for deadline dates, e.g. 2019-12-02.", () ->
                Parser.parseDeadline("submit report /by 02-12-2019"));
    }

    @Test
    void parseDeadline_nonexistentDate_throwsException() {
        assertBotException("Use yyyy-MM-dd for deadline dates, e.g. 2019-12-02.", () ->
                Parser.parseDeadline("submit report /by 2019-02-29"));
    }

    @Test
    void parseDeadline_repeatedByMarker_throwsException() {
        assertBotException("A deadline can contain only one /by marker.", () ->
                Parser.parseDeadline("submit report /by 2019-12-02 /by 2019-12-03"));
    }

    @Test
    void parseDeadline_markerEmbeddedInWord_throwsMissingMarkerException() {
        assertBotException("A deadline must include /by followed by its date.", () ->
                Parser.parseDeadline("submit/by 2019-12-02"));
    }

    @Test
    void parseEvent_validSameDayArguments_returnsEvent() throws BotException {
        Event event = Parser.parseEvent(
                "project meeting /from 2019-12-02 1400 /to 2019-12-02 1600");

        assertEquals("E | 0 | project meeting | 2019-12-02T14:00 | 2019-12-02T16:00",
                event.toFileString());
    }

    @Test
    void parseEvent_validMultiDayArguments_returnsEvent() throws BotException {
        Event event = Parser.parseEvent(
                "orientation /from 2019-12-02 0900 /to 2019-12-05 1700");

        assertEquals("E | 0 | orientation | 2019-12-02T09:00 | 2019-12-05T17:00",
                event.toFileString());
    }

    @Test
    void parseEvent_emptyArguments_throwsException() {
        assertBotException("The description of an event cannot be empty.", () ->
                Parser.parseEvent(""));
    }

    @Test
    void parseEvent_missingFromMarker_throwsException() {
        assertBotException("An event must include /from followed by its start time.", () ->
                Parser.parseEvent("meeting /to 2019-12-02 1600"));
    }

    @Test
    void parseEvent_missingToMarker_throwsException() {
        assertBotException("An event must include /to followed by its end time.", () ->
                Parser.parseEvent("meeting /from 2019-12-02 1400"));
    }

    @Test
    void parseEvent_emptyDescription_throwsException() {
        assertBotException("The description of an event cannot be empty.", () ->
                Parser.parseEvent("/from 2019-12-02 1400 /to 2019-12-02 1600"));
    }

    @Test
    void parseEvent_emptyStartTime_throwsException() {
        assertBotException("The start time of an event cannot be empty.", () ->
                Parser.parseEvent("meeting /from /to 2019-12-02 1600"));
    }

    @Test
    void parseEvent_emptyEndTime_throwsException() {
        assertBotException("The end time of an event cannot be empty.", () ->
                Parser.parseEvent("meeting /from 2019-12-02 1400 /to"));
    }

    @Test
    void parseEvent_invalidStartTime_throwsException() {
        assertBotException("Use yyyy-MM-dd HHmm for event dates and times, e.g. 2019-12-02 1400.", () ->
                Parser.parseEvent(
                        "meeting /from 2019-12-02 25:00 /to 2019-12-03 1600"));
    }

    @Test
    void parseEvent_invalidEndTime_throwsException() {
        assertBotException("Use yyyy-MM-dd HHmm for event dates and times, e.g. 2019-12-02 1400.", () ->
                Parser.parseEvent(
                        "meeting /from 2019-12-02 1400 /to 2019-02-29 1600"));
    }

    @Test
    void parseEvent_endEqualsStart_throwsException() {
        assertBotException("The event end date and time must be after its start date and time.", () ->
                Parser.parseEvent(
                        "meeting /from 2019-12-02 1400 /to 2019-12-02 1400"));
    }

    @Test
    void parseEvent_endBeforeStart_throwsException() {
        assertBotException("The event end date and time must be after its start date and time.", () ->
                Parser.parseEvent(
                        "meeting /from 2019-12-02 1600 /to 2019-12-02 1400"));
    }

    @Test
    void parseEvent_repeatedFromMarker_throwsException() {
        assertBotException("An event can contain only one /from marker.", () ->
                Parser.parseEvent("meeting /from 2019-12-02 1400 "
                        + "/from 2019-12-02 1500 /to 2019-12-02 1600"));
    }

    @Test
    void parseEvent_repeatedToMarker_throwsException() {
        assertBotException("An event can contain only one /to marker.", () ->
                Parser.parseEvent("meeting /from 2019-12-02 1400 "
                        + "/to 2019-12-02 1500 /to 2019-12-02 1600"));
    }

    @Test
    void parseEvent_reversedMarkers_throwsException() {
        assertBotException("The /from marker must appear before the /to marker.", () ->
                Parser.parseEvent("meeting /to 2019-12-02 1600 /from 2019-12-02 1400"));
    }

    @Test
    void parseDateQuery_validDate_returnsLocalDate() throws BotException {
        assertEquals(LocalDate.of(2019, 12, 2), Parser.parseDateQuery("2019-12-02"));
    }

    @Test
    void parseDateQuery_emptyDate_throwsException() {
        assertBotException("Tell me which date to search using yyyy-MM-dd.", () ->
                Parser.parseDateQuery(""));
    }

    @Test
    void parseDateQuery_wrongDateFormat_throwsException() {
        assertBotException("Use yyyy-MM-dd when searching by date, e.g. 2019-12-02.", () ->
                Parser.parseDateQuery("02-12-2019"));
    }

    @Test
    void parseDateQuery_nonexistentDate_throwsException() {
        assertBotException("Use yyyy-MM-dd when searching by date, e.g. 2019-12-02.", () ->
                Parser.parseDateQuery("2019-02-29"));
    }

    @Test
    void parseFindKeyword_validKeyword_returnsKeyword() throws BotException {
        assertEquals("read book", Parser.parseFindKeyword("read book"));
    }

    @Test
    void parseFindKeyword_emptyKeyword_throwsException() {
        assertBotException("Tell me what keyword to find.", () ->
                Parser.parseFindKeyword(""));
    }

    @Test
    void parseTaskIndex_firstTaskNumber_returnsFirstArrayIndex() throws BotException {
        assertEquals(0, Parser.parseTaskIndex("1", 3, "mark"));
    }

    @Test
    void parseTaskIndex_lastTaskNumber_returnsLastArrayIndex() throws BotException {
        assertEquals(2, Parser.parseTaskIndex("3", 3, "mark"));
    }

    @Test
    void parseTaskIndex_negativeTaskCount_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> Parser.parseTaskIndex("1", -1, "mark"));
    }

    @Test
    void parseTaskIndex_blankCommandName_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> Parser.parseTaskIndex("1", 3, ""));
    }

    @Test
    void parseTaskIndex_emptyArgument_throwsException() {
        BotException exception = assertThrows(BotException.class, () ->
                Parser.parseTaskIndex("", 3, "mark"));

        assertEquals("Tell me which task number to mark.", exception.getMessage());
    }

    @Test
    void parseTaskIndex_nonNumericArgument_throwsException() {
        BotException exception = assertThrows(BotException.class, () ->
                Parser.parseTaskIndex("two", 3, "mark"));

        assertEquals("The task number must be a whole number.", exception.getMessage());
    }

    @Test
    void parseTaskIndex_multipleNumbers_throwsException() {
        BotException exception = assertThrows(BotException.class, () ->
                Parser.parseTaskIndex("1 2", 3, "mark"));

        assertEquals("Enter only one task number for the mark command.", exception.getMessage());
    }

    @Test
    void parseTaskIndex_emptyTaskList_throwsException() {
        BotException exception = assertThrows(BotException.class, () ->
                Parser.parseTaskIndex("1", 0, "mark"));

        assertEquals("The task list is empty.", exception.getMessage());
    }

    @Test
    void parseTaskIndex_zeroTaskNumber_throwsException() {
        BotException exception = assertThrows(BotException.class, () ->
                Parser.parseTaskIndex("0", 3, "mark"));

        assertEquals("Task number 0 does not exist. Choose a number from 1 to 3.",
                exception.getMessage());
    }

    @Test
    void parseTaskIndex_negativeTaskNumber_throwsException() {
        BotException exception = assertThrows(BotException.class, () ->
                Parser.parseTaskIndex("-1", 3, "mark"));

        assertEquals("Task number -1 does not exist. Choose a number from 1 to 3.",
                exception.getMessage());
    }

    @Test
    void parseTaskIndex_taskNumberAboveTaskCount_throwsException() {
        BotException exception = assertThrows(BotException.class, () ->
                Parser.parseTaskIndex("4", 3, "mark"));

        assertEquals("Task number 4 does not exist. Choose a number from 1 to 3.",
                exception.getMessage());
    }

    /**
     * Checks that a parser action fails with the expected user-facing explanation.
     *
     * @param expectedMessage expected exception message
     * @param action parser action expected to fail
     */
    private void assertBotException(String expectedMessage, Executable action) {
        BotException exception = assertThrows(BotException.class, action);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
