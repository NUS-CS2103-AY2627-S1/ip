package sevensix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Verifies malformed commands and date boundaries encountered during interactive use. */
class ParserErrorTest {
    /** Horizontal whitespace should be accepted consistently across all command fields. */
    @Test
    void normalizeAcceptsSpacesTabsAndNonBreakingSpaces() throws SevenSixException {
        String command = Parser.normalize("  deadline\t read  book\t/by\t2024-02-29  \t");
        Deadline deadline = Parser.parseDeadline(command);

        assertEquals("read book", deadline.getDescription());
        assertEquals(LocalDate.of(2024, 2, 29), deadline.getBy());
        assertEquals("list", Parser.normalize("\u00a0list\u00a0"));
        assertTrue(Parser.isExitCommand("\t bye  "));
        assertFalse(Parser.isExitCommand("bye extra"));
        assertFalse(Parser.isExitCommand("bye\n"));
    }

    /** Empty and control-character input must produce recoverable application errors. */
    @Test
    void normalizeRejectsEmptyAndControlCharacterInput() {
        assertThrows(SevenSixException.class, () -> Parser.normalize(null));
        for (String input : List.of("", " \t ", "\u00a0", "todo read\nbook", "todo read\rbook", "todo read\0book")) {
            assertThrows(SevenSixException.class, () -> Parser.normalize(input), input);
        }
    }

    /** Repeated, missing, and misplaced markers must never become task descriptions. */
    @Test
    void parseDeadlineRejectsInvalidParameters() {
        for (String command : List.of("deadline report", "deadline /by 2024-02-29", "deadline report /by",
                "deadline report /by 2024-02-29 /by 2024-03-01",
                "deadline report /from tomorrow /by 2024-02-29", "deadline report /by /by 2024-02-29")) {
            assertThrows(SevenSixException.class, () -> Parser.parseDeadline(command), command);
        }
    }

    /** Event marker count and order must be checked before dates are parsed. */
    @Test
    void parseEventRejectsInvalidParameters() {
        for (String command : List.of("event meeting /from 2024-01-01", "event /from 2024-01-01 /to 2024-01-02",
                "event meeting /from /to 2024-01-02", "event meeting /from 2024-01-01 /to",
                "event meeting /to 2024-01-02 /from 2024-01-01",
                "event meeting /from 2024-01-01 /from 2024-01-02 /to 2024-01-03",
                "event meeting /from 2024-01-01 /to 2024-01-02 /to 2024-01-03")) {
            assertThrows(SevenSixException.class, () -> Parser.parseEvent(command), command);
        }
    }

    /** Numeric arguments cannot contain signs, fractions, extra values, or overflow. */
    @Test
    void parseTaskNumberRejectsMalformedNumbers() {
        for (String number : List.of("", "+1", "-1", "1.5", "1 2", "999999999999999999999999", "one")) {
            assertThrows(SevenSixException.class,
                    () -> Parser.parseTaskNumber("mark " + number, Parser.COMMAND_MARK), number);
        }
    }

    /** Strict calendar validation prevents nonexistent dates and precision loss on disk. */
    @Test
    void parseDateRejectsInvalidDatesTimesAndUnsupportedPrecision() {
        for (String date : List.of("2024-02-30", "2023-02-29", "31/4/2024", "2024-13-01",
                "2024-01-01 2400", "2024-01-01 1260", "2024-01-01T12:00:01", "2024-01-01T12:00:00.001")) {
            assertThrows(SevenSixException.class, () -> DateTimeParser.parse(date), date);
        }
        assertThrows(SevenSixException.class, () -> DateTimeParser.parse(null));
        assertThrows(SevenSixException.class, () -> DateTimeParser.parse(""));
    }

    /** Argument-free commands should explain unexpected extra text. */
    @Test
    void parseCommandKeywordRejectsExtraArguments() {
        for (String command : List.of("list all", "undo 1", "bye now", "help todo")) {
            SevenSixException error = assertThrows(SevenSixException.class,
                    () -> Parser.parseCommandKeyword(command));
            assertEquals(Ui.UNEXPECTED_ARGUMENTS, error.getMessage());
        }
    }
}
