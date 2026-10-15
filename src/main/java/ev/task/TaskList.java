package ev.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ev.EvException;

/**
 * The list of tasks the user is keeping.
 *
 * <p>Tasks are numbered from 1 in the order they were added, which is how the user
 * refers to them. This class owns those numbers, so it is also the place where an
 * out-of-range task number is rejected.
 */
public class TaskList {

    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this(new ArrayList<>());
    }

    /**
     * Creates a task list holding the given tasks, typically the ones just loaded from disk.
     *
     * @param tasks the tasks to start with, in the order they should be numbered.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Returns how many tasks are in the list.
     *
     * @return the number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the list has no tasks at all.
     *
     * @return true if the list is empty.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the task at the given position, counting from 0.
     *
     * <p>This is meant for walking the whole list. Use {@link #getByNumber(int)} for a
     * number that came from the user.
     *
     * @param index position of the task, from 0 to {@code size() - 1}.
     * @return the task at that position.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the tasks as a plain list, for code that only needs to read them in order.
     *
     * @return an unmodifiable view of the tasks. It follows later changes to the list,
     *     but cannot be used to make them: adding and removing go through this class.
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Adds a task to the end of the list, unless the list already holds that task.
     *
     * @param task the task to add.
     * @throws EvException if a task about the same thing is already on the list.
     */
    public void add(Task task) throws EvException {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).hasSameDetails(task)) {
                throw new EvException("Already on your list as task " + (i + 1) + ".",
                        "  " + tasks.get(i));
            }
        }
        tasks.add(task);
    }

    /**
     * Returns the task the user refers to by number.
     *
     * @param taskNumber the number shown next to the task, counting from 1.
     * @return the matching task.
     * @throws EvException if the list is empty or there is no task with that number.
     */
    public Task getByNumber(int taskNumber) throws EvException {
        requireExistingNumber(taskNumber);

        Task task = tasks.get(taskNumber - 1);
        assert task != null : "The list never holds a null task";
        return task;
    }

    /**
     * Removes the task the user refers to by number. The tasks after it move up by one.
     *
     * @param taskNumber the number shown next to the task, counting from 1.
     * @return the task that was removed.
     * @throws EvException if the list is empty or there is no task with that number.
     */
    public Task removeByNumber(int taskNumber) throws EvException {
        requireExistingNumber(taskNumber);

        int sizeBefore = tasks.size();
        Task removed = tasks.remove(taskNumber - 1);
        assert tasks.size() == sizeBefore - 1 : "Removing a task must shorten the list by exactly one";
        return removed;
    }

    /**
     * Returns the size of the list as words, with the noun in the right number.
     *
     * @return text such as {@code "1 task"} or {@code "3 tasks"}.
     */
    public String describeSize() {
        return tasks.size() + (tasks.size() == 1 ? " task" : " tasks");
    }

    /**
     * Checks that a task number the user gave actually points at a task.
     *
     * @param taskNumber the number to check, counting from 1.
     * @throws EvException with a message for the user if the number cannot be used.
     */
    private void requireExistingNumber(int taskNumber) throws EvException {
        if (tasks.isEmpty()) {
            throw new EvException("Your list is empty.");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new EvException("No task " + taskNumber + ".",
                    "Pick between 1 and " + tasks.size() + ".");
        }
    }
}
