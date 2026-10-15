package doe;

import java.util.Comparator;
import java.util.List;

/**
 * Contains the task list; has operations to add/delete/modify tasks in the list.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Initializes TaskList with an existing list of tasks (e.g., loaded from storage).
     *
     * @param tasks A list of tasks typically loaded from storage.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "The backing task list must exist";
        assert tasks.stream().allMatch(task -> task != null) : "The task list must not contain null tasks";
        this.tasks = tasks;
    }

    /**
     * Returns the complete list of stored tasks.
     *
     * @return A list containing all Task objects.
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * Sorts tasks chronologically with undated tasks last. Equal dates and undated
     * tasks retain their relative order because List.sort is stable.
     */
    public void sortByDate() {
        tasks.sort(Comparator.comparing(Task::getDate, Comparator.nullsLast(Comparator.naturalOrder())));
    }

    /**
     * Adds a new task to the task list.
     *
     * @param task The Task object to add.
     */
    public void addTask(Task task) {
        assert task != null : "Only valid task objects may be added";
        tasks.add(task);
    }

    /**
     * Removes a task from the task list at a specified index.
     *
     * @param index The zero-based index of the task to remove.
     * @return The Task object that was removed.
     */
    public Task removeTask(int index) {
        assert index >= 0 && index < tasks.size() : "Removal index must refer to an existing task";
        return tasks.remove(index);
    }

    /**
     * Returns a task from the list given its index.
     *
     * @param index The zero-based index of the task to retrieve.
     * @return The Task object at the specified index.
     */
    public Task getTask(int index) {
        assert index >= 0 && index < tasks.size() : "Lookup index must refer to an existing task";
        return tasks.get(index);
    }

    /**
     * Returns the total number of tasks in the list.
     *
     * @return The size of the task list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Finds a task index matching a name or numerical index string.
     *
     * @param removeInput The user input representing either the task's index number or its description.
     * @return The zero-based index of the matching task, or -1 if no match is found.
     */
    public int findTaskIndex(String removeInput) {
        assert removeInput != null : "Task lookup input must have been read before searching";
        removeInput = removeInput.strip();
        int removeIndex = -1;
        // Step 1: Try treating input as an index number
        try {
            int taskNumber = Integer.parseInt(removeInput);
            if (taskNumber >= 1 && taskNumber <= tasks.size()) {
                removeIndex = taskNumber - 1;
            }
        } catch (NumberFormatException e) {
            // Fallthrough if not a number
        }

        // Step 2: Search for a matching task name description if index wasn't found
        if (removeIndex == -1) {
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getDescription().equals(removeInput)) {
                    if (removeIndex != -1) {
                        throw new IllegalArgumentException("multiple tasks have that name; enter a task number.");
                    }
                    removeIndex = i;
                }
            }
        }
        assert removeIndex == -1 || removeIndex < tasks.size() : "A found task index must be within the list";
        return removeIndex;
    }

    /**
     * Finds and returns a list of tasks that contain the given keyword in their description.
     *
     * @param keyword The search term.
     * @return A list of matching Task objects.
     */
    public List<Task> findTasks(String keyword) {
        assert keyword != null : "A search keyword must have been read before searching";
        String lowercaseKeyword = keyword.strip().toLowerCase(java.util.Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(java.util.Locale.ROOT).contains(lowercaseKeyword))
                .toList();
    }
}
