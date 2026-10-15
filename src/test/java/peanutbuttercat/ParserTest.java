package peanutbuttercat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests validation of task numbers entered for peanutbuttercat commands. */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseCommandType_knownCommand_returnsMatchingType() {
        assertEquals(CommandType.TODO, parser.parseCommandType("todo buy milk"));
        assertEquals(CommandType.FIND, parser.parseCommandType("find milk"));
        assertEquals(CommandType.BYE, parser.parseCommandType("  bye  "));
    }

    @Test
    void parseCommandType_unknownOrNullInput_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, parser.parseCommandType("meow at the moon"));
        assertEquals(CommandType.UNKNOWN, parser.parseCommandType(null));
    }

    @Test
    void getDescription_commandWithText_returnsTrimmedDescription() throws PeanutButterCatException {
        assertEquals("buy milk", parser.getDescription("todo   buy milk  ", "todo"));
    }

    @Test
    void getDescription_multipleWhitespace_normalizesDescription() throws PeanutButterCatException {
        assertEquals("buy fresh milk", parser.getDescription("todo  buy\t fresh   milk", "todo"));
    }

    @Test
    void getDescription_commandWithoutText_exceptionThrown() {
        assertThrows(PeanutButterCatException.class, () ->
                parser.getDescription("todo   ", "todo"));
    }

    @Test
    void validateNoArguments_commandWithoutArguments_completesNormally()
            throws PeanutButterCatException {
        parser.validateNoArguments("stats   ", "stats");
    }

    @Test
    void validateNoArguments_commandWithArguments_exceptionThrown() {
        assertThrows(PeanutButterCatException.class, () ->
                parser.validateNoArguments("stats week", "stats"));
    }

    @Test
    void parseDeadline_validInput_returnsDescriptionAndDueTime() throws PeanutButterCatException {
        Deadline deadline = parser.parseDeadline("deadline submit report /by 2/12/2019 1800");

        assertEquals("submit report", deadline.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getBy());
    }

    @Test
    void parseDeadline_missingByMarker_exceptionThrown() {
        assertThrows(PeanutButterCatException.class, () ->
                parser.parseDeadline("deadline submit report"));
    }

    @Test
    void parseDeadline_missingDescription_exceptionThrown() {
        assertThrows(PeanutButterCatException.class, () ->
                parser.parseDeadline("deadline /by 2/12/2019 1800"));
    }

    @Test
    void parseDeadline_missingDueTime_exceptionThrown() {
        assertThrows(PeanutButterCatException.class, () ->
                parser.parseDeadline("deadline submit report /by"));
    }

    @Test
    void parseDeadline_invalidDueTime_exceptionThrown() {
        assertThrows(PeanutButterCatException.class, () ->
                parser.parseDeadline("deadline submit report /by not-a-date"));
    }

    @Test
    void parseDeadline_duplicateByMarker_exceptionThrownWithHelpfulMessage() {
        PeanutButterCatException exception = assertThrows(PeanutButterCatException.class, () ->
                parser.parseDeadline("deadline submit /by report /by 2019-12-02"));

        assertEquals("Use '/by' only once in each command, purr-lease!", exception.getMessage());
    }

    @Test
    void parseDeadline_markerAsPartOfWord_exceptionThrown() {
        assertThrows(PeanutButterCatException.class, () ->
                parser.parseDeadline("deadline submit /bypass 2019-12-02"));
    }

    @Test
    void parseEvent_validInput_returnsDescriptionAndTimeRange() throws PeanutButterCatException {
        Event event = parser.parseEvent(
                "event team meeting /from 2019-12-02 1900 /to 2019-12-02 2000");

        assertEquals("team meeting", event.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 19, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2019, 12, 2, 20, 0), event.getTo());
    }

    @Test
    void parseEvent_missingFromOrToMarker_exceptionThrown() {
        assertThrows(PeanutButterCatException.class, () ->
                parser.parseEvent("event team meeting /from 2019-12-02 1900"));
        assertThrows(PeanutButterCatException.class, () ->
                parser.parseEvent("event team meeting /to 2019-12-02 2000"));
    }

    @Test
    void parseEvent_missingDescription_exceptionThrown() {
        assertThrows(PeanutButterCatException.class, () ->
                parser.parseEvent("event /from 2019-12-02 1900 /to 2019-12-02 2000"));
    }

    @Test
    void parseEvent_missingStartOrEndTime_exceptionThrown() {
        assertThrows(PeanutButterCatException.class, () ->
                parser.parseEvent("event team meeting /from /to 2019-12-02 2000"));
        assertThrows(PeanutButterCatException.class, () ->
                parser.parseEvent("event team meeting /from 2019-12-02 1900 /to"));
    }

    @Test
    void parseEvent_invalidStartOrEndTime_exceptionThrown() {
        assertThrows(PeanutButterCatException.class, () ->
                parser.parseEvent("event team meeting /from not-a-date /to 2019-12-02 2000"));
        assertThrows(PeanutButterCatException.class, () ->
                parser.parseEvent("event team meeting /from 2019-12-02 1900 /to not-a-date"));
    }

    @Test
    void parseEvent_endBeforeStart_exceptionThrownWithHelpfulMessage() {
        PeanutButterCatException exception = assertThrows(PeanutButterCatException.class, () ->
                parser.parseEvent("event team meeting /from 2019-12-02 2000 /to 2019-12-02 1900"));

        assertEquals("An event must end after it starts. Please check the '/from' and '/to' times!",
                exception.getMessage());
    }

    @Test
    void parseEvent_sameStartAndEnd_exceptionThrownWithHelpfulMessage() {
        PeanutButterCatException exception = assertThrows(PeanutButterCatException.class, () ->
                parser.parseEvent("event team meeting /from 2019-12-02 1900 /to 2019-12-02 1900"));

        assertEquals("An event must end after it starts. Please check the '/from' and '/to' times!",
                exception.getMessage());
    }

    @Test
    void parseEvent_duplicateOrReorderedMarkers_exceptionThrown() {
        assertThrows(PeanutButterCatException.class, () -> parser.parseEvent(
                "event meeting /from 2019-12-02 /from 2019-12-03 /to 2019-12-04"));
        assertThrows(PeanutButterCatException.class, () -> parser.parseEvent(
                "event meeting /to 2019-12-03 /from 2019-12-02"));
    }

    @Test
    void parseDateTime_supportedDateAndTimeFormats_returnsExpectedValues()
            throws PeanutButterCatException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                parser.parseDateTime("2/12/2019 1800"));
        assertEquals(LocalDateTime.of(2019, 12, 2, 19, 30),
                parser.parseDateTime("2019-12-02 19:30"));
        assertEquals(LocalDateTime.of(2019, 12, 2, 19, 30),
                parser.parseDateTime("  2019-12-02    19:30  "));
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0),
                parser.parseDateTime("2019-12-02"));
    }

    @Test
    void parseDateTime_unsupportedFormat_exceptionThrown() {
        assertThrows(PeanutButterCatException.class, () ->
                parser.parseDateTime("12/02/2019"));
    }

    @Test
    void parseDateTime_impossibleCalendarDate_exceptionThrown() {
        assertThrows(PeanutButterCatException.class, () ->
                parser.parseDateTime("2019-02-30"));
    }

    @Test
    void parseDate_validDate_returnsDate() throws PeanutButterCatException {
        assertEquals(LocalDate.of(2019, 12, 2), parser.parseDate("on 2019-12-02"));
    }

    @Test
    void parseDate_missingDate_exceptionThrown() {
        assertThrows(PeanutButterCatException.class, () -> parser.parseDate("on"));
    }

    @Test
    void parseDate_invalidDate_exceptionThrown() {
        assertThrows(PeanutButterCatException.class, () ->
                parser.parseDate("on not-a-date"));
    }

    @Test
    void parseDate_dateWithTime_exceptionThrown() {
        assertThrows(PeanutButterCatException.class, () ->
                parser.parseDate("on 2019-12-02 1900"));
    }

    @Test
    void parseTaskIndex_validFirstTask_returnsZeroBasedIndex() throws PeanutButterCatException {
        assertEquals(0, parser.parseTaskIndex("done 1", "done", 3));
    }

    @Test
    void parseTaskIndex_validLastTask_returnsZeroBasedIndex() throws PeanutButterCatException {
        assertEquals(2, parser.parseTaskIndex("delete 3", "delete", 3));
    }

    @Test
    void parseTaskIndex_missingNumber_exceptionThrown() {
        assertThrows(PeanutButterCatException.class, () ->
                parser.parseTaskIndex("done", "done", 3));
    }

    @Test
    void parseTaskIndex_nonNumericNumber_exceptionThrown() {
        assertThrows(PeanutButterCatException.class, () ->
                parser.parseTaskIndex("done two", "done", 3));
        assertThrows(PeanutButterCatException.class, () ->
                parser.parseTaskIndex("done +1", "done", 3));
    }

    @Test
    void parseTaskIndex_zeroNumber_exceptionThrown() {
        assertThrows(PeanutButterCatException.class, () ->
                parser.parseTaskIndex("done 0", "done", 3));
    }

    @Test
    void parseTaskIndex_negativeNumber_exceptionThrown() {
        assertThrows(PeanutButterCatException.class, () ->
                parser.parseTaskIndex("done -1", "done", 3));
    }

    @Test
    void parseTaskIndex_numberAboveTaskCount_exceptionThrown() {
        assertThrows(PeanutButterCatException.class, () ->
                parser.parseTaskIndex("done 4", "done", 3));
    }

    @Test
    void parseTaskIndex_anyNumberWithEmptyTaskList_exceptionThrown() {
        assertThrows(PeanutButterCatException.class, () ->
                parser.parseTaskIndex("done 1", "done", 0));
    }

    @Test
    void parseTaskIndex_negativeTaskCount_assertionErrorThrown() {
        assertThrows(AssertionError.class, () ->
                parser.parseTaskIndex("done 1", "done", -1));
    }

    @Test
    void parseDeadline_wrongCommandType_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> parser.parseDeadline("todo buy milk"));
    }
}
