package bloop.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import bloop.exception.DukeException;
import bloop.task.Priority;

/** Tests find-command keyword parsing. */
public class ParserTest {
    /** Verifies that a find keyword is trimmed without changing its content. */
    @Test
    public void parseFindKeyword_keywordWithWhitespace_returnsTrimmedKeyword() throws DukeException {
        assertEquals("read book", Parser.parseFindKeyword("find   read book  "));
    }

    /** Verifies that find commands require a keyword. */
    @Test
    public void parseFindKeyword_missingKeyword_throwsHelpfulException() {
        DukeException exception = assertThrows(DukeException.class, () -> Parser.parseFindKeyword("find"));

        assertEquals("OOPS!!! Please specify a keyword to find.", exception.getMessage());
    }

    /** Verifies priority parsing for every task-creation command. */
    @Test
    public void taskCommands_optionalPriority_returnsExpectedPriority() throws DukeException {
        assertEquals(Priority.HIGH, Parser.parseTodo("todo read book /priority HIGH").getPriority());
        assertEquals(Priority.MED, Parser.parseDeadline("deadline return book /by 2026-09-10 /priority med")
                .getPriority());
        assertEquals(Priority.LOW, Parser.parseEvent("event meeting /from 2026-09-10 09:00"
                + " /to 2026-09-10 10:00").getPriority());
    }

    /** Verifies that malformed or misplaced priorities report helpful errors. */
    @Test
    public void taskCommands_invalidPriority_throwsHelpfulException() {
        DukeException invalidPriority =
                assertThrows(DukeException.class, () -> Parser.parseTodo("todo read book /priority urgent"));
        DukeException misplacedPriority =
                assertThrows(DukeException.class, () -> Parser.parseDeadline("deadline return book /priority high"
                        + " /by 2026-09-10"));

        assertEquals("OOPS!!! Priority must be low, med, or high.", invalidPriority.getMessage());
        assertEquals("OOPS!!! Put /priority after the deadline date.", misplacedPriority.getMessage());
    }
}
