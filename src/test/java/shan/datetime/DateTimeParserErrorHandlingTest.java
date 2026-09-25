package shan.datetime;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import shan.exception.InvalidArgumentException;

class DateTimeParserErrorHandlingTest {
    @Test
    void parse_nullInput_invalidArgumentExceptionThrown() {
        assertThrows(InvalidArgumentException.class, () -> DateTimeParser.parse(null));
    }

    @Test
    void parseDate_nullInput_invalidArgumentExceptionThrown() {
        assertThrows(InvalidArgumentException.class, () -> DateTimeParser.parseDate(null));
    }
}
