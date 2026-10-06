package duke.place;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.stream.Collectors;

import duke.exception.DukeException;

/**
 * Encapsulates the collection of places and operations on the place list.
 */
public class PlaceList {
    private final ArrayList<Place> places;
    private final Deque<ArrayList<Place>> history = new ArrayDeque<>();

    /**
     * Constructs an empty PlaceList.
     */
    public PlaceList() {
        this.places = new ArrayList<>();
    }

    /**
     * Constructs a PlaceList populated with the specified places.
     *
     * @param places The initial places to populate the list with.
     */
    public PlaceList(Place... places) {
        assert places != null : "Places array must not be null";
        this.places = new ArrayList<>(Arrays.asList(places));
    }

    /**
     * Constructs a PlaceList backed by the provided list of places.
     *
     * @param places The initial list of places.
     */
    public PlaceList(ArrayList<Place> places) {
        assert places != null : "Places list must not be null";
        this.places = places;
    }

    /**
     * Adds a place to the list.
     *
     * @param place The place to add.
     */
    public void addPlace(Place place) {
        assert place != null : "Place to add must not be null";
        places.add(place);
    }

    /**
     * Checks if the place list contains the specified place.
     *
     * @param place The place to search for.
     * @return True if the place list contains the place, false otherwise.
     */
    public boolean contains(Place place) {
        assert place != null : "Place to check must not be null";
        return places.contains(place);
    }

    /**
     * Deletes a place from the list at the specified index.
     *
     * @param index The 0-based index of the place to delete.
     * @return The removed place.
     * @throws DukeException If the index is out of bounds.
     */
    public Place deletePlace(int index) throws DukeException {
        if (index < 0 || index >= places.size()) {
            throw new DukeException("OOPS!!! The place number is invalid.");
        }
        return places.remove(index);
    }

    /**
     * Retrieves a place from the list at the specified index.
     *
     * @param index The 0-based index of the place to retrieve.
     * @return The place at the specified index.
     * @throws DukeException If the index is out of bounds.
     */
    public Place getPlace(int index) throws DukeException {
        if (index < 0 || index >= places.size()) {
            throw new DukeException("OOPS!!! The place number is invalid.");
        }
        return places.get(index);
    }

    /**
     * Returns the number of places in the list.
     *
     * @return The size of the place list.
     */
    public int size() {
        return places.size();
    }

    /**
     * Returns the underlying list of places.
     *
     * @return The ArrayList of places.
     */
    public ArrayList<Place> getPlaces() {
        return places;
    }

    /**
     * Finds all places whose name or details contain the specified keyword.
     *
     * @param keyword The search keyword.
     * @return A new PlaceList containing matching places.
     */
    public PlaceList findPlaces(String keyword) {
        assert keyword != null : "Keyword must not be null";
        ArrayList<Place> matchingPlaces = places.stream()
                .filter(p -> p.toString().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toCollection(ArrayList::new));
        return new PlaceList(matchingPlaces);
    }

    /**
     * Saves the current list state to the history stack for undo operations.
     */
    public void saveSnapshot() {
        ArrayList<Place> snapshot = new ArrayList<>();
        for (Place place : places) {
            snapshot.add(place.copy());
        }
        history.push(snapshot);
    }

    /**
     * Reverts the place list to its previous state.
     *
     * @throws DukeException If there is no previous state to revert to.
     */
    public void undo() throws DukeException {
        if (history.isEmpty()) {
            throw new DukeException("OOPS!!! There are no previous place commands to undo.");
        }
        ArrayList<Place> previousState = history.pop();
        places.clear();
        places.addAll(previousState);
    }

    /**
     * Returns whether there is an undo state available in history.
     *
     * @return True if undo is possible, false otherwise.
     */
    public boolean canUndo() {
        return !history.isEmpty();
    }

    /**
     * Creates and returns a deep copy of this PlaceList.
     *
     * @return A deep copy of this PlaceList.
     */
    public PlaceList copy() {
        ArrayList<Place> copiedPlaces = new ArrayList<>();
        for (Place place : places) {
            copiedPlaces.add(place.copy());
        }
        return new PlaceList(copiedPlaces);
    }
}
