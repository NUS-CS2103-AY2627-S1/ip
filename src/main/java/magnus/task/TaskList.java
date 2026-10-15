package magnus.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Stores and manages the tasks in Magnus's task list.
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
     * Creates a task list containing a copy of the supplied tasks.
     *
     * @param tasks The tasks with which to initialize the list.
     */
    public TaskList(List<Task> tasks) {
        Objects.requireNonNull(tasks, "The initial task collection must not be null");
        if (tasks.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("The initial task collection must not contain null tasks");
        }
        requireUniqueTaskDetails(tasks);
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Verifies that a task collection does not contain duplicate task details.
     *
     * @param tasks The tasks to validate.
     * @throws IllegalArgumentException If two tasks represent the same work.
     */
    private void requireUniqueTaskDetails(List<Task> tasks) {
        for (int firstIndex = 0; firstIndex < tasks.size(); firstIndex++) {
            Task firstTask = tasks.get(firstIndex);
            boolean hasDuplicate = IntStream.range(firstIndex + 1, tasks.size())
                    .anyMatch(secondIndex -> firstTask.hasSameDetails(tasks.get(secondIndex)));
            if (hasDuplicate) {
                throw new IllegalArgumentException(
                        "The initial task collection must not contain duplicate task details");
            }
        }
    }

    /**
     * Captures the list membership, order, and completion states for transactional rollback.
     *
     * @return A snapshot that can later be passed to {@link #restoreSnapshot(Snapshot)}.
     */
    public Snapshot createSnapshot() {
        List<Task> snapshotTasks = new ArrayList<>(this.tasks);
        List<Boolean> snapshotCompletionStatuses = this.tasks.stream()
                .map(Task::isDone)
                .toList();
        return new Snapshot(snapshotTasks, snapshotCompletionStatuses);
    }

    /**
     * Restores the exact list membership, order, and completion states in a prior snapshot.
     *
     * @param snapshot The snapshot to restore.
     */
    public void restoreSnapshot(Snapshot snapshot) {
        Objects.requireNonNull(snapshot);
        this.tasks.clear();
        this.tasks.addAll(snapshot.tasks);
        for (int index = 0; index < this.tasks.size(); index++) {
            if (snapshot.completionStatuses.get(index)) {
                this.tasks.get(index).markAsDone();
            } else {
                this.tasks.get(index).markAsUndone();
            }
        }
    }

    /**
     * Returns an immutable snapshot of the tasks currently in this list.
     *
     * @return An immutable copy of the stored tasks.
     */
    public List<Task> getTasks() {
        return List.copyOf(this.tasks);
    }

    /**
     * Returns the deadline tasks whose deadlines fall on the specified date.
     *
     * @param date The deadline date to match.
     * @return A new task list containing the matching deadline tasks.
     */
    public TaskList filterDeadlinesOnDate(LocalDate date) {
        List<Task> filteredTasks = this.tasks.stream()
                .filter(task -> isDeadlineOnDate(task, date))
                .toList();
        return new TaskList(filteredTasks);
    }

    /**
     * Returns the event tasks fully enclosed by the inclusive date range.
     *
     * @param startDate The first date in the range.
     * @param endDate The last date in the range.
     * @return A new task list containing the matching event tasks.
     */
    public TaskList filterEventsWithinDateRange(LocalDate startDate, LocalDate endDate) {
        assert !startDate.isAfter(endDate)
                : "The event filter requires an ordered date range";
        List<Task> filteredTasks = this.tasks.stream()
                .filter(task -> isEventWithinDateRange(task, startDate, endDate))
                .toList();
        return new TaskList(filteredTasks);
    }

    /**
     * Returns whether a task is a deadline on the specified date.
     *
     * @param task The task to examine.
     * @param date The deadline date to match.
     * @return {@code true} if the task is a deadline on the date.
     */
    private boolean isDeadlineOnDate(Task task, LocalDate date) {
        return task instanceof DeadlineTask deadlineTask
                && deadlineTask.getDeadline().toLocalDate().equals(date);
    }

    /**
     * Returns whether an event task is fully enclosed by the inclusive date range.
     *
     * @param task The task to examine.
     * @param startDate The first date in the range.
     * @param endDate The last date in the range.
     * @return {@code true} if the task is an event within the date range.
     */
    private boolean isEventWithinDateRange(Task task, LocalDate startDate, LocalDate endDate) {
        if (!(task instanceof EventTask eventTask)) {
            return false;
        }

        boolean isStartWithinRange = !eventTask.getStart().toLocalDate().isBefore(startDate);
        boolean isEndWithinRange = !eventTask.getEnd().toLocalDate().isAfter(endDate);
        return isStartWithinRange && isEndWithinRange;
    }

    /**
     * Returns tasks whose descriptions contain the specified query, ignoring case.
     * The matching tasks retain their original order.
     *
     * @param query The text to search for in task descriptions.
     * @return A new task list containing the matching tasks.
     */
    public TaskList filterTasksByDescription(String query) {
        String normalizedQuery = query.toLowerCase(Locale.ROOT);
        List<Task> filteredTasks = this.tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedQuery))
                .toList();
        return new TaskList(filteredTasks);
    }

    /**
     * Returns the number of tasks in this task list.
     *
     * @return The number of stored tasks.
     */
    public int getTaskCount() {
        return this.tasks.size();
    }

    /**
     * Returns the number of completed tasks in this task list.
     *
     * @return The number of tasks marked as completed.
     */
    public long countCompletedTasks() {
        return this.tasks.stream()
                .filter(Task::isDone)
                .count();
    }

    /**
     * Adds a task to the end of this task list.
     *
     * @param task The task to add.
     * @throws IllegalArgumentException If an existing task has the same details.
     */
    public void addTask(Task task) {
        Task validatedTask = Objects.requireNonNull(task, "A task list must not contain null tasks");
        if (containsTaskWithSameDetails(validatedTask)) {
            throw new IllegalArgumentException("A task list must not contain duplicate task details");
        }
        this.tasks.add(validatedTask);
    }

    /**
     * Returns whether this list already contains a task with the same details as the candidate.
     *
     * @param candidate The proposed task.
     * @return {@code true} if an equivalent task already exists.
     */
    public boolean containsTaskWithSameDetails(Task candidate) {
        return containsTaskWithSameDetailsExcept(candidate, -1);
    }

    /**
     * Returns whether this list contains an equivalent task outside one excluded position.
     * This supports updates that leave a task's own details unchanged.
     *
     * @param candidate The proposed task.
     * @param excludedIndex The zero-based position to ignore.
     * @return {@code true} if another equivalent task exists.
     */
    public boolean containsTaskWithSameDetailsExcept(Task candidate, int excludedIndex) {
        Objects.requireNonNull(candidate);
        return IntStream.range(0, this.tasks.size())
                .filter(index -> index != excludedIndex)
                .anyMatch(index -> this.tasks.get(index).hasSameDetails(candidate));
    }

    /**
     * Removes and returns the task at the specified zero-based index.
     * Tasks after the removed task are shifted one position toward the start of the list.
     *
     * @param index The zero-based index of the task to remove.
     * @return The task that was removed.
     * @throws IndexOutOfBoundsException If the index is outside the task list.
     */
    public Task removeTask(int index) {
        return this.tasks.remove(index);
    }

    /**
     * Returns the task at the specified zero-based index.
     *
     * @param index The zero-based index of the task to return.
     * @return The task at the specified index.
     * @throws IndexOutOfBoundsException If the index is outside the task list.
     */
    public Task getTask(int index) {
        return this.tasks.get(index);
    }

    /**
     * Replaces the task at the specified zero-based index.
     *
     * @param index The zero-based index of the task to replace.
     * @param task The replacement task.
     * @throws IndexOutOfBoundsException If the index is outside the task list.
     * @throws IllegalArgumentException If another task has the same details.
     */
    public void replaceTask(int index, Task task) {
        Objects.checkIndex(index, this.tasks.size());
        Task validatedTask = Objects.requireNonNull(task, "A task list must not contain null tasks");
        if (containsTaskWithSameDetailsExcept(validatedTask, index)) {
            throw new IllegalArgumentException("A task list must not contain duplicate task details");
        }
        this.tasks.set(index, validatedTask);
    }

    /**
     * Marks the task at the specified zero-based index as completed.
     *
     * @param index The zero-based index of the task to mark.
     * @throws IndexOutOfBoundsException If the index is outside the task list.
     */
    public void markTaskAsDone(int index) {
        this.tasks.get(index).markAsDone();
    }

    /**
     * Marks the task at the specified zero-based index as incomplete.
     *
     * @param index The zero-based index of the task to mark.
     * @throws IndexOutOfBoundsException If the index is outside the task list.
     */
    public void markTaskAsUndone(int index) {
        this.tasks.get(index).markAsUndone();
    }

    /**
     * Formats all tasks as a one-based numbered list.
     *
     * @return The formatted task list, or an empty string if there are no tasks.
     */
    public String formatTasks() {
        return IntStream.range(0, this.tasks.size())
                .mapToObj(index -> String.format("\t%d. %s", index + 1, this.tasks.get(index)))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * Immutable rollback data produced by a task list.
     */
    public static final class Snapshot {
        private final List<Task> tasks;
        private final List<Boolean> completionStatuses;

        private Snapshot(List<Task> tasks, List<Boolean> completionStatuses) {
            this.tasks = tasks;
            this.completionStatuses = completionStatuses;
        }
    }
}
