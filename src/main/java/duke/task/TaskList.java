package duke.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores the chatbot's tasks and provides operations for changing the list.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list using tasks loaded from storage.
     *
     * @param tasks Tasks to use as the initial list content.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Initial task list should not be null";
        assert !tasks.contains(null) : "Initial task list should not contain null tasks";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        assert task != null : "Task to add should not be null";
        tasks.add(task);
    }

    /**
     * Deletes and returns the task at the given zero-based index.
     *
     * @param taskIndex Zero-based index of the task to delete.
     * @return Deleted task.
     */
    public Task delete(int taskIndex) {
        assert taskIndex >= 0 && taskIndex < tasks.size() : "Task index should be within the task list";
        return tasks.remove(taskIndex);
    }

    /**
     * Marks the task at the given zero-based index as done.
     *
     * @param taskIndex Zero-based index of the task to mark.
     * @return Marked task.
     */
    public Task mark(int taskIndex) {
        assert taskIndex >= 0 && taskIndex < tasks.size() : "Task index should be within the task list";
        Task task = tasks.get(taskIndex);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task at the given zero-based index as not done.
     *
     * @param taskIndex Zero-based index of the task to unmark.
     * @return Unmarked task.
     */
    public Task unmark(int taskIndex) {
        assert taskIndex >= 0 && taskIndex < tasks.size() : "Task index should be within the task list";
        Task task = tasks.get(taskIndex);
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns the task at the given zero-based index.
     */
    public Task get(int taskIndex) {
        assert taskIndex >= 0 && taskIndex < tasks.size() : "Task index should be within the task list";
        return tasks.get(taskIndex);
    }

    /**
     * Returns tasks with descriptions containing the given keyword.
     */
    public TaskList find(String keyword) {
        assert keyword != null && !keyword.isEmpty() : "Search keyword should not be empty";
        List<Task> matchingTasks = tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .toList();

        return new TaskList(matchingTasks);
    }

    /**
     * Returns deadlines due and events taking place on the given date.
     * Multi-day events are included on both boundary dates and every date between them.
     *
     * @param date Date whose scheduled tasks should be returned.
     * @return Scheduled tasks in their original insertion order.
     */
    public TaskList getScheduledTasks(LocalDate date) {
        assert date != null : "Schedule date should not be null";
        List<Task> scheduledTasks = tasks.stream()
                .filter(task -> isScheduledOn(task, date))
                .toList();

        return new TaskList(scheduledTasks);
    }

    /**
     * Returns whether a task has a deadline or event period covering the given date.
     */
    private boolean isScheduledOn(Task task, LocalDate date) {
        if (task instanceof Deadline deadline) {
            return deadline.getBy().equals(date);
        }
        if (task instanceof Event event) {
            return !date.isBefore(event.getFrom()) && !date.isAfter(event.getTo());
        }
        return false;
    }

    /**
     * Returns the number of tasks in the list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an immutable snapshot of the tasks for storage code.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }
}
