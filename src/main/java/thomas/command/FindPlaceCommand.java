package thomas.command;

import thomas.ThomasException;
import thomas.place.PlaceList;
import thomas.storage.PlaceStorage;
import thomas.ui.Ui;

/** Finds saved places by a name keyword. */
public class FindPlaceCommand extends PlaceCommand {
    private final String keyword;

    /** Creates a search command for the supplied keyword. */
    public FindPlaceCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(PlaceList places, Ui ui, PlaceStorage storage) throws ThomasException {
        if (keyword.isEmpty()) {
            throw new ThomasException("Please provide a place name to find. E.g., findplace sushi");
        }
        ui.showMatchingPlaces(places, keyword);
    }
}
