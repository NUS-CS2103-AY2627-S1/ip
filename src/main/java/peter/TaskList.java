package peter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents the current list of tasks and the operations that can be
 * performed on it. Contains no console I/O and no parsing logic.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates a task list pre-populated with tasks, typically loaded from storage.
     *
     * @param loadedTasks the tasks to start with
     */
    public TaskList(List<Task> loadedTasks) {
        assert loadedTasks != null : "loaded task list should not be null";
        this.tasks = new ArrayList<>(loadedTasks);
    }

    /**
     * Creates a task list pre-populated with the given tasks, in argument order.
     *
     * @param tasks the tasks to start with
     */
    public TaskList(Task... tasks) {
        this.tasks = new ArrayList<>(List.of(tasks));
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     * @throws PeterException if an equal task (same type, description, and dates) is already in the list
     */
    public void add(Task task) throws PeterException {
        if (tasks.contains(task)) {
            throw new PeterException("That one's already stuck to your web, pal - no need to web it twice.");
        }
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param index the zero-based task index
     * @return the removed task
     * @throws PeterException if the index is out of range
     */
    public Task delete(int index) throws PeterException {
        validateIndex(index);
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index the zero-based task index
     * @return the task at that index
     * @throws PeterException if the index is out of range
     */
    public Task get(int index) throws PeterException {
        validateIndex(index);
        return tasks.get(index);
    }

    /**
     * Marks the task at the given zero-based index as completed.
     *
     * @param index the zero-based task index
     * @return the marked task
     * @throws PeterException if the index is out of range
     */
    public Task mark(int index) throws PeterException {
        Task task = get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task at the given zero-based index as incomplete.
     *
     * @param index the zero-based task index
     * @return the unmarked task
     * @throws PeterException if the index is out of range
     */
    public Task unmark(int index) throws PeterException {
        Task task = get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns the number of tasks currently in the list.
     *
     * @return the task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns all tasks in the list.
     *
     * @return the underlying list of tasks
     */
    public ArrayList<Task> getAll() {
        return tasks;
    }

    /**
     * Returns the schedule for the given date: deadlines due that day,
     * sorted by due time, followed by events starting that day, sorted by
     * start time.
     *
     * @param date the date to match against
     * @return the deadlines due, then the events starting, on that date
     */
    public List<Task> getScheduleForDate(LocalDate date) {
        List<Deadline> deadlinesOnDate = tasks.stream()
                .filter(task -> task instanceof Deadline)
                .map(task -> (Deadline) task)
                .filter(deadline -> deadline.getBy().toLocalDate().equals(date))
                .sorted(Comparator.comparing(Deadline::getBy))
                .collect(Collectors.toList());
        List<Event> eventsOnDate = tasks.stream()
                .filter(task -> task instanceof Event)
                .map(task -> (Event) task)
                .filter(event -> event.getFrom().toLocalDate().equals(date))
                .sorted(Comparator.comparing(Event::getFrom))
                .collect(Collectors.toList());
        return Stream.<Task>concat(deadlinesOnDate.stream(), eventsOnDate.stream())
                .collect(Collectors.toList());
    }

    /**
     * Returns the tasks whose description contains the given keyword.
     *
     * @param keyword the search term to match against task descriptions
     * @return the matching tasks
     */
    public List<Task> findTasks(String keyword) {
        return tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .collect(Collectors.toList());
    }

    /**
     * Checks that a zero-based index refers to an existing task.
     *
     * @param index the zero-based task index
     * @throws PeterException if the index is out of range
     */
    private void validateIndex(int index) throws PeterException {
        if (index < 0 || index >= tasks.size()) {
            throw new PeterException("That task's not stuck to my web, pal - check the number and try again.");
        }
    }
}
