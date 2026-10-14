package bingus.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import bingus.exception.BingusException;

/**
 * Tests parsing of task identifiers and list dates.
 */
class ParserValueTest {
    @Test
    void parseTaskId_validId_returnsId() {
        Parser parser = new Parser();
        assertEquals(2, parser.parseTaskId("2", 3));
    }

    @Test
    void parseTaskId_idWithSurroundingWhitespace_returnsId() {
        Parser parser = new Parser();

        assertEquals(3, parser.parseTaskId(" 3 ", 3));
    }

    @Test
    void parseTaskId_zero_throwsException() {
        Parser parser = new Parser();

        assertThrows(BingusException.class, () -> parser.parseTaskId("0", 3));
    }

    /**
     * Verifies that a negative task number is rejected.
     */
    @Test
    void parseTaskId_negativeId_throwsException() {
        Parser parser = new Parser();

        assertThrows(BingusException.class, () -> parser.parseTaskId("-1", 3));
    }

    @Test
    void parseTaskId_idGreaterThanTaskCount_throwsException() {
        Parser parser = new Parser();

        assertThrows(BingusException.class, () -> parser.parseTaskId("4", 3));
    }

    @Test
    void parseTaskId_nonNumericInput_throwsException() {
        Parser parser = new Parser();

        BingusException exception = assertThrows(
                BingusException.class, () -> parser.parseTaskId("two", 3));

        assertEquals("The task number must be a whole number.", exception.getMessage());
    }

    @Test
    void parseListDate_validDate_returnsDate() {
        Parser parser = new Parser();

        assertEquals(LocalDate.of(2026, 8, 21), parser.parseListDate("2026-08-21"));
    }

    @Test
    void parseListDate_invalidCalendarDate_throwsException() {
        Parser parser = new Parser();

        assertThrows(BingusException.class, () -> parser.parseListDate("2026-02-30"));
    }

    /**
     * Verifies that a list date must use the documented year-month-day format.
     */
    @Test
    void parseListDate_wrongFormat_throwsException() {
        Parser parser = new Parser();

        assertThrows(BingusException.class, () -> parser.parseListDate("20-09-2026"));
    }
}

