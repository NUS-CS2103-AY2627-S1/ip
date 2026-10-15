package koko;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests for the Parser class.
 */
public class ParserTest {

    /**
     * Tests that a valid deadline is parsed correctly.
     */
    @Test
    void parseDeadline_validDeadline_returnsDeadline() throws KokoException {
        Deadline deadline = Parser.parseDeadline("deadline return book /by 2026-09-01 1800");

        assertEquals(
                "return book",
                deadline.getDescription());
        assertEquals(
                LocalDateTime.of(2026, 9, 1, 18, 0),
                deadline.getBy());
    }

    /**
     * Tests that a deadline without a /by section is rejected.
     */
    @Test
    void parseDeadline_missingBy_throwsException() {
        assertThrows(KokoException.class, () -> Parser.parseDeadline("deadline return book"));
    }

    /**
     * Tests that an invalid date format is rejected.
     */
    @Test
    void parseDeadline_invalidDate_throwsException() {
        assertThrows(KokoException.class, () ->
                Parser.parseDeadline("deadline return book /by 01-09-2026 1800"));
    }

    /**
     * Tests that an invalid time is rejected.
     */
    @Test
    void parseDeadline_invalidTime_throwsException() {
        assertThrows(KokoException.class, () ->
                Parser.parseDeadline("deadline return book /by 2026-09-01 2500"));
    }

    /**
     * Tests that an empty deadline description is rejected.
     */
    @Test
    void parseDeadline_emptyDescription_throwsException() {
        assertThrows(KokoException.class, () ->
                Parser.parseDeadline("deadline /by 2026-09-01 1800"));
    }

    /**
     * Tests that an update with a description and event end time is parsed.
     */
    @Test
    void parseUpdate_multipleFields_returnsUpdateRequest() throws KokoException {
        UpdateRequest request = Parser.parseUpdate(
                "update 2 /desc project review /to 2026-09-20 1800", 3);

        assertEquals(1, request.getTaskIndex());
        assertEquals("project review", request.getDescription());
        assertEquals(LocalDateTime.of(2026, 9, 20, 18, 0), request.getTo());
    }

    /**
     * Tests that repeated update fields are rejected.
     */
    @Test
    void parseUpdate_repeatedField_throwsException() {
        assertThrows(KokoException.class, () -> Parser.parseUpdate(
                "update 1 /to 2026-09-20 1800 /to 2026-09-20 1900", 1));
    }
}
