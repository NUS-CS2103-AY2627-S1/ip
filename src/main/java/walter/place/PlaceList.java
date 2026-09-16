package walter.place;

import java.util.ArrayList;
import java.util.List;

import walter.DukeException;

/**
 * Owns Walter's ordered collection of saved places.
 */
public class PlaceList {
    private final List<Place> places;

    /**
     * Creates a place collection from records loaded by storage.
     *
     * @param places Initial places in insertion order.
     */
    public PlaceList(List<Place> places) {
        this.places = new ArrayList<>(places);
    }

    /**
     * Appends a place to the collection.
     *
     * @param place Place to append.
     */
    public void add(Place place) {
        places.add(place);
    }

    /**
     * Deletes a place at a validated zero-based index.
     *
     * @param placeIndex Zero-based place index.
     * @return Deleted place.
     * @throws DukeException If the index does not identify a saved place.
     */
    public Place delete(int placeIndex) throws DukeException {
        if (placeIndex < 0 || placeIndex >= places.size()) {
            throw new DukeException("Place number is out of range.");
        }
        return places.remove(placeIndex);
    }

    /**
     * Returns the number of saved places.
     *
     * @return Current place count.
     */
    public int size() {
        return places.size();
    }

    /**
     * Returns a read-only snapshot preserving insertion order.
     *
     * @return Snapshot of saved places.
     */
    public List<Place> getPlaces() {
        return List.copyOf(places);
    }
}
