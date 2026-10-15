package nova.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import nova.exception.NovaException;

/** Tests command parsing and validation. */
class ParserTest {
    private final Parser parser = new Parser();

    /** Verifies that exit and list commands are classified correctly. */
    @Test
    void parse_exitAndListCommands_returnsExpectedTypes() throws NovaException {
        Parser.ParsedCommand exit = parser.parse("bye");
        Parser.ParsedCommand list = parser.parse("list");

        assertEquals(Parser.CommandType.EXIT, exit.type());
        assertEquals(Parser.CommandType.LIST, list.type());
    }

    /** Verifies that null input produces a Nova error instead of a runtime exception. */
    @Test
    void parse_nullCommand_throwsClearError() {
        NovaException exception = assertThrows(NovaException.class, () -> parser.parse(null));

        assertEquals("Please enter a command.", exception.getMessage());
    }

    /** Verifies that find commands extract a trimmed keyword. */
    @Test
    void parse_findCommand_extractsKeyword() throws NovaException {
        Parser.ParsedCommand result = parser.parse("find   book  ");

        assertEquals(Parser.CommandType.FIND, result.type());
        assertEquals("book", result.description());
    }

    /** Verifies that task commands preserve their command type and number. */
    @Test
    void parse_taskNumberCommands_extractsCommandAndNumber() throws NovaException {
        Parser.ParsedCommand mark = parser.parse("mark 12");
        Parser.ParsedCommand unmark = parser.parse("unmark 2");
        Parser.ParsedCommand delete = parser.parse("delete 1");

        assertEquals(Parser.CommandType.MARK, mark.type());
        assertEquals(12, mark.taskNumber());
        assertEquals(Parser.CommandType.UNMARK, unmark.type());
        assertEquals(2, unmark.taskNumber());
        assertEquals(Parser.CommandType.DELETE, delete.type());
        assertEquals(1, delete.taskNumber());
    }

    /** Verifies that tag commands extract their task number and label. */
    @Test
    void parse_tagCommands_extractTaskNumberAndLabel() throws NovaException {
        Parser.ParsedCommand tag = parser.parse("tag 2 #urgent");
        Parser.ParsedCommand untag = parser.parse("untag 2 #urgent");

        assertEquals(Parser.CommandType.TAG, tag.type());
        assertEquals(2, tag.taskNumber());
        assertEquals("#urgent", tag.tag());
        assertEquals(Parser.CommandType.UNTAG, untag.type());
        assertEquals("#urgent", untag.tag());
    }

    /** Verifies that zero and negative task numbers are parsed as integers. */
    @Test
    void parse_taskNumberCommands_acceptsZeroAndNegativeNumbers() throws NovaException {
        Parser.ParsedCommand zero = parser.parse("mark 0");
        Parser.ParsedCommand negative = parser.parse("delete -3");

        assertEquals(0, zero.taskNumber());
        assertEquals(-3, negative.taskNumber());
    }

    /** Verifies that missing task numbers produce a clear error. */
    @Test
    void parse_missingTaskNumbers_throwClearError() {
        NovaException markException = assertThrows(NovaException.class, () -> parser.parse("mark"));
        NovaException unmarkException = assertThrows(NovaException.class, () -> parser.parse("unmark"));
        NovaException deleteException = assertThrows(NovaException.class, () -> parser.parse("delete"));

        assertEquals("Please provide a valid task number.", markException.getMessage());
        assertEquals("Please provide a valid task number.", unmarkException.getMessage());
        assertEquals("Please provide a valid task number.", deleteException.getMessage());
    }

    /** Verifies that malformed tag labels produce a clear error. */
    @Test
    void parse_malformedTagLabel_throwsClearError() {
        NovaException exception = assertThrows(NovaException.class, () -> parser.parse("tag 1 urgent"));

        assertEquals("Please provide a valid tag in the format #label.", exception.getMessage());
    }

    /** Verifies that the storage delimiter is rejected in tag labels. */
    @Test
    void parse_tagWithStorageDelimiter_throwsClearError() {
        NovaException exception = assertThrows(NovaException.class, () -> parser.parse("tag 1 #fun|urgent"));

        assertEquals("Please provide a valid tag in the format #label.", exception.getMessage());
    }

    /** Verifies that todo descriptions are trimmed during parsing. */
    @Test
    void parse_todoCommand_trimsDescription() throws NovaException {
        Parser.ParsedCommand result = parser.parse("todo   buy milk  ");

        assertEquals(Parser.CommandType.TODO, result.type());
        assertEquals("buy milk", result.description());
    }

    /** Verifies that the storage delimiter is rejected in task descriptions. */
    @Test
    void parse_taskDescriptionWithStorageDelimiter_throwsClearError() {
        NovaException exception = assertThrows(NovaException.class, () -> parser.parse("todo buy | milk"));

        assertEquals("The character '|' is not allowed in task descriptions.", exception.getMessage());
    }

    /** Verifies that deadline descriptions and ISO dates are parsed. */
    @Test
    void parse_deadlineCommand_parsesIsoDate() throws NovaException {
        Parser.ParsedCommand result = parser.parse("deadline submit report /by 2026-08-24");

        assertEquals(Parser.CommandType.DEADLINE, result.type());
        assertEquals("submit report", result.description());
        assertEquals(LocalDate.of(2026, 8, 24), result.date());
    }

    /** Verifies that event descriptions and time ranges are extracted. */
    @Test
    void parse_eventCommand_extractsDescriptionAndTimeRange() throws NovaException {
        Parser.ParsedCommand result = parser.parse(
                "event project meeting /from 2026-08-24 09:00 /to 2026-08-24 10:00");

        assertEquals(Parser.CommandType.EVENT, result.type());
        assertEquals("project meeting", result.description());
        assertEquals("2026-08-24 09:00", result.from());
        assertEquals("2026-08-24 10:00", result.to());
    }

    /** Verifies that nonexistent event dates are rejected. */
    @Test
    void parse_eventWithInvalidDate_throwsClearError() {
        String invalidEvent = "event meeting /from 2026-02-30 09:00 /to 2026-03-01 10:00";
        NovaException exception = assertThrows(NovaException.class, () -> parser.parse(invalidEvent));

        assertEquals("Please use event date-times in yyyy-MM-dd HH:mm format.", exception.getMessage());
    }

    /** Verifies that equal or reversed event ranges are rejected. */
    @Test
    void parse_eventWithNonChronologicalRange_throwsClearError() {
        String equalEvent = "event meeting /from 2026-08-24 09:00 /to 2026-08-24 09:00";
        String reversedEvent = "event meeting /from 2026-08-24 10:00 /to 2026-08-24 09:00";
        NovaException equalException = assertThrows(NovaException.class, () -> parser.parse(equalEvent));
        NovaException reversedException = assertThrows(NovaException.class, () -> parser.parse(reversedEvent));

        assertEquals("Event start must be before its end.", equalException.getMessage());
        assertEquals("Event start must be before its end.", reversedException.getMessage());
    }

    /** Verifies that date-filter commands parse ISO dates. */
    @Test
    void parse_onCommand_parsesIsoDate() throws NovaException {
        Parser.ParsedCommand result = parser.parse("on 2026-08-24");

        assertEquals(Parser.CommandType.ON, result.type());
        assertEquals(LocalDate.of(2026, 8, 24), result.date());
    }

    /** Verifies that malformed date-filter and deadline dates are rejected. */
    @Test
    void parse_dateCommandsWithInvalidDates_throwClearError() {
        String malformedDeadline = "deadline task /by 24-08-2026";
        NovaException deadlineException = assertThrows(NovaException.class, () -> parser.parse(malformedDeadline));
        NovaException onException = assertThrows(NovaException.class, () -> parser.parse("on 2026/08/24"));

        assertEquals("Please use a valid date in yyyy-MM-dd format.", deadlineException.getMessage());
        assertEquals("Please use a valid date in yyyy-MM-dd format.", onException.getMessage());
    }

    /** Verifies that invalid task numbers produce a clear error. */
    @Test
    void parse_invalidTaskNumber_throwsClearError() {
        NovaException exception = assertThrows(NovaException.class, () -> parser.parse("mark abc"));

        assertEquals("Please provide a valid task number.", exception.getMessage());
    }

    /** Verifies that a missing todo description produces a clear error. */
    @Test
    void parse_missingTodoDescription_throwsClearError() {
        NovaException exception = assertThrows(NovaException.class, () -> parser.parse("todo"));

        assertEquals("Please add a description after 'todo'.", exception.getMessage());
    }

    /** Verifies that an empty find keyword produces a clear error. */
    @Test
    void parse_missingFindKeyword_throwsClearError() {
        NovaException exception = assertThrows(NovaException.class, () -> parser.parse("find"));

        assertEquals("Please add a keyword after 'find'.", exception.getMessage());
    }

    /** Verifies that an impossible deadline date produces a clear error. */
    @Test
    void parse_invalidDeadlineDate_throwsClearError() {
        String impossibleDeadline = "deadline submit report /by 2026-02-30";
        NovaException exception = assertThrows(NovaException.class, () -> parser.parse(impossibleDeadline));

        assertEquals("Please use a valid date in yyyy-MM-dd format.", exception.getMessage());
    }

    /** Verifies that malformed deadline and event commands report format errors. */
    @Test
    void parse_malformedDeadlineAndEventCommands_throwFormatErrors() {
        String malformedDeadline = "deadline submit report";
        NovaException deadlineException = assertThrows(NovaException.class, () -> parser.parse(malformedDeadline));
        String malformedEvent = "event meeting /from Monday";
        NovaException eventException = assertThrows(NovaException.class, () -> parser.parse(malformedEvent));

        assertEquals("Please use: deadline DESCRIPTION /by DATE.", deadlineException.getMessage());
        assertEquals("Please use: event DESCRIPTION /from START /to END.", eventException.getMessage());
    }

    /** Verifies that malformed deadline variants report format errors. */
    @Test
    void parse_malformedDeadlineVariants_throwFormatError() {
        NovaException missingDate = assertThrows(NovaException.class, () -> parser.parse("deadline task /by "));
        String missingDescription = "deadline /by 2026-08-24";
        NovaException exception = assertThrows(NovaException.class, () -> parser.parse(missingDescription));

        assertEquals("Please use: deadline DESCRIPTION /by DATE.", missingDate.getMessage());
        assertEquals("Please use: deadline DESCRIPTION /by DATE.", exception.getMessage());
    }

    /** Verifies that malformed event variants report format errors. */
    @Test
    void parse_malformedEventVariants_throwFormatError() {
        String missingEndCommand = "event meeting /from Monday /to ";
        String missingStartCommand = "event meeting /to Tuesday";
        String reversedMarkersCommand = "event meeting /to Tuesday /from Monday";
        NovaException missingEnd = assertThrows(NovaException.class, () -> parser.parse(missingEndCommand));
        NovaException missingStart = assertThrows(NovaException.class, () -> parser.parse(missingStartCommand));
        NovaException reversedMarkers = assertThrows(NovaException.class, () -> parser.parse(reversedMarkersCommand));

        assertEquals("Please use: event DESCRIPTION /from START /to END.", missingEnd.getMessage());
        assertEquals("Please use: event DESCRIPTION /from START /to END.", missingStart.getMessage());
        assertEquals("Please use: event DESCRIPTION /from START /to END.", reversedMarkers.getMessage());
    }

    /** Verifies that whitespace around a todo description is handled. */
    @Test
    void parse_whitespaceAroundTodoDescription_isHandled() throws NovaException {
        Parser.ParsedCommand result = parser.parse("todo   read book   ");

        assertEquals("read book", result.description());
    }

    /** Verifies that commands with similar prefixes remain unknown. */
    @Test
    void parse_commandWithSimilarPrefix_isUnknown() {
        NovaException exception = assertThrows(NovaException.class, () -> parser.parse("listing"));

        assertEquals("I don't recognize that command.", exception.getMessage());
    }

    /** Verifies that unknown commands produce a clear error. */
    @Test
    void parse_unknownCommand_throwsClearError() {
        NovaException exception = assertThrows(NovaException.class, () -> parser.parse("what is this"));

        assertEquals("I don't recognize that command.", exception.getMessage());
    }
}
