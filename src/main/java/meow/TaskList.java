package meow;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Manages the collection of tasks in the chatbot.
 */
public class TaskList {
    private ArrayList<Task> tasks = new ArrayList<>(100);

    /**
     * Adds a task to the task list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        assert task != null : "Task to add should not be null";
        tasks.add(task);
    }

    /**
     * Removes the task at the specified index from the task list.
     *
     * @param taskIndex the index of the task to remove
     * @return the removed task
     */
    public Task delete(int taskIndex) {
        return tasks.remove(taskIndex);
    }

    /**
     * Returns the number of tasks in the task list.
     *
     * @return the number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at the specified index.
     *
     * @param taskIndex the index of the task
     * @return the task at the specified index
     */
    public Task getTask(int taskIndex) {
        return tasks.get(taskIndex);
    }

    /**
     * Finds tasks whose descriptions contain the specified keyword.
     *
     * @param keyword the keyword to search for.
     * @return a task list containing the matching tasks.
     */
    public TaskList findTasks(String keyword) {
        TaskList matches = new TaskList();
        String lowerKeyword = keyword.toLowerCase();

        tasks.stream()
                .filter(task -> task.getDescription()
                        .toLowerCase()
                        .contains(lowerKeyword))
                .forEach(matches::add);

        return matches;
    }

    /**
     * Sorts dated tasks chronologically and places undated tasks afterwards.
     * Tasks with the same date retain their original relative order.
     */
    public void sortChronologically() {
        tasks.sort((first, second) -> {
            LocalDate firstDate = getTaskDate(first);
            LocalDate secondDate = getTaskDate(second);

            if (firstDate == null && secondDate == null) {
                return 0;
            }

            if (firstDate == null) {
                return 1;
            }

            if (secondDate == null) {
                return -1;
            }

            return firstDate.compareTo(secondDate);
        });
    }

    /**
     * Returns the date used to sort a task.
     *
     * @param task the task to inspect
     * @return the task date, or null if the task has no date
     */
    private LocalDate getTaskDate(Task task) {
        if (task instanceof Deadline deadline) {
            return deadline.getDueDate();
        }

        if (task instanceof Event event) {
            return event.getFromDate();
        }

        return null;
    }

    /**
     * Creates a copy of this task list while preserving the current task order.
     * The task objects themselves are shared between the two lists.
     *
     * @return a copy of this task list
     */
    public TaskList copy() {
        TaskList copy = new TaskList();
        copy.tasks.addAll(tasks);
        return copy;
    }
}
