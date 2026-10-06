package ducky;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests for the command parsing behavior of {@link Parser}.
 */
class ParserTest {
    private final Parser parser = new Parser();


    // Tests parsing commands without arguments.
    @Test
    void parse_simpleCommand_correctCommandTypeReturned() throws DuckyException {
        assertEquals(Parser.CommandType.BYE, parser.parse("bye").getType());
        assertEquals(Parser.CommandType.LIST, parser.parse("list").getType());
    }


    // Tests parsing a find command and extracting its keyword.
    @Test
    void parse_findCommand_keywordReturned() throws DuckyException {
        Parser.ParsedCommand parsed = parser.parse("find Book");

        assertEquals(Parser.CommandType.FIND, parsed.getType());
        assertEquals("Book", parsed.getKeyword());
    }


    // Tests conversion of a user-facing task number to a zero-based index.
    @Test
    void parse_markCommand_oneBasedNumberConvertedToZeroBasedIndex() throws DuckyException {
        Parser.ParsedCommand parsed = parser.parse("mark 3");

        assertEquals(Parser.CommandType.MARK, parsed.getType());
        assertEquals(2, parsed.getTaskIndex());
    }


    // Tests parsing a to-do command with surrounding whitespace in its description.
    @Test
    void parse_todoCommand_trimmedDescriptionAndTodoReturned() throws DuckyException {
        Parser.ParsedCommand parsed = parser.parse("todo   buy milk  ");

        assertEquals(Parser.CommandType.ADD, parsed.getType());
        ToDo task = assertInstanceOf(ToDo.class, parsed.getTask());
        assertEquals("buy milk", task.getDescription());
    }


    // Tests extraction, normalization, and deduplication of tags from a to-do description.
    @Test
    void parse_todoWithTags_descriptionAndUniqueTagsReturned() throws DuckyException {
        Parser.ParsedCommand parsed = parser.parse("todo watch movie #Fun #weekend #fun");

        ToDo task = assertInstanceOf(ToDo.class, parsed.getTask());
        assertEquals("watch movie", task.getDescription());
        assertEquals(List.of("fun", "weekend"), task.getTags());
    }


    // Tests parsing a deadline's description and due date.
    @Test
    void parse_deadlineCommand_dateAndDescriptionParsed() throws DuckyException {
        Parser.ParsedCommand parsed = parser.parse("deadline submit report /by 2026-09-01");

        Deadline task = assertInstanceOf(Deadline.class, parsed.getTask());
        assertEquals("submit report", task.getDescription());
        assertEquals(LocalDate.of(2026, 9, 1), task.getBy());
    }


    // Tests extraction of a tag from a deadline description.
    @Test
    void parse_deadlineWithTag_descriptionDateAndTagReturned() throws DuckyException {
        Parser.ParsedCommand parsed = parser.parse(
                "deadline submit report /by 2026-09-01 #school");

        Deadline task = assertInstanceOf(Deadline.class, parsed.getTask());
        assertEquals("submit report", task.getDescription());
        assertEquals(List.of("school"), task.getTags());
    }


    // Tests parsing an event's description, start time, and end time.
    @Test
    void parse_eventCommand_startAndEndParsed() throws DuckyException {
        Parser.ParsedCommand parsed = parser.parse(
                "event team meeting /from 2026-09-01 0900 /to 2026-09-01 1030");

        Event task = assertInstanceOf(Event.class, parsed.getTask());
        assertEquals("team meeting", task.getDescription());
        assertEquals(LocalDateTime.of(2026, 9, 1, 9, 0), task.getStart());
        assertEquals(LocalDateTime.of(2026, 9, 1, 10, 30), task.getEnd());
    }


    // Tests extraction of a tag from an event description.
    @Test
    void parse_eventWithTag_descriptionTimesAndTagReturned() throws DuckyException {
        Parser.ParsedCommand parsed = parser.parse(
                "event team meeting /from 2026-09-01 0900 /to 2026-09-01 1030 #work");

        Event task = assertInstanceOf(Event.class, parsed.getTask());
        assertEquals("team meeting", task.getDescription());
        assertEquals(List.of("work"), task.getTags());
    }


    // Tests rejection of an unrecognised command.
    @Test
    void parse_invalidCommand_exceptionThrown() {
        assertThrows(DuckyException.class, () -> parser.parse("unknown command"));
    }


    // Tests rejection of missing required argument in the find command.
    @Test
    void parse_emptyFindKeyword_exceptionThrown() {
        assertThrows(DuckyException.class, () -> parser.parse("find"));
        assertThrows(DuckyException.class, () -> parser.parse("find   "));
    }


    // Tests rejection of invalid task-number arguments.
    @Test
    void parse_invalidTaskNumbers_exceptionThrown() {
        assertThrows(DuckyException.class, () -> parser.parse("mark 0"));
        assertThrows(DuckyException.class, () -> parser.parse("delete abc"));
    }


    // Tests rejection of structured commands missing required arguments.
    @Test
    void parse_incompleteStructuredCommand_exceptionThrown() {
        assertThrows(DuckyException.class, () -> parser.parse("todo"));
        assertThrows(DuckyException.class, () -> parser.parse("todo #fun"));
        assertThrows(DuckyException.class, () -> parser.parse("deadline submit report"));
        assertThrows(DuckyException.class, () -> parser.parse("event meeting /from 2026-09-01 0900"));
    }

    @Test
    void parse_deadlineWithoutNameOrDate_fullFormatShown() {
        DuckyException exception = assertThrows(DuckyException.class, () -> parser.parse("deadline /by"));

        assertEquals("Please use: deadline <name> /by <date>", exception.getMessage());
    }

    @Test
    void parse_nullOrBlankCommand_exceptionThrown() {
        assertThrows(DuckyException.class, () -> parser.parse(null));
        assertThrows(DuckyException.class, () -> parser.parse("   "));
    }

    @Test
    void parse_commandWithOuterWhitespace_commandParsed() throws DuckyException {
        assertEquals(Parser.CommandType.LIST, parser.parse("  list  ").getType());
    }

    @Test
    void parse_invalidDeadlineDate_exceptionThrown() {
        assertInvalidCommand("deadline submit report /by 2026-02-30");
    }

    @Test
    void parse_duplicateDeadlineMarker_exceptionThrown() {
        assertInvalidCommand("deadline submit report /by 2026-09-01 /by 2026-09-02");
    }

    @Test
    void parse_invalidEventRange_exceptionThrown() {
        assertInvalidCommand("event meeting /from 2026-09-01 1000 /to 2026-09-01 1000");
        assertInvalidCommand("event meeting /from 2026-09-01 1100 /to 2026-09-01 1000");
    }

    @Test
    void parse_duplicateEventMarker_exceptionThrown() {
        assertInvalidCommand("event meeting /from 2026-09-01 0900 "
                + "/from 2026-09-01 1000 /to 2026-09-01 1100");
    }

    private void assertInvalidCommand(String command) {
        assertThrows(DuckyException.class, () -> parser.parse(command));
    }
}
