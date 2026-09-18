package davidgoggins.task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import davidgoggins.storage.Storage;

/**
 * Holds the user's tasks in the order they were added.
 *
 * <p>Task numbers used by {@code list}, {@code mark} and {@code unmark} are 1-based,
 * matching what the user sees; the conversion to 0-based {@link ArrayList} indices
 * happens here, so callers only ever deal in the numbers the user typed.
 */
public class TaskList {

    /**
     * ArrayList rather than a plain array because the list grows as the user
     * adds tasks, and we do not know the final size in advance.
     */
    private final ArrayList<Task> tasks;

    /** Used to write the list back to disk whenever it changes. */
    private final Storage storage;

    /**
     * Starts from the tasks already on disk.
     *
     * @param storage where the list is loaded from and saved to
     */
    public TaskList(Storage storage) {
        this.storage = storage;
        this.tasks = new ArrayList<>(storage.load());
    }

    /**
     * Starts from a list given directly, without reading the save file.
     *
     * <p>Provided so this class can be built and tested without touching the disk.
     *
     * @param storage where later changes are saved to
     * @param tasks   the tasks to start with
     */
    public TaskList(Storage storage, List<Task> tasks) {
        this.storage = storage;
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        assert task != null : "a null task would crash later, when it is listed or saved";
        tasks.add(task);
        save();
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return the list size, which is 0 before any task is added
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns how many tasks in the list are done.
     *
     * @return a count between 0 and {@link #size()} inclusive
     */
    public int countDone() {
        return (int) tasks.stream().filter(Task::isDone).count();
    }

    /**
     * Returns the number of a task already in the list that duplicates the given one.
     *
     * @param task the task about to be added
     * @return the 1-based number of the first duplicate, or 0 if there is none
     */
    public int findDuplicateNumber(Task task) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).isDuplicateOf(task)) {
                return i + 1;
            }
        }
        return 0;
    }

    /**
     * Returns true if {@code taskNumber} refers to an existing task.
     *
     * @param taskNumber the 1-based number the user typed
     * @return true if the number is between 1 and {@link #size()} inclusive
     */
    public boolean isValidTaskNumber(int taskNumber) {
        return taskNumber >= 1 && taskNumber <= tasks.size();
    }

    /**
     * Returns the task at the given 1-based position.
     *
     * @param taskNumber the 1-based number the user typed
     * @return the task at that position
     */
    public Task get(int taskNumber) {
        // Callers check isValidTaskNumber() and report a bad number to the user first,
        // so an out-of-range number reaching this point is a bug, not a user mistake.
        assert isValidTaskNumber(taskNumber) : "task number out of range: " + taskNumber;
        return tasks.get(taskNumber - 1); // -1 converts the user's numbering to ArrayList's
    }

    /**
     * Marks the given task as done.
     *
     * @param taskNumber the 1-based number the user typed
     * @return the task that was marked, so the caller can print it back
     */
    public Task mark(int taskNumber) {
        Task task = get(taskNumber);
        task.markAsDone();
        save();
        return task;
    }

    /**
     * Marks the given task as not done yet.
     *
     * @param taskNumber the 1-based number the user typed
     * @return the task that was unmarked, so the caller can print it back
     */
    public Task unmark(int taskNumber) {
        Task task = get(taskNumber);
        task.markAsNotDone();
        save();
        return task;
    }

    /**
     * Returns the numbered list of tasks, one per line, in the display form
     * {@code  1.[X] read book}.
     *
     * <p>The header line ("Here are the tasks in your list:") is left to the caller,
     * since it is part of the conversation rather than part of the list itself.
     */
    @Override
    public String toString() {
        return format(tasks);
    }

    /**
     * Returns the given tasks numbered from 1, one per line, in the display form
     * {@code  1.[X] read book}.
     *
     * <p>Takes the tasks as a parameter rather than reading the field, so that the
     * whole list and a set of search results are numbered by the same code.
     *
     * @param tasks the tasks to number, in the order they should appear
     * @return the numbered lines, or an empty string if there are no tasks
     */
    public static String format(List<Task> tasks) {
        // A stream of positions rather than of tasks, since each line needs its number;
        // i + 1 converts back to the 1-based numbering the user sees.
        return IntStream.range(0, tasks.size())
                .mapToObj(i -> " " + (i + 1) + "." + tasks.get(i))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * Returns the tasks whose description contains the given keyword.
     *
     * <p>The list itself is left alone, and the tasks returned are the same objects
     * it holds, so this is a way of looking at the list rather than a copy of it.
     *
     * @param keyword the text to look for, matched without regard to case
     * @return the matching tasks in list order, which may be empty
     */
    public List<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.matches(keyword))
                .collect(Collectors.toList());
    }

    /**
     * Removes the given task; the tasks after it move up one number.
     *
     * @param taskNumber the 1-based number the user typed
     * @return the task that was removed, so the caller can print it back
     */
    public Task remove(int taskNumber) {
        Task task = get(taskNumber);
        int sizeBefore = tasks.size();
        tasks.remove(taskNumber - 1);
        assert tasks.size() == sizeBefore - 1 : "removing a task should shrink the list by one";
        save();
        return task;
    }

    /**
     * Writes the current list to disk.
     *
     * <p>Called by every method that changes the list, rather than by the command
     * handlers, so that "the save file matches the list" holds by construction instead
     * of depending on every caller remembering.
     */
    private void save() {
        storage.save(tasks);
    }
}
