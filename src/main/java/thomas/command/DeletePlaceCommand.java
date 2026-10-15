package thomas.command;

import thomas.ThomasException;
import thomas.place.Place;
import thomas.place.PlaceList;
import thomas.storage.PlaceStorage;
import thomas.ui.Ui;

/** Requests confirmation before deleting a saved place. */
public class DeletePlaceCommand extends PlaceCommand {
    private final int index;

    /** Creates a deletion request for a one-based place index. */
    public DeletePlaceCommand(int index) {
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
        Place place = places.get(index - 1);
        ui.showPlaceDeletionRequest(place, index);
    }
}
