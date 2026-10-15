package luckynoslacky.luckytask;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Stores tasks together with their one-based task numbers and creates indexed
 * views for list and find results.
 */
public class TaskList {
    private final List<IndexedTask> indexedTasks;
    private final Optional<LocalDate> searchDate;

    /**
     * Creates a task list without a date-search context.
     */
    public TaskList() {
        this(null);
    }

    /**
     * Creates a task list with an optional date-search context.
     *
     * @param searchDate date used by the query, or {@code null} for a
     *                   non-date query
     */
    public TaskList(LocalDate searchDate) {
        this.indexedTasks = new ArrayList<>();
        this.searchDate = Optional.ofNullable(searchDate);
    }

    /**
     * Adds a task to the end of this task list.
     *
     * @param task task to add
     */
    public void addTask(Task task) {
        addIndexedTask(indexedTasks.size() + 1, task);
    }

    /**
     * Returns the number of tasks stored in this task list.
     *
     * @return number of stored tasks
     */
    public int size() {
        return indexedTasks.size();
    }

    /**
     * Returns a task identified by its one-based task number.
     *
     * @param taskNumber one-based task number
     * @return requested task
     * @throws IllegalArgumentException if the task number is invalid
     */
    public Task getTask(int taskNumber) {
        return indexedTasks.get(getTaskIndex(taskNumber)).task();
    }

    /**
     * Removes a task identified by its one-based task number.
     *
     * @param taskNumber one-based task number
     * @return removed task
     * @throws IllegalArgumentException if the task number is invalid
     */
    public Task removeTask(int taskNumber) {
        Task removedTask = indexedTasks.remove(getTaskIndex(taskNumber)).task();
        renumberTasks();
        return removedTask;
    }

    /**
     * Restores a task at its original one-based position.
     *
     * @param taskNumber original one-based task number
     * @param task task to restore
     * @throws IllegalArgumentException if the position or task is invalid
     */
    void insertTask(int taskNumber, Task task) {
        if (taskNumber <= 0 || taskNumber > indexedTasks.size() + 1) {
            throw new IllegalArgumentException("Invalid task number.");
        }

        indexedTasks.add(
                taskNumber - 1,
                new IndexedTask(taskNumber, task));
        renumberTasks();
    }

    /**
     * Replaces the task list with tasks loaded from storage.
     *
     * @param loadedTasks tasks to store and index
     * @throws IllegalArgumentException if the list or one of its tasks is null
     */
    public void replaceTasks(List<Task> loadedTasks) {
        if (loadedTasks == null) {
            throw new IllegalArgumentException("Tasks cannot be null.");
        }
        if (loadedTasks.stream().anyMatch(task -> task == null)) {
            throw new IllegalArgumentException("Task cannot be null.");
        }

        indexedTasks.clear();
        loadedTasks.forEach(this::addTask);
    }

    /**
     * Creates an indexed snapshot containing tasks that satisfy a matcher.
     * The original one-based task numbers are retained in the snapshot.
     *
     * @param searchDate date used by the query, or {@code null}
     * @param matcher predicate deciding which tasks to include
     * @return indexed task-list snapshot
     * @throws IllegalArgumentException if the matcher is null
     */
    public TaskList createView(
            LocalDate searchDate,
            Predicate<Task> matcher) {
        if (matcher == null) {
            throw new IllegalArgumentException("Matcher cannot be null.");
        }

        TaskList result = new TaskList(searchDate);
        indexedTasks.stream()
                .filter(indexedTask -> matcher.test(indexedTask.task()))
                .forEach(indexedTask -> result.addIndexedTask(
                        indexedTask.taskNumber(),
                        indexedTask.task()));
        return result;
    }

    /**
     * Returns the tasks in this list without exposing its mutable indexing.
     *
     * @return tasks in task-number order
     */
    public List<Task> getTasks() {
        return indexedTasks.stream().map(IndexedTask::task).toList();
    }

    /**
     * Returns immutable task views with their original one-based numbers.
     *
     * @return task views in their display order
     */
    public List<TaskView> getTaskViews() {
        return indexedTasks.stream()
                .map(indexedTask -> TaskView.fromTask(
                        indexedTask.taskNumber(), indexedTask.task()))
                .toList();
    }

    /**
     * Returns the numbered command-line representation of every task in this
     * list.
     *
     * @return numbered task text in display order
     */
    public String toDisplayString() {
        return indexedTasks.stream()
                .map(IndexedTask::toDisplayString)
                .collect(Collectors.joining("\n"));
    }

    /**
     * Returns whether this task list contains no matching tasks.
     *
     * @return true if no indexed tasks are stored
     */
    public boolean isEmpty() {
        return indexedTasks.isEmpty();
    }

    /**
     * Returns the date used by the query, if one was supplied.
     *
     * @return optional date-search context
     */
    public Optional<LocalDate> getSearchDate() {
        return searchDate;
    }

    /** Adds an indexed task without exposing indexing details to callers. */
    private void addIndexedTask(int taskNumber, Task task) {
        indexedTasks.add(new IndexedTask(taskNumber, task));
    }

    /** Renumbers the stored tasks sequentially after a collection mutation. */
    private void renumberTasks() {
        IntStream.range(0, indexedTasks.size())
                .forEach(index -> {
                    IndexedTask indexedTask = indexedTasks.get(index);
                    indexedTasks.set(
                            index,
                            new IndexedTask(
                                    index + 1,
                                    indexedTask.task()));
                });
    }

    /** Converts and validates a one-based task number into a list index. */
    private int getTaskIndex(int taskNumber) {
        int taskIndex = taskNumber - 1;
        if (taskIndex < 0 || taskIndex >= indexedTasks.size()) {
            throw new IllegalArgumentException("Invalid task number.");
        }
        return taskIndex;
    }

    /**
     * Stores a task together with its original one-based task number.
     *
     * @param taskNumber original task number
     * @param task task being represented
     */
    private record IndexedTask(int taskNumber, Task task) {
        /** Validates the invariant of an indexed task. */
        private IndexedTask {
            if (taskNumber <= 0) {
                throw new IllegalArgumentException(
                        "Task number must be positive.");
            }
            if (task == null) {
                throw new IllegalArgumentException("Task cannot be null.");
            }
        }

        /** Returns this indexed task's numbered command-line text. */
        private String toDisplayString() {
            return taskNumber + "." + task;
        }

    }
}
