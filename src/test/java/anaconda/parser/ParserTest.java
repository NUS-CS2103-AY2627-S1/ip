package anaconda.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import anaconda.exception.AnacondaException;
import anaconda.task.Deadline;
import anaconda.task.Event;
import anaconda.task.ToDo;

/**
 * Tests command recognition, argument validation, and strict date parsing through the public parser API.
 */
public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    public void parseTask_pipeInDescription_rejectsEveryTaskType() {
        for (String description : new String[] {"read | book", "read|book", "|book", "book|", "|", "\"a | b\""}) {
            for (Command command : new Command[] {Command.TODO, Command.DEADLINE, Command.EVENT}) {
                String arguments = description + switch (command) {
                    case DEADLINE -> " /by 2026-09-20";
                    case EVENT -> " /from 2026-09-20 /to 2026-09-21";
                    default -> "";
                };
                AnacondaException exception = assertThrows(AnacondaException.class, () ->
                        parser.parseTask(command, arguments));
                assertEquals("Task descriptions cannot contain '|'. Please remove it and try again.",
                        exception.getMessage());
                assertEquals(AnacondaException.Reason.INVALID_INPUT, exception.getReason());
            }
        }
    }

    @Test
    public void parse_allCommandWords_returnsMatchingEnum() throws AnacondaException {
        for (Command command : Command.values()) {
            assertEquals(command, parser.parse(command.name().toLowerCase(Locale.ROOT)).command());
        }
        assertEquals(Command.BY, parser.parse("/by 2026-08-19").command());
        assertEquals(Command.FROM, parser.parse("/FrOm 2026-08-19").command());
    }

    @Test
    public void parse_mixedCaseAndWhitespace_preservesArgumentContent() throws AnacondaException {
        Parser.ParsedCommand parsed = parser.parse("  ToDo\t Read  Book  ");
        assertEquals(Command.TODO, parsed.command());
        assertEquals("Read  Book", parsed.arguments());
        assertEquals("", parser.parse(" LIST ").arguments());
    }

    @Test
    public void parse_emptyOrUnknownCommand_throwsHelpfulException() {
        for (String input : new String[] {"", " ", "\t"}) {
            assertEquals("Give me a command to work with.",
                    assertThrows(AnacondaException.class, () -> parser.parse(input)).getMessage());
        }
        for (String input : new String[] {"todos book", "unknown", "/before 2026-08-19"}) {
            assertEquals("Yeah... I don't recognize that command.",
                    assertThrows(AnacondaException.class, () -> parser.parse(input)).getMessage());
        }
    }

    @Test
    public void parse_unexpectedArguments_rejectsListHelpClearAndBye() {
        for (String command : new String[] {"list", "help", "clear"}) {
            assertEquals("The " + command + " command does not take extra text.",
                    assertThrows(AnacondaException.class, () ->
                            parser.parse(command + " extra")).getMessage());
        }
        assertEquals("The bye command cannot have extra text.",
                assertThrows(AnacondaException.class, () -> parser.parse("bye now")).getMessage());
    }

    @Test
    public void parse_undoCommands_acceptsCaseAndWhitespace() throws AnacondaException {
        assertEquals(new Parser.ParsedCommand(Command.UNDO, ""), parser.parse("  UnDo\t"));
        assertEquals(new Parser.ParsedCommand(Command.UNDO, "undo"), parser.parse("undo undo"));
        assertEquals(new Parser.ParsedCommand(Command.UNDO, "UnDo"), parser.parse("  UnDo\t  UnDo  "));
    }

    @Test
    public void parse_undoWithInvalidArguments_throwsHelpfulException() {
        for (String input : new String[] {"undo 1", "undo extra", "undo undo undo", "undo undo 1"}) {
            assertEquals("Use 'undo' or 'undo undo'.",
                    assertThrows(AnacondaException.class, () -> parser.parse(input)).getMessage(), input);
        }
    }

    @Test
    public void isExitCommand_onlyStandaloneBye_returnsTrue() {
        assertTrue(parser.isExitCommand(" BYe\t"));
        for (String input : new String[] {"", "bye now", "goodbye", "todo bye"}) {
            assertFalse(parser.isExitCommand(input), input);
        }
    }

    @Test
    public void parseTask_todo_preservesDescriptionAndStartsIncomplete() throws AnacondaException {
        ToDo task = assertInstanceOf(ToDo.class, parser.parseTask(Command.TODO, "Read  Book"));
        assertEquals("Read  Book", task.getDescription());
        assertFalse(task.isDone());
    }

    @Test
    public void parseTask_deadline_acceptsBothDateFormatsAndTrimsFields() throws AnacondaException {
        for (String date : new String[] {"2024-02-29", "29-02-2024"}) {
            Deadline task = assertInstanceOf(Deadline.class,
                    parser.parseTask(Command.DEADLINE, " return book  /by  " + date));
            assertEquals("return book", task.getDescription());
            assertEquals(LocalDate.of(2024, 2, 29), task.getBy());
            assertFalse(task.isDone());
        }
    }

    @Test
    public void parseTask_event_parsesSeparateStartAndEndDates() throws AnacondaException {
        Event task = assertInstanceOf(Event.class,
                parser.parseTask(Command.EVENT, " meeting  /from  2026-08-18 /to 19-08-2026 "));
        assertEquals("meeting", task.getDescription());
        assertEquals(LocalDate.of(2026, 8, 18), task.getFrom());
        assertEquals(LocalDate.of(2026, 8, 19), task.getTo());
        assertFalse(task.isDone());
    }

    @Test
    public void parseTask_eventStartAfterEnd_throwsHelpfulException() {
        String[] reversedRanges = {
            "2026-09-14 /to 2026-09-13",
            "14-09-2026 /to 13-09-2026",
            "2026-01-01 /to 31-12-2025",
            "01-03-2024 /to 2024-02-29"
        };
        for (String range : reversedRanges) {
            AnacondaException exception = assertThrows(AnacondaException.class, () ->
                    parser.parseTask(Command.EVENT, "meeting /from " + range));
            assertEquals("An event's start date cannot be later than its end date.", exception.getMessage());
            assertEquals(AnacondaException.Reason.INVALID_INPUT, exception.getReason());
        }
    }

    @Test
    public void parseTask_eventEqualOrIncreasingDates_acceptsRange() throws AnacondaException {
        String[] validRanges = {
            "2024-02-29 /to 29-02-2024",
            "29-02-2024 /to 2024-02-29",
            "29-02-2024 /to 2024-03-01",
            "2025-12-31 /to 01-01-2026"
        };
        for (String range : validRanges) {
            Event task = assertInstanceOf(Event.class,
                    parser.parseTask(Command.EVENT, "meeting /from " + range));
            assertFalse(task.getFrom().isAfter(task.getTo()), range);
        }
    }

    @Test
    public void parseTask_eventDescriptionContainsToMarker_usesToMarkerAfterFrom() throws AnacondaException {
        Event task = assertInstanceOf(Event.class,
                parser.parseTask(Command.EVENT, "explain /to syntax /from 2026-08-18 /to 2026-08-19"));
        assertEquals("explain /to syntax", task.getDescription());
        assertEquals(LocalDate.of(2026, 8, 18), task.getFrom());
        assertEquals(LocalDate.of(2026, 8, 19), task.getTo());
    }

    @Test
    public void parseTask_emptyDescriptions_throwsHelpfulException() {
        assertEquals("The description of a todo cannot be empty.",
                assertThrows(AnacondaException.class, () ->
                        parser.parseTask(Command.TODO, "")).getMessage());
        assertEquals("The description of a deadline cannot be empty.",
                assertThrows(AnacondaException.class, () ->
                        parser.parseTask(Command.DEADLINE, "/by 2026-08-19")).getMessage());
        assertEquals("The description of a event cannot be empty.",
                assertThrows(AnacondaException.class, () ->
                        parser.parseTask(Command.EVENT, "/from 2026-08-18 /to 2026-08-19")).getMessage());
    }

    @Test
    public void parseTask_missingDeadlineFields_throwsHelpfulException() {
        assertEquals("A deadline needs '/by' followed by a date or time.",
                assertThrows(AnacondaException.class, () ->
                        parser.parseTask(Command.DEADLINE, "book")).getMessage());
        assertEquals("A deadline needs a date or time after '/by'.",
                assertThrows(AnacondaException.class, () ->
                        parser.parseTask(Command.DEADLINE, "book /by ")).getMessage());
    }

    @Test
    public void parseTask_missingOrMisorderedEventMarkers_throwsException() {
        String[] argumentsWithMissingMarkers = {
            "meeting",
            "meeting /from 2026-08-18",
            "meeting /to 2026-08-19",
            "meeting /to 2026-08-19 /from 2026-08-18"
        };
        for (String arguments : argumentsWithMissingMarkers) {
            assertEquals("An event needs both '/from' and '/to' times.",
                    assertThrows(AnacondaException.class, () ->
                            parser.parseTask(Command.EVENT, arguments)).getMessage());
        }
    }

    @Test
    public void parseTask_emptyEventDates_throwsException() {
        String[] argumentsWithMissingDates = {
            "meeting /from /to 2026-08-19",
            "meeting /from/to 2026-08-19",
            "meeting /from 2026-08-18 /to",
            "meeting /from /to"
        };
        for (String arguments : argumentsWithMissingDates) {
            assertEquals("An event needs times after both '/from' and '/to'.",
                    assertThrows(AnacondaException.class, () ->
                            parser.parseTask(Command.EVENT, arguments)).getMessage());
        }
    }

    @Test
    public void parseTask_impossibleOrMalformedDates_rejectsEveryDateField() {
        String[] invalidDates = {
            "2026-02-30",
            "30-02-2026",
            "2024-02-30",
            "30-02-2024",
            "2023-02-29",
            "29-02-2023",
            "2100-02-29",
            "31-04-2026",
            "2026-13-01",
            "2026-00-01",
            "2026-08-00",
            "19/08/2026",
            "2026-8-19",
            "2026-08-19 1800",
            "Sunday"
        };
        for (String date : invalidDates) {
            assertThrows(AnacondaException.class, () ->
                    parser.parseTask(Command.DEADLINE, "book /by " + date));
            assertThrows(AnacondaException.class, () ->
                    parser.parseTask(Command.EVENT, "meeting /from " + date + " /to 2026-08-19"));
            assertThrows(AnacondaException.class, () ->
                    parser.parseTask(Command.EVENT, "meeting /from 2026-08-18 /to " + date));
        }
    }

    @Test
    public void parseTask_nonCreationCommand_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> parser.parseTask(Command.LIST, ""));
    }

    @Test
    public void parseTaskNumber_integerBoundaries_leavesRangeValidationToTaskList()
            throws AnacondaException {
        for (int number : new int[] {Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE}) {
            assertEquals(number, parser.parseTaskNumber(Integer.toString(number)));
        }
    }

    @Test
    public void parseTaskNumber_nonIntegerOrOverflow_throwsHelpfulException() {
        for (String input : new String[] {"", "two", "1 2", "1.0", "2147483648", "-2147483649"}) {
            assertEquals("I need one task number here.",
                    assertThrows(AnacondaException.class, () -> parser.parseTaskNumber(input)).getMessage());
        }
    }

    @Test
    public void parseKeyword_presentOrMissingKeyword_returnsKeywordOrThrowsHelpfulException()
            throws AnacondaException {
        assertEquals("Read  Book", parser.parseKeyword("Read  Book"));
        assertEquals("Give me a keyword to look for.",
                assertThrows(AnacondaException.class, () -> parser.parseKeyword("")).getMessage());
    }

    @Test
    public void parseDateFilter_validDatesAndModifiers_returnsParsedValues() throws AnacondaException {
        for (Command command : new Command[] {Command.BY, Command.FROM}) {
            for (String date : new String[] {"2026-08-19", "19-08-2026"}) {
                assertEquals(new Parser.DateFilter(LocalDate.of(2026, 8, 19), false),
                        parser.parseDateFilter(date, command));
                assertEquals(new Parser.DateFilter(LocalDate.of(2026, 8, 19), true),
                        parser.parseDateFilter(date + "\tSHARP", command));
            }
        }
    }

    @Test
    public void parseDateFilter_nonFilterCommand_throwsIllegalArgumentException() {
        for (Command command : Command.values()) {
            if (command == Command.BY || command == Command.FROM) {
                continue;
            }
            assertEquals("Command does not filter by date: " + command,
                    assertThrows(IllegalArgumentException.class, () ->
                            parser.parseDateFilter("2026-08-19", command)).getMessage());
        }
        assertThrows(IllegalArgumentException.class, () -> parser.parseDateFilter("2026-08-19", null));
    }

    @Test
    public void parseDateFilter_missingDateOrExtraWords_throwsSyntaxException() {
        for (Command command : new Command[] {Command.BY, Command.FROM}) {
            String word = command == Command.BY ? "/by" : "/from";
            for (String input : new String[] {"", "2026-08-19 now", "2026-08-19 sharp extra"}) {
                assertEquals("Use '" + word + " DATE' or '" + word + " DATE sharp'.",
                        assertThrows(AnacondaException.class, () ->
                                parser.parseDateFilter(input, command)).getMessage());
            }
        }
    }

    @Test
    public void parseDateFilter_invalidDate_throwsDateException() {
        for (Command command : new Command[] {Command.BY, Command.FROM}) {
            for (String input : new String[] {"2026-02-30", "30-02-2024 sharp", "2023-02-29",
                "29-02-2100", "31-04-2026 sharp", "tomorrow"}) {
                assertEquals("Please enter a valid calendar date in yyyy-MM-dd or dd-MM-yyyy format.",
                        assertThrows(AnacondaException.class, () ->
                                parser.parseDateFilter(input, command)).getMessage());
            }
        }
    }

    @Test
    public void parseTask_validLeapDays_acceptsCalendarDates() throws AnacondaException {
        for (String date : new String[] {"2000-02-29", "29-02-2000", "2024-02-29", "29-02-2024"}) {
            Deadline deadline = assertInstanceOf(Deadline.class,
                    parser.parseTask(Command.DEADLINE, "report /by " + date));
            Event event = assertInstanceOf(Event.class,
                    parser.parseTask(Command.EVENT, "meeting /from " + date + " /to " + date));
            assertEquals(29, deadline.getBy().getDayOfMonth());
            assertEquals(deadline.getBy(), event.getFrom());
            assertEquals(deadline.getBy(), event.getTo());
            for (Command command : new Command[] {Command.BY, Command.FROM}) {
                assertEquals(deadline.getBy(), parser.parseDateFilter(date, command).date());
            }
        }
    }
}
