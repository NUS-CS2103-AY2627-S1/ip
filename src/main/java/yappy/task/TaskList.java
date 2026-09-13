package yappy.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Owns Yappy's ordered collection of tasks and the operations that modify it.
 */
public final class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks in their existing order.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the number of tasks in the list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at the specified zero-based index.
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "Task index must be valid";
        return tasks.get(index);
    }

    /**
     * Appends a task to the end of the list.
     */
    public void add(Task task) {
        assert task != null : "A task to add must not be null";
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the specified zero-based index.
     */
    public Task remove(int index) {
        assert index >= 0 && index < tasks.size() : "Task index must be valid";
        return tasks.remove(index);
    }

    /**
     * Returns a new task list containing tasks whose descriptions match the keyword.
     */
    public TaskList find(String keyword) {
        List<Task> matchingTasks = tasks.stream()
                .filter(task -> task.containsKeyword(keyword))
                .toList();
        return new TaskList(matchingTasks);
    }

    /**
     * Returns incomplete deadlines from today through the specified number of days ahead.
     * Overdue tasks are deliberately omitted: they need an explicit date change rather than
     * an "upcoming" reminder.
     */
    public TaskList getUpcomingDeadlines(LocalDate today, int daysAhead) {
        assert today != null : "Today's date must not be null";
        assert daysAhead >= 0 : "Reminder period must not be negative";
        LocalDate lastReminderDate = today.plusDays(daysAhead);
        List<Task> upcomingDeadlines = tasks.stream()
                .filter(task -> task instanceof Deadline)
                .filter(task -> !task.isDone())
                .filter(task -> {
                    LocalDate dueDate = ((Deadline) task).getBy();
                    return !dueDate.isBefore(today) && !dueDate.isAfter(lastReminderDate);
                })
                .toList();
        return new TaskList(upcomingDeadlines);
    }
}
