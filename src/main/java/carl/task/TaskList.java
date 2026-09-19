package carl.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import carl.exceptions.CarlCommandException;
import carl.exceptions.CarlException;
import carl.exceptions.CarlUnknownTaskException;

/**
 * Represents a list of tasks and provides operations to manage them.
 */
public class TaskList implements Comparator<Task> {

    private List<Task> tasks;

    /**
     * Constructs a TaskList with the given list of tasks.
     *
     * @param tasks The initial list of tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    @Override
    public int compare(Task t1, Task t2) {
        return t1.name.compareToIgnoreCase(t2.name);
    }

    /**
     * Adds a task to the task list.
     *
     * @param task The task to be added.
     * @throws CarlCommandException If a task with the same details already exists.
     */
    public void addTaskToList(Task task) throws CarlCommandException {
        if (task == null) {
            throw new CarlCommandException("A task cannot be empty.");
        }
        if (tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task))) {
            throw new CarlCommandException("A task with the same details already exists.");
        }
        tasks.add(task);
    }

    /**
     * Converts the current list of tasks into a formatted string for saving to storage.
     *
     * @return A string representation of all tasks for file storage.
     */
    public String toSaveString() {
        StringBuilder sb = new StringBuilder();

        for (Task task : tasks) {
            sb.append(task.toSaveFormat())
                    .append(System.lineSeparator());
        }

        return sb.toString();
    }

    /**
     * Captures task order and completion states so a failed save can be rolled back safely.
     *
     * @return an opaque snapshot of the current list state.
     */
    public Snapshot createSnapshot() {
        List<Task> taskCopies = new ArrayList<>(tasks);
        List<TaskStatus> statuses = tasks.stream().map(task -> task.status).toList();
        return new Snapshot(taskCopies, statuses);
    }

    /**
     * Restores a snapshot after a task-changing command fails.
     *
     * @param snapshot previously captured list state.
     */
    public void restore(Snapshot snapshot) {
        tasks = new ArrayList<>(snapshot.tasks);
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).status = snapshot.statuses.get(i);
        }
    }

    /**
     * Gets the number of tasks currently in the list.
     *
     * @return The number of tasks.
     */
    public int getTasksLeft() {
        return tasks.size();
    }

    /**
     * Retrieves a copy of all tasks in the list.
     *
     * @return A list containing all tasks.
     */
    public List<Task> getAllTasks() {
        return new ArrayList<>(this.tasks);
    }

    /**
     * Retrieves a list of tasks that are due on the specified date.
     *
     * @param date The date to filter tasks by.
     * @return A list of tasks due on the given date.
     */
    public List<Task> getTasksDueOn(LocalDate date) {
        List<Task> tasks = new ArrayList<>();

        for (Task task : this.tasks) {
            if (task.isDueOn(date)) {
                tasks.add(task);
            }
        }

        return tasks;
    }

    /**
     * Returns tasks ordered alphabetically by description without changing saved order.
     *
     * @return a sorted copy of the task list.
     */
    public List<Task> getSortedTasks() {
        List<Task> tasks = new ArrayList<>(this.tasks);
        tasks.sort(this);
        return tasks;
    }

    /**
     * Finds tasks that match the given keyword.
     *
     * @param keyword The keyword to search for in task names.
     * @return A list of tasks that match the keyword.
     */
    public List<Task> findTask(String keyword) {
        return tasks.stream().filter(task -> task.hasNameMatch(keyword)).toList();
    }

    /**
     * Marks the task at the specified index as done.
     *
     * @param index The list index of the task to mark as done.
     * @return The task that was marked as done.
     * @throws CarlCommandException If the index is invalid or the task is already done.
     */
    public Task markTaskAsDone(int index) throws CarlCommandException {
        if (index < 0 || index >= tasks.size()) {
            throw new CarlCommandException("That task number does not exist. Use `list` to check task numbers.");
        }

        Task task = tasks.get(index);
        if (!task.tryMarkAsDone()) {
            throw new CarlCommandException("That task is already completed.");
        }
        return task;
    }

    /**
     * Deletes the task at the specified index.
     *
     * @param index The list index of the task to delete.
     * @return The task that was deleted.
     * @throws CarlException If the index is out of bounds or the task doesn't exist.
     */
    public Task deleteTask(int index) throws CarlException {

        if (index < 0 || index >= tasks.size()) {
            throw new CarlUnknownTaskException();
        }

        Task task = tasks.get(index);

        if (task == null) {
            throw new CarlUnknownTaskException();
        }

        tasks.remove(index);

        return task;

    }

    /**
     * Marks the task at the specified index as not done.
     *
     * @param index The list index of the task to unmark.
     * @return The task that was unmarked.
     * @throws CarlCommandException If the index is invalid or the task is already not done.
     */
    public Task markTaskAsUndone(int index) throws CarlCommandException {

        if (index < 0 || index >= tasks.size()) {
            throw new CarlCommandException("That task number does not exist. Use `list` to check task numbers.");
        }

        Task task = tasks.get(index);

        if (!task.tryUnmarkAsDone()) {

            throw new CarlCommandException("That task is already incomplete.");
        }

        return task;
    }

    /** Stores task references and their mutable completion states for rollback. */
    public static final class Snapshot {
        private final List<Task> tasks;
        private final List<TaskStatus> statuses;

        private Snapshot(List<Task> tasks, List<TaskStatus> statuses) {
            this.tasks = tasks;
            this.statuses = statuses;
        }
    }
}
