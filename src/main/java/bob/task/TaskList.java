package bob.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import bob.exception.BobException;

/**
 * Stores tasks entered by the user during the current chatbot run.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this(List.of());
    }

    /**
     * Creates a task list containing a copy of the supplied tasks.
     *
     * @param tasks initial tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks initial tasks.
     * @return task list containing the supplied tasks.
     */
    public static TaskList of(Task... tasks) {
        return new TaskList(List.of(tasks));
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns whether a task with the same user-visible details already exists.
     *
     * @param candidate task to compare with existing tasks.
     * @return true if an equivalent task is already present.
     */
    public boolean containsEquivalentTask(Task candidate) {
        return tasks.stream().anyMatch(task -> hasSameDetails(task, candidate));
    }

    /**
     * Compares task details, including dates for dated task types.
     *
     * @param first first task to compare.
     * @param second second task to compare.
     * @return true if both tasks have identical details.
     */
    private boolean hasSameDetails(Task first, Task second) {
        if (first == null || second == null || first.getType() != second.getType()
                || !first.getDescription().equals(second.getDescription())) {
            return false;
        }
        if (first instanceof Deadline firstDeadline && second instanceof Deadline secondDeadline) {
            return hasSameDateTime(firstDeadline.getBy(), secondDeadline.getBy());
        }
        if (first instanceof Event firstEvent && second instanceof Event secondEvent) {
            return hasSameDateTime(firstEvent.getFrom(), secondEvent.getFrom())
                    && hasSameDateTime(firstEvent.getTo(), secondEvent.getTo());
        }
        return true;
    }

    /** Returns whether two task date-times have identical date and optional time values. */
    private boolean hasSameDateTime(TaskDateTime first, TaskDateTime second) {
        return first.getDate().equals(second.getDate()) && first.getTime().equals(second.getTime());
    }

    /**
     * Returns a read-only view of the tasks.
     *
     * @return unmodifiable task list.
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index zero-based task index.
     * @return task at the index.
     * @throws BobException if the index is outside the list.
     */
    public Task get(int index) throws BobException {
        requireValidIndex(index);
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return task count.
     */
    public int getTaskCount() {
        return tasks.size();
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring letter case.
     *
     * @param keyword text to search for in task descriptions.
     * @return matching tasks in their original list order.
     */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream().filter(task -> task.getDescription().toLowerCase(Locale.ROOT)
                .contains(normalizedKeyword)).toList();
    }

    /**
     * Returns tasks whose descriptions contain every query term as a word prefix, ignoring letter case.
     *
     * @param query whitespace-separated word prefixes to search for.
     * @return matching tasks in their original list order.
     */
    public List<Task> search(String query) {
        List<String> terms = List.of(query.toLowerCase(Locale.ROOT).trim().split("\\s+"));
        return tasks.stream().filter(task -> matchesAllTerms(task, terms)).toList();
    }

    /**
     * Checks whether a task description contains a word beginning with every search term.
     *
     * @param task task whose description is checked.
     * @param terms normalized search terms.
     * @return true if every term matches a description word prefix.
     */
    private boolean matchesAllTerms(Task task, List<String> terms) {
        List<String> words = List.of(task.getDescription().toLowerCase(Locale.ROOT).split("\\s+"));
        return terms.stream().allMatch(term -> words.stream().anyMatch(word -> word.startsWith(term)));
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index zero-based task index.
     * @return removed task.
     * @throws BobException if the index is outside the list.
     */
    public Task remove(int index) throws BobException {
        requireValidIndex(index);
        return tasks.remove(index);
    }

    /**
     * Validates that an index identifies a task.
     *
     * @param index zero-based task index to validate.
     * @throws BobException if the index is outside the list.
     */
    private void requireValidIndex(int index) throws BobException {
        if (!isValidIndex(index)) {
            throw new BobException("That task number does not exist. Use 'list' to see the available tasks.");
        }
    }

    /**
     * Checks whether an index identifies a task in the list.
     *
     * @param index zero-based index to check.
     * @return true if the index is valid.
     */
    private boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }
}
