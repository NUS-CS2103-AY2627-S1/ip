package zeus.task;

import java.util.ArrayList;
import java.util.List;

import zeus.exception.ZeusException;

/** Owns the in-memory task collection and its list operations. */
public class TaskList {
    /** Tasks stored in their display order. */
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks in order.
     * @param initialTasks tasks with which to initialize the list
     */
    public TaskList(List<Task> initialTasks) {
        tasks = new ArrayList<>(initialTasks);
    }

    /**
     * Adds a task to the end of the list.
     * @param task task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Deletes and returns a numbered task.
     * @param taskNumber one-based task number
     * @return removed task
     * @throws ZeusException if the task number is outside the list
     */
    public Task delete(int taskNumber) throws ZeusException {
        validateTaskNumber(taskNumber);
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Marks and returns a numbered task.
     * @param taskNumber one-based task number
     * @return updated task
     * @throws ZeusException if the task number is outside the list
     */
    public Task mark(int taskNumber) throws ZeusException {
        Task task = getTask(taskNumber);
        task.markAsDone();
        return task;
    }

    /**
     * Unmarks and returns a numbered task.
     * @param taskNumber one-based task number
     * @return updated task
     * @throws ZeusException if the task number is outside the list
     */
    public Task unmark(int taskNumber) throws ZeusException {
        Task task = getTask(taskNumber);
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns tasks whose descriptions contain the specified keyword.
     *
     * @param keyword Keyword to find in task descriptions.
     * @return Matching tasks in their original order.
     */
    public List<Task> find(String keyword) {
        List<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.description.contains(keyword)) {
                matchingTasks.add(task);
            }
        }
        return List.copyOf(matchingTasks);
    }

    /*
     * Returns a read-only snapshot of the tasks.
     * @return tasks in their current order
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Returns the number of stored tasks.
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a numbered task after validating the number.
     * @param taskNumber one-based task number
     * @return selected task
     * @throws ZeusException if the number is outside the list
     */
    private Task getTask(int taskNumber) throws ZeusException {
        validateTaskNumber(taskNumber);
        return tasks.get(taskNumber - 1);
    }

    /**
     * Validates that a one-based task number identifies a stored task.
     * @param taskNumber number to validate
     * @throws ZeusException if the list is empty or the number is outside it
     */
    private void validateTaskNumber(int taskNumber) throws ZeusException {
        if (tasks.isEmpty()) {
            throw new ZeusException("Your task list is empty.");
        } else if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new ZeusException("There is no task number " + taskNumber
                    + ". Choose a number from 1 to " + tasks.size() + ".");
        }
        assert taskNumber >= 1 && taskNumber <= tasks.size()
                : "A validated task number must identify a stored task.";
    }
}
