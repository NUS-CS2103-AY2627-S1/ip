package walter.command;

import walter.DukeException;
import walter.place.PlaceList;
import walter.storage.Storage;
import walter.task.TaskList;
import walter.ui.Ui;

/**
 * Displays saved places without changing Walter's task list or place storage.
 */
public class ListPlacesCommand extends Command {
    /**
     * Loads and displays saved places without modifying tasks or persistence.
     *
     * @param tasks Task list supplied by the application; it is not modified.
     * @param ui User interface used to display places.
     * @param storage Storage from which places are loaded.
     * @throws DukeException If saved places cannot be loaded.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeException {
        PlaceList places = new PlaceList(storage.loadPlaces());
        ui.showPlaces(places.getPlaces());
    }
}
