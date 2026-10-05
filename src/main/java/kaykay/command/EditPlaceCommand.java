package kaykay.command;

import java.io.IOException;
import java.time.LocalDate;

import kaykay.exception.KaykayException;
import kaykay.model.ApplicationData;
import kaykay.model.Place;
import kaykay.model.PlaceList;
import kaykay.storage.Storage;
import kaykay.ui.Ui;

/**
 * Updates selected details of a saved place.
 */
public final class EditPlaceCommand extends Command {
    /** User-supplied one-based place number. */
    private final String placeNumber;

    /** Replacement values, where {@code null} means leave the field unchanged. */
    private final String name;
    private final String type;
    private final String location;
    private final LocalDate visitedOn;
    private final Integer rating;
    private final String notes;

    /**
     * Creates a command that changes the supplied place fields.
     *
     * @param placeNumber one-based number of the place to edit.
     * @param name replacement name, or {@code null}.
     * @param type replacement type, or {@code null}.
     * @param location replacement location, or {@code null}.
     * @param visitedOn replacement visit date, or {@code null}.
     * @param rating replacement rating, or {@code null}.
     * @param notes replacement notes, or {@code null}.
     */
    public EditPlaceCommand(String placeNumber, String name, String type, String location,
            LocalDate visitedOn, Integer rating, String notes) {
        this.placeNumber = placeNumber;
        this.name = name;
        this.type = type;
        this.location = location;
        this.visitedOn = visitedOn;
        this.rating = rating;
        this.notes = notes;
    }

    @Override
    public void execute(ApplicationData data, Ui ui, Storage storage)
            throws KaykayException, IOException {
        PlaceList places = data.getPlaces();
        if (!places.isValidPlaceNumber(placeNumber)) {
            throw new KaykayException("Please provide an existing place number to edit.");
        }
        int index = Integer.parseInt(placeNumber) - 1;
        Place existingPlace = places.getPlace(index);
        Place editedPlace = new Place(
                valueOrExisting(name, existingPlace.getName()),
                valueOrExisting(type, existingPlace.getType()),
                valueOrExisting(location, existingPlace.getLocation()),
                visitedOn == null ? existingPlace.getVisitedOn() : visitedOn,
                rating == null ? existingPlace.getRating() : rating,
                valueOrExisting(notes, existingPlace.getNotes()));
        places.set(index, editedPlace);
        try {
            storage.saveData(data);
        } catch (IOException exception) {
            places.set(index, existingPlace);
            throw exception;
        }
        ui.showEditedPlace(editedPlace);
    }

    /** Returns a replacement value when present, or keeps the existing value. */
    private static String valueOrExisting(String value, String existingValue) {
        return value == null ? existingValue : value;
    }
}
