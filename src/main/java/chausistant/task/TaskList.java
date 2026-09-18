package chausistant.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Owns the chatbot's mutable collection of tasks.
 *
 * <p>Callers use task-focused operations instead of working directly with an
 * {@link ArrayList}. This will let command classes receive one clear task-list
 * dependency as the application is further refactored.</p>
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates a task list containing the supplied tasks in the same order. */
    public TaskList(Task... tasks) {
        this.tasks = new ArrayList<>(List.of(tasks));
    }

    /** Creates a task list containing the supplied tasks in the same order. */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a task at the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /** Inserts a task at the specified zero-based index. */
    public void add(int index, Task task) {
        tasks.add(index, task);
    }

    /**
     * Returns whether this list already contains a task with the same user-visible details.
     *
     * @param task the task to check
     * @return whether an equivalent task is already in the list
     */
    public boolean hasTaskWithSameDetails(Task task) {
        return tasks.stream().anyMatch(task::hasSameDetailsAs);
    }

    /** Returns the task at the specified zero-based index. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Removes and returns the task at the specified zero-based index. */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /** Returns how many tasks are in the list. */
    public int size() {
        return tasks.size();
    }

    /** Returns tasks whose descriptions contain the search phrase, ignoring letter case. */
    public List<Task> findMatchingTasks(String searchPhrase) {
        String normalizedSearchPhrase = searchPhrase.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getItem().toLowerCase(Locale.ROOT)
                        .contains(normalizedSearchPhrase))
                .toList();
    }

    /**
     * Returns incomplete deadlines due from the supplied start through the supplied end, in due-time order.
     *
     * @param start the inclusive start of the reminder window
     * @param end the inclusive end of the reminder window
     * @return incomplete deadlines in the reminder window, sorted by their due time
     */
    public List<DeadlineTask> getIncompleteDeadlinesDueBetween(LocalDateTime start, LocalDateTime end) {
        assert start != null && end != null : "Reminder-window bounds must not be null.";
        assert !end.isBefore(start) : "A reminder window cannot end before it starts.";

        return tasks.stream()
                .filter(DeadlineTask.class::isInstance)
                .map(DeadlineTask.class::cast)
                .filter(deadline -> !deadline.isCompleted())
                .filter(deadline -> !deadline.getDeadline().isBefore(start)
                        && !deadline.getDeadline().isAfter(end))
                .sorted(Comparator.comparing(DeadlineTask::getDeadline))
                .toList();
    }

    /** Returns a read-only snapshot of tasks in their current order. */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }
}
