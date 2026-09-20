package axiom.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import axiom.AxiomException;

/**
 * Contains the list of tasks and supports operations to modify it.
 */
public class TaskList implements Iterable<Task> {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing a copy of the given collection.
     *
     * @param tasks Existing tasks to wrap (typically loaded from storage).
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "Task collection should not be null";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add. Must not be {@code null}.
     * @throws AxiomException If a task with the same details is already in the list.
     */
    public void add(Task task) throws AxiomException {
        // Parser constructs a concrete Task before Axiom calls add.
        assert task != null : "Cannot add a null task";
        if (containsSameDetails(task)) {
            throw new AxiomException("That task is already in your list.");
        }
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given index.
     *
     * @param index Zero-based index of the task to remove. Must be in range {@code [0, size())}.
     * @return The removed task.
     */
    public Task delete(int index) {
        // Parser.parseTaskNumber already rejected missing/out-of-range user input.
        assert isValidIndex(index) : "Delete index should be within list bounds";
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given index.
     *
     * @param index Zero-based index of the task. Must be in range {@code [0, size())}.
     * @return The task at that index.
     */
    public Task get(int index) {
        // Callers convert a Parser-validated 1-based number to a 0-based index.
        assert isValidIndex(index) : "Get index should be within list bounds";
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return The current list size.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Task> iterator() {
        return Collections.unmodifiableList(tasks).iterator();
    }

    /**
     * Marks the task at the given index as done.
     *
     * @param index Zero-based index of the task. Must be in range {@code [0, size())}.
     */
    public void markAsDone(int index) {
        assert isValidIndex(index) : "Mark index should be within list bounds";
        tasks.get(index).markAsDone();
        assert tasks.get(index).isDone() : "Task should be done after markAsDone";
    }

    /**
     * Marks the task at the given index as not done.
     *
     * @param index Zero-based index of the task. Must be in range {@code [0, size())}.
     */
    public void markAsNotDone(int index) {
        assert isValidIndex(index) : "Unmark index should be within list bounds";
        tasks.get(index).markAsNotDone();
        assert !tasks.get(index).isDone() : "Task should not be done after markAsNotDone";
    }

    /**
     * Returns whether {@code index} is a valid 0-based position in this list.
     *
     * @param index Candidate index.
     * @return {@code true} if the index is in range.
     */
    private boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    /**
     * Returns whether the list already contains a task with the same details as {@code task}.
     *
     * @param task Task to look for.
     * @return {@code true} if a matching task is present.
     */
    public boolean containsSameDetails(Task task) {
        return tasks.stream().anyMatch(existing -> existing.hasSameDetails(task));
    }

    /**
     * Sorts tasks in chronological order.
     * Deadlines use their {@code /by} time, events use their {@code /from} time, and tasks
     * without a date appear after dated tasks while keeping their relative order.
     */
    public void sortChronologically() {
        tasks.sort(Comparator.comparing(
                (Task task) -> task.getChronologicalDate().orElse(null),
                Comparator.nullsLast(Comparator.naturalOrder())));
    }

    /**
     * Returns the zero-based indexes of tasks whose description contains the keyword.
     *
     * @param keyword Keyword to search for (case-insensitive). Must not be empty.
     * @return Zero-based indexes of matching tasks, in list order.
     */
    public ArrayList<Integer> findMatchingIndexes(String keyword) {
        assert keyword != null && !keyword.isBlank() : "Find keyword should not be null or blank";
        String lowerKeyword = keyword.toLowerCase();
        return IntStream.range(0, tasks.size())
                .filter(i -> tasks.get(i).getDescription().toLowerCase().contains(lowerKeyword))
                .boxed()
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
