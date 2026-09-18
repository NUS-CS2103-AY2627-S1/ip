package cookie.task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/** Owns the ordered collection of tasks managed by Cookie. */
public class TaskList implements Iterable<Task> {
    /** Classifies temporal task values so values of different shapes remain comparable. */
    private enum TemporalValueType {
        /** A value containing a calendar date, with or without a time. */
        DATED,
        /** A value containing only a time of day. */
        TIME_ONLY,
        /** A task without a temporal value. */
        NONE
    }

    /** Holds the normalized temporal value used when sorting a task. */
    private record TemporalSortKey(TemporalValueType type, LocalDate date, LocalTime time) {
    }

    /** Associates a matching task with its one-based number in the full task list. */
    public record IndexedTask(int taskNumber, Task task) {
        /** Creates a valid one-based task reference. */
        public IndexedTask {
            assert taskNumber > 0 : "A displayed task number must be positive";
            assert task != null : "An indexed task must refer to a task";
        }
    }

    /** The tasks in their display and persistence order. */
    private final ArrayList<Task> tasks;

    /**
     * Creates a task list containing the supplied tasks in argument order.
     *
     * @param tasks The tasks to add to this list.
     */
    public TaskList(Task... tasks) {
        this(List.of(tasks));
    }

    /**
     * Creates a task list containing a copy of the supplied tasks.
     *
     * @param tasks The tasks to copy into this list.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "The initial task collection must not be null";
        assert tasks.stream().noneMatch(task -> task == null)
                : "The initial task collection must not contain null";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task The task to add.
     */
    public void add(Task task) {
        assert task != null : "A task list must not contain null";
        tasks.add(task);
    }

    /**
     * Inserts a task at the specified position.
     *
     * @param index The zero-based insertion position.
     * @param task The task to insert.
     */
    public void add(int index, Task task) {
        assert task != null : "A task list must not contain null";
        tasks.add(index, task);
    }

    /**
     * Marks the task at the specified position as done.
     *
     * @param index The zero-based task position.
     */
    public void mark(int index) {
        tasks.get(index).mark();
    }

    /**
     * Marks the task at the specified position as not done.
     *
     * @param index The zero-based task position.
     */
    public void unmark(int index) {
        tasks.get(index).unmark();
    }

    /**
     * Returns the task at the specified position.
     *
     * @param index The zero-based task position.
     * @return The task at the specified position.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Deletes and returns the task at the specified position.
     *
     * @param index The zero-based task position.
     * @return The deleted task.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return The number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether this list already contains a task with identical identifying details.
     *
     * @param task The candidate task.
     * @return {@code true} if an equivalent task is already present.
     */
    public boolean containsSameDetails(Task task) {
        assert task != null : "A candidate task must not be null";
        return tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task));
    }

    /**
     * Finds tasks whose descriptions contain the requested keyword.
     *
     * @param keyword The keyword to find.
     * @return The matching tasks with their original one-based task numbers.
     */
    public List<IndexedTask> find(String keyword) {
        return findMatching(task -> task.containsKeyword(keyword));
    }

    /**
     * Finds deadlines and events that occur on the requested date.
     *
     * @param date The date to check.
     * @return The matching tasks with their original one-based task numbers.
     */
    public List<IndexedTask> findOn(LocalDate date) {
        return findMatching(task -> task.occursOn(date));
    }

    /**
     * Returns a sorted snapshot of all tasks while preserving their original task numbers.
     *
     * <p>Date sorting compares deadlines by due value and events by start value. Tasks with
     * calendar dates precede time-only tasks, which precede tasks without temporal values.
     *
     * @param criterion The task property to compare.
     * @param direction The direction in which comparable values should appear.
     * @return An immutable sorted view containing original one-based task numbers.
     */
    public List<IndexedTask> getSortedView(SortCriterion criterion, SortDirection direction) {
        assert criterion != null : "A sort criterion must not be null";
        assert direction != null : "A sort direction must not be null";

        List<IndexedTask> indexedTasks = IntStream.range(0, tasks.size())
                .mapToObj(index -> new IndexedTask(index + 1, tasks.get(index)))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        Comparator<IndexedTask> comparator = switch (criterion) {
            case DESCRIPTION -> createDescriptionComparator(direction);
            case DATE -> createDateComparator(direction);
        };
        indexedTasks.sort(comparator);
        return List.copyOf(indexedTasks);
    }

    /** Creates a stable, case-insensitive comparator for task descriptions. */
    private Comparator<IndexedTask> createDescriptionComparator(SortDirection direction) {
        Comparator<IndexedTask> comparator = Comparator.comparing(
                indexedTask -> indexedTask.task().getDescription(),
                String.CASE_INSENSITIVE_ORDER);
        return direction == SortDirection.ASCENDING ? comparator : comparator.reversed();
    }

    /** Creates a comparator that keeps missing temporal value groups last in either direction. */
    private Comparator<IndexedTask> createDateComparator(SortDirection direction) {
        return (first, second) -> compareTemporalKeys(
                createTemporalSortKey(first.task()),
                createTemporalSortKey(second.task()),
                direction);
    }

    /** Returns a normalized temporal sort key for a deadline, event, or undated task. */
    private TemporalSortKey createTemporalSortKey(Task task) {
        DateTimeValue value;
        if (task instanceof Deadline deadline) {
            value = deadline.getBy();
        } else if (task instanceof Event event) {
            value = event.getStart();
        } else {
            return new TemporalSortKey(TemporalValueType.NONE, null, null);
        }

        if (value.getDate() != null) {
            LocalTime normalizedTime = value.getTime() == null ? LocalTime.MIN : value.getTime();
            return new TemporalSortKey(TemporalValueType.DATED, value.getDate(), normalizedTime);
        }
        return new TemporalSortKey(TemporalValueType.TIME_ONLY, null, value.getTime());
    }

    /** Compares normalized temporal keys without reversing their group priority. */
    private int compareTemporalKeys(TemporalSortKey first, TemporalSortKey second,
                                    SortDirection direction) {
        int typeComparison = first.type().compareTo(second.type());
        if (typeComparison != 0) {
            return typeComparison;
        }

        int valueComparison = switch (first.type()) {
            case DATED -> {
                int dateComparison = first.date().compareTo(second.date());
                yield dateComparison != 0
                        ? dateComparison
                        : first.time().compareTo(second.time());
            }
            case TIME_ONLY -> first.time().compareTo(second.time());
            case NONE -> 0;
        };
        return direction == SortDirection.ASCENDING ? valueComparison : -valueComparison;
    }

    /**
     * Returns tasks that satisfy a condition together with their one-based task numbers.
     *
     * @param condition The condition used to select tasks.
     * @return The matching indexed tasks.
     */
    private List<IndexedTask> findMatching(Predicate<Task> condition) {
        return IntStream.range(0, tasks.size())
                .filter(index -> condition.test(tasks.get(index)))
                .mapToObj(index -> new IndexedTask(index + 1, tasks.get(index)))
                .toList();
    }

    /**
     * Returns an iterator over tasks in their stored order.
     *
     * @return An iterator over the tasks.
     */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
