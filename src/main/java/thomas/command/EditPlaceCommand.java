package thomas.command;

import thomas.ThomasException;
import thomas.place.Place;
import thomas.place.PlaceList;
import thomas.storage.PlaceStorage;
import thomas.ui.Ui;

/** Updates one field of a saved place. */
public class EditPlaceCommand extends PlaceCommand {
    private final int index;
    private final String field;
    private final String value;

    /** Creates an edit command for a one-based place index. */
    public EditPlaceCommand(int index, String field, String value) {
        this.index = index;
        this.field = field;
        this.value = value;
    }

    @Override
    public void execute(PlaceList places, Ui ui, PlaceStorage storage) throws ThomasException {
        Place place = getPlace(places);
        place.update(field, value);
        storage.save(places.getPlaces());
        ui.showPlaceUpdated(place, index);
    }

    private Place getPlace(PlaceList places) throws ThomasException {
        if (index < 1 || index > places.size()) {
            throw new ThomasException("Place number does not exist.");
        }
        return places.get(index - 1);
    }
}
