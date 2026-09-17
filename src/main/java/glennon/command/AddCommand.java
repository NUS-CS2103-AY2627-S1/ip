package glennon.command;

import glennon.Storage;
import glennon.Ui;
import glennon.exception.GlennonException;
import glennon.task.Task;
import glennon.task.TaskList;

/**
 * Adds one parsed mission and persists the updated mission log.
 */
public final class AddCommand extends Command {
    /** Mission to add. */
    private final Task mission;

    /**
     * Creates a command that adds the specified mission.
     *
     * @param mission parsed mission to add.
     */
    public AddCommand(Task mission) {
        this.mission = mission;
    }

    /**
     * Saves the addition before updating and displaying the mission log.
     *
     * @param missions missions in the current session.
     * @param ui interface used to display the result.
     * @param storage storage used to persist the change.
     * @throws GlennonException if the mission is a duplicate or the updated list cannot be saved.
     */
    @Override
    public void execute(TaskList missions, Ui ui, Storage storage)
            throws GlennonException {
        TaskList updatedMissions = new TaskList(missions.asList());
        updatedMissions.add(mission);
        storage.saveMissions(updatedMissions.asList());
        missions.add(mission);
        ui.showMissionAdded(mission, missions.size());
    }
}
