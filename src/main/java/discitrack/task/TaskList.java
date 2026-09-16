package discitrack.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import discitrack.exception.DisciTrackException;

/**
 * Stores the user's tasks and provides operations on the task list.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list using the given tasks.
     *
     * @param tasks the initial tasks in the list.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add.
     */
    public void add(Task task) {
        assert task != null
                : "A task list must not contain a null task.";

        tasks.add(task);
    }

    /**
     * Gets the task at the given one-based task number.
     *
     * @param taskNumber the one-based number of the task to get.
     * @return the task at the given task number.
     * @throws DisciTrackException if the task number is outside the list.
     */
    public Task get(int taskNumber) throws DisciTrackException {
        validateTaskNumber(taskNumber);
        return tasks.get(taskNumber - 1);
    }

    /**
     * Deletes the task at the given one-based task number.
     *
     * @param taskNumber the one-based number of the task to delete.
     * @return the removed task.
     * @throws DisciTrackException if the task number is outside the list.
     */
    public Task delete(int taskNumber) throws DisciTrackException {
        validateTaskNumber(taskNumber);
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the tasks as a list.
     *
     * @return the underlying list of tasks.
     */
    public List<Task> asList() {
        return tasks;
    }

    /**
     * Finds tasks that are relevant to the given date.
     *
     * @param date the date to search for.
     * @return tasks with a deadline on the date or an event occurring on the date.
     */
    public List<Task> findTasksByDate(LocalDate date) {
        return tasks.stream()
                .filter(task -> {
                    if (task instanceof Deadline) {
                        Deadline deadline = (Deadline) task;
                        return deadline.getTime().equals(date);
                    }

                    if (task instanceof Event) {
                        Event event = (Event) task;
                        return !date.isBefore(event.getFrom()) && !date.isAfter(event.getTo());
                    }
                    return false;
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Finds tasks whose activity descriptions contain the given keyword.
     *
     * @param keyword the keyword to search for.
     * @return tasks whose activity descriptions contain the keyword.
     */
    public List<Task> findTasksByKeyword(String keyword) {
        String keywordToFind = keyword.toLowerCase();

        return tasks.stream()
                .filter(task -> task.getActivity().toLowerCase().contains(keywordToFind))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Checks that a one-based task number refers to an existing task.
     *
     * @param taskNumber the task number to validate.
     * @throws DisciTrackException if the task number is outside the list.
     */
    private void validateTaskNumber(int taskNumber) throws DisciTrackException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new DisciTrackException("UHOH! No task has that number. Type list to check your task numbers.");
        }
    }
}
