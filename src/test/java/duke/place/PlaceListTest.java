package duke.place;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import duke.exception.DukeException;

/**
 * Unit tests for the {@link PlaceList} class.
 */
public class PlaceListTest {
    private PlaceList placeList;
    private Place restaurant;
    private Place attraction;

    @BeforeEach
    public void setUp() {
        placeList = new PlaceList();
        restaurant = new Place("Jumbo Seafood", "Best chili crab");
        attraction = new Place("Gardens by the Bay", "Supertrees");
    }

    @Test
    public void addPlace_singlePlace_incrementsSize() {
        assertEquals(0, placeList.size());
        placeList.addPlace(restaurant);
        assertEquals(1, placeList.size());
    }

    @Test
    public void contains_existingAndNonExistingPlaces_returnsExpected() {
        placeList.addPlace(restaurant);
        org.junit.jupiter.api.Assertions.assertTrue(
                placeList.contains(new Place("Jumbo Seafood", "Best chili crab")));
        org.junit.jupiter.api.Assertions.assertFalse(placeList.contains(attraction));
    }

    @Test
    public void getPlace_validIndex_returnsPlace() throws DukeException {
        placeList.addPlace(restaurant);
        assertEquals(restaurant, placeList.getPlace(0));
    }

    @Test
    public void getPlace_invalidIndex_throwsException() {
        assertThrows(DukeException.class, () -> placeList.getPlace(0));
        assertThrows(DukeException.class, () -> placeList.getPlace(-1));
    }

    @Test
    public void deletePlace_validIndex_removesAndReturnsPlace() throws DukeException {
        placeList.addPlace(restaurant);
        placeList.addPlace(attraction);

        Place removed = placeList.deletePlace(0);
        assertEquals(restaurant, removed);
        assertEquals(1, placeList.size());
        assertEquals(attraction, placeList.getPlace(0));
    }

    @Test
    public void deletePlace_invalidIndex_throwsException() {
        assertThrows(DukeException.class, () -> placeList.deletePlace(0));
    }

    @Test
    public void findPlaces_matchingKeyword_returnsMatches() throws DukeException {
        placeList.addPlace(restaurant);
        placeList.addPlace(attraction);
        placeList.addPlace(new Place("Lau Pa Sat", "Satay street"));

        PlaceList found = placeList.findPlaces("crab");
        assertEquals(1, found.size());
        assertEquals(restaurant, found.getPlace(0));

        PlaceList foundAll = placeList.findPlaces("by");
        assertEquals(1, foundAll.size());
        assertEquals(attraction, foundAll.getPlace(0));
    }

    @Test
    public void undo_revertsPlaceListModifications() throws DukeException {
        placeList.addPlace(restaurant);
        placeList.saveSnapshot();

        placeList.addPlace(attraction);
        assertEquals(2, placeList.size());

        placeList.undo();
        assertEquals(1, placeList.size());
        assertEquals(restaurant, placeList.getPlace(0));
    }

    @Test
    public void undo_emptyHistory_throwsException() {
        DukeException ex = assertThrows(DukeException.class, () -> placeList.undo());
        assertEquals("OOPS!!! There are no previous place commands to undo.", ex.getMessage());
    }

    @Test
    public void canUndo_reflectsHistoryState() {
        assertFalse(placeList.canUndo());
        placeList.saveSnapshot();
        assertTrue(placeList.canUndo());
    }

    @Test
    public void constructors_andCopy_operateCorrectly() {
        ArrayList<Place> initial = new ArrayList<>();
        initial.add(restaurant);
        PlaceList listFromCollection = new PlaceList(initial);
        assertEquals(1, listFromCollection.size());

        PlaceList listFromVarargs = new PlaceList(restaurant, attraction);
        assertEquals(2, listFromVarargs.size());

        PlaceList copied = listFromVarargs.copy();
        assertEquals(2, copied.size());
        assertEquals(2, listFromVarargs.getPlaces().size());
    }

    @Test
    public void nullArguments_throwAssertionErrors() {
        org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () ->
                new PlaceList((Place[]) null));
        org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () ->
                new PlaceList((ArrayList<Place>) null));
        org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () -> placeList.addPlace(null));
        org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () -> placeList.contains(null));
        org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () -> placeList.findPlaces(null));
    }
}
