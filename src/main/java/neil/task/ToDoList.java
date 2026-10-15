package neil.task;

import java.util.ArrayList;
import java.util.List;

import neil.exception.NeilException;

/**
 * Stores and manages the ordered tasks in Neil.
 */
public class ToDoList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public ToDoList() {
        tasks = new ArrayList<>();
    }

    @Override
    public String toString() {
        String output = "";

        int count = 1;
        for (Task task : tasks) {
            output += String.format("%s.%s\n", count++, task);
        }

        return output;
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task task to add.
     */
    public void add(Task task) {
        assert task != null : "A task list must not contain null tasks";
        tasks.add(task);
    }

    /**
     * Removes the task with the specified one-based number.
     *
     * @param taskNumber one-based number of the task to remove.
     * @return removed task.
     * @throws NeilException if no task has the specified number.
     */
    public Task remove(int taskNumber) throws NeilException {
        if (!taskExists(taskNumber)) {
            throw new NeilException("The task " + taskNumber + " does not exist");
        }
        return tasks.remove(taskNumber - 1);
    }

    private boolean taskExists(int taskNumber) {
        int taskIndex = taskNumber - 1;
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            return false;
        }
        return tasks.get(taskIndex) != null;
    }

    /**
     * Marks the task with the specified one-based number as completed.
     *
     * @param taskNumber one-based number of the task to mark.
     * @return completed task.
     * @throws NeilException if no task has the specified number.
     */
    public Task markTaskAsDone(int taskNumber) throws NeilException {
        if (!taskExists(taskNumber)) {
            throw new NeilException("The task " + taskNumber + " does not exist");
        }
        tasks.get(taskNumber - 1).markAsDone();
        return tasks.get(taskNumber - 1);
    }

    /**
     * Marks the task with the specified one-based number as incomplete.
     *
     * @param taskNumber one-based number of the task to unmark.
     * @return incomplete task.
     * @throws NeilException if no task has the specified number.
     */
    public Task unmarkTask(int taskNumber) throws NeilException {
        if (!taskExists(taskNumber)) {
            throw new NeilException("The task " + taskNumber + " does not exist");
        }
        tasks.get(taskNumber - 1).unmark();
        return tasks.get(taskNumber - 1);
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the tasks whose descriptions contain the specified keyword.
     *
     * @param keyword keyword to find in task descriptions.
     * @return matching tasks in their original order.
     */
    public List<Task> findTasks(String keyword) {
        return tasks.stream()
                .filter(task -> task.hasKeyword(keyword))
                .toList();
    }

    /**
     * Returns an unmodifiable snapshot of the tasks in this list.
     *
     * @return tasks in their original order.
     */
    public List<Task> getTasks() {
        return List.copyOf(this.tasks);
    }
}
