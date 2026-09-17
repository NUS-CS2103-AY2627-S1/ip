package megatron.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import megatron.exception.TaskNotFoundException;

/**
 * Owns the tasks in the order in which they were added.
 */
public final class TaskList implements Iterable<Task> {
    private static final int MINIMUM_FUZZY_TERM_LENGTH = 3;

    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing a copy of the supplied tasks.
     *
     * @param initialTasks The tasks to copy.
     */
    public TaskList(List<Task> initialTasks) {
        tasks = new ArrayList<>(Objects.requireNonNull(initialTasks));
        assert tasks.stream().noneMatch(Objects::isNull)
                : "A task list must not contain null tasks.";
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return The number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task The task to add.
     */
    public void add(Task task) {
        Task nonNullTask = Objects.requireNonNull(task);
        tasks.add(nonNullTask);
        assert tasks.get(tasks.size() - 1) == nonNullTask
                : "A newly added task must be stored at the end of the list.";
    }

    /**
     * Returns the task at a one-based list number.
     *
     * @param taskNumber The one-based task number.
     * @return The selected task.
     * @throws TaskNotFoundException If the task number is outside the list.
     */
    public Task getTask(int taskNumber) throws TaskNotFoundException {
        return tasks.get(convertToIndex(taskNumber));
    }

    /**
     * Removes and returns the task at a one-based list number.
     *
     * @param taskNumber The one-based task number.
     * @return The removed task.
     * @throws TaskNotFoundException If the task number is outside the list.
     */
    public Task removeTask(int taskNumber) throws TaskNotFoundException {
        return tasks.remove(convertToIndex(taskNumber));
    }

    /**
     * Marks and returns the task at a one-based list number as done.
     *
     * @param taskNumber The one-based task number.
     * @return The marked task.
     * @throws TaskNotFoundException If the task number is outside the list.
     */
    public Task setDone(int taskNumber) throws TaskNotFoundException {
        Task task = getTask(taskNumber);
        task.markAsDone();
        return task;
    }

    /**
     * Marks and returns the task at a one-based list number as not done.
     *
     * @param taskNumber The one-based task number.
     * @return The unmarked task.
     * @throws TaskNotFoundException If the task number is outside the list.
     */
    public Task setNotDone(int taskNumber) throws TaskNotFoundException {
        Task task = getTask(taskNumber);
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns tasks whose descriptions match all search terms, in list order.
     *
     * <p>A term matches when it is a case-insensitive substring of the description
     * or differs by at most one character from a description word. Fuzzy matching
     * applies only to terms with at least three characters.</p>
     *
     * @param keyword The search text.
     * @return The matching tasks in list order.
     */
    public TaskList find(String keyword) {
        List<Task> matchingTasks = new ArrayList<>();
        for (TaskMatch match : findMatches(keyword)) {
            matchingTasks.add(match.task());
        }
        return new TaskList(matchingTasks);
    }

    /**
     * Returns matching tasks with their original task numbers, in list order.
     *
     * @param keyword The search text.
     * @return The numbered matching tasks in list order.
     */
    public List<TaskMatch> findMatches(String keyword) {
        Objects.requireNonNull(keyword);
        String[] searchTerms = keyword.trim().toLowerCase(Locale.ROOT).split("\\s+");
        List<TaskMatch> matches = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            String description = task.getDescription().toLowerCase(Locale.ROOT);
            if (doesMatchAllTerms(description, searchTerms)) {
                matches.add(new TaskMatch(i + 1, task));
            }
        }
        return List.copyOf(matches);
    }

    /**
     * Returns whether a description matches every search term.
     */
    private static boolean doesMatchAllTerms(String description, String[] searchTerms) {
        String[] descriptionWords = description.split("\\s+");
        for (String searchTerm : searchTerms) {
            if (!description.contains(searchTerm) && !hasFuzzyWordMatch(searchTerm, descriptionWords)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether a search term differs by at most one character from a description word.
     */
    private static boolean hasFuzzyWordMatch(String searchTerm, String[] descriptionWords) {
        if (searchTerm.length() < MINIMUM_FUZZY_TERM_LENGTH) {
            return false;
        }
        for (String descriptionWord : descriptionWords) {
            if (isWithinOneEdit(searchTerm, descriptionWord)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether two values have a Levenshtein distance of at most one.
     */
    private static boolean isWithinOneEdit(String first, String second) {
        if (Math.abs(first.length() - second.length()) > 1) {
            return false;
        }

        int firstIndex = 0;
        int secondIndex = 0;
        int edits = 0;
        while (firstIndex < first.length() && secondIndex < second.length()) {
            if (first.charAt(firstIndex) == second.charAt(secondIndex)) {
                firstIndex++;
                secondIndex++;
                continue;
            }

            edits++;
            if (edits > 1) {
                return false;
            }
            if (first.length() > second.length()) {
                firstIndex++;
            } else if (first.length() < second.length()) {
                secondIndex++;
            } else {
                firstIndex++;
                secondIndex++;
            }
        }

        if (firstIndex < first.length() || secondIndex < second.length()) {
            edits++;
        }
        return edits <= 1;
    }

    /**
     * Returns an iterator over the tasks in list order.
     */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }

    /**
     * Converts a one-based task number to an internal zero-based index.
     *
     * @param taskNumber The one-based task number.
     * @return The zero-based index.
     * @throws TaskNotFoundException If the task number is outside the list.
     */
    private int convertToIndex(int taskNumber) throws TaskNotFoundException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new TaskNotFoundException();
        }
        int index = taskNumber - 1;
        assert index >= 0 && index < tasks.size()
                : "A valid one-based task number must convert to a valid zero-based index.";
        return index;
    }
}
