package sandrone.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sandrone.SandroneException;

/** Stores and manages the tasks in the current chatbot session. */
public class TaskList {
    private static final int MAX_TASKS = 10;
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /** Returns the number of tasks in this list. */
    public int size() {
        return tasks.size();
    }

    /** Returns whether this list has reached its task limit. */
    public boolean isFull() {
        return size() >= MAX_TASKS;
    }

    /** Adds a task unless the task limit has been reached. */
    public void addTask(Task task) throws SandroneException {
        if (isFull()) {
            throw new SandroneException("Cannot exceed maximum number of tasks");
        }
        tasks.add(task);
    }

    /** Returns the task at the specified zero-based index. */
    public Task getTask(int index) {
        assert index >= 0 && index < tasks.size() : "Task index must refer to an existing task";
        return tasks.get(index);
    }

    /** Returns the task identified by a one-based user task number. */
    public Task getTaskByNumber(int taskNumber) throws SandroneException {
        if (taskNumber <= 0 || taskNumber > size()) {
            throw new SandroneException("Invalid task index");
        }
        return getTask(taskNumber - 1);
    }

    /** Marks the task identified by a one-based user task number as done. */
    public Task markTaskByNumber(int taskNumber) throws SandroneException {
        Task task = getTaskByNumber(taskNumber);
        task.markAsDone();
        return task;
    }

    /** Marks the task identified by a one-based user task number as not done. */
    public Task unmarkTaskByNumber(int taskNumber) throws SandroneException {
        Task task = getTaskByNumber(taskNumber);
        task.markAsNotDone();
        return task;
    }

    /** Removes and returns the task at the specified zero-based index. */
    public Task removeTask(int index) {
        return tasks.remove(index);
    }

    /** Removes and returns the task identified by a one-based user task number. */
    public Task removeTaskByNumber(int taskNumber) throws SandroneException {
        getTaskByNumber(taskNumber);
        return removeTask(taskNumber - 1);
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring case.
     */
    public List<Task> findTasks(String keyword) {
        String lowercaseKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription()
                        .toLowerCase(Locale.ROOT)
                        .contains(lowercaseKeyword))
                .toList();
    }

    /**
     * Calculates a snapshot of statistics for the tasks currently in this list.
     */
    public TaskStatistics getStatistics() {
        int completedTasks = 0;
        int todoCount = 0;
        int deadlineCount = 0;
        int eventCount = 0;
        int scheduledTodayCount = 0;
        LocalDate today = LocalDate.now();

        for (Task task : tasks) {
            if (task.isDone()) {
                completedTasks++;
            }
            if (task instanceof Todo) {
                todoCount++;
            } else if (task instanceof Deadline) {
                deadlineCount++;
            } else if (task instanceof Event) {
                eventCount++;
            }
            if (task.occursOn(today)) {
                scheduledTodayCount++;
            }
        }
        return new TaskStatistics(size(), completedTasks, todoCount, deadlineCount,
                eventCount, scheduledTodayCount, MAX_TASKS);
    }

    /** Returns an unmodifiable snapshot of the tasks for saving. */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }
}
