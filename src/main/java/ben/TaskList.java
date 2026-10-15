package ben;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The in-memory list of tasks, with the add / delete / lookup operations
 * the rest of the program needs.
 * <p>
 * A-MoreOOP: pulled out of the main class so that "what the task list can
 * do" lives in one place. Task numbers used by commands are 1-based (as
 * the user sees them); this class validates them and throws
 * {@link BenException} for anything out of range.
 */
class TaskList {
    private final List<Task> tasks;

    /** Creates an empty list. */
    TaskList() {
        this.tasks = new ArrayList<>();
    }

    /** Creates a list pre-populated with the given tasks (e.g. loaded from disk). */
    TaskList(List<Task> initialTasks) {
        assert initialTasks != null : "initialTasks must not be null; pass an empty list instead";
        this.tasks = new ArrayList<>(initialTasks);
    }

    /**
     * Appends a task to the end of the list.
     *
     * @throws BenException if an identical task (same type, description and
     *     date/time fields) is already in the list
     */
    void add(Task task) throws BenException {
        if (tasks.stream().anyMatch(task::isDuplicateOf)) {
            throw new BenException("This task is already in your list:\n  " + task);
        }
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given 1-based position.
     *
     * @throws BenException if no task has that number
     */
    Task remove(int oneBasedIndex) throws BenException {
        return tasks.remove(checkIndex(oneBasedIndex) - 1);
    }

    /**
     * Returns the task at the given 1-based position.
     *
     * @throws BenException if no task has that number
     */
    Task get(int oneBasedIndex) throws BenException {
        return tasks.get(checkIndex(oneBasedIndex) - 1);
    }

    /** Returns the number of tasks in the list. */
    int size() {
        return tasks.size();
    }

    /** Returns whether the list has no tasks. */
    boolean isEmpty() {
        return tasks.isEmpty();
    }

    /** Returns the backing list, e.g. for saving or iterating. */
    List<Task> asList() {
        return tasks;
    }

    /**
     * Returns the tasks whose description contains {@code keyword}, in
     * their current order. Matching is case-insensitive. The returned list
     * is a copy, so the caller cannot alter the task list through it.
     */
    List<Task> find(String keyword) {
        String needle = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(needle))
                .collect(Collectors.toList());
    }

    private int checkIndex(int oneBasedIndex) throws BenException {
        if (oneBasedIndex < 1 || oneBasedIndex > tasks.size()) {
            throw new BenException("There is no task number " + oneBasedIndex + ".");
        }
        // Postcondition: callers rely on the returned index being safe to use
        // as tasks.get(result - 1) / tasks.remove(result - 1) right after.
        assert oneBasedIndex >= 1 && oneBasedIndex <= tasks.size();
        return oneBasedIndex;
    }
}
