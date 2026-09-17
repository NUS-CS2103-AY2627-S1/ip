package glennon.command;

import glennon.Storage;
import glennon.Ui;
import glennon.exception.GlennonException;
import glennon.task.Task;
import glennon.task.TaskList;

/**
 * Marks or unmarks one mission by its zero-based position.
 */
public final class MarkCommand extends Command {
    /** Zero-based position of the mission whose status should change. */
    private final int missionIndex;

    /** Whether the mission should be marked complete rather than incomplete. */
    private final boolean shouldCompleteMission;

    /**
     * Creates a mission-status command.
     *
     * @param missionIndex zero-based position of the mission.
     * @param shouldCompleteMission true to mark complete, false to mark incomplete.
     */
    public MarkCommand(int missionIndex, boolean shouldCompleteMission) {
        this.missionIndex = missionIndex;
        this.shouldCompleteMission = shouldCompleteMission;
    }

    /**
     * Updates, saves, and displays the selected mission, restoring its original
     * status if the save fails.
     *
     * @param missions missions in the current session.
     * @param ui interface used to display the result.
     * @param storage storage used to persist the change.
     * @throws GlennonException if the index or save operation fails.
     */
    @Override
    public void execute(TaskList missions, Ui ui, Storage storage)
            throws GlennonException {
        Task mission = missions.get(missionIndex);
        boolean wasDone = mission.isDone();
        if (shouldCompleteMission) {
            mission.markAsDone();
        } else {
            mission.markAsNotDone();
        }

        try {
            storage.saveMissions(missions.asList());
        } catch (GlennonException e) {
            // Restore the same task object so existing references remain consistent.
            if (wasDone) {
                mission.markAsDone();
            } else {
                mission.markAsNotDone();
            }
            throw e;
        }
        ui.showMissionStatusChanged(mission, shouldCompleteMission);
    }
}
