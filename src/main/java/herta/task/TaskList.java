package herta.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Owns Herta's ordered collection of tasks and its task-related operations.
 */
public class TaskList implements Iterable<Task> {
    /** Maximum number of tasks accepted in one active or archived collection. */
    public static final int MAXIMUM_TASK_COUNT = 10_000;

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
     * @param initialTasks the tasks with which to initialize the list.
     */
    public TaskList(List<Task> initialTasks) {
        Objects.requireNonNull(initialTasks, "Initial tasks cannot be null.");
        tasks = new ArrayList<>();
        for (Task task : initialTasks) {
            add(task);
        }
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return the number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index the zero-based task index.
     * @return the task at the requested index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task the task to add.
     */
    public void add(Task task) {
        Objects.requireNonNull(task, "A task list cannot contain a null task.");
        if (tasks.size() >= MAXIMUM_TASK_COUNT) {
            throw new IllegalArgumentException("A task list cannot contain more than "
                    + MAXIMUM_TASK_COUNT + " tasks.");
        }
        tasks.add(task);
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index the zero-based task index.
     * @return the removed task.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Replaces this list's contents with a copy of another task list.
     *
     * @param replacement the task list whose contents should be copied.
     */
    public void replaceWith(TaskList replacement) {
        Objects.requireNonNull(replacement, "A replacement task list cannot be null.");
        TaskList validatedReplacement = new TaskList(replacement.asUnmodifiableList());
        tasks.clear();
        tasks.addAll(validatedReplacement.asUnmodifiableList());
    }

    /**
     * Marks a task as complete.
     *
     * @param index the zero-based task index.
     */
    public void markTask(int index) {
        updateTaskStatus(index, true);
    }

    /**
     * Marks a task as incomplete.
     *
     * @param index the zero-based task index.
     */
    public void unmarkTask(int index) {
        updateTaskStatus(index, false);
    }

    /**
     * Sets a task's completion status.
     *
     * @param index the zero-based task index.
     * @param shouldBeCompleted the completion status to apply.
     */
    private void updateTaskStatus(int index, boolean shouldBeCompleted) {
        Task task = get(index);
        if (shouldBeCompleted) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
    }

    /**
     * Restores a task's completion status after a failed persistence attempt.
     *
     * @param index the zero-based task index.
     * @param wasCompleted the status to restore.
     */
    public void restoreStatus(int index, boolean wasCompleted) {
        updateTaskStatus(index, wasCompleted);
    }

    /**
     * Returns the indices of tasks that satisfy a condition.
     *
     * @param matcher the condition a task must satisfy.
     * @return matching zero-based task indices in their current list order.
     */
    public List<Integer> findMatchingIndices(Predicate<Task> matcher) {
        Objects.requireNonNull(matcher, "A task matcher cannot be null.");
        return IntStream.range(0, tasks.size())
                .filter(index -> matcher.test(tasks.get(index)))
                .boxed()
                .collect(Collectors.toList());
    }

    /**
     * Returns task indices sorted by the supplied task comparator without
     * changing the stored order.
     *
     * @param comparator the comparator used to compare tasks.
     * @return zero-based task indices in sorted order.
     */
    public List<Integer> getSortedIndices(Comparator<Task> comparator) {
        Objects.requireNonNull(comparator, "A task comparator cannot be null.");
        List<Integer> sortedIndices = IntStream.range(0, tasks.size())
                .boxed()
                .sorted((first, second) -> comparator.compare(
                        tasks.get(first), tasks.get(second)))
                .collect(Collectors.toList());

        if (sortedIndices.size() != tasks.size()) {
            throw new IllegalStateException("Sorting did not retain every task.");
        }
        for (int index : sortedIndices) {
            if (index < 0 || index >= tasks.size()) {
                throw new IllegalStateException("Sorting produced an invalid task index.");
            }
        }
        return sortedIndices;
    }

    /**
     * Returns a read-only view of the tasks in their current order.
     *
     * @return an unmodifiable task-list view.
     */
    public List<Task> asUnmodifiableList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns a read-only iterator over the tasks in their current order.
     *
     * @return an unmodifiable task iterator.
     */
    @Override
    public Iterator<Task> iterator() {
        return asUnmodifiableList().iterator();
    }
}
