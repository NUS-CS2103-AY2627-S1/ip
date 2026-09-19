package mrchatbot.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

import mrchatbot.exception.MrChatbotException;

/**
 * Contains the user's tasks and provides operations on the task list.
 */
public class TaskList {
    private static final String TASK_NOT_FOUND_MESSAGE = "This task doesn't exist...";

    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the given tasks.
     */
    public TaskList(Task... tasks) {
        this.tasks = new ArrayList<>();
        for (Task task : tasks) {
            this.tasks.add(task);
        }
    }

    /**
     * Creates a task list using tasks loaded from storage.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Returns the number of tasks in the list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns true if the task list has reached the given limit.
     */
    public boolean isFull(int maxTasks) {
        return tasks.size() >= maxTasks;
    }

    /**
     * Adds a new task to the list.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Marks the task at the given one-based task number as done.
     */
    public Task mark(int taskNumber) throws MrChatbotException {
        Task task = getTask(taskNumber);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task at the given one-based task number as not done.
     */
    public Task unmark(int taskNumber) throws MrChatbotException {
        Task task = getTask(taskNumber);
        task.markAsNotDone();
        return task;
    }

    /**
     * Deletes and returns the task at the given one-based task number.
     */
    public Task delete(int taskNumber) throws MrChatbotException {
        validateTaskNumber(taskNumber);
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Returns the task at the given zero-based index for display.
     */
    public Task get(int index) {
        // Display loops generate zero-based indices bounded by size(), rather than user task numbers.
        assert index >= 0 && index < tasks.size() : "Display index must refer to an existing task";
        return tasks.get(index);
    }

    /**
     * Returns the task list in the format used by Storage.
     */
    public ArrayList<Task> asArrayList() {
        return tasks;
    }

    /**
     * Returns tasks whose descriptions contain the given keyword, ignoring case.
     */
    public ArrayList<Task> find(String keyword) {
        String lowerCaseKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(lowerCaseKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Refreshes every recurring task and returns whether any reset changed the list.
     */
    public boolean refreshRecurringTasks(LocalDate today) {
        boolean hasChanges = false;
        for (Task task : tasks) {
            if (task instanceof RecurringTask) {
                boolean hasReset = ((RecurringTask) task).refresh(today);
                hasChanges = hasChanges || hasReset;
            }
        }
        return hasChanges;
    }

    /**
     * Returns the task at the given one-based task number.
     */
    private Task getTask(int taskNumber) throws MrChatbotException {
        validateTaskNumber(taskNumber);
        return tasks.get(taskNumber - 1);
    }

    /**
     * Checks that a one-based task number refers to an existing task.
     */
    private void validateTaskNumber(int taskNumber) throws MrChatbotException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new MrChatbotException(TASK_NOT_FOUND_MESSAGE);
        }
    }
}
