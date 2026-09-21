package pdd.task;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import pdd.PDDException;

/**
 * Wraps the in-memory list of tasks, and owns the operations that mutate
 * or query it (add, delete, mark/unmark, index lookup). Keeping this
 * separate from {@link Storage} (which only reads/writes disk) and from
 * {@link Ui} (which only prints) means the task-number bounds check lives
 * next to the data it's protecting.
 */
public class TaskList {
    private final List<Task> tasks;

    /** Wraps the given list of tasks, e.g. one just loaded from {@code Storage}. */
    public TaskList(List<Task> tasks) {
        this.tasks = tasks;
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /** Returns the underlying tasks, e.g. for {@code Ui} to display or {@code Storage} to save. */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * Converts a 1-based task number typed by the user into a validated
     * 0-based index into this list.
     */
    public int toIndex(String args) throws PDDException {
        int number;
        try {
            number = Integer.parseInt(args);
        } catch (NumberFormatException e) {
            throw new PDDException("OOPS!!! Please provide a valid task number, e.g. mark 2");
        }
        if (number < 1 || number > tasks.size()) {
            throw new PDDException("OOPS!!! Task number " + number + " does not exist. "
                    + "You have " + tasks.size() + " task(s) in the list.");
        }
        int index = number - 1;
        assert index >= 0 && index < tasks.size() : "toIndex must return a valid 0-based index";
        return index;
    }

    /** Returns the task at the given 0-based index. */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "get() requires an index already validated by toIndex()";
        return tasks.get(index);
    }

    /** Appends the given task to the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /** Removes and returns the task at the given 0-based index. */
    public Task delete(int index) {
        assert index >= 0 && index < tasks.size() : "delete() requires an index already validated by toIndex()";
        return tasks.remove(index);
    }

    /** Returns the tasks whose description contains the given keyword (case-insensitive). */
    public List<Task> findMatching(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    /**
     * Sorts this list in place: tasks with a date (deadlines/events) first, in
     * chronological order, followed by tasks without a date (todos) in
     * alphabetical order of description.
     */
    public void sortTasks() {
        Comparator<Task> byDate = Comparator.comparing(
                task -> task.getSortDate().orElse(LocalDate.MAX));
        Comparator<Task> byDescription = Comparator.comparing(
                Task::getDescription, String.CASE_INSENSITIVE_ORDER);
        tasks.sort(byDate.thenComparing(byDescription));
    }
}
