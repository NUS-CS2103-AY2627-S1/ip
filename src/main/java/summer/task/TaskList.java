package summer.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Predicate;

/**
 * Stores the tasks currently known to Summer as a managed list.
 */
public class TaskList {
    private final ArrayList<Task> tasks;
    private final int capacity;

    /**
     * Creates an empty task list with the given maximum number of tasks.
     *
     * @param capacity maximum number of tasks this list can store
     */
    public TaskList(int capacity) {
        // A non-positive capacity would make isFull() either always or never true,
        // breaking AddCommand. Every caller passes a fixed positive constant.
        assert capacity > 0 : "task list capacity must be positive, was " + capacity;

        this.tasks = new ArrayList<>();
        this.capacity = capacity;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        this.tasks.add(task);
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index zero-based position of the task
     * @return task at the given index
     */
    public Task get(int index) {
        // Callers (MarkCommand, UnmarkCommand, ...) always check hasTaskAt first,
        // so a valid index is a precondition of this method.
        assert hasTaskAt(index) : "no task at index " + index;

        return this.tasks.get(index);
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param index zero-based position of the task to remove
     * @return removed task
     */
    public Task delete(int index) {
        // DeleteCommand always checks hasTaskAt first, so a valid index is a
        // precondition of this method.
        assert hasTaskAt(index) : "no task at index " + index;

        return this.tasks.remove(index);
    }

    /**
     * Checks whether the given zero-based index refers to a stored task.
     *
     * @param index zero-based position to check
     * @return true if there is a task at the given index
     */
    public boolean hasTaskAt(int index) {
        return index >= 0 && index < this.tasks.size();
    }

    /**
     * Returns a numbered display of all tasks in the list.
     *
     * @return formatted task list ready to print
     */
    public String list() {
        return render(" Here are the tasks in your list:", task -> true);
    }

    /**
     * Returns a numbered display of tasks that occur on the given date.
     *
     * @param date date to filter tasks by
     * @return formatted list of tasks occurring on the given date, ready to print
     */
    public String listOn(LocalDate date) {
        return render(" Here are the tasks on " + date.format(Task.DATE_DISPLAY_FORMAT) + ":",
                task -> task.occursOn(date));
    }

    /**
     * Returns a numbered display of tasks whose description contains the keyword.
     *
     * @param keyword text to search for within task descriptions
     * @return formatted list of matching tasks, ready to print
     */
    public String find(String keyword) {
        return render(" Here are the matching tasks in your list:",
                task -> task.descriptionContains(keyword));
    }

    /**
     * Sorts the tasks in place by date, earliest first. Dated tasks
     * ({@code Deadline}, {@code Event}) come before undated ones
     * ({@code ToDo}); tasks that compare equal keep their existing order.
     *
     * @return a numbered display of the tasks in their new order, ready to print
     */
    public String sort() {
        // Optional has no natural ordering, so compare on a sentinel far-future
        // date for the undated tasks; this parks them after every dated task
        // while keeping the sort stable among themselves.
        LocalDate undated = LocalDate.MAX;
        this.tasks.sort(Comparator.comparing(task -> task.sortKey().orElse(undated)));

        return render(" Here are the tasks in your list, sorted:", task -> true);
    }

    /**
     * Builds a numbered, printable listing of the tasks accepted by the filter.
     *
     * @param header first line of the listing
     * @param filter decides which tasks are included
     * @return the header followed by one numbered line per matching task
     */
    private String render(String header, Predicate<Task> filter) {
        StringBuilder builder = new StringBuilder(header);
        String lineSeparator = System.lineSeparator();
        int count = 0;

        for (Task task : this.tasks) {
            if (filter.test(task)) {
                count++;
                builder.append(lineSeparator).append(" ").append(count).append(".").append(task);
            }
        }

        // The filter can only ever accept tasks that are in the list, so the
        // number rendered must never exceed the number stored.
        assert count <= this.tasks.size() : "rendered more tasks than are stored";

        return builder.toString();
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return number of stored tasks
     */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Checks whether this list has reached its capacity.
     *
     * @return true if no more tasks can be added
     */
    public boolean isFull() {
        return this.tasks.size() == this.capacity;
    }

}
