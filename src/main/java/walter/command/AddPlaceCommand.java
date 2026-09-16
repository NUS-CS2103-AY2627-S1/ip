package walter.command;

import walter.DukeException;
import walter.place.Place;
import walter.place.PlaceList;
import walter.storage.Storage;
import walter.task.TaskList;
import walter.ui.Ui;

/**
 * Adds and persists one place without changing Walter's task list.
 */
public class AddPlaceCommand extends Command {
    private final Place place;

    /**
     * Creates a command that saves one parsed place.
     *
     * @param place Place to save.
     */
    public AddPlaceCommand(Place place) {
        this.place = place;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.ADD;
    }

    /**
     * Adds the place, persists the updated place list, and displays confirmation.
     *
     * @param tasks Task list supplied by the application; it is not modified.
     * @param ui User interface used to display the saved place.
     * @param storage Storage used to load and persist places.
     * @throws DukeException If saved places cannot be loaded or persisted.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeException {
        PlaceList places = new PlaceList(storage.loadPlaces());
        places.add(place);
        storage.savePlaces(places.getPlaces());
        ui.showAddedPlace(place, places.size());
    }
}
