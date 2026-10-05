package kaykay.command;

import java.io.IOException;

import kaykay.exception.KaykayException;
import kaykay.model.ApplicationData;
import kaykay.model.Place;
import kaykay.model.PlaceList;
import kaykay.storage.Storage;
import kaykay.ui.Ui;

/**
 * Deletes a saved place and persists the resulting data.
 */
public final class DeletePlaceCommand extends Command {
    /** User-supplied one-based place number. */
    private final String placeNumber;

    /**
     * Creates a command for a numbered place.
     *
     * @param placeNumber one-based number of the place to delete.
     */
    public DeletePlaceCommand(String placeNumber) {
        this.placeNumber = placeNumber;
    }

    @Override
    public void execute(ApplicationData data, Ui ui, Storage storage)
            throws KaykayException, IOException {
        PlaceList places = data.getPlaces();
        if (!places.isValidPlaceNumber(placeNumber)) {
            throw new KaykayException("Please provide an existing place number to delete.");
        }
        int index = Integer.parseInt(placeNumber) - 1;
        Place deletedPlace = places.remove(index);
        try {
            storage.saveData(data);
        } catch (IOException exception) {
            places.add(index, deletedPlace);
            throw exception;
        }
        ui.showDeletedPlace(deletedPlace, places.size());
    }
}
