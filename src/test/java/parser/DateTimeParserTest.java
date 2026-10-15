package parser;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import cxception.InvalidDateException;
import tasklist.MyDateTime;

public class DateTimeParserTest {

    @Test
    public void parse_validDateTime_success() throws Exception {
        String input = "2026-08-28 1430";
        MyDateTime result = DateTimeParser.parse(input);
        assertNotNull(result);
    }

    @Test
    public void parse_validDateOnly_success() throws Exception {
        String input = "2026-08-28";
        MyDateTime result = DateTimeParser.parse(input);
        assertNotNull(result);
    }

    @Test
    public void parse_invalidFormats_exceptionThrown() {
        assertThrows(InvalidDateException.class, () -> DateTimeParser.parse("28-08-2026"));
        assertThrows(InvalidDateException.class, () -> DateTimeParser.parse("2026/08/28"));
        assertThrows(InvalidDateException.class, () -> DateTimeParser.parse("2026-08-28 14:30"));
        assertThrows(InvalidDateException.class, () -> DateTimeParser.parse("2026-8-28"));
        assertThrows(InvalidDateException.class, () -> DateTimeParser.parse("invalid-date"));
        assertThrows(InvalidDateException.class, () -> DateTimeParser.parse(""));
        assertThrows(InvalidDateException.class, () -> DateTimeParser.parse("   "));
    }

    @Test
    public void parse_nullInput_exceptionThrown() {
        assertThrows(NullPointerException.class, () -> DateTimeParser.parse(null));
    }
}
