package glennon.command;

import glennon.Storage;
import glennon.Ui;
import glennon.exception.GlennonException;
import glennon.task.TaskList;

/**
 * Sorts scheduled missions chronologically and persists their new order.
 */
public final class SortCommand extends Command {
    /**
     * Creates a command that sorts the mission log chronologically.
     */
    public SortCommand() {
    }

    /**
     * Saves the sorted order before updating and displaying the mission log.
     *
     * @param missions missions in the current session.
     * @param ui interface used to display the result.
     * @param storage storage used to persist the new order.
     * @throws GlennonException if the sorted mission list cannot be saved.
     */
    @Override
    public void execute(TaskList missions, Ui ui, Storage storage)
            throws GlennonException {
        TaskList updatedMissions = new TaskList(missions.asList());
        updatedMissions.sortChronologically();
        storage.saveMissions(updatedMissions.asList());
        missions.sortChronologically();
        ui.showSortedMissions(missions.asList());
    }
}
