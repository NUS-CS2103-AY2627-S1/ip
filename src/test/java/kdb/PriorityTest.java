package kdb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests parsing and displaying task priorities. */
class PriorityTest {
    @Test
    void fromInput_acceptsPriorityNamesRegardlessOfCase() {
        assertEquals(Priority.HIGH, Priority.fromInput("HIGH"));
        assertEquals(Priority.MEDIUM, Priority.fromInput(" medium "));
        assertEquals(Priority.LOW, Priority.fromInput("low"));
    }

    @Test
    void fromInput_blankInputDefaultsToMedium() {
        assertEquals(Priority.MEDIUM, Priority.fromInput("  "));
    }

    @Test
    void fromInput_invalidInputThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> Priority.fromInput("urgent"));
    }

    @Test
    void toString_returnsLowercaseName() {
        assertEquals("high", Priority.HIGH.toString());
    }
}
