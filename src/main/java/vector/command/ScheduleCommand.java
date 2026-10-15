package vector.command;

import vector.storage.Storage;
import vector.task.Task;
import vector.task.TaskList;
import vector.ui.Ui;

/**
 * Represents a command to list tasks occurring on a specific date.
 */
public class ScheduleCommand extends Command {
    private java.time.LocalDate date;

    /**
     * Constructs a ScheduleCommand to find tasks for the specified date.
     *
     * @param date The date to filter tasks by.
     */
    public ScheduleCommand(java.time.LocalDate date) {
        this.date = date;
    }

    /**
     * Executes the command, displaying tasks that occur on the specified date.
     *
     * @param tasks   The task list to operate on.
     * @param ui      The user interface for displaying messages.
     * @param storage The storage for saving updates.
     * @return The response string.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        java.util.List<Task> occurrences = tasks.getTasks().stream()
                .filter(t -> t.occursOn(date))
                .collect(java.util.stream.Collectors.toList());
        if (occurrences.isEmpty()) {
            return "(No tasks found)";
        }
        String taskListStr = java.util.stream.IntStream.range(0, occurrences.size())
                .mapToObj(i -> (i + 1) + "." + occurrences.get(i).toString())
                .collect(java.util.stream.Collectors.joining("\n"));
        return "Scanning schedule for " + date + ". Results:\n" + taskListStr;
    }
}
