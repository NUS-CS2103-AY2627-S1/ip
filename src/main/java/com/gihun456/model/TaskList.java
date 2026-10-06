package com.gihun456.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.gihun456.ErrorMessages;
import com.gihun456.GihunException;

/**
 * Encapsulates the in-memory list of tasks and the operations that act on it.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list initialised from an existing collection.
     *
     * @param tasks Existing tasks to copy into the list.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Creates a task list initialised from the given tasks.
     *
     * @param initialTasks Tasks to copy into the list.
     */
    public TaskList(Task... initialTasks) {
        this.tasks = new ArrayList<>(Arrays.asList(initialTasks));
    }

    /**
     * Adds a task to the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Adds multiple tasks to the list.
     *
     * @param tasksToAdd Tasks to add.
     */
    public void addAll(List<Task> tasksToAdd) {
        tasks.addAll(tasksToAdd);
    }

    /**
     * Adds the given tasks to the list.
     *
     * @param tasksToAdd Tasks to add.
     */
    public void addAll(Task... tasksToAdd) {
        tasks.addAll(Arrays.asList(tasksToAdd));
    }

    /**
     * Removes the task at the specified zero-based index.
     *
     * @param index Index of task to remove.
     * @return Removed task.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the specified index.
     *
     * @param index Zero-based task index.
     * @return Task at the given index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Converts a user-facing task number into a valid zero-based list index.
     *
     * @param input User input describing the task number.
     * @return Zero-based index for the task.
     * @throws GihunException If the task number is missing or invalid.
     */
    public int getValidIndex(String input) throws GihunException {
        if (input == null || input.trim().isEmpty()) {
            throw new GihunException(ErrorMessages.INVALID_TASK_NUMBER);
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            throw new GihunException(ErrorMessages.INVALID_TASK_NUMBER, e);
        }

        int zeroBasedIndex = taskNumber - 1;
        if (zeroBasedIndex < 0 || zeroBasedIndex >= tasks.size()) {
            throw new GihunException(ErrorMessages.INVALID_TASK_NUMBER);
        }

        return zeroBasedIndex;
    }

    /**
     * Retrieves tasks with description matching a given keyword.
     *
     * @param matchingKey User input with a keyword to find matches.
     * @return A list of tasks with matches to the given keyword.
     * @throws GihunException If the keyword is empty.
     */
    public List<Task> getMatchedTasks(String matchingKey) throws GihunException {
        if (matchingKey == null || matchingKey.trim().isEmpty()) {
            throw new GihunException(ErrorMessages.EMPTY_KEYWORD);
        }

        String normalizedKey = matchingKey.trim().toLowerCase();
        return tasks.stream()
                .filter(task -> task.getTaskName().toLowerCase().contains(normalizedKey))
                .collect(Collectors.toList());
    }

    /**
     * Marks a task as done.
     *
     * @param index Zero-based index of the task to mark.
     */
    public void markTask(int index) {
        assert index >= 0 && index < tasks.size()
                : "markTask requires an index validated against the task list";
        tasks.get(index).markAsDone();
    }

    /**
     * Marks a task as not done.
     *
     * @param index Zero-based index of the task to unmark.
     */
    public void unmarkTask(int index) {
        assert index >= 0 && index < tasks.size()
                : "unmarkTask requires an index validated against the task list";
        tasks.get(index).markAsNotDone();
    }

    /**
     * Checks whether the list is empty.
     *
     * @return True if there are no tasks in the list.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Current number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a snapshot of the current tasks in insertion order.
     *
     * @return List of tasks.
     */
    public List<Task> asList() {
        return new ArrayList<>(tasks);
    }

    /**
     * Finds incomplete dated tasks that clash with a candidate dated task.
     *
     * @param candidate New deadline or event to compare against the list.
     * @return Conflicting tasks in their original task-list order.
     */
    public List<Conflict> getConflicts(Task candidate) {
        assert candidate instanceof Deadline || candidate instanceof Event
                : "Conflict detection requires a deadline or event candidate";

        List<Conflict> conflicts = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task existing = tasks.get(i);
            if (!existing.isDone() && conflicts(candidate, existing)) {
                conflicts.add(new Conflict(i + 1, existing));
            }
        }
        return conflicts;
    }

    private boolean conflicts(Task first, Task second) {
        if (first instanceof Deadline firstDeadline && second instanceof Deadline secondDeadline) {
            return firstDeadline.getDueDate().equals(secondDeadline.getDueDate());
        }
        if (first instanceof Deadline firstDeadline && second instanceof Event secondEvent) {
            return isStrictlyInside(firstDeadline.getDueDate(), secondEvent);
        }
        if (first instanceof Event firstEvent && second instanceof Deadline secondDeadline) {
            return isStrictlyInside(secondDeadline.getDueDate(), firstEvent);
        }
        if (!(first instanceof Event) || !(second instanceof Event)) {
            return false;
        }
        Event firstEvent = (Event) first;
        Event secondEvent = (Event) second;
        if (firstEvent.getEndDate().isBefore(firstEvent.getStartDate())
                || secondEvent.getEndDate().isBefore(secondEvent.getStartDate())) {
            return false;
        }
        return firstEvent.getStartDate().isBefore(secondEvent.getEndDate())
                && secondEvent.getStartDate().isBefore(firstEvent.getEndDate());
    }

    private boolean isStrictlyInside(java.time.LocalDateTime timestamp, Event event) {
        if (event.getEndDate().isBefore(event.getStartDate())) {
            return false;
        }
        return event.getStartDate().isBefore(timestamp) && timestamp.isBefore(event.getEndDate());
    }

    /**
     * Associates a conflicting task with its original one-based task number.
     *
     * @param taskNumber Original task-list number.
     * @param task Conflicting task.
     */
    public record Conflict(int taskNumber, Task task) {
    }
}
