package yourhand.tasks;

import yourhand.exceptions.YourHandException;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * Stores the tasks managed during one YourHand session.
 */
public class TaskList {
    private final List<Task> tasks = new ArrayList<>();

    /**
     * Adds a task to the list.
     *
     * @param task Task to store.
     */
    public void add(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("task list must not contain null tasks");
        }
        tasks.add(task);
    }

    /** Adds a task at a zero-based position, for transactional command rollback. */
    public void addAt(int index, Task task) {
        if (task == null || index < 0 || index > tasks.size()) {
            throw new IllegalArgumentException("Invalid task insertion.");
        }
        tasks.add(index, task);
    }

    /**
     * Returns a task using the one-based number shown to the user.
     *
     * @param taskNumber One-based task number.
     * @return The requested task.
     * @throws YourHandException If the task number is outside the current list.
     */
    public Task getTask(int taskNumber) throws YourHandException {
        validateTaskNumber(taskNumber);
        return tasks.get(taskNumber - 1);
    }

    /**
     * Removes and returns a task using the one-based number shown to the user.
     *
     * @param taskNumber One-based task number.
     * @return The removed task.
     * @throws YourHandException If the task number is outside the current list.
     */
    public Task removeTask(int taskNumber) throws YourHandException {
        validateTaskNumber(taskNumber);
        return tasks.remove(taskNumber - 1);
    }

    /** Checks whether a one-based task number refers to a stored task. */
    private void validateTaskNumber(int taskNumber) throws YourHandException {
        if (tasks.isEmpty()) {
            throw new YourHandException("Brother I free how delete stuff. Add one before I can even delete.");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new YourHandException("Pick a task number from 1 to " + tasks.size() + ".");
        }
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return The current task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the list has no tasks.
     *
     * @return True if there are no tasks.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns an immutable snapshot of the current tasks.
     *
     * @return Current tasks in their display order.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Returns the number of the first task with the given description.
     *
     * @param description Description to search for.
     * @return The one-based task number, or {@code -1} when no task matches.
     */
    public int findTaskNumberByDescription(String description) {
        for (int index = 0; index < tasks.size(); index++) {
            if (tasks.get(index).getDescription().equalsIgnoreCase(description)) {
                return index + 1;
            }
        }
        return -1;
    }

    /** Returns one-based numbers of tasks whose descriptions contain the keyword. */
    public List<Integer> findTaskNumbersByDescriptionKeyword(String keyword) {
        Pattern pattern = Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        return IntStream.range(0, tasks.size())
                .filter(index -> pattern.matcher(tasks.get(index).getDescription()).find())
                .map(index -> index + 1)
                .boxed()
                .toList();
    }

    /** Returns one-based numbers of dated tasks occurring on the given date. */
    public List<Integer> findTaskNumbersForDate(LocalDate date) {
        return IntStream.range(0, tasks.size())
                .filter(index -> occursOn(tasks.get(index), date))
                .boxed()
                .sorted(Comparator.comparing(this::getScheduleStart))
                .map(index -> index + 1)
                .toList();
    }

    private boolean occursOn(Task task, LocalDate date) {
        if (task instanceof Deadline deadline) {
            return deadline.getBy().getValue().toLocalDate().equals(date);
        }
        if (task instanceof Event event) {
            LocalDate startDate = event.getFrom().getValue().toLocalDate();
            LocalDate endDate = event.getTo().getValue().toLocalDate();
            return !date.isBefore(startDate) && !date.isAfter(endDate);
        }
        return false;
    }

    private LocalDateTime getScheduleStart(int taskIndex) {
        Task task = tasks.get(taskIndex);
        if (task instanceof Deadline deadline) {
            return deadline.getBy().getValue();
        }
        if (task instanceof Event event) {
            return event.getFrom().getValue();
        }
        throw new IllegalArgumentException("Only dated tasks can be scheduled.");
    }
}
