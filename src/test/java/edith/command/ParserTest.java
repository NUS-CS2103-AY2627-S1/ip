package edith.command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import edith.EdithException;

/** Tests validation performed while converting command text into commands. */
class ParserTest {
    @Test
    void parse_commandHasLeadingOrRepeatedSpaces_throwsException() {
        assertThrows(EdithException.class, () -> Parser.parse(" todo read book"));
        assertThrows(EdithException.class, () -> Parser.parse("todo  read book"));
        assertThrows(EdithException.class, () -> Parser.parse("todo read book "));
    }

    @Test
    void parse_taskNumberIsMissingInvalidOrTooLarge_throwsException() {
        assertThrows(EdithException.class, () -> Parser.parse("mark"));
        assertThrows(EdithException.class, () -> Parser.parse("delete 0"));
        assertThrows(EdithException.class, () -> Parser.parse("unmark one"));
        assertThrows(EdithException.class, () -> Parser.parse("mark 999999999999999999999"));
    }

    @Test
    void parse_taskParametersAreMissingOrRepeated_throwsException() {
        assertThrows(EdithException.class, () -> Parser.parse("deadline finish report"));
        assertThrows(EdithException.class, () -> Parser.parse("deadline finish /by June 6 /by June 7"));
        assertThrows(EdithException.class, () -> Parser.parse("event meeting /from June 6"));
        assertThrows(EdithException.class, () -> Parser.parse("event meeting /from June 6 /to 4pm /to 5pm"));
    }

    @Test
    void parse_invalidDatesAndEventRange_throwsException() {
        assertThrows(EdithException.class, () -> Parser.parse("deadline pay bill /by 2026-02-30"));
        assertThrows(EdithException.class, () -> Parser.parse("event meeting /from 2026-06-06T16:00 /to 4pm"));
        assertThrows(EdithException.class, () -> Parser.parse("event meeting /from 2026-06-07 /to 2026-06-06"));
    }

    @Test
    void parse_validEventWithTimeOnlyEnd_returnsCommand() {
        assertDoesNotThrow(() -> Parser.parse("event meeting /from June 6th 2pm /to 4pm"));
    }
}
