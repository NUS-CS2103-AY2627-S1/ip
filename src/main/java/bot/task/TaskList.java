package bot.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Holds the current list of tasks and every operation that reads or
 * changes it (adding, removing, marking, and querying by date). Callers
 * work with task numbers and dates; they never touch the underlying
 * {@code List<Task>} directly.
 */
public class TaskList {
    private final List<Task> tasks;

    /** Starts with an empty task list, e.g. when there was nothing to load from disk. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /** Starts with the given tasks already in the list, e.g. ones just loaded from disk. */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task The task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /** Returns whether a task with the same type, description, and dates already exists. */
    public boolean containsEquivalent(Task candidate) {
        String candidateData = withoutDoneFlag(candidate);
        return tasks.stream().anyMatch(task -> withoutDoneFlag(task).equals(candidateData));
    }

    /** Removes the mutable completion flag before comparing task details. */
    private String withoutDoneFlag(Task task) {
        return task.toSaveFormat().replaceFirst(" \\| [01] \\| ", " | * | ");
    }

    /** Removes and returns the task at the given 0-based index. */
    public Task delete(int index) {
        assertValidIndex(index);
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given 0-based index.
     *
     * @param index The 0-based index of the task to return.
     * @return The task at that index.
     */
    public Task get(int index) {
        assertValidIndex(index);
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks currently in the list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Marks the task at the given 0-based index as done.
     *
     * @param index The 0-based index of the task to mark.
     */
    public void mark(int index) {
        assertValidIndex(index);
        tasks.get(index).markAsDone();
    }

    /**
     * Marks the task at the given 0-based index as not done.
     *
     * @param index The 0-based index of the task to unmark.
     */
    public void unmark(int index) {
        assertValidIndex(index);
        tasks.get(index).markAsNotDone();
    }

    /**
     * Asserts that {@code index} is a valid 0-based index into the task
     * list. Every caller that reaches get/mark/unmark/delete already went
     * through {@link bot.parser.Parser#parseTaskIndex}, which rejects an
     * out-of-range number with a user-facing "OOPS!!!" message before
     * TaskList is ever called - so this assertion documents that
     * precondition and should never fail. It's an assertion rather than a
     * thrown exception because an out-of-range index here would be a bug
     * in the caller, not something a user can trigger directly.
     */
    private void assertValidIndex(int index) {
        assert index >= 0 && index < tasks.size()
                : "index " + index + " is out of bounds for a task list of size " + tasks.size()
                        + " - the caller should have validated it first (e.g. via Parser.parseTaskIndex)";
    }

    /** The tasks occurring on the given date, in list order, for the {@code on} command. */
    public List<Task> tasksOn(LocalDate date) {
        return tasks.stream()
                .filter(task -> task.occursOn(date))
                .collect(Collectors.toList());
    }

    /**
     * Returns the tasks whose description contains {@code keyword},
     * matched case-insensitively, in list order, for the {@code find} command.
     */
    public List<Task> findByKeyword(String keyword) {
        String needle = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(needle))
                .collect(Collectors.toList());
    }

    /**
     * Reorders the task list in place for the {@code sort} command: Todos
     * first (in their existing relative order), then every Deadline and
     * Event merged together in ascending date order (a Deadline by its due
     * date, an Event by its start date). Tasks on the same date, or with no
     * date at all, keep their existing relative order, since the sort used
     * here is stable.
     */
    public void sort() {
        tasks.sort(Comparator.comparing(Task::getSortDate, Comparator.nullsFirst(Comparator.naturalOrder())));
    }

    /** A read-only view of every task in the list, in order - for {@code list} and for saving to disk. */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }
}
