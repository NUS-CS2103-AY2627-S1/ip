package seedu.goat.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Holds the tasks the user is tracking, in the order they were added.
 * <p>
 * This class deliberately does no printing and no saving. It answers questions about the
 * tasks and changes them, leaving the caller to decide what to tell the user and when to
 * write to disk, which is what makes it straightforward to test on its own.
 */
public class TaskList {

    /** The tasks, in list order. Positions shown to the user are these indices plus one. */
    private final ArrayList<Task> tasks;

    /**
     * Creates a list holding the tasks named here, or an empty list if none are named.
     * <p>
     * Varargs rather than a separate no-argument constructor, because a caller that
     * already knows its tasks can now name them in place instead of constructing an
     * empty list and calling {@link #add(Task)} once per task. {@code new TaskList()}
     * still reads the same and simply passes no tasks.
     *
     * @param tasks the tasks to start with, in order.
     */
    public TaskList(Task... tasks) {
        this.tasks = new ArrayList<>(List.of(tasks));
    }

    /**
     * Creates a list holding the given tasks.
     * <p>
     * Kept alongside the varargs constructor for callers such as Storage, which loads a
     * list whose length it cannot know when the code is written. The tasks are copied,
     * so later changes to this list do not disturb the caller's.
     *
     * @param tasks the tasks to start with, in order.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add.
     */
    public void add(Task task) {
        assert task != null : "A null task would break every later listing";
        tasks.add(task);
    }

    /**
     * Removes the task at a position and returns it.
     *
     * @param index zero-based position in the list.
     * @return the task that was removed.
     */
    public Task delete(int index) {
        assert index >= 0 && index < tasks.size() : "Callers check the position before deleting";
        return tasks.remove(index);
    }

    /**
     * Returns the task at a position without removing it.
     *
     * @param index zero-based position in the list.
     * @return the task at that position.
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "Callers check the position before reading";
        return tasks.get(index);
    }

    /**
     * Returns how many tasks are held.
     *
     * @return the number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether there are no tasks.
     *
     * @return true if the list is empty.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the tasks falling on a given date, in list order.
     * <p>
     * Each task decides for itself whether it matches, so this method needs no knowledge
     * of the different kinds of task.
     *
     * @param date the date being asked about.
     * @return the matching tasks, empty if none match.
     */
    public List<Task> findOn(LocalDate date) {
        assert date != null : "A date is parsed before the list is searched";
        return tasks.stream()
                .filter(task -> task.isOn(date))
                .toList();
    }

    /**
     * Returns the tasks whose description contains a keyword, in list order.
     * <p>
     * Each task decides for itself whether it matches, so this method needs no access to
     * the description and no knowledge of the different kinds of task.
     *
     * @param keyword the text to look for.
     * @return the matching tasks, empty if none match.
     */
    public List<Task> find(String keyword) {
        assert keyword != null : "The parser rejects a missing keyword before the search";
        return tasks.stream()
                .filter(task -> task.hasKeyword(keyword))
                .toList();
    }

    /**
     * Returns where a task that duplicates the given one already sits.
     * <p>
     * The position is returned rather than a plain yes or no so that the caller can name
     * the offending task back to the user, who then knows which entry to look at.
     *
     * @param candidate the task the user is trying to add.
     * @return the zero-based position of the first duplicate, or -1 if there is none.
     */
    public int indexOfDuplicate(Task candidate) {
        assert candidate != null : "The parser builds a task before it is checked";
        return IntStream.range(0, tasks.size())
                .filter(i -> tasks.get(i).isDuplicateOf(candidate))
                .findFirst()
                .orElse(-1);
    }

    /**
     * Returns a read-only view of the tasks, for callers that only need to walk them.
     *
     * @return the tasks in list order.
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }
}
