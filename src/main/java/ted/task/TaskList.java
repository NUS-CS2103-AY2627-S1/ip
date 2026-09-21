package ted.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ted.TedException;

/**
 * The tasks Ted is keeping track of, in the order they were added.
 * <p>
 * Wrapping the list in its own class rather than passing an
 * {@code ArrayList<Task>} around means the rules about task numbers live in
 * one place: every lookup goes through {@link #requireValidIndex(int)}, so no
 * caller can reach past the end of the list.
 */
public class TaskList {
    /** The stored tasks. */
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list holding tasks that were already loaded, e.g. from disk.
     *
     * @param tasks the tasks to start with.
     */
    public TaskList(List<Task> tasks) {
        assert tasks.stream().allMatch(task -> task != null) : "Storage.load() never adds null";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to remember.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes the task at the given position.
     *
     * @param index zero-based position of the task.
     * @return the task that was removed.
     * @throws TedException if there is no task at that position.
     */
    public Task remove(int index) throws TedException {
        requireValidIndex(index);
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given position.
     *
     * @param index zero-based position of the task.
     * @return the task at that position.
     * @throws TedException if there is no task at that position.
     */
    public Task get(int index) throws TedException {
        requireValidIndex(index);
        return tasks.get(index);
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
     * @return {@code true} if the list is empty.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns whether the list already holds a task with the same details as
     * the given one.
     *
     * @param task the task about to be added.
     * @return {@code true} if a stored task is a duplicate of it.
     * @see Task#isDuplicateOf(Task)
     */
    public boolean hasDuplicateOf(Task task) {
        return tasks.stream().anyMatch(storedTask -> storedTask.isDuplicateOf(task));
    }

    /**
     * Returns the tasks whose description contains the given keyword.
     * <p>
     * The search ignores case, because a user looking for "book" should not
     * have to remember whether they typed "Book" when adding the task. Case is
     * folded with {@link Locale#ROOT} rather than the computer's own locale:
     * under Turkish rules, "FINISH" lowers to a word spelled with a dotless i,
     * which would never match "finish".
     *
     * @param keyword text to look for inside task descriptions.
     * @return the matching tasks, in the order they appear in this list.
     */
    public TaskList find(String keyword) {
        String lowerCaseKeyword = keyword.toLowerCase(Locale.ROOT);
        return new TaskList(tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(lowerCaseKeyword))
                .toList());
    }

    /**
     * Returns the tasks that have the given tag.
     * <p>
     * Only the exact tag counts, so {@code #fun} does not match a task tagged
     * {@code #funny}: a tag is a label the user chose, not text to search inside.
     *
     * @param tag the tag to look for.
     * @return the tasks with that tag, in the order they appear in this list.
     */
    public TaskList findByTag(Tag tag) {
        return new TaskList(tasks.stream()
                .filter(task -> task.hasTag(tag))
                .toList());
    }

    /**
     * Returns the number the user sees next to a task in the full list.
     * Tasks are matched by identity rather than by their details, so that
     * two tasks that look alike still get their own numbers.
     *
     * @param task a task held by this list.
     * @return the task's position, counting from 1.
     */
    public int getNumberOf(Task task) {
        // Task does not override equals, so indexOf compares identities.
        int index = tasks.indexOf(task);
        assert index >= 0 : "callers only ask about tasks taken from this list";
        return index + 1;
    }

    /**
     * Returns the tasks as a plain list, for code that only needs to read them.
     *
     * @return an unmodifiable copy of the tasks, in order, which later
     *         changes to this list do not affect.
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Rejects a task number that does not point at a stored task.
     * The message speaks in the 1-based numbers the user sees, not the 0-based
     * indices the list uses.
     *
     * @param index zero-based position the user asked for.
     * @throws TedException if there is no task at that position.
     */
    private void requireValidIndex(int index) throws TedException {
        int taskNumber = index + 1;
        if (tasks.isEmpty()) {
            throw new TedException("Your list is empty, so there's no task " + taskNumber + ". "
                    + "Add something first. Try: todo borrow book");
        }
        if (index < 0 || index >= tasks.size()) {
            // "Pick a number from 1 to 1" would read oddly for a list of one.
            String validNumbers = tasks.size() == 1
                    ? "You only have 1 task, so the only number that works is 1."
                    : "You have " + tasks.size() + ", so pick a number from 1 to " + tasks.size() + ".";
            throw new TedException("There's no task " + taskNumber + ", buddy. " + validNumbers);
        }
    }
}
