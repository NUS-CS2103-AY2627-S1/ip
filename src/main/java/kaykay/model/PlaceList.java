package kaykay.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Stores the places that Kaykay manages and provides place-list operations.
 */
public final class PlaceList {
    /** Places in their current display order. */
    private final ArrayList<Place> places;

    /** Creates an empty place list. */
    public PlaceList() {
        places = new ArrayList<>();
    }

    /**
     * Creates a place list containing a copy of the supplied places.
     *
     * @param places places loaded from storage.
     */
    public PlaceList(List<Place> places) {
        this.places = new ArrayList<>(places);
    }

    /**
     * Creates a place list containing the supplied places.
     *
     * @param places places to add to the list.
     */
    public PlaceList(Place... places) {
        this.places = new ArrayList<>(Arrays.asList(places));
    }

    public int size() {
        return places.size();
    }

    public Place getPlace(int index) {
        return places.get(index);
    }

    /**
     * Returns places whose details contain the keyword, ignoring letter case.
     *
     * @param keyword text to search for.
     * @return matching places in their current display order.
     */
    public List<Place> findPlaces(String keyword) {
        List<Place> matchingPlaces = new ArrayList<>();
        for (Place place : places) {
            if (place.hasMatchingDetail(keyword)) {
                matchingPlaces.add(place);
            }
        }
        return matchingPlaces;
    }

    public void add(Place place) {
        places.add(place);
    }

    public void add(int index, Place place) {
        places.add(index, place);
    }

    public Place remove(int index) {
        return places.remove(index);
    }

    public void remove(Place place) {
        places.remove(place);
    }

    public Place set(int index, Place place) {
        return places.set(index, place);
    }

    /**
     * Checks whether a value identifies an existing one-based place number.
     *
     * @param value place number entered by the user.
     * @return true if the value identifies a place in this list.
     */
    public boolean isValidPlaceNumber(String value) {
        try {
            int placeNumber = Integer.parseInt(value);
            return placeNumber >= 1 && placeNumber <= places.size();
        } catch (NumberFormatException exception) {
            return false;
        }
    }
}
