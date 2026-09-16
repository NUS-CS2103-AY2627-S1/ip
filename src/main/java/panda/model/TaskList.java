package panda.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Encapsulates Panda's task collection and the basic operations performed on it.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list with the provided initial tasks.
     *
     * @param initialTasks the tasks to start with
     */
    public TaskList(List<Task> initialTasks) {
        this.tasks = new ArrayList<>(initialTasks);
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return the task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at a given position.
     *
     * @param index the zero-based task index
     * @return the task at that index
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns whether a task with the same details is already stored.
     *
     * @param task task to check
     * @return whether an equivalent task exists
     */
    public boolean containsEquivalent(Task task) {
        return tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task));
    }

    /**
     * Removes and returns the task at a given position.
     *
     * @param index the zero-based task index
     * @return the removed task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns all tasks whose descriptions contain the given keyword, case-insensitive.
     *
     * @param keyword the keyword to search for
     * @return the matching tasks in their current order
     */
    public ArrayList<Task> find(String keyword) {
        String searchTerm = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(searchTerm))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns a copy of the given tasks with deadlines first, ordered by their due date/time.
     * Non-deadline tasks retain their original relative order after the deadlines.
     *
     * @param taskList tasks to order
     * @param direction date ordering direction
     * @return a sorted copy of the tasks
     */
    public ArrayList<Task> sortDeadlinesByDate(List<Task> taskList, SortDirection direction) {
        Comparator<Task> deadlineComparator = Comparator.comparing(task -> ((Deadline) task).getBy());
        if (direction == SortDirection.DESCENDING) {
            deadlineComparator = deadlineComparator.reversed();
        }
        ArrayList<Task> sortedTasks = taskList.stream()
                .filter(Deadline.class::isInstance)
                .sorted(deadlineComparator)
                .collect(Collectors.toCollection(ArrayList::new));
        taskList.stream()
                .filter(task -> !(task instanceof Deadline))
                .forEach(sortedTasks::add);
        return sortedTasks;
    }

    /**
     * Marks the task at the given index as done.
     *
     * @param index the zero-based task index
     * @return the updated task
     */
    public Task markTask(int index) {
        Task task = tasks.get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task at the given index as not done.
     *
     * @param index the zero-based task index
     * @return the updated task
     */
    public Task unmarkTask(int index) {
        Task task = tasks.get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns the tasks as a mutable list for persistence operations.
     *
     * @return the underlying task list
     */
    public ArrayList<Task> asList() {
        return tasks;
    }
}
