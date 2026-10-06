package duke.command;

import duke.exception.DukeException;
import duke.place.Place;
import duke.place.PlaceList;
import duke.storage.Storage;
import duke.task.TaskList;
import duke.ui.Ui;

/**
 * Represents a command to add a place to the place list.
 */
public class AddPlaceCommand extends Command {
    private final Place place;

    /**
     * Constructs an AddPlaceCommand with the place to add.
     *
     * @param place The place to add.
     */
    public AddPlaceCommand(Place place) {
        this.place = place;
    }

    /**
     * Executes the add place command by saving a snapshot, adding the place,
     * saving changes, and notifying the user.
     *
     * @param tasks   The task list.
     * @param ui      The user interface.
     * @param storage The storage handler.
     * @throws DukeException If saving places fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeException {
        PlaceList places = storage.getPlaceList();
        if (places.contains(place)) {
            throw new DukeException("OOPS!!! This place already exists in your list.");
        }
        places.saveSnapshot();
        places.addPlace(place);
        storage.savePlaces(places);
        ui.showPlaceAdded(place, places.size());
    }
}
