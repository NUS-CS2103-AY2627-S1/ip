package tem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Stores the current task list and provides operations to change it.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the given tasks.
     *
     * @param tasks initial tasks to store.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Initial task list should not be null";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Creates a task list containing the given tasks in order.
     *
     * @param tasks initial tasks to store.
     */
    public TaskList(Task... tasks) {
        assert tasks != null : "Initial task array should not be null";
        this.tasks = new ArrayList<>(List.of(tasks));
    }

    /**
     * Returns the tasks in this list.
     *
     * @return unmodifiable view of tasks currently stored
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Returns how many tasks are in the list.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index zero-based position in the list.
     * @return selected task
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "Task index should be within list bounds";
        return tasks.get(index);
    }

    /**
     * Adds multiple tasks to the end of the list.
     *
     * @param tasksToAdd tasks to append.
     */
    public void addAll(Task... tasksToAdd) {
        for (Task task : tasksToAdd) {
            tasks.add(task);
        }
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add.
     */
    public void add(Task task) {
        assert task != null : "Added task should not be null";
        tasks.add(task);
    }

    /**
     * Returns whether a task with the same user-visible details is already stored.
     *
     * @param candidate task that may be added.
     * @return whether an equivalent task is already in the list
     */
    public boolean containsEquivalent(Task candidate) {
        assert candidate != null : "Candidate task should not be null";
        return tasks.stream().anyMatch(task -> task.hasSameDetails(candidate));
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param index zero-based position in the list.
     * @return removed task
     */
    public Task delete(int index) {
        assert index >= 0 && index < tasks.size() : "Task index should be within list bounds";
        return tasks.remove(index);
    }

    /**
     * Returns zero-based indices of tasks whose descriptions contain the keyword.
     *
     * @param keyword text to search for in task descriptions.
     * @return indices of matching tasks in their current list order
     */
    public List<Integer> findMatchingIndices(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return IntStream.range(0, tasks.size())
                .filter(i -> tasks.get(i).getDescription().toLowerCase().contains(lowerKeyword))
                .boxed()
                .collect(Collectors.toList());
    }

    /**
     * Reorders tasks so deadlines come first by due date, then other tasks.
     *
     * Non-deadline tasks keep their previous relative order after the deadlines.
     */
    public void sortChronologically() {
        List<Task> sortedTasks = new ArrayList<>();
        tasks.stream()
                .filter(Deadline.class::isInstance)
                .map(Deadline.class::cast)
                .sorted(Comparator.comparing(Deadline::getBy))
                .forEach(sortedTasks::add);
        tasks.stream()
                .filter(task -> !(task instanceof Deadline))
                .forEach(sortedTasks::add);
        tasks.clear();
        tasks.addAll(sortedTasks);
    }
}
