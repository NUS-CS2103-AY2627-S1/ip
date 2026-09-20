package duchess.parser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import duchess.DuchessException;
import duchess.task.Deadline;
import duchess.task.Event;
import duchess.task.Todo;

/** Tests the task-index parsing behavior used by Duchess commands. */
public class ParserTest {
    /** Verifies that a todo command creates a task with the supplied description. */
    @Test
    public void parseTask_todoCommand_returnsTodoTask() throws DuchessException {
        Todo task = assertInstanceOf(Todo.class, Parser.parseTask("todo read book"));

        assertAll(
                () -> assertEquals("read book", task.getDescription()),
                () -> assertFalse(task.isDone()),
                () -> assertEquals("[T][ ] read book", task.toString())
        );
    }

    /** Verifies that harmless outer and separator whitespace is accepted. */
    @Test
    public void parseTask_extraWhitespace_returnsTask() throws DuchessException {
        Todo task = assertInstanceOf(Todo.class, Parser.parseTask("  todo     read book  "));

        assertEquals("read book", task.getDescription());
    }

    /** Verifies that a deadline command creates a task with its parsed date. */
    @Test
    public void parseTask_deadlineCommand_returnsDeadlineTask() throws DuchessException {
        Deadline task = assertInstanceOf(Deadline.class,
                Parser.parseTask("deadline return book /by 2019-12-02"));

        assertAll(
                () -> assertEquals("return book", task.getDescription()),
                () -> assertEquals(LocalDate.of(2019, 12, 2), task.getBy()),
                () -> assertFalse(task.isDone()),
                () -> assertEquals("[D][ ] return book (by: Dec 02 2019)", task.toString())
        );
    }

    /** Verifies that an event command creates a task with its supplied event time. */
    @Test
    public void parseTask_eventCommand_returnsEventTask() throws DuchessException {
        Event task = assertInstanceOf(Event.class,
                Parser.parseTask("event buy bread /at Saturday"));

        assertAll(
                () -> assertEquals("buy bread", task.getDescription()),
                () -> assertEquals("Saturday", task.getAt()),
                () -> assertFalse(task.isDone()),
                () -> assertEquals("[E][ ] buy bread (at: Saturday)", task.toString())
        );
    }

    /** Verifies that task type and detail markers are case-insensitive. */
    @Test
    public void parseTask_mixedCaseCommand_returnsCorrectTaskType() throws DuchessException {
        assertAll(
                () -> assertInstanceOf(Todo.class, Parser.parseTask("TODO read book")),
                () -> assertInstanceOf(Deadline.class,
                        Parser.parseTask("DEADLINE report /BY 2019-06-07")),
                () -> assertInstanceOf(Event.class,
                        Parser.parseTask("EVENT meeting /AT Monday"))
        );
    }

    /** Verifies that a blank command is rejected with a helpful error. */
    @Test
    public void parseTask_blankCommand_throwsDuchessException() {
        assertParseTaskFailsWithMessage("   ",
                "OOPS!!! A command cannot be empty. "
                        + "Try todo, deadline, event, list, find, mark, unmark, delete, or bye.");
    }

    /** Verifies that null input is treated as an empty command. */
    @Test
    public void parseTask_nullCommand_throwsDuchessException() {
        assertParseTaskFailsWithMessage(null,
                "OOPS!!! A command cannot be empty. "
                        + "Try todo, deadline, event, list, find, mark, unmark, delete, or bye.");
    }

    /** Verifies that a todo without a description is rejected. */
    @Test
    public void parseTask_todoWithoutDescription_throwsDuchessException() {
        assertParseTaskFailsWithMessage("todo   ",
                "OOPS!!! The description of a todo cannot be empty.");
    }

    /** Verifies that a deadline without a date is rejected. */
    @Test
    public void parseTask_deadlineWithoutByValue_throwsDuchessException() {
        assertParseTaskFailsWithMessage("deadline return book",
                "OOPS!!! A deadline must include a non-empty /by value. "
                        + "Example: deadline task description /by time.");
    }

    /** Verifies that a deadline without a description is rejected. */
    @Test
    public void parseTask_deadlineWithoutDescription_throwsDuchessException() {
        assertParseTaskFailsWithMessage("deadline /by 2019-06-07",
                "OOPS!!! The description of a deadline cannot be empty.");
    }

    /** Verifies that an invalid deadline date is rejected. */
    @Test
    public void parseTask_deadlineWithInvalidDate_throwsDuchessException() {
        assertParseTaskFailsWithMessage("deadline report /by not-a-date",
                "OOPS!!! The deadline date is invalid. "
                        + "Use yyyy-MM-dd, for example: 2019-12-02.");
    }

    /** Verifies that a deadline cannot contain its parameter more than once. */
    @Test
    public void parseTask_deadlineWithDuplicateByParameters_throwsDuchessException() {
        assertParseTaskFailsWithMessage("deadline report /by 2026-09-20 /by 2026-09-21",
                "OOPS!!! A deadline must contain exactly one /by parameter.");
    }

    /** Verifies that an event without a time is rejected. */
    @Test
    public void parseTask_eventWithoutAtValue_throwsDuchessException() {
        assertParseTaskFailsWithMessage("event meeting",
                "OOPS!!! An event must include a non-empty /at value. "
                        + "Example: event task description /at time.");
    }

    /** Verifies that an event without a description is rejected. */
    @Test
    public void parseTask_eventWithoutDescription_throwsDuchessException() {
        assertParseTaskFailsWithMessage("event /at Monday",
                "OOPS!!! The description of an event cannot be empty.");
    }

    /** Verifies that a legacy event cannot contain more than one time parameter. */
    @Test
    public void parseTask_eventWithDuplicateAtParameters_throwsDuchessException() {
        assertParseTaskFailsWithMessage("event meeting /at Monday /at Tuesday",
                "OOPS!!! An event must contain exactly one /at parameter.");
    }

    /** Verifies that embedded control characters cannot become task data. */
    @Test
    public void parseTask_controlCharacter_throwsDuchessException() {
        assertParseTaskFailsWithMessage("todo read\nlist",
                "OOPS!!! Commands cannot contain control characters.");
    }

    /** Verifies that unsupported commands are rejected. */
    @Test
    public void parseTask_unknownCommand_throwsDuchessException() {
        assertParseTaskFailsWithMessage("blah",
                "OOPS!!! I'm sorry, but I don't know what that means :-(\n"
                        + "Try todo, deadline, event, list, find, mark, unmark, delete, or bye.");
    }

    /** Verifies that a valid one-based index becomes the expected zero-based index. */
    @Test
    public void parseTaskIndex_validIndex_returnsZeroBasedIndex() {
        assertAll(
                () -> assertEquals(0, Parser.parseTaskIndex("mark 1", "mark ")),
                () -> assertEquals(11, Parser.parseTaskIndex("delete 12", "delete "))
        );
    }

    /** Verifies that whitespace around a valid index is ignored. */
    @Test
    public void parseTaskIndex_whitespacePaddedIndex_returnsZeroBasedIndex() {
        assertEquals(0, Parser.parseTaskIndex("delete   1  ", "delete "));
    }

    /** Verifies that zero is rejected as an invalid one-based index. */
    @Test
    public void parseTaskIndex_zeroIndex_returnsNegativeValue() {
        assertTrue(Parser.parseTaskIndex("mark 0", "mark ") < 0);
    }

    /** Verifies that a negative task number is rejected as an invalid index. */
    @Test
    public void parseTaskIndex_negativeIndex_returnsNegativeValue() {
        assertTrue(Parser.parseTaskIndex("delete -1", "delete ") < 0);
    }

    /** Verifies that alphabetic input is rejected. */
    @Test
    public void parseTaskIndex_alphabeticIndex_returnsMinusOne() {
        assertEquals(-1, Parser.parseTaskIndex("mark abc", "mark "));
    }

    /** Verifies that decimal input is rejected. */
    @Test
    public void parseTaskIndex_decimalIndex_returnsMinusOne() {
        assertEquals(-1, Parser.parseTaskIndex("delete 1.5", "delete "));
    }

    /** Verifies that a missing index is rejected. */
    @Test
    public void parseTaskIndex_missingIndex_returnsMinusOne() {
        assertEquals(-1, Parser.parseTaskIndex("mark", "mark "));
    }

    /** Verifies that whitespace-only input is rejected. */
    @Test
    public void parseTaskIndex_blankIndex_returnsMinusOne() {
        assertEquals(-1, Parser.parseTaskIndex("unmark   ", "unmark "));
    }

    /** Verifies that extra tokens after an index are rejected. */
    @Test
    public void parseTaskIndex_extraTokens_returnsMinusOne() {
        assertEquals(-1, Parser.parseTaskIndex("delete 1 extra", "delete "));
    }

    /** Verifies that an integer outside the supported range is rejected. */
    @Test
    public void parseTaskIndex_overflowingIndex_returnsMinusOne() {
        assertEquals(-1, Parser.parseTaskIndex("mark 2147483648", "mark "));
    }

    /** Verifies that a number larger than a long is rejected safely. */
    @Test
    public void parseTaskIndex_largerThanLong_returnsMinusOne() {
        assertEquals(-1, Parser.parseTaskIndex("mark 999999999999999999999999", "mark "));
    }

    /** Verifies that an otherwise valid number is rejected for the wrong command prefix. */
    @Test
    public void parseTaskIndex_wrongCommandPrefix_returnsMinusOne() {
        assertEquals(-1, Parser.parseTaskIndex("delete 1", "mark "));
    }

    /** Verifies assertion-based preconditions for the task-index parser. */
    @Test
    public void parseTaskIndex_nullInput_throwsAssertionError() {
        assertAll(
                () -> assertThrows(AssertionError.class,
                        () -> Parser.parseTaskIndex(null, "mark ")),
                () -> assertThrows(AssertionError.class,
                        () -> Parser.parseTaskIndex("mark 1", null))
        );
    }

    /** Verifies that the minimum integer cannot overflow into a valid task index. */
    @Test
    public void parseTaskIndex_minimumInteger_returnsMinusOne() {
        assertEquals(-1, Parser.parseTaskIndex("mark -2147483648", "mark "));
    }

    /** Verifies date ranges and their case-insensitive squad alias. */
    @Test
    public void parseTask_eventRange_parsesDates() throws DuchessException {
        Event event = assertInstanceOf(Event.class,
                Parser.parseTask("BRIEFING orientation /FROM 2026-09-15 /TO 2026-09-17"));
        assertEquals("orientation", event.getDescription());
        assertEquals(LocalDate.of(2026, 9, 15), event.getFrom());
        assertEquals(LocalDate.of(2026, 9, 17), event.getTo());
    }

    /** Verifies malformed ranges cannot be accepted as legacy event text. */
    @Test
    public void parseTask_invalidEventRange_throwsDuchessException() {
        String[] invalidCommands = {
            "event /from 2026-09-15 /to 2026-09-17",
            "event meeting /from 2026-09-15",
            "event meeting /to 2026-09-17",
            "event meeting /from /to 2026-09-17",
            "event meeting /from 2026-09-15 /to",
            "event meeting /from 2026-02-30 /to 2026-03-01",
            "event meeting /from 2026-09-17 /to 2026-09-15",
            "event meeting /from 2026-09-15 /to 2026-09-15",
            "event meeting /to 2026-09-17 /from 2026-09-15",
            "event meeting /from 2026-09-15 /to 2026-09-17 /at noon",
            "event meeting /from 2026-09-15 /from 2026-09-16 /to 2026-09-17",
            "event meeting /from 2026-09-15 /to 2026-09-17 /to 2026-09-18"
        };
        for (String command : invalidCommands) {
            assertThrows(DuchessException.class, () -> Parser.parseTask(command), command);
        }
    }

    /** Asserts that parsing a command fails with the expected user-facing message. */
    private void assertParseTaskFailsWithMessage(String command, String expectedMessage) {
        DuchessException exception = assertThrows(DuchessException.class,
                () -> Parser.parseTask(command));
        assertEquals(expectedMessage, exception.getMessage());
    }
}
