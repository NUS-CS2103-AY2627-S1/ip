package nabmak.task;

import java.util.ArrayList;

/**
 * Represents the list of tasks and handles requests related to tasks.
 */
public class TaskList {
    private ArrayList<Task> tasks;

    /**
     * Creates a tasklist with specified tasks.
     *
     * @param tasks tasks added to list
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Returns number of tasks in task list.
     *
     * @return number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task present at specified index.
     *
     * @param index index of task
     * @return task at index
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size();
        return tasks.get(index);
    }

    /**
     * Adds task into the task list.
     *
     * @param task task to be added
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Deletes specified task from task list and returns the task.
     *
     * @param index index of task
     * @return deleted task
     */
    public Task delete(int index) {
        assert index >= 0 && index < tasks.size();
        return tasks.remove(index);
    }

    /**
     * Returns the list of tasks.
     *
     * @return list of tasks
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Returns a list of all tasks containing specified keyword.
     *
     * @param keyword the keyword which the tasks contain
     * @return tasks matching specified keyword
     */
    public TaskList find(String keyword) {
        ArrayList<Task> matches = new ArrayList<>();

        for (Task task : tasks) {
            if (task.getDesc().contains(keyword)) {
                matches.add(task);
            }
        }

        return new TaskList(matches);
    }
}
