package thomas.command;

import java.time.LocalDate;
import java.util.Map;

import thomas.ThomasException;
import thomas.place.Place;
import thomas.place.PlaceList;
import thomas.storage.PlaceStorage;
import thomas.ui.Ui;

/** Adds a place to the saved-place collection. */
public class AddPlaceCommand extends PlaceCommand {
    private final String name;
    private final Map<String, String> fields;

    /** Creates a command with a name and parsed place fields. */
    public AddPlaceCommand(String name, Map<String, String> fields) {
        this.name = name;
        this.fields = fields;
    }

    @Override
    public void execute(PlaceList places, Ui ui, PlaceStorage storage) throws ThomasException {
        if (name.trim().isEmpty()) {
            throw new ThomasException("Place name cannot be empty.");
        }
        require("type", "Place requires /type <type>.");
        require("at", "Place requires /at <address>.");
        require("rating", "Place requires /rating <1-5>.");
        require("price", "Place requires /price <amount>.");
        LocalDate date = fields.containsKey("visited") ? Place.parseDate(fields.get("visited")) : null;
        String note = fields.get("note");
        Place place = new Place(name, fields.get("type"), fields.get("at"),
                Place.parseRating(fields.get("rating")), fields.get("price"), date, note);
        if (places.containsEquivalent(place)) {
            throw new ThomasException("This place already exists.");
        }
        places.add(place);
        storage.save(places.getPlaces());
        ui.showPlaceAdded(place, places.size());
    }

    private void require(String field, String message) throws ThomasException {
        if (!fields.containsKey(field)) {
            throw new ThomasException(message);
        }
    }
}
