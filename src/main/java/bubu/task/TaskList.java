package bubu.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import bubu.exception.DuplicateTaskException;

/**
 * Owns the chatbot's tasks and provides controlled operations on the list.
 */
public class TaskList {
    private static final int FIRST_TASK_INDEX = 0;

    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list from tasks loaded from storage.
     *
     * @param tasks loaded tasks.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Tasks cannot be null";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list if an equivalent task is not present.
     *
     * @param task task to add.
     * @throws DuplicateTaskException if an equivalent task already exists.
     */
    public void add(Task task) throws DuplicateTaskException {
        assert task != null : "Task cannot be null";
        if (containsEquivalentTask(task)) {
            throw new DuplicateTaskException();
        }
        tasks.add(task);
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index zero-based task index.
     * @return task at the index.
     */
    public Task get(int index) {
        assert hasIndex(index) : "No task exists at index " + index;
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index zero-based task index.
     * @return removed task.
     */
    public Task remove(int index) {
        assert hasIndex(index) : "No task exists at index " + index;
        return tasks.remove(index);
    }

    /**
     * Inserts a task at a zero-based index.
     *
     * @param index zero-based insertion index.
     * @param task task to insert.
     */
    public void insert(int index, Task task) {
        assert index >= FIRST_TASK_INDEX && index <= tasks.size()
                : "Invalid insertion index " + index;
        assert task != null : "Task cannot be null";
        tasks.add(index, task);
    }

    /**
     * Checks whether a zero-based index identifies a task.
     *
     * @param index zero-based task index.
     * @return whether the index is valid.
     */
    public boolean hasIndex(int index) {
        return index >= FIRST_TASK_INDEX && index < tasks.size();
    }

    /**
     * Returns the number of tasks in the list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a read-only view for displaying or saving the current tasks.
     *
     * @return unmodifiable task list.
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Finds tasks whose descriptions contain a given keyword, ignoring letter case.
     *
     * @param keyword the keyword to search for.
     * @return a list of matching tasks.
     */
    public List<Task> findMatchingTasks(String keyword) {
        assert keyword != null : "Keyword cannot be null";
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> descriptionContainsKeyword(task, normalizedKeyword))
                .toList();
    }

    /**
     * Checks whether a task description contains a normalized search keyword.
     *
     * @param task the task to check
     * @param normalizedKeyword the normalized keyword to search for
     * @return true if the description contains the keyword, false otherwise
     */
    private boolean descriptionContainsKeyword(Task task, String normalizedKeyword) {
        String normalizedDescription = task.getDescription().toLowerCase(Locale.ROOT);
        return normalizedDescription.contains(normalizedKeyword);
    }

    /**
     * Finds incomplete tasks that have a scheduled time within the given window.
     *
     * @param start beginning of the time window, inclusive.
     * @param end end of the time window, inclusive.
     * @return incomplete scheduled tasks within the time window.
     */
    public List<Task> findIncompleteTasksDueBetween(LocalDateTime start, LocalDateTime end) {
        assert start != null : "Start time cannot be null";
        assert end != null : "End time cannot be null";
        assert !end.isBefore(start) : "End time cannot be before start time";

        return tasks.stream()
                .filter(task -> !task.isDone())
                .filter(task -> isDueBetween(task, start, end))
                .toList();
    }

    /**
     * Returns whether a task's reminder time is within the given inclusive window.
     *
     * @param task the task to check
     * @param start the start of the time window
     * @param end the end of the time window
     * @return true if the task's reminder time is within the window, false otherwise
     */
    private boolean isDueBetween(Task task, LocalDateTime start, LocalDateTime end) {
        return task.getReminderTime()
                .filter(reminderTime -> !reminderTime.isBefore(start))
                .filter(reminderTime -> !reminderTime.isAfter(end))
                .isPresent();
    }

    /**
     * Checks whether the list already contains the same task details and type.
     *
     * @param candidate task to compare with existing tasks.
     * @return true if an equivalent task is already present.
     */
    private boolean containsEquivalentTask(Task candidate) {
        return tasks.stream().anyMatch(task -> hasSameDetails(task, candidate));
    }

    /**
     * Compares task details that are persisted and shown to the user.
     *
     * @param first first task to compare.
     * @param second second task to compare.
     * @return true if both tasks have the same type and details.
     */
    private boolean hasSameDetails(Task first, Task second) {
        if (first.getClass() != second.getClass()
                || !first.getDescription().equals(second.getDescription())) {
            return false;
        }
        if (first instanceof Deadline firstDeadline && second instanceof Deadline secondDeadline) {
            return firstDeadline.getDeadline().equals(secondDeadline.getDeadline());
        }
        if (first instanceof Event firstEvent && second instanceof Event secondEvent) {
            return firstEvent.getStart().equals(secondEvent.getStart())
                    && firstEvent.getEnd().equals(secondEvent.getEnd());
        }
        return true;
    }
}
