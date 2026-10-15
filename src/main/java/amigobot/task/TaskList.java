package amigobot.task;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Wraps an ArrayList of tasks and provides operations to add, delete, and access tasks.
 */
public class TaskList {

    private final ArrayList<Task> tasks;

    /** Constructs an empty TaskList. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a TaskList with the given pre-existing tasks.
     *
     * @param tasks the list of tasks to wrap.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds one or more tasks to the list.
     *
     * @param newTasks the tasks to add.
     */
    public void addTask(Task... newTasks) {
        for (Task task : newTasks) {
            tasks.add(task);
        }
    }

    /**
     * Removes and returns the task at the given index (0-based).
     */
    public Task deleteTask(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given index.
     *
     * @param index the 0-based index.
     * @return the task at the index.
     */
    public Task getTask(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns tasks whose description contains the given keyword (case-insensitive).
     */
    public ArrayList<Task> findTasks(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns the underlying list for serialization by Storage.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }
}
