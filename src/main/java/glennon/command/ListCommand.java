package glennon.command;

import glennon.Storage;
import glennon.Ui;
import glennon.task.TaskList;

/**
 * Displays every mission in the mission log.
 */
public final class ListCommand extends Command {
    /**
     * Creates a command that displays the current mission log.
     */
    public ListCommand() {
    }

    /**
     * Displays the current mission list without changing it.
     *
     * @param missions missions in the current session.
     * @param ui interface used to display the result.
     * @param storage storage used by the application.
     */
    @Override
    public void execute(TaskList missions, Ui ui, Storage storage) {
        ui.showMissionList(missions.asList());
    }
}
