package zen.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import zen.ZenException;
import zen.task.Deadline;
import zen.task.Event;
import zen.task.Priority;
import zen.task.Todo;

/**
 * Tests parsing and validation of user command input.
 */
public class ParserTest {
    private static final String DEADLINE_FORMAT = "\nDeadline format: deadline <description> /by yyyy-MM-dd HH:mm:ss";
    private static final String EVENT_FORMAT = "\nEvent format: event <description> /from "
            + "yyyy-MM-dd HH:mm:ss /to yyyy-MM-dd HH:mm:ss";
    private static final String PRIORITY_FORMAT = "Please choose high, medium, or low for priority."
            + "\nPriority format: /priority <high|medium|low>";

    // AI-assisted
    @Test
    public void parse_knownCommandKeywords_returnsMatchingCommandTypes() {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(OccurCommand.class, Parser.parse("occur 2026-10-10"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(TodoCommand.class, Parser.parse("todo buy milk"));
        assertInstanceOf(DeadlineCommand.class, Parser.parse("deadline submit /by 2026-10-10 10:30:00"));
        assertInstanceOf(EventCommand.class,
                Parser.parse("event party /from 2026-10-10 10:30:00 /to 2026-10-10 11:30:00"));
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
    }

    // AI-assisted
    @Test
    public void parse_mixedCaseCommandWithExtraWhitespace_returnsMatchingCommandType() {
        assertInstanceOf(TodoCommand.class, Parser.parse("  ToDo   buy milk  "));
    }

    // AI-assisted
    @Test
    public void parse_emptyOrUnknownCommand_returnsUnknownCommand() {
        assertInstanceOf(UnknownCommand.class, Parser.parse(""));
        assertInstanceOf(UnknownCommand.class, Parser.parse("remind me"));
    }

    // AI-assisted
    @Test
    public void requireNoArguments_emptyArguments_doesNotThrow() throws ZenException {
        Parser.requireNoArguments("", "list");
    }

    // AI-assisted
    @Test
    public void requireNoArguments_nonEmptyArguments_throwsHelpfulException() {
        ZenException exception = assertThrows(ZenException.class, () -> Parser.requireNoArguments("today", "list"));

        assertEquals("The list command does not take arguments.", exception.getMessage());
    }

    // AI-assisted
    @Test
    public void parseDate_validIsoDate_returnsDate() throws ZenException {
        assertEquals(LocalDate.of(2026, 10, 10), Parser.parseDate("2026-10-10"));
    }

    // AI-assisted
    @Test
    public void parseDate_malformedOrImpossibleDate_throwsHelpfulException() {
        ZenException malformedException = assertThrows(ZenException.class, () -> Parser.parseDate("10-10-2026"));
        ZenException impossibleException = assertThrows(ZenException.class, () -> Parser.parseDate("2026-02-30"));

        String expectedMessage = "The date is not in the correct format.\nCorrect format: yyyy-MM-dd";
        assertEquals(expectedMessage, malformedException.getMessage());
        assertEquals(expectedMessage, impossibleException.getMessage());
    }

    // AI-assisted
    @Test
    public void parseTaskNumber_digitsOnly_returnsTaskNumber() throws ZenException {
        assertEquals(42, Parser.parseTaskNumber("42"));
    }

    // AI-assisted
    @Test
    public void parseTaskNumber_nonDigitInput_throwsHelpfulException() {
        for (String invalidInput : new String[] {"", "0", "-1", "1.5", "one", "999999999999999999999"}) {
            ZenException exception = assertThrows(ZenException.class, () -> Parser.parseTaskNumber(invalidInput));
            assertEquals("The task number must be a positive integer.", exception.getMessage());
        }
    }

    // AI-assisted
    @Test
    public void parseTodo_priorityValueIgnoresCase_returnsNormalizedPriority() throws ZenException {
        Todo todo = Parser.parseTodo("buy milk /priority HIGH");

        assertEquals(Priority.HIGH, todo.getPriority());
        assertEquals("T | 0 | buy milk | high", todo.toStorageString());
    }

    // AI-assisted
    @Test
    public void parseTodo_missingUnknownOrDuplicatePriority_throwsHelpfulException() {
        String[] invalidInputs = {
            "buy milk /priority",
            "buy milk /priority urgent",
            "buy milk /priority low /priority high"
        };
        for (String input : invalidInputs) {
            ZenException exception = assertThrows(ZenException.class, () -> Parser.parseTodo(input));
            assertEquals(PRIORITY_FORMAT, exception.getMessage());
        }
    }

    @Test
    public void parseTodo_pipeInDescription_throwsHelpfulException() {
        ZenException exception = assertThrows(ZenException.class, () -> Parser.parseTodo("buy | milk"));

        assertEquals("Task descriptions cannot contain the | character.", exception.getMessage());
    }

    // AI-assisted
    @Test
    public void parseDeadline_validInputWithWhitespace_returnsDeadline() throws ZenException {
        Deadline deadline = Parser.parseDeadline("  submit assignment  /by  2026-10-10 10:30:00  ");

        assertEquals("D | 0 | submit assignment | 2026-10-10T10:30 | none", deadline.toStorageString());
    }

    @Test
    public void parseDeadline_delimiterWithinDescriptionToken_returnsDeadline() throws ZenException {
        Deadline deadline = Parser.parseDeadline("check nus.edu/bylaws /by 2026-10-20 12:00:00");

        assertEquals("D | 0 | check nus.edu/bylaws | 2026-10-20T12:00 | none", deadline.toStorageString());
    }

    // AI-assisted
    @Test
    public void parseDeadline_terminalPriority_returnsPrioritizedDeadline() throws ZenException {
        Deadline deadline = Parser.parseDeadline("submit assignment /by 2026-10-10 10:30:00 /priority medium");

        assertEquals("D | 0 | submit assignment | 2026-10-10T10:30 | medium", deadline.toStorageString());
    }

    // AI-assisted
    @Test
    public void parseDeadline_missingMarker_throwsHelpfulException() {
        ZenException exception = assertThrows(ZenException.class, () -> Parser.parseDeadline("submit assignment"));

        assertEquals("Please include /by in your deadline to separate the description from the due date and time."
                + DEADLINE_FORMAT,
                exception.getMessage());
    }

    // AI-assisted
    @Test
    public void parseDeadline_duplicateMarker_throwsHelpfulException() {
        ZenException exception = assertThrows(ZenException.class, () ->
                Parser.parseDeadline("submit /by 2026-10-10 10:30:00 /by 2026-10-11 10:30:00"));

        assertEquals("Please include only one /by in your deadline to separate the description "
                + "from the due date and time."
                + DEADLINE_FORMAT,
                exception.getMessage());
    }

    // AI-assisted
    @Test
    public void parseDeadline_emptyDescriptionOrDueDate_throwsHelpfulException() {
        ZenException emptyDescription = assertThrows(ZenException.class, () ->
                Parser.parseDeadline(" /by 2026-10-10 10:30:00"));
        ZenException emptyDueDate = assertThrows(ZenException.class, () ->
                Parser.parseDeadline("submit /by  "));

        assertEquals("The deadline description cannot be empty. Please try again." + DEADLINE_FORMAT,
                emptyDescription.getMessage());
        assertEquals("The due date and time cannot be empty. Please try again." + DEADLINE_FORMAT,
                emptyDueDate.getMessage());
    }

    // AI-assisted
    @Test
    public void parseDeadline_invalidDateTime_throwsHelpfulException() {
        ZenException exception = assertThrows(ZenException.class, () -> Parser.parseDeadline("submit /by 2026-10-10"));

        assertEquals("The due date and time must follow the required format." + DEADLINE_FORMAT,
                exception.getMessage());
    }

    @Test
    public void parseDeadline_impossibleDate_throwsHelpfulException() {
        ZenException exception = assertThrows(ZenException.class, () ->
                Parser.parseDeadline("submit /by 2026-02-30 10:30:00"));

        assertEquals("The due date and time must follow the required format." + DEADLINE_FORMAT,
                exception.getMessage());
    }

    @Test
    public void parseDeadline_pipeInDescription_throwsHelpfulException() {
        ZenException exception = assertThrows(ZenException.class, () ->
                Parser.parseDeadline("submit | report /by 2026-10-10 10:30:00"));

        assertEquals("Task descriptions cannot contain the | character.", exception.getMessage());
    }

    // AI-assisted
    @Test
    public void parseEvent_validInputWithEqualStartAndEnd_returnsEvent() throws ZenException {
        Event event = Parser.parseEvent("  meeting  /from 2026-10-10 10:30:00 /to 2026-10-10 10:30:00  ");

        assertEquals("E | 0 | meeting | 2026-10-10T10:30 to 2026-10-10T10:30 | none", event.toStorageString());
    }

    @Test
    public void parseEvent_delimitersWithinDescriptionTokens_returnsEvent() throws ZenException {
        Event event = Parser.parseEvent("check /fromage nus.edu/today /from 2026-10-20 09:00:00 "
                + "/to 2026-10-20 10:00:00");

        assertEquals("E | 0 | check /fromage nus.edu/today | 2026-10-20T09:00 to 2026-10-20T10:00 | none",
                event.toStorageString());
    }

    @Test
    public void parseEvent_terminalPriority_returnsPrioritizedEvent() throws ZenException {
        Event event = Parser.parseEvent("meeting /from 2026-10-10 10:30:00 /to 2026-10-10 11:30:00 /priority low");

        assertEquals("E | 0 | meeting | 2026-10-10T10:30 to 2026-10-10T11:30 | low",
                event.toStorageString());
    }

    // AI-assisted
    @Test
    public void parseEvent_missingMarkersOrMarkersInWrongOrder_throwsHelpfulException() {
        ZenException missingFrom = assertThrows(ZenException.class, () ->
                Parser.parseEvent("meeting /to 2026-10-10 11:30:00"));
        ZenException missingTo = assertThrows(ZenException.class, () ->
                Parser.parseEvent("meeting /from 2026-10-10 10:30:00"));
        ZenException wrongOrder = assertThrows(ZenException.class, () ->
                Parser.parseEvent("meeting /to 2026-10-10 11:30:00 /from 2026-10-10 10:30:00"));

        assertEquals("Please include /from in your event to separate the description from the timings." + EVENT_FORMAT,
                missingFrom.getMessage());
        assertEquals("Please include /to in your event." + EVENT_FORMAT, missingTo.getMessage());
        assertEquals("/from must come before /to." + EVENT_FORMAT, wrongOrder.getMessage());
    }

    // AI-assisted
    @Test
    public void parseEvent_duplicateMarkers_throwsHelpfulException() {
        ZenException duplicateFrom = assertThrows(ZenException.class, () ->
                Parser.parseEvent("meeting /from 2026-10-10 10:30:00 /from 2026-10-10 11:00:00 "
                        + "/to 2026-10-10 11:30:00"));
        ZenException duplicateTo = assertThrows(ZenException.class, () ->
                Parser.parseEvent("meeting /from 2026-10-10 10:30:00 /to 2026-10-10 11:00:00 "
                        + "/to 2026-10-10 11:30:00"));

        assertEquals("Please include only one /from in your event." + EVENT_FORMAT, duplicateFrom.getMessage());
        assertEquals("Please include only one /to in your event." + EVENT_FORMAT, duplicateTo.getMessage());
    }

    // AI-assisted
    @Test
    public void parseEvent_emptyDescriptionStartOrEnd_throwsHelpfulException() {
        ZenException emptyDescription = assertThrows(ZenException.class, () ->
                Parser.parseEvent(" /from 2026-10-10 10:30:00 /to 2026-10-10 11:30:00"));
        ZenException emptyStart = assertThrows(ZenException.class, () ->
                Parser.parseEvent("meeting /from  /to 2026-10-10 11:30:00"));
        ZenException emptyEnd = assertThrows(ZenException.class, () ->
                Parser.parseEvent("meeting /from 2026-10-10 10:30:00 /to  "));

        assertEquals("The event description cannot be empty. Please try again." + EVENT_FORMAT,
                emptyDescription.getMessage());
        assertEquals("The event start time cannot be empty. Please try again." + EVENT_FORMAT,
                emptyStart.getMessage());
        assertEquals("The event end time cannot be empty. Please try again." + EVENT_FORMAT,
                emptyEnd.getMessage());
    }

    // AI-assisted
    @Test
    public void parseEvent_invalidDateTimeOrStartAfterEnd_throwsHelpfulException() {
        ZenException invalidDateTime = assertThrows(ZenException.class, () ->
                Parser.parseEvent("meeting /from not-a-date /to 2026-10-10 11:30:00"));
        ZenException startAfterEnd = assertThrows(ZenException.class, () ->
                Parser.parseEvent("meeting /from 2026-10-10 12:30:00 /to 2026-10-10 11:30:00"));

        assertEquals("The start and end date and time must follow the required format." + EVENT_FORMAT,
                invalidDateTime.getMessage());
        assertEquals("The start date and time is after the end date and time." + EVENT_FORMAT,
                startAfterEnd.getMessage());
    }

    @Test
    public void parseEvent_impossibleDateOrPipeInDescription_throwsHelpfulException() {
        ZenException impossibleDate = assertThrows(ZenException.class, () ->
                Parser.parseEvent("meeting /from 2026-02-30 10:30:00 /to 2026-03-01 11:30:00"));
        ZenException pipeDescription = assertThrows(ZenException.class, () ->
                Parser.parseEvent("meet | plan /from 2026-10-10 10:30:00 /to 2026-10-10 11:30:00"));

        assertEquals("The start and end date and time must follow the required format." + EVENT_FORMAT,
                impossibleDate.getMessage());
        assertEquals("Task descriptions cannot contain the | character.", pipeDescription.getMessage());
    }
}
