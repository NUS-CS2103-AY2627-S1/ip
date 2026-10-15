package luna.command;

import luna.task.TaskList;

/**
 * Lists all tasks.
 */
public class ListCommand extends Command {
    private final TaskList tasks;

    /**
     * Creates a command that lists the given tasks.
     *
     * @param tasks Task list to display.
     */
    public ListCommand(TaskList tasks) {
        this.tasks = tasks;
    }

    @Override
    public String execute() {
        StringBuilder response = new StringBuilder("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            response.append("\n")
                    .append(i + 1)
                    .append(". ")
                    .append(tasks.get(i));
        }
        return response.toString();
    }
}
