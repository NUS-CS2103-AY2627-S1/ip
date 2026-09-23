package sallman;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import sallman.task.Task;


/**
 * The tasks the user is tracking, with the operations that act on the list as
 * a whole.
 * <p>
 * Task numbers shown to the user start at 1, but every index taken or returned
 * here starts at 0. Converting between the two is the caller's job, so that
 * this class deals only in list positions.
 */
public class TaskList {

    /**
     * How many past states are remembered. Undo is expected to walk back over
     * a mistake, not over a whole session, and every remembered state holds a
     * copy of every task, so the list is capped.
     */
    private static final int HISTORY_LIMIT = 20;

    /** The tasks, in the order they were added unless the list has since been sorted. */
    private final List<Task> tasks;

    /** Past states of the list, most recent first. */
    private final Deque<List<Task>> history = new ArrayDeque<>();

    /** Creates an empty list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a list holding the given tasks, e.g. those just loaded from disk.
     *
     * @param tasks the tasks to start with
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes the task at the given position.
     *
     * @param index position of the task, counting from 0
     * @return the task that was removed
     */
    public Task remove(int index) {
        // Parser.parseTaskNumber has already rejected a number outside the
        // list, so an index arriving here out of range is a bug in the chatbot
        // rather than something the user typed.
        assert index >= 0 && index < tasks.size()
                : "removing index " + index + " from " + tasks.size() + " tasks";
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given position.
     *
     * @param index position of the task, counting from 0
     * @return the task there
     */
    public Task get(int index) {
        // Checked by the parser before the command runs, as with remove().
        assert index >= 0 && index < tasks.size()
                : "reading index " + index + " of " + tasks.size() + " tasks";
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
     * Returns where a task sits in the list.
     * <p>
     * The task is found by identity, not by equal contents, so that of two
     * identical tasks the right one is found.
     *
     * @param task a task in this list
     * @return its position, counting from 0, or -1 if it is not in the list
     */
    public int indexOf(Task task) {
        return IntStream.range(0, tasks.size())
                .filter(i -> tasks.get(i) == task)
                .findFirst()
                .orElse(-1);
    }

    /**
     * Returns the position of a task that duplicates the given one.
     *
     * @param task the task to look for
     * @return the position of the first matching task, counting from 0, or
     *         empty when the list holds nothing like it
     */
    public OptionalInt indexOfSameTask(Task task) {
        return IntStream.range(0, tasks.size())
                .filter(i -> tasks.get(i).isSameTask(task))
                .findFirst();
    }

    /**
     * Returns the tasks falling on the given date.
     * <p>
     * Each task decides for itself whether it falls on that date, so this does
     * not need to know which kinds of task carry dates.
     *
     * @param date the date being asked about
     * @return the matching tasks, in list order, possibly empty
     */
    public List<Task> tasksOn(LocalDate date) {
        return tasks.stream()
                .filter(task -> task.isOn(date))
                .toList();
    }

    /**
     * Returns the tasks whose description contains the given keyword.
     * <p>
     * Each task decides for itself whether it matches, so this does not need
     * to know how a description is stored.
     *
     * @param keyword the text being searched for
     * @return the matching tasks, in list order, possibly empty
     */
    public List<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.hasKeyword(keyword))
                .toList();
    }

    /**
     * Returns whether the tasks are already in the given order.
     * <p>
     * The sort is stable, so when this holds, sorting would leave every task
     * where it is.
     *
     * @param order how to compare one task with another
     * @return true if no task comes before one the order puts ahead of it
     */
    public boolean isSortedBy(Comparator<Task> order) {
        return IntStream.range(1, tasks.size())
                .allMatch(i -> order.compare(tasks.get(i - 1), tasks.get(i)) <= 0);
    }

    /**
     * Puts the tasks in the given order.
     * <p>
     * The sort is stable, so tasks the order cannot tell apart keep the order
     * they were already in, which after an earlier sort is that sort's order.
     *
     * @param order how to compare one task with another
     */
    public void sort(Comparator<Task> order) {
        tasks.sort(order);
    }

    /**
     * Remembers the current state, so that the change about to be made can be
     * undone.
     * <p>
     * Called by a command once it knows it is going to change something, so
     * that a command rejected for a bad task number does not leave a state
     * that undo would then restore to no effect.
     */
    public void saveSnapshot() {
        history.push(tasks.stream().map(Task::copy).toList());
        while (history.size() > HISTORY_LIMIT) {
            history.removeLast();
        }
    }

    /**
     * Restores the state from before the most recent remembered change.
     *
     * @throws SallmanException if nothing has been changed yet this session
     */
    public void undo() throws SallmanException {
        if (history.isEmpty()) {
            throw new SallmanException("I apologise, but there is nothing to undo.",
                    "As a large language model, I can only undo changes made since I started.");
        }
        List<Task> previousTasks = history.pop();
        tasks.clear();
        tasks.addAll(previousTasks);
    }

    /**
     * Returns how many changes can still be undone.
     *
     * @return the number of remembered states
     */
    public int getUndoCount() {
        return history.size();
    }

    /**
     * Returns the tasks as a plain list, for saving.
     *
     * @return a copy of the tasks, so callers cannot change the list through it
     */
    public List<Task> asList() {
        return new ArrayList<>(tasks);
    }
}
