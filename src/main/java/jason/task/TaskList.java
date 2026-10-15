package jason.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Stores tasks and provides one-based list operations for the application.
 */
public class TaskList {
    private List<Task> taskList;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.taskList = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks tasks to place into the list.
     */
    public TaskList(Task... tasks) {
        this.taskList = new ArrayList<>(List.of(tasks));
    }

    /**
     * Creates a task list containing a copy of the supplied tasks.
     *
     * @param tasks tasks to copy into the list.
     */
    public TaskList(List<Task> tasks) {
        this.taskList = new ArrayList<>(tasks);
    }

    /**
     * Appends a task to the list.
     *
     * @param t task to append.
     */
    public void add(Task t) {
        this.taskList.add(t);
    }

    /**
     * Returns the task at a one-based index.
     *
     * @param i one-based task index.
     * @return task at the specified index.
     */
    public Task get(int i) {
        return this.taskList.get(i - 1);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return number of tasks.
     */
    public int size() {
        return this.taskList.size();
    }

    /**
     * Removes the task at a one-based index.
     *
     * @param i one-based task index.
     */
    public void remove(int i) {
        this.taskList.remove(get(i));
    }

    /**
     * Returns whether a one-based index identifies a task in this list.
     *
     * @param i one-based task index.
     * @return true if the index identifies a task; otherwise false.
     */
    public boolean isValidIndex(int i) {
        return i <= this.size() && i > 0;
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring letter case.
     *
     * @param keyword text to search for.
     * @return matching tasks in their original list order.
     */
    public List<Task> find(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }

        String searchTerm = keyword.trim().toLowerCase(Locale.ROOT);
        return taskList.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(searchTerm))
                .toList();
    }

    /**
     * Returns a numbered, human-readable representation of the task list.
     *
     * @return formatted task list, or an empty-list message.
     */
    @Override
    public String toString() {
        if (taskList.isEmpty()) {
            return "List is empty. Please add something!";
        }

        String taskLines = IntStream.range(0, taskList.size())
                .mapToObj(index -> String.format("%d. %s\n", index + 1, taskList.get(index)))
                .collect(Collectors.joining());
        return "Here are the tasks on your list: \n" + taskLines
                + String.format("You have %d tasks.", taskList.size());
    }

}
