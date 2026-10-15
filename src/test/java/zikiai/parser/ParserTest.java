package zikiai.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import zikiai.exception.ZikiaiException;
import zikiai.task.Deadline;
import zikiai.task.Event;
import zikiai.task.Priority;
import zikiai.task.Todo;

/**
 * Tests the non-trivial command parsing and validation performed by {@link Parser}.
 */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void commandRecognition_exactAndIncompleteKnownCommands_recognized() {
        assertTrue(parser.isByeCommand("bye"));
        assertTrue(parser.isListCommand("list"));
        assertTrue(parser.isMarkCommand("mark"));
        assertTrue(parser.isUnmarkCommand("unmark task"));
        assertTrue(parser.isDeleteCommand("delete\t1"));
        assertTrue(parser.isFindCommand("find"));
        assertTrue(parser.isTodoCommand("todo read book"));
        assertTrue(parser.isDeadlineCommand("deadline task"));
        assertTrue(parser.isEventCommand("event meeting"));
        assertTrue(parser.isPriorityCommand("priority"));
    }

    @Test
    void commandRecognition_prefixCollisions_notRecognized() {
        assertFalse(parser.isByeCommand("byebye"));
        assertFalse(parser.isListCommand("listing"));
        assertFalse(parser.isMarkCommand("marker 1"));
        assertFalse(parser.isUnmarkCommand("unmarked 1"));
        assertFalse(parser.isDeleteCommand("deleted 1"));
        assertFalse(parser.isFindCommand("finder book"));
        assertFalse(parser.isTodoCommand("todos read book"));
        assertFalse(parser.isDeadlineCommand("deadlines task"));
        assertFalse(parser.isEventCommand("events meeting"));
        assertFalse(parser.isPriorityCommand("priority-level 1"));
    }

    @Test
    void parseTaskIndex_firstTask_zeroBasedIndexReturned() throws ZikiaiException {
        assertEquals(0, parser.parseTaskIndex("mark 1", 3));
    }

    @Test
    void parseTaskIndex_lastTask_zeroBasedIndexReturned() throws ZikiaiException {
        assertEquals(2, parser.parseTaskIndex("delete 3", 3));
    }

    @Test
    void parseTaskIndex_repeatedWhitespace_zeroBasedIndexReturned() throws ZikiaiException {
        assertEquals(1, parser.parseTaskIndex("mark    2", 3));
    }

    @Test
    void parseTaskIndex_missingNumber_actionableFormatThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseTaskIndex("unmark", 3));

        assertEquals("Use unmark TASK_NUMBER, for example unmark 1.", exception.getMessage());
    }

    @Test
    void parseTaskIndex_zero_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseTaskIndex("mark 0", 3));

        assertEquals("That task number does not exist.", exception.getMessage());
    }

    @Test
    void parseTaskIndex_numberBeyondList_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseTaskIndex("unmark 4", 3));

        assertEquals("That task number does not exist.", exception.getMessage());
    }

    @Test
    void parseTaskIndex_numberTooLargeForInteger_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseTaskIndex("delete 999999999999999999999", 3));

        assertEquals("That task number is too large.", exception.getMessage());
    }

    @Test
    void parseTaskIndex_nonNumberedCommand_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> parser.parseTaskIndex("list", 3));
    }

    @Test
    void parseTaskIndex_negativeTaskCount_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> parser.parseTaskIndex("mark 1", -1));
    }

    @Test
    void parsePriorityTaskIndex_validCommand_zeroBasedIndexReturned() throws ZikiaiException {
        assertEquals(1, parser.parsePriorityTaskIndex("priority 2 high", 3));
    }

    @Test
    void parsePriorityTaskIndex_nonNumericNumber_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () ->
                        parser.parsePriorityTaskIndex("priority two high", 3));

        assertEquals(
                "Use priority TASK_NUMBER with high, medium, low, or none.",
                exception.getMessage());
    }

    @Test
    void parsePriorityTaskIndex_numberBeyondList_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () ->
                        parser.parsePriorityTaskIndex("priority 4 high", 3));

        assertEquals("That task number does not exist.", exception.getMessage());
    }

    @Test
    void parsePriority_validLevels_prioritiesReturned() throws ZikiaiException {
        assertEquals(Priority.HIGH, parser.parsePriority("priority 1 high"));
        assertEquals(Priority.MEDIUM, parser.parsePriority("priority 1 medium"));
        assertEquals(Priority.LOW, parser.parsePriority("priority 1 low"));
        assertEquals(Priority.NONE, parser.parsePriority("priority 1 none"));
    }

    @Test
    void parsePriority_unknownLevel_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () ->
                        parser.parsePriority("priority 1 urgent"));

        assertEquals(
                "Use priority TASK_NUMBER with high, medium, low, or none.",
                exception.getMessage());
    }

    @Test
    void parsePriority_missingLevel_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parsePriority("priority 1"));

        assertEquals(
                "Use priority TASK_NUMBER with high, medium, low, or none.",
                exception.getMessage());
    }

    @Test
    void parseFindKeyword_validCommand_trimmedKeywordReturned() throws ZikiaiException {
        assertEquals("return book", parser.parseFindKeyword("find   return book   "));
    }

    @Test
    void parseFindKeyword_emptyCommand_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseFindKeyword("find"));

        assertEquals("Use find KEYWORD, for example find book.", exception.getMessage());
    }

    @Test
    void parseFindKeyword_whitespaceOnly_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseFindKeyword("find   "));

        assertEquals("Use find KEYWORD, for example find book.", exception.getMessage());
    }

    @Test
    void parseTodo_validCommand_trimmedTodoReturned() throws ZikiaiException {
        Todo todo = parser.parseTodo("todo    read book   ");

        assertEquals("[T][ ] read book", todo.getDescription());
        assertEquals("[T][ ] | read book", todo.toDataString());
    }

    @Test
    void parseTodo_emptyDescription_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseTodo("todo   "));

        assertEquals("Use todo DESCRIPTION, for example todo read book.", exception.getMessage());
    }

    @Test
    void parseTodo_reservedSeparator_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseTodo("todo read | book"));

        assertEquals("Task details cannot contain the | character.", exception.getMessage());
    }

    @Test
    void parseDeadline_validLeapDay_formattedDeadlineReturned() throws ZikiaiException {
        Deadline deadline = parser.parseDeadline("deadline submit report /by 2024-02-29");

        assertEquals("[D][ ] submit report (by: Feb 29 2024)", deadline.getDescription());
        assertEquals("[D][ ] | submit report | 2024-02-29", deadline.toDataString());
    }

    @Test
    void parseDeadline_missingByMarker_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseDeadline("deadline submit report 2026-08-23"));

        assertEquals(
                "Use deadline DESCRIPTION /by yyyy-MM-dd, for example "
                        + "deadline submit report /by 2026-09-30.",
                exception.getMessage());
    }

    @Test
    void parseDeadline_missingDescription_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseDeadline("deadline /by 2026-08-23"));

        assertEquals(
                "Use deadline DESCRIPTION /by yyyy-MM-dd, for example "
                        + "deadline submit report /by 2026-09-30.",
                exception.getMessage());
    }

    @Test
    void parseDeadline_missingDate_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseDeadline("deadline submit report /by"));

        assertEquals(
                "Use deadline DESCRIPTION /by yyyy-MM-dd, for example "
                        + "deadline submit report /by 2026-09-30.",
                exception.getMessage());
    }

    @Test
    void parseDeadline_duplicateByMarker_actionableFormatThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseDeadline(
                        "deadline report /by 2026-09-30 /by 2026-10-01"));

        assertEquals(
                "Use deadline DESCRIPTION /by yyyy-MM-dd, for example "
                        + "deadline submit report /by 2026-09-30.",
                exception.getMessage());
    }

    @Test
    void parseDeadline_impossibleDate_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseDeadline("deadline submit report /by 2025-02-29"));

        assertEquals(
                "Please enter the deadline as yyyy-MM-dd, for example 2026-08-23.",
                exception.getMessage());
    }

    @Test
    void parseDeadline_reservedSeparator_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseDeadline("deadline submit | report /by 2026-08-23"));

        assertEquals("Task details cannot contain the | character.", exception.getMessage());
    }

    @Test
    void parseEvent_validCommand_trimmedEventReturned() throws ZikiaiException {
        Event event = parser.parseEvent("event   team meeting /from Mon 2pm /to 4pm   ");

        assertEquals(
                "[E][ ] team meeting (from: Mon 2pm to: 4pm)", event.getDescription());
        assertEquals(
                "[E][ ] | team meeting | Mon 2pm | 4pm", event.toDataString());
    }

    @Test
    void parseDeadline_separatorInDate_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseDeadline("deadline report /by 2026-08-23 | noon"));

        assertEquals("Task details cannot contain the | character.", exception.getMessage());
    }

    @Test
    void parseEvent_separatorInDescription_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseEvent("event team | meeting /from 2pm /to 4pm"));

        assertEquals("Task details cannot contain the | character.", exception.getMessage());
    }

    @Test
    void parseEvent_separatorInEndTime_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseEvent("event meeting /from 2pm /to 4pm | later"));

        assertEquals("Task details cannot contain the | character.", exception.getMessage());
    }

    @Test
    void parseEvent_missingToMarker_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseEvent("event team meeting /from Mon 2pm"));

        assertEquals(
                "Use event DESCRIPTION /from START /to END, for example "
                        + "event meeting /from 2pm /to 4pm.",
                exception.getMessage());
    }

    @Test
    void parseEvent_missingDescription_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseEvent("event /from Mon 2pm /to 4pm"));

        assertEquals(
                "Use event DESCRIPTION /from START /to END, for example "
                        + "event meeting /from 2pm /to 4pm.",
                exception.getMessage());
    }

    @Test
    void parseEvent_missingStartTime_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseEvent("event team meeting /from /to 4pm"));

        assertEquals(
                "Use event DESCRIPTION /from START /to END, for example "
                        + "event meeting /from 2pm /to 4pm.",
                exception.getMessage());
    }

    @Test
    void parseEvent_missingEndTime_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseEvent("event team meeting /from Mon 2pm /to"));

        assertEquals(
                "Use event DESCRIPTION /from START /to END, for example "
                        + "event meeting /from 2pm /to 4pm.",
                exception.getMessage());
    }

    @Test
    void parseEvent_reversedMarkers_actionableFormatThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseEvent(
                        "event meeting /to 4pm /from 2pm"));

        assertEquals(
                "Use event DESCRIPTION /from START /to END, for example "
                        + "event meeting /from 2pm /to 4pm.",
                exception.getMessage());
    }

    @Test
    void parseEvent_reservedSeparator_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseEvent("event team meeting /from Mon | 2pm /to 4pm"));

        assertEquals("Task details cannot contain the | character.", exception.getMessage());
    }
}
