package bogos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests for converting user commands into tasks and task numbers.
 */
public class ParserTest {
    /**
     * Verifies that all task-command prefixes are classified as task commands.
     */
    @Test
    public void isTaskCommand_taskCommands_returnsTrue() {
        Parser parser = new Parser();

        assertTrue(parser.isTaskCommand("todo read book"));
        assertTrue(parser.isTaskCommand("todo"));
        assertTrue(parser.isTaskCommand("deadline submit report /by 2026-09-15"));
        assertTrue(parser.isTaskCommand("deadline"));
        assertTrue(parser.isTaskCommand("event project meeting /from 2026-09-15 /to 2026-09-16"));
        assertTrue(parser.isTaskCommand("event"));
    }

    /**
     * Verifies that a non-task command is not classified as a task command.
     */
    @Test
    public void isTaskCommand_nonTaskCommand_returnsFalse() {
        Parser parser = new Parser();

        assertFalse(parser.isTaskCommand("list"));
    }

    /**
     * Verifies that a valid to-do command creates an incomplete to-do.
     *
     * @throws BogosException If the valid command cannot be parsed.
     */
    @Test
    public void parseTask_todo_success() throws BogosException {
        Parser parser = new Parser();

        Task task = parser.parseTask("todo CS3241 assignment");

        Todo todo = assertInstanceOf(Todo.class, task);
        assertEquals("CS3241 assignment", todo.getDescription());
        assertFalse(todo.isDone());
    }

    /**
     * Verifies that inline tags are extracted in input order and omitted from a to-do description.
     *
     * @throws BogosException If the valid command cannot be parsed.
     */
    @Test
    public void parseTask_todoWithTags_success() throws BogosException {
        Parser parser = new Parser();

        Todo todo = assertInstanceOf(Todo.class, parser.parseTask("todo watch movie #fun #weekend"));

        assertEquals("watch movie", todo.getDescription());
        assertEquals(List.of("fun", "weekend"), todo.getTags());
        assertEquals("[T][ ] watch movie #fun #weekend", todo.toString());
    }

    /**
     * Verifies that a valid deadline command creates an incomplete deadline.
     *
     * @throws BogosException If the valid command cannot be parsed.
     */
    @Test
    public void parseTask_deadline_success() throws BogosException {
        Parser parser = new Parser();

        Task task = parser.parseTask("deadline submit report /by 2026-09-15");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("submit report", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 9, 15), deadline.getDueDate());
        assertFalse(deadline.isDone());
    }

    /**
     * Verifies that tags can be appended after a deadline's date marker.
     *
     * @throws BogosException If the valid command cannot be parsed.
     */
    @Test
    public void parseTask_deadlineWithTrailingTags_success() throws BogosException {
        Parser parser = new Parser();

        Deadline deadline = assertInstanceOf(Deadline.class,
                parser.parseTask("deadline submit report /by 2026-09-15 #school"));

        assertEquals("submit report", deadline.getDescription());
        assertEquals(List.of("school"), deadline.getTags());
        assertEquals("[D][ ] submit report (by: Sep 15 2026) #school", deadline.toString());
    }

    /**
     * Verifies that a valid event command creates an incomplete event.
     *
     * @throws BogosException If the valid command cannot be parsed.
     */
    @Test
    public void parseTask_event_success() throws BogosException {
        Parser parser = new Parser();

        Task task = parser.parseTask("event project meeting /from 2026-09-15 /to 2026-09-16");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("project meeting", event.getDescription());
        assertEquals(LocalDate.of(2026, 9, 15), event.getStartDate());
        assertEquals(LocalDate.of(2026, 9, 16), event.getEndDate());
        assertFalse(event.isDone());
    }

    /**
     * Verifies that tags can be appended after an event's date markers.
     *
     * @throws BogosException If the valid command cannot be parsed.
     */
    @Test
    public void parseTask_eventWithTrailingTags_success() throws BogosException {
        Parser parser = new Parser();

        Event event = assertInstanceOf(Event.class,
                parser.parseTask("event project meeting /from 2026-09-15 /to 2026-09-16 #team #Fun"));

        assertEquals("project meeting", event.getDescription());
        assertEquals(List.of("team", "Fun"), event.getTags());
        assertEquals("[E][ ] project meeting (from: Sep 15 2026 to: Sep 16 2026) #team #Fun",
                event.toString());
    }

    /**
     * Verifies that event date markers may appear in either order.
     *
     * @throws BogosException If the valid command cannot be parsed.
     */
    @Test
    public void parseTask_eventWithReorderedDateMarkers_success() throws BogosException {
        Parser parser = new Parser();

        Event event = assertInstanceOf(Event.class,
                parser.parseTask("event submit report /to 2026-11-02 /from 2026-11-01"));

        assertEquals(LocalDate.of(2026, 11, 1), event.getStartDate());
        assertEquals(LocalDate.of(2026, 11, 2), event.getEndDate());
    }

    /**
     * Verifies that an unrecognised command produces a parser error.
     */
    @Test
    public void parseTask_nonsense_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("todooles homework"));

        assertEquals("Bwhat? Best browse: help", exception.getMessage());
    }

    /**
     * Verifies that a to-do command requires a description.
     */
    @Test
    public void parseTask_todoWithEmptyDesc_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("todo "));

        assertEquals("Bwhere body? Be: todo DESCRIPTION [#TAG]...", exception.getMessage());
    }

    /**
     * Verifies that a bare to-do command provides its required usage.
     */
    @Test
    public void parseTask_bareTodo_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("todo"));

        assertEquals("Bwhere body? Be: todo DESCRIPTION [#TAG]...", exception.getMessage());
    }

    /**
     * Verifies that a bare deadline command provides its required usage.
     */
    @Test
    public void parseTask_bareDeadline_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("deadline"));

        assertEquals("Bwhere body? Be: deadline DESCRIPTION /by YYYY-MM-DD [#TAG]...", exception.getMessage());
    }

    /**
     * Verifies that a bare event command provides its required usage.
     */
    @Test
    public void parseTask_bareEvent_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("event"));

        assertEquals("Bwhere body? Be: event DESCRIPTION /from YYYY-MM-DD /to YYYY-MM-DD [#TAG]...",
                exception.getMessage());
    }

    /**
     * Verifies that a tag without text produces a tag error.
     */
    @Test
    public void parseTask_todoWithBlankTag_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("todo watch movie #"));

        assertEquals("Bogus blank #badge. :[", exception.getMessage());
    }

    /**
     * Verifies that repeated case-sensitive tags produce a duplicate-tag error.
     */
    @Test
    public void parseTask_todoWithDuplicateTags_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("todo watch movie #fun #fun"));

        assertEquals("Bummer, buplicate #badge. :[", exception.getMessage());
    }

    /**
     * Verifies that ordinary text cannot follow a tag suffix.
     */
    @Test
    public void parseTask_todoWithTextAfterTag_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("todo spacetag #two words"));

        assertEquals("Bah! Unidentified Foreign Object: words", exception.getMessage());
    }

    /**
     * Verifies that differently capitalised tags are distinct.
     *
     * @throws BogosException If the valid command cannot be parsed.
     */
    @Test
    public void parseTask_todoWithDifferentlyCapitalisedTags_success() throws BogosException {
        Parser parser = new Parser();

        Todo todo = assertInstanceOf(Todo.class, parser.parseTask("todo watch movie #Fun #fun"));

        assertEquals(List.of("Fun", "fun"), todo.getTags());
    }

    /**
     * Verifies that removing tags cannot leave a blank task description.
     */
    @Test
    public void parseTask_todoWithOnlyTags_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("todo #fun #weekend"));

        assertEquals("Bwhere body? Be: todo DESCRIPTION [#TAG]...", exception.getMessage());
    }

    /**
     * Verifies that surrounding whitespace is removed from a to-do description.
     *
     * @throws BogosException If the valid command cannot be parsed.
     */
    @Test
    public void parseTask_todoWithLeadingTrailingWhitespace_trimmedDescription() throws BogosException {
        Parser parser = new Parser();

        Todo todo = assertInstanceOf(Todo.class, parser.parseTask("todo   revise  notes   "));

        assertEquals("revise  notes", todo.getDescription());
    }

    /**
     * Verifies that a deadline command requires its due-date marker.
     */
    @Test
    public void parseTask_deadlineWithNoBy_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("deadline submit report"));

        assertEquals("Bwhere /by? Be: deadline DESCRIPTION /by YYYY-MM-DD [#TAG]...", exception.getMessage());
    }

    /**
     * Verifies that a deadline command requires a due date.
     */
    @Test
    public void parseTask_deadlineWithEmptyBy_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("deadline submit report /by "));

        assertEquals("Bogus blank /by. :[", exception.getMessage());
    }

    /**
     * Verifies that a deadline command requires a description.
     */
    @Test
    public void parseTask_deadlineWithEmptyDesc_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("deadline  /by 2026-09-15"));

        assertEquals("Bwhere body? Be: deadline DESCRIPTION /by YYYY-MM-DD [#TAG]...", exception.getMessage());
    }

    /**
     * Verifies that a missing deadline description takes priority over an invalid due date.
     */
    @Test
    public void parseTask_deadlineWithEmptyDescAndInvalidDate_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("deadline /by nonsense"));

        assertEquals("Bwhere body? Be: deadline DESCRIPTION /by YYYY-MM-DD [#TAG]...", exception.getMessage());
    }

    /**
     * Verifies that a deadline date must use the ISO-8601 format.
     */
    @Test
    public void parseTask_deadlineWithInvalidByDate_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("deadline submit report /by 2026-02-30"));

        assertEquals("Bogus. Bring Bogos bona-fide YYYY-MM-DD. :[", exception.getMessage());
    }

    /**
     * Verifies that ordinary text cannot follow a deadline's due date.
     */
    @Test
    public void parseTask_deadlineWithTextAfterDate_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("deadline bridge /by 2026-02-02 bridge"));

        assertEquals("Bah! Unidentified Foreign Object: bridge", exception.getMessage());
    }

    /**
     * Verifies that a deadline command cannot specify its due-date parameter twice.
     */
    @Test
    public void parseTask_deadlineWithRepeatedBy_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("deadline submit report /by 2026-09-15 /by 2026-09-16"));

        assertEquals("Bummer, buplicate /by. :[", exception.getMessage());
    }

    /**
     * Verifies that an event command requires its start-date marker.
     */
    @Test
    public void parseTask_eventWithNoFrom_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("event project meeting /to 2026-09-16"));

        assertEquals("Bwhere /from? Be: event DESCRIPTION /from YYYY-MM-DD /to YYYY-MM-DD [#TAG]...",
                exception.getMessage());
    }

    /**
     * Verifies that an event command requires a start date.
     */
    @Test
    public void parseTask_eventWithEmptyFrom_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("event project meeting /from  /to 2026-09-16"));

        assertEquals("Bogus blank /from. :[", exception.getMessage());
    }

    /**
     * Verifies that a missing event description takes priority over date parameters.
     */
    @Test
    public void parseTask_eventWithEmptyDescAndDateParameters_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("event /from 2026-05-12 /to 2026-05-13"));

        assertEquals("Bwhere body? Be: event DESCRIPTION /from YYYY-MM-DD /to YYYY-MM-DD [#TAG]...",
                exception.getMessage());
    }

    /**
     * Verifies that an event command requires its end-date marker.
     */
    @Test
    public void parseTask_eventWithNoTo_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("event project meeting /from 2026-09-15"));

        assertEquals("Bwhere /to? Be: event DESCRIPTION /from YYYY-MM-DD /to YYYY-MM-DD [#TAG]...",
                exception.getMessage());
    }

    /**
     * Verifies that an event command requires an end date.
     */
    @Test
    public void parseTask_eventWithEmptyTo_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("event project meeting /from 2026-09-15 /to "));

        assertEquals("Bogus blank /to. :[", exception.getMessage());
    }

    /**
     * Verifies that an event cannot end before it starts.
     */
    @Test
    public void parseTask_eventWithEndDateBeforeStartDate_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("event project meeting /from 2026-09-16 /to 2026-09-15"));

        assertEquals("Bro be breathing backwards??", exception.getMessage());
    }

    /**
     * Verifies that reversed event dates are reported even when their markers are reordered.
     */
    @Test
    public void parseTask_eventWithReorderedMarkersAndReversedDates_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("event submit report /to 2026-11-01 /from 2026-11-02"));

        assertEquals("Bro be breathing backwards??", exception.getMessage());
    }

    /**
     * Verifies that an event cannot end on the same date on which it starts.
     */
    @Test
    public void parseTask_eventWithSameDates_success() throws BogosException {
        Parser parser = new Parser();

        Event event = assertInstanceOf(Event.class,
                parser.parseTask("event project meeting /from 2026-09-15 /to 2026-09-15"));

        assertEquals(event.getStartDate(), event.getEndDate());
    }

    /**
     * Verifies that an event start date must use the ISO-8601 format.
     */
    @Test
    public void parseTask_eventWithInvalidFromDate_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("event project meeting /from tomorrow /to 2026-09-16"));

        assertEquals("Bogus. Bring Bogos bona-fide YYYY-MM-DD. :[", exception.getMessage());
    }

    /**
     * Verifies that a missing separator between an event date and marker is reported as an invalid date.
     */
    @Test
    public void parseTask_eventWithDateAndMarkerWithoutSeparator_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("event hike /from 2026-05-12/to 2026-04-10"));

        assertEquals("Bogus. Bring Bogos bona-fide YYYY-MM-DD. :[", exception.getMessage());
    }

    /**
     * Verifies that ordinary text cannot follow an event date.
     */
    @Test
    public void parseTask_eventWithTextAfterDate_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("event meeting /from 2026-02-02 stray /to 2026-02-03"));

        assertEquals("Bah! Unidentified Foreign Object: stray", exception.getMessage());
    }

    /**
     * Verifies that all unsupported deadline markers receive the standard deadline usage error.
     */
    @Test
    public void parseTask_deadlineWithUnsupportedParameter_exceptionThrown() {
        Parser parser = new Parser();

        BogosException fromException = assertThrows(BogosException.class,
                () -> parser.parseTask("deadline submit report /by 2026-11-01 /from 2026-11-02"));
        BogosException unknownException = assertThrows(BogosException.class,
                () -> parser.parseTask("deadline submit report /hello 2026-11-02"));

        assertEquals("Bwhere /by? Be: deadline DESCRIPTION /by YYYY-MM-DD [#TAG]...",
                fromException.getMessage());
        assertEquals("Bwhere /by? Be: deadline DESCRIPTION /by YYYY-MM-DD [#TAG]...",
                unknownException.getMessage());
    }

    /**
     * Verifies that unsupported event markers receive the standard event usage error.
     */
    @Test
    public void parseTask_eventWithUnsupportedParameter_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("event submit report /from 2026-11-01 /by 2026-11-02 /to 2026-11-03"));

        assertEquals("Bwhere /from? Be: event DESCRIPTION /from YYYY-MM-DD /to YYYY-MM-DD [#TAG]...",
                exception.getMessage());
    }

    /**
     * Verifies that an event command cannot specify its start-date parameter twice.
     */
    @Test
    public void parseTask_eventWithRepeatedFrom_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("event project meeting /from 2026-09-15"
                        + " /from 2026-09-16 /to 2026-09-17"));

        assertEquals("Bummer, buplicate /from. :[", exception.getMessage());
    }

    /**
     * Verifies that an event command cannot specify its end-date parameter twice.
     */
    @Test
    public void parseTask_eventWithRepeatedTo_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTask("event project meeting /from 2026-09-15"
                        + " /to 2026-09-16 /to 2026-09-17"));

        assertEquals("Bummer, buplicate /to. :[", exception.getMessage());
    }

    /**
     * Verifies that a whole-number task position is parsed successfully.
     *
     * @throws BogosException If the valid task number cannot be parsed.
     */
    @Test
    public void parseTaskNumber_integer_success() throws BogosException {
        Parser parser = new Parser();

        int taskNumber = parser.parseTaskNumber("42");

        assertEquals(42, taskNumber);
    }

    /**
     * Verifies that a decimal task position produces a parser error.
     */
    @Test
    public void parseTaskNumber_double_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTaskNumber("1.5"));

        assertEquals("Bogus. Bring Bogos base-ten. :[", exception.getMessage());
    }

    /**
     * Verifies that a non-numeric task position produces a parser error.
     */
    @Test
    public void parseTaskNumber_alphabet_exceptionThrown() {
        Parser parser = new Parser();

        BogosException exception = assertThrows(BogosException.class,
                () -> parser.parseTaskNumber("one"));

        assertEquals("Bogus. Bring Bogos base-ten. :[", exception.getMessage());
    }

    /**
     * Verifies that command normalization removes surrounding and repeated whitespace.
     */
    @Test
    public void normalizeCommand_commandWithExtraWhitespace_returnsSingleSpacedCommand() {
        assertEquals("todo buy milk #errands", Bogos.normalizeCommand("  todo   buy   milk   #errands  "));
    }
}
