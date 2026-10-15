package bob.task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Holds the tasks the user has told the chatbot about, in the order they were
 * added, together with the operations the chatbot performs on them.
 *
 * <p>This was a bare {@link ArrayList} field in {@link bob.Bob Bob}, reached into
 * directly by every command. Every one of them therefore had to know that the
 * tasks were kept in a list, that the list counts from 0 while the user counts
 * from 1, and how to walk it. Wrapping the list in a class of its own gives that
 * knowledge one home: a {@link bob.command.Command Command} now asks this class for what it wants,
 * and nothing outside it touches the list itself.
 *
 * <p>Positions matter here in a way they do not in an ordinary collection. The
 * user names a task by the number it has in
 * {@link bob.command.CommandWord#LIST CommandWord.LIST}, so a shortened listing
 * is only useful if it shows each task under that same number — a number
 * counting the matches could not be typed into {@code mark} or {@code delete}.
 * That is why {@link #findIndexes} and {@link #findIndexesSoonestFirst} return
 * positions in this list rather than the tasks they found: the position is the
 * part the user needs, and the task can be fetched with {@link #get}.
 *
 * <p>Nothing here validates what the user typed and nothing here prints. A task
 * number that names no task is refused by
 * {@link bob.command.TaskNumberCommand TaskNumberCommand}, which is the one that knows both the
 * number the user typed and how long this list is; this class assumes it is given
 * a position that exists, exactly as a plain list would.
 */
public class TaskList {

    /** A position no task is ever at, given as the one to skip when none is to be skipped. */
    private static final int NO_INDEX = -1;

    /**
     * The tasks themselves, in the order they were added.
     *
     * <p>This is an {@link ArrayList} rather than a fixed-size array. Since tasks
     * can be deleted as well as added, a plain array would mean growing it by hand
     * when it filled up, and shifting every later element down by one on every
     * deletion while tracking how many slots are still in use. An
     * {@code ArrayList} does all of that itself: {@code add} grows it,
     * {@code remove} closes the gap, and {@code size} always says how many tasks
     * there are, so there is no separate count that could drift out of step with
     * the contents.
     *
     * <p>Each element is a {@link Task}. Because {@link Todo}, {@link Deadline}
     * and {@link Event} are all subclasses of {@link Task}, one list of
     * {@code Task} can hold any mixture of the three, and the code that lists,
     * marks or deletes them needs to know only that each is a task.
     */
    private final ArrayList<Task> tasks;

    /** Creates an empty task list, which is what the chatbot starts a first run with. */
    public TaskList() {
        this(List.of());
    }

    /**
     * Creates a task list holding the given tasks, in the order given — used to
     * take up the tasks {@link bob.storage.Storage Storage} read back from the save file.
     *
     * <p>The tasks are copied into a list of this class's own rather than the
     * given list being kept and added to. The list handed in may be one that
     * cannot be added to at all, and keeping it would in any case leave whoever
     * passed it able to change this task list behind its back.
     *
     * @param tasks the tasks to start with.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list, so that the numbering already shown to
     * the user goes on meaning what it did and the new task takes the next number.
     *
     * @param task the task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes the task at one position and returns it, so the caller can show the
     * user which task is gone.
     *
     * <p>The tasks after it move up to fill the gap, so the numbers stay a run of
     * 1, 2, 3 with nothing missing. That renumbering is why the removed task is
     * returned rather than merely dropped: after a deletion the number the user
     * typed refers to a different task than it did before.
     *
     * @param index the task's position in this list, counting from 0.
     * @return the task that was removed.
     */
    public Task delete(int index) {
        assert index >= 0 && index < tasks.size()
                : "No task at position " + index + " to delete from a list of " + tasks.size();
        return tasks.remove(index);
    }

    /**
     * Puts a task in place of the one at a position, so that it takes over that
     * task's number rather than going to the end of the list.
     *
     * <p>This is how an edited task is stored: nothing else in the list moves, so
     * every number the user has already been shown still names the same task.
     *
     * @param index the position to replace, counting from 0.
     * @param task  the task to put there.
     */
    public void set(int index, Task task) {
        assert index >= 0 && index < tasks.size()
                : "No task at position " + index + " to replace in a list of " + tasks.size();
        tasks.set(index, task);
    }

    /**
     * Returns the task at one position.
     *
     * @param index the task's position in this list, counting from 0.
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size()
                : "No task at position " + index + " in a list of " + tasks.size();
        return tasks.get(index);
    }

    /** Returns how many tasks are in the list. */
    public int size() {
        return tasks.size();
    }

    /** Returns whether the user has no tasks at all. */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the tasks as a plain list, for {@link bob.storage.Storage Storage} to write out.
     *
     * <p>A copy is returned rather than the list itself, so that saving cannot
     * alter what is saved. {@code Storage} is given a {@code List<Task>} rather
     * than this class because writing tasks to a file needs nothing more than to
     * walk them in order, and asking for less is what keeps the two apart.
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Returns the positions of the tasks passing a test, in the order they appear
     * in the list.
     *
     * <p>Which tasks are wanted arrives as a {@link Predicate}: a question about a
     * task that can be passed to a method and asked there. Passing the test itself
     * is what lets this one method serve {@link bob.command.CommandWord#ON CommandWord.ON},
     * {@link bob.command.CommandWord#BEFORE CommandWord.BEFORE} and
     * {@link bob.command.CommandWord#AFTER CommandWord.AFTER} alike; the alternative — a flag saying which
     * command called, and a {@code switch} on it here — would put each command's
     * meaning somewhere other than in the command.
     *
     * <p>The stream runs over the positions rather than over the tasks, because a
     * stream of tasks would lose track of where each one sits, and the position
     * is the answer wanted.
     *
     * @param isWanted the test a task has to pass to be included.
     * @return the positions of the matching tasks, counting from 0, in a list that
     *         cannot be changed.
     */
    public List<Integer> findIndexes(Predicate<Task> isWanted) {
        return IntStream.range(0, tasks.size())
                .filter(i -> isWanted.test(tasks.get(i)))
                .boxed()
                .toList();
    }

    /**
     * Returns the positions of every task, in the order they were added — what
     * {@link bob.command.CommandWord#LIST CommandWord.LIST} shows.
     *
     * <p>Written in terms of {@link #findIndexes} with a test every task passes,
     * so that a full listing and a shortened one are produced the same way and
     * are shown by the same code.
     *
     * @return the positions of all the tasks, counting from 0.
     */
    public List<Integer> getAllIndexes() {
        return findIndexes(task -> true);
    }

    /**
     * Returns the positions of the unfinished tasks that carry a date, most urgent first.
     *
     * <p>A todo has no date, so it is left out rather than sorted to one end:
     * there is no answer to where an undated task belongs among dated ones.
     *
     * <p>A task already done is left out too, however soon its date. Nothing about it
     * is urgent any more, and listing it would push out an unfinished task that is.
     *
     * <p>The positions are sorted, not the tasks. Sorting the list itself would
     * answer the question just as well and quietly renumber the user's whole list
     * as a side effect.
     *
     * <p>The positions are sorted into a new list rather than in place, because
     * the list {@link #findIndexes} returns cannot be changed.
     *
     * @return the positions of the unfinished dated tasks, counting from 0, soonest
     *         first, in a list that cannot be changed.
     */
    public List<Integer> findIndexesSoonestFirst() {
        List<Integer> urgentIndexes = findIndexes(
                task -> !task.isDone() && task.getScheduledDate().isPresent());
        // orElseThrow cannot fire: only tasks that have a date are in this list.
        return urgentIndexes.stream()
                .sorted(Comparator.comparing(
                        index -> tasks.get(index).getScheduledDate().orElseThrow()))
                .toList();
    }

    /**
     * Returns the position of the first task in the list that is the same task as
     * {@code task}, in the sense of {@link Task#isSameTaskAs}, or an empty
     * {@link Optional} when there is none.
     *
     * <p>Used to refuse a task the list already holds. A position is returned rather
     * than a yes or no so that the user can be shown which task it matched: capitals
     * and spaces are ignored in matching, so that task may not look exactly like the
     * one they typed.
     *
     * @param task the task to look for.
     * @return the position of the first matching task, counting from 0.
     */
    public Optional<Integer> findDuplicate(Task task) {
        return findDuplicate(task, NO_INDEX);
    }

    /**
     * Returns the position of the first task in the list, other than the one at
     * {@code indexToSkip}, that is the same task as {@code task}.
     *
     * <p>This is for an edit, whose result is compared with the list while the task it
     * was made from is still in it. Skipping that task means an edit that changes only
     * the capitals of a description is not refused for matching the task it changes.
     *
     * @param task        the task to look for.
     * @param indexToSkip the position of the one task not to compare it with, counting from 0.
     * @return the position of the first other matching task, counting from 0.
     */
    public Optional<Integer> findDuplicate(Task task, int indexToSkip) {
        return findIndexes(existing -> existing.isSameTaskAs(task)).stream()
                .filter(index -> index != indexToSkip)
                .findFirst();
    }
}
