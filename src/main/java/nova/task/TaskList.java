package nova.task;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import nova.exception.NovaException;
import nova.storage.Storage;

/**
 * Owns Nova's tasks and manages operations that inspect or change them.
 */
public class TaskList {
    /**
     * Associates a task with its one-based position in the task list.
     */
    public static class NumberedTask {
        private final int taskNumber;
        private final Task task;

        /**
         * Creates a numbered task at the given one-based position.
         *
         * @param taskNumber one-based position in the task list
         * @param task task at that position
         */
        NumberedTask(int taskNumber, Task task) {
            this.taskNumber = taskNumber;
            this.task = task;
        }

        /**
         * Returns this task's one-based position in the task list.
         *
         * @return one-based task number
         */
        public int getTaskNumber() {
            return taskNumber;
        }

        /**
         * Returns the numbered task.
         *
         * @return task associated with the task number
         */
        public Task getTask() {
            return task;
        }
    }

    private final List<Task> tasks;
    private final Storage storage;

    /**
     * Creates a task list containing tasks loaded from storage.
     *
     * @param tasks initially loaded tasks.
     * @param storage storage used to persist task-list changes.
     */
    public TaskList(List<Task> tasks, Storage storage) {
        this.tasks = new ArrayList<>(tasks);
        this.storage = storage;
    }

    /**
     * Returns an unmodifiable snapshot of all tasks.
     *
     * @return current tasks.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Adds and saves a task, restoring the list if saving fails.
     *
     * @param task task to add.
     * @throws IOException if the updated list cannot be saved.
     */
    public void add(Task task) throws IOException {
        tasks.add(task);
        try {
            storage.save(tasks);
        } catch (IOException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
    }

    /**
     * Deletes and saves a numbered task, restoring the list if saving fails.
     *
     * @param taskNumber one-based task number.
     * @return deleted task.
     * @throws NovaException if the task number does not exist.
     * @throws IOException if the updated list cannot be saved.
     */
    public Task delete(int taskNumber) throws NovaException, IOException {
        int taskIndex = getTaskIndex(taskNumber);
        Task removedTask = tasks.remove(taskIndex);
        try {
            storage.save(tasks);
        } catch (IOException exception) {
            tasks.add(taskIndex, removedTask);
            throw exception;
        }
        return removedTask;
    }

    /**
     * Marks and saves a task as completed, restoring it if saving fails.
     *
     * @param taskNumber one-based task number.
     * @return updated task.
     * @throws NovaException if the task number does not exist.
     * @throws IOException if the updated list cannot be saved.
     */
    public Task mark(int taskNumber) throws NovaException, IOException {
        return updateCompletionState(taskNumber, true);
    }

    /**
     * Marks and saves a task as not completed, restoring it if saving fails.
     *
     * @param taskNumber one-based task number.
     * @return updated task.
     * @throws NovaException if the task number does not exist.
     * @throws IOException if the updated list cannot be saved.
     */
    public Task unmark(int taskNumber) throws NovaException, IOException {
        return updateCompletionState(taskNumber, false);
    }

    /**
     * Changes and saves a task's completion state, restoring it if saving fails.
     *
     * @param taskNumber one-based task number
     * @param shouldMark whether the task should be marked as done
     * @return updated task
     * @throws NovaException if the task number does not exist
     * @throws IOException if the updated list cannot be saved
     */
    private Task updateCompletionState(int taskNumber, boolean shouldMark) throws NovaException, IOException {
        Task task = tasks.get(getTaskIndex(taskNumber));
        boolean wasDone = task.isDone();
        updateCompletionState(task, shouldMark);
        try {
            storage.save(tasks);
        } catch (IOException exception) {
            updateCompletionState(task, wasDone);
            throw exception;
        }
        return task;
    }

    /**
     * Sets a task's completion state.
     *
     * @param task task to update
     * @param shouldMark whether the task should be marked as completed
     */
    private void updateCompletionState(Task task, boolean shouldMark) {
        if (shouldMark) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
    }

    /**
     * Returns numbered deadlines that fall on the given date.
     *
     * @param date date to search.
     * @return matching deadlines with their task-list numbers.
     */
    public List<NumberedTask> getDeadlinesOn(LocalDate date) {
        List<NumberedTask> matchingTasks = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task instanceof Deadline deadline && deadline.isDueOn(date)) {
                matchingTasks.add(new NumberedTask(i + 1, task));
            }
        }
        return matchingTasks;
    }

    /**
     * Returns tasks whose descriptions contain the given keyword.
     *
     * @param keyword keyword to search for.
     * @return matching tasks with their task-list numbers in task-list order.
     */
    public List<NumberedTask> findTasks(String keyword) {
        return IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).description.contains(keyword))
                .mapToObj(index -> new NumberedTask(index + 1, tasks.get(index)))
                .toList();
    }

    /**
     * Returns the zero-based index for a valid task number.
     *
     * @param taskNumber one-based task number
     * @return zero-based index of the task
     * @throws NovaException if the task number does not exist
     */
    private int getTaskIndex(int taskNumber) throws NovaException {
        int taskIndex = taskNumber - 1;
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new NovaException("Task " + taskNumber + " does not exist in the list.");
        }
        return taskIndex;
    }

}
