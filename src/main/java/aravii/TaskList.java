package aravii;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Owns an ordered collection of polymorphic tasks, independent of storage and UI.
 */
public class TaskList {
    private final List<Task> tasks = new ArrayList<>();

    /**
     * Creates an empty task list.
     */
    public TaskList() {
    }

    /**
     * Adds one or more tasks in the supplied order.
     *
     * @param newTasks The tasks to add.
     */
    public void add(Task... newTasks) {
        if (newTasks == null) {
            throw new IllegalArgumentException("Tasks must not be null.");
        }
        for (Task task : newTasks) {
            if (task == null) {
                throw new IllegalArgumentException("Task list must not contain null tasks.");
            }
        }
        tasks.addAll(List.of(newTasks));
    }

    /**
     * Returns the task selected by a one-based task number.
     *
     * @param taskNumber The one-based task number.
     * @return The selected task.
     */
    public Task get(String taskNumber) {
        if (taskNumber == null) {
            throw new IllegalArgumentException("Please provide a valid task number.");
        }
        try {
            int number = Integer.parseInt(taskNumber.strip());
            if (number < 1 || number > tasks.size()) {
                throw new IllegalArgumentException("That task number does not exist.");
            }
            return tasks.get(number - 1);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Please provide a valid task number.", exception);
        }
    }

    /**
     * Removes and returns the task selected by a one-based task number.
     *
     * @param taskNumber The one-based task number.
     * @return The removed task.
     */
    public Task remove(String taskNumber) {
        Task task = get(taskNumber);
        tasks.remove(task);
        return task;
    }

    /**
     * Returns a read-only snapshot of the task order for storage.
     *
     * @return The tasks in their current order.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Returns the total number of tasks, including completed tasks.
     *
     * @return The number of tasks in the list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Sorts tasks stably by description without depending on the system locale.
     */
    public void sortByDescription() {
        tasks.sort(Comparator.comparing(Task::getDescription, String.CASE_INSENSITIVE_ORDER));
    }

    /**
     * Formats all tasks with one-based numbering.
     *
     * @return The formatted task list.
     */
    public String formatAll() {
        return formatIndices(IntStream.range(0, tasks.size()));
    }

    /**
     * Formats matching tasks while retaining their original list numbers.
     *
     * @param keyword The keyword to search for.
     * @return The matching tasks.
     */
    public String formatMatching(String keyword) {
        return formatIndices(IntStream.range(0, tasks.size()).filter(index -> tasks.get(index).matches(keyword)));
    }

    private String formatIndices(IntStream indices) {
        String output = indices.mapToObj(index -> (index + 1) + ". " + tasks.get(index))
                .collect(Collectors.joining("\n"));
        return output.isEmpty() ? output : output + "\n";
    }
}
