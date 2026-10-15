package slotbot.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Manages SlotBot's collection of tasks.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates a task list containing the given tasks.
     *
     * @param tasks Initial tasks in the list.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Initial task list must not be null";
        assert !new ArrayList<>(tasks).contains(null) : "Initial task list must not contain null tasks";

        this.tasks = new ArrayList<>(tasks);
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
     * Deletes and returns the task at the given index.
     *
     * @param index Zero-based index of the task to delete.
     * @return Deleted task.
     */
    public Task delete(int index) {
        assert index >= 0 && index < tasks.size() : "Task index must be within the list";

        return tasks.remove(index);
    }

    /**
     * Returns the task at the given index.
     *
     * @param index Zero-based index of the task to return.
     * @return Task at the given index.
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "Task index must be within the list";

        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an unmodifiable snapshot of the tasks.
     *
     * @return Tasks in their current order.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Returns the indexes of tasks whose descriptions contain the given keyword.
     *
     * @param keyword Case-sensitive text to search for.
     * @return Matching zero-based indexes in their original order.
     */
    public List<Integer> findMatchingTaskIndexes(String keyword) {
        assert keyword != null : "Search keyword must not be null";

        return IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).getDescription().contains(keyword))
                .boxed()
                .toList();
    }

    /**
     * Returns unfinished deadlines due within the requested reminder window.
     *
     * @param startDate First date included in the reminder window.
     * @param daysAhead Number of days after the start date to include.
     * @return Unfinished deadlines sorted by due date.
     */
    public List<Deadline> findUpcomingDeadlines(LocalDate startDate, int daysAhead) {
        assert startDate != null : "Reminder start date must not be null";
        assert daysAhead >= 0 : "Reminder window must not be negative";

        LocalDate endDate = startDate.plusDays(daysAhead);
        return tasks.stream()
                .filter(Deadline.class::isInstance)
                .map(Deadline.class::cast)
                .filter(deadline -> !deadline.isDone())
                .filter(deadline -> !deadline.getDate().isBefore(startDate)
                        && !deadline.getDate().isAfter(endDate))
                .sorted(Comparator.comparing(Deadline::getDate))
                .toList();
    }
}
