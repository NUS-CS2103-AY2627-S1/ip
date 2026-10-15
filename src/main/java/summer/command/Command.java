package summer.command;

import summer.SummerException;
import summer.storage.Storage;
import summer.task.TaskList;
import summer.ui.Ui;

/**
 * A user command: executes against the task list and reports via {@link Ui},
 * saving via {@link Storage} if it changed the list.
 */
public abstract class Command {
    /**
     * Runs this command against the task list.
     *
     * @param tasks the task list to read or modify
     * @param ui used to report the outcome to the user
     * @param storage used to persist the list if this command changes it
     * @throws SummerException if the command cannot be carried out
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws SummerException;

    /**
     * @return true if the chatbot should exit after this command (false by default)
     */
    public boolean isExit() {
        return false;
    }
}
