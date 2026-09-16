package kibo.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import kibo.exception.InvalidCommandException;
import kibo.exception.KiboException;
import kibo.task.Deadline;
import kibo.task.Event;
import kibo.task.Task;

/**
 * Tests command parsing that can be checked without running the command-line UI.
 */
public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseCommandType_allKeywordsAndWhitespace_recognizesWholeCommands() throws Exception {
        for (CommandType command : CommandType.values()) {
            if (command != CommandType.UNKNOWN) {
                assertEquals(command, parser.parseCommandType(command.getKeyword()));
                assertEquals(command, parser.parseCommandType(command.getKeyword() + "\targument"));
                assertEquals(CommandType.UNKNOWN, CommandType.fromInput(command.getKeyword() + "suffix"));
            }
        }
    }

    @Test
    void parseCommandType_emptyUnknownOrDifferentCase_rejectsCommand() {
        for (String input : new String[]{"", " ", "blah", "TODO read book", "listing"}) {
            assertThrows(InvalidCommandException.class, () -> parser.parseCommandType(input), input);
        }
    }

    @Test
    void ensureNoArguments_exactAndExtraText_validatesListAndBye() {
        for (CommandType command : new CommandType[]{CommandType.LIST, CommandType.BYE}) {
            assertDoesNotThrow(() -> parser.ensureNoArguments(command.getKeyword(), command));
            assertThrows(InvalidCommandException.class, () ->
                    parser.ensureNoArguments(command.getKeyword() + " extra", command));
        }
    }

    @Test
    void parseTodo_paddedUnicodeDescription_preservesTextAndTrimsEdges() throws Exception {
        assertEquals("阅读  book", parser.parseTodo("todo   阅读  book   ").getDescription());
        assertThrows(InvalidCommandException.class, () -> parser.parseTodo("todo"));
        assertThrows(InvalidCommandException.class, () -> parser.parseTodo("todo   "));
    }

    @Test
    void parseTaskIndex_firstAndLastTask_returnsZeroBasedIndex() throws Exception {
        for (CommandType command : new CommandType[]{CommandType.MARK, CommandType.UNMARK, CommandType.DELETE}) {
            assertEquals(0, parser.parseTaskIndex(command.getKeyword() + "  1  ", command, 3));
            assertEquals(2, parser.parseTaskIndex(command.getKeyword() + " 3", command, 3));
        }
    }

    @Test
    void parseTaskIndex_invalidNumbers_throwsHelpfulException() {
        for (String number : new String[]{"", "one", "1.5", "1 2", "2147483648"}) {
            assertThrows(InvalidCommandException.class, () ->
                    parser.parseTaskIndex("mark " + number, CommandType.MARK, 3), number);
        }
        for (String number : new String[]{"0", "-1", "4"}) {
            KiboException exception = assertThrows(KiboException.class, () ->
                    parser.parseTaskIndex("delete " + number, CommandType.DELETE, 3));
            assertEquals("Task " + number + " does not exist in your list.", exception.getMessage());
        }
        assertThrows(KiboException.class, () -> parser.parseTaskIndex("unmark 1", CommandType.UNMARK, 0));
    }

    @Test
    void parseDates_leapDayAndDateOnlyEvent_acceptsValidDate() throws Exception {
        LocalDate leapDay = LocalDate.of(2024, 2, 29);
        assertEquals(leapDay, parser.parseScheduleDate("schedule  2024-02-29  "));
        assertTrue(parser.parseDeadline("deadline report /by 2024-02-29").isScheduledOn(leapDay));
        assertTrue(parser.parseEvent("event meeting /from 2024-02-29 /to evening").isScheduledOn(leapDay));
        assertThrows(InvalidCommandException.class, () ->
                parser.parseEvent("event meeting /from 2024-2-29 /to evening"));
    }

    @Test
    void parseEvent_reversedMarkers_rejectsInput() {
        assertInvalidEvent("event meeting /to 4pm /from 2pm");
    }

    @Test
    void parseDeadline_validDescriptionAndDate_returnsDeadline() throws InvalidCommandException {
        Task task = parser.parseDeadline("deadline return book /by 2019-12-02");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.of(2019, 12, 2), deadline.getBy());
        assertEquals("[D][ ] return book (by: Dec 02 2019)", deadline.toString());
    }

    @Test
    void parseDeadline_missingMarkerDescriptionOrDate_throwsInvalidCommandException() {
        assertInvalidDeadline("deadline return book by 2019-12-02");
        assertInvalidDeadline("deadline /by 2019-12-02");
        assertInvalidDeadline("deadline return book /by");
    }

    @Test
    void parseDeadline_invalidDate_throwsInvalidCommandException() {
        InvalidCommandException exception = assertThrows(
                InvalidCommandException.class, () ->
                        parser.parseDeadline("deadline return book /by 2019-02-29"));

        assertEquals("The deadline date must use yyyy-MM-dd format.\n"
                + "Usage: deadline [description] /by yyyy-MM-dd", exception.getMessage());
    }

    @Test
    void parseEvent_validDescriptionStartAndEnd_returnsEvent() throws InvalidCommandException {
        Task task = parser.parseEvent("event project meeting /from Mon 2pm /to 4pm");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("project meeting", event.getDescription());
        assertEquals("Mon 2pm", event.getFrom());
        assertEquals("4pm", event.getTo());
        assertEquals("[E][ ] project meeting (from: Mon 2pm to: 4pm)", event.toString());
        assertFalse(event.isScheduledOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    void parseEvent_validDatedStart_returnsScheduledEvent() throws InvalidCommandException {
        Task task = parser.parseEvent(
                "event project meeting /from 2019-12-02 2pm /to 4pm");

        Event event = assertInstanceOf(Event.class, task);
        assertTrue(event.isScheduledOn(LocalDate.of(2019, 12, 2)));
        assertFalse(event.isScheduledOn(LocalDate.of(2019, 12, 3)));
        assertEquals("2019-12-02 2pm", event.getFrom());
        assertEquals("4pm", event.getTo());
    }

    @Test
    void parseEvent_invalidDatedStart_throwsInvalidCommandException() {
        InvalidCommandException exception = assertThrows(
                InvalidCommandException.class, () -> parser.parseEvent(
                        "event project meeting /from 2019-02-29 2pm /to 4pm"));

        assertEquals("The event start date must use yyyy-MM-dd format.\n"
                + "Date-aware event usage: event [description] "
                + "/from yyyy-MM-dd [time] /to [end]", exception.getMessage());
    }

    @Test
    void parseEvent_missingMarkerDescriptionStartOrEnd_throwsInvalidCommandException() {
        assertInvalidEvent("event project meeting from Mon 2pm to 4pm");
        assertInvalidEvent("event project meeting /from Mon 2pm");
        assertInvalidEvent("event /from Mon 2pm /to 4pm");
        assertInvalidEvent("event project meeting /from /to 4pm");
        assertInvalidEvent("event project meeting /from Mon 2pm /to");
    }

    @Test
    void parseFindKeyword_validKeyword_returnsKeyword() throws InvalidCommandException {
        assertEquals("book", parser.parseFindKeyword("find book"));
    }

    @Test
    void parseFindKeyword_emptyKeyword_throwsInvalidCommandException() {
        InvalidCommandException exception = assertThrows(
                InvalidCommandException.class, () -> parser.parseFindKeyword("find"));

        assertEquals("The search keyword cannot be empty.\nUsage: find [keyword]",
                exception.getMessage());
    }

    @Test
    void parseScheduleDate_validDate_returnsLocalDate() throws InvalidCommandException {
        assertEquals(LocalDate.of(2019, 12, 2),
                parser.parseScheduleDate("schedule 2019-12-02"));
    }

    @Test
    void parseScheduleDate_missingOrInvalidDate_throwsInvalidCommandException() {
        assertInvalidScheduleDate("schedule");
        assertInvalidScheduleDate("schedule 2019-02-29");
        assertInvalidScheduleDate("schedule tomorrow");
        assertInvalidScheduleDate("schedule 2019-12-02 extra");
    }

    @Test
    void parseTaskIndex_nonNumberedCommand_throwsAssertionError() {
        assertThrows(AssertionError.class, () ->
                parser.parseTaskIndex("todo 1", CommandType.TODO, 1));
    }

    @Test
    void parseTaskIndex_negativeTaskCount_throwsAssertionError() {
        assertThrows(AssertionError.class, () ->
                parser.parseTaskIndex("mark 1", CommandType.MARK, -1));
    }

    /**
     * Verifies each malformed deadline command produces the standard usage error.
     *
     * @param input malformed deadline command.
     */
    private void assertInvalidDeadline(String input) {
        InvalidCommandException exception = assertThrows(
                InvalidCommandException.class, () -> parser.parseDeadline(input));
        assertEquals("A deadline needs a description and /by date.\n"
                + "Usage: deadline [description] /by yyyy-MM-dd", exception.getMessage());
    }

    /**
     * Verifies each malformed event command produces the standard usage error.
     *
     * @param input malformed event command.
     */
    private void assertInvalidEvent(String input) {
        InvalidCommandException exception = assertThrows(
                InvalidCommandException.class, () -> parser.parseEvent(input));
        assertEquals("An event needs a description, /from start, and /to end.\n"
                + "Usage: event [description] /from [start] /to [end]", exception.getMessage());
    }

    /**
     * Verifies each malformed schedule command produces the standard usage error.
     *
     * @param input malformed schedule command.
     */
    private void assertInvalidScheduleDate(String input) {
        InvalidCommandException exception = assertThrows(
                InvalidCommandException.class, () -> parser.parseScheduleDate(input));
        assertEquals("The schedule date must use yyyy-MM-dd format.\n"
                + "Usage: schedule yyyy-MM-dd", exception.getMessage());
    }
}
