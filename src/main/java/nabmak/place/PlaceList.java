package nabmak.place;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the list of places and handles requests related to places.
 */

public class PlaceList {
    private final List<Place> places;

    /**
     * Creates a placelist with specified places.
     *
     * @param places places added to list
     */
    public PlaceList(List<Place> places) {
        this.places = new ArrayList<>(places);
    }

    /**
     * Creates an empty place list.
     */
    public PlaceList() {
        this.places = new ArrayList<>();
    }

    /**
     * Returns the place present at specified index.
     *
     * @param index index of place
     * @return place at index
     */
    public Place get(int index) {
        assert index >= 0 && index < places.size();
        return places.get(index);
    }

    /**
     * Adds place into the place list.
     *
     * @param place place to be added
     */
    public void add(Place place) {
        places.add(place);
    }

    /**
     * Deletes specified place from place list and returns the place.
     *
     * @param index index of place
     * @return deleted place
     */
    public Place delete(int index) {
        assert index >= 0 && index < places.size();
        return places.remove(index);
    }

    /**
     * Returns the list of places.
     *
     * @return list of places
     */
    public List<Place> getPlaces() {
        return List.copyOf(places);
    }

    /**
     * Returns the number of places in the list.
     *
     * @return number of places
     */
    public int size() {
        return places.size();
    }
}
