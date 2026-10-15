package kafka.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import kafka.exception.ParserException;
import kafka.task.Deadline;
import kafka.task.Event;
import kafka.task.Task;
import kafka.task.Todo;

/**
 * Tests parsing of valid and invalid task commands.
 */
class TaskParserTest {
    @Test
    void parseTodo_preservesDescriptionWhitespace() throws ParserException {
        Task task = TaskParser.parseTodo("todo   read book   ");

        assertInstanceOf(Todo.class, task);
        assertEquals("[T][ ]    read book   ", task.display());
    }

    @Test
    void parseTodoRejectsEmptyDescription() {
        assertThrows(ParserException.class, () -> TaskParser.parseTodo("todo   "));
    }

    @Test
    void parseTodoRejectsStorageDelimiterInDescription() {
        ParserException exception = assertThrows(ParserException.class, () ->
                TaskParser.parseTodo("todo compare Java | Python"));

        assertEquals("Task details cannot contain | sorryyy", exception.getMessage());
    }

    @Test
    void parseDeadlinePreservesDeadlineAsText() throws ParserException {
        Task task = TaskParser.parseDeadline("deadline do homework /by no idea :-p");

        assertInstanceOf(Deadline.class, task);
        assertEquals("[D][ ]  do homework  (by: no idea :-p)", task.display());
    }

    @Test
    void parseDeadlineFormatsRecognizedDate() throws ParserException {
        Task task = TaskParser.parseDeadline("deadline submit report /by 2020-01-18");

        assertEquals("[D][ ]  submit report  (by: 18 Jan 2020)", task.display());
    }

    @Test
    void parseDeadlineFormatsRecognizedTimeAsTwentyFourHourTime()
            throws ParserException {
        Task task = TaskParser.parseDeadline("deadline sleep /by 11:59pm");

        assertEquals("[D][ ]  sleep  (by: 2359)", task.display());
    }

    @Test
    void parseDeadlineFormatsRecognizedDateAndTime() throws ParserException {
        Task task = TaskParser.parseDeadline(
                "deadline celebrate /by 2020-01-18 11:59pm");

        assertEquals("[D][ ]  celebrate  (by: 18 Jan 2020 2359)", task.display());
    }

    @Test
    void parseDeadlineAcceptsSlashAndHumanReadableDates() throws ParserException {
        Task slashDate = TaskParser.parseDeadline("deadline first /by 18/1/2020");
        Task humanDate = TaskParser.parseDeadline("deadline second /by 18 jan 2020");

        assertEquals("[D][ ]  first  (by: 18 Jan 2020)", slashDate.display());
        assertEquals("[D][ ]  second  (by: 18 Jan 2020)", humanDate.display());
    }

    @Test
    void parseDeadlineRejectsMissingByMarker() {
        assertThrows(ParserException.class, () ->
                TaskParser.parseDeadline("deadline return book"));
    }

    @Test
    void parseDeadlineRejectsEmptyDeadline() {
        assertThrows(ParserException.class, () ->
                TaskParser.parseDeadline("deadline return book /by"));
    }

    @Test
    void parseDeadlineRejectsStorageDelimiterInEverySavedField() {
        ParserException descriptionError = assertThrows(ParserException.class, () ->
                TaskParser.parseDeadline("deadline compare A | B /by Friday"));
        ParserException deadlineError = assertThrows(ParserException.class, () ->
                TaskParser.parseDeadline("deadline submit report /by Fri | Sat"));

        assertEquals("Task details cannot contain | sorryyy", descriptionError.getMessage());
        assertEquals("Task details cannot contain | sorryyy", deadlineError.getMessage());
    }

    @Test
    void parseEventReturnsEvent() throws ParserException {
        Task task = TaskParser.parseEvent(
                "event project meeting /from Mon 2pm /to 4pm");

        assertInstanceOf(Event.class, task);
        assertEquals("[E][ ]  project meeting  (from: Mon 2pm to: 1600)",
                task.display());
    }

    @Test
    void parseEventFormatsDateAndTimeAtBothEndpoints() throws ParserException {
        Task task = TaskParser.parseEvent(
                "event launch /from 2020-01-18 9am /to 2020-01-18 23:59");

        assertEquals("[E][ ]  launch  (from: 18 Jan 2020 0900"
                + " to: 18 Jan 2020 2359)", task.display());
    }

    @Test
    void parseEventRejectsMissingMarkers() {
        assertThrows(ParserException.class, () ->
                TaskParser.parseEvent("event project meeting"));
    }

    @Test
    void parseEventRejectsEmptyStartOrEnd() {
        assertThrows(ParserException.class, () ->
                TaskParser.parseEvent("event project meeting /from /to 4pm"));
        assertThrows(ParserException.class, () ->
                TaskParser.parseEvent("event project meeting /from 2pm /to"));
    }

    @Test
    void parseEventRejectsStorageDelimiterInEverySavedField() {
        ParserException descriptionError = assertThrows(ParserException.class, () ->
                TaskParser.parseEvent("event compare A | B /from 2pm /to 3pm"));
        ParserException startError = assertThrows(ParserException.class, () ->
                TaskParser.parseEvent("event meeting /from Mon | Tue /to Wed"));
        ParserException endError = assertThrows(ParserException.class, () ->
                TaskParser.parseEvent("event meeting /from Mon /to Tue | Wed"));

        assertEquals("Task details cannot contain | sorryyy", descriptionError.getMessage());
        assertEquals("Task details cannot contain | sorryyy", startError.getMessage());
        assertEquals("Task details cannot contain | sorryyy", endError.getMessage());
    }

    @Test
    void parseTaskNumberReturnsPositiveWholeNumber() throws ParserException {
        assertEquals(12, TaskParser.parseTaskNumber("delete 12", "delete"));
    }

    @Test
    void parseTaskNumberRejectsInvalidNumbers() {
        assertThrows(ParserException.class, () ->
                TaskParser.parseTaskNumber("mark abc", "mark"));
        assertThrows(ParserException.class, () ->
                TaskParser.parseTaskNumber("mark 0", "mark"));
    }

    @Test
    void parseRenameReturnsTaskNumberAndNewName() throws ParserException {
        RenameRequest request = TaskParser.parseRename("rename 2 buy groceries");

        assertEquals(2, request.taskNumber());
        assertEquals(" buy groceries", request.newName());
    }

    @Test
    void parseRenameRejectsMissingOrUnsafeName() {
        assertThrows(ParserException.class, () -> TaskParser.parseRename("rename 2"));
        assertThrows(ParserException.class, () -> TaskParser.parseRename("rename two books"));
        assertThrows(ParserException.class, () -> TaskParser.parseRename("rename 2 buy | cook"));
    }

    @ParameterizedTest
    @CsvSource({
        "2024-02-29, 29 Feb 2024",
        "29/2/2024, 29 Feb 2024",
        "29 fEb 2024, 29 Feb 2024",
        "0000, 0000",
        "9:05, 0905",
        "12am, 0000",
        "12pm, 1200",
        "1:05PM, 1305",
        "29/2/2024 9:05, 29 Feb 2024 0905",
        "29 FEB 2024 12am, 29 Feb 2024 0000",
        "2024-02-29   0905, 29 Feb 2024 0905"
    })
    void parseDeadline_supportedDateAndTimeFormats_normalizesValue(String input, String expected)
            throws ParserException {
        Deadline deadline = TaskParser.parseDeadline("deadline submit report /by " + input);

        assertEquals("D | 0 |  submit report  | " + expected, deadline.toDataString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2023-02-29", "31/4/2024", "24:00", "1260", "13pm", "next   Sunday"})
    void parseDeadline_unrecognizedTiming_preservesText(String timing) throws ParserException {
        Deadline deadline = TaskParser.parseDeadline("deadline submit report /by " + timing);

        assertEquals("D | 0 |  submit report  | " + timing, deadline.toDataString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"deadline", "deadline /by Friday", "deadline   /by Friday"})
    void parseDeadline_missingDescription_throwsParserException(String input) {
        assertThrows(ParserException.class, () -> TaskParser.parseDeadline(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "event", "event /from Monday /to Tuesday", "event meeting /from Monday",
        "event meeting /to Tuesday", "event meeting /to Tuesday /from Monday"
    })
    void parseEvent_missingDescriptionOrInvalidMarkers_throwsParserException(String input) {
        assertThrows(ParserException.class, () -> TaskParser.parseEvent(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "-1", "1.5", "1 2", "2147483648"})
    void parseTaskNumber_invalidNumber_throwsParserException(String number) {
        assertThrows(ParserException.class, () -> TaskParser.parseTaskNumber("delete " + number, "delete"));
    }

    @Test
    void parseTaskNumber_boundaryValuesAndWhitespace_returnsNumber() throws ParserException {
        assertEquals(1, TaskParser.parseTaskNumber("mark   1   ", "mark"));
        assertEquals(Integer.MAX_VALUE, TaskParser.parseTaskNumber("unmark 2147483647", "unmark"));
    }

    @Test
    void parseRename_extraWhitespace_preservesName() throws ParserException {
        assertEquals(new RenameRequest(1, "   read a novel   "),
                TaskParser.parseRename("rename   1   read a novel   "));
    }

    @ParameterizedTest
    @ValueSource(strings = {"rename", "rename 1   ", "rename 0 book", "rename -1 book", "rename 2147483648 book"})
    void parseRename_invalidArguments_throwsParserException(String input) {
        assertThrows(ParserException.class, () -> TaskParser.parseRename(input));
    }

    @Test
    void parseFindKeyword_phraseWithWhitespace_preservesCaseAndTrimsEdges() throws ParserException {
        assertEquals("Read Book", TaskParser.parseFindKeyword("find   Read Book   "));
    }

    @ParameterizedTest
    @ValueSource(strings = {"find", "find   "})
    void parseFindKeyword_missingKeyword_throwsParserException(String input) {
        assertThrows(ParserException.class, () -> TaskParser.parseFindKeyword(input));
    }

    @ParameterizedTest
    @CsvSource({"2024-02-29 12pm, 29 Feb 2024 1200", "next Sunday, next Sunday"})
    void parseSnooze_deadline_returnsNormalizedReplacement(String input, String expected) throws ParserException {
        assertEquals(new DeadlineSnoozeRequest(2, expected), TaskParser.parseSnooze("snooze   2   /by " + input));
    }

    @ParameterizedTest
    @CsvSource({
        "/from 9am, 0900,",
        "/to 12pm,, 1200",
        "/from 2024-02-29 9am /to next Sunday, 29 Feb 2024 0900, next Sunday"
    })
    void parseSnooze_event_returnsOnlySuppliedEndpoints(String schedule, String from, String to)
            throws ParserException {
        assertEquals(new EventSnoozeRequest(3, Optional.ofNullable(from), Optional.ofNullable(to)),
                TaskParser.parseSnooze("snooze 3 " + schedule));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "snooze", "snooze 1", "snooze one /by Sunday", "snooze 0 /by Sunday",
        "snooze -1 /by Sunday", "snooze 2147483648 /by Sunday", "snooze 1 Sunday",
        "snooze 1 /by", "snooze 1 /from", "snooze 1 /to",
        "snooze 1 /from /to Sunday", "snooze 1 /from Monday /to",
        "snooze 1 /by Sunday /from Monday", "snooze 1 /by Sunday /to Monday",
        "snooze 1 /from Monday /by Sunday", "snooze 1 /to Tuesday /from Monday",
        "snooze 1 /by Sun | Mon", "snooze 1 /from Mon | Tue", "snooze 1 /to Tue | Wed"
    })
    void parseSnooze_invalidArguments_throwsParserException(String input) {
        assertThrows(ParserException.class, () -> TaskParser.parseSnooze(input));
    }

    @Test
    void parseDeadline_markerSubstrings_preservesDescriptionAndTiming() throws ParserException {
        assertEquals("D | 0 |  read /bytes guide  | Friday",
                TaskParser.parseDeadline("deadline read /bytes guide /by Friday").toDataString());
        assertEquals("D | 0 |  read docs/by  | tomorrow/today",
                TaskParser.parseDeadline("deadline read docs/by /by tomorrow/today").toDataString());
    }

    @Test
    void parseEvent_markerSubstrings_preservesDescriptionAndTiming() throws ParserException {
        assertEquals("E | 0 |  review /fromage /today docs/from  | Monday | Tuesday",
                TaskParser.parseEvent("event review /fromage /today docs/from /from Monday /to Tuesday")
                        .toDataString());
        assertEquals("E | 0 |  meeting  | tomorrow/today | Tuesday",
                TaskParser.parseEvent("event meeting /from tomorrow/today /to Tuesday").toDataString());
    }

    @Test
    void parseSnooze_markerSubstrings_preservesTiming() throws ParserException {
        assertEquals(new DeadlineSnoozeRequest(1, "tomorrow/today"),
                TaskParser.parseSnooze("snooze 1 /by tomorrow/today"));
        assertEquals(new EventSnoozeRequest(2, Optional.of("/bytes"), Optional.empty()),
                TaskParser.parseSnooze("snooze 2 /from /bytes"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"deadline report /by Friday /by Saturday", "deadline report /by/by Friday",
        "deadline report /byFriday", "deadline report/ by Friday", "deadline report/by Friday"})
    void parseDeadline_duplicateOrIncompleteMarkers_rejectsInput(String input) {
        assertThrows(ParserException.class, () -> TaskParser.parseDeadline(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "event meeting /from Monday /from Tuesday /to Wednesday",
        "event meeting /from Monday /to Tuesday /to Wednesday",
        "event meeting /fromMonday /to Tuesday", "event meeting /from Monday /today Tuesday"
    })
    void parseEvent_duplicateOrIncompleteMarkers_rejectsInput(String input) {
        assertThrows(ParserException.class, () -> TaskParser.parseEvent(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "snooze 1 /by Friday /by Saturday", "snooze 1 /from Monday /from Tuesday",
        "snooze 1 /to Monday /to Tuesday", "snooze 1 /from Monday /to Tuesday /to Wednesday",
        "snooze 1 /byFriday", "snooze 1 /fromMonday", "snooze 1 /today", "snooze 1 /bytes"
    })
    void parseSnooze_duplicateOrIncompleteMarkers_rejectsInput(String input) {
        assertThrows(ParserException.class, () -> TaskParser.parseSnooze(input));
    }

    @Test
    void parseTasks_unicodeWhitespace_preservesDescriptionAndRecognizesMarkers() throws ParserException {
        assertEquals("T | 0 |  \u2003read book\u2003",
                TaskParser.parseTodo("todo \u2003read book\u2003").toDataString());
        assertEquals("D | 0 |  report\u2003 | Friday",
                TaskParser.parseDeadline("deadline report\u2003/by\u2003Friday\u2003").toDataString());
        assertEquals("E | 0 |  meeting\u2003 | Monday | Tuesday",
                TaskParser.parseEvent("event meeting\u2003/from\u2003Monday\u2003/to\u2003Tuesday")
                        .toDataString());
    }

    @Test
    void parseTasks_multilineDetails_rejectsUnsafeStorageFields() {
        assertThrows(ParserException.class, () -> TaskParser.parseTodo("todo first\nsecond"));
        assertThrows(ParserException.class, () -> TaskParser.parseDeadline("deadline report /by next\nMonday"));
        assertThrows(ParserException.class, () -> TaskParser.parseEvent("event meeting /from Mon\rday /to Tuesday"));
        assertThrows(ParserException.class, () -> TaskParser.parseRename("rename 1 first\nsecond"));
        assertThrows(ParserException.class, () -> TaskParser.parseSnooze("snooze 1 /by next\nMonday"));
    }
}
