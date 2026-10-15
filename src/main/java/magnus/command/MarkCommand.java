package magnus.command;

import magnus.exception.CommandSyntaxException;
import magnus.exception.MagnusException;
import magnus.exception.TaskNotFoundException;
import magnus.task.TaskList;

/**
 * Marks a task in a task list as completed.
 */
public class MarkCommand implements Command {
    private final TaskList tasks;

    /**
     * Creates a command that marks tasks in the specified task list.
     *
     * @param tasks The task list containing the tasks to mark.
     */
    public MarkCommand(TaskList tasks) {
        this.tasks = tasks;
    }

    /**
     * Marks the task identified by the one-based task number in the first command argument.
     *
     * @param args The command arguments, with the one-based task number at index 0.
     * @return A message describing the task that was marked and the completed-task total.
     * @throws CommandSyntaxException If the task number is missing, non-numeric, or accompanied
     *                                by extra arguments.
     * @throws TaskNotFoundException If the task number does not identify a task in the list.
     */
    @Override
    public String execute(String... args) throws MagnusException {
        int taskIndex = TaskIndexParser.parseTaskIndex(args, "mark", this.tasks.getTaskCount());

        this.tasks.markTaskAsDone(taskIndex);
        return "\tCheckmate for this task - I've marked it as completed:\n\n\t"
                + this.tasks.getTask(taskIndex) + "\n\n"
                + TaskCountMessage.formatCompleted(this.tasks.countCompletedTasks());
    }
}
