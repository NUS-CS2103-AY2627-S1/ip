package glennon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import glennon.command.AddCommand;
import glennon.command.DeleteCommand;
import glennon.command.ExitCommand;
import glennon.command.FindCommand;
import glennon.command.ListCommand;
import glennon.command.MarkCommand;
import glennon.command.OnCommand;
import glennon.command.SortCommand;
import glennon.exception.GlennonException;
import glennon.task.Deadline;
import glennon.task.Event;
import glennon.task.TaskList;
import glennon.task.Todo;

/**
 * Tests Glennon's parsing and input-validation rules.
 */
class ParserTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void parseCommandType_validCommands_returnsMatchingTypes() {
        assertEquals(Parser.CommandType.BYE, Parser.parseCommandType("bye"));
        assertEquals(Parser.CommandType.LIST, Parser.parseCommandType("list"));
        assertEquals(Parser.CommandType.SORT, Parser.parseCommandType("sort"));
        assertEquals(Parser.CommandType.FIND, Parser.parseCommandType("find book"));
        assertEquals(Parser.CommandType.ON, Parser.parseCommandType("on 2/12/2019"));
        assertEquals(Parser.CommandType.MARK, Parser.parseCommandType("mark 1"));
        assertEquals(Parser.CommandType.UNMARK, Parser.parseCommandType("unmark 1"));
        assertEquals(Parser.CommandType.DELETE, Parser.parseCommandType("delete 1"));
        assertEquals(Parser.CommandType.TODO, Parser.parseCommandType("todo read book"));
        assertEquals(Parser.CommandType.DEADLINE,
                Parser.parseCommandType("deadline submit report /by 2/12/2019 1800"));
        assertEquals(Parser.CommandType.EVENT,
                Parser.parseCommandType("event meeting /from 2/12/2019 1400 /to 2/12/2019 1600"));
    }

    @Test
    void parseCommandType_keywordPrefixOrWrongCase_returnsUnknown() {
        assertEquals(Parser.CommandType.UNKNOWN, Parser.parseCommandType("listing"));
        assertEquals(Parser.CommandType.UNKNOWN, Parser.parseCommandType("Todo read book"));
        assertEquals(Parser.CommandType.UNKNOWN, Parser.parseCommandType("listings"));
    }

    @Test
    void parseCommandType_nonCommandDelimiters_returnsUnknown() {
        for (String separator : new String[] {"/", "-", "_", "\u2003", "\u00a0"}) {
            assertEquals(Parser.CommandType.UNKNOWN, Parser.parseCommandType("todo" + separator + "book"));
        }
    }

    @Test
    void parseCommandType_surroundingSpacesOrTabSeparator_returnsMatchingType() {
        assertEquals(Parser.CommandType.BYE, Parser.parseCommandType(" \tbye\t "));
        assertEquals(Parser.CommandType.TODO, Parser.parseCommandType("\ttodo\tread book\t"));
        assertEquals(Parser.CommandType.LIST, Parser.parseCommandType("list extra"));
    }

    @Test
    void parseCommandType_nullBlankOrMultiline_returnsUnknown() {
        assertEquals(Parser.CommandType.UNKNOWN, Parser.parseCommandType(null));
        assertEquals(Parser.CommandType.UNKNOWN, Parser.parseCommandType(" \t "));
        assertEquals(Parser.CommandType.UNKNOWN, Parser.parseCommandType("list\nbye"));
    }

    @Test
    void parseTodo_validDescription_returnsTodo() throws GlennonException {
        Todo todo = Parser.parseTodo("todo   read the JUnit guide   ");

        assertEquals("read the JUnit guide", todo.getDescription());
    }

    @Test
    void parseTodo_missingDescription_throwsHelpfulException() {
        GlennonException exception = assertThrows(
                GlennonException.class, () -> Parser.parseTodo("todo   "));

        assertEquals("Please enter a mission after todo.", exception.getMessage());
    }

    @Test
    void parseTodo_unicodeWhitespaceDescription_throwsHelpfulException() {
        for (String description : List.of("\u2003", "\u00a0", "\u202f", "\u2007",
                " \t\u00a0\u202f\u2007\u2003 ")) {
            GlennonException exception = assertThrows(
                    GlennonException.class, () -> Parser.parseTodo("todo " + description));

            assertEquals("Please enter a mission after todo.", exception.getMessage());
        }
    }

    @Test
    void parseTodo_unicodeSpacePadding_trimsOnlyDescriptionEdges() throws GlennonException {
        String expectedDescription = "🚀 学习\u00a0C++\u202f/\u2007Java  & café\tpractice";
        for (String padding : List.of("\u00a0", "\u202f", "\u2007", " \t\u00a0\u202f\u2007 ")) {
            Todo todo = Parser.parseTodo("todo " + padding + expectedDescription + padding);

            assertEquals(expectedDescription, todo.getDescription());
        }
    }

    @Test
    void parseTodo_punctuationAndUnicode_preservesDescription() throws GlennonException {
        Todo todo = Parser.parseTodo("  todo\t学习 C++/Java  & café | 🚀\tpractice  ");

        assertEquals("学习 C++/Java  & café | 🚀\tpractice", todo.getDescription());
    }

    @Test
    void parseKeyword_validKeyword_returnsTrimmedText() throws GlennonException {
        assertEquals("project book", Parser.parseKeyword("find   project book   "));
        assertEquals("project  book", Parser.parseKeyword(" \tfind\tproject  book \t"));
    }

    @Test
    void parseKeyword_missingKeyword_throwsHelpfulException() {
        GlennonException exception = assertThrows(
                GlennonException.class, () -> Parser.parseKeyword("find   "));

        assertEquals("Please enter a keyword after find.", exception.getMessage());
    }

    @Test
    void parseKeyword_flagWordsAndUnicode_preservesLiteralSearchText() throws GlennonException {
        assertEquals("学习 C++/Java /by  café 🚀",
                Parser.parseKeyword("\u2003find \u2003学习 C++/Java /by  café 🚀\u2003"));
    }

    @Test
    void parseDeadline_validLeapDay_returnsDeadline() throws GlennonException {
        Deadline deadline = Parser.parseDeadline("deadline celebrate /by 29/2/2024 0905");

        assertEquals("celebrate", deadline.getDescription());
        assertEquals(LocalDateTime.of(2024, 2, 29, 9, 5), deadline.getDueDateTime());
    }

    @Test
    void parseDeadline_dateWithoutTime_defaultsToEndOfDay() throws GlennonException {
        Deadline deadline = Parser.parseDeadline("deadline submit report /by 2/12/2019");

        assertEquals(LocalDateTime.of(2019, 12, 2, 23, 59), deadline.getDueDateTime());
    }

    @Test
    void parseDeadline_timeBoundariesAndLeapCentury_returnsExactDateTime() throws GlennonException {
        Deadline midnight = Parser.parseDeadline("deadline leap century /by 29/02/2000 0000");
        Deadline lastMinute = Parser.parseDeadline("deadline year end /by 31/12/2025 2359");

        assertEquals(LocalDateTime.of(2000, 2, 29, 0, 0), midnight.getDueDateTime());
        assertEquals(LocalDateTime.of(2025, 12, 31, 23, 59), lastMinute.getDueDateTime());
        assertFalse(midnight.isDone());
        assertFalse(lastMinute.isDone());
    }

    @Test
    void parseDeadline_extraSpacesAndTabs_parsesDateTimeAndPreservesDescription() throws GlennonException {
        Deadline deadline = Parser.parseDeadline(" \tdeadline\tsubmit  report\t/by\t2/12/2019  \t1800\t ");

        assertEquals("submit  report", deadline.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getDueDateTime());
    }

    @Test
    void parseDeadline_unicodeSpacePadding_trimsOnlyDescriptionEdges() throws GlennonException {
        String expectedDescription = "🚀 学习\u00a0C++\u202f/\u2007Java  & café\tpractice";
        for (String padding : List.of("\u00a0", "\u202f", "\u2007", " \t\u00a0\u202f\u2007 ")) {
            Deadline deadline = Parser.parseDeadline(
                    "deadline " + padding + expectedDescription + padding + " /by 2/12/2019 1800");

            assertEquals(expectedDescription, deadline.getDescription());
            assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getDueDateTime());
        }
    }

    @Test
    void parseDeadline_unicodeBlankDescription_throwsUsageException() {
        for (String description : List.of("\u00a0", "\u202f", "\u2007", " \t\u00a0\u202f\u2007 ")) {
            GlennonException exception = assertThrows(GlennonException.class, () -> Parser.parseDeadline(
                    "deadline " + description + " /by 2/12/2019 1800"));

            assertEquals("Use: deadline <mission> /by <d/M/yyyy [HHmm]>.", exception.getMessage());
        }
    }

    @Test
    void parseDeadline_slashesAndFlagPrefixes_preservesDescription() throws GlennonException {
        Deadline deadline = Parser.parseDeadline(
                "deadline learn C++/Java /bypass /fromage /today /by-design /by 2/12/2019");

        assertEquals("learn C++/Java /bypass /fromage /today /by-design", deadline.getDescription());
    }

    @Test
    void parseDeadline_missingSeparator_throwsUsageException() {
        GlennonException exception = assertThrows(
                GlennonException.class, () -> Parser.parseDeadline("deadline submit report 2/12/2019 1800"));

        assertEquals("Use: deadline <mission> /by <d/M/yyyy [HHmm]>.", exception.getMessage());
    }

    @Test
    void parseDeadline_missingDetailsOrMalformedSeparator_throwsUsageException() {
        List<String> inputs = List.of("deadline", "deadline /by 2/12/2019", "deadline report /by",
                "deadline report/by 2/12/2019", "deadline report /by2/12/2019");
        for (String input : inputs) {
            GlennonException exception = assertThrows(GlennonException.class, () -> Parser.parseDeadline(input));

            assertEquals("Use: deadline <mission> /by <d/M/yyyy [HHmm]>.", exception.getMessage(), input);
        }
    }

    @Test
    void parseDeadline_repeatedByParameter_throwsSpecificException() {
        GlennonException exception = assertThrows(GlennonException.class, () -> Parser.parseDeadline(
                "deadline report /by 2/12/2019 /by 3/12/2019"));

        assertEquals("Please specify /by only once.", exception.getMessage());
    }

    @Test
    void parseDeadline_eventParameters_throwsSpecificException() {
        for (String parameter : new String[] {"/from", "/to"}) {
            GlennonException exception = assertThrows(GlennonException.class, () -> Parser.parseDeadline(
                    "deadline report /by 2/12/2019 " + parameter + " 3/12/2019"));

            assertEquals("Unexpected parameter " + parameter
                            + ". Use: deadline <mission> /by <d/M/yyyy [HHmm]>.",
                    exception.getMessage());
        }
    }

    @Test
    void parseDeadline_impossibleDate_throwsDateTimeException() {
        GlennonException exception = assertThrows(
                GlennonException.class, () -> Parser.parseDeadline("deadline submit report /by 31/2/2025 1800"));

        assertEquals("Please enter a valid calendar date as d/M/yyyy with an optional HHmm time, "
                        + "for example 2/12/2019 or 2/12/2019 1800.",
                exception.getMessage());
    }

    @Test
    void parseDeadline_invalidTimeOrExtraDateTokens_throwsDateTimeException() {
        List<String> values = List.of("29/2/2023", "30/2/2024", "2/12/2019 2400", "2/12/2019 1860",
                "2/12/2019 1800 extra", "2/12/2019 18:00", "2/12/2019 /unknown 1800");
        for (String value : values) {
            GlennonException exception = assertThrows(GlennonException.class, () -> Parser.parseDeadline(
                    "deadline report /by " + value));

            assertEquals("Please enter a valid calendar date as d/M/yyyy with an optional HHmm time, "
                            + "for example 2/12/2019 or 2/12/2019 1800.",
                    exception.getMessage(), value);
        }
    }

    @Test
    void parseDeadline_invalidCalendarBoundaries_throwsDateTimeException() {
        for (String value : List.of("0/1/2025", "1/0/2025", "1/13/2025", "31/4/2025", "29/2/1900")) {
            GlennonException exception = assertThrows(
                    GlennonException.class, () -> Parser.parseDeadline("deadline report /by " + value));

            assertEquals("Please enter a valid calendar date as d/M/yyyy with an optional HHmm time, "
                            + "for example 2/12/2019 or 2/12/2019 1800.",
                    exception.getMessage(), value);
        }
    }

    @Test
    void parseEvent_multiDayRange_returnsEvent() throws GlennonException {
        Event event = Parser.parseEvent(
                "event hackathon /from 31/12/2025 2300 /to 1/1/2026 0100");

        assertEquals("hackathon", event.getDescription());
        assertEquals(LocalDateTime.of(2025, 12, 31, 23, 0), event.getStartDateTime());
        assertEquals(LocalDateTime.of(2026, 1, 1, 1, 0), event.getEndDateTime());
    }

    @Test
    void parseEvent_tabsAndRepeatedSpaces_preservesDescriptionAndParsesTimes() throws GlennonException {
        Event event = Parser.parseEvent(
                " \tevent\tC++/Java  学习\t/from\t2/12/2019 \t1400\t/to  2/12/2019   1600\t");

        assertEquals("C++/Java  学习", event.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 14, 0), event.getStartDateTime());
        assertEquals(LocalDateTime.of(2019, 12, 2, 16, 0), event.getEndDateTime());
    }

    @Test
    void parseEvent_unicodeSpacePadding_trimsOnlyDescriptionEdges() throws GlennonException {
        String expectedDescription = "🚀 学习\u00a0C++\u202f/\u2007Java  & café\tpractice";
        for (String padding : List.of("\u00a0", "\u202f", "\u2007", " \t\u00a0\u202f\u2007 ")) {
            Event event = Parser.parseEvent("event " + padding + expectedDescription + padding
                    + " /from 2/12/2019 1400 /to 2/12/2019 1600");

            assertEquals(expectedDescription, event.getDescription());
            assertEquals(LocalDateTime.of(2019, 12, 2, 14, 0), event.getStartDateTime());
            assertEquals(LocalDateTime.of(2019, 12, 2, 16, 0), event.getEndDateTime());
        }
    }

    @Test
    void parseEvent_unicodeBlankDescription_throwsUsageException() {
        for (String description : List.of("\u00a0", "\u202f", "\u2007", " \t\u00a0\u202f\u2007 ")) {
            GlennonException exception = assertThrows(GlennonException.class, () -> Parser.parseEvent(
                    "event " + description + " /from 2/12/2019 1400 /to 2/12/2019 1600"));

            assertEquals("Use: event <mission> /from <d/M/yyyy [HHmm]> /to <d/M/yyyy [HHmm]>.",
                    exception.getMessage());
        }
    }

    @Test
    void parseEvent_datesWithoutTimes_defaultsToAllDayBoundaries() throws GlennonException {
        Event event = Parser.parseEvent("event conference /from 2/12/2019 /to 3/12/2019");

        assertEquals(LocalDate.of(2019, 12, 2).atStartOfDay(), event.getStartDateTime());
        assertEquals(LocalDate.of(2019, 12, 3).atTime(LocalTime.MAX), event.getEndDateTime());
        assertEquals("[E][ ] conference (all day: Dec 2 2019 to: Dec 3 2019)", event.toString());
    }

    @Test
    void parseEvent_dateOnlyEnd_defaultsToEndOfDay() throws GlennonException {
        Event event = Parser.parseEvent(
                "event workshop /from 2/12/2019 1400 /to 2/12/2019");

        assertEquals(LocalDateTime.of(2019, 12, 2, 14, 0), event.getStartDateTime());
        assertEquals(LocalDate.of(2019, 12, 2).atTime(LocalTime.MAX), event.getEndDateTime());
    }

    @Test
    void parseEvent_dateOnlyStartAndExplicitEnd_defaultsToStartOfDay() throws GlennonException {
        Event event = Parser.parseEvent("event morning shift /from 2/12/2019 /to 2/12/2019 0900");

        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), event.getStartDateTime());
        assertEquals(LocalDateTime.of(2019, 12, 2, 9, 0), event.getEndDateTime());
        assertFalse(event.isDone());
    }

    @Test
    void parseEvent_oneMinuteAcrossMidnight_returnsEvent() throws GlennonException {
        Event event = Parser.parseEvent("event countdown /from 31/12/2025 2359 /to 1/1/2026 0000");

        assertEquals(LocalDateTime.of(2025, 12, 31, 23, 59), event.getStartDateTime());
        assertEquals(LocalDateTime.of(2026, 1, 1, 0, 0), event.getEndDateTime());
    }

    @Test
    void parseEvent_slashesAndFlagPrefixes_preservesDescription() throws GlennonException {
        Event event = Parser.parseEvent("event /fromage /today /bypass C++/Java "
                + "/from 2/12/2019 1400 /to 2/12/2019 1600");

        assertEquals("/fromage /today /bypass C++/Java", event.getDescription());
    }

    @Test
    void parseEvent_invalidStartOrEnd_throwsDateTimeException() {
        for (String value : List.of("29/2/2023", "2/12/2019 2400", "2/12/2019 9:00", "tomorrow")) {
            for (String input : List.of("event meeting /from " + value + " /to 3/12/2019",
                    "event meeting /from 1/12/2019 /to " + value)) {
                GlennonException exception = assertThrows(
                        GlennonException.class, () -> Parser.parseEvent(input));

                assertEquals("Please enter a valid calendar date as d/M/yyyy with an optional HHmm time, "
                                + "for example 2/12/2019 or 2/12/2019 1800.",
                        exception.getMessage(), input);
            }
        }
    }

    @Test
    void parseEvent_sameDateWithoutTimes_returnsAllDayEvent() throws GlennonException {
        Event event = Parser.parseEvent(
                "event checkpoint /from 2/12/2019 /to 2/12/2019");

        assertEquals(LocalDate.of(2019, 12, 2).atStartOfDay(), event.getStartDateTime());
        assertEquals(LocalDate.of(2019, 12, 2).atTime(LocalTime.MAX), event.getEndDateTime());
    }

    @Test
    void parseEvent_sameStartAndEnd_throwsOrderingException() {
        GlennonException exception = assertThrows(GlennonException.class, () -> Parser.parseEvent(
                "event checkpoint /from 2/12/2019 1800 /to 2/12/2019 1800"));

        assertEquals("The event end must be after its start.", exception.getMessage());
    }

    @Test
    void parseEvent_dateOnlyStartAndMidnightEnd_throwsOrderingException() {
        GlennonException exception = assertThrows(GlennonException.class, () -> Parser.parseEvent(
                "event checkpoint /from 2/12/2019 /to 2/12/2019 0000"));

        assertEquals("The event end must be after its start.", exception.getMessage());
    }

    @Test
    void parseEvent_endBeforeStart_throwsOrderingException() {
        GlennonException exception = assertThrows(GlennonException.class, () -> Parser.parseEvent(
                "event meeting /from 2/12/2019 1801 /to 2/12/2019 1800"));

        assertEquals("The event end must be after its start.", exception.getMessage());
    }

    @Test
    void parseEvent_missingEnd_throwsUsageException() {
        GlennonException exception = assertThrows(
                GlennonException.class, () -> Parser.parseEvent("event meeting /from 2/12/2019 1800 /to "));

        assertEquals("Use: event <mission> /from <d/M/yyyy [HHmm]> /to <d/M/yyyy [HHmm]>.",
                exception.getMessage());
    }

    @Test
    void parseEvent_missingOrReversedParameters_throwsUsageException() {
        List<String> inputs = List.of("event", "event /from 2/12/2019 /to 3/12/2019",
                "event meeting /from /to 3/12/2019", "event meeting /to 3/12/2019",
                "event meeting /from 2/12/2019", "event meeting /to 3/12/2019 /from 2/12/2019",
                "event meeting/from 2/12/2019 /to 3/12/2019",
                "event meeting /from 2/12/2019 /to3/12/2019");
        for (String input : inputs) {
            GlennonException exception = assertThrows(GlennonException.class, () -> Parser.parseEvent(input));

            assertEquals("Use: event <mission> /from <d/M/yyyy [HHmm]> /to <d/M/yyyy [HHmm]>.",
                    exception.getMessage(), input);
        }
    }

    @Test
    void parseEvent_repeatedScheduleParameters_throwsSpecificException() {
        for (String parameter : new String[] {"/from", "/to"}) {
            GlennonException exception = assertThrows(GlennonException.class, () -> Parser.parseEvent(
                    "event meeting /from 2/12/2019 /to 3/12/2019 " + parameter + " 4/12/2019"));

            assertEquals("Please specify " + parameter + " only once.", exception.getMessage());
        }
    }

    @Test
    void parseEvent_deadlineParameter_throwsSpecificException() {
        GlennonException exception = assertThrows(GlennonException.class, () -> Parser.parseEvent(
                "event meeting /from 2/12/2019 /to 3/12/2019 /by 4/12/2019"));

        assertEquals("Unexpected parameter /by. "
                        + "Use: event <mission> /from <d/M/yyyy [HHmm]> /to <d/M/yyyy [HHmm]>.",
                exception.getMessage());
    }

    @Test
    void parseMissionIndex_oneBasedNumber_returnsZeroBasedIndex() throws GlennonException {
        assertEquals(0, Parser.parseMissionIndex("mark 1", Parser.CommandType.MARK));
        assertEquals(41, Parser.parseMissionIndex("delete 42", Parser.CommandType.DELETE));
        assertEquals(0, Parser.parseMissionIndex(" \tunmark\t001\t ", Parser.CommandType.UNMARK));
        assertEquals(Integer.MAX_VALUE - 1,
                Parser.parseMissionIndex("mark 2147483647", Parser.CommandType.MARK));
    }

    @Test
    void parseMissionIndex_nonInteger_throwsHelpfulException() {
        GlennonException exception = assertThrows(
                GlennonException.class, () -> Parser.parseMissionIndex("mark first", Parser.CommandType.MARK));

        assertEquals("Please enter a valid mission number.", exception.getMessage());
    }

    @Test
    void parseMissionIndex_nonPositiveNonAsciiOrOverflow_throwsHelpfulException() {
        List<String> values = List.of("0", "-1", "-2147483648", "2147483648", "999999999999999999999",
                "+1", "1.0", "١", "１", "", "1 2", "1\t2");
        for (String value : values) {
            GlennonException exception = assertThrows(GlennonException.class, () -> Parser.parseMissionIndex(
                    "mark " + value, Parser.CommandType.MARK));

            assertEquals("Please enter a valid mission number.", exception.getMessage(), value);
        }
    }

    @Test
    void parseTodo_mismatchedCommand_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> Parser.parseTodo("find book"));
    }

    @Test
    void parseMissionIndex_commandWithoutArguments_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> Parser.parseMissionIndex(
                "list", Parser.CommandType.LIST));
    }

    @Test
    void parseDate_validLeapDay_returnsDate() throws GlennonException {
        assertEquals(LocalDate.of(2024, 2, 29), Parser.parseDate("on 29/2/2024"));
        assertEquals(LocalDate.of(2024, 2, 29), Parser.parseDate("\ton\t29/2/2024 \t"));
    }

    @Test
    void parseDate_invalidOrMalformedDate_throwsHelpfulException() {
        GlennonException impossibleDate = assertThrows(
                GlennonException.class, () -> Parser.parseDate("on 29/2/2023"));
        GlennonException wrongFormat = assertThrows(
                GlennonException.class, () -> Parser.parseDate("on 2024-02-29"));

        assertEquals("Please enter a valid calendar date as d/M/yyyy, for example 2/12/2019.",
                impossibleDate.getMessage());
        assertEquals(impossibleDate.getMessage(), wrongFormat.getMessage());
    }

    @Test
    void parseDate_timeOrTrailingArguments_throwsDateOnlyGuidance() {
        for (String value : List.of("2/12/2019 1800", "2/12/2019 extra", "2 /12/2019", "2/12/19")) {
            GlennonException exception = assertThrows(
                    GlennonException.class, () -> Parser.parseDate("on " + value));

            assertEquals("Please enter a valid calendar date as d/M/yyyy, for example 2/12/2019.",
                    exception.getMessage(), value);
        }
    }

    @Test
    void parse_taskCommands_returnsExecutableCommand() throws GlennonException {
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class, Parser.parse("deadline report /by 2/12/2019"));
        assertInstanceOf(AddCommand.class, Parser.parse("event meeting /from 2/12/2019 /to 3/12/2019"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(MarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(OnCommand.class, Parser.parse("on 2/12/2019"));
        assertInstanceOf(SortCommand.class, Parser.parse("sort"));
    }

    @Test
    void parse_scheduledCommands_bindsDescriptionsAndTimes() throws GlennonException {
        TaskList missions = new TaskList();
        Ui ui = new Ui(new PrintWriter(new StringWriter()));
        Storage storage = new Storage(temporaryDirectory.resolve("missions.txt"));

        Parser.parse("deadline submit  report /by 29/2/2024 0905").execute(missions, ui, storage);
        Parser.parse("event release /from 31/12/2025 2359 /to 1/1/2026 0000").execute(missions, ui, storage);

        Deadline deadline = assertInstanceOf(Deadline.class, missions.asList().get(0));
        Event event = assertInstanceOf(Event.class, missions.asList().get(1));
        assertEquals("submit  report", deadline.getDescription());
        assertEquals(LocalDateTime.of(2024, 2, 29, 9, 5), deadline.getDueDateTime());
        assertEquals("release", event.getDescription());
        assertEquals(LocalDateTime.of(2025, 12, 31, 23, 59), event.getStartDateTime());
        assertEquals(LocalDateTime.of(2026, 1, 1, 0, 0), event.getEndDateTime());
    }

    @Test
    void parse_statusAndDeleteCommands_bindsIndexAndCompletionFlag() throws GlennonException {
        Todo firstMission = new Todo("first");
        Todo secondMission = new Todo("second");
        TaskList missions = new TaskList(List.of(firstMission, secondMission));
        Ui ui = new Ui(new PrintWriter(new StringWriter()));
        Storage storage = new Storage(temporaryDirectory.resolve("missions.txt"));

        Parser.parse("mark 2").execute(missions, ui, storage);
        assertFalse(firstMission.isDone());
        assertTrue(secondMission.isDone());

        Parser.parse("unmark 2").execute(missions, ui, storage);
        assertFalse(firstMission.isDone());
        assertFalse(secondMission.isDone());

        Parser.parse("delete 1").execute(missions, ui, storage);
        assertEquals(1, missions.size());
        assertSame(secondMission, missions.asList().getFirst());
    }

    @Test
    void parse_missingRequiredArguments_throwsCommandSpecificGuidance() {
        List<String> inputs = List.of("todo", "find", "on", "mark", "unmark", "delete", "deadline", "event");
        List<String> messages = List.of("Please enter a mission after todo.",
                "Please enter a keyword after find.",
                "Please enter a valid calendar date as d/M/yyyy, for example 2/12/2019.",
                "Please enter a valid mission number.", "Please enter a valid mission number.",
                "Please enter a valid mission number.",
                "Use: deadline <mission> /by <d/M/yyyy [HHmm]>.",
                "Use: event <mission> /from <d/M/yyyy [HHmm]> /to <d/M/yyyy [HHmm]>.");

        for (int i = 0; i < inputs.size(); i++) {
            String input = inputs.get(i);
            GlennonException exception = assertThrows(GlennonException.class, () -> Parser.parse(input));

            assertEquals(messages.get(i), exception.getMessage(), input);
        }
    }

    @Test
    void parse_parameterlessCommandsWithSurroundingWhitespace_returnsCommand() throws GlennonException {
        assertInstanceOf(ListCommand.class, Parser.parse(" \tlist\t "));
        assertInstanceOf(SortCommand.class, Parser.parse("\tsort  "));
        assertInstanceOf(ExitCommand.class, Parser.parse(" bye\t"));
    }

    @Test
    void parse_parameterlessCommandsWithArguments_throwsSpecificException() {
        for (String keyword : new String[] {"list", "sort", "bye"}) {
            GlennonException exception = assertThrows(GlennonException.class, () -> Parser.parse(
                    "  " + keyword + "\textra  "));

            assertEquals("The " + keyword + " command does not take arguments. Use: " + keyword + ".",
                    exception.getMessage());
        }
    }

    @Test
    void parse_nullOrBlankInput_throwsHelpfulException() {
        for (String input : new String[] {null, "", " ", "\t", " \t  "}) {
            GlennonException exception = assertThrows(GlennonException.class, () -> Parser.parse(input));

            assertEquals("Please enter a command.", exception.getMessage());
        }
    }

    @Test
    void parse_multilineOrControlCharacters_throwsHelpfulException() {
        List<String> inputs = List.of("list\nbye", "\nlist", "list\r", "todo read\u0000book", "todo\u001B book",
                "todo read\u007Fbook", "todo read\u0085book", "todo read\u2028book", "todo read\u2029book");
        for (String input : inputs) {
            GlennonException exception = assertThrows(GlennonException.class, () -> Parser.parse(input));

            assertEquals("Please enter one command without control characters.", exception.getMessage());
        }
    }

    @Test
    void parse_isoControlCharactersExceptTab_rejectsEveryControlCharacter() {
        for (char character = 0; character <= '\u009f'; character++) {
            if (!Character.isISOControl(character) || character == '\t') {
                continue;
            }
            String input = "todo read" + character + "book";
            String context = "Control character " + (int) character;
            GlennonException exception = assertThrows(GlennonException.class, () -> Parser.parse(input), context);

            assertEquals("Please enter one command without control characters.", exception.getMessage(), context);
            assertEquals(Parser.CommandType.UNKNOWN, Parser.parseCommandType(input), context);
        }
    }

    @Test
    void parse_unknownCommand_throwsGuidanceException() {
        GlennonException exception = assertThrows(
                GlennonException.class, () -> Parser.parse("launch rocket"));

        assertEquals("Glennon doesn't recognize that command.\n"
                        + "Try: todo, deadline, event, list, sort, find, on, mark, "
                        + "unmark, delete, or bye.",
                exception.getMessage());
    }
}
