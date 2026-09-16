package zucc.task;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import zucc.ZuccException;

/**
 * Tests the names and numeric levels accepted for task priorities.
 */
public class PriorityTest {
    /**
     * Verifies that priority names are case-insensitive and map to their expected levels.
     *
     * @throws ZuccException if a supported priority cannot be parsed.
     */
    @Test
    public void fromUserInput_namedPriorities_expectedLevelsReturned() throws ZuccException {
        assertAll(
                () -> assertEquals(Priority.HIGH, Priority.fromUserInput("HIGH")),
                () -> assertEquals(Priority.MEDIUM, Priority.fromUserInput("medium")),
                () -> assertEquals(Priority.LOW, Priority.fromUserInput(" low ")));
    }

    /**
     * Verifies that numeric priority aliases map one through three from high to low.
     *
     * @throws ZuccException if a supported priority cannot be parsed.
     */
    @Test
    public void fromUserInput_numericPriorities_expectedLevelsReturned() throws ZuccException {
        assertAll(
                () -> assertEquals(Priority.HIGH, Priority.fromUserInput("1")),
                () -> assertEquals(Priority.MEDIUM, Priority.fromUserInput("2")),
                () -> assertEquals(Priority.LOW, Priority.fromUserInput("3")));
    }

    /**
     * Verifies that an omitted priority produces the default no-priority value.
     *
     * @throws ZuccException if the default priority cannot be produced.
     */
    @Test
    public void fromUserInput_omittedPriority_noneReturned() throws ZuccException {
        assertEquals(Priority.NONE, Priority.fromUserInput(null));
    }

    /** Verifies that blank and unsupported priorities are rejected. */
    @Test
    public void fromUserInput_blankOrUnsupportedPriority_exceptionThrown() {
        assertAll(
                () -> assertThrows(ZuccException.class, () -> Priority.fromUserInput("")),
                () -> assertThrows(ZuccException.class, () -> Priority.fromUserInput("urgent")),
                () -> assertThrows(ZuccException.class, () -> Priority.fromUserInput("4")));
    }
}
