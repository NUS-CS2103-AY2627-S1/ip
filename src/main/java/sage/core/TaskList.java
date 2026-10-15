package sage.core;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import sage.task.Deadline;
import sage.task.Event;
import sage.task.Task;

/**
 * Stores and manages the collection of tasks in memory.
 */
public class TaskList {
    /**
     * Maximum number of tasks and notes supported by the application.
     */
    public static final int MAX_TASKS = 100;
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list with the provided tasks.
     *
     * @param tasks The initial tasks to populate the list with.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the list if it is below the capacity limit.
     *
     * @param task The task to add.
     * @throws IllegalStateException If the task list has reached its maximum capacity.
     * @throws IllegalArgumentException If the same task details are already present.
     */
    public void add(Task task) {
        if (tasks.stream().anyMatch(existing -> hasSameDetails(existing, task))) {
            throw new IllegalArgumentException("That item is already in your list. Use list to find it.");
        }
        if (tasks.size() >= MAX_TASKS) {
            throw new IllegalStateException("You have reached the maximum number of tasks.");
        }
        tasks.add(task);
        // A successful insertion must preserve the capacity enforced above.
        assert tasks.size() <= MAX_TASKS : "Adding a task must not exceed the task-list capacity";
    }

    /**
     * Compares task identity independently of completion state and harmless text spacing or case.
     * Parsed date-times are compared by value so equivalent input formats remain duplicates.
     *
     * @param first The existing item.
     * @param second The candidate item.
     * @return True if both items have the same category, description, and time details.
     */
    private static boolean hasSameDetails(Task first, Task second) {
        if (first.getType() != second.getType()
                || !normalize(first.getDescription()).equals(normalize(second.getDescription()))) {
            return false;
        }
        if (first instanceof Deadline firstDeadline && second instanceof Deadline secondDeadline) {
            return normalizeTime(firstDeadline.getBy(), firstDeadline.getByText())
                    .equals(normalizeTime(secondDeadline.getBy(), secondDeadline.getByText()));
        }
        if (first instanceof Event firstEvent && second instanceof Event secondEvent) {
            return normalizeTime(firstEvent.getFrom(), firstEvent.getFromText())
                    .equals(normalizeTime(secondEvent.getFrom(), secondEvent.getFromText()))
                    && normalizeTime(firstEvent.getTo(), firstEvent.getToText())
                    .equals(normalizeTime(secondEvent.getTo(), secondEvent.getToText()));
        }
        return true;
    }

    /**
     * Produces stable text for comparisons without changing the user-visible description.
     *
     * @param value The description or natural-language time.
     * @return Case-insensitive text with repeated whitespace collapsed.
     */
    private static String normalize(String value) {
        return value.strip().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    /**
     * Normalizes a parsed time or its retained natural-language representation.
     *
     * @param parsedTime The parsed value, if available.
     * @param text The retained text.
     * @return A consistent time identity.
     */
    private static String normalizeTime(LocalDateTime parsedTime, String text) {
        return parsedTime == null ? normalize(text) : parsedTime.toString();
    }

    /**
     * Removes and returns the task at the given index.
     *
     * @param index The zero-based index of the task to delete.
     * @return The removed task.
     * @throws IndexOutOfBoundsException If the index is outside the task list.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Marks the task at the given index as complete.
     *
     * @param index The zero-based index of the task.
     * @throws IndexOutOfBoundsException If the index is outside the task list.
     */
    public void markDone(int index) {
        tasks.get(index).markAsDone();
        // Task subclasses must honor the completion-state contract before callers persist the change.
        assert "X".equals(tasks.get(index).getStatusIcon()) : "Marked task must be complete";
    }

    /**
     * Marks the task at the given index as incomplete.
     *
     * @param index The zero-based index of the task.
     * @throws IndexOutOfBoundsException If the index is outside the task list.
     */
    public void markUndone(int index) {
        tasks.get(index).markAsNotDone();
        // Unmarking must restore the incomplete state before callers persist the change.
        assert " ".equals(tasks.get(index).getStatusIcon()) : "Unmarked task must be incomplete";
    }

    /**
     * Returns the task at the given index.
     *
     * @param index The zero-based index of the task.
     * @return The task at that index.
     * @throws IndexOutOfBoundsException If the index is outside the task list.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks currently in the list.
     *
     * @return The size of the task list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the backing list of tasks.
     *
     * @return The current task collection.
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * Returns tasks whose descriptions contain the given phrase, ignoring case and repeated whitespace.
     * A null or blank keyword produces an empty result.
     *
     * @param keyword The keyword to find in task descriptions.
     * @return A new mutable list containing the matching tasks in their original order.
     */
    public List<Task> find(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return new ArrayList<>();
        }

        String normalizedKeyword = normalize(keyword);
        return tasks.stream()
                .filter(task -> normalize(task.getDescription()).contains(normalizedKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
