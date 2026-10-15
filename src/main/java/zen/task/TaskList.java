package zen.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import zen.ZenException;

/** Stores and manages the application's ordered tasks. */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Adds a task in priority order, preserving insertion order for equal priorities.
     *
     * @param task the task to add.
     */
    public void addTask(Task task) {
        assert task != null : "A task list must not contain null tasks.";
        int insertionIndex = tasks.size();
        for (int i = 0; i < tasks.size(); i++) {
            if (task.getPriority().compareTo(tasks.get(i).getPriority()) < 0) {
                insertionIndex = i;
                break;
            }
        }
        this.tasks.add(insertionIndex, task);
    }

    /**
     * Deletes a task identified by its one-based task number.
     *
     * @param taskNum the one-based task number.
     * @return the deleted task.
     * @throws ZenException if no task has the supplied number.
     */
    public Task deleteTask(int taskNum) throws ZenException {
        validateTaskNumber(taskNum);
        return this.tasks.remove(taskNum - 1);
    }

    /**
     * Checks whether a one-based task number identifies an existing task.
     *
     * @param taskNum task number to check
     * @return true if the number is within this list's bounds
     */
    private boolean isValidTaskNumber(int taskNum) {
        return taskNum > 0 && taskNum <= tasks.size();
    }

    /**
     * Validates whether the task number entered by the user is valid.
     * @param taskNum task number to check
     * @throws ZenException if task number is invalid or task list is empty
     */
    private void validateTaskNumber(int taskNum) throws ZenException {
        if (this.isEmpty()) {
            throw new ZenException("The task list is empty. Add a task first.");
        }

        if (isValidTaskNumber(taskNum)) {
            return;
        }

        throw new ZenException("Choose a task number from 1 to " + this.size() + ".");
    }

    /**
     * Marks a task identified by its one-based task number as complete.
     *
     * @param taskNum the one-based task number.
     * @return the completed task.
     * @throws ZenException if no task has the supplied number.
     */
    public Task markTask(int taskNum) throws ZenException {
        validateTaskNumber(taskNum);
        assert isValidTaskNumber(taskNum) : "Validated task number must be in range";
        Task task = this.tasks.get(taskNum - 1);
        task.markAsDone();
        return task;
    }

    /**
     * Marks a task identified by its one-based task number as incomplete.
     *
     * @param taskNum the one-based task number.
     * @return the incomplete task.
     * @throws ZenException if no task has the supplied number.
     */
    public Task unmarkTask(int taskNum) throws ZenException {
        validateTaskNumber(taskNum);
        assert isValidTaskNumber(taskNum) : "Validated task number must be in range";
        Task task = this.tasks.get(taskNum - 1);
        task.markAsNotDone();
        return task;
    }

    /** Returns the number of tasks in this list. */
    public int size() {
        return this.tasks.size();
    }

    /** Returns whether this list has no tasks. */
    public boolean isEmpty() {
        return this.tasks.isEmpty();
    }

    /**
     * Returns a new task list containing only the tasks that have the keyword
     * present in the description, preserving their original order.
     *
     * @param keyword the case-sensitive description keyword to filter tasks by
     * @return a task list containing only tasks whose descriptions contain the keyword
     */
    public TaskList findTasksByDescription(String keyword) {
        TaskList matchingTasks = new TaskList();

        for (Task task : tasks) {
            if (task.containsKeyword(keyword)) {
                matchingTasks.addTask(task);
            }
        }

        return matchingTasks;
    }

    /**
     * Returns a new task list containing only the tasks that occur on the
     * given date, preserving their original order.
     *
     * @param date the date to filter tasks by
     * @return a task list containing only tasks that occur on the specified date
     */
    public TaskList getAllTasksOnDate(LocalDate date) {
        TaskList tasksOnDate = new TaskList();

        for (Task task : tasks) {
            if (task.occursOn(date)) {
                tasksOnDate.addTask(task);
            }
        }

        return tasksOnDate;
    }

    /**
     * Returns all tasks as newline-separated records for persistent storage.
     *
     * @return pipe-delimited task records, one per line
     */
    public String toStorageString() {
        return tasks.stream()
                .map(Task::toStorageString)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < this.tasks.size(); i++) {
            output.append(String.format("%d.%s", i + 1, tasks.get(i)));

            // Add a new line after each item except the last.
            if (i < this.tasks.size() - 1) {
                output.append("\n");
            }
        }

        return output.toString();
    }
}
