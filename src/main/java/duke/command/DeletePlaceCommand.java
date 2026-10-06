package duke.command;

import duke.exception.DukeException;
import duke.place.Place;
import duke.place.PlaceList;
import duke.storage.Storage;
import duke.task.TaskList;
import duke.ui.Ui;

/**
 * Represents a command to delete a place from the place list.
 */
public class DeletePlaceCommand extends Command {
    private final int targetIndex;

    /**
     * Constructs a DeletePlaceCommand for the place at the specified index.
     *
     * @param targetIndex The 0-based index of the place to delete.
     */
    public DeletePlaceCommand(int targetIndex) {
        this.targetIndex = targetIndex;
    }

    /**
     * Executes the delete place command by removing the specified place, saving changes,
     * and notifying the user.
     *
     * @param tasks   The task list.
     * @param ui      The user interface.
     * @param storage The storage handler.
     * @throws DukeException If the place index is invalid or saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeException {
        PlaceList places = storage.getPlaceList();
        places.saveSnapshot();
        Place removedPlace = places.deletePlace(targetIndex);
        storage.savePlaces(places);
        ui.showPlaceDeleted(removedPlace, places.size());
    }
}
