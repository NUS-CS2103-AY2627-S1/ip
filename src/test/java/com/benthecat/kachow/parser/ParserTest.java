package com.benthecat.kachow.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.benthecat.kachow.exception.KachowException;
import com.benthecat.kachow.task.Deadline;
import com.benthecat.kachow.task.Event;
import com.benthecat.kachow.task.Task;
import com.benthecat.kachow.task.Todo;

/**
 * Tests command validation and task construction described by the UI test cases.
 */
class ParserTest {
    private final Parser parser = new Parser();

    /** Verifies command parsing with surrounding whitespace, tabs, and a trimmed argument. */
    @Test
    void parse_whitespaceAroundCommand_returnsKeywordAndTrimmedArgument() throws KachowException {
        Parser.ParsedCommand parsed = parser.parse("  deadline\t return book /by 2019-12-02  ");

        assertEquals(Command.DEADLINE, parsed.command());
        assertEquals("return book /by 2019-12-02", parsed.argument());
    }

    @Test
    void parse_untrustedInput_rejectsControlsAndNormalizesUnicodeSpaces() throws KachowException {
        for (String input : List.of("todo one\ntodo two", "todo bad\rline", "find \u001b[2J",
                "todo hidden\u0000", "todo one\u2028two")) {
            assertThrows(KachowException.class, () -> parser.parse(input));
        }
        assertThrows(KachowException.class, () -> parser.parse(null));
        assertEquals("read book", parseTask("\u00a0todo\t read\u2003 book\u00a0").getDescription());
    }

    @Test
    void parseTask_unsafeDescriptionsAndEqualRanges_rejectsInvalidData() {
        for (String input : List.of("todo read | book", "todo read /by tomorrow",
                "deadline read /oops x /by 2026-09-10", "event read /oops x /from 2026-09-10 /to 2026-09-11",
                "event zero /from 2026-09-10 1000 /to 1000",
                "event zero /from 2026-09-10 /to 2026-09-10",
                "deadline invalid /by 2026-02-30", "deadline invalid /by 2026-09-10 24:00")) {
            assertThrows(KachowException.class, () -> parseTask(input), input);
        }
    }

    @Test
    void parseTaskNumber_signedUnicodeAndOverflowNumbers_rejectsInvalidTokens() {
        for (String number : List.of("+1", "-1", "0", "１", "١", "1.0", "999999999999999999", "1 2")) {
            assertThrows(KachowException.class, () -> parser.parseTaskNumber(parser.parse("mark " + number)));
            assertThrows(KachowException.class, () -> parser.parseEditCommand(
                    parser.parse("edit " + number + " /description changed")));
        }
    }

    /** Verifies that each task-creation command produces the correct task values. */
    @Test
    void parseTask_allTaskCommands_returnCorrectTaskTypesAndValues() throws KachowException {
        Task todo = parseTask("todo read book");
        Task deadline = parseTask("deadline return book /by 2/12/2019 1800");
        Task event = parseTask("event sprint planning /from 3/12/2019 0900 /to 10:30");

        assertInstanceOf(Todo.class, todo);
        assertEquals("[T][ ] read book", todo.getStatusText());
        assertInstanceOf(Deadline.class, deadline);
        assertEquals("[D][ ] return book (by: Dec 02 2019, 6:00 PM)", deadline.getStatusText());
        assertInstanceOf(Event.class, event);
        assertEquals("[E][ ] sprint planning (from: Dec 03 2019, 9:00 AM"
                + " to: Dec 03 2019, 10:30 AM)", event.getStatusText());
    }

    /** Verifies that an event with explicit start and end dates can span midnight. */
    @Test
    void parseTask_explicitOvernightEvent_preservesBothDates() throws KachowException {
        Event event = assertInstanceOf(Event.class,
                parseTask("event overnight /from 2024/01/02 2300 /to 2024/01/03 0100"));

        assertEquals(LocalDateTime.of(2024, 1, 2, 23, 0), event.getFrom().toLocalDateTime());
        assertEquals(LocalDateTime.of(2024, 1, 3, 1, 0), event.getTo().toLocalDateTime());
    }

    /** Verifies corrective guidance for empty and unsupported command keywords. */
    @Test
    void parse_emptyOrUnknownCommand_throwsSpecificGuidance() {
        KachowException empty = assertThrows(KachowException.class, () -> parser.parse("   "));
        KachowException unknown = assertThrows(KachowException.class, () -> parser.parse("dance"));

        assertEquals("That command stalled on the starting line. Enter a command to keep racing.",
                empty.getMessage());
        assertEquals("That command took a wrong turn. Try todo, deadline, event, list, find, on, mark,"
                + " unmark, edit, delete, or bye.", unknown.getMessage());
    }

    /** Verifies command-specific guidance when a no-argument command has extra text. */
    @Test
    void requireNoArgument_extraArgument_throwsCommandSpecificGuidance() throws KachowException {
        Parser.ParsedCommand list = parser.parse("list turbo");

        KachowException exception = assertThrows(KachowException.class, () ->
                parser.requireNoArgument(list));

        assertEquals("The list command has extra cargo. Use: list", exception.getMessage());
    }

    /** Verifies guidance for missing, misplaced, or repeated task syntax markers. */
    @Test
    void parseTask_malformedComponents_throwSpecificGuidance() {
        List<InvalidInput> testCases = List.of(
                new InvalidInput("todo", "This racer needs a name. Use: todo DESCRIPTION"),
                new InvalidInput("deadline return book",
                        "That deadline is missing its /by checkpoint."
                                + " Use: deadline DESCRIPTION /by DATE_OR_TIME"),
                new InvalidInput("deadline service /by",
                        "That deadline needs a date or time after /by."
                                + " Use: deadline DESCRIPTION /by DATE_OR_TIME"),
                new InvalidInput("deadline service /by Friday /by Monday",
                        "That deadline has too many /by checkpoints."
                                + " Use exactly one: deadline DESCRIPTION /by DATE_OR_TIME"),
                new InvalidInput("event meeting /to Tue /from Mon",
                        "That event's /from must come before /to."
                                + " Use: event DESCRIPTION /from START /to END"),
                new InvalidInput("event meeting /from Mon",
                        "That event is missing its /to finish line."
                                + " Use: event DESCRIPTION /from START /to END"),
                new InvalidInput("event meeting /from /to Tue",
                        "That event needs a start after /from."
                                + " Use: event DESCRIPTION /from START /to END"),
                new InvalidInput("event meeting /from Mon /to",
                        "That event needs an end after /to."
                                + " Use: event DESCRIPTION /from START /to END"),
                new InvalidInput("event meeting /from Mon /to Tue /to Wed",
                        "That event has extra route markers."
                                + " Use one /from and one /to: event DESCRIPTION /from START /to END"));

        testCases.forEach(this::assertInvalidTask);
    }

    /** Verifies guidance for invalid date/time values and reversed event ranges. */
    @Test
    void parseTask_invalidDateTimesAndRange_throwParameterSpecificGuidance() {
        List<InvalidInput> testCases = List.of(
                new InvalidInput("deadline invalid /by 31/02/2019",
                        "That deadline date or time is invalid. Use yyyy-MM-dd, yyyy/M/d, d/M/yyyy,"
                                + " or padded MM/dd/yyyy (US), optionally followed by HHmm, HH:mm,"
                                + " or an AM/PM time."),
                new InvalidInput("event invalid /from 31/02/2019 0900 /to 1000",
                        "That event start date or time is invalid. Use yyyy-MM-dd, yyyy/M/d, d/M/yyyy,"
                                + " or padded MM/dd/yyyy (US), optionally followed by HHmm, HH:mm,"
                                + " or an AM/PM time."),
                new InvalidInput("event invalid /from 3/12/2019 0900 /to tomorrow",
                        "That event end date or time is invalid. Use yyyy-MM-dd, yyyy/M/d, d/M/yyyy,"
                                + " or padded MM/dd/yyyy (US), optionally followed by HHmm, HH:mm,"
                                + " or an AM/PM time."),
                new InvalidInput("event backwards /from 2024-01-02 1800 /to 1700",
                        "That event must end after it starts. Use a full /to date for an overnight event."));

        testCases.forEach(this::assertInvalidTask);
    }

    /** Verifies parsing and validation of positive one-based task numbers. */
    @Test
    void parseTaskNumber_validAndInvalidArguments_followOneBasedRules() throws KachowException {
        assertEquals(2, parser.parseTaskNumber(parser.parse("delete 2")));

        List<String> invalidCommands = List.of("delete", "delete zero", "delete -1", "delete 1 turbo");
        for (String command : invalidCommands) {
            assertThrows(KachowException.class, () ->
                    parser.parseTaskNumber(parser.parse(command)));
        }
    }

    /** Verifies parsing of multiple edit fields in any order. */
    @Test
    void parseEditCommand_multipleFields_returnsNumberAndAllChanges() throws KachowException {
        Parser.EditCommand editCommand = parser.parseEditCommand(
                parser.parse("edit 3 /to 2026-08-06 1700 /description planning meeting /from 1300"));

        assertEquals(3, editCommand.taskNumber());
        assertEquals(3, editCommand.changes().size());
        assertEquals("2026-08-06 1700", editCommand.changes().get(Parser.EditField.TO));
        assertEquals("planning meeting", editCommand.changes().get(Parser.EditField.DESCRIPTION));
        assertEquals("1300", editCommand.changes().get(Parser.EditField.FROM));
    }

    /** Verifies that editing several event fields preserves completion status. */
    @Test
    void applyEdit_multipleEventFields_changesAllRequestedDetails() throws KachowException {
        Event original = new Event(
                "project meeting",
                new DateTimeParser.ParsedDateTime(LocalDateTime.of(2026, 8, 6, 14, 0)),
                new DateTimeParser.ParsedDateTime(LocalDateTime.of(2026, 8, 6, 16, 0)),
                true);
        Parser.EditCommand editCommand = parser.parseEditCommand(
                parser.parse("edit 1 /from 1300 /to 1700 /description planning meeting"));

        Event edited = assertInstanceOf(Event.class, parser.applyEdit(original, editCommand));

        assertEquals("planning meeting", edited.getDescription());
        assertEquals(LocalDateTime.of(2026, 8, 6, 13, 0), edited.getFrom().toLocalDateTime());
        assertEquals(LocalDateTime.of(2026, 8, 6, 17, 0), edited.getTo().toLocalDateTime());
        assertTrue(edited.isDone());
    }

    /** Verifies editable descriptions and deadline values across the remaining task types. */
    @Test
    void applyEdit_descriptionAndDeadlineDueDate_changeOnlySelectedFields() throws KachowException {
        Todo todo = new Todo("read book", true);
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 8, 6, 14, 0), true);

        Todo renamedTodo = assertInstanceOf(Todo.class,
                parser.applyEdit(todo, parser.parseEditCommand(parser.parse("edit 1 /description read novel"))));
        Deadline rescheduledDeadline = assertInstanceOf(Deadline.class, parser.applyEdit(deadline,
                parser.parseEditCommand(parser.parse("edit 2 /description final report /by 1800"))));

        assertEquals("read novel", renamedTodo.getDescription());
        assertTrue(renamedTodo.isDone());
        assertEquals("final report", rescheduledDeadline.getDescription());
        assertEquals(LocalDateTime.of(2026, 8, 6, 18, 0),
                rescheduledDeadline.getDueDateTime().toLocalDateTime());
        assertTrue(rescheduledDeadline.isDone());
    }

    /** Verifies edit-specific guidance and validation for malformed or inapplicable changes. */
    @Test
    void parseAndApplyEdit_invalidRequests_throwSpecificGuidance() throws KachowException {
        List<String> malformedCommands = List.of(
                "edit",
                "edit zero /description name",
                "edit 1",
                "edit 1 /unknown value",
                "edit 1 /description",
                "edit 1 /from 1300 /to");
        for (String command : malformedCommands) {
            assertThrows(KachowException.class, () -> parser.parseEditCommand(parser.parse(command)));
        }

        Todo todo = new Todo("read book");
        Event event = new Event(
                "meeting",
                new DateTimeParser.ParsedDateTime(LocalDateTime.of(2026, 8, 6, 14, 0)),
                new DateTimeParser.ParsedDateTime(LocalDateTime.of(2026, 8, 6, 16, 0)));

        KachowException duplicateField = assertThrows(KachowException.class, () ->
                parser.parseEditCommand(parser.parse("edit 1 /to 1700 /to 1800")));
        assertEquals("That edit repeats /to. Specify each detail once.", duplicateField.getMessage());

        KachowException unsupportedField = assertThrows(KachowException.class, () ->
                parser.applyEdit(todo, parser.parseEditCommand(
                        parser.parse("edit 1 /description renamed /to 1700"))));
        assertEquals("This racer does not have a /to detail.", unsupportedField.getMessage());
        assertEquals("read book", todo.getDescription());

        assertThrows(KachowException.class, () -> parser.applyEdit(event,
                parser.parseEditCommand(parser.parse(
                        "edit 1 /from 1800 /to 1700 /description invalid meeting"))));
        assertEquals("meeting", event.getDescription());
        assertEquals(LocalDateTime.of(2026, 8, 6, 14, 0), event.getFrom().toLocalDateTime());
        assertEquals(LocalDateTime.of(2026, 8, 6, 16, 0), event.getTo().toLocalDateTime());
    }

    /** Verifies that the on command accepts valid dates and rejects missing or timed values. */
    @Test
    void parseDate_validDateAndInvalidArguments_followOnCommandRules() throws KachowException {
        assertEquals(LocalDate.of(2019, 12, 3), parser.parseDate(parser.parse("on 12/03/2019")));

        List<String> invalidCommands = List.of("on", "on 2019-12-03 0900", "on tomorrow");
        for (String command : invalidCommands) {
            assertThrows(KachowException.class, () -> parser.parseDate(parser.parse(command)));
        }
    }

    @Test
    void parseSearchKeyword_presentAndMissingArguments_followFindCommandRules() throws KachowException {
        assertEquals("return book", parser.parseSearchKeyword(parser.parse("find return book")));

        KachowException exception = assertThrows(KachowException.class, () ->
                parser.parseSearchKeyword(parser.parse("find")));
        assertEquals("Tell me which racer to search for. Use: find KEYWORD", exception.getMessage());
    }

    @Test
    void parseTask_missingDescriptionsAndRepeatedStarts_returnsPreciseGuidance() {
        List<InvalidInput> cases = List.of(
                new InvalidInput("deadline", "This deadline racer needs a task description. "
                        + "Use: deadline DESCRIPTION /by DATE_OR_TIME"),
                new InvalidInput("deadline /by 2026-09-11", "This deadline racer needs a task description. "
                        + "Use: deadline DESCRIPTION /by DATE_OR_TIME"),
                new InvalidInput("event", "This event racer needs a description. "
                        + "Use: event DESCRIPTION /from START /to END"),
                new InvalidInput("event /from 2026-09-11 /to 2026-09-12", "This event racer needs a description. "
                        + "Use: event DESCRIPTION /from START /to END"),
                new InvalidInput("event meeting /to 2026-09-12", "That event is missing its /from starting line. "
                        + "Use: event DESCRIPTION /from START /to END"),
                new InvalidInput("event meeting /from 2026-09-11 /from 2026-09-11 /to 2026-09-12",
                        "That event has extra route markers. Use one /from and one /to: "
                                + "event DESCRIPTION /from START /to END"));
        cases.forEach(this::assertInvalidTask);
    }

    @Test
    void parseTask_markerSubstrings_recognizesOnlyCompleteSeparatedFields() throws KachowException {
        assertEquals("learn/by/example", parseTask("deadline learn/by/example /by 2026-09-11").getDescription());
        assertEquals("travel/from/home/to/work", parseTask(
                "event travel/from/home/to/work /from 2026-09-11 /to 2026-09-12").getDescription());
        for (String input : List.of("deadline read /by2026-09-11", "deadline read/by 2026-09-11",
                "event meeting /from2026-09-11 /to 2026-09-12", "event meeting /from 2026-09-11 /to2026-09-12")) {
            assertThrows(KachowException.class, () -> parseTask(input), input);
        }
    }

    @Test
    void requireNoArgument_emptyArgument_acceptsListAndBye() throws KachowException {
        parser.requireNoArgument(parser.parse("list"));
        parser.requireNoArgument(parser.parse(" bye "));
        assertEquals(Integer.MAX_VALUE, parser.parseTaskNumber(parser.parse("mark 2147483647")));
        assertEquals(1, parser.parseTaskNumber(parser.parse("unmark 0001")));
    }

    @Test
    void applyEdit_singleField_preservesUnmentionedDatesAndOriginalTask() throws KachowException {
        Deadline deadline = new Deadline("report", LocalDateTime.of(2026, 9, 11, 18, 0), true);
        Deadline renamed = assertInstanceOf(Deadline.class, parser.applyEdit(deadline,
                parser.parseEditCommand(parser.parse("edit 1 /description final report"))));
        assertNotSame(deadline, renamed);
        assertEquals(deadline.getDueDateTime(), renamed.getDueDateTime());
        assertEquals("report", deadline.getDescription());
        assertTrue(renamed.isDone());

        Event event = assertInstanceOf(Event.class,
                parseTask("event overnight /from 2026-09-11 2300 /to 2026-09-12 0100"));
        Event laterEnd = assertInstanceOf(Event.class, parser.applyEdit(event,
                parser.parseEditCommand(parser.parse("edit 1 /to 0200"))));
        assertEquals(event.getFrom(), laterEnd.getFrom());
        assertEquals(LocalDateTime.of(2026, 9, 12, 2, 0), laterEnd.getTo().toLocalDateTime());
        Event earlierStart = assertInstanceOf(Event.class, parser.applyEdit(event,
                parser.parseEditCommand(parser.parse("edit 1 /from 2200"))));
        assertEquals(event.getTo(), earlierStart.getTo());
        assertEquals(LocalDateTime.of(2026, 9, 11, 22, 0), earlierStart.getFrom().toLocalDateTime());
        assertEquals(LocalDateTime.of(2026, 9, 11, 23, 0), event.getFrom().toLocalDateTime());
        assertEquals(LocalDateTime.of(2026, 9, 12, 1, 0), event.getTo().toLocalDateTime());
    }

    @Test
    void applyEdit_invalidDatesAndUnsupportedFields_leavesEveryOriginalDetailIntact() throws KachowException {
        Deadline deadline = new Deadline("report", LocalDate.of(2026, 9, 11), true);
        Event event = assertInstanceOf(Event.class,
                parseTask("event meeting /from 2026-09-11 1000 /to 1100"));
        for (String input : List.of("edit 1 /by 2026-02-30", "edit 1 /by tomorrow", "edit 1 /from 0900")) {
            assertThrows(KachowException.class, () -> parser.applyEdit(deadline,
                    parser.parseEditCommand(parser.parse(input))), input);
        }
        for (String input : List.of("edit 1 /from tomorrow", "edit 1 /to 2460", "edit 1 /by 2026-09-11",
                "edit 1 /description renamed /to 1000")) {
            assertThrows(KachowException.class, () -> parser.applyEdit(event,
                    parser.parseEditCommand(parser.parse(input))), input);
        }
        assertEquals("[D][X] report (by: Sep 11 2026)", deadline.getStatusText());
        assertEquals("[E][ ] meeting (from: Sep 11 2026, 10:00 AM to: Sep 11 2026, 11:00 AM)",
                event.getStatusText());
    }

    @Test
    void parseEditCommand_emptyRepeatedAndUnknownFields_rejectsAllMalformedPairs() {
        for (String field : List.of("/description", "/by", "/from", "/to")) {
            for (String input : List.of("edit 1 " + field, "edit 1 " + field + " /to 1200",
                    "edit 1 " + field + " value " + field + " another")) {
                assertThrows(KachowException.class, () -> parser.parseEditCommand(parser.parse(input)), input);
            }
        }
        assertThrows(KachowException.class, () ->
                parser.parseEditCommand(parser.parse("edit 1 /description name /unknown value")));
        assertThrows(KachowException.class, () -> parser.parseEditCommand(parser.parse("edit 1 description name")));
    }

    @Test
    void editCommand_sourceMapAndReturnedMap_cannotMutateParsedRequest() throws KachowException {
        Map<Parser.EditField, String> source = new EnumMap<>(Parser.EditField.class);
        source.put(Parser.EditField.DESCRIPTION, "original");
        Parser.EditCommand command = new Parser.EditCommand(1, source);
        source.put(Parser.EditField.DESCRIPTION, "changed");
        assertEquals(Map.of(Parser.EditField.DESCRIPTION, "original"), command.changes());
        assertThrows(UnsupportedOperationException.class, () -> command.changes().clear());
        Parser.EditCommand parsed = parser.parseEditCommand(parser.parse("edit 1 /description C++/Java"));
        assertEquals("C++/Java", parsed.changes().get(Parser.EditField.DESCRIPTION));
    }

    /**
     * Parses a complete task command for use by parser tests.
     *
     * @param input Complete command text.
     * @return Task produced by the parser.
     * @throws KachowException If the command or task details are invalid.
     */
    private Task parseTask(String input) throws KachowException {
        return parser.parseTask(parser.parse(input));
    }

    /**
     * Checks the exact guidance for one malformed task command.
     *
     * @param testCase Malformed input and its expected message.
     */
    private void assertInvalidTask(InvalidInput testCase) {
        KachowException exception = assertThrows(KachowException.class, () ->
                parseTask(testCase.input()));
        assertEquals(testCase.expectedMessage(), exception.getMessage());
    }

    /** Associates malformed command input with the exact corrective guidance shown by the UI. */
    private record InvalidInput(String input, String expectedMessage) { }
}
