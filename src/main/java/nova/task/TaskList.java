package nova.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.stream.Collectors;

import nova.exception.NovaException;

/** Owns the tasks currently managed by Nova. */
public class TaskList implements Iterable<Task> {
    /** The tasks in their user-visible order. */
    private final ArrayList<Task> tasks = new ArrayList<>();

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /** Inserts a task at the specified zero-based position. */
    public void add(int index, Task task) {
        tasks.add(index, task);
    }

    /** Adds a task unless a task with the same identity is already present. */
    public void addIfNotDuplicate(Task task) throws NovaException {
        boolean isDuplicate = tasks.stream().anyMatch(existingTask -> existingTask.hasSameIdentity(task));
        if (isDuplicate) {
            throw new NovaException("A task with the same details already exists.");
        }
        tasks.add(task);
    }

    /** Removes and returns the task at the specified zero-based position. */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /** Returns the task at the specified zero-based position. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Returns the number of tasks. */
    public int size() {
        return tasks.size();
    }

    /** Returns the task at a one-based user-facing position. */
    public Task getByNumber(int taskNumber) throws NovaException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new NovaException("Please provide a valid task number.");
        }
        return tasks.get(taskNumber - 1);
    }

    /** Marks the task at a one-based user-facing position as done. */
    public void markAsDone(int taskNumber) throws NovaException {
        getByNumber(taskNumber).markAsDone();
    }

    /** Marks the task at a one-based user-facing position as not done. */
    public void markAsNotDone(int taskNumber) throws NovaException {
        getByNumber(taskNumber).markAsNotDone();
    }

    /** Adds a tag to the task at a one-based user-facing position. */
    public boolean addTag(int taskNumber, String tag) throws NovaException {
        return getByNumber(taskNumber).addTag(tag);
    }

    /** Removes a tag from the task or reports that the tag is absent. */
    public void removeTag(int taskNumber, String tag) throws NovaException {
        if (!getByNumber(taskNumber).removeTag(tag)) {
            throw new NovaException("Task does not have the tag " + tag + ".");
        }
    }

    /** Removes and returns the task at a one-based user-facing position. */
    public Task removeByNumber(int taskNumber) throws NovaException {
        getByNumber(taskNumber);
        int taskIndex = taskNumber - 1;
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "Validated task number should map to an in-bounds task index";
        return tasks.remove(taskIndex);
    }

    /** Returns deadlines and events that occur on the requested date. */
    public ArrayList<Task> getTasksOnDate(LocalDate date) {
        String dateText = date.toString();
        ArrayList<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task instanceof Deadline deadline && deadline.by.equals(date)) {
                matchingTasks.add(task);
            } else if (task instanceof Event event && isEventOnDate(event, dateText)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }

    /** Returns whether an event starts or ends on the requested date. */
    private static boolean isEventOnDate(Event event, String dateText) {
        return event.from.contains(dateText) || event.to.contains(dateText);
    }

    /** Returns tasks whose descriptions contain the keyword, ignoring letter case. */
    public ArrayList<Task> findByKeyword(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /** Allows callers to process every task without owning the collection. */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
