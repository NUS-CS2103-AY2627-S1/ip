package eve.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import eve.EveException;

/**
 * Holds the current list of tasks and the operations that act on it
 * (adding, deleting, looking one up by position, or finding the ones that
 * occur on a given date). Callers never see the underlying storage, so how
 * the list is represented internally can change without affecting them.
 */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list pre-populated with the given tasks, e.g. ones just
     * loaded from disk.
     *
     * @param tasks the initial tasks, in order.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given position.
     *
     * @param index the 0-based position of the task to remove.
     * @return the task that was removed.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given position.
     *
     * @param index the 0-based position of the task.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Returns how many tasks are in the list. */
    public int size() {
        return tasks.size();
    }

    /**
     * Validates a 1-based task number typed by the user against the
     * current list size, and converts it to a 0-based index.
     *
     * @param taskNumber the 1-based task number to validate.
     * @return the corresponding 0-based index.
     * @throws EveException if the number is out of range.
     */
    public int toIndex(int taskNumber) throws EveException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new EveException("Oops, there's no task number " + taskNumber + " in your list!");
        }
        int index = taskNumber - 1;
        // Postcondition this method promises every caller (MarkCommand, UnmarkCommand,
        // DeleteCommand): they can pass the result straight to get()/delete() without
        // re-checking bounds themselves. Guards against a future edit to the bounds
        // check above accidentally breaking that guarantee.
        assert index >= 0 && index < tasks.size() : "toIndex produced an out-of-bounds index: " + index;
        return index;
    }

    /**
     * Returns every task that occurs on the given date (see
     * {@link Task#occursOn}), in list order.
     *
     * @param date the date to check against.
     */
    public List<Task> occurringOn(LocalDate date) {
        return tasks.stream()
                .filter(task -> task.occursOn(date))
                .toList();
    }

    /**
     * Returns every task whose description contains the given keyword (see
     * {@link Task#matches}), in list order.
     *
     * @param keyword the text to search for.
     */
    public List<Task> matching(String keyword) {
        return tasks.stream()
                .filter(task -> task.matches(keyword))
                .toList();
    }

    /** Returns a read-only view of every task in the list, in order. */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns every task that has a schedule date (see
     * {@link Task#getScheduleDate}) -- deadlines and events, but not plain
     * to-dos -- sorted chronologically by that date.
     *
     * @return the dated tasks, earliest first.
     */
    public List<Task> schedule() {
        return tasks.stream()
                .filter(task -> task.getScheduleDate().isPresent())
                .sorted(Comparator.comparing(task -> task.getScheduleDate().get()))
                .toList();
    }
}
