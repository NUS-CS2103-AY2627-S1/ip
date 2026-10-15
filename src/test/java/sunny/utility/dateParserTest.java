package sunny.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class DateParserTest {

    @Test
    public void getInstanceReturnsSameInstance() {
        DateParser first = DateParser.getInstance();
        DateParser second = DateParser.getInstance();

        assertSame(first, second);
    }

    @Test
    public void parseValidDate() {
        DateParser parser = DateParser.getInstance();

        Optional<LocalDateTime> result = parser.parse("24/08/2026");

        assertTrue(result.isPresent());
        assertEquals(
                LocalDateTime.of(2026, 8, 24, 23, 59),
                result.get()
        );
    }

    @Test
    public void parseNullThrowsCorrectException() {
        DateParser parser = DateParser.getInstance();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(null)
        );

        assertEquals("Input string cannot be null", exception.getMessage());
    }

    @Test
    public void parseSlashDate() {
        DateParser parser = DateParser.getInstance();

        Optional<LocalDateTime> result = parser.parse("24/08/2026");

        assertTrue(result.isPresent());
        assertEquals(
                LocalDateTime.of(2026, 8, 24, 23, 59),
                result.get()
        );
    }

    @Test
    public void parseDashDate() {
        DateParser parser = DateParser.getInstance();

        Optional<LocalDateTime> result = parser.parse("24-08-2026");

        assertTrue(result.isPresent());
        assertEquals(
                LocalDateTime.of(2026, 8, 24, 23, 59),
                result.get()
        );
    }

    @Test
    public void parseDateWithTime() {
        DateParser parser = DateParser.getInstance();

        Optional<LocalDateTime> result = parser.parse("24/08/2026 1430");

        assertTrue(result.isPresent());
        assertEquals(
                LocalDateTime.of(2026, 8, 24, 14, 30),
                result.get()
        );
    }

    @Test
    public void parseTrimsWhitespace() {
        DateParser parser = DateParser.getInstance();

        Optional<LocalDateTime> result =
                parser.parse("   24/08/2026   ");

        assertTrue(result.isPresent());
        assertEquals(
                LocalDateTime.of(2026, 8, 24, 23, 59),
                result.get()
        );
    }

    @Test
    public void parseInvalidCalendarDateReturnsEmpty() {
        DateParser parser = DateParser.getInstance();

        Optional<LocalDateTime> result =
                parser.parse("29/02/2026");

        assertTrue(result.isEmpty());
    }
}
