package bingbong.task;

import java.util.ArrayList;

/**
 * Keeps track of current list of tasks in memory. It handles adding, deleting, and counting the tasks in the list.
 */
public class TaskList {

    private final ArrayList<Task> tasks;

    /**
     * Constructs a completely blank TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a TaskList initialised with existing pre-loaded dataset rows.
     *
     * @param loadedTasks An array package listing of tasks parsed from persistent hard drive files.
     */
    public TaskList(ArrayList<Task> loadedTasks) {
        this.tasks = loadedTasks;
    }

    /**
     * Retrieves the quantitative volume size count of task elements active in memory.
     *
     * @return The numeric count integer value.
     */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Extracts a target object task reference mapping from a specific slot index row.
     *
     * @param index The index of item to select.
     * @return The Task object instance located at that target slot block.
     */
    public Task get(int index) {
        return this.tasks.get(index);
    }

    /**
     * Appends a new task entity onto the end of the collection data store array.
     *
     * @param task The concrete task class target instance to inject.
     */
    public void add(Task task) {
        this.tasks.add(task);
    }

    /**
     * Removes a target object task entity. Shifts any remaining array blocks forward automatically.
     *
     * @param index The index of item to remove.
     * @return The Task object reference that was deleted out of memory.
     */
    public Task remove(int index) {
        return this.tasks.remove(index);
    }
}
