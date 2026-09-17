package glennon.command;

import glennon.Storage;
import glennon.Ui;
import glennon.exception.GlennonException;
import glennon.task.Task;
import glennon.task.TaskList;

/**
 * Deletes one mission by its zero-based position.
 */
public final class DeleteCommand extends Command {
    /** Zero-based position of the mission to delete. */
    private final int missionIndex;

    /**
     * Creates a mission-deletion command.
     *
     * @param missionIndex zero-based position of the mission to delete.
     */
    public DeleteCommand(int missionIndex) {
        this.missionIndex = missionIndex;
    }

    /**
     * Saves the removal before updating and displaying the mission log.
     *
     * @param missions missions in the current session.
     * @param ui interface used to display the result.
     * @param storage storage used to persist the change.
     * @throws GlennonException if the index or save operation fails.
     */
    @Override
    public void execute(TaskList missions, Ui ui, Storage storage)
            throws GlennonException {
        TaskList updatedMissions = new TaskList(missions.asList());
        Task removedMission = updatedMissions.remove(missionIndex);
        storage.saveMissions(updatedMissions.asList());
        missions.remove(missionIndex);
        ui.showMissionRemoved(removedMission, missions.size());
    }
}
