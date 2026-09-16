package serangooner.task;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import serangooner.SerangoonerException;

/**
 * Stores and edits the tasks entered by the user.
 * The list lives entirely in memory and knows nothing of where its tasks
 * came from, so writing them back to disk is left to whoever holds both
 * this list and a {@link serangooner.storage.Storage Storage}. It says
 * nothing to the user either: an edit gives back the task it touched, and
 * how that is worded is for the {@link serangooner.ui.Ui Ui} to decide.
 * Every edit pushes an action that reverses it, so the most recent edit
 * can be undone.
 */
public class TaskList {
    private final List<Task> tasks = new ArrayList<>();
    // Undo stack authored with the help of Codex.
    private final Deque<Runnable> undoActions = new ArrayDeque<>();

    /**
     * Constructs an empty task list.
     */
    public TaskList() {
        this(List.of());
    }

    /**
     * Constructs a task list holding the given tasks.
     *
     * @param tasks Tasks the list starts out with, in the order they are shown.
     */
    public TaskList(List<Task> tasks) {
        this.tasks.addAll(tasks);
    }

    /**
     * Returns the tasks in the order they are shown, as a view that cannot be
     * edited through.
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns the number of tasks currently stored.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Adds the given task to the end of the list.
     * A task that duplicates one already in the list is refused, so that the
     * same task is never entered twice by mistake. The tasks a list starts out
     * with are taken as they are, without being checked against each other.
     *
     * @param task Task to add.
     * @return Task that was added.
     * @throws SerangoonerException If the list already holds a duplicate of the task.
     */
    public Task add(Task task) {
        Optional<Entry> duplicate = findDuplicate(task);
        if (duplicate.isPresent()) {
            throw new SerangoonerException("ADD FAILED. you already have that as task "
                    + duplicate.get().number() + ": " + duplicate.get().task());
        }

        int taskCountBefore = tasks.size();
        tasks.add(task);
        undoActions.push(() -> tasks.remove(task));
        assert tasks.size() == taskCountBefore + 1 : "adding must leave exactly one more task";
        return task;
    }

    /**
     * Marks the task at the given position as done.
     *
     * @param taskNumber Position of the task in the list, counting from one.
     * @return Task that was marked.
     * @throws SerangoonerException If no task holds that position.
     */
    public Task mark(int taskNumber) {
        Task task = getTask(taskNumber, "MARK");
        boolean wasDone = task.isDone();
        task.markDone();
        assert task.isDone() : "a task that was just marked must read as done";
        undoActions.push(() -> setDone(task, wasDone));
        return task;
    }

    /**
     * Marks the task at the given position as incomplete.
     *
     * @param taskNumber Position of the task in the list, counting from one.
     * @return Task that was unmarked.
     * @throws SerangoonerException If no task holds that position.
     */
    public Task unmark(int taskNumber) {
        Task task = getTask(taskNumber, "UNMARK");
        boolean wasDone = task.isDone();
        task.markNotDone();
        assert !task.isDone() : "a task that was just unmarked must read as incomplete";
        undoActions.push(() -> setDone(task, wasDone));
        return task;
    }

    /**
     * Deletes the task at the given position.
     *
     * @param taskNumber Position of the task in the list, counting from one.
     * @return Task that was deleted.
     * @throws SerangoonerException If no task holds that position.
     */
    public Task delete(int taskNumber) {
        Task task = getTask(taskNumber, "DELETE");
        int taskCountBefore = tasks.size();
        tasks.remove(taskNumber - 1);
        undoActions.push(() -> tasks.add(taskNumber - 1, task));
        assert tasks.size() == taskCountBefore - 1 : "deleting must remove exactly one task";
        return task;
    }

    /**
     * Reverses the most recent edit made to the list.
     *
     * @return True if an edit was undone, false if there was nothing to undo.
     */
    public boolean undo() {
        if (undoActions.isEmpty()) {
            return false;
        }
        undoActions.pop().run();
        return true;
    }

    /**
     * Returns every task, each with the number it is known by.
     */
    public List<Entry> getEntries() {
        return filterEntries(task -> true);
    }

    /**
     * Returns the tasks falling on or between the given dates, each with the
     * number it is known by.
     *
     * @param start First date of the range, inclusive.
     * @param end Last date of the range, inclusive.
     * @return Matching tasks, in the order they are stored.
     */
    public List<Entry> getEntriesWithin(LocalDate start, LocalDate end) {
        assert !end.isBefore(start) : "a DateRange has already put the two dates in order";
        return filterEntries(task -> task.isWithin(start, end));
    }

    /**
     * Returns the tasks whose description carries the given keyword, each with
     * the number it is known by.
     * Case is ignored, so a keyword typed in any case finds the task.
     *
     * @param keyword Text to look for in each task description.
     * @return Matching tasks, in the order they are stored.
     */
    public List<Entry> getEntriesMatching(String keyword) {
        String lowerCaseKeyword = keyword.toLowerCase(Locale.ROOT);
        return filterEntries(task -> task.getDescription()
                .toLowerCase(Locale.ROOT).contains(lowerCaseKeyword));
    }

    /**
     * Returns the first task in the list that duplicates the given one, with
     * the number it is known by.
     *
     * @param task Task to look for a duplicate of.
     * @return Duplicate already in the list, or nothing if the task is new.
     */
    private Optional<Entry> findDuplicate(Task task) {
        return filterEntries(task::isDuplicateOf).stream().findFirst();
    }

    /**
     * Returns the tasks that the given filter accepts.
     * A task keeps the number it has in the full list, so that a number read
     * off any listing stays usable with mark, unmark and delete.
     *
     * @param filter Decides which tasks are returned.
     * @return Matching tasks, in the order they are stored.
     */
    private List<Entry> filterEntries(Predicate<Task> filter) {
        List<Entry> matches = IntStream.rangeClosed(1, tasks.size())
                .mapToObj(number -> new Entry(number, tasks.get(number - 1)))
                .filter(entry -> filter.test(entry.task()))
                .toList();
        assert matches.size() <= tasks.size() : "a filtered listing cannot outgrow the list";
        return matches;
    }

    /**
     * Returns the task at the given position.
     *
     * @param taskNumber Position of the task in the list, counting from one.
     * @param commandName Name of the command, for use in the error message.
     * @return Task holding that position.
     * @throws SerangoonerException If no task holds that position.
     */
    private Task getTask(int taskNumber, String commandName) {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new SerangoonerException(commandName + " FAILED. task number must be between 1 and "
                    + tasks.size());
        }
        return tasks.get(taskNumber - 1);
    }

    /**
     * Sets whether the given task is complete.
     *
     * @param task Task to update.
     * @param isDone True to mark the task as done, false to mark it as incomplete.
     */
    private static void setDone(Task task, boolean isDone) {
        if (isDone) {
            task.markDone();
        } else {
            task.markNotDone();
        }
    }

    /**
     * Represents a task together with the number the user knows it by.
     * The number is the task's position in the full list, so that a number
     * read off a filtered listing stays usable with mark, unmark and delete.
     *
     * @param number Position of the task in the full list, counting from one.
     * @param task Task at that position.
     */
    public record Entry(int number, Task task) { }
}
