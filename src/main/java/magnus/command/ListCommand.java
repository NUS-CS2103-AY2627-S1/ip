package magnus.command;

import magnus.exception.CommandSyntaxException;
import magnus.task.TaskList;

/**
 * Displays all tasks in a task list.
 */
public class ListCommand implements Command {
    private final TaskList tasks;

    /**
     * Creates a command that displays the specified task list.
     *
     * @param tasks The task list to display.
     */
    public ListCommand(TaskList tasks) {
        this.tasks = tasks;
    }

    /**
     * Formats every task in the task list for display.
     *
     * @param args The command arguments.
     * @return A message containing every task in the task list.
     */
    @Override
    public String execute(String... args) throws CommandSyntaxException {
        if (args.length > 0) {
            throw new CommandSyntaxException("\tThat move has extra pieces - the list command does not accept "
                    + "arguments.\n"
                    + "\tUsage: list");
        }
        if (this.tasks.getTaskCount() == 0) {
            return "\tThere are no tasks on the board yet.";
        }
        return "\tHere's the current position - your full task list:\n\n" + tasks.formatTasks();
    }
}
