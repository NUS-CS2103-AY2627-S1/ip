package yapbot.command;

import yapbot.exception.YapBotException;
import yapbot.storage.Storage;
import yapbot.task.Task;
import yapbot.task.TaskList;
import yapbot.ui.Ui;

/**
 * Represents a command that adds an already-constructed task to the list.
 * Used for todo, deadline, and event alike, since by the time the Parser
 * builds this command the task itself is already fully formed.
 */
public class AddCommand extends Command {

    private final Task task;

    /**
     * Creates a command that adds the given task.
     *
     * @param task the task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Returns the task this command will add. Exists mainly so tests (e.g.
     * for {@link yapbot.parser.Parser}) can verify the task was built
     * correctly without needing to execute the command.
     *
     * @return the task to be added.
     */
    public Task getTask() {
        return task;
    }

    /**
     * Adds the wrapped task to the task list, persists the updated list to
     * disk, and shows a confirmation to the user.
     *
     * @param taskList the task list to add to.
     * @param ui the Ui to show the confirmation on.
     * @throws YapBotException if the task list is already at capacity.
     */
    @Override
    public void execute(TaskList taskList, Ui ui) throws YapBotException {
        taskList.add(task);
        Storage.save(taskList.toArray(), taskList.size());
        ui.showTaskAdded(task, taskList.size());
    }
}