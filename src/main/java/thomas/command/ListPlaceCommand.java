package thomas.command;

import thomas.ThomasException;
import thomas.place.PlaceList;
import thomas.storage.PlaceStorage;
import thomas.ui.Ui;

/** Displays every saved place. */
public class ListPlaceCommand extends PlaceCommand {
    @Override
    public void execute(PlaceList places, Ui ui, PlaceStorage storage) throws ThomasException {
        ui.showPlaces(places);
    }
}
