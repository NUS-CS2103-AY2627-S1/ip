package anby;

import java.util.ArrayList;
import java.util.stream.Collectors;

import anby.task.Task;

/**
 * Stores and manages the user's tasks.
 */
public class TaskList {
    private ArrayList<Task> tasks;

    /**
     * Creates a task list backed by the given tasks.
     *
     * @param tasks initial tasks in the list
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
        assert this.tasks != null;
    }

    /**
     * Checks whether there are no tasks in the list.
     *
     * @return true if the task list is empty
     */
    public boolean isEmpty() {
        return this.tasks.isEmpty();
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return number of tasks
     */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Returns all tasks in the list.
     *
     * @return backing list of tasks
     */
    public ArrayList<Task> getTasks() {
        return this.tasks;
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index zero-based index of the task
     * @return task at the given index
     */
    public Task get(int index) {
        return this.tasks.get(index);
    }

    /**
     * Returns the task for a one-based task number.
     *
     * @param taskNumber one-based task number as text
     * @return task with the given task number
     * @throws AnbyException if the task number is invalid
     */
    public Task getTask(String taskNumber) throws AnbyException {
        int index = parseTaskIndex(taskNumber);
        Task task = this.tasks.get(index);
        assert task != null;
        return task;
    }

    /**
     * Adds tasks to the list.
     *
     * @param tasks tasks to add
     */
    public void add(Task... tasks) {
        assert tasks != null;

        for (Task task : tasks) {
            assert task != null;
            this.tasks.add(task);
        }
    }

    /**
     * Deletes the task for a one-based task number.
     *
     * @param taskNumber one-based task number as text
     * @return deleted task
     * @throws AnbyException if the task number is invalid
     */
    public Task delete(String taskNumber) throws AnbyException {
        int index = parseTaskIndex(taskNumber);
        Task removedTask = this.tasks.remove(index);
        assert removedTask != null;
        return removedTask;
    }

    /**
     * Marks the task for a one-based task number as done.
     *
     * @param taskNumber one-based task number as text
     * @throws AnbyException if the task number is invalid or the task is already done
     */
    public void mark(String taskNumber) throws AnbyException {
        Task task = getTask(taskNumber);

        if (task.isDone()) {
            throw new AnbyException("you're actually already done with this task lol\n"
                    + "try: list to pick another task");
        }

        task.markAsDone();
    }

    /**
     * Marks the task for a one-based task number as not done.
     *
     * @param taskNumber one-based task number as text
     * @throws AnbyException if the task number is invalid or the task is not done
     */
    public void unmark(String taskNumber) throws AnbyException {
        Task task = getTask(taskNumber);

        if (!task.isDone()) {
            throw new AnbyException("bruh you haven't done this yet anyway\n"
                    + "try: mark 1 before unmarking that task");
        }

        task.unmarkAsDone();
    }

    /**
     * Returns all tasks whose names contain the given keyword.
     *
     * @param keyword keyword to search for
     * @return task list containing matching tasks
     */
    public TaskList find(String keyword) {
        assert keyword != null;

        String keywordInUpperCase = keyword.toUpperCase();
        ArrayList<Task> matchingTasks = this.tasks.stream()
                .filter(task -> task.getName().toUpperCase().contains(keywordInUpperCase))
                .collect(Collectors.toCollection(ArrayList::new));

        return new TaskList(matchingTasks);
    }

    private int parseTaskIndex(String text) throws AnbyException {
        try {
            int taskNumber = Integer.parseInt(text);

            if (taskNumber < 1 || taskNumber > this.tasks.size()) {
                throw new AnbyException("hey give me a valid task number!\n"
                        + "try: list to pick a task number");
            }

            int index = taskNumber - 1;
            assert index >= 0 && index < this.tasks.size();
            return index;
        } catch (NumberFormatException e) {
            throw new AnbyException("hey give me a valid task number!\n"
                    + "try: list to pick a task number");
        }
    }
}
