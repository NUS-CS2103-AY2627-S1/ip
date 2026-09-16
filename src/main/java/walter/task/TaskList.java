package walter.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import walter.DukeException;

/**
 * Owns Walter's ordered task collection and enforces its capacity and index rules.
 */
public class TaskList {
    private static final int MAX_TASKS = 100;

    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list from tasks loaded by storage.
     *
     * @param tasks Initial tasks in display order.
     * @throws DukeException If the supplied list exceeds Walter's supported capacity.
     */
    public TaskList(List<Task> tasks) throws DukeException {
        assert tasks != null : "Loaded task collection should not be null";
        if (tasks.size() > MAX_TASKS) {
            throw new DukeException("Saved task list exceeds the supported capacity.");
        }
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds one task while enforcing Walter's existing capacity.
     *
     * @param task Task to append to the list.
     * @throws DukeException If the task duplicates an existing task or the task list is full.
     */
    public void add(Task task) throws DukeException {
        assert task != null : "Only a valid task should be added to the task list";
        rejectDuplicateTask(task);
        if (tasks.size() >= MAX_TASKS) {
            throw new DukeException("Task list is full.");
        }
        tasks.add(task);
    }

    /**
     * Rejects a task whose type and logical fields match an existing task. Descriptions and
     * free-text event timings are compared case-insensitively after insignificant whitespace is
     * collapsed. Completion status deliberately does not affect duplicate identity.
     *
     * @param candidate Task being considered for addition.
     * @throws DukeException If the candidate duplicates an existing task.
     */
    private void rejectDuplicateTask(Task candidate) throws DukeException {
        if (tasks.stream().anyMatch(existing -> isDuplicate(existing, candidate))) {
            throw new DukeException("That task already exists.");
        }
    }

    /**
     * Checks whether two tasks have the same type and duplicate-defining fields.
     */
    private static boolean isDuplicate(Task first, Task second) {
        if (first.getClass() != second.getClass()
                || !normalizeText(first.getDescription())
                        .equals(normalizeText(second.getDescription()))) {
            return false;
        }
        if (first instanceof Todo) {
            return true;
        }
        if (first instanceof Deadline firstDeadline && second instanceof Deadline secondDeadline) {
            return firstDeadline.getBy().equals(secondDeadline.getBy());
        }
        Event firstEvent = (Event) first;
        Event secondEvent = (Event) second;
        if (firstEvent.isAtFormat() != secondEvent.isAtFormat()) {
            return false;
        }
        if (firstEvent.isAtFormat()) {
            return normalizeText(firstEvent.getAt()).equals(normalizeText(secondEvent.getAt()));
        }
        return normalizeText(firstEvent.getFrom()).equals(normalizeText(secondEvent.getFrom()))
                && normalizeText(firstEvent.getTo()).equals(normalizeText(secondEvent.getTo()));
    }

    /**
     * Removes insignificant whitespace and case differences from user-entered identity fields.
     */
    private static String normalizeText(String text) {
        return text.strip().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    /**
     * Deletes and returns the task at a validated zero-based index.
     *
     * @param taskIndex Zero-based index of the task to delete.
     * @return Deleted task.
     * @throws DukeException If the index does not identify an existing task.
     */
    public Task delete(int taskIndex) throws DukeException {
        validateIndex(taskIndex);
        return tasks.remove(taskIndex);
    }

    /**
     * Restores a task removed by a command whose persistence step failed.
     *
     * @param taskIndex Original zero-based position of the task.
     * @param task Task to restore.
     */
    public void restoreDeletedTask(int taskIndex, Task task) {
        assert taskIndex >= 0 && taskIndex <= tasks.size()
                : "A deleted task should be restored at its original index";
        assert task != null : "A restored task should not be null";
        tasks.add(taskIndex, task);
    }

    /**
     * Marks and returns the task at a validated zero-based index.
     *
     * @param taskIndex Zero-based index of the task to mark as done.
     * @return Task whose status was changed.
     * @throws DukeException If the index is invalid or the task is already done.
     */
    public Task markAsDone(int taskIndex) throws DukeException {
        Task task = get(taskIndex);
        if (task.isDone()) {
            throw new DukeException("Task is already marked as done.");
        }
        task.markAsDone();
        return task;
    }

    /**
     * Unmarks and returns the task at a validated zero-based index.
     *
     * @param taskIndex Zero-based index of the task to mark as not done.
     * @return Task whose status was changed.
     * @throws DukeException If the index is invalid or the task is already not done.
     */
    public Task markAsNotDone(int taskIndex) throws DukeException {
        Task task = get(taskIndex);
        if (!task.isDone()) {
            throw new DukeException("Task is already not done.");
        }
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns the task at a validated zero-based index.
     *
     * @param taskIndex Zero-based index of the task to retrieve.
     * @return Task at the requested index.
     * @throws DukeException If the index does not identify an existing task.
     */
    public Task get(int taskIndex) throws DukeException {
        validateIndex(taskIndex);
        return tasks.get(taskIndex);
    }

    /**
     * Returns the number of stored tasks.
     *
     * @return Current task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a read-only snapshot for display or persistence.
     *
     * @return Unmodifiable snapshot preserving the current task order.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Returns matching Deadlines in their original task-list order.
     *
     * @param date Date for which deadlines are requested.
     * @return Deadlines due on the given date, preserving task-list order.
     */
    public List<Deadline> getDeadlinesOn(LocalDate date) {
        return tasks.stream()
                .filter(Deadline.class::isInstance)
                .map(Deadline.class::cast)
                .filter(deadline -> deadline.getBy().equals(date))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring case.
     *
     * @param keyword Text to match against task descriptions.
     * @return Matching tasks in their original list order.
     */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription()
                        .toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns zero-based indices of tasks whose descriptions contain the keyword.
     *
     * @param keyword Text to match against task descriptions.
     * @return Matching indices in task-list order.
     */
    public List<Integer> findMatchingIndices(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<Integer> matches = new ArrayList<>();
        for (int index = 0; index < tasks.size(); index++) {
            if (tasks.get(index).getDescription().toLowerCase(Locale.ROOT)
                    .contains(normalizedKeyword)) {
                matches.add(index);
            }
        }
        return matches;
    }

    /**
     * Rejects an index that does not identify an existing task.
     *
     * @param taskIndex Zero-based index to validate.
     * @throws DukeException If the index is negative or beyond the end of the task list.
     */
    private void validateIndex(int taskIndex) throws DukeException {
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new DukeException("Task number is out of range.");
        }
    }
}
