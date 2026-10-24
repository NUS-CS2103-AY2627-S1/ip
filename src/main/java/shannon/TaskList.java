package shannon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import shannon.exception.DuplicateTaskException;
import shannon.exception.TaskNotFoundException;
import shannon.task.Task;

/**
 * The list of tasks, and the operations that change it.
 * <p>
 * Wrapping the {@link ArrayList} keeps the list and the rule protecting it together: every command
 * that names a task ({@code mark}, {@code unmark}, {@code delete}) must check that the number
 * refers to a real task, and here that check cannot be skipped, because the only way in is through
 * a method that performs it.
 * <p>
 * {@link #getTasks(int...)} and {@link #deleteTasks(int...)} take the numbers the user typed,
 * counting from 1, not positions counting from 0 &mdash; hence those names rather than
 * {@code get}/{@code remove}, which a reader would rightly expect to be zero-based. Both are
 * varargs, so one method serves {@code delete 2} and {@code delete 2 5 7} alike; the alternative,
 * an overload taking a single {@code int} beside one taking a list, would be two methods that must
 * be kept saying the same thing.
 */
public class TaskList {

    /** The tasks, in the order they were added. */
    private final ArrayList<Task> tasks;

    /** Starts an empty list, for a first run or after a save file could not be read. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Starts from tasks already restored from the save file.
     *
     * @param tasks the loaded tasks, in file order; copied so that later changes to the list
     *              cannot be made behind this object's back.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list, unless the list already has one with the same details.
     * <p>
     * A repeat is almost always a mistake, such as a command sent twice, or a task added again
     * after a save error made it look lost. Refusing it keeps the list from filling with copies
     * the user then has to delete one by one. {@link Task#hasSameDetails(Task)} says what counts
     * as the same.
     *
     * @param task the task to add
     * @throws DuplicateTaskException if a task with the same details is already in the list
     */
    public void add(Task task) throws DuplicateTaskException {
        assert task != null : "Cannot add a null task";
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).hasSameDetails(task)) {
                // Named by its number, counting from 1, so the user can go straight to it.
                throw new DuplicateTaskException(tasks.get(i).toString(), i + 1);
            }
        }
        tasks.add(task);
    }

    /**
     * Returns the tasks the user asked for, leaving them in the list.
     *
     * @param taskNumbers the numbers the user typed, counting from 1; at least one is expected
     * @return the tasks with those numbers, in the order asked for and without repeats
     * @throws TaskNotFoundException if any of the numbers matches no task
     */
    public List<Task> getTasks(int... taskNumbers) throws TaskNotFoundException {
        // Parser.parseTaskNumbers() refuses an empty line, so there is always at least one number.
        assert taskNumbers.length > 0 : "At least one task number is expected";
        List<Task> found = new ArrayList<>();
        for (int index : indicesOf(taskNumbers)) {
            found.add(tasks.get(index));
        }
        return Collections.unmodifiableList(found);
    }

    /**
     * Removes the tasks the user asked for and returns them, so the caller can still show what
     * was deleted.
     * <p>
     * Either every named task is removed or none is: the numbers are all checked before the first
     * removal, so {@code delete 2 99} reports the bad number and leaves task 2 alone rather than
     * deleting half of what was asked for.
     *
     * @param taskNumbers the numbers the user typed, counting from 1; at least one is expected
     * @return the tasks that were removed, in the order asked for and without repeats
     * @throws TaskNotFoundException if any of the numbers matches no task
     */
    public List<Task> deleteTasks(int... taskNumbers) throws TaskNotFoundException {
        assert taskNumbers.length > 0 : "At least one task number is expected";
        int sizeBefore = tasks.size();
        List<Integer> indices = new ArrayList<>(indicesOf(taskNumbers));
        List<Task> removed = indices.stream()
                .map(tasks::get)
                .toList();

        // Highest position first. ArrayList.remove(int) shifts the later tasks down to close the
        // gap, so removing in the order typed would leave the remaining indices pointing one
        // place too far along the list.
        indices.sort(Comparator.reverseOrder());
        for (int index : indices) {
            tasks.remove(index);
        }
        // Repeats were already dropped by indicesOf(), so every task collected above went once.
        assert tasks.size() == sizeBefore - removed.size()
                : "Each named task should be removed exactly once";
        return Collections.unmodifiableList(removed);
    }

    /**
     * Returns every task whose description contains {@code keyword}, in list order.
     * <p>
     * The match ignores case and looks anywhere in the description, so {@code find book} also
     * finds "Bookshop trip": someone searching their own list is recalling it roughly, not
     * quoting it. Only the description is searched, not a deadline's date or an event's times.
     * <p>
     * The result is a plain {@link List} rather than another {@code TaskList}, because it is a
     * snapshot to be shown and nothing more: deleting from it would not delete from the real
     * list, so it should not offer the methods that look as though it would.
     *
     * @param keyword the text to look for; may be several words
     * @return the matching tasks, unmodifiable and possibly empty
     */
    public List<Task> find(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(lowerKeyword))
                .toList();
    }

    /** Returns how many tasks are in the list. */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the tasks in the order they were added, for code that only reads them: the
     * {@link Ui} to print, and the {@link Storage} to save.
     *
     * @return an unmodifiable view, so that handing the tasks out cannot become a second way of
     *         changing them that bypasses the checks above
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Turns the task numbers the user typed into positions in the list, checking each on the way.
     * <p>
     * Private, because a position is this class's own business: no caller outside should ever
     * hold one and risk using it after the list has changed.
     * <p>
     * A {@link LinkedHashSet} drops repeats while keeping the order they were typed in, so
     * {@code delete 2 2} removes the second task once instead of removing it and then whatever
     * moved up into its place.
     *
     * @param taskNumbers the numbers the user typed, counting from 1.
     * @return the matching indices, counting from 0, in the order asked for and without repeats.
     * @throws TaskNotFoundException if any number does not match a task in the list.
     */
    private Set<Integer> indicesOf(int... taskNumbers) throws TaskNotFoundException {
        Set<Integer> indices = new LinkedHashSet<>();
        for (int taskNumber : taskNumbers) {
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new TaskNotFoundException(taskNumber, tasks.size());
            }
            indices.add(taskNumber - 1); // the user counts from 1, the list from 0
        }
        return indices;
    }
}
