package alfred.task;

import java.util.ArrayList;
import java.util.List;

import alfred.AlfredException;

/**
 * Keeps the user's tasks in the order they were added. The operations that
 * take an index check it, since it comes from a number the user typed;
 * {@link #get(int)} is the exception, being how the list is walked for
 * display.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates a list holding the tasks named, or an empty list if none are.
     *
     * @param tasks the tasks to start with, in the order they are to be kept.
     */
    public TaskList(Task... tasks) {
        this.tasks = new ArrayList<>(List.of(tasks));
    }

    /**
     * Creates a list holding a copy of the given tasks, in the order given.
     *
     * @param tasks the tasks to start with, in the order they are to be kept.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns how many tasks are stored.
     *
     * @return the number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Says whether there are no tasks at all.
     *
     * @return true if nothing is stored.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the task at one position, counting from 0.
     *
     * @param index which task, from 0 up to one less than {@link #size()}.
     * @return the task stored there.
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "no task at index " + index;

        return tasks.get(index);
    }

    /**
     * Adds a task to the end of the list, unless the same task is already on
     * it. Only additions are checked; the tasks a list is created with are
     * taken as they are.
     *
     * @param task the task to store.
     * @throws AlfredException if the list already holds the same task, as
     *         {@link Task#isSameTask(Task)} judges it.
     */
    public void add(Task task) throws AlfredException {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).isSameTask(task)) {
                throw new AlfredException("You already have that task, sir, as number " + (i + 1) + ".");
            }
        }
        tasks.add(task);
    }

    /**
     * Removes the task at one position, closing the gap so that the tasks after
     * it move up a number.
     *
     * @param index which task, counting from 0.
     * @return the task that was removed, so it can be shown to the user.
     * @throws AlfredException if no task is stored at that index.
     */
    public Task delete(int index) throws AlfredException {
        checkIndex(index);
        return tasks.remove(index);
    }

    /**
     * Marks the task at one position as done.
     *
     * @param index which task, counting from 0.
     * @return the task, now carrying its new mark.
     * @throws AlfredException if no task is stored at that index.
     */
    public Task markDone(int index) throws AlfredException {
        checkIndex(index);
        Task task = tasks.get(index);
        task.markDone();
        return task;
    }

    /**
     * Marks the task at one position as not done after all.
     *
     * @param index which task, counting from 0.
     * @return the task, now carrying its new mark.
     * @throws AlfredException if no task is stored at that index.
     */
    public Task unmarkDone(int index) throws AlfredException {
        checkIndex(index);
        Task task = tasks.get(index);
        task.unmarkDone();
        return task;
    }

    /**
     * Sets how much the task at one position matters, {@link Priority#NONE}
     * taking its priority off again.
     *
     * @param index which task, counting from 0.
     * @param priority the level to give it.
     * @return the task, now carrying its new priority.
     * @throws AlfredException if no task is stored at that index.
     */
    public Task setPriority(int index, Priority priority) throws AlfredException {
        checkIndex(index);
        Task task = tasks.get(index);
        task.setPriority(priority);
        return task;
    }

    /**
     * Refuses an index that names no stored task.
     *
     * @param index the index to check, counting from 0.
     * @throws AlfredException if it falls outside the stored tasks.
     */
    private void checkIndex(int index) throws AlfredException {
        if (index < 0 || index >= tasks.size()) {
            throw new AlfredException("There is no such task, sir.");
        }
    }

    /**
     * Returns a copy of the tasks as a plain list, for saving.
     *
     * @return the tasks, in the order they are stored.
     */
    public List<Task> toList() {
        return List.copyOf(tasks);
    }
}
