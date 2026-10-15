package chione.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import chione.ChioneException;

/**
 * The tasks Chione is keeping track of.
 *
 * <p>Wrapping the list rather than passing an {@link ArrayList} around gives the
 * collection somewhere to keep the operations that belong to it — searching by
 * date, for instance — instead of leaving them scattered through the code that
 * happens to need them. It also means the rest of the program never sees the
 * underlying list, so the choice of collection can change without anything else
 * having to change with it.
 */
public class TaskList {
    /** The tasks, in the order the user added them. */
    private final ArrayList<Task> tasks;

    /** Creates an empty list, as used when nothing has been saved yet. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a list holding tasks that have already been loaded.
     *
     * @param tasks the tasks to start with
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds tasks to the end of the list, in the order given.
     *
     * <p>Accepting any number of tasks costs a caller with one task nothing --
     * {@code add(task)} still reads the same -- and saves a caller with several
     * from writing the same line over and over. Loading a saved list is the one
     * case that stays separate: those tasks arrive as a list rather than as
     * arguments someone wrote out, so they go through the constructor.
     *
     * @param tasksToAdd the tasks to add
     */
    public void add(Task... tasksToAdd) {
        for (Task task : tasksToAdd) {
            // A null would go unnoticed until the list is shown or saved, long
            // after the mistake that put it here.
            assert task != null : "cannot add a null task";
        }
        Collections.addAll(tasks, tasksToAdd);
    }

    /**
     * Removes the task at the given position and hands it back, so that it can
     * still be shown to the user after it has left the list.
     *
     * @param index zero-based position of the task
     * @return the task that was removed
     * @throws ChioneException if there is no task at that position
     */
    public Task remove(int index) throws ChioneException {
        checkIndex(index);
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given position.
     *
     * @param index zero-based position of the task
     * @return the task found there
     * @throws ChioneException if there is no task at that position
     */
    public Task get(int index) throws ChioneException {
        checkIndex(index);
        return tasks.get(index);
    }

    /**
     * Refuses a position that is not in the list.
     *
     * <p>The list is the only thing that knows how long it is, so it is the right
     * place to answer this. Checking here also means the answer cannot drift out
     * of step with the list, however the number reaching it was arrived at.
     *
     * <p>The message counts from 1, because that is how the tasks were numbered
     * when the user saw them.
     *
     * @param index zero-based position to check
     * @throws ChioneException if there is no task at that position
     */
    private void checkIndex(int index) throws ChioneException {
        if (index < 0 || index >= tasks.size()) {
            throw new ChioneException("There is no task " + (index + 1) + ". "
                    + "Your list has " + tasks.size() + " task(s) right now.");
        }
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
     * Reports whether the list holds no tasks at all.
     *
     * @return {@code true} if there is nothing in the list
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the tasks falling on one particular day.
     *
     * <p>Each task is asked whether it falls on the day, so this search never has
     * to know what kinds of task exist. The answer is itself a {@code TaskList},
     * which lets the result be displayed by exactly the same code that displays
     * the full list.
     *
     * @param date the day being asked about
     * @return the matching tasks, in their original order
     */
    public TaskList findOn(LocalDate date) {
        return filter(task -> task.occursOn(date));
    }

    /**
     * Returns the tasks whose description contains the given word.
     *
     * <p>As with {@link #findOn}, each task answers for itself, so the search
     * works for any task type.
     *
     * @param keyword the word being searched for
     * @return the matching tasks, in their original order
     */
    public TaskList find(String keyword) {
        return filter(task -> task.hasKeyword(keyword));
    }

    /**
     * Reports whether the list already holds a repeat of the given task.
     *
     * <p>Each stored task is asked whether it is a repeat of the newcomer, so this
     * works for any task type without the list knowing what kinds exist.
     *
     * @param task the task about to be added
     * @return {@code true} if a task already in the list is a repeat of it
     */
    public boolean hasDuplicateOf(Task task) {
        return tasks.stream().anyMatch(stored -> stored.isDuplicateOf(task));
    }

    /**
     * Returns the tasks that answer yes to a question.
     *
     * <p>Searching by date and searching by word differ only in what is asked of
     * each task, so the walk through the list is written once here and the
     * question is handed in. {@link Predicate} is Java's name for "a test that
     * takes one thing and answers true or false"; the callers above supply one
     * as a lambda, which is shorthand for a whole class that only holds a test.
     *
     * <p>The tasks are gathered into an {@link ArrayList} rather than with
     * {@code toList()}, because the constructor this hands them to takes one, and
     * because a list this class owns has to stay modifiable.
     *
     * @param test what to ask of each task
     * @return the tasks that answered yes, in their original order
     */
    private TaskList filter(Predicate<Task> test) {
        return new TaskList(tasks.stream()
                .filter(test)
                .collect(Collectors.toCollection(ArrayList::new)));
    }

    /**
     * Returns the tasks as a plain list, for code that only needs to read through
     * them — saving them to disk, for instance.
     *
     * <p>The list handed back cannot be modified, so the only way to change what
     * this object holds remains the methods above.
     *
     * @return an unmodifiable view of the tasks
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }
}
