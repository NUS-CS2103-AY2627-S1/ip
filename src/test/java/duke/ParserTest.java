package duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/** Tests command argument and date parsing. */
public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    public void parseDate_validDateTime_returnsExpectedValue() {
        assertEquals(LocalDateTime.of(2026, 9, 18, 18, 0),
                parser.parseDate("2026-09-18 18:00"));
    }

    @Test
    public void parseDate_invalidDateTime_throwsException() {
        assertThrows(DateTimeParseException.class,
                () -> parser.parseDate("18 September 2026"));
    }

    @Test
    public void argument_trimsTextAfterCommandPrefix() {
        assertEquals("read book", parser.argument("todo read book", 5));
    }

    @Test
    public void normalizeCommand_irregularWhitespace_returnsSingleSpacedCommand() {
        assertEquals("deadline submit report /by 2026-09-18 18:00",
                parser.normalizeCommand("  deadline   submit report   /by   2026-09-18 18:00  "));
    }
}
