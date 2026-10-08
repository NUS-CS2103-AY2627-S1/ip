package rudra.command;

import java.util.ArrayList;

import rudra.exception.RudraException;
import rudra.storage.Storage;
import rudra.task.Deadline;
import rudra.task.Task;
import rudra.ui.Ui;

/**
 * Sorts deadlines chronologically before all other tasks.
 */
public class SortCommand extends Command {
    /**
     * Sorts deadlines from earliest to latest and saves the updated task order.
     * Non-deadline tasks and deadlines with equal due times retain their relative order.
     *
     * @param tasks Current task list.
     * @param ui UI helper used to show output.
     * @param storage Storage helper used to persist changes.
     * @throws RudraException If the sorted task order cannot be saved.
     */
    @Override
    public void execute(ArrayList<Task> tasks, Ui ui, Storage storage) throws RudraException {
        assert tasks != null : "The task list must be initialized before executing a command";
        assert storage != null : "Storage must be initialized before executing a command";

        ArrayList<Task> originalTasks = new ArrayList<>(tasks);
        tasks.sort(SortCommand::compareTasks);

        try {
            storage.saveTasks(tasks);
        } catch (RudraException e) {
            tasks.clear();
            tasks.addAll(originalTasks);
            assert tasks.equals(originalTasks) : "A failed save must restore the original task order";
            throw new RudraException(e.getMessage() + " Your task list was left unchanged.");
        }

        ui.showTasksSorted(tasks);
    }

    /**
     * Compares tasks so deadlines come first in chronological order.
     *
     * @param first First task to compare.
     * @param second Second task to compare.
     * @return Ordering value for the stable task-list sort.
     */
    private static int compareTasks(Task first, Task second) {
        boolean isFirstDeadline = first instanceof Deadline;
        boolean isSecondDeadline = second instanceof Deadline;

        if (isFirstDeadline && isSecondDeadline) {
            Deadline firstDeadline = (Deadline) first;
            Deadline secondDeadline = (Deadline) second;
            return firstDeadline.getBy().compareTo(secondDeadline.getBy());
        }
        if (isFirstDeadline) {
            return -1;
        }
        if (isSecondDeadline) {
            return 1;
        }
        return 0;
    }
}
