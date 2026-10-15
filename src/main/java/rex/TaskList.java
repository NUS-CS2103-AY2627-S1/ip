package rex;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import rex.task.Task;

/**
 * The tasks the user is keeping, and the operations that work on them as a
 * group: adding, removing, and finding the ones that fall on a given day.
 *
 * Wrapping the list rather than passing an ArrayList around means questions
 * about the list are asked of the list itself, and the answer lives in one
 * place instead of being written out again wherever it is needed.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list holding the given tasks, in the order given.
     *
     * @param tasks the tasks to start with, e.g. the ones just loaded from the
     *     save file.
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "A task list is built either empty or from tasks that were loaded";
        this.tasks = tasks;
    }

    /** Returns how many tasks there are. */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at a position, counting from 0.
     *
     * @param index a position that has already been checked against size().
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "Position " + index + " was never checked against size()";
        return tasks.get(index);
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        assert task != null : "The parser builds a task before an add command is created";
        tasks.add(task);
    }

    /**
     * Returns the task the user means by a number, counting from 1 as shown
     * in the list.
     *
     * Whether a number names a task depends on how many there are, which is
     * this class's own business, so the check belongs here rather than in
     * whatever is about to use the task.
     *
     * @param number the task number as the user typed it, counting from 1.
     * @throws RexException if no task has that number.
     */
    public Task getByNumber(int number) throws RexException {
        return tasks.get(requireExistingNumber(number));
    }

    /**
     * Removes and returns the task the user means by a number, counting from 1.
     *
     * @param number the task number as the user typed it, counting from 1.
     * @return the task that was removed, so that it can be shown to the user.
     * @throws RexException if no task has that number.
     */
    public Task deleteByNumber(int number) throws RexException {
        return tasks.remove(requireExistingNumber(number));
    }

    /**
     * Turns a task number counting from 1 into a position counting from 0,
     * refusing any number that names no task.
     */
    private int requireExistingNumber(int number) throws RexException {
        int index = number - 1;
        if (index < 0 || index >= tasks.size()) {
            throw new RexException("Woof! There's no task numbered " + number + " in your bowl.");
        }
        return index;
    }

    /**
     * Returns the positions of the tasks falling on the given day, in list
     * order.
     *
     * Positions are returned rather than the tasks themselves because the user
     * sees each task by its number in the whole list, and that number would be
     * lost if the matches were handed back on their own.
     *
     * @param day the day being asked about.
     * @return the positions, counting from 0, of every task on that day.
     */
    public List<Integer> findIndicesOn(LocalDate day) {
        // A stream over the positions rather than over the tasks themselves,
        // because it is the positions that are being collected.
        return IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).isOn(day))
                .boxed()
                .toList();
    }

    /**
     * Returns the positions of the tasks whose description contains the given
     * keyword, ignoring case, in list order.
     *
     * Any part of a description counts, not only whole words, so "book" finds
     * "bookshop" as well as "read book". Positions are returned rather than
     * the tasks themselves for the same reason as in findIndicesOn: the user
     * knows each task by its number in the whole list.
     *
     * @param keyword the text to look for.
     * @return the positions, counting from 0, of every task containing it.
     */
    public List<Integer> findIndicesMatching(String keyword) {
        assert keyword != null && !keyword.isEmpty() : "The parser refuses a find command with no keyword";

        String lowercaseKeyword = keyword.toLowerCase();

        return IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).getDescription()
                        .toLowerCase().contains(lowercaseKeyword))
                .boxed()
                .toList();
    }

    /**
     * Returns the tasks themselves, for saving to disk.
     *
     * The list is wrapped so that it cannot be changed through what is handed
     * back. Returning the real list would undo the point of holding it
     * privately: any caller could then clear it, or add a null to it, without
     * going through this class at all.
     *
     * Note that this is a view, not a copy, so it stays in step with later
     * changes made through this class and costs nothing to create. Trying to
     * change it fails at run time rather than being caught by the compiler,
     * which is the trade-off for not copying the list on every save.
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }
}
