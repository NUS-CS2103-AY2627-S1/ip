package minty.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * Owns Minty's tasks and provides operations for querying and updating them.
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
     * Creates a task list containing tasks loaded from storage.
     *
     * @param tasks initial tasks.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
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
     * Removes and returns the task at a zero-based index.
     *
     * @param index zero-based task index.
     * @return removed task.
     */
    public Task delete(int index) {
        assert isValidIndex(index) : "Task index must be validated before deletion";
        return tasks.remove(index);
    }

    /**
     * Marks and returns the task at a zero-based index.
     *
     * @param index zero-based task index.
     * @return task that was marked.
     */
    public Task mark(int index) {
        assert isValidIndex(index) : "Task index must be validated before marking";
        Task task = tasks.get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Unmarks and returns the task at a zero-based index.
     *
     * @param index zero-based task index.
     * @return task that was unmarked.
     */
    public Task unmark(int index) {
        assert isValidIndex(index) : "Task index must be validated before unmarking";
        Task task = tasks.get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns the dated tasks occurring on a specified date.
     *
     * @param date date to search for.
     * @return matching deadlines and events in list order.
     */
    public ArrayList<Task> findOn(LocalDate date) {
        return tasks.stream()
                .filter(task -> task.occursOn(date))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns tasks whose descriptions contain a keyword.
     *
     * @param keyword keyword to search for.
     * @return matching tasks in list order.
     */
    public ArrayList<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.containsKeyword(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns reminders for today through six days later, keeping original task numbers.
     *
     * @param today first date in the reminder window.
     * @return matches ordered by date, then original list order.
     */
    public ArrayList<NumberedTask> findReminders(LocalDate today) {
        ArrayList<NumberedTask> matches = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (!task.isDone() && task.getReminderDate().filter(date ->
                    !date.isBefore(today) && date.toEpochDay() - today.toEpochDay() < 7).isPresent()) {
                matches.add(new NumberedTask(i + 1, task));
            }
        }
        matches.sort(Comparator.comparing(match -> match.task().getReminderDate().orElseThrow()));
        return matches;
    }

    /**
     * Associates a task with its current one-based number in the full list.
     *
     * @param number original task number.
     * @param task matching task.
     */
    public record NumberedTask(int number, Task task) {
    }

    /**
     * Returns whether an index identifies a task in this list.
     *
     * @param index zero-based task index.
     * @return {@code true} if the index is within the task list.
     */
    private boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    /**
     * Returns an iterator over the tasks in list order.
     *
     * @return task iterator.
     */
    @Override
    public Iterator<Task> iterator() {
        return Collections.unmodifiableList(tasks).iterator();
    }
}
