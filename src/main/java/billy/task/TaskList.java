package billy.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import billy.BillyException;

/**
 * The user's tasks, in the order they were added.
 *
 * <p>Holding the tasks behind a class rather than passing a bare list around
 * gives the list one owner, and one place for the rules about it. Chief among
 * those is what counts as a real task number: the check used to sit beside every
 * command that took one, and now lives here, where the list itself knows the
 * answer.
 *
 * <p>Task numbers are the ones the user sees, counting from 1. Callers therefore
 * never do the arithmetic to turn a number on screen into a position in the
 * list, which is exactly the sort of off-by-one this class exists to prevent.
 */
public class TaskList {

    /** The tasks themselves. Never null, and only ever changed through this class. */
    private final ArrayList<Task> tasks;

    /** Creates an empty list, as used on a first run when nothing has been saved. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a list holding tasks that have already been read from somewhere,
     * typically the save file.
     *
     * @param tasks the tasks to start with, in the order they should appear
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null
                : "A task list is never backed by null; a failed load gives an empty list instead.";
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes the task the user named.
     *
     * @param taskNumber the task's number as the user sees it, counting from 1
     * @return the task that was removed, so it can be shown in a confirmation
     * @throws BillyException if no task has that number
     */
    public Task remove(int taskNumber) throws BillyException {
        Task task = get(taskNumber);
        Task removed = tasks.remove(taskNumber - 1);
        // Both lines above turn the same task number into a position, and this is
        // the one place that conversion happens twice.
        assert removed == task : "get and remove disagree about task number " + taskNumber + ".";
        return task;
    }

    /**
     * Returns the task the user named.
     *
     * <p>An empty list is reported differently from a number that is merely out
     * of range, since "you have nothing" and "you have three" call for different
     * advice.
     *
     * @param taskNumber the task's number as the user sees it, counting from 1
     * @return the task at that number
     * @throws BillyException if no task has that number
     */
    public Task get(int taskNumber) throws BillyException {
        if (tasks.isEmpty()) {
            throw new BillyException("Your list is empty, so there's nothing to change.");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new BillyException("There's no task " + taskNumber
                    + " on your list. You have " + tasks.size() + ".");
        }
        return tasks.get(taskNumber - 1);
    }

    /**
     * Returns how many tasks are stored.
     *
     * @return the number of tasks, counting from 0 upwards
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the numbers of the outstanding tasks that describe the same thing
     * as the given task, as the user refers to them.
     *
     * <p>Finished tasks are left out: ticking something off and adding it again
     * is how a recurring chore is repeated, and warning about it would be
     * wrong. The numbers count from 1, so they can be shown to the user and
     * typed straight back in.
     *
     * @param task the task to look for matches of
     * @return the matching task numbers, in list order, empty if there are none
     */
    public List<Integer> findSameTasks(Task task) {
        return IntStream.range(0, tasks.size())
                .filter(i -> !tasks.get(i).isDone())
                .filter(i -> tasks.get(i).isSameTask(task))
                .mapToObj(i -> i + 1)
                .toList();
    }

    /**
     * Returns whether every task in the list is done.
     *
     * <p>Used by the commands that remark on finishing the last outstanding
     * task. Kept here rather than in those commands because the list is what
     * knows what it holds.
     *
     * @return whether the list holds tasks and all of them are done
     */
    public boolean isAllDone() {
        // An empty list is not "all done": there was nothing to finish, and
        // congratulating the user for it would be absurd.
        return !tasks.isEmpty() && tasks.stream().allMatch(Task::isDone);
    }

    /**
     * Returns whether the list holds no tasks at all.
     *
     * @return whether the list is empty
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the tasks in order, for reading only.
     *
     * <p>The returned list cannot be changed. Adding and removing have to go
     * through this class, so no caller can slip a task past the checks made
     * here, however convenient it might be to reach in directly.
     *
     * @return the tasks, as a view that refuses to be modified
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }
}
