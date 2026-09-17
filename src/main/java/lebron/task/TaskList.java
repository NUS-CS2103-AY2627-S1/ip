package lebron.task;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Represents a list of tasks.
 */
public class TaskList {
    private ArrayList<Task> tasks;

    /**
     * Constructs a TaskList with the given list of tasks.
     * @param tasks The list of tasks.
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "The list passed to TaskList should not be null";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the TaskList.
     *
     * @param task The task to be added.
     * @throws TaskListException if an equal task (same type, description, and dates) is
     *         already in the list.
     */
    public void addTask(Task task) throws TaskListException {
        if (tasks.contains(task)) {
            throw new TaskListException("You already got that one on your list!");
        }
        this.tasks.add(task);
    }

    /**
     * Removes a task from the TaskList.
     *
     * @param taskNumber The 1 based index of the task removed from TaskList.
     * @throws TaskListException if taskNumber is not in range.
     */
    public void deleteTask(int taskNumber) throws TaskListException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new TaskListException("This task does not exist kid.");
        }
        this.tasks.remove(taskNumber - 1);
    }

    /**
     * Retrieves a task from the TaskList.
     *
     * @param taskNumber The 1 based index of the task retrieved from TaskList.
     * @return The task at the specified index.
     * @throws TaskListException if taskNumber is not in range.
     */
    public Task getTask(int taskNumber) throws TaskListException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new TaskListException("This task does not exist kid.");
        }
        return this.tasks.get(taskNumber - 1);
    }

    /**
     * Finds all tasks whose description contains the given keyword.
     *
     * @param keyword The keyword to search for (case-insensitive).
     * @return The matching tasks, in their original order.
     */
    public ArrayList<Task> findTasks(String keyword) {
        assert keyword != null : "Search keyword should not be null; Parser should have rejected it already";
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns the size of the TaskList.
     *
     * @return The size of the list
     */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Reformats the TaskList to be saved using Storage
     *
     * @return Reformatted String.
     */
    public String reformat() {
        return tasks.stream()
                .map(task -> task.reformat() + "\n")
                .collect(Collectors.joining());
    }

    @Override
    public String toString() {
        return IntStream.range(0, tasks.size())
                .mapToObj(i -> (i + 1) + ". " + tasks.get(i) + "\n")
                .collect(Collectors.joining());
    }
}
