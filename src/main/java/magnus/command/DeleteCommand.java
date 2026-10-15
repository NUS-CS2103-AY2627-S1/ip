package magnus.command;

import magnus.exception.CommandSyntaxException;
import magnus.exception.MagnusException;
import magnus.exception.TaskNotFoundException;
import magnus.task.Task;
import magnus.task.TaskList;

/**
 * Deletes tasks from a task list.
 */
public class DeleteCommand implements Command {
    private final TaskList tasks;

    /**
     * Creates a command that deletes tasks from the specified task list.
     *
     * @param tasks The task list from which tasks are deleted.
     */
    public DeleteCommand(TaskList tasks) {
        this.tasks = tasks;
    }

    /**
     * Deletes the task identified by the one-based task number in the first command argument.
     *
     * @param args The command arguments, with the one-based task number at index 0.
     * @return A message describing the task that was deleted and the current task total.
     * @throws CommandSyntaxException If the task number is missing, non-numeric, or accompanied
     *                                by extra arguments.
     * @throws TaskNotFoundException If the task number does not identify a task in the list.
     */
    @Override
    public String execute(String... args) throws MagnusException {
        int taskIndex = TaskIndexParser.parseTaskIndex(args, "delete", this.tasks.getTaskCount());

        Task removedTask = this.tasks.removeTask(taskIndex);
        return "\tPiece captured and cleared - I've deleted this task:\n\n\t" + removedTask
                + "\n\n" + TaskCountMessage.formatTotal(this.tasks.getTaskCount());
    }
}
