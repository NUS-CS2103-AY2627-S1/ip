package crack;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

import crack.task.Task;

/**
 * The tasks Crack is keeping track of, in the order the user added them.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a list holding tasks that were already loaded from disk.
     *
     * @param tasks The tasks to start with.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds tasks to the end of the list, in the order they are given.
     *
     * @param tasksToAdd The tasks to add.
     */
    public void add(Task... tasksToAdd) {
        for (Task task : tasksToAdd) {
            tasks.add(task);
        }
    }

    /**
     * Removes the task at the given index and returns it.
     *
     * @param index Zero based position in the list.
     * @return The task that was removed.
     */
    public Task remove(int index) {
        assert index >= 0 && index < tasks.size() : "caller must check the index before removing";
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given index.
     *
     * @param index Zero based position in the list.
     * @return The task sitting there.
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "caller must check the index before reading";
        return tasks.get(index);
    }

    /**
     * Returns how many tasks are in the list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the list has nothing in it.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns every task, in order.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Returns only the tasks whose description contains the given keyword.
     *
     * @param keyword The text being searched for.
     * @return The matching tasks, in list order.
     */
    public ArrayList<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.matches(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns only the tasks landing on the given day.
     * Deadlines match the day they are due, and events match every day they span.
     *
     * @param date The day being asked about.
     * @return The matching tasks, in list order.
     */
    public ArrayList<Task> onDate(LocalDate date) {
        return tasks.stream()
                .filter(task -> task.isOn(date))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
