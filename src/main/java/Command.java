import java.io.IOException;

/**
 * One action the user asked for, ready to be carried out.
 *
 * <p>{@link Parser} turns typed text into a {@code Command}, and the main loop
 * simply executes it. Adding a feature therefore means adding a subclass here
 * rather than adding another branch to a growing if-else chain.
 */
public abstract class Command {

    /**
     * Carries out this command.
     *
     * @param tasks the task list to read or change
     * @param ui the interface used to show the result
     * @param storage the save file to update when the list changes
     * @throws BibiException if the command cannot be completed
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws BibiException;

    /**
     * Reports whether Bibi should stop after this command.
     *
     * <p>Only the exit command answers {@code true}, so the default suits every
     * other subclass.
     *
     * @return {@code true} when this command ends the session
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Writes the task list to disk after a command has changed it.
     *
     * <p>A failed save is reported but does not fail the command, because the
     * change the user asked for has already been made in memory. Keeping this
     * here means every command that changes the list saves the same way.
     *
     * @param tasks the task list to store
     * @param ui the interface used to report a failure
     * @param storage the save file to write to
     */
    protected void saveTasks(TaskList tasks, Ui ui, Storage storage) {
        try {
            storage.save(tasks.getTasks());
        } catch (IOException exception) {
            ui.showSaveError(storage.getFilePath(), exception);
        }
    }
}
