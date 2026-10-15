package thomas.task;

import java.util.ArrayList;
import java.util.stream.Collectors;

import thomas.ThomasException;

/**
 * Stores tasks and provides operations for changing the task list.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list from existing tasks.
     *
     * @param tasks Tasks to store.
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "Task collection must not be null";
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        assert task != null : "Task to add must not be null";
        tasks.add(task);
    }

    /**
     * Returns whether a task with the same type and details is already stored.
     * Completion status is intentionally ignored.
     *
     * @param candidate Task to compare.
     * @return {@code true} if an equivalent task exists.
     */
    public boolean containsEquivalent(Task candidate) {
        assert candidate != null : "Candidate task must not be null";
        String candidateDetails = withoutStatus(candidate.toFileFormat());
        return tasks.stream().anyMatch(task -> withoutStatus(task.toFileFormat()).equals(candidateDetails));
    }

    private String withoutStatus(String fileFormat) {
        return fileFormat.replaceFirst("^([TDE] \\| )[YN]( \\| )", "$1$2");
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index position in the list
     * @return the task at that position
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "Task index must be within the list";
        return tasks.get(index);
    }

    /**
     * Removes the task at the given index.
     *
     * @param index Zero-based index of the task.
     * @return Removed task.
     */
    public Task delete(int index) {
        assert index >= 0 && index < tasks.size() : "Task index must be within the list";
        return tasks.remove(index);
    }

    /**
     * Returns how many tasks are currently stored.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the underlying list of tasks.
     *
     * @return the stored tasks
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Marks the task at the given index as done.
     *
     * @param index Zero-based index of the task.
     * @throws ThomasException If the task is already done.
     */
    public void mark(int index) throws ThomasException {
        Task task = get(index);
        if (task.isDone()) {
            throw new ThomasException("Task is already marked as done!");
        }
        task.markAsDone();
    }

    /**
     * Marks the task at the given index as not done.
     *
     * @param index Zero-based index of the task.
     * @throws ThomasException If the task is already not done.
     */
    public void unmark(int index) throws ThomasException {
        Task task = get(index);
        if (!task.isDone()) {
            throw new ThomasException("Task is already marked as not done!");
        }
        task.markAsNotDone();
    }

    /**
     * Returns all tasks with descriptions containing the given keyword.
     * Matching is case-insensitive.
     *
     * @param keyword text to find in task descriptions
     * @return matching tasks
     */
    public ArrayList<Task> findByKeyword(String keyword) {
        String normalizedKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(normalizedKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
