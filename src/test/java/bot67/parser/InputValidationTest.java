package bot67.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

import bot67.exception.Bot67Exception;

/** Covers malformed commands that previously became invalid persisted tasks. */
class InputValidationTest {
    private final Parser parser = new Parser();

    @Test
    void normalize_acceptsSpacingAndRejectsRecordSeparators() throws Bot67Exception {
        assertEquals("todo read book", parser.normalize("  todo\t read   book  "));
        for (String input : new String[]{null, " ", "todo a | b", "todo a\nb", "todo a\u0000b"}) {
            assertThrows(Bot67Exception.class, () -> parser.normalize(input));
        }
    }

    @Test
    void deadline_rejectsMissingRepeatedAndUnknownParameters() {
        for (String input : new String[]{"deadline  /by Sunday", "deadline book /by Sunday /by Monday",
            "deadline book /by Sunday /from Monday", "deadline book /by", "deadline book /by 2026-2-30"}) {
            assertThrows(Bot67Exception.class, () -> parser.requireValidDeadline(input));
        }
    }

    @Test
    void event_rejectsEmptyReorderedRepeatedAndNonIncreasingEndpoints() {
        for (String input : new String[]{"event  /from Mon /to Tue", "event book /to Tue /from Mon",
            "event book /from Mon /from Tue /to Wed", "event book /from Mon /to Tue /to Wed",
            "event book /from 2026-10-15 /to 2026-10-15",
            "event book /from 2026-10-16T14:00 /to 2026-10-15T14:00"}) {
            assertThrows(Bot67Exception.class, () -> parser.requireValidEvent(input));
        }
    }

    @Test
    void dates_rejectImpossibleDatesInBothTimeFormats() throws Bot67Exception {
        for (String input : new String[]{"2026-02-30", "2026-02-30T12:00", "2026-02-30 12:00",
            "2026-10-15 24:00", "2026-10-15T25:00"}) {
            assertThrows(DateTimeParseException.class, () -> DateTimeParser.parse(input));
            assertThrows(Bot67Exception.class, () -> parser.requireValidDeadline("deadline task /by " + input));
        }
        parser.requireValidDeadline("deadline task /by 2028-02-29 12:00");
        parser.requireValidEvent("event meeting /from Mon 2pm /to 4pm");
    }
}
