package nico;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows all tasks with the requested priority, including completed tasks.
 */
public class ShowPriorityCommand extends Command {
    private final Priority priority;

    /**
     * Creates a filter for the given priority.
     */
    public ShowPriorityCommand(String argument) throws NicoException {
        if (argument == null || argument.isBlank()) {
            throw NicoException.invalidInput("Choose a priority to show.", "showpriority PRIORITY");
        }
        priority = Priority.parse(argument);
    }

    @Override
    public void execute(List<Task> tasks, Ui ui) {
        List<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getPriority() == priority) {
                matchingTasks.add(task);
            }
        }
        ui.showTaskGroup("These tasks got priority " + priority, matchingTasks);
    }
}
