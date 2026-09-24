package aravii;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Covers malformed command boundaries and strict calendar validation.
 */
class ParserTest {
    @ParameterizedTest
    @ValueSource(strings = {"", "Hi", "todo", "deadline", "event", "find", "mark", "unmark", "delete",
        "list extra", "bye extra", "sort extra", "todo one\ttwo", "todo one\ntwo", "todo test\r"})
    void parse_invalidSyntax_throwsHelpfulError(String input) {
        assertThrows(IllegalArgumentException.class, () -> Parser.parse(input));
    }

    @Test
    void parse_nullInput_throwsHelpfulError() {
        assertThrows(IllegalArgumentException.class, () -> Parser.parse(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"deadline /by 2026-09-20", "deadline work /by", "deadline work",
        "deadline work /by 2026-02-30", "deadline work /by 2026-02-29", "deadline work /by 20/09/2026",
        "event /from 2026-09-21 14:00 /to 2026-09-21 15:00", "event work /from /to",
        "event work /to 2026-09-21 14:00 /from 2026-09-21 15:00",
        "event work /from 2026-02-30 14:00 /to 2026-02-30 15:00",
        "event work /from 2026-09-21 24:00 /to 2026-09-22 01:00",
        "event work /from 2026-09-21 15:00 /to 2026-09-21 14:00"})
    void parseTask_invalidArguments_rejectsWithoutCrashing(String input) {
        assertThrows(IllegalArgumentException.class, () -> Parser.parseTask(Parser.parse(input)));
    }

    @Test
    void parseTask_validLeapDay_preservesTypedDate() {
        Task task = Parser.parseTask(Parser.parse("  deadline   leap day /by 2028-02-29  "));
        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals(LocalDate.of(2028, 2, 29), deadline.getDueDate());
    }

    @Test
    void parseTask_sameTimeAndOvernightEvents_areValid() {
        assertInstanceOf(Event.class, Parser.parseTask(Parser.parse(
                "event point /from 2026-09-21 14:00 /to 2026-09-21 14:00")));
        assertInstanceOf(Event.class, Parser.parseTask(Parser.parse(
                "event overnight /from 2026-09-21 23:00 /to 2026-09-22 01:00")));
    }
}
