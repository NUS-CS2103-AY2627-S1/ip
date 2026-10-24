package clara.task;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import clara.exception.ClaraException;

// NOTE: AI-assisted OOP refactoring. See CITATIONS.md [C-007].

/**
 * Represents Clara's collection of tasks and provides operations to manipulate them.
 */
public class TaskList {
    private static final int FIRST_TASK_INDEX = 1;

    private final List<Task> tasks;

    /**
     * Constructs an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Constructs a task list initialized with the given tasks.
     *
     * @param tasks the initial list of tasks
     */
    public TaskList(Task... tasks) {
        this.tasks = new ArrayList<>(List.of(tasks));
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the task list contains no tasks.
     *
     * @return true if the list is empty, false otherwise
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the underlying list of tasks.
     *
     * @return the list of tasks
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * Returns the task at the specified 1-based task index.
     *
     * @param taskIndex the one-based index of the task to retrieve
     * @return the task at the specified index
     * @throws ClaraException if the task index is out of bounds
     */
    public Task getTask(final int taskIndex) throws ClaraException {
        return tasks.get(toListIndex(taskIndex));
    }

    /**
     * Adds a task to the task list.
     *
     * @param task the task to add
     */
    public void addTask(final Task task) {
        // NOTE: AI-assisted invariant assertions. See CITATIONS.md [C-015].
        assert task != null : "Task list cannot contain null tasks";
        tasks.add(task);
    }

    /**
     * Marks the specified task as completed.
     *
     * @param taskIndex the one-based index of the task to mark
     * @return the marked task
     * @throws ClaraException if the task index is invalid or the task is already marked
     */
    public Task markTask(final int taskIndex) throws ClaraException {
        Task task = getTask(taskIndex);
        if (task.isDone()) {
            throw new ClaraException(
                    "Oops - Task " + taskIndex + " is already marked:\n" + task.getTaskName());
        }
        task.setDone(true);
        return task;
    }

    /**
     * Marks the specified task as incomplete.
     *
     * @param taskIndex the one-based index of the task to unmark
     * @return the unmarked task
     * @throws ClaraException if the task index is invalid or the task is already unmarked
     */
    public Task unmarkTask(final int taskIndex) throws ClaraException {
        Task task = getTask(taskIndex);
        if (!task.isDone()) {
            throw new ClaraException(
                    "Oops - Task " + taskIndex + " is already unmarked:\n" + task.getTaskName());
        }
        task.setDone(false);
        return task;
    }

    /**
     * Deletes the specified task from the task list.
     *
     * @param taskIndex the one-based index of the task to delete
     * @return the removed task
     * @throws ClaraException if the task index is invalid
     */
    public Task deleteTask(final int taskIndex) throws ClaraException {
        return tasks.remove(toListIndex(taskIndex));
    }

    /**
     * Converts a user-facing task index to a zero-based list index.
     *
     * @param taskIndex the one-based task index to convert
     * @return the corresponding zero-based list index
     * @throws ClaraException if the task index is out of bounds
     */
    private int toListIndex(int taskIndex) throws ClaraException {
        if (taskIndex < FIRST_TASK_INDEX || taskIndex > tasks.size()) {
            throw new ClaraException("There is no task " + taskIndex + ".");
        }
        return taskIndex - FIRST_TASK_INDEX;
    }

    // NOTE: AI-assisted task find implementation. See CITATIONS.md [C-006].

    /**
     * Finds the 0-based indices of tasks whose names contain the given argument.
     *
     * @param argument the search term
     * @return list of matching 0-based task indices
     */
    public List<Integer> findMatchingIndices(String argument) {
        Pattern pattern = Pattern.compile(Pattern.quote(argument));

        return IntStream.range(0, tasks.size()) // A-Stream usage
                .filter(i -> pattern.matcher(tasks.get(i).getTaskName()).find())
                .boxed()
                .toList();
    }
}
