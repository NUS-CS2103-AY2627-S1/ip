package ev;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

import ev.task.Task;

/**
 * Represents the registry of tasks E.V. is currently tracking.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Constructs an empty task registry.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a registry holding the given tasks.
     *
     * @param tasks the tasks to start with.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the registry.
     *
     * @param task the task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes the task at the given index and returns it.
     *
     * @param index zero-based index of the task to remove.
     * @return the task that was removed.
     */
    public Task remove(int index) {
        assert index >= 0 && index < tasks.size() : "Task index must be within the registry.";

        return tasks.remove(index);
    }

    /**
     * Returns the task at the given index.
     *
     * @param index zero-based index of the task.
     * @return the task stored at that index.
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "Task index must be within the registry.";

        return tasks.get(index);
    }

    /**
     * Returns how many tasks the registry holds.
     *
     * @return the number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the position of every task in registry order, numbered from one.
     *
     * @return the one-based positions of every task.
     */
    public ArrayList<Integer> positions() {
        ArrayList<Integer> positions = new ArrayList<>();

        for (int i = 1; i <= tasks.size(); i++) {
            positions.add(i);
        }

        return positions;
    }

    /**
     * Returns the positions of tasks whose descriptions contain the given text, in
     * registry order and ignoring case. These are the same numbers mark, unmark and
     * delete accept, so a result can be acted on directly.
     *
     * @param keyword the text to search for.
     * @return the one-based positions of the matching tasks.
     */
    public ArrayList<Integer> findPositions(String keyword) {
        String target = keyword.toLowerCase(Locale.ENGLISH);
        ArrayList<Integer> positions = new ArrayList<>();

        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getDescription().toLowerCase(Locale.ENGLISH).contains(target)) {
                positions.add(i + 1);
            }
        }

        return positions;
    }

    /**
     * Returns every task's position ordered by description, ignoring case. The numbers
     * are registry positions rather than a fresh count, so they will not run in order.
     * That is deliberate: the number shown is the one mark, unmark and delete accept.
     *
     * @return the one-based positions of every task, in description order.
     */
    public ArrayList<Integer> sortedPositions() {
        ArrayList<Integer> positions = positions();

        positions.sort(Comparator.comparing(position -> tasks.get(position - 1).getDescription(),
                String.CASE_INSENSITIVE_ORDER));

        return positions;
    }


    /**
     * Returns a registry holding the given tasks.
     *
     * @param tasks the tasks to start with.
     * @return a registry containing exactly those tasks.
     */
    public static TaskList of(Task... tasks) {
        TaskList registry = new TaskList();

        for (Task task : tasks) {
            registry.add(task);
        }

        return registry;
    }
}
