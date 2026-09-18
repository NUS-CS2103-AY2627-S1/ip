package milo.task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import milo.exception.MiloException;

/**
 * Contains the task list and the operations that act on it: adding,
 * removing, retrieving, and rendering it for display. An {@link ArrayList}
 * backs the list so it grows as needed, with no separate counter to keep in
 * step with the number of tasks actually stored.
 */
public class TaskList {
    /** The tasks, in the order they were added. */
    private final ArrayList<Task> tasks;

    /**
     * The tasks as they stood before the most recent change, or null when
     * nothing has changed yet. Exactly one snapshot is kept, which is what
     * limits undo to the single most recent change.
     */
    private ArrayList<Task> previousTasks = null;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list starting from an existing collection of tasks,
     * such as one just read back from disk.
     *
     * @param tasks the tasks to start with
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        // Parser either builds a real task or throws, so it never hands over null.
        assert task != null : "a null task should never be added to the list";

        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given position.
     *
     * @param index the position to remove, counting from 0
     * @return the task that was removed
     * @throws MiloException if the index does not refer to a task in the list
     */
    public Task delete(int index) throws MiloException {
        requireInRange(index);

        Task removed = tasks.remove(index);
        // requireInRange has already ruled out the only case that could
        // return nothing, so callers are safe to display this directly.
        assert removed != null : "an in-range index should always yield a task";
        return removed;
    }

    /**
     * Returns the task at the given position, without removing it.
     *
     * @param index the position to fetch, counting from 0
     * @return the task at that position
     * @throws MiloException if the index does not refer to a task in the list
     */
    public Task get(int index) throws MiloException {
        requireInRange(index);
        return tasks.get(index);
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
     * Returns whether the list has no tasks in it.
     *
     * @return true if the list is empty
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the underlying tasks, for code (such as {@link Storage}) that
     * needs to work with the whole collection rather than one task at a time.
     *
     * @return the tasks, in list order
     */
    public ArrayList<Task> asArrayList() {
        return tasks;
    }

    /**
     * Renders the tasks as numbered lines, ready to be displayed.
     *
     * @return the lines of the list block, or a single explanatory line if
     *         nothing has been stored yet
     */
    public String[] getDisplayLines() {
        return renderNumbered(tasks,
                "Here's everything on your list:",
                "Your list is empty — nothing to chase yet.");
    }

    /**
     * Renders the tasks whose description contains the given keyword, as
     * numbered lines ready to be displayed. Matches are renumbered from 1
     * within the result, rather than keeping their position in the full
     * list, since the user searching has no way to know what those original
     * numbers were.
     *
     * @param keyword the text to search for in each task's description
     * @return the lines of the matching-tasks block, or a single explanatory
     *         line if nothing matched
     */
    public String[] getMatchingDisplayLines(String keyword) {
        List<Task> matches = tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .toList();

        return renderNumbered(matches,
                "Here's what I sniffed out:",
                "I had a good sniff around and found nothing matching.");
    }

    /**
     * Renders tasks as a header followed by lines numbered from 1, which is
     * how both the full list and a search result are displayed.
     *
     * @param tasksToShow  the tasks to render, in the order to show them
     * @param header       the line introducing the block
     * @param emptyMessage the single line to show instead when there is
     *                     nothing to render
     * @return the lines of the block
     */
    private static String[] renderNumbered(List<Task> tasksToShow, String header,
            String emptyMessage) {
        if (tasksToShow.isEmpty()) {
            return new String[] {emptyMessage};
        }

        // Tasks are numbered from 1 for the user, but indexed from 0.
        String[] lines = Stream.concat(
                Stream.of(header),
                IntStream.range(0, tasksToShow.size())
                        .mapToObj(i -> (i + 1) + "." + tasksToShow.get(i)))
                .toArray(String[]::new);

        // One header line plus one line per task: a mismatch would mean the
        // caller was silently shown fewer tasks than it passed in.
        assert lines.length == tasksToShow.size() + 1 : "every task should get exactly one line";
        return lines;
    }

    /**
     * Returns whether the list already holds a task that reads exactly like
     * the given one. Tasks are compared by how they are displayed, which
     * covers the type, the description and any dates, but deliberately not
     * whether they are done: a task already ticked off is still the same task.
     *
     * @param candidate the task about to be added
     * @return true if an identical task is already on the list
     */
    public boolean containsSameAs(Task candidate) {
        String candidateText = describeIgnoringStatus(candidate);
        return tasks.stream()
                .anyMatch(task -> describeIgnoringStatus(task).equals(candidateText));
    }

    /**
     * Renders a task the way it is displayed, but always as not done, so that
     * two tasks can be compared on what they are rather than on their status.
     *
     * @param task the task to describe
     * @return the task's text with its status box cleared
     */
    private static String describeIgnoringStatus(Task task) {
        Task copy = task.copy();
        copy.markAsNotDone();
        return copy.toString();
    }

    /**
     * Remembers the tasks as they stand now, so that a command about to
     * change them can be undone. Replaces any earlier snapshot, so only the
     * most recent change can ever be undone.
     */
    public void saveSnapshot() {
        // Tasks are copied rather than shared: mark and unmark change a task
        // in place, so a snapshot holding the same objects would change too.
        previousTasks = tasks.stream()
                .map(Task::copy)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Puts the tasks back as they were at the most recent
     * {@link #saveSnapshot()}, and forgets that snapshot so undo does not
     * repeat.
     *
     * @return true if there was a snapshot to go back to, false if nothing
     *         has changed yet
     */
    public boolean restorePrevious() {
        if (previousTasks == null) {
            return false;
        }

        // The list itself is replaced in place rather than reassigned, so
        // that anything already holding this TaskList sees the change.
        tasks.clear();
        tasks.addAll(previousTasks);
        previousTasks = null;
        return true;
    }

    /**
     * Checks that a position refers to an actual task.
     * A defensive check: normal use always validates the number first (see
     * {@code Milo.parseTaskIndex}), so this only guards against misuse.
     *
     * @param index the position to check, counting from 0
     * @throws MiloException if the position is out of range
     */
    private void requireInRange(int index) throws MiloException {
        if (index < 0 || index >= tasks.size()) {
            throw new MiloException("There's no task " + (index + 1) + " on your list.");
        }
    }
}
