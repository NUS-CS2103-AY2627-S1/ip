package verity.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import verity.client.ClientList;
import verity.exception.VerityException;

/**
 * Stores and manages the user's tasks.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param initialTasks Tasks to place in the task list.
     */
    public TaskList(Task... initialTasks) {
        assert initialTasks != null : "Initial task array must not be null.";
        assert Arrays.stream(initialTasks)
                .allMatch(task -> task != null)
                : "Task list must not contain null tasks.";

        this.tasks = new ArrayList<>(Arrays.asList(initialTasks));
    }

    /**
     * Adds a task.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        assert task != null : "Added task must not be null.";

        tasks.add(task);
    }

    /**
     * Deletes and returns a task.
     *
     * @param taskIndex Zero-based index of the task.
     * @return Deleted task.
     */
    public Task delete(int taskIndex) {
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "Task index must refer to an existing task.";

        return tasks.remove(taskIndex);
    }

    /**
     * Returns a task.
     *
     * @param taskIndex Zero-based index of the task.
     * @return Task at the specified index.
     */
    public Task get(int taskIndex) {
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "Task index must refer to an existing task.";

        return tasks.get(taskIndex);
    }

    /**
     * Marks a task as completed.
     *
     * @param taskIndex Zero-based index of the task.
     * @return Updated task.
     */
    public Task mark(int taskIndex) {
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "Task index must refer to an existing task.";

        Task task = tasks.get(taskIndex);
        task.markAsDone();
        return task;
    }

    /**
     * Marks a task as incomplete.
     *
     * @param taskIndex Zero-based index of the task.
     * @return Updated task.
     */
    public Task unmark(int taskIndex) {
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "Task index must refer to an existing task.";

        Task task = tasks.get(taskIndex);
        task.markAsUndone();
        return task;
    }

    /**
     * Returns tasks occurring on a date.
     *
     * @param date Date to search for.
     * @return Tasks occurring on the date.
     */
    public List<Task> findOn(LocalDate date) {
        return tasks.stream()
                .filter(task -> task.occursOn(date))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns tasks whose descriptions contain the keyword.
     *
     * @param keyword Keyword to search for.
     * @return Matching tasks in their original order.
     */
    public List<Task> findByKeyword(String keyword) {
        return tasks.stream()
                .filter(task -> task.matchesKeyword(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns tasks associated with a client.
     *
     * @param clientId Client ID to find.
     * @return Associated tasks in task-list order.
     */
    public List<Task> findByClientId(String clientId) {
        return tasks.stream()
                .filter(task -> task.hasClientId(clientId))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Ensures every stored client reference points to an existing client.
     *
     * @param clientList Loaded clients.
     * @throws VerityException If a task references a missing client.
     */
    public void validateClientReferences(ClientList clientList)
            throws VerityException {
        for (int i = 0; i < tasks.size(); i++) {
            for (String clientId : tasks.get(i).getClientIds()) {
                if (!clientList.containsId(clientId)) {
                    throw new VerityException("Line " + (i + 1)
                            + ": unknown client ID '" + clientId + "'.");
                }
            }
        }
    }

    /**
     * Returns the number of tasks.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a read-only snapshot of the tasks.
     *
     * @return Snapshot of the tasks.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }
}
