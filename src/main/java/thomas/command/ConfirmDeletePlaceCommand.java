package thomas.command;

import thomas.ThomasException;
import thomas.place.Place;
import thomas.place.PlaceList;
import thomas.storage.PlaceStorage;
import thomas.ui.Ui;

/** Deletes a saved place after the application has recorded a matching request. */
public class ConfirmDeletePlaceCommand extends PlaceCommand {
    private final int index;

    /** Creates a confirmation for a one-based place index. */
    public ConfirmDeletePlaceCommand(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public void execute(PlaceList places, Ui ui, PlaceStorage storage) throws ThomasException {
        if (index < 1 || index > places.size()) {
            throw new ThomasException("Place number does not exist.");
        }
        Place deleted = places.delete(index - 1);
        storage.save(places.getPlaces());
        ui.showPlaceDeleted(deleted, index, places.size());
    }
}
