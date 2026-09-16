package lebronjames.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lebronjames.LebronJamesException;

/**
 * Holds the tasks the user is keeping track of, and the operations that change
 * or query that list.
 *
 * <p>This class wraps an {@code ArrayList<Task>} rather than extending it. That
 * keeps the chatbot's vocabulary small and deliberate: the rest of the program
 * can add, delete, and search tasks, but cannot reach in and, say, sort or
 * shuffle the list in ways the save file was never designed for. The list
 * itself is never handed out; {@link #asList()} returns an unmodifiable view,
 * so that promise holds in code and not only in this comment.
 *
 * <p>It is also where a task number is turned into a task. Doing the range
 * check here means every command that names a task &mdash; mark, unmark,
 * delete &mdash; reports an out-of-range number the same way.
 */
public class TaskList {
    /**
     * Returned by {@link #findDuplicateNumber(Task)} when the list holds no
     * task equal to the one asked about. Tasks are numbered from one, so zero
     * cannot be mistaken for a real task number.
     */
    public static final int NO_DUPLICATE = 0;

    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list holding the given tasks, typically the ones just
     * restored from the save file.
     *
     * @param tasks Tasks to start with.
     */
    public TaskList(ArrayList<Task> tasks) {
        // Storage.load() hands back an empty list when there is no save file, so
        // it never returns null. Null here would mean that contract was broken.
        assert tasks != null : "A task list must be created from a real list";

        this.tasks = tasks;
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        // Parser either returns a fully built task or throws, so it cannot hand
        // an AddCommand a null one.
        assert task != null : "Cannot add a null task";

        int sizeBefore = tasks.size();
        tasks.add(task);

        // Guards against a future change swapping in a collection that refuses
        // duplicates, which would silently drop a task the user was told was added.
        assert tasks.size() == sizeBefore + 1 : "Adding a task must grow the list by exactly one";
    }

    /**
     * Removes and returns the task with the given one-based number.
     *
     * @param taskNumber Number of the task as shown by the list command.
     * @return Task that was removed.
     * @throws LebronJamesException If no task has that number.
     */
    public Task remove(int taskNumber) throws LebronJamesException {
        Task task = get(taskNumber);

        int sizeBefore = tasks.size();
        tasks.remove(taskNumber - 1);

        // remove(int) removes by position, not by value. Asserting the size
        // change catches an accidental switch to remove(Object), which would
        // delete the first equal task instead of the numbered one.
        assert tasks.size() == sizeBefore - 1 : "Removing a task must shrink the list by exactly one";

        return task;
    }

    /**
     * Returns the task with the given one-based number.
     *
     * @param taskNumber Number of the task as shown by the list command.
     * @return Task with that number.
     * @throws LebronJamesException If no task has that number.
     */
    public Task get(int taskNumber) throws LebronJamesException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new LebronJamesException(
                    "AIRBALL. Play " + taskNumber + " is not on the board. Enter a number shown by list.");
        }
        Task task = tasks.get(taskNumber - 1);

        // Nothing ever puts a null into the list, and every caller immediately
        // marks, deletes, or prints what it gets back.
        assert task != null : "The task list must never hold a null task";

        return task;
    }

    /**
     * Returns the number of the task already on the list that is the same as
     * the given one, or {@link #NO_DUPLICATE} if there is none.
     *
     * <p>What counts as "the same" is decided by the task itself, in
     * {@link Task#equals(Object)}: same kind, same description ignoring
     * capitalisation, and the same dates where the kind carries any.
     *
     * <p>The number is returned rather than a plain yes or no so the chatbot
     * can point the user at the task that is already there.
     *
     * @param task Task being considered for adding.
     * @return One-based number of the existing duplicate, or NO_DUPLICATE.
     */
    public int findDuplicateNumber(Task task) {
        assert task != null : "Cannot look for a duplicate of a null task";

        // indexOf compares with equals, which is what defines a duplicate here.
        int duplicateIndex = tasks.indexOf(task);
        return duplicateIndex == -1 ? NO_DUPLICATE : duplicateIndex + 1;
    }

    /**
     * Returns the tasks that fall on the given date.
     *
     * <p>To-dos are never included because they carry no date.
     *
     * @param date Date the user asked about.
     * @return Tasks scheduled on that date, in list order, as an unmodifiable list.
     */
    public List<Task> findTasksOn(LocalDate date) {
        List<Task> matchingTasks = tasks.stream()
                .filter(task -> task.isOn(date))
                .toList();

        // A filter can only ever narrow the list, so a longer result would mean
        // tasks were being duplicated into it.
        assert matchingTasks.size() <= tasks.size() : "A search cannot return more tasks than the list holds";

        return matchingTasks;
    }

    /**
     * Returns the tasks whose description contains the given keyword.
     *
     * <p>The search ignores capitalisation, so "book" finds "Read Book". It
     * matches anywhere in the description rather than only whole words, so
     * "book" also finds "bookshop"; that is the more forgiving behaviour for
     * someone half-remembering what they typed.
     *
     * @param keyword Text to look for.
     * @return Matching tasks, in list order, as an unmodifiable list.
     */
    public List<Task> findTasksWithKeyword(String keyword) {
        String lowerCaseKeyword = keyword.toLowerCase();
        List<Task> matchingTasks = tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(lowerCaseKeyword))
                .toList();

        // As above: filtering cannot invent tasks that are not in the list.
        assert matchingTasks.size() <= tasks.size() : "A search cannot return more tasks than the list holds";

        return matchingTasks;
    }

    /**
     * Returns a read-only view of all tasks, in list order, for displaying or
     * saving.
     *
     * <p>The view reflects later changes made through this TaskList, but cannot
     * itself be used to make them.
     *
     * @return The tasks currently stored, as an unmodifiable list.
     */
    public List<Task> asList() {
        // A view rather than a copy: every caller only reads the list, so
        // copying would allocate on each save for no benefit, while a view
        // still refuses changes attempted behind this class's back.
        return Collections.unmodifiableList(tasks);
    }
}
