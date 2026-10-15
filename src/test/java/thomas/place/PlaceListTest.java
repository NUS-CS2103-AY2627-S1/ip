package thomas.place;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

class PlaceListTest {
    @Test
    void addGetDeleteAndSize_maintainPlaceOrder() throws Exception {
        Place first = place("Sushi");
        Place second = place("Cafe");
        PlaceList places = new PlaceList();

        places.add(first);
        places.add(second);

        assertEquals(2, places.size());
        assertSame(first, places.get(0));
        assertSame(first, places.delete(0));
        assertSame(second, places.get(0));
        assertEquals(1, places.size());
    }

    @Test
    void constructor_existingCollection_usesProvidedPlaces() throws Exception {
        ArrayList<Place> original = new ArrayList<>();
        Place place = place("Sushi");
        original.add(place);

        PlaceList places = new PlaceList(original);

        assertSame(original, places.getPlaces());
        assertSame(place, places.get(0));
    }

    @Test
    void findByName_mixedCasePartialKeyword_returnsMatches() throws Exception {
        Place sushi = place("Sushi Place");
        PlaceList places = new PlaceList();
        places.add(sushi);
        places.add(place("Cafe"));

        ArrayList<Place> matches = places.findByName("sUsHi");

        assertEquals(1, matches.size());
        assertSame(sushi, matches.get(0));
        assertEquals(1, places.getOneBasedIndex(sushi));
        assertEquals(0, places.getOneBasedIndex(place("Missing")));
    }

    @Test
    void containsEquivalent_caseDifferences_returnsTrue() throws Exception {
        PlaceList places = new PlaceList();
        places.add(place("Sushi"));

        assertTrue(places.containsEquivalent(place("sUsHi")));
        assertFalse(places.containsEquivalent(place("Cafe")));
    }

    private Place place(String name) throws Exception {
        return new Place(name, "Restaurant", "Town", 4, "12", null, null);
    }
}
