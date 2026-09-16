package walter.command;

import walter.DukeException;
import walter.place.Place;
import walter.place.PlaceList;
import walter.storage.Storage;
import walter.task.TaskList;
import walter.ui.Ui;

/**
 * Deletes and persists one saved place without changing Walter's task list.
 */
public class DeletePlaceCommand extends Command {
    private final int placeIndex;

    /**
     * Creates a command that deletes one zero-based place index.
     *
     * @param placeIndex Zero-based index to delete.
     */
    public DeletePlaceCommand(int placeIndex) {
        this.placeIndex = placeIndex;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.DELETE;
    }

    /**
     * Deletes the selected place, persists the updated list, and displays confirmation.
     *
     * @param tasks Task list supplied by the application; it is not modified.
     * @param ui User interface used to display the deleted place.
     * @param storage Storage used to load and persist places.
     * @throws DukeException If the place index is invalid or places cannot be loaded or saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeException {
        PlaceList places = new PlaceList(storage.loadPlaces());
        Place deletedPlace = places.delete(placeIndex);
        storage.savePlaces(places.getPlaces());
        ui.showDeletedPlace(deletedPlace, places.size());
    }
}
