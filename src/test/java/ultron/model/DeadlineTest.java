package ultron.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ultron.exception.UltronException;

/**
 * Tests deadline parsing and display formatting.
 */
class DeadlineTest {

    @Test
    void getDescription_deadlineWithDate_formatsDate() throws UltronException {
        Deadline deadline = new Deadline("return book /by 2/12/2019");

        assertEquals("return book( by: 02/Dec/2019 )", deadline.getDescription());
    }

    @Test
    void getDescription_deadlineWithDateAndTime_formatsDateAndTime() throws UltronException {
        Deadline deadline = new Deadline("return book /by 2/12/2019 1800");

        assertEquals("return book( by: 02/Dec/2019 1800 )", deadline.getDescription());
    }

    @Test
    void constructor_missingByMarker_exceptionThrown() {
        assertThrows(UltronException.class, () -> new Deadline("return book"));
    }

    @Test
    void constructor_invalidDate_exceptionThrown() {
        assertThrows(UltronException.class, () -> new Deadline("return book /by tomorrow"));
    }
}
