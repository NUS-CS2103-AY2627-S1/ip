package walter.place;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import walter.DukeException;

/**
 * Tests saved-place collection operations and index boundaries.
 */
public class PlaceListTest {
    @Test
    public void addAndDelete_validPlaces_orderAndRemovedPlaceReturned() throws DukeException {
        Place first = new Place("home", "Clementi");
        Place second = new Place("school", "Kent Ridge");
        PlaceList places = new PlaceList(List.of(first));

        places.add(second);
        Place removed = places.delete(0);

        assertSame(first, removed);
        assertEquals(1, places.size());
        assertSame(second, places.getPlaces().get(0));
    }

    @Test
    public void delete_invalidIndices_exceptionThrownWithoutChangingPlaces() {
        Place place = new Place("home", "Clementi");
        PlaceList places = new PlaceList(List.of(place));

        assertThrows(DukeException.class, () -> places.delete(-1));
        assertThrows(DukeException.class, () -> places.delete(1));
        assertEquals(List.of(place), places.getPlaces());
    }

    @Test
    public void getPlaces_returnedSnapshotCannotMutateInternalList() {
        PlaceList places = new PlaceList(List.of(new Place("home", "Clementi")));
        List<Place> snapshot = places.getPlaces();
        Place additionalPlace = new Place("school", "Kent Ridge");

        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(additionalPlace));

        places.add(new Place("library", "NUS"));
        assertEquals(1, snapshot.size());
        assertEquals(2, places.size());
    }
}
