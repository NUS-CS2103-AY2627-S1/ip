package kaykay.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests place formatting, serialization, and matching. */
class PlaceTest {
    @Test
    void place_completeDetails_formatsForDisplayAndStorage() {
        Place place = new Place("Burnt Ends", "restaurant", "Dempsey",
                LocalDate.of(2026, 9, 10), 5, "Great brisket");

        assertEquals("[P] Burnt Ends (type: restaurant; location: Dempsey; "
                + "visited: 10 09 2026; rating: 5/5; notes: Great brisket)", place.toString());
        assertEquals("P | Burnt Ends | restaurant | Dempsey | 10 09 2026 | 5 | Great brisket",
                place.toFileFormat());
    }

    @Test
    void place_onlyName_omitsEmptyDetails() {
        Place place = new Place("NUS Computing", "", "", null, null, "");

        assertEquals("[P] NUS Computing", place.toString());
        assertEquals("P | NUS Computing |  |  |  |  | ", place.toFileFormat());
    }

    @Test
    void hasMatchingDetail_keyword_searchesAllDisplayedDetailsIgnoringCase() {
        Place place = new Place("Burnt Ends", "restaurant", "Dempsey",
                LocalDate.of(2026, 9, 10), 5, "Great brisket");

        assertTrue(place.hasMatchingDetail("BURNT"));
        assertTrue(place.hasMatchingDetail("brisket"));
        assertTrue(place.hasMatchingDetail("10 09 2026"));
        assertFalse(place.hasMatchingDetail("museum"));
        assertFalse(place.hasMatchingDetail("notes:"));
    }
}
