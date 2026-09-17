package yuki.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

import yuki.exception.YukiException;
import yuki.time.TaskDateTime;

/**
 * Owns and manages Yuki's ordered collection of tasks.
 */
public class TaskList {
    /** Tasks in the same order in which the user added them. */
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks Tasks loaded from storage.
     */
    public TaskList(List<Task> tasks) {
        Objects.requireNonNull(tasks, "tasks must not be null");
        this.tasks = new ArrayList<>();
        tasks.forEach(this::addTask);
    }

    /**
     * Adds a unique task to the end of the list.
     *
     * @throws YukiException If another task already has the same details.
     */
    public void addTask(Task task) {
        Objects.requireNonNull(task, "task must not be null");
        if (tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task))) {
            throw new YukiException("That task is already in the list.");
        }
        tasks.add(task);
    }

    /**
     * Removes and returns the task at a user-facing task number.
     *
     * @param taskNumber One-based task number entered by the user.
     * @return The removed task.
     * @throws YukiException If the number does not identify an existing task.
     */
    public Task deleteTask(int taskNumber) {
        return tasks.remove(toListIndex(taskNumber));
    }

    /**
     * Marks and returns the task at a user-facing task number.
     *
     * @param taskNumber One-based task number entered by the user.
     * @return The task that was marked as done.
     * @throws YukiException If the number does not identify an existing task.
     */
    public Task markTask(int taskNumber) {
        Task task = getTask(taskNumber);
        if (task.isDone()) {
            throw new YukiException("That task is already marked as done.");
        }
        task.markAsDone();
        return task;
    }

    /**
     * Unmarks and returns the task at a user-facing task number.
     *
     * @param taskNumber One-based task number entered by the user.
     * @return The task that was marked as not done.
     * @throws YukiException If the number does not identify an existing task.
     */
    public Task unmarkTask(int taskNumber) {
        Task task = getTask(taskNumber);
        if (!task.isDone()) {
            throw new YukiException("That task is already marked as not done.");
        }
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring letter case.
     *
     * @param keyword Text to search for in task descriptions.
     * @return Matching tasks in their original order.
     */
    public List<Task> findTasks(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription()
                        .toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .toList();
    }

    /**
     * Returns the original task numbers of incomplete dated tasks in a time window.
     *
     * <p>Deadlines use their due time, while events use their start time. The
     * returned task numbers are ordered by time and then by their list position.</p>
     *
     * @param fromInclusive Start of the time window, inclusive.
     * @param toInclusive End of the time window, inclusive.
     * @return One-based task numbers of upcoming tasks.
     * @throws IllegalArgumentException If the end is before the start.
     */
    public List<Integer> findUpcomingTaskNumbers(
            LocalDateTime fromInclusive, LocalDateTime toInclusive) {
        if (toInclusive.isBefore(fromInclusive)) {
            throw new IllegalArgumentException("The time window cannot end before it starts");
        }

        Comparator<Integer> byTaskTime = Comparator
                .comparing(index -> getReminderDateTime(tasks.get(index)).orElseThrow());
        return IntStream.range(0, tasks.size())
                .filter(index -> !tasks.get(index).isDone())
                .filter(index -> isWithinTimeWindow(
                        tasks.get(index), fromInclusive, toInclusive))
                .boxed()
                .sorted(byTaskTime.thenComparingInt(Integer::intValue))
                .map(index -> index + 1)
                .toList();
    }

    /**
     * Returns the task at a user-facing task number.
     *
     * @param taskNumber One-based task number entered by the user.
     * @return The selected task.
     * @throws YukiException If the number does not identify an existing task.
     */
    public Task getTask(int taskNumber) {
        return tasks.get(toListIndex(taskNumber));
    }

    /** Returns the number of tasks currently in the list. */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an unmodifiable snapshot for displaying or saving the tasks.
     *
     * @return The tasks in their current order.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /** Validates a user-facing number and converts it to a zero-based list index. */
    private int toListIndex(int taskNumber) {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new YukiException(
                    "I couldn't find a task with that number. Please enter a number between 1 and "
                            + tasks.size() + ".");
        }

        int listIndex = taskNumber - 1;
        assert listIndex >= 0 && listIndex < tasks.size()
                : "A validated task number must map to an existing list index";
        return listIndex;
    }

    /** Returns whether a task has a comparable time within the supplied window. */
    private boolean isWithinTimeWindow(
            Task task, LocalDateTime fromInclusive, LocalDateTime toInclusive) {
        return getReminderDateTime(task)
                .filter(dateTime -> !dateTime.isBefore(fromInclusive))
                .filter(dateTime -> !dateTime.isAfter(toInclusive))
                .isPresent();
    }

    /** Returns the deadline or event start time used for reminders. */
    private Optional<LocalDateTime> getReminderDateTime(Task task) {
        TaskDateTime taskDateTime;
        if (task instanceof Deadline deadline) {
            taskDateTime = deadline.getBy();
        } else if (task instanceof Event event) {
            taskDateTime = event.getFrom();
        } else {
            return Optional.empty();
        }
        return taskDateTime.toReminderDateTime();
    }
}
