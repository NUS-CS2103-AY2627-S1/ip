package harvey.task;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import harvey.HarveyException;
/**
 * The list of tasks Harvey is keeping, together with the operations that change it.
 * <p>
 * The point of wrapping the {@link ArrayList} rather than passing one around is where the
 * task numbers are handled. The user counts tasks from 1 and an {@code ArrayList} counts
 * from 0, so every caller used to subtract 1 by hand and check the range itself. Both of
 * those now happen here, once, which is the only place that can see the size of the list
 * anyway.
 */
public class TaskList {
    /** The tasks, in the order the user added them. Never null. */
    private final ArrayList<Task> tasks;

    /** Creates an empty list, for when there is nothing saved to start from. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a list holding tasks that were loaded from somewhere, e.g. the save file.
     *
     * @param tasks the tasks to start with.
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "Storage.load() returns an empty list when there is no save file, never null";
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add.
     */
    public void add(Task task) {
        assert task != null : "AddCommand.createTask() either builds a task or throws, so it is never null";
        tasks.add(task);
    }

    /**
     * Returns the task the user refers to by number.
     *
     * @param taskNumber the number shown by {@code list}, counting from 1.
     * @return the task with that number.
     * @throws HarveyException if there is no task with that number.
     */
    public Task get(int taskNumber) throws HarveyException {
        return tasks.get(toIndex(taskNumber));
    }

    /**
     * Removes the task the user refers to by number and returns it.
     * The tasks after it shift down, so the remaining ones stay numbered 1, 2, 3, ...
     * with no gap.
     *
     * @param taskNumber the number shown by {@code list}, counting from 1.
     * @return the task that was removed.
     * @throws HarveyException if there is no task with that number.
     */
    public Task delete(int taskNumber) throws HarveyException {
        int sizeBefore = tasks.size();
        Task removed = tasks.remove(toIndex(taskNumber));
        assert tasks.size() == sizeBefore - 1 : "Deleting one task should shorten the list by exactly one";
        return removed;
    }

    /**
     * Returns how many tasks are stored.
     *
     * @return the number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether there are no tasks at all.
     *
     * @return true if the list is empty.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the tasks as a plain list, for code that needs to walk through all of them.
     * <p>
     * This hands out the actual list rather than a copy, which is the simple choice: a
     * caller could in principle modify it behind this class's back. Returning a copy, or
     * making {@code TaskList} implement {@code Iterable}, would close that hole; neither
     * is worth the extra machinery while the only callers are display and saving.
     *
     * @return the tasks in order.
     */
    public ArrayList<Task> asList() {
        return tasks;
    }

    /**
     * Converts a task number as the user says it into a position in the list.
     *
     * @param taskNumber the number shown by {@code list}, counting from 1.
     * @return the matching index, counting from 0.
     * @throws HarveyException if the number names a task that does not exist.
     */
    private int toIndex(int taskNumber) throws HarveyException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new HarveyException("There is no task " + taskNumber + ". You have "
                    + tasks.size() + " task(s), so pick a number from 1 to " + tasks.size() + ".");
        }
        int index = taskNumber - 1;
        assert index >= 0 && index < tasks.size()
                : "The range check above should have rejected " + taskNumber;
        return index;
    }

    /**
     * Returns an event already in the list that shares time with the given one.
     * <p>
     * Only events can clash. A todo has no time at all and a deadline is a single instant
     * to finish something by, so neither occupies a stretch of the schedule that another
     * task could collide with; both are skipped.
     * <p>
     * The search stops at the first match rather than collecting every one, because the
     * caller only needs to name one conflict for the user to act on. An event is never
     * reported as clashing with itself, so a task that is already stored can be checked
     * again safely.
     *
     * @param candidate the event being considered.
     * @return the first clashing event, or empty if the candidate fits.
     */
    public Optional<Event> findClash(Event candidate) {
        assert candidate != null : "AddCommand only checks an event it has just built";
        return tasks.stream()
                .filter(task -> task instanceof Event)
                .map(task -> (Event) task)
                .filter(event -> event != candidate && event.overlaps(candidate))
                .findFirst();
    }

    /**
     * Returns a task already in the list with the same details as the given one.
     * <p>
     * Like {@link #findClash(Event)}, a task is never reported as a copy of itself, and
     * only the first match is returned because one is enough for the user to act on.
     *
     * @param candidate the task being considered.
     * @return the first task with the same details, or empty if the candidate is new.
     */
    public Optional<Task> findDuplicate(Task candidate) {
        assert candidate != null : "AddCommand only checks a task it has just built";
        return tasks.stream()
                .filter(task -> task != candidate && task.hasSameDetails(candidate))
                .findFirst();
    }

    /**
     * Returns the tasks whose description contains the given keyword.
     * <p>
     * The result is a new list, so the matches can be shown without disturbing the
     * numbering of the real one. That also means marking or deleting is still done
     * against the full list, using the numbers that {@code list} shows.
     *
     * @param keyword the text to look for.
     * @return a list of the matching tasks, in their original order.
     */
    public TaskList find(String keyword) {
        // toCollection is used rather than toList because the TaskList constructor takes
        // an ArrayList, and because the result is a list the caller may keep adding to.
        return new TaskList(tasks.stream()
                .filter(task -> task.hasKeyword(keyword))
                .collect(Collectors.toCollection(ArrayList::new)));
    }
}
