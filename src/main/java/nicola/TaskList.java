package nicola;

import java.util.ArrayList;
import java.util.List;

/**
 * stores and manages the tasks used by the chatbot.
 */
public class TaskList {
    private static final int MAX_TASKS = 100;
    private final List<Task> tasks;

    /**
     * create an empty task list
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * create a task list containing the given tasks
     *
     * @param loadedTasks the existing tasks to place in the list
     */
    public TaskList(List<Task> loadedTasks) {
        assert loadedTasks != null : "Loaded task list should not be null";

        tasks = new ArrayList<>(loadedTasks);
    }

    /**
     * return the number of the tasks in a task list
     *
     * @return the number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * get a specific task
     *
     * @param index index of the task wanted
     * @return the task at the index
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * add the task to the list
     *
     * @param task task to add
     */
    public void add(Task task) {
        assert task != null : "Task to add should not be null";

        if (tasks.size() < MAX_TASKS) {
            tasks.add(task);
        }
    }

    /**
     * remove the specific task
     *
     * @param index index of the task
     * @return the removed task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * return all tasks in the task list
     *
     * @return the list of tasks
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * check if the task list is full
     *
     * @return true if the list is full, false otherwise
     */
    public boolean isFull() {
        return tasks.size() >= MAX_TASKS;
    }
}
