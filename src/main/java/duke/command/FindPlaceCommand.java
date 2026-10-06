package duke.command;

import duke.exception.DukeException;
import duke.place.PlaceList;
import duke.storage.Storage;
import duke.task.TaskList;
import duke.ui.Ui;

/**
 * Represents a command to search for places containing a keyword.
 */
public class FindPlaceCommand extends Command {
    private final String keyword;

    /**
     * Constructs a FindPlaceCommand with the search keyword.
     *
     * @param keyword The keyword to search for.
     */
    public FindPlaceCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Executes the find place command by filtering places and displaying matches.
     *
     * @param tasks   The task list.
     * @param ui      The user interface.
     * @param storage The storage handler.
     * @throws DukeException If executing the command fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeException {
        PlaceList matchedPlaces = storage.getPlaceList().findPlaces(keyword);
        ui.showFoundPlaces(matchedPlaces);
    }
}
