package cookie.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import cookie.task.DateTimeValue;
import cookie.task.SortCriterion;
import cookie.task.SortDirection;

/** Tests command parsing, validation, and date/time conversion. */
public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    public void parse_commandWithWhitespace_preservesDescriptionAndArguments()
            throws CookieException {
        Parser.ParsedCommand parsed = parser.parse("  ToDo   buy   milk  ");

        assertEquals(Command.TODO, parsed.command());
        assertEquals("ToDo", parsed.action());
        assertEquals(2, parsed.argumentCount());
        assertEquals(List.of("buy", "milk"), parsed.arguments());
        assertEquals("buy", parsed.argument(0));
        assertEquals("milk", parsed.argument(1));
        assertEquals("buy milk", parsed.description());
    }

    @Test
    public void parse_blankInput_throwsCookieException() {
        CookieException exception = assertThrows(CookieException.class, () -> parser.parse(" \t  "));
        assertEquals("I couldn't understand an empty command.", exception.getMessage());
        assertEquals("I couldn't understand an empty command.",
                assertThrows(CookieException.class, () -> parser.parse(null)).getMessage());
    }

    @Test
    public void validationMethods_acceptAndRejectExpectedArguments() throws CookieException {
        Parser.ParsedCommand noArguments = parser.parse("list");
        Parser.ParsedCommand oneArgument = parser.parse("on 2026-08-27");
        Parser.ParsedCommand findKeyword = parser.parse("find book");
        Parser.ParsedCommand manyArguments = parser.parse("list now");

        parser.requireNoArguments(noArguments);
        parser.requireSingleArgument(oneArgument, "on <date>");
        parser.requireSingleArgument(findKeyword, "find <keyword>");
        assertEquals("2026-08-27", parser.requireDescription(parser.parse("todo 2026-08-27")));
        assertEquals("safe", parser.requireFileSafe("safe"));

        assertEquals("The list command does not take any arguments.", assertThrows(
                CookieException.class, () -> parser.requireNoArguments(manyArguments)).getMessage());
        Parser.ParsedCommand tooManyArguments = parser.parse("on 2026-08-27 extra");
        assertEquals("Usage: on <date>.", assertThrows(
                CookieException.class, () -> parser.requireSingleArgument(tooManyArguments,
                        "on <date>")).getMessage());
        assertEquals("Usage: find <keyword>.", assertThrows(
                CookieException.class, () -> parser.requireSingleArgument(
                        parser.parse("find book now"), "find <keyword>")).getMessage());
        assertEquals("A todo task needs a description.", assertThrows(
                CookieException.class, () -> parser.requireDescription(parser.parse("todo")))
                .getMessage());
        assertEquals("Task details cannot contain '|'.", assertThrows(
                CookieException.class, () -> parser.requireFileSafe("unsafe|value")).getMessage());
        assertEquals("Task details cannot contain control characters.", assertThrows(
                CookieException.class, () -> parser.requireFileSafe("unsafe\u0000value"))
                .getMessage());
    }

    @Test
    public void parseDateTime_supportedFormats_returnsCorrectComponents() throws CookieException {
        DateTimeValue isoDateTime = parser.parseDateTime(" 2026-08-27   0915 ");
        assertEquals(LocalDate.of(2026, 8, 27), isoDateTime.getDate());
        assertEquals(LocalTime.of(9, 15), isoDateTime.getTime());

        DateTimeValue slashDateTime = parser.parseDateTime("27/8/2026 0915");
        assertEquals(LocalDate.of(2026, 8, 27), slashDateTime.getDate());
        assertEquals(LocalTime.of(9, 15), slashDateTime.getTime());

        DateTimeValue dateOnly = parser.parseDateTime("2026-08-27");
        assertEquals(LocalDate.of(2026, 8, 27), dateOnly.getDate());
        assertNull(dateOnly.getTime());

        DateTimeValue timeOnly = parser.parseDateTime("0915");
        assertNull(timeOnly.getDate());
        assertEquals(LocalTime.of(9, 15), timeOnly.getTime());
    }

    @Test
    public void parseDateTime_invalidValues_throwsCookieException() {
        for (String invalidValue : new String[] {"2026-02-30", "2460", "2026/08/27 0915"}) {
            CookieException exception = assertThrows(CookieException.class, () -> parser.parseDateTime(invalidValue));
            assertEquals("A date, time, or date and time must use yyyy-MM-dd, d/M/yyyy, HHmm, "
                    + "yyyy-MM-dd HHmm, or d/M/yyyy HHmm.", exception.getMessage());
        }
    }

    @Test
    public void parseDate_supportedFormats_returnsCorrectDates() throws CookieException {
        assertEquals(LocalDate.of(2026, 8, 27), parser.parseDate("2026-08-27"));
        assertEquals(LocalDate.of(2026, 8, 27), parser.parseDate("27/8/2026"));
    }

    @Test
    public void parseDate_invalidValue_throwsCookieException() {
        CookieException exception = assertThrows(CookieException.class, () -> parser.parseDate("2026-02-30"));
        assertEquals("A date must use yyyy-MM-dd or d/M/yyyy.", exception.getMessage());
    }

    @Test
    public void parseSort_supportedArguments_returnsCriterionAndDirection() throws CookieException {
        Parser.ParsedSort defaultDirection = parser.parseSort(parser.parse("sort description"));
        Parser.ParsedSort explicitDirection = parser.parseSort(parser.parse("sort DATE DeSc"));
        Parser.ParsedSort explicitAscending = parser.parseSort(parser.parse("sort date ASC"));

        assertEquals(SortCriterion.DESCRIPTION, defaultDirection.criterion());
        assertEquals(SortDirection.ASCENDING, defaultDirection.direction());
        assertEquals(SortCriterion.DATE, explicitDirection.criterion());
        assertEquals(SortDirection.DESCENDING, explicitDirection.direction());
        assertEquals(SortDirection.ASCENDING, explicitAscending.direction());
    }

    @Test
    public void parseSort_invalidArguments_throwsUsageError() {
        for (String invalidCommand : new String[] {
            "sort", "sort status", "sort date descending", "sort date asc extra"
        }) {
            CookieException exception = assertThrows(CookieException.class, () ->
                    parser.parseSort(parser.parse(invalidCommand)));
            assertEquals("Usage: sort <description|date> [asc|desc].", exception.getMessage());
        }
    }

    @Test
    public void parseDeadline_validInput_returnsDescriptionAndDateTime() throws CookieException {
        Parser.ParsedDeadline parsed = parser.parseDeadline(
                "submit report /by 27/8/2026 0915");

        assertEquals("submit report", parsed.description());
        assertEquals(LocalDate.of(2026, 8, 27), parsed.dateTime().getDate());
        assertEquals(LocalTime.of(9, 15), parsed.dateTime().getTime());
    }

    @Test
    public void parseDeadline_missingOrInvalidFields_throwsCookieException() {
        String structureError = "A deadline needs a description and a date and time after /by.";
        assertEquals(structureError,
                assertThrows(CookieException.class, () ->
                        parser.parseDeadline("submit report")).getMessage());
        assertEquals(structureError,
                assertThrows(CookieException.class, () ->
                        parser.parseDeadline(" /by 2026-08-27")).getMessage());

        String valueError = "A deadline date, time, or date and time must use yyyy-MM-dd, d/M/yyyy, "
                + "HHmm, yyyy-MM-dd HHmm, or d/M/yyyy HHmm.";
        assertEquals(valueError,
                assertThrows(CookieException.class, () ->
                        parser.parseDeadline("submit report /by 2026-02-30")).getMessage());
        assertEquals("Task details cannot contain '|'.",
                assertThrows(CookieException.class, () ->
                        parser.parseDeadline("unsafe|task /by 2026-08-27")).getMessage());
        assertEquals("A deadline must contain exactly one /by parameter.",
                assertThrows(CookieException.class, () -> parser.parseDeadline(
                        "submit report /by 2026-08-27 /by 2026-08-28")).getMessage());
    }

    @Test
    public void parseEvent_validInput_returnsDescriptionAndDateTimes() throws CookieException {
        Parser.ParsedEvent parsed = parser.parseEvent(
                "project meeting /from 27/8/2026 0900 /to 27/8/2026 1030");

        assertEquals("project meeting", parsed.description());
        assertEquals(LocalTime.of(9, 0), parsed.start().getTime());
        assertEquals(LocalTime.of(10, 30), parsed.end().getTime());
    }

    @Test
    public void parseEvent_missingOrInvalidFields_throwsCookieException() {
        String structureError = "An event needs a description, a start time after /from, "
                + "and an end time after /to.";
        for (String invalid : new String[] {
            "project meeting", " /from 0900 /to 1000", "project meeting /from /to 1000",
            "project meeting /from 0900 /to "
        }) {
            assertEquals(structureError,
                    assertThrows(CookieException.class, () ->
                            parser.parseEvent(invalid)).getMessage());
        }

        String valueError = "An event's start and end values must use yyyy-MM-dd, d/M/yyyy, HHmm, "
                + "yyyy-MM-dd HHmm, or d/M/yyyy HHmm.";
        assertEquals(valueError,
                assertThrows(CookieException.class, () ->
                        parser.parseEvent("project meeting /from 2460 /to 1000")).getMessage());
        assertEquals("Task details cannot contain '|'.",
                assertThrows(CookieException.class, () ->
                        parser.parseEvent("unsafe|event /from 0900 /to 1000")).getMessage());
        assertEquals("An event must contain exactly one /from and one /to parameter.",
                assertThrows(CookieException.class, () -> parser.parseEvent(
                        "meeting /from 0900 /from 0930 /to 1000")).getMessage());
        assertEquals("An event must contain exactly one /from and one /to parameter.",
                assertThrows(CookieException.class, () -> parser.parseEvent(
                        "meeting /from 0900 /to 0930 /to 1000")).getMessage());
    }

    @Test
    public void parseEvent_invalidPeriod_throwsSpecificCookieException() {
        assertEquals("An event's end must be later than its start.",
                assertThrows(CookieException.class, () ->
                        parser.parseEvent("meeting /from 1000 /to 1000")).getMessage());
        assertEquals("An event's end must be later than its start.",
                assertThrows(CookieException.class, () ->
                        parser.parseEvent("meeting /from 1100 /to 1000")).getMessage());
        assertEquals("An event's start and end must both be dates, both be times, "
                        + "or both be dates and times.",
                assertThrows(CookieException.class, () -> parser.parseEvent(
                        "meeting /from 2026-08-27 /to 1000")).getMessage());
    }

    @Test
    public void parseTaskIndex_validAndInvalidNumbers_behavesCorrectly() throws CookieException {
        assertEquals(0, parser.parseTaskIndex(parser.parse("mark 1"), 3));
        assertEquals(2, parser.parseTaskIndex(parser.parse("mark 3"), 3));
        assertThrows(AssertionError.class, () ->
                parser.parseTaskIndex(parser.parse("mark 1"), -1));

        assertEquals("Usage: mark <task number>.",
                assertThrows(CookieException.class, () ->
                        parser.parseTaskIndex(parser.parse("mark"), 3)).getMessage());
        assertEquals("The task number must be a positive whole number.", assertThrows(
                CookieException.class, () -> parser.parseTaskIndex(parser.parse("mark abc"), 3))
                .getMessage());
        for (int invalidNumber : new int[] {0, -1, 4}) {
            assertEquals("There is no task numbered " + invalidNumber + ".",
                    assertThrows(CookieException.class, () -> parser.parseTaskIndex(
                            parser.parse("mark " + invalidNumber), 3)).getMessage());
        }
        assertEquals("Usage: mark <task number>.",
                assertThrows(CookieException.class, () ->
                        parser.parseTaskIndex(parser.parse("mark 1 extra"), 3)).getMessage());
    }
}
