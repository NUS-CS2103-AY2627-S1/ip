package piplupbot.task;

import java.util.ArrayList;
import java.util.stream.Collectors;

import piplupbot.PiplupBotException;

// ACKNOWLEDGEMENTS: This Java file was written with the help of Claude.

/**
 * Represents the tasks the user has stored, in the order they were added until
 * a {@code sort} puts them into another one.
 *
 * <p>This class owns the collection itself and the one rule that goes with it:
 * the user names a task by the number {@code list} showed, counting from 1,
 * while an {@code ArrayList} counts from 0. Converting between the two, and
 * refusing a number that names no task, used to be spread over three command
 * handlers; keeping it here means there is one place that can get it wrong, and
 * a caller cannot reach past the guard by accident.</p>
 *
 * <p>An {@code ArrayList} is used underneath because it grows as tasks are added
 * and closes the gap itself when one is removed, so there is no fixed limit on
 * how many tasks fit and no separate count to keep in step with the contents:
 * {@link #size()} is always the truth.</p>
 *
 * <p>The list is declared as holding {@link Task} but actually holds
 * {@link Todo}, {@link Deadline} and {@link Event} objects. This is polymorphism
 * at work: the tasks are stored and printed through the shared {@code Task}
 * type, and each object's own {@code toString()} decides how it appears.</p>
 */
public class TaskList {
    /** The stored tasks, in the order the user sees them. */
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty list, for a first run or when the save file could not be
     * read.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a list holding the given tasks, e.g. the ones just read from disk.
     *
     * <p>The tasks are copied into a list of this object's own rather than the
     * given list being kept. That way nothing outside can still be holding a
     * reference through which the list could be changed behind this class's
     * back, which is the whole point of putting the collection in a class. With
     * a handful of tasks the copy costs nothing worth measuring.</p>
     *
     * @param initialTasks The tasks to start with, in the order to keep them.
     */
    public TaskList(ArrayList<Task> initialTasks) {
        this.tasks = new ArrayList<>(initialTasks);
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
     * Adds a task to the end of the list, unless the list already holds the same
     * task. It takes a {@code Task} rather than a description, so the same
     * method works for every kind of task without needing to know which one it
     * got.
     *
     * <p>Duplicates are refused here, rather than by the command that adds, for
     * the reason task numbers are checked here: this class owns the collection,
     * so a rule about what it may hold cannot be skipped by a caller that
     * forgets it. What counts as the same task is {@link Task#isDuplicateOf}'s
     * question.</p>
     *
     * <p>Only adding is checked. The tasks read from the save file go to the
     * constructor as they are, because a repeat there was typed into the file by
     * hand, and dropping it would lose whatever set the two copies apart -- one
     * of them being done, say. {@code delete} is how to remove it.</p>
     *
     * @param task The task to remember.
     * @throws PiplupBotException If the list already holds a task with the same
     *                            details, whether or not that one is done.
     */
    public void add(Task task) throws PiplupBotException {
        assert task != null : "A null task was added to the list";
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).isDuplicateOf(task)) {
                // Showing the stored task with its number, as list would, says
                // which one it is and what to type to act on it.
                throw new PiplupBotException("Pip... You already have this task in your list:",
                        "  " + toNumberedLine(i));
            }
        }
        tasks.add(task);
    }

    /**
     * Returns the task at the given position.
     *
     * @param taskNumber The task's position as shown by {@code list}, counting from 1.
     * @return The task at that position.
     * @throws PiplupBotException If no stored task has that number.
     */
    public Task get(int taskNumber) throws PiplupBotException {
        requireTaskNumber(taskNumber);
        return tasks.get(taskNumber - 1);
    }

    /**
     * Removes the task at the given position and hands it back.
     *
     * <p>{@code ArrayList.remove} shifts the tasks that followed it one place
     * towards the front, so the numbering stays contiguous: after deleting task
     * 3, the old task 4 becomes task 3. It also returns the task it removed,
     * which is what the caller's confirmation shows.</p>
     *
     * @param taskNumber The task's position as shown by {@code list}, counting from 1.
     * @return The task that was removed.
     * @throws PiplupBotException If no stored task has that number.
     */
    public Task remove(int taskNumber) throws PiplupBotException {
        requireTaskNumber(taskNumber);
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Rearranges the stored tasks into the order the given key and direction
     * describe.
     *
     * <p>The tasks are reordered in place rather than a sorted copy being handed
     * back, so the numbers {@link #toNumberedLines()} shows afterwards are the
     * ones {@link #get} and {@link #remove} take: a user who sorts and then
     * types {@code mark 2} marks the second row they were just shown. The price
     * is that the order the tasks were added in is kept nowhere afterwards, so
     * nothing can restore it.</p>
     *
     * <p>{@code List.sort} is stable -- tasks that compare equal keep the order
     * they were already in -- which is what lets one sort refine another:
     * sorting by date and then by done status leaves each group still in date
     * order. {@link SortKey} therefore needs no tie-breaking rule of its own.</p>
     *
     * @param key       What to order the tasks by.
     * @param direction Whether that order runs forwards or backwards.
     */
    public void sort(SortKey key, SortDirection direction) {
        tasks.sort(key.getComparator(direction));
    }

    /**
     * Returns the tasks as a plain list, for the callers that need to walk the
     * whole thing -- printing them all, or writing them all to disk.
     *
     * <p>A copy is returned rather than the list itself, so that adding to or
     * removing from what a caller gets back cannot change what is stored: the
     * only ways in are {@link #add} and {@link #remove}, which is what keeps
     * the numbering rule above in one place.</p>
     *
     * @return A copy of the stored tasks, in the order the user sees them.
     */
    public ArrayList<Task> asList() {
        return new ArrayList<>(tasks);
    }

    /**
     * Returns the tasks whose description contains the given text, in the order
     * they appear in this list.
     *
     * <p>The matches come back as a {@code TaskList} rather than as lines of
     * text, so the caller gets them numbered by the very method that numbers the
     * full list -- one numbering rule for both, instead of a second copy of it
     * living in whichever command does the searching.</p>
     *
     * <p>The list handed back holds the stored tasks themselves rather than
     * copies of them, but is a separate list, so adding to or removing from it
     * changes nothing here. That is what makes it safe to hand out: a search
     * result is something to read, and the only ways to change what is stored
     * remain {@link #add} and {@link #remove}.</p>
     *
     * <p>Whether a task matches is {@link Task#descriptionContains}'s question,
     * asked of each task in turn. This method decides only which tasks to ask
     * and in what order to keep the answers.</p>
     *
     * @param keyword The text to look for in each description.
     * @return The matching tasks, possibly none.
     */
    public TaskList find(String keyword) {
        return new TaskList(tasks.stream()
                .filter(task -> task.descriptionContains(keyword))
                .collect(Collectors.toCollection(ArrayList::new)));
    }

    /**
     * Returns one line per stored task, each numbered the way the user refers
     * to it, e.g. {@code "2.[D][ ] return book (by: Oct 15 2019 06:00 PM)"}.
     *
     * <p>The list numbers its own tasks so that the {@code + 1} which turns an
     * index into a task number sits beside the {@code - 1} in {@link #get} and
     * {@link #remove} that turns it back. They are two halves of one rule, and
     * keeping them in one class is what stops them drifting apart.</p>
     *
     * <p>Only the tasks are returned, without any heading above them: what to
     * call this list is the caller's decision, which is what lets the same lines
     * appear under different wording.</p>
     *
     * @return A line per task, in the order the user sees them; empty if there
     *         are no tasks.
     */
    public String[] toNumberedLines() {
        String[] lines = new String[tasks.size()];
        for (int i = 0; i < tasks.size(); i++) {
            lines[i] = toNumberedLine(i);
        }
        return lines;
    }

    /**
     * Returns one stored task with its number in front, e.g.
     * {@code "2.[T][ ] read book"}.
     * {@link #toNumberedLines()} and the duplicate check in {@link #add} both
     * show a task this way, so the layout is written once for the two.
     *
     * @param index Where the task is in the list, counting from 0.
     * @return The task's number, counting from 1, followed by the task itself.
     */
    private String toNumberedLine(int index) {
        return (index + 1) + "." + tasks.get(index);
    }

    /**
     * Checks that a number names one of the stored tasks.
     * {@code ArrayList} would throw {@code IndexOutOfBoundsException} on a bad
     * index anyway; checking first lets the bot explain the problem in its own
     * words instead of ending the conversation with a stack trace.
     *
     * @param taskNumber The task's position as shown by {@code list}, counting from 1.
     * @throws PiplupBotException If no stored task has that number.
     */
    private void requireTaskNumber(int taskNumber) throws PiplupBotException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new PiplupBotException("Pip... There is no task numbered " + taskNumber + ".");
        }
    }
}
