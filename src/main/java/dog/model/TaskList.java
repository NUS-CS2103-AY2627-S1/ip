package dog.model;

import java.util.ArrayList;

import dog.exceptions.DogException;

/**
 * Manages a list of tasks with operations to add, delete, mark, and retrieve tasks.
 */
public class TaskList {
    private ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<Task>();
    }

    /**
     * Creates a task list with initial tasks.
     *
     * @param initialTasks the initial list of tasks.
     */
    public TaskList(ArrayList<Task> initialTasks) {
        this.tasks = new ArrayList<Task>(initialTasks);
    }

    /**
     * Adds a task to the list.
     *
     * @param task the task to add.
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Gets a task at the specified index.
     *
     * @param index the index of the task (0-based).
     * @return the task at the index.
     * @throws DogException if index is out of bounds.
     */
    public Task getTask(int index) throws DogException {
        try {
            return tasks.get(index);
        } catch (IndexOutOfBoundsException e) {
            throw new DogException("ARF ARF Task index out of bounds.");
        }
    }

    /**
     * Deletes a task from the list at the specified index.
     *
     * @param index the index of the task to delete (0-based).
     * @return the deleted task.
     * @throws DogException if index is out of bounds.
     */
    public Task deleteTask(int index) throws DogException {
        try {
            return tasks.remove(index);
        } catch (IndexOutOfBoundsException e) {
            throw new DogException("ARF ARF Task index out of bounds.");
        }
    }

    /**
     * Marks a task as done at the specified index.
     *
     * @param index the index of the task to mark (0-based).
     * @throws DogException if index is out of bounds.
     */
    public void markTask(int index) throws DogException {
        try {
            Task taskToMark = tasks.get(index);
            assert taskToMark != null : "task to be marked should not be null";
            taskToMark.markAsDone();
        } catch (IndexOutOfBoundsException e) {
            throw new DogException("ARF ARF Task index out of bounds.");
        }
    }

    /**
     * Gets the total number of tasks in the list.
     *
     * @return the number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Checks if the task list is empty.
     *
     * @return true if the list is empty, false otherwise.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns an ArrayList copy of the tasks.
     *
     * @return a copy of the task list.
     */
    public ArrayList<Task> getTasks() {
        return new ArrayList<Task>(tasks);
    }

    /**
     * Finds tasks that contain the given keyword in their description.
     *
     * @param keyword the keyword to search for.
     * @return a new TaskList containing matching tasks.
     * @throws DogException if keyword is empty or null.
     */
    public TaskList findTasks(String keyword) throws DogException {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new DogException("ARF ARF Please provide a search keyword. (e.g., 'find book')");
        }

        TaskList foundTasks = new TaskList();
        String searchKeyword = keyword.trim().toLowerCase();

        for (Task task : tasks) {
            if (task.containsKeyword(searchKeyword)) {
                foundTasks.addTask(task);
            }
        }

        return foundTasks;
    }

    /**
     * Returns a string representation of the task list.
     *
     * @return a formatted string showing all tasks in the list
     */
    @Override
    public String toString() {
        if (tasks.isEmpty()) {
            return " (No tasks found)";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            sb.append((i + 1)).append(". ").append(tasks.get(i)).append("\n");
        }
        return sb.toString().trim();
    }
}
