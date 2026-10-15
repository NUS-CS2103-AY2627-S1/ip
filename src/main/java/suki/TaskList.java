package suki;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The list of tasks Suki is tracking.
 *
 * <p>This wraps an {@link ArrayList} instead of passing one around directly so
 * that list-related rules live in one place. In particular, index checking
 * happens here rather than being repeated by every caller.
 */
public class TaskList {
    /** The tasks, in the order the user added them. */
    private final ArrayList<Task> tasks;

    /** Creates an empty list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a list holding the given tasks, e.g. those loaded from disk.
     *
     * @param tasks the tasks to start with
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "a TaskList must wrap a real list, not null";
        this.tasks = tasks;
    }

    /**
     * Returns how many tasks are in the list.
     *
     * @return the number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the list has no tasks.
     *
     * @return true if there are no tasks
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Appends a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        assert task != null : "callers must not add a null task";
        int sizeBefore = tasks.size();
        tasks.add(task);
        assert tasks.size() == sizeBefore + 1 : "adding must grow the list by exactly one";
    }

    /**
     * Returns the task at the given position.
     *
     * @param index zero-based position in the list
     * @return the task at that position
     * @throws SukiException if there is no task at that position
     */
    public Task get(int index) throws SukiException {
        checkIndex(index);
        Task task = tasks.get(index);
        assert task != null : "the list must never hold a null task";
        return task;
    }

    /**
     * Removes and returns the task at the given position.
     *
     * @param index zero-based position in the list
     * @return the task that was removed
     * @throws SukiException if there is no task at that position
     */
    public Task remove(int index) throws SukiException {
        checkIndex(index);
        int sizeBefore = tasks.size();
        Task removed = tasks.remove(index);
        assert tasks.size() == sizeBefore - 1 : "removing must shrink the list by exactly one";
        return removed;
    }

    /**
     * Returns the tasks whose description contains the given text.
     *
     * <p>The match is case-insensitive and looks anywhere in the description,
     * so "book" finds "Return book" as well as "bookshop". Only the
     * description is searched: dates are excluded because a user looking for
     * "oct" almost certainly means the word, not October.
     *
     * <p>Written as a stream because the search is exactly a filter over the
     * tasks: the stream says "keep the tasks whose description contains the
     * keyword" directly, where the equivalent loop says it indirectly, through
     * an accumulator list that the reader has to follow to see what is being
     * built.
     *
     * @param keyword the text to look for
     * @return a new list holding the matching tasks, in their original order
     */
    public TaskList find(String keyword) {
        assert keyword != null : "the keyword is validated by the parser and cannot be null here";
        String lowerKeyword = keyword.toLowerCase();
        ArrayList<Task> matches = tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
        assert matches.size() <= tasks.size() : "a search cannot return more tasks than there are";
        return new TaskList(matches);
    }

    /**
     * Orders tasks by when they are scheduled, earliest first.
     *
     * <p>Tasks with no date, i.e. todos, sort after every dated task rather
     * than before them: a sorted list is read to find out what is coming up
     * next, and an undated task is never the answer to that.
     *
     * <p>The comparator is used with a stable sort, so tasks that compare
     * equal, including all the undated ones, keep the order the user added
     * them in. That makes repeated sorts predictable.
     */
    private static final Comparator<Task> BY_SCHEDULED_DATE = (first, second) -> {
        Optional<DateTime> firstDate = first.getScheduledDateTime();
        Optional<DateTime> secondDate = second.getScheduledDateTime();
        if (firstDate.isEmpty() && secondDate.isEmpty()) {
            return 0;
        }
        if (firstDate.isEmpty()) {
            return 1;
        }
        if (secondDate.isEmpty()) {
            return -1;
        }
        return firstDate.get().compareTo(secondDate.get());
    };

    /**
     * Sorts the tasks in place, earliest scheduled task first.
     *
     * <p>Sorting in place rather than returning a sorted copy is deliberate:
     * the user asks for the list to be sorted, and expects the new order to be
     * the one they see from then on and the one that is saved.
     */
    public void sortByScheduledDate() {
        int sizeBefore = tasks.size();
        tasks.sort(BY_SCHEDULED_DATE);
        assert tasks.size() == sizeBefore : "sorting must not add or drop tasks";
    }

    /**
     * Returns the tasks in order, for components such as {@link Storage} and
     * {@link Ui} that need to walk every task.
     *
     * <p>The view is unmodifiable so that callers can read the list without
     * being able to change it behind this class's back. Callers that want to
     * add or remove go through {@link #add} and {@link #remove}, which is what
     * keeps the bounds checking in one place.
     *
     * @return an unmodifiable view of the tasks, in the order they were added
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Rejects positions that fall outside the list.
     *
     * @param index the zero-based position to check
     * @throws SukiException if there is no task at that position
     */
    private void checkIndex(int index) throws SukiException {
        // A bad index comes from the user, so it is an exception rather than an
        // assertion: assertions are for bugs, not for foreseeable user mistakes.
        if (index < 0 || index >= tasks.size()) {
            throw new SukiException("That task number doesn't exist. You have "
                    + tasks.size() + " task(s).");
        }
    }
}
