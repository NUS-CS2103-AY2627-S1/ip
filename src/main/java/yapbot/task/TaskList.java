package yapbot.task;

import java.util.Arrays;

import yapbot.exception.YapBotException;
import yapbot.storage.Storage;

/**
 * Holds the list of tasks and the operations that mutate it: adding,
 * deleting, marking/unmarking as done, and validating a user-supplied
 * task number against the current list.
 */
public class TaskList {

    /**
     * The maximum number of tasks a {@code TaskList} can hold. The single
     * source of truth for that limit: callers that build the array they
     * later pass to {@link #TaskList(Task[], int)} (e.g. {@link Storage#load})
     * should size it against this constant rather than a separate literal.
     */
    public static final int MAX_CAPACITY = 100;

    private final Task[] tasks;
    private int size;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new Task[MAX_CAPACITY];
        this.size = 0;
    }

    /**
     * Creates a task list pre-populated from an already-loaded array, e.g.
     * the array returned by {@link Storage#load(Task[])}.
     *
     * @param loadedTasks the source array to copy from.
     * @param loadedCount the number of valid tasks at the start of {@code loadedTasks}.
     */
    public TaskList(Task[] loadedTasks, int loadedCount) {
        assert loadedCount >= 0 && loadedCount <= MAX_CAPACITY
                : "Storage.load() must never report a count outside the array it was given";
        this.tasks = new Task[MAX_CAPACITY];
        System.arraycopy(loadedTasks, 0, this.tasks, 0, loadedCount);
        this.size = loadedCount;
    }

    /**
     * Returns the number of tasks currently in the list.
     *
     * @return the task count.
     */
    public int size() {
        return size;
    }

    /**
     * Returns whether the list is at its maximum capacity.
     *
     * @return {@code true} if no more tasks can be added.
     */
    public boolean isFull() {
        return size >= MAX_CAPACITY;
    }

    /**
     * Returns the task at the given index.
     *
     * @param index a valid, zero-based index (see {@link #validateIndex}).
     * @return the task at that index.
     */
    public Task get(int index) {
        assert index >= 0 && index < size
                : "index must already be validated by validateIndex() before reaching here";
        return tasks[index];
    }

    /**
     * Returns the backing array, sized {@value #MAX_CAPACITY}. Only the
     * first {@link #size()} entries are valid; the rest are {@code null}.
     * Exists to interoperate with {@link Storage}, which operates on a
     * plain array/count pair.
     *
     * @return the backing task array.
     */
    public Task[] toArray() {
        return tasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add.
     * @throws YapBotException if the list is already at capacity, or an
     *         existing task in the list already has the same details.
     */
    public void add(Task task) throws YapBotException {
        if (isFull()) {
            throw new YapBotException("Sorry, your task list is full (max " + MAX_CAPACITY + " tasks).");
        }
        Task duplicate = findDuplicate(task);
        if (duplicate != null) {
            throw new YapBotException("This task already exists in your list: " + duplicate);
        }
        assert size < MAX_CAPACITY : "the isFull() guard above must have already ruled out this case";
        tasks[size] = task;
        size++;
    }

    /**
     * Finds an existing task in the list with the same details as {@code task}.
     *
     * @param task the task to check against the list.
     * @return the first matching existing task, or {@code null} if there is none.
     */
    private Task findDuplicate(Task task) {
        for (int i = 0; i < size; i++) {
            if (tasks[i].isDuplicateOf(task)) {
                return tasks[i];
            }
        }
        return null;
    }

    /**
     * Removes the task at the given index, shifting later tasks up by one.
     *
     * @param index a valid, zero-based index (see {@link #validateIndex}).
     * @return the task that was removed.
     */
    public Task delete(int index) {
        assert index >= 0 && index < size
                : "index must already be validated by validateIndex() before reaching here";
        Task removed = tasks[index];
        for (int i = index; i < size - 1; i++) {
            tasks[i] = tasks[i + 1];
        }
        tasks[size - 1] = null;
        size--;
        return removed;
    }

    /**
     * Marks the task at the given index as done.
     *
     * @param index a valid, zero-based index (see {@link #validateIndex}).
     */
    public void markAsDone(int index) {
        assert index >= 0 && index < size
                : "index must already be validated by validateIndex() before reaching here";
        tasks[index].markAsDone();
    }

    /**
     * Marks the task at the given index as not done.
     *
     * @param index a valid, zero-based index (see {@link #validateIndex}).
     */
    public void markAsNotDone(int index) {
        assert index >= 0 && index < size
                : "index must already be validated by validateIndex() before reaching here";
        tasks[index].markAsNotDone();
    }

    /**
     * Returns every task whose description contains the given keyword, in
     * list order.
     *
     * @param keyword the text to search for, matched as a case-sensitive substring.
     * @return the matching tasks, sized to exactly the number of matches.
     */
    public Task[] findMatching(String keyword) {
        assert keyword != null : "FindCommand must validate the keyword before calling this";
        return Arrays.stream(tasks, 0, size)
                .filter(task -> task.getDescription().contains(keyword))
                .toArray(Task[]::new);
    }

    /**
     * Parses and validates the task number out of a "mark"/"unmark"/"delete"
     * command against the current list, e.g. {@code "mark 2"} with keyword
     * {@code "mark"} yields the zero-based index {@code 1}.
     *
     * @param command the full command text as typed by the user.
     * @param keyword the command keyword to strip off the front (e.g. "mark").
     * @return the validated, zero-based task index.
     * @throws YapBotException if no number was given, it isn't a valid
     *         integer, the list is empty, or the number is out of range.
     */
    public int validateIndex(String command, String keyword) throws YapBotException {
        String argument = command.length() > keyword.length()
                ? command.substring(keyword.length()).trim()
                : "";
        if (argument.isEmpty()) {
            throw new YapBotException("Please specify a task number, e.g. '" + keyword + " 2'.");
        }

        int index;
        try {
            index = Integer.parseInt(argument) - 1;
        } catch (NumberFormatException e) {
            throw new YapBotException("'" + argument + "' is not a valid task number.");
        }

        if (size == 0) {
            throw new YapBotException("Your task list is empty, so there's nothing to " + keyword + ".");
        }
        if (index < 0 || index >= size) {
            throw new YapBotException("Task number " + (index + 1) + " doesn't exist. "
                    + "You have " + size + " task(s).");
        }
        return index;
    }
}