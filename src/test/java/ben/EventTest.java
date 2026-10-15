package ben;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests for {@link Event}: from/to display, serialization, and the same-from/to guard. */
class EventTest {
    @Test
    void toString_showsFromAndTo() throws BenException {
        Event event = new Event("trip", "Mon", "Fri");
        assertEquals("[E][ ] trip (from: Mon to: Fri)", event.toString());
    }

    @Test
    void serialize_includesFromAndToAsSeparateFields() throws BenException {
        Event event = new Event("trip", "Mon", "Fri");
        assertEquals("E | 0 | trip | Mon | Fri", event.serialize());
    }

    @Test
    void constructor_fromEqualsToIgnoringCaseAndSpacing_throwsBenException() {
        assertThrows(BenException.class, () -> new Event("trip", " Mon 2pm ", "mon 2pm"));
    }

    @Test
    void constructor_fromDifferentFromTo_succeeds() throws BenException {
        Event event = new Event("trip", "Mon 2pm", "Mon 4pm");
        assertEquals("[E][ ] trip (from: Mon 2pm to: Mon 4pm)", event.toString());
    }
}
