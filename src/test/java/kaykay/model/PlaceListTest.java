package kaykay.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests place-list searching, mutation, and place-number validation. */
class PlaceListTest {
    @Test
    void placeList_mutations_preserveExpectedOrderAndSize() {
        Place first = createPlace("first", "restaurant");
        Place second = createPlace("second", "museum");
        Place replacement = createPlace("replacement", "park");
        PlaceList places = new PlaceList(first);

        places.add(second);
        assertEquals(2, places.size());
        assertEquals(first, places.getPlace(0));
        assertEquals(second, places.getPlace(1));

        assertEquals(first, places.set(0, replacement));
        assertEquals(replacement, places.remove(0));
        places.remove(second);
        assertEquals(0, places.size());
    }

    @Test
    void findPlaces_keyword_returnsMatchingPlacesInOrder() {
        Place first = createPlace("Burnt Ends", "restaurant");
        Place second = createPlace("ArtScience Museum", "museum");
        Place third = createPlace("Another Restaurant", "restaurant");
        PlaceList places = new PlaceList(first, second, third);

        assertEquals(List.of(first, third), places.findPlaces("RESTAURANT"));
    }

    @Test
    void isValidPlaceNumber_placeNumber_isAcceptedOnlyWhenInRange() {
        PlaceList places = new PlaceList(createPlace("one", ""));

        assertTrue(places.isValidPlaceNumber("1"));
        assertFalse(places.isValidPlaceNumber("0"));
        assertFalse(places.isValidPlaceNumber("2"));
        assertFalse(places.isValidPlaceNumber("not-a-number"));
    }

    /** Creates a place with only the fields relevant to these tests. */
    private static Place createPlace(String name, String type) {
        return new Place(name, type, "", null, null, "");
    }
}
