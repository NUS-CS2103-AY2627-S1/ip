package chimpanzinibananini.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import chimpanzinibananini.exception.ChimpanziniBananiniException;

/**
 * Owns the collection of tasks and provides task-list operations.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this(new ArrayList<>());
    }

    /**
     * Creates a task list containing the loaded tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Marks the given one-based task number and returns that task.
     */
    public Task mark(int taskNumber) throws ChimpanziniBananiniException {
        Task task = getByTaskNumber(taskNumber);
        task.markAsDone();
        return task;
    }

    /**
     * Deletes and returns the task at the given one-based task number.
     */
    public Task delete(int taskNumber) throws ChimpanziniBananiniException {
        validateTaskNumber(taskNumber);
        return tasks.remove(taskNumber - 1);
    }
    /** Returns tasks whose descriptions contain the given keyword. */
    public List<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .toList();
    }

    /**
     * Returns incomplete deadlines from now through seven local days later, inclusive.
     * Orders matches by due time, preserving task-list order for ties without changing this list.
     */
    public List<Task> getUpcomingDeadlines(LocalDateTime now) {
        LocalDateTime windowEnd = now.plusDays(7);
        return tasks.stream()
                .filter(task -> task instanceof Deadline && !task.isDone())
                .map(task -> (Deadline) task)
                .filter(deadline -> !deadline.getBy().isBefore(now)
                        && !deadline.getBy().isAfter(windowEnd))
                .sorted(Comparator.comparing(Deadline::getBy))
                .<Task>map(deadline -> deadline)
                .toList();
    }

    /** Returns the task at a zero-based index for displaying the list. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a read-only view of the tasks for persistence.
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /** Returns a task after checking that its one-based number exists. */
    private Task getByTaskNumber(int taskNumber) throws ChimpanziniBananiniException {
        validateTaskNumber(taskNumber);
        return tasks.get(taskNumber - 1);
    }

    /** Checks that a one-based task number refers to an existing task. */
    private void validateTaskNumber(int taskNumber) throws ChimpanziniBananiniException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new ChimpanziniBananiniException("That task number does not exist 💀");
        }
    }
}
