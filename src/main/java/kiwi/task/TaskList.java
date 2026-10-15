package kiwi.task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Holds the in-memory list of tasks and supports add, delete, get, and mark operations.
 * Does not print messages or save to disk — callers handle UI and persistence.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list that wraps an existing list (e.g. loaded from storage).
     *
     * @param tasks tasks to manage; must not be {@code null}.
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "Storage.load() and callers always pass a list, never null";
        this.tasks = tasks;
    }

    /**
     * Adds one or more tasks to the end of the list, in the order given.
     *
     * @param tasksToAdd tasks to add.
     */
    public void add(Task... tasksToAdd) {
        assert tasksToAdd != null : "Vararg add is called with task objects from Parser";
        for (Task task : tasksToAdd) {
            assert task != null : "A parsed or loaded task is never null";
            tasks.add(task);
        }
    }

    /**
     * Inserts {@code task} at {@code index}, shifting later tasks to the right.
     *
     * @param index 0-based insertion position, from 0 to {@link #size()} inclusive.
     * @param task task to insert.
     */
    public void add(int index, Task task) {
        assert task != null : "A parsed or loaded task is never null";
        assert index >= 0 && index <= tasks.size() : "Insert index is within 0..size";
        tasks.add(index, task);
    }

    /**
     * Removes and returns the task at the given 0-based index.
     *
     * @param index 0-based position of the task to remove.
     * @return the removed task.
     */
    public Task delete(int index) {
        assert isValidIndex(index) : "Callers must check isValidIndex before delete";
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given 0-based index.
     *
     * @param index 0-based position.
     * @return the task at that position.
     */
    public Task get(int index) {
        assert isValidIndex(index) : "Callers must check isValidIndex, or loop 0..size-1";
        return tasks.get(index);
    }

    /**
     * Returns how many tasks are in the list.
     *
     * @return current size.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the underlying list for saving or display.
     *
     * @return the mutable list of tasks.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Marks the task at {@code index} as done.
     *
     * @param index 0-based position.
     */
    public void markDone(int index) {
        assert isValidIndex(index) : "Callers must check isValidIndex before markDone";
        tasks.get(index).markAsDone();
    }

    /**
     * Marks the task at {@code index} as not done.
     *
     * @param index 0-based position.
     */
    public void markNotDone(int index) {
        assert isValidIndex(index) : "Callers must check isValidIndex before markNotDone";
        tasks.get(index).markAsNotDone();
    }

    /**
     * Checks whether {@code index} is a valid 0-based position in this list.
     *
     * @param index candidate index.
     * @return {@code true} if the index is in range.
     */
    public boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    /**
     * Returns whether a task with the same details as {@code task} is already in this list.
     *
     * @param task candidate task.
     * @return {@code true} if a matching task exists.
     */
    public boolean containsSameDetails(Task task) {
        assert task != null : "Callers always pass a parsed or loaded task";
        return tasks.stream().anyMatch(existing -> existing.hasSameDetails(task));
    }

    /**
     * Returns a new list of tasks whose description contains {@code keyword}.
     * Matching is a case-sensitive substring search. This list is not modified.
     *
     * @param keyword text to look for in each task description
     * @return matching tasks, in the same order as this list
     */
    public TaskList find(String keyword) {
        assert keyword != null : "Parser already validates the find keyword";
        ArrayList<Task> matches = tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
        return new TaskList(matches);
    }

    /**
     * Finds the nearest work day on or after {@code startDate} with {@code hours} free.
     * Only events occupy the calendar (the whole work day for each day in their range).
     * Deadlines and to-dos do not block free time.
     *
     * @param hours length of the slot; must fit in one work day.
     * @param startDate first day to consider.
     * @return the earliest matching slot, or empty if none within {@link FreeSlot#SEARCH_DAYS}.
     */
    public Optional<FreeSlot> findNearestFreeSlot(int hours, LocalDate startDate) {
        assert hours >= 1 && hours <= FreeSlot.WORK_DAY_HOURS
                : "Parser already rejects hours that do not fit a work day";
        assert startDate != null : "FreeCommand always supplies a start date";
        for (int offset = 0; offset < FreeSlot.SEARCH_DAYS; offset++) {
            LocalDate date = startDate.plusDays(offset);
            if (!hasEventOn(date)) {
                LocalTime start = FreeSlot.WORK_DAY_START;
                LocalTime end = start.plusHours(hours);
                return Optional.of(new FreeSlot(date, start, end));
            }
        }
        return Optional.empty();
    }

    /**
     * Returns whether any event occupies {@code date}.
     */
    private boolean hasEventOn(LocalDate date) {
        return tasks.stream()
                .anyMatch(task -> task.getType() == TaskType.EVENT && task.occursOn(date));
    }
}
