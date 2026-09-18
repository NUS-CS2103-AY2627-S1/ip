package daddy.task;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Stores the tasks managed by Daddy and provides list-level operations.
 */
public class TaskList implements Iterable<Task> {
    /** Marks the beginning of a weekday in which Daddy searches for free time. */
    private static final LocalTime WORK_DAY_START = LocalTime.of(9, 0);
    /** Marks the end of a weekday in which Daddy searches for free time. */
    private static final LocalTime WORK_DAY_END = LocalTime.of(18, 0);
    /** Stores tasks in the same order in which they are displayed to the user. */
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Adds one or more tasks to the end of the list in the supplied order.
     *
     * @param tasksToAdd the tasks to add
     * @throws AssertionError if the task array or any task is null
     */
    public void add(Task... tasksToAdd) {
        assert tasksToAdd != null : "tasks to add must not be null";
        for (Task task : tasksToAdd) {
            assert task != null : "task to add must not be null";
        }
        Collections.addAll(tasks, tasksToAdd);
    }

    /**
     * Inserts one task at a specific zero-based position.
     *
     * <p>This operation supports restoring a removed task when persistence fails.</p>
     *
     * @param index the position at which to restore the task
     * @param task the task to insert
     * @throws AssertionError if the index or task is invalid
     */
    public void insert(int index, Task task) {
        assert index >= 0 && index <= tasks.size() : "task insertion index must be in range";
        assert task != null : "task to insert must not be null";
        tasks.add(index, task);
    }

    /**
     * Returns whether a zero-based index identifies a task in this list.
     *
     * @param index the zero-based task index
     * @return whether the index is in range
     */
    public boolean hasTaskAt(int index) {
        return index >= 0 && index < tasks.size();
    }

    /**
     * Returns the task at a valid zero-based index.
     *
     * @param index the zero-based task index
     * @return the selected task
     * @throws AssertionError if the task index is out of range
     */
    public Task getTaskAt(int index) {
        assert hasTaskAt(index) : "task index must be in range";
        return tasks.get(index);
    }

    /**
     * Returns whether the list already contains a task with the same user-entered details.
     *
     * @param candidate the task details to find
     * @return whether an equivalent task already exists
     * @throws AssertionError if the candidate is null
     */
    public boolean containsEquivalentTask(Task candidate) {
        assert candidate != null : "candidate task must not be null";
        return tasks.stream().anyMatch(task -> task.hasSameDetailsAs(candidate));
    }

    /**
     * Marks the task at a zero-based index as done.
     *
     * @param index the zero-based task index
     * @return the task that was marked
     * @throws AssertionError if the task index is out of range
     */
    public Task markTaskAsDone(int index) {
        assert hasTaskAt(index) : "task index must be in range";
        Task task = tasks.get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task at a zero-based index as not done.
     *
     * @param index the zero-based task index
     * @return the task that was unmarked
     * @throws AssertionError if the task index is out of range
     */
    public Task markTaskAsNotDone(int index) {
        assert hasTaskAt(index) : "task index must be in range";
        Task task = tasks.get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index the zero-based task index
     * @return the removed task
     * @throws AssertionError if the task index is out of range
     */
    public Task removeTaskAt(int index) {
        assert hasTaskAt(index) : "task index must be in range";
        return tasks.remove(index);
    }

    /**
     * Returns the tasks that occur on a supplied date.
     *
     * @param date the date to match
     * @return an unmodifiable list of matching tasks in their list order
     * @throws AssertionError if the date is null
     */
    public List<Task> getTasksOccurringOn(LocalDate date) {
        assert date != null : "date must not be null";
        return tasks.stream()
                .filter(task -> task.occursOn(date))
                .toList();
    }

    /**
     * Returns tasks whose descriptions contain a keyword, ignoring letter case.
     *
     * @param keyword the text to find in task descriptions
     * @return an unmodifiable list of matching tasks in their list order
     * @throws AssertionError if the keyword is null
     */
    public List<Task> getTasksMatching(String keyword) {
        assert keyword != null : "keyword must not be null";
        String lowerCaseKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(lowerCaseKeyword))
                .toList();
    }

    /**
     * Finds the earliest weekday slot that can contain a requested duration during working hours.
     *
     * <p>Only events block time. Event intervals are treated as including their start but excluding their end,
     * so a free slot may begin at the exact time an event ends.</p>
     *
     * @param duration the required positive duration, no longer than the 09:00 to 18:00 working day
     * @param searchStart the earliest date and time at which the slot may begin
     * @return the start of the earliest matching slot
     * @throws AssertionError if the duration or search start violates the method contract
     */
    public LocalDateTime findEarliestFreeTime(Duration duration, LocalDateTime searchStart) {
        assert duration != null : "free-time duration must not be null";
        assert searchStart != null : "free-time search start must not be null";
        assert !duration.isZero() && !duration.isNegative() : "free-time duration must be positive";
        assert duration.compareTo(Duration.between(WORK_DAY_START, WORK_DAY_END)) <= 0
                : "free-time duration must fit within one working day";

        LocalDate date = searchStart.toLocalDate();
        while (true) {
            if (isWeekend(date)) {
                date = date.plusDays(1);
                continue;
            }

            LocalDateTime dayStart = date.atTime(WORK_DAY_START);
            LocalDateTime dayEnd = date.atTime(WORK_DAY_END);
            LocalDateTime candidate = searchStart.toLocalDate().equals(date) && searchStart.isAfter(dayStart)
                    ? searchStart : dayStart;
            if (!candidate.isBefore(dayEnd) || candidate.plus(duration).isAfter(dayEnd)) {
                date = date.plusDays(1);
                continue;
            }

            for (Event event : getEventsOverlapping(dayStart, dayEnd)) {
                LocalDateTime eventStart = event.getFrom().isBefore(dayStart) ? dayStart : event.getFrom();
                LocalDateTime eventEnd = event.getTo().isAfter(dayEnd) ? dayEnd : event.getTo();
                if (!eventEnd.isAfter(candidate)) {
                    continue;
                }
                if (eventStart.isAfter(candidate) && !candidate.plus(duration).isAfter(eventStart)) {
                    return candidate;
                }
                if (eventEnd.isAfter(candidate)) {
                    candidate = eventEnd;
                }
                if (candidate.plus(duration).isAfter(dayEnd)) {
                    break;
                }
            }

            if (!candidate.plus(duration).isAfter(dayEnd)) {
                return candidate;
            }
            date = date.plusDays(1);
        }
    }

    /**
     * Returns events that overlap one working day, ordered by their start times.
     *
     * @param dayStart the beginning of the working day
     * @param dayEnd the end of the working day
     * @return overlapping events ordered by start time
     */
    private List<Event> getEventsOverlapping(LocalDateTime dayStart, LocalDateTime dayEnd) {
        return tasks.stream()
                .filter(Event.class::isInstance)
                .map(Event.class::cast)
                .filter(event -> event.getTo().isAfter(dayStart) && event.getFrom().isBefore(dayEnd))
                .sorted(Comparator.comparing(Event::getFrom))
                .toList();
    }

    /**
     * Returns whether a date falls outside Daddy's Monday-to-Friday working week.
     *
     * @param date the date to check
     * @return whether the date is Saturday or Sunday
     */
    private boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return the task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an iterator for reading the tasks in their list order.
     *
     * @return an iterator over the tasks
     */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
