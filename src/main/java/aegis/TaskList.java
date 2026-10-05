package aegis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import aegis.task.Task;

/**
 * Represents a list of tasks.
 * Wraps an internal ArrayList and provides operations for adding,
 * retrieving, deleting, marking and unmarking tasks.
 */
public class TaskList {

    private final ArrayList<Task> tasks;

    /**
     * Creates an empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a TaskList using the given list of tasks.
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "TaskList cannot wrap a null backing list";
        this.tasks = tasks;
    }

    /**
     * Adds a task to the list.
     */
    public void add(Task task) {
        assert task != null : "TaskList should not contain null tasks";
        tasks.add(task);
    }

    /**
     * Checks whether a task with the same type, description and date details already exists.
     */
    public boolean containsSameDetails(Task task) {
        assert task != null : "Task to compare should not be null";
        return tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task));
    }

    /**
     * Gets a task from the list given an index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the current number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Deletes the task at the particular index.
     */
    public void delete(int index) {
        tasks.remove(index);
    }

    /**
     * Marks the task at the particular index as complete.
     */
    public void mark(int index) {
        tasks.get(index).mark();
    }

    /**
     * Marks the task at the particular index as not complete.
     */
    public void unmark(int index) {
        tasks.get(index).unmark();
    }

    /**
     * Sorts the taskList by date.
     */
    public void sortByDate() {
        tasks.sort(Comparator.comparing(Task::getDate));
    }

    /**
     * Returns all tasks whose description contains the given keyword.
     *
     * @param tag The search word (or phrase) to match against task descriptions.
     * @return An ArrayList of matching tasks.
     */
    public ArrayList<Task> getMatchingTasks(String tag) {
        return tasks.stream()
                .filter(task -> task.toString().contains(tag))
                .collect(Collectors.toCollection(ArrayList::new));
    }

}
