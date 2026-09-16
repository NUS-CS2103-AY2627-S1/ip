package walter.place;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests place accessors and user-facing formatting.
 */
public class PlaceTest {
    @Test
    public void constructor_validDetails_accessorsAndDisplayPreserveDetails() {
        Place place = new Place("NUS Central Library", "12 Computing Drive");

        assertEquals("NUS Central Library", place.getName());
        assertEquals("12 Computing Drive", place.getAddress());
        assertEquals("NUS Central Library — 12 Computing Drive", place.toString());
    }
}
