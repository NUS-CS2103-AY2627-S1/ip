package labubu;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Stores and manages the tasks tracked by Labubu.
 */
public class TaskList {
    private final List<Task> taskList = new ArrayList<>();
    /**
     * Creates an empty task list.
     */
    public TaskList() {}

    /**
     * Creates a task list containing the given tasks.
     *
     * @param tasks Tasks to add to the list.
     */
    public TaskList(List<Task> tasks) {
        taskList.addAll(tasks);
    }

    /**
     * Converts all tasks to their save-file representations.
     *
     * @return Lines suitable for writing to the save file.
     */
    public List<String> toSaveFormat() {
        return taskList.stream()
                .map(Task::toSaveFormat)
                .collect(Collectors.toList());
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Number of tasks.
     */
    public int getTaskListSize() {
        return taskList.size();
    }

    /**
     * Returns the task at the specified zero-based index.
     *
     * @param index Index of the task.
     * @return Task at the index.
     */
    public Task getTask(int index) {
        return taskList.get(index);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void addTask(Task task) {
        assert task != null : "Task list must not contain null tasks";
        taskList.add(task);
    }

    /**
     * Removes and returns the task at the specified zero-based index.
     *
     * @param index Index of the task to remove.
     * @return Removed task.
     */
    public Task removeTask(int index) {
        Task task = taskList.get(index);
        taskList.remove(index);
        return task;
    }

    /**
     * Returns a list of tasks with keyword in their task titles.
     *
     * @param keyword Word to match against.
     * @return List of tasks that contain keyword.
     */
    public List<Task> findTaskByKeyword(String keyword) {
        return taskList.stream()
                .filter(task -> task.getTaskTitle().contains(keyword))
                .collect(Collectors.toList());
    }
}
