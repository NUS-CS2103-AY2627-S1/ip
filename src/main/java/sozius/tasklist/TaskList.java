package sozius.tasklist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sozius.task.Task;

/**
 * TaskList class is the collection for storing tasks in memory
 */
public class TaskList implements Iterable<Task> {
    private final List<Task> tasks;
    /**
     * Creates an empty tasklist
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }
    /**
     * Creates a tasklist using a List of tasks
     * @param tasks the ArrayList
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }
    /**
     * Adds a task to the tasklist
     * @param task the task
     */
    public void add(Task task) {
        tasks.add(task);
    }
    /**
     * removes the task at the index and returns it
     * @param index the index
     * @return the task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }
    /**
     * returns the task at the index
     * @param index the index
     * @return the task
     */
    public Task get(int index) {
        return tasks.get(index);
    }
    /**
     * Returns the number of tasks in the tasklist
     * @return the number of tasks
     */
    public int size() {
        return tasks.size();
    }
    /**
     * Marks the task at the index
     * @param index the index
     */
    public void markTask(int index) {
        tasks.get(index).setDone(true);
    }
    /**
     * Unmarks the task at the index
     * @param index the index
     */
    public void unmarkTask(int index) {
        tasks.get(index).setDone(false);
    }
    /**
     * Returns the last task added to the tasklist
     * @return the last task
     */
    public Task getLast() {
        return tasks.getLast();
    }
    /**
     * Returns an iterator over tasks in the tasklist
     * @return the iterator
     */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
