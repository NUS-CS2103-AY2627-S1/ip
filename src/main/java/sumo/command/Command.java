package sumo.command;

import java.io.IOException;

import sumo.exception.SumoException;
import sumo.storage.Storage;
import sumo.task.TaskList;
import sumo.ui.Ui;

/** Represents one user instruction that can be executed by Sumo. */
public abstract class Command {
    /** Creates a command. */
    protected Command() {
    }

    /**
     * Carries out this command using the application's collaborators.
     *
     * @param tasks task list on which the command operates.
     * @param ui user interface through which the command displays output.
     * @param storage storage used to persist task-list changes.
     * @throws IOException if a task-list change cannot be saved.
     * @throws SumoException if the command would create invalid task data.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws IOException, SumoException;

    /**
     * Returns whether this command should end the application.
     *
     * @return whether this command should end the application.
     */
    public boolean isExit() {
        return false;
    }
}
