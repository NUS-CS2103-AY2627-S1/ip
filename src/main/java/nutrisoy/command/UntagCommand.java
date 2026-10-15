package nutrisoy.command;

import nutrisoy.exception.DukeException;
import nutrisoy.task.Task;
import nutrisoy.task.TaskList;
import nutrisoy.ui.Ui;

/**
 * Removes a tag from a task identified by a user-provided index.
 */
public class UntagCommand extends Command {
    private final String indexString;
    private final String tagName;

    /**
     * Creates a command with a task index and tag name.
     *
     * @param arguments task index and tag name
     * @throws DukeException if the arguments are missing or malformed
     */
    public UntagCommand(String arguments) throws DukeException {
        String[] parts = parseTagArguments(arguments, "untag");
        this.indexString = parts[0];
        this.tagName = parts[1];
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui) throws DukeException {
        assert tasks != null && ui != null : "Command collaborators must not be null";
        int index = parseTaskIndex(indexString, tasks,
                "Usage: untag [index] [tag]");
        Task task = tasks.get(index);
        if (!task.hasTag(tagName)) {
            throw new DukeException("That task does not have the #" + tagName + " tag.");
        }
        task.removeTag(tagName);
        ui.showTaskUntagged(task, tagName);
    }
}
