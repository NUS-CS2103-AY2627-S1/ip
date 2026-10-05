package kaykay.command;

import java.io.IOException;

import kaykay.model.ApplicationData;
import kaykay.model.Place;
import kaykay.model.PlaceList;
import kaykay.storage.Storage;
import kaykay.ui.Ui;

/**
 * Adds a place and persists its details.
 */
public final class AddPlaceCommand extends Command {
    /** Place to add. */
    private final Place place;

    /**
     * Creates a command that adds the supplied place.
     *
     * @param place place to add.
     */
    public AddPlaceCommand(Place place) {
        this.place = place;
    }

    /**
     * Adds and saves the place, rolling back the addition if saving fails.
     *
     * @param data application data containing the place list to modify.
     * @param ui UI used to show the confirmation.
     * @param storage storage used to persist the updated data.
     * @throws IOException if the updated data cannot be saved.
     */
    @Override
    public void execute(ApplicationData data, Ui ui, Storage storage) throws IOException {
        PlaceList places = data.getPlaces();
        for (int i = 0; i < places.size(); i += 1) {
            Place existingPlace = places.getPlace(i);
            if (existingPlace.hasSameDetails(place)) {
                ui.showDuplicatePlace(existingPlace, i + 1);
                return;
            }
        }
        places.add(place);
        try {
            storage.saveData(data);
        } catch (IOException exception) {
            places.remove(place);
            throw exception;
        }
        ui.showAddedPlace(place, places.size());
    }
}
