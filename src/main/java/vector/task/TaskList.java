package vector.task;

import java.util.ArrayList;

/**
 * Represents a list of tasks and provides operations to modify or retrieve them.
 */
public class TaskList {
    private ArrayList<Task> tasks;

    /**
     * Constructs a TaskList with an existing list of tasks.
     *
     * @param tasks An ArrayList of Task objects.
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "Initial task list should not be null";
        this.tasks = tasks;
    }

    /**
     * Constructs an empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Adds a task to the list.
     *
     * @param task The task to add.
     */
    public void add(Task task) throws vector.VectorException {
        assert task != null : "Cannot add a null task to the list";
        if (tasks.contains(task)) {
            throw new vector.VectorException("An identical task already exists in the matrix.");
        }
        tasks.add(task);
    }

    /**
     * Removes a task from the list at the specified index.
     *
     * @param index The zero-based index of the task to remove.
     * @return The removed task.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Retrieves a task from the list at the specified index.
     *
     * @param index The zero-based index of the task to retrieve.
     * @return The task at the given index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Gets the current number of tasks in the list.
     *
     * @return The size of the task list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the underlying ArrayList of tasks.
     *
     * @return The ArrayList of Task objects.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Finds tasks whose description contains the keyword.
     *
     * @param keyword The keyword to search for.
     * @return An ArrayList of matching tasks.
     */
    public ArrayList<Task> findTasks(String keyword) {
        return tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    }
}
