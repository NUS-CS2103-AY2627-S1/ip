package thomas.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import thomas.ThomasException;

/**
 * The tasks the chatbot is keeping, and the operations over them.
 * <p>
 * This wraps an {@link ArrayList} rather than being one, so that the list can
 * only be changed through the operations below. That is what lets the
 * user-facing rules live here instead of being repeated by every caller: a task
 * number the user typed is checked against the list that actually exists before
 * anything indexes into it.
 * <p>
 * Two numbering schemes meet in this class, so each accessor says in its name
 * which one it takes. {@link #get(int)} takes a position counting from 0, as
 * {@code ArrayList} does, and is for walking the list. {@link #getByNumber(int)}
 * and {@link #deleteByNumber(int)} take the number the user sees, counting from
 * 1, and are for carrying out commands. Conversion between the two happens here
 * and nowhere else.
 */
public class TaskList {
    /**
     * The tasks, in the order they were added.
     * <p>
     * An {@code ArrayList} rather than a {@code Task[]}: it grows as tasks are
     * added, so there is no fixed ceiling to enforce, and removing closes the
     * gap left by a deleted task instead of leaving a hole to shuffle by hand.
     */
    private final ArrayList<Task> tasks;

    /** Creates an empty task list, as used on a first run. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list holding tasks that already exist, as loaded from the
     * save file.
     *
     * @param tasks The tasks to start with, in list order; taken over as-is.
     */
    public TaskList(ArrayList<Task> tasks) {
        // The list is taken over as-is and every method below reads it, so a
        // null here is a mistake in the caller that would surface much later,
        // as a NullPointerException from whichever operation ran first.
        assert tasks != null : "Cannot build a task list over a null ArrayList";
        this.tasks = tasks;
    }

    /**
     * Returns how many tasks are stored.
     *
     * @return The number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at a position, counting from 0.
     * <p>
     * For walking the whole list, where the position is already known to be
     * valid. A number that came from the user goes through
     * {@link #getByNumber(int)} instead, which checks it.
     *
     * @param index The position, from 0 to {@code size() - 1}.
     * @return The task at that position.
     */
    public Task get(int index) {
        // This method trusts its caller, so the range it documents is stated
        // here rather than checked: a position out of range is a mistake in
        // the calling loop, not something the user did.
        assert index >= 0 && index < tasks.size()
                : "Position " + index + " is outside a list of " + tasks.size() + " task(s)";
        return tasks.get(index);
    }

    /**
     * Adds a task to the end of the list, refusing one that is already there.
     * <p>
     * A task typed twice is far more often a slip -- the user forgot it was
     * already on the list, or pressed enter twice -- than a wish for two
     * copies, and two copies cost more than they give: marking one leaves the
     * other undone, and deleting one leaves the other looking like the same
     * chore still to do. So the second is refused, and the message says where
     * the first one sits so the user can go to it. What counts as the same
     * task is {@link Task#equals(Object)}'s to decide.
     * <p>
     * Checked here rather than in the command that adds, so there is no way to
     * build a list holding two of the same task through this method. Putting
     * a task back with {@link #insertByNumber(int, Task)} is not checked,
     * because that only ever replays a delete that really happened.
     *
     * @param task The task to store.
     * @throws ThomasException If the same task is already on the list.
     */
    public void add(Task task) throws ThomasException {
        // Only Parser builds tasks, and it either returns one or throws, so a
        // null arriving here means that contract has been broken. Caught now
        // rather than as a NullPointerException the next time the list is
        // printed, by which point what added it is no longer on the stack.
        assert task != null : "Cannot add a null task to the list";
        int existingPosition = tasks.indexOf(task);
        if (existingPosition >= 0) {
            throw new ThomasException("Bust my buffers! That wagon is already on my train, at number "
                    + (existingPosition + 1) + ":\n   " + tasks.get(existingPosition));
        }
        tasks.add(task);
    }

    /**
     * Checks a task number the user typed and turns it into a position.
     * <p>
     * The user counts tasks from 1 and the list counts from 0, so the number is
     * checked against the tasks that exist and then shifted down by one here --
     * the single place that conversion happens. The range is checked against
     * the current size rather than any capacity: a number past the end must be
     * reported, not passed to {@link ArrayList#get(int)}.
     *
     * @param taskNumber The number as the user typed it, counting from 1.
     * @return The matching position, counting from 0.
     * @throws ThomasException If no task carries that number.
     */
    private int requirePosition(int taskNumber) throws ThomasException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new ThomasException("There's no wagon " + taskNumber + " on my train! "
                    + "I'm only pulling " + tasks.size() + " wagon(s).");
        }
        int position = taskNumber - 1;
        // The guard above already forces this, so it cannot fail as the two
        // stand today. It is here as a check on that guard: this is the only
        // place the two numbering schemes meet, and loosening the range test
        // by one would otherwise show up as an IndexOutOfBoundsException from
        // inside ArrayList rather than here, where the conversion is.
        assert position >= 0 && position < tasks.size()
                : "Task number " + taskNumber + " converted to position " + position
                        + " in a list of " + tasks.size() + " task(s)";
        return position;
    }

    /**
     * Returns the task the user named by its number.
     *
     * @param taskNumber The number as the user typed it, counting from 1.
     * @return The task carrying that number.
     * @throws ThomasException If no task carries that number.
     */
    public Task getByNumber(int taskNumber) throws ThomasException {
        return tasks.get(requirePosition(taskNumber));
    }

    /**
     * Removes the task the user named by its number and returns it.
     * <p>
     * The removed task is returned so it can be shown back to the user. Removing
     * closes the gap: everything after it shifts down one, so the numbering
     * stays contiguous and the numbers the user sees never develop holes.
     *
     * @param taskNumber The number as the user typed it, counting from 1.
     * @return The task that was removed.
     * @throws ThomasException If no task carries that number.
     */
    public Task deleteByNumber(int taskNumber) throws ThomasException {
        return tasks.remove(requirePosition(taskNumber));
    }

    /**
     * Puts a task back at the number it used to carry.
     * <p>
     * The counterpart to {@link #deleteByNumber(int)}, for undoing one: the task
     * goes back where it was rather than on the end, and everything from that
     * number on shifts up one, so the numbering closes around it exactly as
     * deleting opened it.
     * <p>
     * Deliberately not routed through {@link #requirePosition(int)}, which stops
     * at the last task that exists. Putting back the task that used to be last
     * means reaching one past the end, so the range here is one wider. That is
     * also why nothing is thrown: this is reached only from an undo replaying a
     * delete that really happened, so a number outside the range is a mistake in
     * the code rather than anything the user did.
     *
     * @param taskNumber The number the task is to carry again, counting from 1,
     *                   at most one past the last task.
     * @param task The task to put back.
     */
    public void insertByNumber(int taskNumber, Task task) {
        assert task != null : "Cannot put a null task back into the list";
        assert taskNumber >= 1 && taskNumber <= tasks.size() + 1
                : "Task number " + taskNumber + " is outside a list of "
                        + tasks.size() + " task(s) to put a task back into";
        tasks.add(taskNumber - 1, task);
    }

    /**
     * Returns the positions of the tasks that fall on a given day.
     * <p>
     * Positions rather than the tasks themselves, because the caller shows each
     * match numbered by where it sits in the whole list, not by where it sits
     * among the matches: a number shown must be the number {@code mark} and
     * {@code delete} take. Handing back only the tasks would lose that, and
     * numbering the matches 1, 2, 3 would read more tidily and send the user to
     * the wrong task.
     * <p>
     * Which tasks match is decided by {@link Task#occursOn(LocalDate)}, so this
     * never asks a task what type it is: a new dated task type overrides that
     * method and is filtered correctly here without this loop changing.
     *
     * @param day The day being asked about.
     * @return The positions of the matching tasks, counting from 0, in list order.
     */
    public List<Integer> positionsOn(LocalDate day) {
        // A stream over the positions rather than over the tasks: it is the
        // position that is wanted, and streaming the tasks would lose it.
        return IntStream.range(0, tasks.size())
                .filter(position -> tasks.get(position).occursOn(day))
                .boxed()
                .toList();
    }

    /**
     * Returns the positions of the tasks whose description contains a keyword.
     * <p>
     * Positions rather than the tasks themselves, for the same reason as
     * {@link #positionsOn(LocalDate)}: the caller numbers each match by where it
     * sits in the whole list, so a number shown is the number {@code mark} and
     * {@code delete} take. Numbering the matches 1, 2, 3 would read more tidily
     * and send the user to the wrong task.
     * <p>
     * Which tasks match is {@link Task#matches(String)}'s to decide, so this
     * never reads a description itself.
     *
     * @param keyword the text to look for, as the user typed it
     * @return the positions of the matching tasks, counting from 0, in list order
     */
    public List<Integer> positionsMatching(String keyword) {
        return IntStream.range(0, tasks.size())
                .filter(position -> tasks.get(position).matches(keyword))
                .boxed()
                .toList();
    }
}
