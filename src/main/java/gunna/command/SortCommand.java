package gunna.command;

import java.util.ArrayList;
import java.util.Comparator;

import gunna.Storage;
import gunna.TaskList;
import gunna.Ui;
import gunna.task.Deadline;
import gunna.task.Task;

/**
 * Represents a command to sort and display tasks by a specified criterion.
 * Sorting is temporary and does not modify the underlying task list.
 */
public class SortCommand extends Command {
    private String criterion;

    /**
     * Creates a SortCommand with the specified sort criterion.
     *
     * @param criterion The criterion to sort by (already normalized to lowercase).
     */
    public SortCommand(String criterion) {
        this.criterion = criterion;
    }

    /**
     * Executes the sort command by creating a sorted copy of tasks and displaying them.
     * The original task list is not modified (immutability preserved).
     *
     * @param tasks The task list (not modified).
     * @param ui The UI to display the sorted tasks.
     * @param storage The storage (not used, sorting is temporary).
     * @return The response message for GUI display.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        // Create sorted copy to preserve immutability
        ArrayList<Task> sortedTasks = new ArrayList<>(tasks.getTasks());

        // Apply appropriate comparator based on criterion
        switch (criterion) {
            case "status":
                sortedTasks.sort(getStatusComparator());
                break;
            case "description":
                sortedTasks.sort(getDescriptionComparator());
                break;
            case "date":
                sortedTasks.sort(getDateComparator());
                break;
            default:
                // Should never happen due to validation in Parser
                break;
        }

        // Display via UI for CLI
        ui.showSortedTasks(sortedTasks, criterion);

        // Build response for GUI (same content, no delimiters)
        if (sortedTasks.isEmpty()) {
            return "Here are your tasks sorted by " + criterion + ":\nYou have no tasks in your list.";
        } else {
            StringBuilder response = new StringBuilder("Here are your tasks sorted by " + criterion + ":");
            for (int i = 0; i < sortedTasks.size(); i++) {
                response.append("\n").append(i + 1).append(".").append(sortedTasks.get(i));
            }
            return response.toString();
        }
    }

    /**
     * Returns a comparator that sorts tasks by completion status.
     * Not-done tasks (false) come before done tasks (true).
     *
     * @return Comparator for sorting by status.
     */
    private Comparator<Task> getStatusComparator() {
        return Comparator.comparing(Task::isDone);
    }

    /**
     * Returns a comparator that sorts tasks by description alphabetically (case-insensitive).
     *
     * @return Comparator for sorting by description.
     */
    private Comparator<Task> getDescriptionComparator() {
        return Comparator.comparing(task -> task.getDescription().toLowerCase());
    }

    /**
     * Returns a comparator that sorts tasks by date.
     * Deadlines are sorted by date (oldest first), followed by Todos and Events
     * in their original relative order.
     *
     * @return Comparator for sorting by date.
     */
    private Comparator<Task> getDateComparator() {
        return (task1, task2) -> {
            boolean isDeadline1 = task1 instanceof Deadline;
            boolean isDeadline2 = task2 instanceof Deadline;

            if (isDeadline1 && isDeadline2) {
                // Both are Deadlines - compare by date (oldest first)
                Deadline d1 = (Deadline) task1;
                Deadline d2 = (Deadline) task2;
                return d1.getByDate().compareTo(d2.getByDate());
            } else if (isDeadline1) {
                // Only task1 is a Deadline - it comes first
                return -1;
            } else if (isDeadline2) {
                // Only task2 is a Deadline - it comes first
                return 1;
            } else {
                // Neither is a Deadline - maintain original order (stable sort)
                return 0;
            }
        };
    }
}
