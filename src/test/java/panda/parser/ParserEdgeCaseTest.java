package panda.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import panda.exception.EmptyDescriptionException;
import panda.exception.InvalidCommandException;
import panda.exception.InvalidDateException;
import panda.exception.InvalidTaskNumberException;
import panda.exception.InvalidUpdateException;
import panda.exception.MissingDateTimeException;
import panda.exception.PandaException;

/**
 * Tests parser boundaries that are easy to miss in representative command examples.
 */
class ParserEdgeCaseTest {
    private final Parser parser = new Parser();

    @Test
    void isExitCommand_exactKeywordOnly_distinguishesExitMessages() {
        assertTrue(parser.isExitCommand("bye"));
        assertFalse(parser.isExitCommand("bye "));
        assertFalse(parser.isExitCommand("Bye"));
        assertFalse(parser.isExitCommand("bye now"));
    }

    @Test
    void parse_byeCommand_returnsArgumentFreeCommand() throws PandaException {
        Parser.ParsedCommand result = parser.parse("bye");

        assertEquals(Command.BYE, result.command());
        assertNull(result.task());
        assertNull(result.taskNumber());
        assertNull(result.filterDate());
        assertNull(result.searchTerm());
        assertNull(result.updateDetails());
    }

    @Test
    void parse_extraWhitespace_trimsCommandArguments() throws PandaException {
        Parser.ParsedCommand todo = parser.parse("todo    read book   ");
        Parser.ParsedCommand find = parser.parse("find    Java book   ");
        Parser.ParsedCommand numbered = parser.parse("mark    12   ");

        assertEquals("read book", todo.task().getName());
        assertEquals("Java book", find.searchTerm());
        assertEquals(12, numbered.taskNumber());
    }

    @Test
    void parse_alternativeDatedSyntax_returnsDatedTasks() throws PandaException {
        Parser.ParsedCommand deadline = parser.parse(
                "deadline submit report by 2026-09-14 18:00");
        Parser.ParsedCommand event = parser.parse(
                "event camp from 2026-09-14 09:00 to 2026-09-15 17:00");

        assertEquals("submit report (by: Sep 14 2026 18:00)",
                deadline.task().getDisplayText());
        assertEquals("camp (from: Sep 14 2026 09:00 to: Sep 15 2026 17:00)",
                event.task().getDisplayText());
    }

    @Test
    void parse_missingTaskNumbers_throwsCommandSpecificErrors() {
        assertErrorMessage(InvalidTaskNumberException.class,
                "OOPS!!! This panda needs a valid task number after mark.", "mark");
        assertErrorMessage(InvalidTaskNumberException.class,
                "OOPS!!! This panda needs a valid task number after unmark.", "unmark");
        assertErrorMessage(InvalidTaskNumberException.class,
                "OOPS!!! This panda needs a valid task number after delete.", "delete");
        assertErrorMessage(InvalidTaskNumberException.class,
                "OOPS!!! This panda needs a valid task number after update.", "update");
    }

    @Test
    void parse_nonIntegerAndOverflowTaskNumbers_throwsInvalidTaskNumberException() {
        assertThrows(InvalidTaskNumberException.class, () -> parser.parse("mark 1.5"));
        assertThrows(InvalidTaskNumberException.class, () ->
                parser.parse("delete 999999999999999999999"));
        assertThrows(InvalidTaskNumberException.class, () ->
                parser.parse("update 999999999999999999999 /name task"));
    }

    @Test
    void parse_missingDatedDescriptions_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () ->
                parser.parse("deadline /by 2026-09-14 10:00"));
        assertThrows(EmptyDescriptionException.class, () ->
                parser.parse("deadline by 2026-09-14 10:00"));
        assertThrows(EmptyDescriptionException.class, () ->
                parser.parse("event /from 2026-09-14 10:00 /to 2026-09-14 11:00"));
        assertThrows(EmptyDescriptionException.class, () ->
                parser.parse("event from 2026-09-14 10:00 to 2026-09-14 11:00"));
    }

    @Test
    void parse_incompleteDatedCommands_throwsMissingDateTimeException() {
        assertThrows(MissingDateTimeException.class, () ->
                parser.parse("deadline report /by"));
        assertThrows(MissingDateTimeException.class, () ->
                parser.parse("deadline report /by "));
        assertThrows(MissingDateTimeException.class, () ->
                parser.parse("event camp /from 2026-09-14 10:00 /to"));
        assertThrows(MissingDateTimeException.class, () ->
                parser.parse("event camp /from /to 2026-09-14 11:00"));
        assertThrows(MissingDateTimeException.class, () ->
                parser.parse("event camp /to 2026-09-14 11:00"));
    }

    @Test
    void parse_invalidDatedValues_throwsInvalidDateException() {
        assertThrows(InvalidDateException.class, () ->
                parser.parse("deadline report /by 2025-02-29 10:00"));
        assertThrows(InvalidDateException.class, () ->
                parser.parse(
                        "event camp /from 2026-09-14 10:00 /to 2026-13-14 11:00"));
    }

    @Test
    void parse_malformedUpdateMarkers_throwsInvalidUpdateException() {
        assertThrows(InvalidUpdateException.class, () ->
                parser.parse("update 1 /unknown value"));
        assertThrows(InvalidUpdateException.class, () ->
                parser.parse("update 1 /by 2026-09-14 10:00 /namebad"));
        assertThrows(InvalidUpdateException.class, () ->
                parser.parse("update 1 /from /to 2026-09-14 11:00"));
        assertThrows(InvalidUpdateException.class, () ->
                parser.parse("update 1 /from 2026-09-14 10:00 /to"));
        assertThrows(InvalidUpdateException.class, () ->
                parser.parse(
                        "update 1 /from 2026-09-14 10:00 /to 2026-09-14 11:00 /namebad"));
    }

    @Test
    void parse_leadingOrWrongCaseCommand_throwsInvalidCommandException() {
        assertThrows(InvalidCommandException.class, () -> parser.parse(" todo read book"));
        assertThrows(InvalidCommandException.class, () -> parser.parse("TODO read book"));
    }

    private <T extends PandaException> void assertErrorMessage(Class<T> exceptionType,
            String expectedMessage, String command) {
        T exception = assertThrows(exceptionType, () -> parser.parse(command));
        assertEquals(expectedMessage, exception.getMessage());
    }
}
