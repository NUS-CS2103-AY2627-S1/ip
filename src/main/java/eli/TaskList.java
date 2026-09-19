package eli;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

/** Represents the collection of tasks managed by the application. */
public class TaskList extends ArrayList<Task> {
    private static final long serialVersionUID = 1L;
    private static final Comparator<Task> TASK_SORT_ORDER = Comparator
            .comparingInt((Task task) -> task.getTaskType().getSortOrder())
            .thenComparing(task -> task.getSortDate().isEmpty())
            .thenComparing(task -> task.getSortDate().orElse(LocalDateTime.MAX))
            .thenComparing(Task::getDescription, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(Task::getDescription);

    /** Adds a task to this list. */
    public void addTask(Task task) {
        assert task != null : "Task list must not contain null tasks";
        add(task);
    }

    /** Returns the task at a one-based user-facing number. */
    public Task getTask(int taskNumber) {
        assert hasTaskNumber(taskNumber) : "Task number must refer to an existing task";
        return get(taskNumber - 1);
    }

    /** Removes and returns the task at a one-based user-facing number. */
    public Task removeTask(int taskNumber) {
        assert hasTaskNumber(taskNumber) : "Task number must refer to an existing task";
        return remove(taskNumber - 1);
    }

    /** Returns whether a one-based task number is valid. */
    public boolean hasTaskNumber(int taskNumber) {
        return taskNumber >= 1 && taskNumber <= size();
    }

    /** Returns whether the list already contains a task with the same details. */
    public boolean containsTaskWithSameDetails(Task candidate) {
        assert candidate != null : "Candidate task must not be null";
        return stream().anyMatch(task -> task.hasSameDetails(candidate));
    }

    /** Sorts tasks by type, supported date, and description. */
    public void sortTasks() {
        sort(TASK_SORT_ORDER);
    }
}
