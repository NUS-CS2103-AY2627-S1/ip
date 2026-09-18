package alpha.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Stores and updates the tasks entered by the user. */
public class TaskList {
    private static final int FIRST_TASK_NUMBER = 1;
    private final ArrayList<Task> tasks = new ArrayList<>();

    /** Creates an empty task list. */
    public TaskList() {
    }

    /** Creates a task list containing a copy of the supplied tasks. */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "A task list must be initialized from a non-null collection";
        this.tasks.addAll(tasks);
    }

    /** Adds a plain task as a todo and returns the added task. */
    public Task addTask(String description) {
        return this.addTask(new Todo(description));
    }

    /** Adds any task subtype and returns the added task. */
    public Task addTask(Task task) {
        assert task != null : "Only non-null tasks can be added";
        this.tasks.add(task);
        return task;
    }

    /** Returns a numbered display of all tasks in this list. */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.tasks.size(); i++) {
            builder.append(String.format("%d: %s%n", i + TaskList.FIRST_TASK_NUMBER, this.tasks.get(i)));
        }

        return builder.toString();
    }

    /** Returns the task at a one-based list number, or {@code null} if it is invalid. */
    public Task getTask(int number) {
        if (!this.isValidTaskNumber(number)) {
            return null;
        }
        return this.tasks.get(number - TaskList.FIRST_TASK_NUMBER);
    }

    /** Removes and returns the task at a one-based list number, or {@code null} if invalid. */
    public Task deleteTask(int number) {
        if (!this.isValidTaskNumber(number)) {
            return null;
        }
        Task removedTask = this.tasks.remove(number - TaskList.FIRST_TASK_NUMBER);
        return removedTask;
    }

    /** Returns the number of tasks currently in the list. */
    public int size() {
        return this.tasks.size();
    }

    /** Returns a read-only view of the tasks for persistence and display. */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(this.tasks);
    }

    /** Returns tasks whose descriptions contain the keyword, ignoring letter case. */
    public List<Task> findTasks(String keyword) {
        assert keyword != null : "A search keyword must not be null";
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        ArrayList<Task> matchingTasks = new ArrayList<>();
        for (Task task : this.tasks) {
            if (task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword)) {
                matchingTasks.add(task);
            }
        }
        return Collections.unmodifiableList(matchingTasks);
    }

    /** Marks the task at the given one-based list number as done. */
    public void markDone(int number) {
        Task task = this.getTask(number);
        if (task != null) {
            task.markDone();
        }
    }

    /** Marks the task at the given one-based list number as not done. */
    public void markUndone(int number) {
        Task task = this.getTask(number);
        if (task != null) {
            task.markUndone();
        }
    }

    /** Sorts dated tasks chronologically, leaving undated tasks at the end. */
    public void sortByDateTime() {
        this.tasks.sort(Comparator.comparing(Task::getSortDateTime,
                Comparator.nullsLast(Comparator.naturalOrder())));
    }

    /** Returns whether a one-based task number identifies an existing task. */
    private boolean isValidTaskNumber(int number) {
        return number >= TaskList.FIRST_TASK_NUMBER && number <= this.tasks.size();
    }
}
