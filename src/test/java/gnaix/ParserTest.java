package gnaix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import gnaix.task.Deadline;
import gnaix.task.Event;
import gnaix.task.Task;
import gnaix.task.Todo;

class ParserTest {

    @Test
    void parse_validTodoCommand_todoCreated() {
        ParsedCommand result = Parser.parse("todo buy milk");

        assertFalse(result.hasError());
        assertEquals(Command.TODO, result.getCommand());

        Task task = result.getTask();
        assertTrue(task instanceof Todo);
        assertEquals("buy milk", task.getDescription());
    }

    @Test
    void parse_uppercaseTodoCommand_todoCreated() {
        ParsedCommand result = Parser.parse("TODO buy milk");

        assertFalse(result.hasError());
        assertEquals(Command.TODO, result.getCommand());
        assertEquals("buy milk", result.getTask().getDescription());
    }

    @Test
    void parse_leadingTrailingWhitespace_commandParsed() {
        ParsedCommand result = Parser.parse("  todo buy milk  ");

        assertFalse(result.hasError());
        assertEquals(Command.TODO, result.getCommand());
        assertEquals("buy milk", result.getTask().getDescription());
    }

    @Test
    void parse_repeatedSpaces_commandParsed() {
        ParsedCommand result = Parser.parse("mark     3");

        assertFalse(result.hasError());
        assertEquals(Command.MARK, result.getCommand());
        assertEquals(3, result.getIndex());
    }

    @Test
    void parse_validDeadlineCommand_deadlineCreated() {
        ParsedCommand result = Parser.parse(
                "deadline return book /by 2026-09-01");

        assertFalse(result.hasError());
        assertEquals(Command.DEADLINE, result.getCommand());

        Task task = result.getTask();
        assertTrue(task instanceof Deadline);

        Deadline deadline = (Deadline) task;
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 9, 1), deadline.getDoBy());
    }

    @Test
    void parse_validEventCommand_eventCreated() {
        ParsedCommand result = Parser.parse(
                "event project meeting /from 2026-09-01 1400 "
                        + "/to 2026-09-01 1600");

        assertFalse(result.hasError());
        assertEquals(Command.EVENT, result.getCommand());

        Task task = result.getTask();
        assertTrue(task instanceof Event);

        Event event = (Event) task;
        assertEquals("project meeting", event.getDescription());
        assertEquals(
                LocalDateTime.of(2026, 9, 1, 14, 0),
                event.getFrom());
        assertEquals(
                LocalDateTime.of(2026, 9, 1, 16, 0),
                event.getTo());
    }

    @Test
    void parse_validDateCommand_dateParsed() {
        ParsedCommand result = Parser.parse("date 2026-09-01");

        assertFalse(result.hasError());
        assertEquals(Command.DATE, result.getCommand());
        assertEquals(
                LocalDate.of(2026, 9, 1),
                result.getDate());
    }

    @Test
    void parse_validListCommand_listReturned() {
        ParsedCommand result = Parser.parse("list");

        assertFalse(result.hasError());
        assertEquals(Command.LIST, result.getCommand());
        assertNull(result.getTask());
    }

    @Test
    void parse_validByeCommand_byeReturned() {
        ParsedCommand result = Parser.parse("bye");

        assertFalse(result.hasError());
        assertEquals(Command.BYE, result.getCommand());
        assertNull(result.getTask());
    }

    @Test
    void parseBye_trailingArguments_errorReturned() {
        ParsedCommand result = Parser.parse("bye later");

        assertTrue(result.hasError());
        assertEquals(
                "The bye command doesn't take arguments."
                        + System.lineSeparator()
                        + "Try bye on its own.",
                result.getError());
    }

    @Test
    void parseList_trailingArguments_errorReturned() {
        ParsedCommand result = Parser.parse("list tasks");

        assertTrue(result.hasError());
        assertEquals(
                "The list command doesn't take arguments."
                        + System.lineSeparator()
                        + "Try list on its own.",
                result.getError());
    }

    @Test
    void parse_validIndexCommand_indexParsed() {
        ParsedCommand result = Parser.parse("mark 3");

        assertFalse(result.hasError());
        assertEquals(Command.MARK, result.getCommand());
        assertEquals(3, result.getIndex());
    }

    @Test
    void parse_validDeleteCommand_indexParsed() {
        ParsedCommand result = Parser.parse("delete 2");

        assertFalse(result.hasError());
        assertEquals(Command.DELETE, result.getCommand());
        assertEquals(2, result.getIndex());
    }

    @Test
    void parse_validUnmarkCommand_indexParsed() {
        ParsedCommand result = Parser.parse("unmark 1");

        assertFalse(result.hasError());
        assertEquals(Command.UNMARK, result.getCommand());
        assertEquals(1, result.getIndex());
    }

    @Test
    void parse_emptyCommand_errorReturned() {
        ParsedCommand result = Parser.parse("");

        assertTrue(result.hasError());
        assertEquals(Command.UNKNOWN, result.getCommand());
        assertEquals(
                "You'll need to type a command.",
                result.getError());
    }

    @Test
    void parse_unknownCommand_errorReturned() {
        ParsedCommand result = Parser.parse("hello");

        assertTrue(result.hasError());
        assertEquals(Command.UNKNOWN, result.getCommand());
        assertEquals(
                "I don't know what that means."
                        + System.lineSeparator()
                        + "Try a valid command.",
                result.getError());
        assertNull(result.getTask());
    }

    @Test
    void parseTodo_missingDescription_errorReturned() {
        ParsedCommand result = Parser.parse("todo");

        assertTrue(result.hasError());
        assertEquals(
                "That task needs a description.",
                result.getError());
    }

    @Test
    void parseDeadline_missingByDate_errorReturned() {
        ParsedCommand result = Parser.parse("deadline return book");

        assertTrue(result.hasError());
        assertEquals(
                "That deadline needs a description and a /by date.",
                result.getError());
    }

    @Test
    void parseDeadline_emptyDescription_errorReturned() {
        ParsedCommand result = Parser.parse("deadline /by 2026-09-01");

        assertTrue(result.hasError());
        assertEquals(
                "That deadline needs a description and a /by date.",
                result.getError());
    }

    @Test
    void parseDeadline_emptyDate_errorReturned() {
        ParsedCommand result = Parser.parse("deadline return book /by");

        assertTrue(result.hasError());
        assertEquals(
                "That deadline needs a description and a /by date.",
                result.getError());
    }

    @Test
    void parseDeadline_invalidDate_errorReturned() {
        ParsedCommand result = Parser.parse(
                "deadline return book /by 2026-99-99");

        assertTrue(result.hasError());
        assertEquals(
                "That date doesn't work."
                        + System.lineSeparator()
                        + "Use yyyy-MM-dd.",
                result.getError());
    }

    @Test
    void parseEvent_missingTimings_errorReturned() {
        ParsedCommand result = Parser.parse(
                "event project meeting");

        assertTrue(result.hasError());
        assertEquals(
                "That event needs a description, /from time, and /to time.",
                result.getError());
    }

    @Test
    void parseEvent_missingToTime_errorReturned() {
        ParsedCommand result = Parser.parse(
                "event project meeting /from 2026-09-01 1400");

        assertTrue(result.hasError());
        assertEquals(
                "That event needs a description, /from time, and /to time.",
                result.getError());
    }

    @Test
    void parseEvent_emptyDescription_errorReturned() {
        ParsedCommand result = Parser.parse(
                "event /from 2026-09-01 1400 /to 2026-09-01 1600");

        assertTrue(result.hasError());
        assertEquals(
                "That event needs a description, /from time, and /to time.",
                result.getError());
    }

    @Test
    void parseEvent_emptyFromTime_errorReturned() {
        ParsedCommand result = Parser.parse(
                "event project meeting /from /to 2026-09-01 1600");

        assertTrue(result.hasError());
        assertEquals(
                "That event needs a description, /from time, and /to time.",
                result.getError());
    }

    @Test
    void parseEvent_emptyToTime_errorReturned() {
        ParsedCommand result = Parser.parse(
                "event project meeting /from 2026-09-01 1400 /to");

        assertTrue(result.hasError());
        assertEquals(
                "That event needs a description, /from time, and /to time.",
                result.getError());
    }

    @Test
    void parseEvent_invalidTime_errorReturned() {
        ParsedCommand result = Parser.parse(
                "event project meeting /from 2026-99-99 1400 "
                        + "/to 2026-09-01 1600");

        assertTrue(result.hasError());
        assertEquals(
                "That date doesn't work."
                        + System.lineSeparator()
                        + "Use yyyy-MM-dd HHmm.",
                result.getError());
    }

    @Test
    void parseEvent_endBeforeStart_errorReturned() {
        ParsedCommand result = Parser.parse(
                "event project meeting /from 2026-09-01 1600 "
                        + "/to 2026-09-01 1400");

        assertTrue(result.hasError());
        assertEquals(
                "That event's end time is before its start time."
                        + System.lineSeparator()
                        + "Use an end time at or after its start time.",
                result.getError());
    }

    @Test
    void parseEvent_sameStartAndEnd_eventCreated() {
        ParsedCommand result = Parser.parse(
                "event project meeting /from 2026-09-01 1400 "
                        + "/to 2026-09-01 1400");

        assertFalse(result.hasError());
        assertEquals(Command.EVENT, result.getCommand());
        Event event = (Event) result.getTask();
        assertEquals(
                LocalDateTime.of(2026, 9, 1, 14, 0),
                event.getFrom());
        assertEquals(
                LocalDateTime.of(2026, 9, 1, 14, 0),
                event.getTo());
    }

    @Test
    void parseDate_invalidDate_errorReturned() {
        ParsedCommand result = Parser.parse("date 2026-99-99");

        assertTrue(result.hasError());
        assertEquals(
                "That date doesn't work."
                        + System.lineSeparator()
                        + "Use yyyy-MM-dd.",
                result.getError());
    }

    @Test
    void parseIndex_nonNumericValue_errorReturned() {
        ParsedCommand result = Parser.parse("mark abc");

        assertTrue(result.hasError());
        assertEquals(
                "Which task?"
                        + System.lineSeparator()
                        + "You'll need to give me a task number.",
                result.getError());
    }

    @Test
    void parseIndex_missingValue_errorReturned() {
        ParsedCommand result = Parser.parse("mark");

        assertTrue(result.hasError());
        assertEquals(
                "Which task?"
                        + System.lineSeparator()
                        + "You'll need to give me a task number.",
                result.getError());
    }

    @Test
    void parse_validFindCommand_keywordReturned() {
        ParsedCommand result = Parser.parse("find book");

        assertFalse(result.hasError());
        assertEquals(Command.FIND, result.getCommand());
        assertEquals("book", result.getKeyword());
    }

    @Test
    void parseFind_missingKeyword_errorReturned() {
        ParsedCommand result = Parser.parse("find");

        assertTrue(result.hasError());
        assertEquals(
                "Search for what? Give me a keyword.",
                result.getError());
    }

    @Test
    void parseTodo_singleTag_tagExtracted() {
        ParsedCommand result =
                Parser.parse("todo study CS2103T #school");

        assertFalse(result.hasError());
        assertEquals(Command.TODO, result.getCommand());
        assertEquals(
                "study CS2103T",
                result.getTask().getDescription());
        assertEquals(Set.of("school"), result.getTask().getTags());
    }

    @Test
    void parseTodo_multipleTags_inputOrderPreserved() {
        ParsedCommand result =
                Parser.parse("todo study CS2103T #school #urgent");

        assertFalse(result.hasError());
        assertEquals(
                "study CS2103T",
                result.getTask().getDescription());
        assertEquals(
                List.of("school", "urgent"),
                List.copyOf(result.getTask().getTags()));
    }

    @Test
    void parseTodo_duplicateTags_firstOccurrenceOrderPreserved() {
        ParsedCommand result =
                Parser.parse(
                        "todo study CS2103T #school #urgent #School");

        assertFalse(result.hasError());
        assertEquals(
                List.of("school", "urgent"),
                List.copyOf(result.getTask().getTags()));
    }

    @Test
    void parseTodo_mixedCaseTags_orderPreservedAfterNormalisation() {
        ParsedCommand result =
                Parser.parse("todo study CS2103T #School #URGENT");

        assertFalse(result.hasError());
        assertEquals(
                List.of("school", "urgent"),
                List.copyOf(result.getTask().getTags()));
    }

    @Test
    void parseTodo_cSharpDescription_hashPreserved() {
        ParsedCommand result =
                Parser.parse("todo learn C# #programming");

        assertFalse(result.hasError());
        assertEquals(
                "learn C#",
                result.getTask().getDescription());
        assertEquals(
                Set.of("programming"),
                result.getTask().getTags());
    }

    @Test
    void parseDeadline_tagsAfterDate_tagsExtracted() {
        ParsedCommand result =
                Parser.parse(
                        "deadline submit quiz /by 2026-09-10 #school #urgent");

        assertFalse(result.hasError());
        assertEquals(Command.DEADLINE, result.getCommand());
        assertEquals(
                "submit quiz",
                result.getTask().getDescription());
        assertEquals(
                Set.of("school", "urgent"),
                result.getTask().getTags());
    }

    @Test
    void parseEvent_tagsAfterEndTime_tagsExtracted() {
        ParsedCommand result =
                Parser.parse(
                        "event meeting /from 2026-09-15 1000 "
                                + "/to 2026-09-15 1100 #school");

        assertFalse(result.hasError());
        assertEquals(Command.EVENT, result.getCommand());
        assertEquals(
                "meeting",
                result.getTask().getDescription());
        assertEquals(
                Set.of("school"),
                result.getTask().getTags());
    }

    @Test
    void parseTag_singleTag_tagsReturned() {
        ParsedCommand result = Parser.parse("tag #school");

        assertFalse(result.hasError());
        assertEquals(Command.TAG, result.getCommand());
        assertEquals(Set.of("school"), result.getTags());
    }

    @Test
    void parseTag_multipleTags_allTagsReturned() {
        ParsedCommand result =
                Parser.parse("tag #school #urgent");

        assertFalse(result.hasError());
        assertEquals(Command.TAG, result.getCommand());
        assertEquals(
                Set.of("school", "urgent"),
                result.getTags());
    }

    @Test
    void parseTag_mixedCaseTags_normalised() {
        ParsedCommand result =
                Parser.parse("tag #School #URGENT");

        assertFalse(result.hasError());
        assertEquals(
                Set.of("school", "urgent"),
                result.getTags());
    }

    @Test
    void parseTag_duplicateTags_storedOnce() {
        ParsedCommand result =
                Parser.parse("tag #school #School");

        assertFalse(result.hasError());
        assertEquals(
                Set.of("school"),
                result.getTags());
    }

    @Test
    void parseTag_missingHash_errorReturned() {
        ParsedCommand result = Parser.parse("tag school");

        assertTrue(result.hasError());
        assertEquals(
                "That tag format won't work."
                        + System.lineSeparator()
                        + "Try something like #school.",
                result.getError());
    }

    @Test
    void parseTag_hashOnly_errorReturned() {
        ParsedCommand result = Parser.parse("tag #");

        assertTrue(result.hasError());
        assertEquals(
                "That tag format won't work."
                        + System.lineSeparator()
                        + "Try something like #school.",
                result.getError());
    }

    @Test
    void parseTag_invalidCharacters_errorReturned() {
        ParsedCommand result = Parser.parse("tag #school!");

        assertTrue(result.hasError());
        assertEquals(
                "That tag format won't work."
                        + System.lineSeparator()
                        + "Try something like #school.",
                result.getError());
    }

    @Test
    void parseTag_missingTag_errorReturned() {
        ParsedCommand result = Parser.parse("tag");

        assertTrue(result.hasError());
        assertEquals(
                "That tag format won't work."
                        + System.lineSeparator()
                        + "Try something like #school.",
                result.getError());
    }
}
