package neil.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import neil.exception.NeilException;
import neil.task.DeadlineTask;
import neil.task.EventTask;
import neil.task.Task;
import neil.task.ToDoTask;

/**
 * Tests for converting user input into tasks and task numbers.
 */
class ParserTest {

    @Test
    void parseTask_todoCommand_todoTaskCreated() throws NeilException {
        Task task = Parser.parseTask("  todo   read book  ");

        assertInstanceOf(ToDoTask.class, task);
        assertEquals("T | 0 | read book", task.encode());
    }

    @Test
    void parseTask_deadlineCommandWithExtraWhitespace_deadlineTaskCreated() throws NeilException {
        Task task = Parser.parseTask("  deadline   return book   /by   2026-08-25  ");

        assertInstanceOf(DeadlineTask.class, task);
        assertEquals("D | 0 | return book | 2026-08-25", task.encode());
    }

    @Test
    void parseTask_eventCommandWithExtraWhitespace_eventTaskCreated() throws NeilException {
        Task task = Parser.parseTask(
                "  event   team meeting   /from   2026-08-25 14:00   /to   2026-08-25 16:00  ");

        assertInstanceOf(EventTask.class, task);
        assertEquals("E | 0 | team meeting | 2026-08-25 14:00 | 2026-08-25 16:00", task.encode());
    }

    @Test
    void parseTask_blankOrUnsupportedCommand_exceptionThrown() {
        assertInvalidTaskInput("   ");
        assertInvalidTaskInput("reminder read book");
    }

    @Test
    void parseTask_taskCommandWithoutDescription_exceptionThrown() {
        assertInvalidTaskInput("todo");
        assertInvalidTaskInput("deadline   ");
        assertInvalidTaskInput("event   ");
    }

    @Test
    void parseTask_taskDetailsContainStorageDelimiter_exceptionThrown() {
        assertStorageDelimiterRejected("todo compare A | B");
        assertStorageDelimiterRejected("deadline submit A | B /by 2026-08-25");
        assertStorageDelimiterRejected(
                "event discuss A | B /from 2026-08-25 14:00 /to 2026-08-25 16:00");
    }

    @Test
    void parseTask_deadlineWithMissingOrInvalidDate_exceptionThrown() {
        assertInvalidTaskInput("deadline return book");
        assertInvalidTaskInput("deadline return book /by tomorrow");
        assertInvalidTaskInput("deadline return book /by ");
    }

    @Test
    void parseTask_eventWithMissingOrBlankTime_exceptionThrown() {
        assertInvalidTaskInput("event team meeting");
        assertInvalidTaskInput("event team meeting /from  /to 2026-08-25 16:00");
        assertInvalidTaskInput("event team meeting /from 2026-08-25 14:00 /to ");
    }

    @Test
    void parseTask_eventWithInvalidDateOrTime_exceptionThrown() {
        assertInvalidTaskInput("event team meeting /from 2026-02-30 14:00 /to 2026-03-01 16:00");
        assertInvalidTaskInput("event team meeting /from 2026-08-25 25:00 /to 2026-08-26 16:00");
        assertInvalidTaskInput("event team meeting /from Monday 2pm /to Monday 4pm");
    }

    @Test
    void parseTask_eventStartNotEarlierThanEnd_exceptionThrown() {
        assertInvalidTaskInput("event meeting /from 2026-08-25 14:00 /to 2026-08-25 14:00");
        assertInvalidTaskInput("event meeting /from 2026-08-25 16:00 /to 2026-08-25 14:00");
    }

    @Test
    void parseTaskNumber_positiveInteger_taskNumberReturned() throws NeilException {
        assertEquals(3, Parser.parseTaskNumber(new String[] {"mark", "3"}));
    }

    @Test
    void parseTaskNumber_missingOrExtraArgument_exceptionThrown() {
        assertInvalidTaskNumber(new String[] {"mark"});
        assertInvalidTaskNumber(new String[] {"mark", "1", "extra"});
    }

    @Test
    void parseTaskNumber_nonPositiveOrNonNumericNumber_exceptionThrown() {
        assertInvalidTaskNumber(new String[] {"mark", "0"});
        assertInvalidTaskNumber(new String[] {"mark", "-1"});
        assertInvalidTaskNumber(new String[] {"mark", "first"});
    }

    @Test
    void parseFindKeyword_keywordProvided_keywordReturned() throws NeilException {
        assertEquals("read book", Parser.parseFindKeyword("  find   read book  "));
    }

    @Test
    void parseFindKeyword_keywordMissing_exceptionThrown() {
        assertThrows(NeilException.class, () -> Parser.parseFindKeyword("find"));
        assertThrows(NeilException.class, () -> Parser.parseFindKeyword("find   "));
    }

    /**
     * Verifies that invalid task input reports a domain-specific exception.
     *
     * @param input invalid task command.
     */
    private void assertInvalidTaskInput(String input) {
        assertThrows(NeilException.class, () -> Parser.parseTask(input));
    }

    /**
     * Verifies that task details containing the storage delimiter are rejected.
     *
     * @param input task command containing the storage delimiter.
     */
    private void assertStorageDelimiterRejected(String input) {
        NeilException exception = assertThrows(NeilException.class, () -> Parser.parseTask(input));

        assertEquals(
                "Neil: Task details cannot contain the | character",
                exception.getMessage());
    }

    /**
     * Verifies that invalid task-number input reports a domain-specific exception.
     *
     * @param parts command words containing an invalid task number.
     */
    private void assertInvalidTaskNumber(String[] parts) {
        assertThrows(NeilException.class, () -> Parser.parseTaskNumber(parts));
    }
}
