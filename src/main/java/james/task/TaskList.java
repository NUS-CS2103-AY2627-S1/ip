package james.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Encapsulates the in-memory list of tasks and provides operations to
 * add, delete, query, and filter tasks.
 */
public class TaskList {
    private static final String TASK_LIST_ENTRY_FORMAT = "%d.%s";
    private static final String LINE_BREAK = "\n";

    private final ArrayList<Task> tasks;

    /**
     * Constructs a TaskList initialized with an existing list of tasks.
     *
     * @param tasks The initial list of tasks, or {@code null} for an empty list.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks == null ? new ArrayList<>() : tasks;
    }

    /**
     * Constructs an empty TaskList.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Returns a deep snapshot whose tasks can be changed independently.
     *
     * @return Independent task list snapshot.
     */
    public TaskList copy() {
        TaskList copy = new TaskList();
        for (Task task : tasks) {
            copy.addTask(task.copy());
        }
        return copy;
    }

    /**
     * Retrieves the task at the specified zero-based index.
     *
     * @param index Zero-based index of the task.
     * @return The task at the specified index.
     */
    public Task getTask(int index) {
        return tasks.get(index);
    }

    /**
     * Adds a new task to the task list.
     *
     * @param task The task to add.
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Deletes and returns the task at the specified zero-based index.
     *
     * @param index Zero-based index of the task to delete.
     * @return The removed task.
     * @throws IndexOutOfBoundsException If index is out of bounds.
     */
    public Task deleteTask(int index) throws IndexOutOfBoundsException {
        return tasks.remove(index);
    }

    /**
     * Deletes a specific task from the list.
     *
     * @param task The task to remove.
     */
    public void deleteTask(Task task) {
        tasks.remove(task);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return The number of tasks.
     */
    public int getSize() {
        return tasks.size();
    }

    /**
     * Returns the underlying list of tasks.
     *
     * @return The list of tasks.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Checks if a task occurs on a specific date.
     * Deadlines match if their deadline date is the specified date.
     * Events match if the specified date falls within their start and end dates (inclusive).
     *
     * @param task The task to check.
     * @param date The date to check against.
     * @return True if the task occurs on the specified date, false otherwise.
     */
    public static boolean isTaskMatchingDate(Task task, LocalDate date) {
        if (task instanceof Event eventTask) {
            LocalDate from = eventTask.getFrom();
            LocalDate to = eventTask.getTo();
            return !date.isBefore(from) && !date.isAfter(to);
        } else if (task instanceof Deadline deadlineTask) {
            LocalDate by = deadlineTask.getBy();
            return date.isEqual(by);
        }
        return false;
    }

    /**
     * Returns a list of tasks that occur on the specified date.
     *
     * @param date The date to filter tasks by.
     * @return An ArrayList containing matching tasks.
     */
    public ArrayList<Task> getTasksOnDate(LocalDate date) {
        ArrayList<Task> tasksWithDate = new ArrayList<>();
        for (Task task : tasks) {
            if (isTaskMatchingDate(task, date)) {
                tasksWithDate.add(task);
            }
        }
        return tasksWithDate;
    }

    /**
     * Returns tasks whose descriptions contain the specified keyword.
     * Matching ignores letter case and preserves insertion order.
     *
     * @param keyword Search keyword.
     * @return An ArrayList containing matching tasks.
     */
    public ArrayList<Task> findTasks(String keyword) {
        ArrayList<Task> matchingTasks = new ArrayList<>();
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }

    /**
     * Returns a numbered string representation of all tasks in the list.
     *
     * @return Formatted multiline string listing all tasks.
     */
    @Override
    public String toString() {
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            message.append(TASK_LIST_ENTRY_FORMAT.formatted(i + 1, tasks.get(i)));

            message.append(LINE_BREAK);
        }
        return message.toString();
    }
}
