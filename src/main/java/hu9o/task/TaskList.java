package hu9o.task;

import java.util.ArrayList;
import java.util.stream.Collectors;

import hu9o.errors.InvalidDeleteIndexException;
import hu9o.errors.InvalidMarkIndexException;
import hu9o.errors.InvalidUnmarkIndexException;

/**
 * The in-memory list of tasks for the current program run.
 *
 * <p>It extends {@link ArrayList} so callers can still iterate over it and read
 * its {@code size()}, while adding task-aware operations that validate a
 * one-based task number before touching the list. Bounds checking lives here
 * because only the list knows how many tasks it holds; turning raw command text
 * into that number is {@link hu9o.parser.Parser}'s job.
 */
public class TaskList extends ArrayList<Task> {
    /** Identifies this class version during serialization (inherited from {@link ArrayList}). */
    private static final long serialVersionUID = 1L;

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add.
     */
    public void addTask(Task task) {
        add(task);
    }

    /**
     * Removes and returns the task at the given one-based position.
     *
     * @param oneBasedIndex the task number shown to the user (1 = first task);
     *                      0 means the command could not be parsed.
     * @return the task that was removed.
     * @throws InvalidDeleteIndexException if no task has that number.
     */
    public Task deleteTask(int oneBasedIndex) throws InvalidDeleteIndexException {
        if (oneBasedIndex < 1 || oneBasedIndex > size()) {
            throw new InvalidDeleteIndexException();
        }
        return remove(oneBasedIndex - 1);
    }

    /**
     * Returns the tasks whose description contains the given keyword, matched
     * case-insensitively, in their current list order.
     *
     * @param keyword the text to search for.
     * @return the matching tasks; empty if none match.
     */
    public TaskList findTasks(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        // toCollection(TaskList::new) keeps the return type a TaskList rather
        // than a plain List, so callers still get the task-aware operations.
        return stream()
                .filter(task -> task.getTaskDescription().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toCollection(TaskList::new));
    }

    /**
     * Returns how many tasks in this list are marked done.
     *
     * @return the number of done tasks.
     */
    public long countDone() {
        return stream().filter(Task::isDone).count();
    }

    /**
     * Returns the task at the given one-based position for a mark or unmark command.
     *
     * @param oneBasedIndex  the task number shown to the user (1 = first task);
     *                       0 means the command could not be parsed.
     * @param isMarkCommand  {@code true} for {@code mark}, {@code false} for {@code unmark};
     *                       only changes which exception is thrown.
     * @return the task at that position.
     * @throws InvalidMarkIndexException   if the number is invalid and this is a mark command.
     * @throws InvalidUnmarkIndexException if the number is invalid and this is an unmark command.
     */
    public Task getTask(int oneBasedIndex, boolean isMarkCommand)
            throws InvalidMarkIndexException, InvalidUnmarkIndexException {
        if (oneBasedIndex < 1 || oneBasedIndex > size()) {
            if (isMarkCommand) {
                throw new InvalidMarkIndexException();
            }
            throw new InvalidUnmarkIndexException();
        }
        return get(oneBasedIndex - 1);
    }
}
