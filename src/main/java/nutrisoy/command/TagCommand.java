package nutrisoy.command;

import nutrisoy.exception.DukeException;
import nutrisoy.task.Task;
import nutrisoy.task.TaskList;
import nutrisoy.ui.Ui;

/**
 * Adds a tag to a task identified by a user-provided index.
 */
public class TagCommand extends Command {
    private final String indexString;
    private final String tagName;

    /**
     * Creates a command with a task index and tag name.
     *
     * @param arguments task index and tag name
     * @throws DukeException if the arguments are missing or malformed
     */
    public TagCommand(String arguments) throws DukeException {
        String[] parts = parseTagArguments(arguments, "tag");
        this.indexString = parts[0];
        this.tagName = parts[1];
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui) throws DukeException {
        assert tasks != null && ui != null : "Command collaborators must not be null";
        int index = parseTaskIndex(indexString, tasks,
                "Usage: tag [index] [tag]");
        Task task = tasks.get(index);
        if (task.hasTag(tagName)) {
            throw new DukeException("That task already has the #" + tagName + " tag.");
        }
        task.addTag(tagName);
        ui.showTaskTagged(task, tagName);
    }
}
