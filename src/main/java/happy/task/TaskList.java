package happy.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

import happy.exception.HappyException;

/**
 * Encapsulates the task list and provides operations to add, delete,
 * mark, unmark, and query tasks.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Constructs an empty TaskList.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Constructs a TaskList with an existing list of tasks.
     *
     * @param tasks Initial list of tasks.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a new task to the task list.
     *
     * @param task Task object to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Adds one or more tasks to the task list.
     *
     * @param tasksToAdd Tasks to add.
     */
    public void add(Task... tasksToAdd) {
        assert tasksToAdd != null : "Task array to add should not be null";
        for (Task task : tasksToAdd) {
            assert task != null : "Task object to add should not be null";
            tasks.add(task);
        }
    }


    /**
     * Deletes a task at the specified 0-based index.
     *
     * @param index Index of task to delete.
     * @return The deleted Task object.
     * @throws HappyException If the index is out of bounds.
     */
    public Task delete(int index) throws HappyException {
        if (index < 0 || index >= tasks.size()) {
            throw new HappyException("OOPS!!! Task number " + (index + 1) + " does not exist.");
        }
        return tasks.remove(index);
    }

    /**
     * Marks a task at the specified 0-based index as done.
     *
     * @param index Index of task to mark.
     * @return The marked Task object.
     * @throws HappyException If the index is out of bounds.
     */
    public Task mark(int index) throws HappyException {
        if (index < 0 || index >= tasks.size()) {
            throw new HappyException("OOPS!!! Task number " + (index + 1) + " does not exist.");
        }
        Task task = tasks.get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Marks a task at the specified 0-based index as not done.
     *
     * @param index Index of task to unmark.
     * @return The unmarked Task object.
     * @throws HappyException If the index is out of bounds.
     */
    public Task unmark(int index) throws HappyException {
        if (index < 0 || index >= tasks.size()) {
            throw new HappyException("OOPS!!! Task number " + (index + 1) + " does not exist.");
        }
        Task task = tasks.get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Sets the priority of a task at the specified 0-based index.
     *
     * @param index 0-based index of task.
     * @param priority Priority level.
     * @return The updated Task object.
     * @throws HappyException If index is out of bounds.
     */
    public Task setPriority(int index, Priority priority) throws HappyException {
        if (index < 0 || index >= tasks.size()) {
            throw new HappyException("OOPS!!! Task number " + (index + 1) + " does not exist.");
        }
        Task task = tasks.get(index);
        task.setPriority(priority);
        return task;
    }

    /**
     * Retrieves the task at the specified 0-based index.
     *
     * @param index Index of task to retrieve.
     * @return Task object at index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the total number of tasks in the list.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the underlying ArrayList of Task objects.
     *
     * @return ArrayList containing tasks.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Finds and returns all tasks that occur on the specified target date.
     *
     * @param date Target date.
     * @return List of matching tasks.
     */
    public ArrayList<Task> getTasksOccurringOn(LocalDate date) {
        return tasks.stream()
                .filter(task -> task.isOccurringOn(date))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Finds and returns all tasks whose description contains the specified keyword.
     *
     * @param keyword Substring keyword to search for in task descriptions.
     * @return List of matching tasks.
     */
    public ArrayList<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Checks if an identical task already exists in the task list.
     *
     * @param newTask Task object to check.
     * @return true if a duplicate task exists, false otherwise.
     */
    public boolean isDuplicate(Task newTask) {
        for (Task existingTask : tasks) {
            if (existingTask.equals(newTask)) {
                return true;
            }
        }
        return false;
    }
}
