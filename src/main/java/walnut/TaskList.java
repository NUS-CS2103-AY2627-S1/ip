package walnut;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Maintains the ordered collection of tasks shown by Walnut.
 */
public class TaskList implements Iterable<Task> {
    private ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        tasks.add(task);
    }

    /**
     * Returns the task at the specified zero-based index.
     *
     * @param index zero-based index
     * @return task at the specified index
     */
    public Task get(int index) {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException("Invalid task index");
        }

        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Removes the task at the specified zero-based index.
     *
     * @param index zero-based index
     */
    public void remove(int index) {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException("Invalid task index");
        }

        tasks.remove(index);
    }

    /**
     * Returns whether the task list contains no tasks.
     *
     * @return {@code true} if the list is empty.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns all tasks whose descriptions contain the specified keyword.
     *
     * @param keyword Keyword to search for in task descriptions.
     * @return Tasks whose descriptions contain the keyword.
     */
    public ArrayList<Task> find(String keyword) {
        assert keyword != null && !keyword.isBlank()
                : "Search keyword cannot be null or blank";
        ArrayList<Task> foundTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDescription().contains(keyword)) {
                foundTasks.add(task);
            }
        }
        return foundTasks;
    }

    /**
     * Checks whether an equivalent task already exists.
     *
     * @param candidate task to check
     * @return true if an equivalent task is already present
     */
    public boolean containsEquivalent(Task candidate) {
        if (candidate == null) {
            return false;
        }

        for (Task existing : tasks) {
            if (existing.getClass() != candidate.getClass()) {
                continue;
            }

            if (!existing.getDescription().equals(candidate.getDescription())) {
                continue;
            }

            if (candidate instanceof Deadline candidateDeadline
                    && existing instanceof Deadline existingDeadline) {
                if (!existingDeadline.getDeadline()
                        .equals(candidateDeadline.getDeadline())) {
                    continue;
                }
            }

            if (candidate instanceof Event candidateEvent
                    && existing instanceof Event existingEvent) {
                if (!existingEvent.getEventStartTime()
                        .equals(candidateEvent.getEventStartTime())
                        || !existingEvent.getEventEndTime()
                        .equals(candidateEvent.getEventEndTime())) {
                    continue;
                }
            }

            return true;
        }

        return false;
    }

    /**
     * Returns a formatted string containing the specified tasks.
     *
     * @param tasks Tasks to format.
     * @return Formatted representation of the tasks.
     */
    public static String toString(ArrayList<Task> tasks) {
        assert tasks != null : "Task collection cannot be null";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            sb.append((i + 1) + ". " + tasks.get(i).toString() + "\n");
        }
        sb.append("You have " + tasks.size() + " tasks in your list.");
        return sb.toString();
    }

    @Override
    public String toString() {
        return toString(this.tasks);
    }

    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }


}
