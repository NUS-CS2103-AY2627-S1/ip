package mary.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Owns the collection of tasks and task-list operations.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list from loaded tasks.
     */
    public TaskList(List<Task> loadedTasks) {
        tasks = new ArrayList<>(loadedTasks);
    }

    /**
     * Returns whether the list contains no tasks.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at the zero-based index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Appends a task to the list.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the zero-based index.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns a defensive copy of the task collection.
     */
    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    /**
     * Replaces list order after a proposed change has been saved successfully.
     */
    public void replaceWith(List<Task> savedTasks) {
        tasks.clear();
        tasks.addAll(savedTasks);
    }

    /**
     * Sorts by deadline due time or event start time, with undated tasks last.
     * Equal times and undated tasks retain their relative order and completion state.
     */
    public void sortChronologically() {
        tasks.sort(Comparator.comparing(TaskList::getScheduledTime,
                Comparator.nullsLast(Comparator.naturalOrder())));
    }

    /**
     * Returns the relevant chronological key, or null for an undated task.
     */
    private static LocalDateTime getScheduledTime(Task task) {
        if (task instanceof Deadline deadline) {
            return deadline.getBy();
        }
        if (task instanceof Event event) {
            return event.getFrom();
        }
        return null;
    }

    /**
     * Finds tasks whose descriptions contain the keyword, ignoring case.
     *
     * @param keyword non-null search text.
     * @return a new list of matching task objects in their original order.
     */
    public List<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.matchesDescription(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
