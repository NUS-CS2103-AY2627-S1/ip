package ekud.command;

import ekud.EkudException;
import ekud.storage.Storage;
import ekud.task.TaskList;
import ekud.ui.Ui;

/**
 * A user command that can be executed against the task list.
 * Each concrete command owns its own validation and behavior so new
 * commands can be added without growing {@link ekud.Ekud}.
 */
public interface Command {
    /**
     * Carries out this command.
     * Commands that change the list should call {@link Storage#save} after the
     * change so the file stays in sync.
     *
     * @param tasks   the task list to read or update
     * @param ui      used to show results to the user
     * @param storage used to persist the list after a change
     * @throws EkudException if the command cannot be executed
     */
    void execute(TaskList tasks, Ui ui, Storage storage) throws EkudException;

    /**
     * Returns whether the chatbot should stop after this command.
     *
     * @return {@code true} only for the exit command
     */
    default boolean isExit() {
        return false;
    }
}
