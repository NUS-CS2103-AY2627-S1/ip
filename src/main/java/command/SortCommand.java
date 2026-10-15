package command;

import exception.CbtException;
import task.TaskList;

/** Sorts tasks according to a user-selected criterion. */
public class SortCommand implements Command {
    private static final String DATE_CRITERION = "date";
    private final String criterion;

    /**
     * Creates a command using the user-entered sort criterion.
     *
     * @param criterion criterion by which tasks should be sorted.
     */
    public SortCommand(String criterion) {
        this.criterion = criterion;
    }

    /**
     * Sorts tasks chronologically when the date criterion is supplied.
     *
     * @param taskList task list to reorder.
     * @return result containing the reordered and renumbered tasks.
     * @throws CbtException if the criterion is missing or unsupported.
     */
    @Override
    public CommandResult execute(TaskList taskList) throws CbtException {
        if (!criterion.equalsIgnoreCase(DATE_CRITERION)) {
            throw new CbtException("Use: sort date");
        }

        taskList.sortChronologically();
        String message = "Flight plan aligned by date:";
        String formattedTasks = taskList.formatTasks();
        if (!formattedTasks.isEmpty()) {
            message += System.lineSeparator() + formattedTasks;
        }
        return new CommandResult(message, true, false);
    }
}
