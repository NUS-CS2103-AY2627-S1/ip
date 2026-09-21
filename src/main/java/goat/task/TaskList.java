package goat.task;

import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;

import goat.GoatException;

/**
 * Holds the tasks Goat is keeping track of, and the operations that change them.
 *
 * Before this class existed, {@link goat.Goat} passed a bare {@code ArrayList<Task>}
 * to every helper method. That worked, but it left the list itself with no
 * behavior of its own: anything holding the list could do anything to it, and
 * "the rules about the task list" were spread across whoever happened to be
 * handling the current command.
 *
 * Wrapping the list gives those rules one home. In particular, the translation
 * from the number the user types (counting from 1) to the index the list uses
 * (counting from 0), and the check that the number refers to a task that really
 * exists, now happen here in {@link #get(int)} and {@link #delete(int)} rather
 * than being repeated by each caller.
 *
 * The wrapped list is kept private and is never handed out, so a task can only
 * be added or removed through the methods below.
 */
public class TaskList {
    /**
     * The tasks, in the order the user sees them.
     *
     * An ArrayList grows as needed and tracks its own size, so there is no
     * fixed cap and no separate counter to keep in step with the contents.
     */
    private final ArrayList<Task> tasks;

    /** Creates an empty task list, for a first run or after a failed load. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list holding tasks that were already built elsewhere,
     * typically the ones {@link goat.storage.Storage#load()} has just read from disk.
     *
     * The list is copied rather than stored directly, so that whoever supplied
     * it cannot go on changing the contents behind this object's back.
     *
     * @param tasks the tasks to start with
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds one task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes the task with the given number and hands it back, so the caller
     * can show the user what was removed.
     *
     * Everything after the removed task shifts down a place, so the numbers
     * shown by "list" stay contiguous.
     *
     * @param taskNumber the task's position as the user counts it, from 1
     * @return the task that was removed
     * @throws GoatException if no task has that number
     */
    public Task delete(int taskNumber) throws GoatException {
        return tasks.remove(toIndex(taskNumber));
    }

    /**
     * Returns the task with the given number, without removing it.
     *
     * @param taskNumber the task's position as the user counts it, from 1
     * @return the task at that position
     * @throws GoatException if no task has that number
     */
    public Task get(int taskNumber) throws GoatException {
        return tasks.get(toIndex(taskNumber));
    }

    /**
     * Returns every task whose description contains the given text.
     *
     * The search is case-insensitive and matches anywhere in the description,
     * so "book" finds "read book" and "Book club" alike. Matching on a
     * substring rather than a whole word is the more forgiving of the two, and
     * someone searching a task list is usually half-remembering a phrase.
     *
     * The matches are returned as a new TaskList so that the caller can number
     * and show them with the same code that handles the full list. They are the
     * same Task objects, not copies, but the returned list is separate: adding
     * to or deleting from it does not touch the list searched.
     *
     * @param keyword the text to look for
     * @return the matching tasks, in the order they appear in this list
     */
    public TaskList find(String keyword) {
        // Locale.ROOT, not the machine's locale. Under a Turkish locale
        // "TITLE".toLowerCase() is "tıtle" with a dotless i, so a search for
        // "title" would silently match nothing -- the same app finding
        // different things depending on the user's OS language setting.
        String lowerCaseKeyword = keyword.toLowerCase(Locale.ROOT);
        // The loop this replaces spent four of its six lines on the mechanics
        // of collecting results. What is left says only what a match is.
        TaskList matches = new TaskList(tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT)
                        .contains(lowerCaseKeyword))
                .collect(Collectors.toCollection(ArrayList::new)));
        assert matches.size() <= tasks.size() : "find returned more tasks than the list holds";
        return matches;
    }

    /**
     * Puts the tasks into the given order.
     *
     * The list is sorted in place rather than a sorted copy being returned,
     * because the numbers the user types refer to positions in this list: a
     * sorted copy would leave "mark 1" meaning the task at the top of a list
     * nobody is looking at.
     *
     * @param order the order to put them in
     */
    public void sort(SortOrder order) {
        tasks.sort(order.getComparator());
    }

    /**
     * Reports whether the list already holds a task describing the same thing.
     *
     * @param task the task to look for
     * @return true if an equivalent task is already on the list
     */
    public boolean containsSameTaskAs(Task task) {
        return tasks.stream().anyMatch(existing -> existing.hasSameDetailsAs(task));
    }

    /**
     * Returns how many tasks are stored.
     *
     * @return the number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Reports whether there are no tasks at all.
     *
     * @return true if the list is empty
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Converts a task number as the user counts it into a list index, checking
     * on the way that it refers to a task that exists.
     *
     * This is the single place that knows about the off-by-one between the two
     * ways of counting, which is why every other method here goes through it.
     *
     * @param taskNumber the task's position as the user counts it, from 1
     * @return the matching 0-based index
     * @throws GoatException if the number is outside the list
     */
    private int toIndex(int taskNumber) throws GoatException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new GoatException("There is no task '" + taskNumber
                    + "'. Use list to see what there is.");
        }
        int index = taskNumber - 1;
        // Not user input any more: the throw above has already dealt with a
        // number the user got wrong, so anything out of range here would mean
        // the conversion itself is broken.
        assert index >= 0 && index < tasks.size()
                : "index " + index + " outside a list of " + tasks.size();
        return index;
    }
}
