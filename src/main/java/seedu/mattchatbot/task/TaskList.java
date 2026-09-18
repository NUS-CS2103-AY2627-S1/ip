package seedu.mattchatbot.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import seedu.mattchatbot.Command;
import seedu.mattchatbot.MattChatBotException;

/**
 * The list of tasks, with the operations that change it.
 * <p>
 * Extracted so that the rules about the list -- what counts as a valid task
 * number, what it means for a task to fall on a date -- live with the list
 * itself, rather than being repeated by whoever happens to be using it.
 */
public class TaskList {

    /** The tasks, in the order they were added. */
    private final ArrayList<Task> tasks;

    /** Creates an empty list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a list holding the given tasks, e.g. those just loaded from disk.
     *
     * @param tasks the tasks to start with
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds tasks to the end of the list, in the order given.
     * <p>
     * Takes varargs rather than a single task because adding several at once
     * is common when a list is being set up, and a caller that has just one
     * still writes the same call as before.
     *
     * @param tasksToAdd the tasks to add
     */
    public void add(Task... tasksToAdd) {
        int sizeBefore = tasks.size();
        Collections.addAll(tasks, tasksToAdd);
        // Varargs makes it easy to add nothing by accident, e.g. by spreading
        // an empty array, so this states that a call is meant to add tasks
        // and that every one given is kept.
        assert tasks.size() == sizeBefore + tasksToAdd.length : "add keeps every task given";
    }

    /**
     * Removes the task at the given position.
     *
     * @param index the task's position, 0-based
     * @return the task that was removed
     * @throws MattChatBotException if there is no task at that position
     */
    public Task remove(int index) throws MattChatBotException {
        checkInRange(index);
        // checkInRange is the only guard between the user's task number and
        // ArrayList, so this records what it is relied on to have ruled out.
        assert index >= 0 && index < tasks.size() : "checkInRange rejects out-of-range indexes";
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given position.
     *
     * @param index the task's position, 0-based
     * @return the task there
     * @throws MattChatBotException if there is no task at that position
     */
    public Task get(int index) throws MattChatBotException {
        checkInRange(index);
        assert index >= 0 && index < tasks.size() : "checkInRange rejects out-of-range indexes";
        return tasks.get(index);
    }

    /**
     * Returns the tasks that fall on the given date.
     *
     * @param date the date being asked about
     * @return the matching tasks, in list order
     */
    public ArrayList<Task> getTasksOn(LocalDate date) {
        return findTasks(task -> task.isOn(date));
    }

    /**
     * Returns the tasks whose description contains the given keyword,
     * ignoring case.
     *
     * @param keyword the text to look for
     * @return the matching tasks, in list order
     */
    public ArrayList<Task> getTasksMatching(String keyword) {
        String wanted = keyword.toLowerCase();
        return findTasks(task -> task.getDescription().toLowerCase().contains(wanted));
    }

    /**
     * Returns whether the list already holds the same task.
     * <p>
     * Sameness is the task's own idea of it, so that what counts as a repeat
     * stays with the type that knows which of its parts matter.
     *
     * @param task the task being considered
     * @return true if an entry like it is already in the list
     */
    public boolean contains(Task task) {
        return tasks.stream().anyMatch(task::isSameTask);
    }

    /**
     * Returns the tasks the given test accepts, in list order.
     * <p>
     * Holds the one thing every search has in common -- walk the list, keep
     * what matches -- so that each search method is left saying only what it
     * counts as a match.
     *
     * @param isWanted decides whether a task belongs in the result
     * @return the matching tasks, in list order
     */
    private ArrayList<Task> findTasks(Predicate<Task> isWanted) {
        return tasks.stream()
                .filter(isWanted)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns every task, for saving or displaying.
     *
     * @return the tasks, in list order
     */
    public ArrayList<Task> asList() {
        return tasks;
    }

    /**
     * Returns how many tasks the list holds.
     *
     * @return the number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the list has no tasks.
     *
     * @return true if there are no tasks
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Rejects a position that no task occupies.
     * <p>
     * Checked here rather than by each caller, so that every command that
     * refers to a task by number reports the same thing for the same mistake.
     *
     * @param index the position to check, 0-based
     * @throws MattChatBotException if there is no task at that position
     */
    private void checkInRange(int index) throws MattChatBotException {
        if (tasks.isEmpty()) {
            throw new MattChatBotException(
                    "Your list is empty, so there is no entry " + (index + 1) + " yet.");
        }
        if (index < 0 || index >= tasks.size()) {
            throw new MattChatBotException("You have " + describeSize()
                    + ", so there is no entry " + (index + 1) + ". Type "
                    + Command.LIST.getKeyword() + " to review them.");
        }
    }

    /**
     * Returns the task count with the right singular or plural noun, e.g.
     * {@code 1 task} or {@code 4 tasks}.
     *
     * @return the count and its noun
     */
    public String describeSize() {
        return tasks.size() + (tasks.size() == 1 ? " entry" : " entries");
    }
}
