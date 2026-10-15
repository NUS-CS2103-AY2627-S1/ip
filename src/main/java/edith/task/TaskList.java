package edith.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Maintains Edith's ordered collection of tasks. */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks in their existing order.
     *
     * @param tasks the tasks to add to this task list
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Inserts a task at a zero-based position when a failed deletion is undone.
     *
     * @param index the position at which to insert the task
     * @param task the task to restore
     */
    public void add(int index, Task task) {
        tasks.add(index, task);
    }

    /** Returns whether a task with the same type, description, and dates already exists. */
    public boolean containsEquivalent(Task candidate) {
        return tasks.stream().anyMatch(task -> task.getClass() == candidate.getClass()
                && task.toStorageString().substring(6).equals(candidate.toStorageString().substring(6)));
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index the zero-based position of the task
     * @return the task at the specified position
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param index the zero-based position of the task
     * @return the task removed from the specified position
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the list contains no tasks.
     *
     * @return {@code true} if the list is empty
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns tasks matching a tag exactly or containing the supplied text in a description or tag.
     * A valid tag used as the entire query selects exact tag matching. All matching ignores letter case.
     *
     * @param keyword the word or phrase to search for
     * @return matching tasks in their original list order
     */
    public TaskList find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        boolean isExactTagSearch = Task.isValidTag(keyword);
        List<Task> matches = tasks.stream()
                .filter(task -> {
                    if (isExactTagSearch) {
                        return task.getTags().stream()
                                .anyMatch(tag -> tag.toLowerCase(Locale.ROOT).equals(normalizedKeyword));
                    }
                    return task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword)
                            || task.getTags().stream()
                                    .anyMatch(tag -> tag.toLowerCase(Locale.ROOT).contains(normalizedKeyword));
                })
                .toList();
        return new TaskList(matches);
    }

    /**
     * Returns an immutable snapshot for components that need to read all tasks.
     *
     * @return the current tasks in list order
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }
}
