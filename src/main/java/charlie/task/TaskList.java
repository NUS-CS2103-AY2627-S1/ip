package charlie.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import charlie.exception.CharlieException;

/**
 * Owns Charlie's collection of tasks and provides operations on that collection.
 */
public class TaskList {
    /** Tasks in their current list order. */
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing tasks loaded from storage.
     *
     * @param initialTasks Tasks with which to initialize the list.
     * @throws CharlieException If the initial tasks contain duplicate details.
     */
    public TaskList(List<Task> initialTasks) {
        this.tasks = new ArrayList<>();
        for (Task task : initialTasks) {
            add(task);
        }
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     * @throws CharlieException If another task already has the same details.
     */
    public void add(Task task) {
        validateUnique(task, -1);
        tasks.add(task);
    }

    /**
     * Returns the task state that would result from adding a task.
     *
     * @param task Task to include in the proposed state.
     * @return Proposed tasks without changing this task list.
     * @throws CharlieException If another task already has the same details.
     */
    public List<Task> getTasksAfterAdding(Task task) {
        validateUnique(task, -1);
        List<Task> proposedTasks = new ArrayList<>(tasks);
        proposedTasks.add(task);
        return List.copyOf(proposedTasks);
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index Zero-based task index.
     * @return Selected task.
     * @throws CharlieException If the index does not identify an existing task.
     */
    public Task get(int index) {
        validateIndex(index);
        return tasks.get(index);
    }

    /**
     * Returns the task state that would result from replacing one task.
     *
     * @param index Zero-based index of the task to replace.
     * @param updatedTask Task to store at the selected index.
     * @return Proposed tasks without changing this task list.
     * @throws CharlieException If the index is invalid or another task has the updated details.
     */
    public List<Task> getTasksAfterReplacing(int index, Task updatedTask) {
        validateIndex(index);
        validateUnique(updatedTask, index);
        List<Task> proposedTasks = new ArrayList<>(tasks);
        proposedTasks.set(index, updatedTask);
        return List.copyOf(proposedTasks);
    }

    /**
     * Replaces the task at the given zero-based index.
     *
     * @param index Zero-based index of the task to replace.
     * @param updatedTask Task to store at the selected index.
     * @return Updated task.
     * @throws CharlieException If the index is invalid or another task has the updated details.
     */
    public Task replace(int index, Task updatedTask) {
        validateIndex(index);
        validateUnique(updatedTask, index);
        tasks.set(index, updatedTask);
        return updatedTask;
    }

    /**
     * Marks and returns the task at the given zero-based index as done.
     *
     * @param index Zero-based task index.
     * @return Marked task.
     * @throws CharlieException If the index does not identify an existing task.
     */
    public Task mark(int index) {
        validateIndex(index);
        Task task = tasks.get(index);
        task.markDone();
        return task;
    }

    /**
     * Marks and returns the task at the given zero-based index as not done.
     *
     * @param index Zero-based task index.
     * @return Unmarked task.
     * @throws CharlieException If the index does not identify an existing task.
     */
    public Task unmark(int index) {
        validateIndex(index);
        Task task = tasks.get(index);
        task.markUndone();
        return task;
    }

    /**
     * Deletes and returns the task at the given zero-based index.
     *
     * @param index Zero-based task index.
     * @return Deleted task.
     * @throws CharlieException If the index does not identify an existing task.
     */
    public Task delete(int index) {
        validateIndex(index);
        return tasks.remove(index);
    }

    /**
     * Returns the task state that would result from deleting a task.
     *
     * @param index Zero-based index of the task to omit.
     * @return Proposed tasks without changing this task list.
     * @throws CharlieException If the index does not identify an existing task.
     */
    public List<Task> getTasksAfterDeleting(int index) {
        validateIndex(index);
        List<Task> proposedTasks = new ArrayList<>(tasks);
        proposedTasks.remove(index);
        return List.copyOf(proposedTasks);
    }

    /**
     * Returns the task state that would result from changing one task's status.
     *
     * @param index Zero-based index of the task to copy with a new status.
     * @param isDone Completion status for the copied task.
     * @return Proposed tasks without changing this task list.
     * @throws CharlieException If the index does not identify an existing task.
     */
    public List<Task> getTasksAfterChangingStatus(int index, boolean isDone) {
        validateIndex(index);
        List<Task> proposedTasks = new ArrayList<>(tasks);
        proposedTasks.set(index, tasks.get(index).copyWithStatus(isDone));
        assert proposedTasks.get(index).isDone == isDone
                : "Copied task must have the requested completion status";
        return List.copyOf(proposedTasks);
    }

    /**
     * Verifies that a zero-based task index exists in the current list.
     *
     * @param index Zero-based task index to validate.
     * @throws CharlieException If the task list is empty or the index is outside it.
     */
    private void validateIndex(int index) {
        if (tasks.isEmpty()) {
            throw new CharlieException("There are no tasks in the list.");
        }
        if (index < 0 || index >= tasks.size()) {
            throw new CharlieException(
                    "Please enter a task number from 1 to " + tasks.size() + ".");
        }
    }

    /**
     * Verifies that a task's identifying details are unique in this list.
     *
     * @param candidate Task proposed for addition or replacement.
     * @param excludedIndex Existing index to ignore when replacing a task, or -1 when adding.
     * @throws CharlieException If another task already has the same details.
     */
    private void validateUnique(Task candidate, int excludedIndex) {
        for (int i = 0; i < tasks.size(); i++) {
            if (i != excludedIndex && tasks.get(i).hasSameDetails(candidate)) {
                throw new CharlieException("This task already exists in the list.");
            }
        }
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return Task count.
     */
    public int getSize() {
        return tasks.size();
    }

    /**
     * Returns a read-only snapshot suitable for saving or displaying.
     *
     * @return Current tasks in list order.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Finds deadlines and events that occur on the requested date.
     *
     * @param searchDate Date on which tasks must occur.
     * @return Matching tasks in their original list order.
     */
    public List<Task> findOnDate(LocalDate searchDate) {
        return tasks.stream()
                .filter(task -> occursOnDate(task, searchDate))
                .toList();
    }

    /**
     * Returns whether a deadline or event occurs on the requested date.
     *
     * @param task Task to check.
     * @param searchDate Date on which the task must occur.
     * @return True when the task is dated and occurs on the requested date.
     */
    private boolean occursOnDate(Task task, LocalDate searchDate) {
        if (task instanceof Deadline deadlineTask) {
            return deadlineTask.deadline.equals(searchDate);
        }
        if (task instanceof Event eventTask) {
            LocalDate startDate = eventTask.startDateTime.toLocalDate();
            LocalDate endDate = eventTask.endDateTime.toLocalDate();
            return !searchDate.isBefore(startDate) && !searchDate.isAfter(endDate);
        }
        return false;
    }

    /**
     * Finds tasks whose descriptions contain the requested keyword or phrase.
     *
     * @param keyword Keyword or phrase to search for using case-insensitive matching.
     * @return Matching tasks in their original list order.
     */
    public List<Task> findByKeyword(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.description.toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .toList();
    }
}
