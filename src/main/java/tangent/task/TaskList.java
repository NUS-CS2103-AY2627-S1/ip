package tangent.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import tangent.exception.ErrorMessages;
import tangent.exception.TangentException;

/**
 * Owns the in-memory collection of tasks and its basic operations.
 */
public class TaskList {
    /** The task list containing the tasks currently held in memory. */
    private final List<Task> tasks;
    /** Original indexes represented by the most recent find result. */
    private List<Integer> displayedTaskIndexes;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
        this.displayedTaskIndexes = new ArrayList<>();
    }

    /** Creates a task list containing the supplied loaded tasks. */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "loaded task collection must exist";
        this.tasks = new ArrayList<>(tasks);
        this.displayedTaskIndexes = createIdentityIndexes();
    }

    /** Creates a task list containing the supplied tasks */
    public TaskList(Task... tasks) {
        assert tasks != null : "task varargs must exist";
        this.tasks = new ArrayList<>(List.of(tasks));
        this.displayedTaskIndexes = createIdentityIndexes();
    }

    /** Adds a task to the end of this list. */
    public void add(Task task) {
        assert task != null : "task list must not contain null tasks";
        tasks.add(task);
        resetDisplayedIndexes();
    }

    /** Adds a task at the specified 0-based index in this list. */
    public void add(int index, Task task) {
        assert task != null : "task list must not contain null tasks";
        tasks.add(index, task);
        resetDisplayedIndexes();
    }

    /** Removes and returns the final task in this list. */
    public Task removeLast() {
        Task removedTask = tasks.removeLast();
        resetDisplayedIndexes();
        return removedTask;
    }

    /** Returns the task at the specified 0-based index. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Validates every supplied zero-based index before a batch operation mutates this list. */
    public void validateIndexes(List<Integer> indexes) throws TangentException {
        for (int index : indexes) {
            if (index < 0 || index >= tasks.size()) {
                throw new TangentException(ErrorMessages.INVALID_TASK_INDEX_MESSAGE);
            }
        }
    }

    /** Converts indexes shown by the most recent find result to indexes in this task list. */
    public List<Integer> resolveDisplayedIndexes(List<Integer> indexes) throws TangentException {
        validateIndexesAgainst(indexes, displayedTaskIndexes.size());
        List<Integer> resolvedIndexes = indexes.stream().map(displayedTaskIndexes::get).toList();
        resetDisplayedIndexes();
        return resolvedIndexes;
    }

    /** Restores command indexes to refer to the complete task list. */
    public void resetDisplayedIndexes() {
        displayedTaskIndexes = createIdentityIndexes();
    }

    /** Returns selected tasks in their original task-list order. */
    public List<Task> getTasksAtIndexes(List<Integer> indexes) {
        List<Task> selectedTasks = new ArrayList<>();
        for (int index = 0; index < tasks.size(); index++) {
            if (indexes.contains(index)) {
                selectedTasks.add(tasks.get(index));
            }
        }
        return selectedTasks;
    }

    /** Removes selected tasks using their original 0-based indexes and returns them in original order. */
    public List<Task> removeAtIndexes(List<Integer> indexes) {
        List<Task> removedTasks = getTasksAtIndexes(indexes);
        List<Integer> indexesToRemove = new ArrayList<>(indexes);
        indexesToRemove.sort(Collections.reverseOrder());
        for (int index : indexesToRemove) {
            tasks.remove(index);
        }
        return removedTasks;
    }

    /** Restores tasks at their original zero-based indexes after a failed batch save. */
    public void restoreAtIndexes(List<Integer> indexes, List<Task> restoredTasks) {
        List<Integer> indexesToRestore = new ArrayList<>(indexes);
        indexesToRestore.sort(Integer::compareTo);
        for (int i = 0; i < indexesToRestore.size(); i++) {
            add(indexesToRestore.get(i), restoredTasks.get(i));
        }
    }

    /** Returns the number of tasks in this list. */
    public int size() {
        return tasks.size();
    }

    /** Returns whether this list contains no tasks. */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /** Returns a shallow copy of the current task list. */
    public List<Task> toList() {
        return new ArrayList<>(tasks);
    }

    /** Returns a list of tasks with descriptions containing the {@code keyword} (case-insensitive). */
    public TaskList find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<Integer> matchingIndexes = new ArrayList<>();
        for (int index = 0; index < tasks.size(); index++) {
            if (tasks.get(index).getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword)) {
                matchingIndexes.add(index);
            }
        }
        displayedTaskIndexes = matchingIndexes;
        List<Task> matchingTasks = matchingIndexes.stream().map(tasks::get).toList();
        return new TaskList(matchingTasks);
    }

    /** Validates indexes against a supplied display size. */
    private void validateIndexesAgainst(List<Integer> indexes, int displaySize) throws TangentException {
        for (int index : indexes) {
            if (index < 0 || index >= displaySize) {
                throw new TangentException(ErrorMessages.INVALID_TASK_INDEX_MESSAGE);
            }
        }
    }

    /** Creates zero-based indexes for every task in this list. */
    private List<Integer> createIdentityIndexes() {
        List<Integer> indexes = new ArrayList<>();
        for (int index = 0; index < tasks.size(); index++) {
            indexes.add(index);
        }
        return indexes;
    }
}
