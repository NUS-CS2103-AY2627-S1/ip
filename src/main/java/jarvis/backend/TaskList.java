package jarvis.backend;

import java.util.ArrayList;
import java.util.List;

import jarvis.classes.Event;
import jarvis.classes.Task;
import jarvis.exceptions.InvalidTaskNumberException;
import jarvis.exceptions.ScheduleConflictException;
import jarvis.storage.Storage;

/** Stores, updates, and persists the user's tasks. */
public class TaskList {
    /** A list to store tasks added by the user. */
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this(new ArrayList<>());
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks Existing tasks.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "task list should not be null";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task and saves the updated list.
     *
     * @param task Task to add.
     * @throws ScheduleConflictException If there is a schedule conflict with another event task
     */
    public void addTask(Task task) throws ScheduleConflictException {
        assert task != null : "Task being added must not be null";

        if (task instanceof Event event) {
            Event conflictingEvent = findScheduleConflict(event);

            if (conflictingEvent != null) {
                throw new ScheduleConflictException(
                        "Error : This event clashes with an existing event : \n"
                        + "  " + conflictingEvent.toString()
                );
            }
        }

        tasks.add(task);
        saveTasks();
    }

    /**
     * Finds an existing event that clashes with the new event.
     *
     * @param newEvent Event being added.
     * @return The conflicting event, or null if there is no clash.
     */
    private Event findScheduleConflict(Event newEvent) {
        assert newEvent != null : "event should not be null";
        return tasks.stream()
                .filter(task -> task instanceof Event)
                .map(task -> (Event) task)
                .filter(newEvent::clashesWith)
                .findFirst()
                .orElse(null);
    }

    /**
     * Removes a task by zero-based index and saves the updated list.
     *
     * @param index Zero-based index of the task to remove.
     * @return Removed task.
     * @throws InvalidTaskNumberException If the number is invalid.
     */
    public Task deleteTask(int index) throws InvalidTaskNumberException {
        validateTaskIndex(index);

        Task removedTask = tasks.remove(index);
        saveTasks();
        return removedTask;
    }

    /**
     * Returns all tasks stored in this list.
     *
     * @return Read-only view of the tasks.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index Zero-based index of the task.
     * @return Task at the requested index.
     * @throws InvalidTaskNumberException If the number is invalid.
     */
    public Task getTask(int index) throws InvalidTaskNumberException {
        validateTaskIndex(index);
        return tasks.get(index);
    }

    /**
     * Updates a task's completion status and saves the updated list.
     *
     * @param index Zero-based index of the task to update.
     * @param status New completion status.
     */
    public void setCompletionStatus(int index, Task.CompletionStatus status) throws InvalidTaskNumberException {
        validateTaskIndex(index);

        tasks.get(index).setCompletionStatus(status);
        saveTasks();
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /** Saves the current tasks to persistent storage. */
    public void saveTasks() {
        Storage.saveTasks(tasks);
    }

    /**
     * Returns tasks whose description contains the given key word.
     *
     * @param taskKeyWord The key word to filter tasks by.
     * @return List of tasks matching the keyword.
     */
    public List<Task> filterTasks(String taskKeyWord) {
        String filterKeyWord = taskKeyWord.toLowerCase();

        return tasks.stream()
                .filter(task -> task.getTaskName()
                        .toLowerCase()
                        .contains(filterKeyWord))
                .toList();
    }

    /**
     * Validates a zero-based task number.
     *
     * @param index Zero-based task number.
     * @throws InvalidTaskNumberException If the number is invalid.
     */
    private void validateTaskIndex(int index)
            throws InvalidTaskNumberException {

        if (index < 0 || index >= tasks.size()) {
            throw new InvalidTaskNumberException(
                    "Error: The task number you inputted is invalid.\n"
                            + String.format(
                            "Please re-enter with a valid number ranging from "
                                    + "1 to %d",
                            tasks.size())
            );
        }
    }
}
